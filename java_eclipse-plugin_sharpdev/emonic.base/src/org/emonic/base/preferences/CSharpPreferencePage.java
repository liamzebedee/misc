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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CSharpPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public CSharpPreferencePage() {
		setDescription("General C# settings are available in the contained preference pages.");
		noDefaultAndApplyButton();
	}

	protected Control createContents(Composite parent) {
		return parent;
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

}
