/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 19, 2005
 */

package org.emonic.base.wizards.newconsoleprojectwizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.filemanipulators.CSharpFileManipulator;
import org.emonic.base.filemanipulators.NetProjectManipulator;
import org.emonic.base.wizards.Messages;


/**
 * @author bb
 *
 */
public class NewCSharpConsoleResourceWizard extends BasicNewResourceWizard {
	
	private NewCSharpConsoleProjectCreationPage newDotNetCreationPage;
	private WizardNewProjectReferencePage wizardNewProjectReferencePage;
    private NewCSharpConsoleSourceLayoutPage newDotNetProjectSourceLayoutPage;
	// cache of newly-created project
	private IProject newProject;
	// ID according to plugin.xml to be referenced out of the code
    public final static String ID ="org.emonic.base.wizards.EmonoNewProjectResourceWizard"; //$NON-NLS-1$
    
    public NewCSharpConsoleResourceWizard() {
    	setWindowTitle(Messages.getString("NewDotNetProjectResourceWizard_WindowTitle")); //$NON-NLS-1$
    }
	
	public void addPages() {
		newDotNetCreationPage = new NewCSharpConsoleProjectCreationPage();
		addPage(newDotNetCreationPage);
		newDotNetProjectSourceLayoutPage = new NewCSharpConsoleSourceLayoutPage();
		newDotNetProjectSourceLayoutPage.setTitle(Messages.getString("NewDotNetProjectResourceWizard_NamespaceCopyrightPageTitle")); //$NON-NLS-1$
		newDotNetProjectSourceLayoutPage.setDescription(Messages.getString("NewDotNetProjectResourceWizard_NamespaceCopyrightPageDescription")); //$NON-NLS-1$
		this.addPage(newDotNetProjectSourceLayoutPage);
		
		// only add page if there are already projects in the workspace
		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length > 0) {
			wizardNewProjectReferencePage = new WizardNewProjectReferencePage("ReferenceProject"); //$NON-NLS-1$
			wizardNewProjectReferencePage.setTitle(Messages.getString("NewDotNetProjectResourceWizard_ReferencePageTitle")); //$NON-NLS-1$
			// TODO: change the text if referencing projects is possible
			wizardNewProjectReferencePage.setDescription("Set references to other projects. (Has no effects currently!)"); //$NON-NLS-1$
			this.addPage(wizardNewProjectReferencePage);
		}
	}
	
	public boolean performFinish() {
		prepareAndCreateProject();
		return newProject != null;
	}

	/**
	 * 
	 */
	private void prepareAndCreateProject() {
		newDotNetCreationPage.commitData();
		// This method prepares the creation of the project and 
		// calls createRealProject via a WorkspaceModificator
//		 get a project handle
		final IProject newProjectHandle = newDotNetCreationPage.getProjectHandle();

		// get a project descriptor
		IPath newPath = null;
		if (!newDotNetCreationPage.useDefaults())
			newPath = newDotNetCreationPage.getLocationPath();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description =
			workspace.newProjectDescription(newProjectHandle.getName());
		description.setLocation(newPath);

		// update the referenced project if provided
		if (wizardNewProjectReferencePage != null) {
			IProject[] refProjects = wizardNewProjectReferencePage.getReferencedProjects();
			if (refProjects.length > 0)
				description.setReferencedProjects(refProjects);
		}

		// create the new project operation
        //		 create the new project operation
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
				throws CoreException {
				// Here it hapens!
				createRealProject(description, newProjectHandle, monitor);
			}

			
		};

		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
			
		} catch (InterruptedException e) {
			ErrorDialog.openError(getShell(),"Error:",e.toString(),null);
		} catch (InvocationTargetException e) {
			// ie.- one of the steps resulted in a core exception	
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				IStatus status = ((CoreException) t).getStatus();
				if (status.getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					MessageDialog.openError(getShell(),"Error:",Messages.getString("NewDotNetProjectResourceWizard_MessageVariantExists")) ; //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					ErrorDialog.openError(getShell(), "Error:", Messages.getString("NewDotNetProjectResourceWizard_ErrorCannotCreateProject"), status); // no special message //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else if (t instanceof Exception) {
				ErrorDialog.openError(getShell(), "Error:", Messages.getString("NewDotNetProjectResourceWizard_ErrorCannotCreateProject"), new Status(IStatus.ERROR, EMonoPlugin.PLUGIN_ID, 0, Messages.getString("NewDotNetProjectResourceWizard_ErrorFailedCreateProject"), t)); // no special message //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else if (t instanceof Error) {
				throw (Error) t;
			}
		}
		
		newProject = newProjectHandle;
	}
	
	private void createRealProject(IProjectDescription description, IProject projectHandle, IProgressMonitor monitor) {
		org.emonic.base.filemanipulators.NetProjectManipulator creator = new NetProjectManipulator(description,projectHandle,monitor);
		creator.createProject();
		// The order matters here:
		// We first have to set the mechanism, then the file, then the rest! 
		creator.setBuildMechanism(newDotNetCreationPage.getBuildMechanism());
		creator.setBuildFile(newDotNetCreationPage.getBuildFile());
		
		creator.setSrcDir(newDotNetCreationPage.getSourceFolder().trim());
		creator.setBinDir(newDotNetCreationPage.getOutputFolder().trim());
		creator.setCopyright(newDotNetProjectSourceLayoutPage.getCopyright());
		creator.setNamespace(newDotNetProjectSourceLayoutPage.getNamespace());
		
		String tgf = newDotNetCreationPage.getBuildTargetFramework();
		String[] parts= tgf.split("-",2);
		if (parts.length==2){
			creator.setTargetFramework(parts[0],parts[1]);
		}
		createClassesAndTarget(projectHandle,monitor);
		
		//creator.createPreferencesFile();
		monitor.done();
		
	}
	
	private InputStream openContentStream() {
		String contents = "";
		return new ByteArrayInputStream(contents.getBytes());
	}
	

	
	
	private void createClassesAndTarget(final IProject projectHandle,IProgressMonitor monitor){
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

			protected void execute(IProgressMonitor monitor)
					throws CoreException {
                // Calculate Filename
				String filename = newDotNetCreationPage.getSourceFolder().trim() + File.separator + "program.cs";
				try {
					final IFile file = projectHandle.getFile(new Path(filename));
					InputStream stream = openContentStream();
					if (file.exists()) {
						file.setContents(stream, true, true, monitor);
					} else {
						file.create(stream, true, monitor);
					}

					stream.close();
					CSharpFileManipulator mani = new CSharpFileManipulator(file);
					
					
					String copyright = newDotNetProjectSourceLayoutPage.getCopyright();
					String namespace = newDotNetProjectSourceLayoutPage.getNamespace();
					int pos = 0;
					if (!"".equals(copyright)) {
						pos = mani.addCopyright(copyright, true);
					}
					CodeElement ns = null;
					if (!"".equals(namespace)) {
						ns = new CodeElement(null, IDotNetElement.NAMESPACE);
						ns.setElementName(namespace);
					}

					CodeElement cls = new CodeElement(ns, IDotNetElement.CLASS);
					cls.setElementName("program");


					CodeElement constructor = new CodeElement(cls,
							IDotNetElement.CONSTRUCTOR);
					constructor.setElementName("program");
					constructor.setAccessType("public");
					constructor.setSignature("()");
					cls.addChild(constructor);
					

					CodeElement main = new CodeElement(cls,
								IDotNetElement.METHOD);
					main.setElementName("Main");
					main.setAccessType("public");
					main.setTypeSignature("void");
					main.staticMod = true;
					main.setSignature("(string[] args)");
				    main.setBody("System.Console.WriteLine(\"Hello World!\");");
					cls.addChild(main);
		
					// Add it to the namespace and build it if the namespace
					// exists
					if (ns != null) {
						ns.addChild(cls);
						mani.AddCodeElement(ns, pos, true);
					} else {
						// Generate only the class
						mani.AddCodeElement(cls, pos, true);
					}

					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}

					projectHandle.refreshLocal(IResource.DEPTH_ONE,
							new NullProgressMonitor());

					BuildMechanismManipulator manipulator = BuildDescriptionFactory
							.getBuildMechanismManipulator(file.getProject());
					// use the build file manipulator to append the new
					// interface to an existing target or to create a new target
					// containing the created interface
					if (manipulator.isSourceTargetManipulator()) {
						
							String targetName = projectHandle.getName();
							//SourceTarget target = manipulator
							//.getSourceTarget(targetName);
							SourceTarget sourceTarget= new SourceTarget();
							sourceTarget.setName(targetName);
							sourceTarget
							.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
							sourceTarget.setType("exe");
							sourceTarget.addSource(file);
							sourceTarget.setOptimization(true);
							sourceTarget.setDebuggingOutput(true);
							sourceTarget.setWarningLevel(3);

							sourceTarget.setDefinitions(new String[0]);
							manipulator.writeNewTargetInTree(sourceTarget,true);
						}

					manipulator.save();
				} catch (Exception e){
					
				    System.out.println(e.getMessage());
					e.printStackTrace();
				}
				
		
			} // End execute
		}; // End Workspace-op
		try{
		op.run(monitor);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}	
	

	


