/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.emonic.base.codehierarchy;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IPath;
import org.emonic.base.documentation.IDocumentation;

public class BinaryNamespace extends BinaryMember implements INamespace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map types;

	private String name;

	//private String path;

	public BinaryNamespace(IDotNetElement parent, String name, IPath  path,
			IDocumentation documentation) {
		super(parent, 0, documentation);
		this.name = name;
		//this.path = path;
		types = new TreeMap();
	}

	public void put(String name, IType type) {
		types.put(name, type);
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return NAMESPACE;
	}

	public IType getType(String name) {
		return (IType) types.get(name);
	}

	public IType[] getTypes() {
		return (IType[]) types.values().toArray(new IType[types.size()]);
	}

	public IDotNetElement[] getChildren() {
		return getTypes();
	}

	public boolean hasChildren() {
		return !types.isEmpty();
	}

	

//	public String getSource() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public ISourceRange getSourceRange() {
//		// TODO Auto-generated method stub
//		return null;
//	}



}
