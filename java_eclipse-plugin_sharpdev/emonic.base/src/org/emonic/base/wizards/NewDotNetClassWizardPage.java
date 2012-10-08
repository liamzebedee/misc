/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * An abstract class that is to be extended to provide a basic user interface
 * for the creation of classes that are compatible with the .NET environment.
 */
abstract class NewDotNetClassWizardPage extends NewDotNetTypeWizardPage {

	private Text superclassText;

	private Button mainMethodBtn;
	private Button defaultConstructorBtn;

	/**
	 * Creates a new generic wizard page for the creation of classes that are
	 * compatible with the .NET environment.
	 * 
	 * @param pageName
	 *            the name of this wizard page
	 * @param container
	 *            the container that will hold the created file, may be
	 *            <tt>null</tt>
	 */
	NewDotNetClassWizardPage(String pageName, IContainer container) {
		super(pageName, container);
	}

	private void createSuperclassSection(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("&Superclass");

		superclassText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		superclassText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false, 2, 1));
		superclassText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
	}

	private void createMethodStubSection(Composite parent) {
		Label label = new Label(parent, SWT.LEAD);
		label.setText("Which method stubs would you like to create?");
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false,
				3, 1));

		new Label(parent, SWT.NONE);
		mainMethodBtn = new Button(parent, SWT.CHECK);
		mainMethodBtn.setText("public static void Main(string[] args)");
		mainMethodBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 2, 1));

		new Label(parent, SWT.NONE);
		defaultConstructorBtn = new Button(parent, SWT.CHECK);
		defaultConstructorBtn.setText("Default constructor");
		defaultConstructorBtn.setLayoutData(new GridData(SWT.FILL,
				SWT.BEGINNING, true, false, 2, 1));
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// create a section for specifying the name of this class
		createTypeNameSection(composite);

		// create a section for specifying the superclass of this class
		createSuperclassSection(composite);

		// create a section for specifying the superinterfaces of this class
		createInterfacesSection(composite);

		// create a section for the user to specify some generic method stubs to
		// be generated
		createMethodStubSection(composite);

		// create a separator
		createSeparator(composite);
		
		// create a source folder section for specifying the source folder
		createSourceFolderSection(composite);
		
		// create a namespace section
		createNamespaceSection(composite);
		
		setControl(composite);
		setPageComplete(false);
	}

	String getSuperclass() {
		return superclassText == null ? null : superclassText.getText().trim();
	}

	boolean shouldCreateMainMethod() {
		return mainMethodBtn.getSelection();
	}

	boolean shouldCreateDefaultConstructor() {
		return defaultConstructorBtn.getSelection();
	}

	boolean verify() {
		if (super.verify()) {
			String superclass = getSuperclass();
			// ensure that the first character of the superclass is a letter
			if (superclass != null && !superclass.equals("") //$NON-NLS-1$
					&& !Character.isLetter(superclass.charAt(0))) {
				setPageComplete("The superclass '" + superclass
						+ "' is not valid.");
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
}
