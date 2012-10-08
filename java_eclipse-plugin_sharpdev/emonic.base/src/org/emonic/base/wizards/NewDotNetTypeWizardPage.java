/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.emonic.base.Constants;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;

/**
 * An abstract <tt>IWizardPage</tt> implementation that is to be subclassed
 * and added to <tt>IWizard</tt> implementations for the creation of .NET
 * types such as classes and interfaces.
 */
abstract class NewDotNetTypeWizardPage extends WizardPage {

	/**
	 * The text field control for specifying the source folder that will contain
	 * the created file.
	 * 
	 * @see #createSourceFolderSection(Composite)
	 */
	private Text sourceFolderText;

	private Button sourceFolderBrowseButton;
	/**
	 * The text field control for specifying the namespace for the generated
	 * interface.
	 * 
	 * @see #createNamespaceSection(Composite)
	 */
	private Text namespaceText;

	/**
	 * The text field control for specifying the name of the created type.
	 * 
	 * @see #createTypeNameSection(Composite)
	 */
	private Text typeNameText;

	/**
	 * The <tt>TableViewer</tt> that lists all the interfaces that this .NET
	 * type will implement.
	 */
	private TableViewer interfacesViewer;

	/**
	 * A button for adding interfaces to be implemented by the created .NET
	 * type.
	 */
	private Button addInterfaceBtn;

	private Button removeInterfaceBtn;

	private Image interfaceImg;

	private IContainer container;

	NewDotNetTypeWizardPage(String pageName, IContainer container) {
		super(pageName);
		this.container = container;
	}

