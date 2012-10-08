/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;

public class DotNetBuildPathPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Text sourceText;

	private Text outputText;

	public DotNetBuildPathPreferencePage() {
		setDescription("Specify the default build path entries used by the \"New .NET Project\" creation wizard:");
		noDefaultAndApplyButton();
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
	}

	protected Control createContents(Composite parent) {
		parent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setText("Source and output folder");

		Label label = new Label(group, SWT.LEAD);
		label.setText("&Source folder name:");
		sourceText = new Text(group, SWT.SINGLE | SWT.BORDER);
		sourceText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		sourceText.setText(getPreferenceStore().getString(
				DefaultPrefsSetter.SRCBIN_SRCNAME));

		label = new Label(group, SWT.LEAD);
		label.setText("&Output folder name:");
		outputText = new Text(group, SWT.SINGLE | SWT.BORDER);
		outputText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		outputText.setText(getPreferenceStore().getString(
				DefaultPrefsSetter.SRCBIN_BINNAME));
        
		return parent;
	}

	
	
	public boolean performOk() {
		if (super.performOk()) {
			getPreferenceStore().setValue(DefaultPrefsSetter.SRCBIN_SRCNAME,
					sourceText.getText());
			getPreferenceStore().setValue(DefaultPrefsSetter.SRCBIN_BINNAME,
					outputText.getText());
			return true;
		}
		return false;
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

}
