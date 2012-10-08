/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2007, 2008 University of Technology Vienna, ICT
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
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;

public class NonPublicMembersFilter extends ViewerFilter{

	//return false if the element is a non Public Method
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		switch (((IDotNetElement) element).getElementType()) {
		case IDotNetElement.METHOD:
		case IDotNetElement.FIELD:
		case IDotNetElement.PROPERTY:
		case IDotNetElement.CONSTRUCTOR:
			if(((CodeElement)element).getAccessType().equals("public"))
			  	return true;	//access type has to be public!
			else 
				return false; //private,internal or protected...
		case IDotNetElement.VARIABLE:
			return false;
		default:
			return true;
		}
	}
	
}