	void createSourceFolderSection(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("&Source folder:");

		sourceFolderText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		sourceFolderText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false, 1, 1));
		
		sourceFolderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		if (container != null) {
			String path = container.getFullPath().toString();
			if (path.charAt(0) == '/') {
				path = path.substring(1);
			}
			sourceFolderText.setText(path);
		}
		
		sourceFolderBrowseButton = new Button(parent, SWT.PUSH);
		sourceFolderBrowseButton.setText("&Browse...");
		
		sourceFolderBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog csd = new ContainerSelectionDialog(
						sourceFolderBrowseButton.getShell(),
						ResourcesPlugin.getWorkspace().getRoot(),
						false,
						"Select source folder");
				csd.showClosedProjects(false);
				csd.open();
				Object[] result = csd.getResult();
				if (result != null && result[0] != null) {
					String selected = ((Path)result[0]).toString();
					if (selected.charAt(0) == '/') {
						selected = selected.substring(1);
					}
					sourceFolderText.setText(selected);
				}
			}
		});
	}

	void createNamespaceSection(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Names&pace:");

		namespaceText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		namespaceText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 2, 1));
		namespaceText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		if (container != null && !(container instanceof IWorkspaceRoot)) {
			String namespace = new ProjectPreferencesManipulator(container
					.getProject()).getNamespace();
			namespaceText.setText(namespace);
		}
	}

	void createTypeNameSection(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Na&me:");

		typeNameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		typeNameText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 2, 1));
		typeNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
				if (getMessageType() != IMessageProvider.ERROR) {
					String filename = typeNameText.getText() + '.'
							+ getExtension();
					String folder = getSourceFolder();
					if (folder != null) {
						IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
								.getRoot();
						Path path = new Path(folder);
						IFile targetFile;
						if (path.segmentCount() == 1) {
							IProject project = root.getProject(folder);
							targetFile = project.getFile(filename);
						} else {
							IFolder sourceFolder = root.getFolder(path);
							targetFile = sourceFolder.getFile(filename);
						}

						try {
							targetFile.refreshLocal(IResource.DEPTH_ZERO,
									new NullProgressMonitor());
							if (targetFile.exists()) {
								setPageComplete("Type already exists.");
							}
						} catch (CoreException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}

	public void dispose() {
		if (interfaceImg != null) {
			interfaceImg.dispose();
		}
		super.dispose();
	}

	void createInterfacesSection(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("&Interfaces:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false,
				false, 1, 2));

		interfaceImg = EMonoPlugin.imageDescriptorFromPlugin(
				"icons/newint_wiz.gif").createImage();

		interfacesViewer = new TableViewer(parent);
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 2);
		data.heightHint = 50;
		interfacesViewer.getTable().setLayoutData(data);
		interfacesViewer.setContentProvider(new ArrayContentProvider());
		interfacesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				removeInterfaceBtn.setEnabled(!e.getSelection().isEmpty());
			}
		});
		interfacesViewer.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return interfaceImg;
			}
		});

		addInterfaceBtn = new Button(parent, SWT.PUSH);
		addInterfaceBtn.setText("&Add...");
		addInterfaceBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				false, false));
		addInterfaceBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InputDialog dialog = new InputDialog(
						addInterfaceBtn.getShell(), "Implemented Interface",
						"Please enter the name of a .NET interface type", null,
						new IInputValidator() {
							public String isValid(String newText) {
								if (newText.trim().equals("")) {
									return "An interface must be specified.";
								} else {
									return null;
								}
							}							
						});
				if (Window.OK == dialog.open()) {
					interfacesViewer.add(dialog.getValue().trim());
				}
			}
		});

		removeInterfaceBtn = new Button(parent, SWT.PUSH);
		removeInterfaceBtn.setText("&Remove");
		removeInterfaceBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				false, false));
		removeInterfaceBtn.setEnabled(false);
		removeInterfaceBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iss = (IStructuredSelection) interfacesViewer
						.getSelection();
				interfacesViewer.remove(iss.toArray());
			}
		});
	}

	void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 3, 1));
	}

	void setPageComplete(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	String getSourceFolder() {
		return sourceFolderText == null ? null : sourceFolderText.getText()
				.trim();
	}

	String getNamespace() {
		return namespaceText == null ? null : namespaceText.getText().trim();
	}

	String getTypeName() {
		return typeNameText == null ? null : typeNameText.getText().trim();
	}

	String[] getSuperinterfaces() {
		if (interfacesViewer == null) {
			return null;
		} else {
			TableItem[] items = interfacesViewer.getTable().getItems();
			int length = items.length;
			String[] superinterfaces = new String[length];
			for (int i = 0; i < length; i++) {
				superinterfaces[i] = items[i].getText();
			}
			return superinterfaces;
		}
	}

	/**
	 * Retrieves the file extension for the file that is to be created.
	 * 
	 * @return the file extension of the file that will be created by this
	 *         page's containing wizard
	 */
	abstract String getExtension();

	abstract String getLanguage();

	boolean verify() {
		/* check source folder */
		String folder = getSourceFolder();
		if (folder != null) {
			if (folder.equals("")) { //$NON-NLS-1$
				// the user has not entered in a source folder
				setPageComplete("Source folder is empty.");
				return false;
			}
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			Path path = new Path(folder);
			IContainer sourceFolder = null;
			if (path.segmentCount() == 1) {
				sourceFolder = root.getProject(folder);
			} else {
				sourceFolder = root.getFolder(path);
			}
			// make sure that the folder exists
			if (!sourceFolder.exists()) {
				setPageComplete("Folder '" + folder + "' does not exist.");
				return false;
			}
			if (!sourceFolder.isAccessible()) {
				// warn the user if the folder is not accessible
				setPageComplete("Folder '" + folder
						+ "' is not accessible.");
				return false;
			}
			try {
				String[] natures = sourceFolder.getProject().getDescription().getNatureIds();
				boolean hasNature = false;
				for (int i = 0; i < natures.length; i++) {
					if (natures[i].equals(Constants.EmonicNatureID)) {
						hasNature = true;
						break;
					}
				}

				if (!hasNature) {
					setPageComplete("Source folder is not a .NET project.");
					return false;
				}
			} catch (CoreException e) {
				// technically this should not happen as we have checked
				// to make sure that the folder exists and is accessible
				setPageComplete("The project's properties could not be read.");
			}
		}

		/* check namespace */
		String namespace = getNamespace();
		if (namespace != null && !namespace.equals("")) { //$NON-NLS-1$
			// ensure that the first letter of the namespace name is a letter
			if (!Character.isLetter(namespace.charAt(0))) {
				setPageComplete("Namespace is not valid. The namespace '"
						+ namespace + "' is not a valid identifier.");
				return false;
			}
		}

		/* check for name */
		String typeName = getTypeName();
		// check that a name has been entered
		if (typeName != null && typeName.equals("")) { //$NON-NLS-1$
			setPageComplete("Type name is empty.");
			return false;
		}

		/* check for discouraged name */
		if (typeName != null) {
			// ensure that the first character of the type name is a letter
			if (!Character.isLetter(typeName.charAt(0))) {
				setPageComplete("Type name is not valid. The type name '"
						+ typeName + "' is not a valid identifier.");
				return false;
			}
			if (Character.isLowerCase(typeName.charAt(0))) {
				// if it's a lowercase character, warn the user that the
				// convention is to use uppercase letters
				setMessage(
						"Type name is discouraged. By convention, .NET type names usually start with an uppercase letter.",
						IMessageProvider.WARNING);
			} else {
				setMessage(getDescription());
			}
		}
		// all checks have passed, the page is complete
		setPageComplete(null);
		return true;
	}
	
	
}
