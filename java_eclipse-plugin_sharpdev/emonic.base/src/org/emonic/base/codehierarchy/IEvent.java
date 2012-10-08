/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
 * Represents an event that has been defined by a .NET type.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IEvent extends IMember {

	public String getDelegateType();

	/**
	 * This method is experimental and may not work as expected and may be
	 * removed in the future.
	 */
	public int getNumberOfParameters();

	/**
	 * This method is experimental and may not work as expected and may be
	 * removed in the future.
	 */
	public String[] getParameterNames();

	/**
	 * This method is experimental and may not work as expected and may be
	 * removed in the future.
	 */
	public String[] getParameterTypes();

	/**
	 * This method is experimental and may not work as expected and may be
	 * removed in the future.
	 */
	public String getSignature();

}
