/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.wizards.newprojectwizard;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class NewDotNetProjectActionDelegate implements
		IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private IStructuredSelection iss;

	public void dispose() {
		// nothing to dispose
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		NewDotNetProjectResourceWizard wizard = new NewDotNetProjectResourceWizard();
		wizard.init(window.getWorkbench(), iss);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			iss = (IStructuredSelection) selection;
		} else {
			iss = StructuredSelection.EMPTY;
		}
	}

}
