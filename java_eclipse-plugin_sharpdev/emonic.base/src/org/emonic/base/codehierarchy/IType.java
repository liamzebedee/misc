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
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IType extends IMember {

	public IEvent[] getEvents();

	public IField[] getFields();

	public String getFullName();

	public IMethod[] getMethods();

	public IProperty[] getProperties();

	/**
	 * Returns the name of this type's superclass, or <code>null</code> for
	 * source types that do not specify a superclass.
	 * <p>
	 * For interfaces, the superclass name is always
	 * <code>"System.Object"</code>.
	 * For source types, the name as declared is returned, for binary types, the
	 * resolved, qualified name is returned.
	 * </p>
	 * 
	 * @return the name of this type's superclass, or <code>null</code> for
	 *         source types that do not specify a superclass
	 */
	public String getSuperclassName();

	public String[] getSuperInterfaceNames();

	public IType[] getTypes();

	public boolean isClass();

	public boolean isEnum();

	public boolean isInterface();

	public boolean isStruct();

}
