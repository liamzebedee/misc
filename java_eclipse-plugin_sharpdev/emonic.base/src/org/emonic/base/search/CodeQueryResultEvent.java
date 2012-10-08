/*
 * Created on 30.06.2007
 * emonic.base org.emonic.base.search CodeQueryResultEvent.java
 */
package org.emonic.base.search;

import org.eclipse.search.ui.SearchResultEvent;

final class CodeQueryResultEvent extends SearchResultEvent {

	/**
	 * We must create this class since SearchResultEvent is abstract....
	 */
	private static final long serialVersionUID = 8290480344753283129L;

	
	CodeQueryResultEvent( CodeQueryResult result)
	{
		super( result);
	}
	
	
}
