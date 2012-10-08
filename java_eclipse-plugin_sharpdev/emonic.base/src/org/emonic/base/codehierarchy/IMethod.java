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
 * Represents a method or constructor that has been declared within a .NET type.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IMethod extends IMember {

	/**
	 * Returns the number of parameters that are defined by this method's
	 * signature.
	 * 
	 * @return the number of parameters of this method
	 */
	public int getNumberOfParameters();

	/**
	 * Returns the name of all the parameters that have been defined by this
	 * method's signature. This does not include generic parameters.
	 * 
	 * @return the names of the parameters of this method, or an empty array if
	 *         there are no parameters
	 */
	public String[] getParameterNames();

	public String[] getParameterTypes();

	public String getSignature();

	/**
	 * Returns the return type of this method. If the method's return type is
	 * <code>void</code>, <code>null</code> will be returned.
	 * 
	 * @return the return type of this method, or <code>null</code> if the
	 *         method's return type is <code>void</code>
	 */
	public String getReturnType();

	/**
	 * Returns whether this method definition is a constructor or not.
	 * 
	 * @return <code>true</code> if this method defines a constructor,
	 *         <code>false</code> otherwise
	 */
	public boolean isConstructor();

	/**
	 * Returns the local variables contained within the method
	 * @return
	 */
	public ILocalVariable[] getLocalVars();
}
