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
package org.emonic.base.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.wizards.NewCSharpClassWizard;
import org.emonic.base.wizards.NewCSharpInterfaceWizard;

public class NewCSharpTypeActionDelegate implements
		IWorkbenchWindowPulldownDelegate {

	private IWorkbenchWindow window;
	private IStructuredSelection iss;

	private Menu menu;
	private Image classImage;
	private Image interfaceImage;

	public Menu getMenu(Control parent) {
		if (menu == null) {
			menu = new Menu(parent);

			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText("Class");
			classImage = EMonoPlugin.imageDescriptorFromPlugin(
					"icons/newclass_wiz.gif").createImage();
			item.setImage(classImage);
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					NewCSharpClassWizard wizard = new NewCSharpClassWizard();
					wizard.init(window.getWorkbench(), iss);
					WizardDialog dialog = new WizardDialog(window.getShell(),
							wizard);
					dialog.open();
				}
			});

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Interface");
			interfaceImage = EMonoPlugin.imageDescriptorFromPlugin(
					"icons/newint_wiz.gif").createImage();
			item.setImage(interfaceImage);
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					NewCSharpInterfaceWizard wizard = new NewCSharpInterfaceWizard();
					wizard.init(window.getWorkbench(), iss);
					WizardDialog dialog = new WizardDialog(window.getShell(),
							wizard);
					dialog.open();
				}
			});
		}
		return menu;
	}

	public void dispose() {
		if (menu != null && !menu.isDisposed()) {
			menu.dispose();
		}

		if (classImage != null && !classImage.isDisposed()) {
			classImage.dispose();
		}

		if (interfaceImage != null && !interfaceImage.isDisposed()) {
			interfaceImage.dispose();
		}
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		NewCSharpClassWizard wizard = new NewCSharpClassWizard();
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
