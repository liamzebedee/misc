/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.preferences;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.framework.FrameworkInstall;
import org.emonic.base.framework.IDotNetDebugConstants;
import org.emonic.base.framework.IFrameworkInstall;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class FrameworksPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.emonic.base.preferences.frameworksPreferencePage"; //$NON-NLS-1$
	
	private CheckboxTableViewer frameworksViewer;

	private Button addFrameworkBtn;

	private Button editFrameworkBtn;

	private Button removeFrameworkBtn;

	private List installs;

	private IEclipsePreferences preferences;

	public FrameworksPreferencePage() {
		setDescription(FrameworkMessages.FrameworksPreferencePage_Description);
	}

	private void addListeners() {
		frameworksViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent e) {
				if (e.getChecked()) {
					Object element = e.getElement();
					frameworksViewer
							.setCheckedElements(new Object[] { element });
					setErrorMessage(null);
					setValid(true);
				} else {
					setErrorMessage(FrameworkMessages.FrameworksPreferencePage_SelectDefaultFrameworkError);
					setValid(false);
				}
			}
		});

		frameworksViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						IStructuredSelection iss = (IStructuredSelection) e
								.getSelection();
						int size = iss.size();
						if (size == frameworksViewer.getTable().getItems().length) {
							// don't let the user remove every single item in
							// the table
							removeFrameworkBtn.setEnabled(false);
						} else {
							removeFrameworkBtn.setEnabled(true);
						}

						// the user can only edit one framework at a time
						editFrameworkBtn.setEnabled(size == 1);
					}
				});

		addFrameworkBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddFrameworkDialog dialog = new AddFrameworkDialog(
						addFrameworkBtn.getShell());
				if (Window.OK == dialog.open()) {
					IFrameworkInstall install = dialog.getInstall();
					installs.add(install);
					frameworksViewer.add(install);

					if (installs.size() == 1) {
						frameworksViewer.setChecked(install, true);
					} else {
						// since there are two more frameworks available, we
						// enable the remove button
						removeFrameworkBtn.setEnabled(true);
					}
				}
			}
		});

		editFrameworkBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iss = (IStructuredSelection) frameworksViewer
						.getSelection();
				Object element = iss.getFirstElement();
				AddFrameworkDialog dialog = new AddFrameworkDialog(
						addFrameworkBtn.getShell(), (FrameworkInstall) element);
				if (Window.OK == dialog.open()) {
					frameworksViewer.refresh(element, true);
				}
			}
		});

		removeFrameworkBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iss = (IStructuredSelection) frameworksViewer
						.getSelection();
				Object[] array = iss.toArray();
				installs.removeAll(Arrays.asList(array));
				frameworksViewer.remove(array);

				if (frameworksViewer.getTable().getItems().length == 1) {
					removeFrameworkBtn.setEnabled(false);
				}

				if (frameworksViewer.getCheckedElements().length == 0) {
					setErrorMessage(FrameworkMessages.FrameworksPreferencePage_SelectDefaultFrameworkError);
					setValid(false);
				}
			}
		});
	}

	protected Control createContents(Composite parent) {
		// there are no "default" settings for this preference page
		noDefaultAndApplyButton();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		Label label = new Label(composite, SWT.LEAD);
		label
				.setText(FrameworkMessages.FrameworksPreferencePage_InstalledFrameworksLabel);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
				false, 2, 1));

		frameworksViewer = CheckboxTableViewer.newCheckList(composite,
				SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		frameworksViewer.setContentProvider(new ArrayContentProvider());
		frameworksViewer.setLabelProvider(new FrameworksLabelProvider());

		Table frameworksTable = frameworksViewer.getTable();
		frameworksTable.setLinesVisible(true);
		frameworksTable.setHeaderVisible(true);
		frameworksTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 3));

		TableColumn column = new TableColumn(frameworksTable, SWT.LEAD);
		column.setText(FrameworkMessages.FrameworksPreferencePage_NameColumn);
		column.setWidth(100);
		column = new TableColumn(frameworksTable, SWT.LEAD);
		column.setText(FrameworkMessages.FrameworksPreferencePage_LocationColumn);
		column.setWidth(200);
		column = new TableColumn(frameworksTable, SWT.LEAD);
		column.setText(FrameworkMessages.FrameworksPreferencePage_TypeColumn);
		column.setWidth(100);

		frameworksViewer.setInput(installs);

		addFrameworkBtn = new Button(composite, SWT.PUSH);
		addFrameworkBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true,
				false));
		addFrameworkBtn.setText(FrameworkMessages.FrameworksPreferencePage_AddButton);

		editFrameworkBtn = new Button(composite, SWT.PUSH);
		editFrameworkBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true,
				false));
		editFrameworkBtn.setText(FrameworkMessages.FrameworksPreferencePage_EditButton);
		editFrameworkBtn.setEnabled(false);

		removeFrameworkBtn = new Button(composite, SWT.PUSH);
		removeFrameworkBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true,
				false));
		removeFrameworkBtn
				.setText(FrameworkMessages.FrameworksPreferencePage_RemoveButton);
		removeFrameworkBtn.setEnabled(false);

		addListeners();

		String defaultFramework = preferences.get(
				IDotNetDebugConstants.DEFAULT_FRAMEWORK_INSTALL, null);
		for (int i = 0; i < installs.size(); i++) {
			IFrameworkInstall install = (IFrameworkInstall) installs.get(i);
			if (install.getId().equals(defaultFramework)) {
				frameworksViewer.setCheckedElements(new Object[] { install });
				break;
			}
		}

		return composite;
	}

	public void init(IWorkbench workbench) {
		installs = new ArrayList();
		preferences = new InstanceScope().getNode(EMonoPlugin.PLUGIN_ID);
		try {
			String[] keys = preferences.childrenNames();
			for (int i = 0; i < keys.length; i++) {
				FrameworkInstall install = new FrameworkInstall();
				install.setId(keys[i]);
				Preferences node = preferences.node(keys[i]);
				install.setName(node.get(
						IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME, "")); //$NON-NLS-1$
				install.setType(node.get(
						IDotNetDebugConstants.FRAMEWORK_INSTALL_TYPE, "")); //$NON-NLS-1$
				install.setInstallLocation(new File(node.get(
						IDotNetDebugConstants.FRAMEWORK_INSTALL_LOCATION, ""))); //$NON-NLS-1$
				install.setDocumentationLocation(node.get(
						IDotNetDebugConstants.FRAMEWORK_DOCUMENTATION_LOCATION,
						"")); //$NON-NLS-1$
				install
						.setRuntimeArguments(node
								.get(
										IDotNetDebugConstants.FRAMEWORK_INSTALL_RUNTIME_ARGUMENTS,
										"")); //$NON-NLS-1$
				install.setGACLocation(node.get(
						IDotNetDebugConstants.FRAMEWORK_GAC_LOCATION, ""));
				installs.add(install);
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public boolean performOk() {
		if (super.performOk()) {
			preferences = new InstanceScope()
					.getNode(EMonoPlugin.PLUGIN_ID);
			try {
				String[] children = preferences.childrenNames();
				for (int i = 0; i < children.length; i++) {
					Preferences p = preferences.node(children[i]);
					p.removeNode();
				}
				
				for (int i = 0; i < installs.size(); i++) {
					IFrameworkInstall install = (IFrameworkInstall) installs
							.get(i);
					Preferences node = preferences.node(install.getId());
					node.put(IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME,
							install.getName());
					node.put(IDotNetDebugConstants.FRAMEWORK_INSTALL_TYPE,
							install.getType());
					node.put(IDotNetDebugConstants.FRAMEWORK_INSTALL_LOCATION,
							install.getInstallLocation().getAbsolutePath());
					node.put(
							IDotNetDebugConstants.FRAMEWORK_DOCUMENTATION_LOCATION,
							install.getDocumentationLocation());
					node.put(
							IDotNetDebugConstants.FRAMEWORK_INSTALL_RUNTIME_ARGUMENTS,
							install.getRuntimeArguments());
					node.put(
							IDotNetDebugConstants.FRAMEWORK_GAC_LOCATION,
							install.getGACLocation());
					node.flush();
				}
				
				Object[] elements = frameworksViewer.getCheckedElements();
				if (elements.length != 0) {
					preferences.put(
							IDotNetDebugConstants.DEFAULT_FRAMEWORK_INSTALL,
							((IFrameworkInstall) elements[0]).getId());
				}
				preferences.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
}
