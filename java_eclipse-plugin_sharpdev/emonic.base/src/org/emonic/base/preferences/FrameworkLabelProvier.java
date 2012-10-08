/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *     B. Brem Port to base package
 *******************************************************************************/
package org.emonic.base.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.emonic.base.framework.IFrameworkInstall;

class FrameworksLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ((IFrameworkInstall) element).getName();
		case 1:
			return ((IFrameworkInstall) element).getInstallLocation()
					.getAbsolutePath();
		case 2:
			return ((IFrameworkInstall) element).getType();
		default:
			return super.getText(element);
		}
	}

}
