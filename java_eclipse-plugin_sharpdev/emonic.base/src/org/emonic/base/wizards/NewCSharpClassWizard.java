/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM: Initial implementation
 * Bernhard Brem: Adaption to emonic
 * Remy Chi Jian Suen: Refactoring
 *******************************************************************************/
package org.emonic.base.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.filemanipulators.CSharpFileManipulator;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;

/**
 * This class is an <tt>IWizard</tt> implementation for creating a new class for
 * the C# language.
 */
public class NewCSharpClassWizard extends NewDotNetTypeWizard {

	public static final String WIZARD_ID = "org.emonic.base.wizards.newCSharpClassWizard"; //$NON-NLS-1$

	private static final String MSGERRORBODEY = "The class could not be created."; //$NON-NLS-1$

	private static final String MSGERRORHEAD = "File Creation Error"; //$NON-NLS-1$

	private static final String CSHARPENDING = ".cs";

	protected static final String EMPTYSTRING = "";

	private NewBuildContributionWizardPage buildPage;

	public NewCSharpClassWizard() {
		setWindowTitle("New C# Class");
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		addPage(new NewCSharpClassWizardPage(container));
		buildPage = new NewBuildContributionWizardPage(getTypeWizardPage(),
				IBuildMechanismDescriptor.CSHARP_LANGUAGE);
		addPage(buildPage);
	}

	private InputStream openContentStream() {
		String contents = EMPTYSTRING;
		return new ByteArrayInputStream(contents.getBytes());
	}

	public boolean performFinish() {
		final NewDotNetClassWizardPage page = (NewDotNetClassWizardPage) getTypeWizardPage();
		String folder = page.getSourceFolder();
		Path path = new Path(folder);

		// retrieve the IContainer that will contain the IFile that will be
		// created
		IContainer container = null;
		if (path.segmentCount() == 1) {
			// if the path has one segment, it's a project
			container = root.getProject(folder);
		} else {
			// if there are more, it's a folder
			container = root.getFolder(path);
		}

		final IContainer parent = container;
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

			protected void execute(IProgressMonitor monitor)
					throws CoreException {

				String typeName = page.getTypeName();
				String filename = typeName + CSHARPENDING;
				try {
					final IFile file = parent.getFile(new Path(filename));
					InputStream stream = openContentStream();
					if (file.exists()) {
						file.setContents(stream, true, true, monitor);
					} else {
						file.create(stream, true, monitor);
					}

					stream.close();
					CSharpFileManipulator mani = new CSharpFileManipulator(file);
					String namespace = page.getNamespace();
					String copyright = EMPTYSTRING;
					ProjectPreferencesManipulator pref = new ProjectPreferencesManipulator(
							file.getProject());
					copyright = pref.getCopyright();
					int pos = 0;
					if (!EMPTYSTRING.equals(copyright)) {
						pos = mani.addCopyright(copyright, true);
					}
					CodeElement ns = null;
					if (!EMPTYSTRING.equals(namespace)) {
						ns = new CodeElement(null, IDotNetElement.NAMESPACE);
						ns.setElementName(namespace);
					}

					CodeElement cls = new CodeElement(ns, IDotNetElement.CLASS);
					cls.setElementName(typeName);


					StringBuffer derived = new StringBuffer();
					String superclass = page.getSuperclass();
					if (superclass != null && !superclass.equals("")) { //$NON-NLS-1$
						derived.append(superclass);
						derived.append(", "); //$NON-NLS-1$
					}

					String[] superinterfaces = page.getSuperinterfaces();
					if (superinterfaces != null && superinterfaces.length != 0) {
						synchronized (derived) {
							for (int i = 0; i < superinterfaces.length; i++) {
								derived.append(superinterfaces[i]);
								derived.append(", "); //$NON-NLS-1$
							}
						}
					}

					if (!derived.toString().equals("")) { //$NON-NLS-1$
						derived.delete(derived.length() - 2, derived.length());
						cls.setDerived(derived.toString());
					}

					if (page.shouldCreateDefaultConstructor()) {
						CodeElement constructor = new CodeElement(cls,
								IDotNetElement.CONSTRUCTOR);
						constructor.setElementName(typeName);
						constructor.setAccessType("public");
						constructor.setSignature("()");
						cls.addChild(constructor);
					}

					if (page.shouldCreateMainMethod()) {
						CodeElement main = new CodeElement(cls,
								IDotNetElement.METHOD);
						main.setElementName("Main");
						main.setAccessType("public");
						main.setTypeSignature("void");
						main.staticMod = true;
						main.setSignature("(string[] args)");
						cls.addChild(main);
					}

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

					parent.refreshLocal(IResource.DEPTH_ONE,
							new NullProgressMonitor());

					BuildMechanismManipulator manipulator = BuildDescriptionFactory
							.getBuildMechanismManipulator(file.getProject());
					// use the build file manipulator to append the new
					// interface to an existing target or to create a new target
					// containing the created interface
					if (manipulator.isSourceTargetManipulator()) {
						if (buildPage.isAppendingToTarget()) {
							Target target = manipulator
									.getTarget(buildPage
											.getExistingTargetName());
							if (SourceTarget.class.isAssignableFrom(target.getClass())){
								SourceTarget starget=(SourceTarget)target;
								starget.addSource(file);
								manipulator.rewriteTarget(starget);
							}
						} else {
							String targetName = buildPage.getTargetName();
							//SourceTarget target = manipulator
							//.getSourceTarget(targetName);
							SourceTarget sourceTarget= new SourceTarget();
							sourceTarget.setName(targetName);
							sourceTarget
							.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
							sourceTarget.setType(buildPage.getTargetType());
							sourceTarget.addSource(file);
							sourceTarget.setOptimization(buildPage.shouldOptimize());
							sourceTarget.setDebuggingOutput(buildPage
									.shouldCreateDebugOutput());
							sourceTarget.setReferences(buildPage.getReferences());
							sourceTarget.setWarningLevel(buildPage.getWarningLevel());

							String definitions = buildPage.getDefines();
							if (definitions != null && !definitions.equals("")) {
								sourceTarget.setDefinitions(definitions
										.split("\\s+"));
							} else {
								sourceTarget.setDefinitions(new String[0]);
							}
							// TODO: Let the user decide
							manipulator.writeNewTargetInTree(sourceTarget,true);
						}

					}
					manipulator.save();
					try {
						IWorkbenchWindow window = workbench
								.getActiveWorkbenchWindow();
						// open the created class in an editor
						IDE.openEditor(window.getActivePage(), file, true);
					} catch (PartInitException e) {
						// ignored, we'll just not open the editor
					}
				} catch (IOException e2) {
					MessageDialog.openError(page.getShell(), MSGERRORHEAD,
							MSGERRORBODEY);
					e2.printStackTrace();
				}
			}
		};

		try {
			getContainer().run(false, true, op);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(page.getShell(), MSGERRORHEAD,
					MSGERRORBODEY);
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		return true;
	}
}
