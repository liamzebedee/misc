/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.emonic.base.wizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;

class NewBuildContributionWizardPage extends WizardPage {

	/**
	 * A radio button for specifying that the created type should be appended as
	 * part of a compilation task
	 */
	private Button existingTargetBtn;

	/**
	 * A combo control to select a target that the created element should be
	 * added to.
	 */
	private Combo existingTargetsCombo;

	/**
	 * A radio button specifying that a new target should be created in the
	 * build file to compile this created type.
	 */
	private Button newTargetBtn;

	private Text buildFileText;

	private Label targetNameLabel;

	private Text targetNameText;

	private Label targetNameInfoLabel;

	private Label referencesLabel;

	private TableViewer referencesViewer;
	private Button addReferenceBtn;
	private Button removeReferenceBtn;

	private Label definesLabel;

	private Text definesText;

	private Label warningLabel;

	private Combo warningCombo;

	private Label optimizeLabel;

	private Button optimizeCheckBox;

	private Label debugLabel;

	private Button debugCheckBox;

	private Label targetTypeLabel;

	private Combo targetTypeCombo;

	private NewDotNetTypeWizardPage page;

	private String language;

	NewBuildContributionWizardPage(NewDotNetTypeWizardPage page, String language) {
		super(NewBuildContributionWizardPage.class.getName());
		this.page = page;
		this.language = language;

		setTitle("Compilation Configuration");
		setDescription("Add the created source file to a target.");
	}

	public void createControl(Composite parent) {
		WizardDialog dialog = (WizardDialog) getContainer();
		dialog.addPageChangedListener(new IPageChangedListener() {
			public void pageChanged(PageChangedEvent e) {
				if (e.getSelectedPage() == NewBuildContributionWizardPage.this) {
					update();
				}
			}
		});

		parent = new Composite(parent, SWT.NONE);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(3, false));

		existingTargetBtn = new Button(parent, SWT.RADIO);
		existingTargetBtn.setText("Add to an existing build target:");
		existingTargetBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				enableNewTargetWidgets(false);
				existingTargetsCombo.setEnabled(true);

