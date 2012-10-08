/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.codehierarchy;

/**
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ISourceReference {

	/**
	 * Returns the source code that is associated with this element. The string
	 * corresponds to the region that is covered by the source range returned by
	 * <code>getSourceRange()</code>.
	 * 
	 * @return the source code of this element, or <code>null</code> if this
	 *         element has no source code
	 */
	public String getSource();

	/**
	 * Returns the range of the source that is associated with this element.
	 * 
	 * @return the element's source range, or <code>null</code> if this
	 *         element has no associated source code
	 */
	public ISourceRange getSourceRange();

}
