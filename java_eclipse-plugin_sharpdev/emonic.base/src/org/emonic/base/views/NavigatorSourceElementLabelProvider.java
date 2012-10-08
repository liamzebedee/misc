/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.views;

import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IMethod;

public class NavigatorSourceElementLabelProvider extends
		SourceElementLabelProvider {

	public String getText(Object element) {
		if (element instanceof PendingUpdateAdapter) {
			return ((PendingUpdateAdapter) element).getLabel(element);
		} else if (element instanceof CodeElement) {
			CodeElement ele = (CodeElement) element;
			String val = ele.getElementName();

			if ((ele.getElementType() == IDotNetElement.METHOD)
					|| (ele.getElementType() == IDotNetElement.CONSTRUCTOR)
					|| (ele.getElementType() == IDotNetElement.DESTRUCTOR)) {
				val += ele.getNormedSignature();
			}
			return val;
		}
		switch (((IDotNetElement) element).getElementType()) {
		case IDotNetElement.METHOD:
			IMethod method = (IMethod) element;
			StringBuffer buffer = new StringBuffer();
			if (method.isConstructor()) {
				buffer.append(method.getParent().getElementName());
			} else {
				String name = method.getElementName();
				if (name.equals(".cctor")) {
					return name;
				}
				buffer.append(name);
			}
			synchronized (buffer) {
				buffer.append('(');
				String[] types = method.getParameterTypes();
				for (int i = 0; i < types.length; i++) {
					int index = types[i].lastIndexOf('.');
					if (index == -1) {
						buffer.append(types[i]).append(", "); //$NON-NLS-1$
					} else {
						buffer.append(types[i].substring(index + 1)).append(
								", "); //$NON-NLS-1$
					}
				}
				if (types.length != 0) {
					buffer.delete(buffer.length() - 2, buffer.length());
				}
				buffer.append(')');
				return buffer.toString();
			}
		default:
			return ((IDotNetElement) element).getElementName();
		}
	}
}
