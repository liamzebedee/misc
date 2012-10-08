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
 * Represents a .NET assembly.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IAssembly extends ICompilationUnit {

	/**
	 * Returns the major version of this assembly.
	 * 
	 * @return this assembly's major version
	 */
	public int getMajorVersion();

	/**
	 * Returns the minor version of this assembly.
	 * 
	 * @return this assembly's minor version
	 */
	public int getMinorVersion();

	/**
	 * Returns the build number of this assembly.
	 * 
	 * @return this assembly's build number
	 */
	public int getBuildNumber();

	/**
	 * Returns the reivision number of this assembly.
	 * 
	 * @return this assembly's revision number
	 */
	public int getRevisionNumber();


}
