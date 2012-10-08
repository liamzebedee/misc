/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.wizards;

import org.eclipse.core.resources.IContainer;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;

/**
 * This class is a page in the wizard for creating a class for the C# language.
 */
class NewCSharpClassWizardPage extends NewDotNetClassWizardPage {

	/**
	 * Creates a new page for creating a new C# class.
	 * 
	 * @param container
	 *            the container for the file that is to be created, may be
	 *            <tt>null</tt>
	 */
	NewCSharpClassWizardPage(IContainer container) {
		super(NewCSharpClassWizardPage.class.getName(), container);
		setTitle("C# Class");
		setDescription("Create a new C# class.");
	}

	String getExtension() {
		return "cs"; //$NON-NLS-1$
	}

	String getLanguage() {
		return IBuildMechanismDescriptor.CSHARP_LANGUAGE;
	}

}
