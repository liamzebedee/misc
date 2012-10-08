/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *     Bernhard Brem - Port to emonic.basic
 *******************************************************************************/
package org.emonic.base.preferences;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.framework.FrameworkBinaryFinder;
import org.emonic.base.framework.FrameworkFactory;
import org.emonic.base.framework.FrameworkInstall;
import org.emonic.base.framework.IFrameworkInstall;

public class AddFrameworkDialog extends StatusDialog {

	private static final int MICROSOFT_DOT_NET_INDEX = 0;

	private static final int MONO_INDEX = 1;

	private Combo frameworkTypeCombo;

	private Text nameText;

	private Text frameworkLocationText;

	private Button browseInstallLocationBtn;

	private Text documentationLocationText;

	private Button browseDocumentationLocationBtn;

	private Button browseGACLocationBtn;
	
	private Text argumentsText;
	
	private Text gacText;

	private IFrameworkInstall[] installs;

	private FrameworkInstall install;

	
	public AddFrameworkDialog(Shell parent) {
		this(parent, null);
	}

	public AddFrameworkDialog(Shell parent, FrameworkInstall install) {
		super(parent);
		this.install = install;
		installs = FrameworkFactory.getFrameworkInstalls();
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(AddFrameworkDialogMessages.AddFrameworkDialog_Title);
	}

	public void create() {
		super.create();
		if (install == null) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
	}

	
	