				if (verify()) {
					setPageComplete(null);
				}
			}
		});

		existingTargetsCombo = new Combo(parent, SWT.READ_ONLY);
		existingTargetsCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 2, 1));

		newTargetBtn = new Button(parent, SWT.RADIO);
		newTargetBtn.setText("Create a new build target in file:");
		newTargetBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				enableNewTargetWidgets(true);
				existingTargetsCombo.setEnabled(false);

				if (verify()) {
					setPageComplete(null);
				}
			}
		});

		buildFileText = new Text(parent, SWT.SINGLE | SWT.READ_ONLY
				| SWT.BORDER);
		buildFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		targetTypeLabel = new Label(parent, SWT.LEFT);
		targetTypeLabel.setText("Target type:");

		targetTypeCombo = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
		targetTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		targetTypeCombo.setItems(new String[] { "exe", "winexe", "library",
				"module" });
		targetTypeCombo.select(0);

		targetNameLabel = new Label(parent, SWT.LEFT);
		targetNameLabel.setText("Target name:");

		targetNameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		targetNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		targetNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (verify()) {
					setPageComplete(null);
				}
			}
		});

		targetNameInfoLabel = new Label(parent, SWT.CENTER);
		targetNameInfoLabel
				.setText("Hint: This is the assembly filename without the extension");
		targetNameInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 4, 1));

		referencesLabel = new Label(parent, SWT.LEFT);
		referencesLabel.setText("References:");
		referencesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.END,
				false, false, 3, 1));

		referencesViewer = new TableViewer(parent);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2);
		data.heightHint = 50;
		referencesViewer.getTable().setLayoutData(data);
		referencesViewer.setContentProvider(new ArrayContentProvider());
		referencesViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						removeReferenceBtn.setEnabled(!e.getSelection()
								.isEmpty());
					}
				});

		addReferenceBtn = new Button(parent, SWT.PUSH);
		addReferenceBtn.setText("&Add...");
		addReferenceBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				false, false, 1, 1));
		addReferenceBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(addReferenceBtn.getShell(),
						SWT.OPEN);

				String workspaceRoot = ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().toString();
				String sourceFolder = page.getSourceFolder();
				dialog.setFilterPath(workspaceRoot + File.separatorChar
						+ sourceFolder);
				dialog.setText("Add a reference to an external assembly");

				String selected = dialog.open();
				if (selected != null) {
					referencesViewer.add(selected.trim());
				}
			}
		});

		removeReferenceBtn = new Button(parent, SWT.PUSH);
		removeReferenceBtn.setText("&Remove");
		removeReferenceBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				false, false, 1, 1));
		removeReferenceBtn.setEnabled(false);
		removeReferenceBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iss = (IStructuredSelection) referencesViewer
						.getSelection();
				referencesViewer.remove(iss.toArray());
			}
		});

		definesLabel = new Label(parent, SWT.LEFT);
		definesLabel.setText("Defines (separated by spaces):");

		definesText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		definesText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		warningLabel = new Label(parent, SWT.LEFT);
		warningLabel.setText("Warning level:");

		warningCombo = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
		warningCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		warningCombo.setItems(new String[] { "0", "1", "2", "3", "4" });
		warningCombo.select(4);

		optimizeLabel = new Label(parent, SWT.LEFT);
		optimizeLabel.setText("Optimize:");

		optimizeCheckBox = new Button(parent, SWT.CHECK);
		optimizeCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		optimizeCheckBox.setSelection(true);

		debugLabel = new Label(parent, SWT.LEFT);
		debugLabel.setText("Debug:");

		debugCheckBox = new Button(parent, SWT.CHECK);
		debugCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		debugCheckBox.setSelection(true);

		setControl(parent);
		update();
		if (verify()) {
			setPageComplete(null);
		}
	}

	private void update() {
		BuildMechanismManipulator manipulator = BuildDescriptionFactory
				.getBuildMechanismManipulator(getProject());
		// Manipulator buildfile-based?
		if (manipulator.isBuildFileManipulator()) {
			IFile buildFile = manipulator.getBuildFile();
			buildFileText
					.setText(buildFile.getProjectRelativePath().toString());
			buildFileText.setEnabled(true);
		} else {
			buildFileText.setText(""); //$NON-NLS-1$
			buildFileText.setEnabled(false);
		}

		// Do we have a build mechanism capable of this language?
		if (manipulator.isSourceTargetManipulator()
				&& manipulator.supportsLanguage(language)) {

			// IBuildSourceTargetManipulator btm = (
			// IBuildSourceTargetManipulator) manipulator;

			enableTargetWidgets(true);

			// retrieve all the valid targets for this language's
			// compilation targets
			String[] items = manipulator.getAllTargetNamesOfLanguage(language);
			// we set it into the combo box
			existingTargetsCombo.setItems(items);
			if (items.length != 0) {
				// we have some items, so we select it and assume the user
				// will want to append it to one of these targets
				existingTargetsCombo.select(0);
				existingTargetBtn.setSelection(true);
				enableNewTargetWidgets(false);
			} else {
				// there are no targets, we allow the user to create a new
				// one
				existingTargetsCombo.setEnabled(false);
				newTargetBtn.setSelection(true);
				enableNewTargetWidgets(true);
			}
		} else {
			enableTargetWidgets(false);
		}
	}

	private void enableTargetWidgets(boolean enabled) {
		existingTargetBtn.setEnabled(enabled);
		existingTargetsCombo.setEnabled(enabled);
		newTargetBtn.setEnabled(enabled);
	}

	private void enableNewTargetWidgets(boolean enabled) {
		buildFileText.setEnabled(enabled);
		targetNameLabel.setEnabled(enabled);
		targetNameText.setEnabled(enabled);
		targetNameInfoLabel.setEnabled(enabled);
		referencesLabel.setEnabled(enabled);
		addReferenceBtn.setEnabled(enabled);
		definesLabel.setEnabled(enabled);
		definesText.setEnabled(enabled);
		warningLabel.setEnabled(enabled);
		warningCombo.setEnabled(enabled);
		optimizeLabel.setEnabled(enabled);
		optimizeCheckBox.setEnabled(enabled);
		debugLabel.setEnabled(enabled);
		debugCheckBox.setEnabled(enabled);
		targetTypeLabel.setEnabled(enabled);
		targetTypeCombo.setEnabled(enabled);
	}

	String getExistingTargetName() {
		return existingTargetsCombo.getItem(existingTargetsCombo
				.getSelectionIndex());
	}

	String getTargetName() {
		if (isAppendingToTarget()) {
			return getExistingTargetName();
		} else {
			return targetNameText.getText().trim();
		}
	}

	String[] getReferences() {
		if (referencesViewer == null) {
			return new String[0];
		} else {
			TableItem[] items = referencesViewer.getTable().getItems();
			int length = items.length;
			String[] references = new String[length];
			for (int i = 0; i < length; i++) {
				references[i] = items[i].getText();
			}
			return references;
		}
	}

	String getDefines() {
		return definesText.getText().trim();
	}

	int getWarningLevel() {
		return warningCombo.getSelectionIndex();
	}

	boolean shouldCreateDebugOutput() {
		return debugCheckBox.getSelection();
	}

	boolean shouldOptimize() {
		return optimizeCheckBox.getSelection();
	}

	String getTargetType() {
		return targetTypeCombo.getItem(targetTypeCombo.getSelectionIndex());
	}

	boolean isAppendingToTarget() {
		return existingTargetBtn.getSelection();
	}

	private IProject getProject() {
		String sourceFolder = page.getSourceFolder();
		int slashIndex = sourceFolder.indexOf('/');

		String projectName = sourceFolder;
		if (slashIndex != -1) {
			projectName = sourceFolder.substring(0, slashIndex);
		}

		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	private boolean verify() {
		if (isAppendingToTarget()) {
			// if we're appending the file to an existing target, we want to
			// make sure that a target has been selected
			if (existingTargetsCombo.getSelectionIndex() == -1) {
				setPageComplete("No target has been specified.");
				return false;
			}
		} else {
			String targetName = getTargetName();
			// if we're creating a new target, make sure that a target has been
			// specified
			if (targetName.equals("")) { //$NON-NLS-1$
				setPageComplete("A target name must be specified.");
				return false;
			}
		}
		return true;
	}

	private void setPageComplete(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

}
