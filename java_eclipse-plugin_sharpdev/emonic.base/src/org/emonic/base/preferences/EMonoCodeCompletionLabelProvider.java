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


package org.emonic.base.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class EMonoCodeCompletionLabelProvider extends LabelProvider implements
				ITableLabelProvider{

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		
		switch(columnIndex){		
		default:
			return super.getText(element);
		}
	}

	
	

}
