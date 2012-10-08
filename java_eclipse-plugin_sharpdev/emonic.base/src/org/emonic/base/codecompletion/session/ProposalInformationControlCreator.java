/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.codecompletion.session;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledFormText;
import org.emonic.base.codehierarchy.CodeHierarchyHelper;
//import org.emonic.base.codehierarchy.MBel2AssemblyParser;
import org.emonic.base.documentation.IDocumentation;

final class ProposalInformationControlCreator implements IInformationControl,
		IInformationControlExtension, IInformationControlExtension2 {

	private Shell shell;

	private FormText text;

	private Object input;

	ProposalInformationControlCreator(Shell parent) {
		shell = new Shell(parent, SWT.NO_FOCUS | SWT.ON_TOP);
		shell.setLayout(new GridLayout());

		ScrolledFormText scrolledFormText = new ScrolledFormText(shell,
				SWT.V_SCROLL, false);
		text = new FormText(scrolledFormText, SWT.MULTI);
		scrolledFormText.setBackground(scrolledFormText.getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
		scrolledFormText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		scrolledFormText.setFormText(text);
		scrolledFormText.setExpandVertical(true);

		Display display = shell.getDisplay();
		text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		text.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		scrolledFormText.setBackground(scrolledFormText.getDisplay()
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		shell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#addDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener) {
		shell.addDisposeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#addFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void addFocusListener(FocusListener listener) {
		text.addFocusListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#computeSizeHint()
	 */
	public Point computeSizeHint() {
		// Return the shell's size
		// Note that it already has the persisted size if persisting is enabled.
		return shell.getSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#dispose()
	 */
	public void dispose() {
		shell.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#isFocusControl()
	 */
	public boolean isFocusControl() {
		return text.isFocusControl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setFocus()
	 */
	public void setFocus() {
		shell.forceFocus();
		text.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#removeDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void removeDisposeListener(DisposeListener listener) {
		shell.removeDisposeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#removeFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void removeFocusListener(FocusListener listener) {
		text.removeFocusListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBackgroundColor(Color background) {
		text.setBackground(background);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setForegroundColor(Color foreground) {
		text.setForeground(foreground);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setInformation(java.lang.String)
	 */
	public void setInformation(String information) {
		// Ignore, see IInformationControlExtension2
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setLocation(org.eclipse.swt.graphics.Point)
	 */
	public void setLocation(Point location) {
		shell.setLocation(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		shell.setSize(width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setSizeConstraints(int,
	 *      int)
	 */
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		shell.setVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension#hasContents()
	 */
	public boolean hasContents() {
		return input != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension2#setInput(java.lang.Object)
	 */
	public void setInput(Object input) {
		String doc = CodeHierarchyHelper.getFormText((IDocumentation) input);
		if (doc != null) {
			this.input = input;
			text.setText(doc, true, false);
		}
	}

}
