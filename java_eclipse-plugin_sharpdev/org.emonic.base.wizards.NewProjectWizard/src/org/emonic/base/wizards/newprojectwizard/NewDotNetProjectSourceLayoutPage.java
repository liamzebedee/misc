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
package org.emonic.base.wizards.newprojectwizard;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.emonic.base.wizards.Messages;

public class NewDotNetProjectSourceLayoutPage extends WizardPage {

	private Text namespaceTextFld;

	private Text copyrightTextFld;

	private String _copyright = "(c) YEAR by AUTHOR\nAll rights reserved."; //$NON-NLS-1$

	private String _namespace = ""; //$NON-NLS-1$

	public NewDotNetProjectSourceLayoutPage() {
		super("wizardPage"); //$NON-NLS-1$
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite entryTable = new Composite(parent, SWT.NULL);

		// Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		entryTable.setLayout(layout);

		// Namespace group
		Group namespaceGroup = new Group(entryTable, SWT.NULL);
		namespaceGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		namespaceGroup.setLayout(new GridLayout(1, true));
		namespaceGroup.setText(Messages.getString("NewDotNetProjectSourceLayoutPage_Namespace")); //$NON-NLS-1$
		namespaceTextFld = new Text(namespaceGroup, SWT.SINGLE | SWT.BORDER);
		namespaceTextFld.setText(_namespace);
		namespaceTextFld.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		// Copyright group
		Group copyrightGroup = new Group(entryTable, SWT.NULL);
		copyrightGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		copyrightGroup.setLayout(new GridLayout(1, true));
		copyrightGroup.setText(Messages.getString("NewDotNetProjectSourceLayoutPage_Copyright")); //$NON-NLS-1$

		copyrightTextFld = new Text(copyrightGroup, SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		// copyrightTextFld.s
		copyrightTextFld.setText(_copyright);
		copyrightTextFld.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));

		copyrightTextFld.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				_copyright = copyrightTextFld.getText();
			}
		});
		
		namespaceTextFld.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				_namespace = namespaceTextFld.getText();
			}
		});

		setControl(entryTable);
	}

	String getCopyright() {
		return _copyright;
	}

	String getNamespace() {
		return _namespace;
	}
}