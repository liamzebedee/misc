/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.emonic.base.codehierarchy;

import java.io.Serializable;

import org.eclipse.core.runtime.IPath;

/**
 * Represents an entire compilation unit.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ICompilationUnit extends IDotNetElement, IParent, Serializable {

	/**
	 * Returns a list of all of the namespaces that have been declared within
	 * this compilation unit.
	 * 
	 * @return all of the namespaces defined by this compilation unit
	 */
	public INamespace[] getNamespaces();

	/**
	 * Returns the namespace contained within the compilation unit with the specified
	 * name.
	 * 
	 * @param namespaceName
	 *            the name of the namespace to retrieve
	 * @return the namespace in the assembly that matches the given name, or
	 *         <code>null</code> if such a namespace is not defined
	 */
	public INamespace getNamespace(String namespaceName);
	
	/**
	 * Returns a list of all the types within this compilation unit in the order
	 * in which they are defined in the compilation unit. This does not include
	 * local types that are declared within methods.
	 * 
	 * @return all of the types that have been defined by this compilation unit
	 */
	public IType[] getTypes();
	


	public IType getType(String typeName);

	/**
	 * Returns the path of the compilation unit located in the directory
	 */
	public IPath getPath();

}
