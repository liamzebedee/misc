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
public interface INamespace extends IDotNetElement, IParent {


	/**
	 * Returns the type that has the same qualified name within this namespace.
	 * The string should not include the name of this namespace. A '+' symbol
	 * should be used to delimit inner types. Thus, if the type being sought is
	 * named 'TypeTwo' and it is declared within a type named 'TypeOne', the
	 * passed in string should be 'TypeOne+TypeTwo'.
	 * 
	 * @param name
	 *            the qualified name of the type including its parent declaring
	 *            type using the '+' symbol as a delimiter if applicable
	 * @return the type that matches the given name, or <tt>null</tt> if it
	 *         could be found
	 */
	public IType getType(String name);

	public IType[] getTypes();

}
