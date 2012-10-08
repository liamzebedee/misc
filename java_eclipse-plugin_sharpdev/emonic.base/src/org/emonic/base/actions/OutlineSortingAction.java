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
 *      Harald Krapfenbauer - Remember outline settings
 */

package org.emonic.base.actions;

import org.eclipse.jface.action.Action;
import org.emonic.base.views.EMonoOutlinePage;
import org.emonic.base.views.OutlineViewerComparator;
import org.emonic.base.views.OutlineViewerComparatorAlphabetical;

public class OutlineSortingAction extends Action
{
	private EMonoOutlinePage _page;	
	
	public OutlineSortingAction(EMonoOutlinePage page, String text){
		super(text, Action.AS_CHECK_BOX);
		this._page = page;
	}
	
	public void run() {
		// change viewer Comparator => 
		// add sorting to alphabetical order
		if (isChecked()) {
			EMonoOutlinePage.sortingEnabled = true;
			_page.setTreeViewerComparator(new OutlineViewerComparatorAlphabetical());
		} else {
			EMonoOutlinePage.sortingEnabled = false;
			_page.setTreeViewerComparator(new OutlineViewerComparator());
		}
	}
}
