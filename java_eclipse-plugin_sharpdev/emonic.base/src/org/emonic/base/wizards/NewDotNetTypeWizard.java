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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * An abstract <tt>IWizard</tt> implementation for creating new types (such as
 * classes and interfaces) for the .NET environment.
 */
abstract class NewDotNetTypeWizard extends Wizard implements IWorkbenchWizard, INewWizard {

	IWorkbench workbench;
	IWorkspaceRoot root;
	IContainer container;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		Object element = selection.getFirstElement();
		// try to see if the selected element can be adapted to an IResource, if
		// yes, we check if it's an IContainer, if it is, we set it to the
		// field, otherwise, we retrieve its parent
		if (element instanceof IResource) {
			IResource resource = (IResource) element;
			container = resource instanceof IContainer ? (IContainer) resource
					: resource.getParent();
		} else if (element instanceof IAdaptable) {
			IResource resource = (IResource) ((IAdaptable) element)
					.getAdapter(IResource.class);
			container = resource instanceof IContainer ? (IContainer) resource
					: resource.getParent();
		}
		root = ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Retrieves the first page of this wizard which is expected to be a
	 * {@link NewDotNetTypeWizardPage}.
	 * 
	 * @return the first page of this wizard, a <tt>NewDotNetTypeWizardPage</tt>
	 * @throws ClassCastException
	 *             if the first page is not an instanceof a
	 *             <tt>NewDotNetTypeWizardPage</tt>
	 */
	NewDotNetTypeWizardPage getTypeWizardPage() {
		return (NewDotNetTypeWizardPage) getPages()[0];
	}

}
