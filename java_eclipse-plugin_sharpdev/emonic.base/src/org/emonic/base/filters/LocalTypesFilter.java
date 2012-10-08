/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2007 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */

package org.emonic.base.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.emonic.base.codehierarchy.IDotNetElement;

public class LocalTypesFilter extends ViewerFilter {
	
	//return false if the element is a local type (=variable)
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return ((IDotNetElement) element).getElementType() != IDotNetElement.VARIABLE;
	}

}
