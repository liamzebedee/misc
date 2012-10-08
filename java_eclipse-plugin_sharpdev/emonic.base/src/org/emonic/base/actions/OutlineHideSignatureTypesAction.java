/*
 * Created on 04.08.2007
 * emonic.base org.emonic.base.actions OutlineHideSignatureTypesAction.java
 * 
 * Contributors:
 * 		Bernhard Brem
 * 		Harald Krapfenbauer - Remember outline settings
 */
package org.emonic.base.actions;

import org.eclipse.jface.action.Action;
import org.emonic.base.views.EMonoOutlinePage;

/**
 * This action allows to switch on and of the display of types in the outline view
 * @author bb
 *
 */
public class OutlineHideSignatureTypesAction extends Action
{
	private EMonoOutlinePage _page;
	
	public OutlineHideSignatureTypesAction(EMonoOutlinePage page, String text) {
		super(text,Action.AS_CHECK_BOX);
		this._page = page;
	}
	
	public void run() {
		_page.setSigMode(!isChecked());
		EMonoOutlinePage.hideSignatureTypesEnabled = isChecked();
	}
}
