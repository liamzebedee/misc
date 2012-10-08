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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * An abstract class that is to be extended to provide a basic user interface
 * for the creation of interfaces that are compatible with the .NET environment.
 */
abstract class NewDotNetInterfaceWizardPage extends NewDotNetTypeWizardPage {

	/**
	 * Creates a new generic wizard page for the creation of interfaces that are
	 * compatible with the .NET environment.
	 * 
	 * @param pageName
	 *            the name of this wizard page
	 * @param container
	 *            the container that will hold the created file, may be
	 *            <tt>null</tt>
	 */
	NewDotNetInterfaceWizardPage(String pageName, IContainer container) {
		super(pageName, container);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// create a source folder section for specifying the source folder
		createSourceFolderSection(composite);
		// create a section for specifying what namespace the created interface
		// will be in
		createNamespaceSection(composite);

		// create a separator
		createSeparator(composite);

		// create a section for specifying the name of this interface
		createTypeNameSection(composite);

		// create a section for specifying the superinterfaces of this interface
		createInterfacesSection(composite);

		setControl(composite);
		setPageComplete(false);
	}

}
