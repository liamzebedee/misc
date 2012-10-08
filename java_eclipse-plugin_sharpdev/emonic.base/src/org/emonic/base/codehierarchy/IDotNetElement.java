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

import java.io.Serializable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.emonic.base.documentation.IDocumentation;

/**
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IDotNetElement extends IAdaptable, Serializable {

	public static final int ROOT = 0;
	
	public static final int COMPILATION_UNIT = ROOT + 1;

	public static final int USING = COMPILATION_UNIT + 1;

	public static final int USING_CONTAINER = USING + 1;

	/**
	 * Constant representing a .NET assembly. A .NET element with this type can
	 * be safely cast to <code>IAssembly</code>.
	 */
	public static final int ASSEMBLY = USING_CONTAINER + 1;

	/**
	 * Constant representing a source file. A sourceunit element with this type can
	 * be safely cast to <code>ISourceUnit</code>
	 */
	public static final int SOURCEUNIT = ASSEMBLY + 1;
	
	/**
	 * Constant representing a namespace. A .NET element with this type can be
	 * safely cast to <code>INamespace</code>.
	 */
	public static final int NAMESPACE = SOURCEUNIT + 1;

	/**
	 * Constant representing a type (a class, interface, or enum). A .NET
	 * element with this type can be safely cast to <code>IType</code>.
	 */
	public static final int TYPE = NAMESPACE + 1;

	public static final int INTERFACE = TYPE + 1;

	public static final int CLASS = INTERFACE + 1;

	public static final int STRUCT = CLASS + 1;

	public static final int ENUM = STRUCT + 1;

	public static final int CONSTRUCTOR = ENUM + 1;
	
	public static final int DESTRUCTOR = CONSTRUCTOR + 1;

	/**assemblyPath
	 * Constant representing a method or constructor. A .NET element with this
	 * type can be safely cast to <code>IMethod</code>.
	 */
	public static final int METHOD = DESTRUCTOR + 1;

	/**
	 * Constant representing a field. A .NET element with this type can be
	 * safely cast to <code>IField</code>.
	 */
	public static final int FIELD = METHOD + 1;

	/**
	 * Constant representing a property. A .NET element with this type can be
	 * safely cast to <code>IProperty</code>.
	 */
	public static final int PROPERTY = FIELD + 1;

	/**
	 * Constant representing an event. A .NET element with this type can be
	 * safely cast to <code>IEvent</code>.
	 */
	public static final int EVENT = PROPERTY + 1;

	public static final int VARIABLE = EVENT + 1;

	public static final int COMMENT = VARIABLE + 1;

	public static final int STRING = COMMENT + 1;

	public IDotNetElement getAncestor(int ancestorType);

	public IDocumentation getDocumentation();

	/**
	 * Returns the name of this element.
	 * 
	 * @return the element name
	 */
	public String getElementName();

	/**
	 * Returns the kind of this element as an integer.
	 * 
	 * @return an integer value corresponding to the constants defined in
	 *         <code>IDotNetElement</code>
	 */
	public int getElementType();

	/**
	 * Returns the parent element that contains this element directly, or
	 * <code>null</code> if there is no parent.
	 * 
	 * @return the parent element, or <code>null</code> if there is no parent
	 */
	public IDotNetElement getParent();
	
	public IPath getPath();

}