	private void addListeners() {
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String name = nameText.getText().trim();
				if (name.equals("")) { //$NON-NLS-1$
					updateStatus(new Status(IStatus.ERROR,
							EMonoPlugin.PLUGIN_ID, 0,
							AddFrameworkDialogMessages.AddFrameworkDialog_NoNameError, null));
				} else {
					verify();
				}
			}
		});

		frameworkTypeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (frameworkTypeCombo.getSelectionIndex() == MICROSOFT_DOT_NET_INDEX) {
					// we disable the arguments text field because you cannot
					// pass in runtime arguments to Microsoft's implementation
					argumentsText.setEnabled(false);
				} else {
					argumentsText.setEnabled(true);
				}
				verify();
			}
		});

		

	

	
		frameworkLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
				if (getStatus().isOK()) {
					if (documentationLocationText.getText().trim().equals("")) { //$NON-NLS-1$
						if (frameworkTypeCombo.getSelectionIndex() == MONO_INDEX) {
							StringBuffer locationText = new StringBuffer(
									frameworkLocationText.getText().trim());
							synchronized (locationText) {
								locationText.append(File.separatorChar).append(
										"lib"); //$NON-NLS-1$
								locationText.append(File.separatorChar).append(
										"monodoc"); //$NON-NLS-1$

								String locationString = locationText.toString();
								File location = new File(locationString);
								if (location.canRead()
										&& location.isDirectory()) {
									documentationLocationText
											.setText(locationString);
								}
							}
						}
					}
					if (gacText.getText().trim().equals("")) { //$NON-NLS-1$
						if (frameworkTypeCombo.getSelectionIndex() == MONO_INDEX) {
							StringBuffer locationText = new StringBuffer(
									frameworkLocationText.getText().trim());
							synchronized (locationText) {
								locationText.append(File.separatorChar).append(
										"lib"); //$NON-NLS-1$
								locationText.append(File.separatorChar).append(
										"mono"); //$NON-NLS-1$
								locationText.append(File.separatorChar).append(
										"2.0"); //$NON-NLS-1$

								String locationString = locationText.toString();
								File location = new File(locationString);
								if (location.canRead()
										&& location.isDirectory()) {
									gacText.setText(locationString);
								}
							}
						}
					}

				}
			}
		});

		browseInstallLocationBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(
						browseInstallLocationBtn.getShell());
				dialog.setFilterPath(frameworkLocationText.getText().trim());
				String path = dialog.open();
				if (path != null) {
					frameworkLocationText.setText(path);
				}
			}
		});

		documentationLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		
		gacText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		browseDocumentationLocationBtn
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						DirectoryDialog dialog = new DirectoryDialog(
								browseDocumentationLocationBtn.getShell());
						dialog.setFilterPath(documentationLocationText.getText().trim());
						String path = dialog.open();
						if (path != null) {
							documentationLocationText.setText(path);
						}
					}
				});
		
		browseGACLocationBtn
		.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(
						browseGACLocationBtn.getShell());
				dialog.setFilterPath(gacText.getText().trim());
				String path = dialog.open();
				if (path != null) {
					gacText.setText(path);
				}
			}
		});

	}

	protected Control createDialogArea(Composite parent) {
		parent = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(3, false));

		Label label = new Label(composite, SWT.LEAD);
		label.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkTypeLabel);
		frameworkTypeCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		frameworkTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false, 2, 1));
		frameworkTypeCombo.setItems(new String[] {
				IFrameworkInstall.MICROSOFT_DOT_NET_FRAMEWORK,
				IFrameworkInstall.MONO_FRAMEWORK });

		label = new Label(composite, SWT.LEAD);
		label.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkNameLabel);
		nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 2, 1));

		label = new Label(composite, SWT.LEAD);
		label.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkHomeDirectoryLabel);
		frameworkLocationText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		frameworkLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		browseInstallLocationBtn = new Button(composite, SWT.PUSH);
		browseInstallLocationBtn
				.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkHomeDirectoryBrowseButton);

		label = new Label(composite, SWT.LEAD);
		label
				.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkDocumentationDirectoryLabel);
		documentationLocationText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		documentationLocationText.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false));
		browseDocumentationLocationBtn = new Button(composite, SWT.PUSH);
		browseDocumentationLocationBtn
				.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkDocumentationDirectoryBrowseButton);

		label = new Label(composite, SWT.LEAD);
		label
				.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkGACDirectoryLabel);
		gacText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gacText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));

		browseGACLocationBtn = new Button(composite, SWT.PUSH);
		browseGACLocationBtn
				.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkDocumentationDirectoryBrowseButton);

		
		label = new Label(composite, SWT.LEAD);
		label
				.setText(AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkDefaultRuntimeArgumentsLabel);
		argumentsText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		argumentsText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 2, 1));

		// if we're on a Windows machine, we'll default to Microsoft .NET as the
		// framework type
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			frameworkTypeCombo.select(0);
			argumentsText.setEnabled(false);
		} else {
			frameworkTypeCombo.select(1);
		}

		if (install != null) {
			// initialize the fields of this dialog if we're editing a framework
			// definition
			initialize();
		}

		addListeners();

		return composite;
	}

	private void initialize() {
		if (install.getType().equals(
				IFrameworkInstall.MICROSOFT_DOT_NET_FRAMEWORK)) {
			frameworkTypeCombo.select(MICROSOFT_DOT_NET_INDEX);
		} else {
			frameworkTypeCombo.select(MONO_INDEX);
		}

		nameText.setText(install.getName());
		frameworkLocationText.setText(install.getInstallLocation()
				.getAbsolutePath());
		documentationLocationText.setText(install.getDocumentationLocation());
		argumentsText.setText(install.getRuntimeArguments());
		gacText.setText(install.getGACLocation());
	}

	private void verify() {
	FrameworkInstall	tstinstall = new FrameworkInstall();
	tstinstall.setType(frameworkTypeCombo.getText().trim());
	tstinstall.setName(nameText.getText().trim());
	tstinstall.setInstallLocation(new File(frameworkLocationText.getText()
			.trim()));
	tstinstall.setDocumentationLocation(documentationLocationText.getText()
			.trim());
	tstinstall.setGACLocation(gacText.getText().trim());
		String path = frameworkLocationText.getText().trim();
		if (path.equals("")) { //$NON-NLS-1$
			updateStatus(new Status(IStatus.ERROR, EMonoPlugin.PLUGIN_ID, 0,
					AddFrameworkDialogMessages.AddFrameworkDialog_NoLocationError, null));
		} else {
			File file = new File(path);
			if (!file.exists()) {
				updateStatus(new Status(
						IStatus.ERROR,
						EMonoPlugin.PLUGIN_ID,
						0,
						AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkLocationDoesNotExistError,
						null));
			} else if (!file.isDirectory()) {
				updateStatus(new Status(
						IStatus.ERROR,
						EMonoPlugin.PLUGIN_ID,
						0,
						AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkLocationIsFileError,
						null));
			} else if (!file.canRead()) {
				updateStatus(new Status(
						IStatus.ERROR,
						EMonoPlugin.PLUGIN_ID,
						0,
						AddFrameworkDialogMessages.AddFrameworkDialog_FrameworkLocationCannotBeReadError,
						null));
			} else if (frameworkTypeCombo.getSelectionIndex() == MONO_INDEX
					&& (new FrameworkBinaryFinder(tstinstall)).findMonoBinary() == null) {
				updateStatus(new Status(IStatus.ERROR, EMonoPlugin.PLUGIN_ID,
						0, AddFrameworkDialogMessages.AddFrameworkDialog_NoMonoBinaryError, null));
			} else {
				String name = nameText.getText().trim();
				if (name.equals("")) { //$NON-NLS-1$
					// if the user hasn't set a name yet, we'll set one
					nameText.setText(file.getName());
				} else if (install == null) {
					for (int i = 0; i < installs.length; i++) {
						if (installs[i].getName().equals(name)) {
							updateStatus(new Status(
									IStatus.ERROR,
									EMonoPlugin.PLUGIN_ID,
									0,
									AddFrameworkDialogMessages.AddFrameworkDialog_NameConflictError,
									null));
							return;
						}
					}
				} else {
					for (int i = 0; i < installs.length; i++) {
						if (!installs[i].getId().equals(install.getId())
								&& installs[i].getName().equals(name)) {
							updateStatus(new Status(
									IStatus.ERROR,
									EMonoPlugin.PLUGIN_ID,
									0,
									AddFrameworkDialogMessages.AddFrameworkDialog_NameConflictError,
									null));
							return;
						}
					}
				}

				String documentationLocation = documentationLocationText
						.getText().trim();
				if (documentationLocation.equals("")) { //$NON-NLS-1$
					updateStatus(Status.OK_STATUS);
					return;
				}

				File documentationDirectory = new File(documentationLocation);
				if (!documentationDirectory.exists()) {
					updateStatus(new Status(
							IStatus.ERROR,
							EMonoPlugin.PLUGIN_ID,
							0,
							AddFrameworkDialogMessages.AddFrameworkDialog_DocumentationLocationDoesNotExistError,
							null));
				} else if (!documentationDirectory.isDirectory()) {
					updateStatus(new Status(
							IStatus.ERROR,
							EMonoPlugin.PLUGIN_ID,
							0,
							AddFrameworkDialogMessages.AddFrameworkDialog_DocumentationLocationIsFileError,
							null));
				} else if (!documentationDirectory.canRead()) {
					updateStatus(new Status(
							IStatus.ERROR,
							EMonoPlugin.PLUGIN_ID,
							0,
							AddFrameworkDialogMessages.AddFrameworkDialog_DocumentationLocationCannotBeReadError,
							null));
				} else {
					updateStatus(Status.OK_STATUS);
				}
				
				//Global Assembly Cache
				String gacLocation = gacText.getText().trim();
				if (gacLocation.equals("")) { //$NON-NLS-1$
					//No CodeCompletion with GAC elements is set!
					updateStatus(Status.OK_STATUS);
					return;				
				}
				
				File gacDirectory = new File(gacLocation);
				if (!gacDirectory.exists()) {
					updateStatus(new Status(
							IStatus.ERROR,
							EMonoPlugin.PLUGIN_ID,
							0,
							AddFrameworkDialogMessages.AddFrameworkDialog_GACLocationDoesNotExistError,
							null));
				} else if (!gacDirectory.isDirectory()) {
					updateStatus(new Status(
							IStatus.ERROR,
							EMonoPlugin.PLUGIN_ID,
							0,
							AddFrameworkDialogMessages.AddFrameworkDialog_GACLocationIsFileError,
							null));
				} else if (!gacDirectory.canRead()) {
					updateStatus(new Status(
							IStatus.ERROR,
							EMonoPlugin.PLUGIN_ID,
							0,
							AddFrameworkDialogMessages.AddFrameworkDialog_GACLocationCannotBeReadError,
							null));
				} else {
					updateStatus(Status.OK_STATUS);
				}
				
			}
		}
	}

	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		if (point.x < 500) {
			point.x = 500;
		}
		return point;
	}

	protected void okPressed() {
		if (install == null) {
			install = new FrameworkInstall();
			install.setId(Long.toString(System.currentTimeMillis()));
		}
		install.setType(frameworkTypeCombo.getText().trim());
		install.setName(nameText.getText().trim());
		install.setInstallLocation(new File(frameworkLocationText.getText()
				.trim()));
		install.setDocumentationLocation(documentationLocationText.getText()
				.trim());
		install.setGACLocation(gacText.getText().trim());

		if (frameworkTypeCombo.getSelectionIndex() == MICROSOFT_DOT_NET_INDEX) {
			install.setRuntimeArguments(""); //$NON-NLS-1$
		} else {
			install.setRuntimeArguments(argumentsText.getText().trim());
		}

		super.okPressed();
	}

	public IFrameworkInstall getInstall() {
		return install;
	}
}
