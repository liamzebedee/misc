/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.emonic.base.codehierarchy.CodeElement;

public class OpenElementAction extends Action {

	private IWorkbenchPage page;

	private ISelectionProvider selectionProvider;

	private CodeElement element;

	public OpenElementAction(IWorkbenchPage page,
			ISelectionProvider selectionProvider) {
		setText("Open");
		this.page = page;
		this.selectionProvider = selectionProvider;
	}

	public boolean isEnabled() {
		ISelection selection = selectionProvider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection iss = (IStructuredSelection) selection;
			if (iss.size() == 1) {
				Object o = iss.getFirstElement();
				if (o instanceof CodeElement) {
					element = (CodeElement) o;
					return true;
				}
			}
		}
		return false;
	}

	public void run() {
		IFile file =  element.getSourceAsResource();
		if (file.exists()) {
			try {
				IEditorPart part = IDE.openEditor(page, file);
				ITextEditor editor = (ITextEditor) part
						.getAdapter(ITextEditor.class);
				if (editor != null) {
					int start = element.getNameOffset();
					int length = element.getLength();
					int nameLength = element.getNameLength();
					if (nameLength == 0) {
						editor.setHighlightRange(start, length, true);
					} else {
						editor.selectAndReveal(start, nameLength);
					}
				}
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
