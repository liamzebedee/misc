/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.debug.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.ide.IDE;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.framework.IDotNetDebugConstants;
import org.emonic.base.preferences.FrameworksPreferencePage;
import org.emonic.debug.core.EmonicDebugCore;
import org.emonic.debug.core.IDotNetLaunchConfigurationConstants;
import org.emonic.debug.core.IRemoteLaunch;
import org.emonic.debug.internal.ui.launching.Messages;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class MainTab extends AbstractLaunchConfigurationTab {

	private static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace()
			.getRoot();

	private Text projectText;

	private Button browseProjectBtn;

	private Text executableText;

	private Text workDirText;

	private Button browseExecutableBtn;

	private Button browseWorkDirBtn;

	private Combo frameworkCombo;

	private Button frameworkBtn;

	private IProject project;

	private ILaunchConfiguration configuration;

	private IEclipsePreferences preferences;

	private void addListeners() {
		projectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		browseProjectBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(
						browseProjectBtn.getShell(), new LabelProvider() {
							public Image getImage(Object element) {
								return PlatformUI
										.getWorkbench()
										.getSharedImages()
										.getImage(
												IDE.SharedImages.IMG_OBJ_PROJECT);
							}

							public String getText(Object element) {
								return ((IProject) element).getName();
							}
						});
				List elements = new ArrayList();
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
						.getProjects();
				for (int i = 0; i < projects.length; i++) {
					if (projects[i].isOpen()) {
						elements.add(projects[i]);
					}
				}
				dialog.setTitle(Messages.MainTab_BrowseProjectDialogTitle);
				dialog.setMessage(Messages.MainTab_BrowseProjectDialogMessage);
				dialog.setElements(elements.toArray());

				if (Window.OK == dialog.open()) {
					IProject project = (IProject) dialog.getFirstResult();
					projectText.setText(project.getName());
				}
			}
		});

		executableText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		browseExecutableBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final List resources = new ArrayList();
				try {
					project.accept(new IResourceVisitor() {
						public boolean visit(IResource resource) {
							if (resource instanceof IContainer) {
								return true;
							} else {
								IFile file = (IFile) resource;
								String extension = file.getFileExtension();
								if (extension != null
										&& extension.equalsIgnoreCase("exe")) { //$NON-NLS-1$
									resources.add(file);
								}
								return false;
							}
						}
					});
				} catch (CoreException ce) {
					// ignored as shouldn't be possible
				}

				ElementListSelectionDialog dialog = new ElementListSelectionDialog(
						browseProjectBtn.getShell(), new LabelProvider() {
							public String getText(Object element) {
								return ((IFile) element)
										.getProjectRelativePath().toString();
							}
						});
				dialog.setTitle(Messages.MainTab_BrowseExecutableDialogTitle);
				dialog
						.setMessage(Messages.MainTab_BrowseExecutableDialogMessage);
				dialog.setElements(resources.toArray());

				if (Window.OK == dialog.open()) {
					IFile file = (IFile) dialog.getFirstResult();
					executableText.setText(file.getProjectRelativePath()
							.toString());
				}
			}
		});

		workDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		browseWorkDirBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(browseWorkDirBtn
						.getShell());
				dd.setText("Choose working directory");
				if (!workDirText.equals("")
						&& new File(workDirText.getText()).isDirectory()) {
					dd.setFilterPath(workDirText.getText());
				} else {
					String filterPath = ROOT.getProject(projectText.getText())
							.getLocation().toString();
					if (!filterPath.equals(""))
						dd.setFilterPath(filterPath);
				}
				String selected = dd.open();
				workDirText.setText(selected);
			}
		});

		frameworkCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				verify();
			}
		});

		frameworkBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Window window = PreferencesUtil
						.createPreferenceDialogOn(
								frameworkBtn.getShell(),
								FrameworksPreferencePage.PAGE_ID,
								new String[] { FrameworksPreferencePage.PAGE_ID },
								null);
				if (Window.OK == window.open()) {
					updateFrameworkCombo();
				}
			}
		});
	}

	private void updateFrameworkCombo() {
		try {
			preferences = new InstanceScope()
					.getNode(EMonoPlugin.PLUGIN_ID);

			String framework = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_FRAMEWORK_ID,
					(String) null);
			if (framework == null) {
				framework = preferences.get(
						IDotNetDebugConstants.DEFAULT_FRAMEWORK_INSTALL, null);
			}

			if (framework != null) {
				String[] frameworkIds = preferences.childrenNames();
				if (frameworkIds.length != 0) {
					int index = -1;
					String[] frameworkNames = new String[frameworkIds.length];
					for (int i = 0; i < frameworkIds.length; i++) {
						if (framework.equals(frameworkIds[i])) {
							index = i;
						}
						frameworkNames[i] = preferences
								.node(frameworkIds[i])
								.get(
										IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME,
										null);
					}
					frameworkCombo.setItems(frameworkNames);

					if (index == -1) {
						framework = preferences
								.get(
										IDotNetDebugConstants.DEFAULT_FRAMEWORK_INSTALL,
										null);
						for (int i = 0; i < frameworkIds.length; i++) {
							if (framework.equals(frameworkIds[i])) {
								frameworkCombo.select(i);
							}
						}
					} else {
						frameworkCombo.select(index);
					}
				}
			}
		} catch (CoreException e) {
			// ignored
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void createProjectGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.MainTab_ProjectGroupLabel);
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setLayout(new GridLayout(2, false));

		projectText = new Text(group, SWT.SINGLE | SWT.BORDER);
		projectText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));

		browseProjectBtn = new Button(group, SWT.PUSH);
		browseProjectBtn.setText(Messages.MainTab_BrowseProjectButtonLabel);
	}

	private void createExecutableGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.MainTab_ExecutableGroupLabel);
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setLayout(new GridLayout(2, false));

		executableText = new Text(group, SWT.SINGLE | SWT.BORDER);
		executableText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));

		browseExecutableBtn = new Button(group, SWT.PUSH);
		browseExecutableBtn
				.setText(Messages.MainTab_BrowseExecutableButtonLabel);
	}

	private void createWorkDirGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		if (EmonicDebugUI.getDefault().getRemoteTabProvider() != null)
			// remote launching available --> display message that working directory
			// is just for local launching
			group.setText(Messages.MainTab_WorkDirGroupLabel_RemoteLaunchingAvailable);
		else
			group.setText(Messages.MainTab_WorkDirGroupLabel);
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setLayout(new GridLayout(2, false));

		workDirText = new Text(group, SWT.SINGLE | SWT.BORDER);
		workDirText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));

		browseWorkDirBtn = new Button(group, SWT.PUSH);
		browseWorkDirBtn.setText(Messages.MainTab_BrowseWorkDirButtonLabel);
	}

	private void createFrameworkGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.MainTab_FrameworkGroupLabel);
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setLayout(new GridLayout(3, false));

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.MainTab_TargetFrameworkComboLabel);

		frameworkCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		frameworkCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));

		frameworkBtn = new Button(group, SWT.PUSH);
		frameworkBtn.setText(Messages.MainTab_InstalledFrameworksButtonLabel);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createProjectGroup(composite);
		createExecutableGroup(composite);
		createWorkDirGroup(composite);
		createFrameworkGroup(composite);

		addListeners();

		setControl(composite);
	}

	public String getName() {
		return Messages.MainTab_TabName;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		this.configuration = configuration;
		try {
			String project = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
			if (!project.equals("")) { //$NON-NLS-1$
				projectText.setText(project);
			} else {
				browseExecutableBtn.setEnabled(false);
			}
		} catch (CoreException e) {
			// ignored
		}

		try {
			String executable = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_EXECUTABLE_NAME,
					""); //$NON-NLS-1$
			if (!executable.equals("")) { //$NON-NLS-1$
				executableText.setText(executable);
			}
		} catch (CoreException e) {
			// ignored
		}

		try {
			String workDir = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					""); //$NON-NLS-1$
			if (!workDir.equals("")) { //$NON-NLS-1$
				workDirText.setText(workDir);
			}
		} catch (CoreException e) {
			// ignored
		}

		updateFrameworkCombo();
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				IDotNetLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				projectText.getText());
		configuration.setAttribute(
				IDotNetLaunchConfigurationConstants.ATTR_EXECUTABLE_NAME,
				executableText.getText());
		configuration.setAttribute(
				IDotNetLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				workDirText.getText());

		try {
			String selectedFramework = frameworkCombo.getText();
			String[] keys = preferences.childrenNames();
			for (int i = 0; i < keys.length; i++) {
				Preferences node = preferences.node(keys[i]);
				String frameworkName = node.get(
						IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME, null);
				if (selectedFramework.equals(frameworkName)) {
					configuration
							.setAttribute(
									IDotNetLaunchConfigurationConstants.ATTR_FRAMEWORK_ID,
									keys[i]);
					break;
				}
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// nothing to do
	}

	private void verify() {
		browseExecutableBtn.setEnabled(false);

		String projectName = projectText.getText();
		if (projectName.equals("")) { //$NON-NLS-1$
			setErrorMessage(Messages.MainTab_ProjectNotSpecifiedError);
		} else {
			project = ROOT.getProject(projectName);
			if (!project.exists()) {
				setErrorMessage(NLS.bind(
						Messages.MainTab_ProjectDoesNotExistError, project
						.getName()));
			} else if (!project.isOpen()) {
				setErrorMessage(NLS.bind(Messages.MainTab_ProjectIsClosedError,
						project.getName()));
			} else {
				browseExecutableBtn.setEnabled(true);

				String executableName = executableText.getText();
				if (executableName.equals("")) { //$NON-NLS-1$
					setErrorMessage(Messages.MainTab_ExecutableNotSpecifiedError);
				} else {
					IFile file = project.getFile(executableName);
					if (file.exists()) {
						if (checkWorkDir() == true) {
							setErrorMessage(null);
//							setMessage(Messages.MainTab_RunApplicationMessage);
						} else {
							setErrorMessage(Messages.MainTab_WorkDirInvalidError);
						}
					} else {
						setErrorMessage(NLS.bind(
								Messages.MainTab_ExecutableDoesNotExistError,
								executableName));
					}
				}
			}
		}

		ILaunchConfigurationDialog dialog = getLaunchConfigurationDialog();
		if (dialog != null) {
			dialog.updateButtons();
			dialog.updateMessage();
		}
	}

	private boolean checkWorkDir() {
		// remote launch? --> work dir does not matter, since remote launch tab has an own field
		IRemoteLaunch remoteLaunch = EmonicDebugCore.getInstance()
		.getRemoteLaunchProvider();
		boolean test = false;
		try {
			if (remoteLaunch == null || !remoteLaunch.isLaunchRemote(configuration)) {
				test = true;
			} 
		} catch (CoreException ce) {
			test = true;
		}
		if (test) {
			// check local working directory
			String workDir = workDirText.getText();
			if (workDir == null || workDir.equals("")
					|| !new File(workDir).isDirectory()) {
				return false;
			}
		}
		return true;
	}
}
