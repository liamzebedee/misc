/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;

public class CSharpTypingPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button closeStringsBtn;

	private Button closeBracketsBtn;

	private Button closeBracesBtn;

	public CSharpTypingPreferencePage() {
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
	}

	private void createCloseGroup(Composite contents) {
		Group group = new Group(contents, SWT.NONE);
		group.setText("Automatically close");
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(new GridLayout(1, true));

		closeStringsBtn = new Button(group, SWT.CHECK);
		closeStringsBtn.setText("&Strings");
		closeStringsBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));
		closeStringsBtn.setSelection(getPreferenceStore().getBoolean(
				DefaultPrefsSetter.QUOTESCOMPLETION));

		closeBracketsBtn = new Button(group, SWT.CHECK);
		closeBracketsBtn.setText("(Parentheses) and [square] brac&kets");
		closeBracketsBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));
		closeBracketsBtn.setSelection(getPreferenceStore().getBoolean(
				DefaultPrefsSetter.BRACKETCOMPLETION));

		closeBracesBtn = new Button(group, SWT.CHECK);
		closeBracesBtn.setText("{B&races}");
		closeBracesBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));
		closeBracesBtn.setSelection(getPreferenceStore().getBoolean(
				DefaultPrefsSetter.BRACESCOMPLETION));
	}

	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contents.setLayout(new GridLayout(1, true));

		createCloseGroup(contents);

		return contents;
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

	public boolean performOk() {
		if (!super.performOk()) {
			return false;
		}

		getPreferenceStore().setValue(DefaultPrefsSetter.QUOTESCOMPLETION,
				closeStringsBtn.getSelection());
		getPreferenceStore().setValue(DefaultPrefsSetter.BRACKETCOMPLETION,
				closeBracketsBtn.getSelection());
		getPreferenceStore().setValue(DefaultPrefsSetter.BRACESCOMPLETION,
				closeBracesBtn.getSelection());

		return true;
	}

}
