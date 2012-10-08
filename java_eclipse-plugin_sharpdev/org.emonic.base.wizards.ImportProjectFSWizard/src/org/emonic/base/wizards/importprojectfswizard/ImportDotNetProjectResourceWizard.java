/*******************************************************************************
 * Copyright (c) 2010 B. Brem
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    B. Brem
 *******************************************************************************/
/*
 * Created on Mar 19, 2005
 */

package org.emonic.base.wizards.importprojectfswizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.filemanipulators.NetProjectManipulator;
import org.emonic.base.wizards.Messages;


/**
 * @author bb
 *
 */
public class ImportDotNetProjectResourceWizard extends BasicNewResourceWizard {
	
	private NewDotNetProjectCreationPage newDotNetCreationPage;
	private WizardNewProjectReferencePage wizardNewProjectReferencePage;
	// cache of newly-created project
	private IProject newProject;
	// ID according to plugin.xml to be referenced out of the code
    public final static String ID ="org.emonic.base.wizards.EmonoNewProjectResourceWizard"; //$NON-NLS-1$
    
    public ImportDotNetProjectResourceWizard() {
    	setWindowTitle("Import from FileSystem"); //$NON-NLS-1$
    }
	
	public void addPages() {
		newDotNetCreationPage = new NewDotNetProjectCreationPage();
		addPage(newDotNetCreationPage);
		
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
		// Copy recursive the content of the project to import
		copyRecurseve(projectHandle);
		// The order matters here:
		// We first have to set the mechanism, then the file, then the rest! 
		creator.setBuildMechanism(newDotNetCreationPage.getBuildMechanism());
		creator.setBuildFile(newDotNetCreationPage.getBuildFile());
		String tgf = newDotNetCreationPage.getBuildTargetFramework();
		String[] parts= tgf.split("-",2);
		if (parts.length==2){
			creator.setTargetFramework(parts[0],parts[1]);
		}
		try{
			projectHandle.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (Exception e){
			// Ignore silently
		}
		//creator.createPreferencesFile();
		monitor.done();
		
	}

	private void copyRecurseve(IProject projectHandle) {
		
		String src = newDotNetCreationPage.getSourceFolder().trim();
		String dest = projectHandle.getLocation().toOSString();
		try {
			copyFiles(new File(src), new File(dest));
		} catch(IOException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getStackTrace().toString());
		}
		
	}
	
	
	/**
	 * This function will copy files or directories from one location to another.
	 * note that the source and the destination must be mutually exclusive. This 
	 * function can not be used to copy a directory to a sub directory of itself.
	 * The function will also have problems if the destination files already exist.
	 * @param src -- A File object that represents the source for the copy
	 * @param dest -- A File object that represnts the destination for the copy.
	 * @throws IOException if unable to copy.
	 */
	public static void copyFiles(File src, File dest) throws IOException {
		//Check to ensure that the source is valid...
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath()+".");
		} else if (!src.canRead()) { //check to ensure we have rights to the source...
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath()+".");
		}
		//is this a directory copy?
		if (src.isDirectory()) 	{
			if (!dest.exists()) { //does the destination already exist?
				//if not we need to make it exist if possible (note this is mkdirs not mkdir)
				if (!dest.mkdirs()) {
					throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
				}
			}
			//get a listing of files...
			String list[] = src.list();
			//copy all the files in the list.
			for (int i = 0; i < list.length; i++)
			{
				File dest1 = new File(dest, list[i]);
				File src1 = new File(src, list[i]);
				copyFiles(src1 , dest1);
			}
		} else { 
			//This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
			int bytesRead;
			try {
				//open the files for input and output
				fin =  new FileInputStream(src);
				fout = new FileOutputStream (dest);
				//while bytesRead indicates a successful read, lets write...
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer,0,bytesRead);
				}
			} catch (IOException e) { //Error copying file... 
				IOException wrapper = new IOException("copyFiles: Unable to copy file: " + 
							src.getAbsolutePath() + "to" + dest.getAbsolutePath()+".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally { //Ensure that the files are closed (if they were open).
				if (fin != null) { fin.close(); }
				if (fout != null) { fout.close(); }
			}
		}
	}
	
	
	

	

}
