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
import org.emonic.base.filters.NonPublicMembersFilter;
import org.emonic.base.views.EMonoOutlinePage;

public class OutlineNonPublicMembersFilterAction extends Action
{
	private EMonoOutlinePage _page;	
	private NonPublicMembersFilter _filter;
	
	public OutlineNonPublicMembersFilterAction(EMonoOutlinePage page, String text) {
		super(text,Action.AS_CHECK_BOX);
		this._page = page;
		_filter = new NonPublicMembersFilter();
	}
	
	public void run() {
		// create a filter and remove the fields
		if (isChecked()) {
			EMonoOutlinePage.nonPublicMembersFilterEnabled = true;
			_page.addTreeViewerFilter(_filter);
		} else {
			EMonoOutlinePage.nonPublicMembersFilterEnabled = false;
			_page.removeTreeViewerFilter(_filter);
		}
	}
}
