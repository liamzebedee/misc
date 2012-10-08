/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */
package org.emonic.base.codehierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

public class SourceUnit extends DotNetElement implements ISourceUnit{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9018737786788026682L;

	private Map namespaces;

	private Map types;

	private String name;

	private IPath path; 
	
	private String fileExtension;

	
	public SourceUnit(Map namespaces, Map types, String name, IPath path, String fileExtension) {
		super(null, null);
		this.namespaces = namespaces;
		this.types = types;
		this.name = name;
		this.path = path;
		this.fileExtension = fileExtension;
	}

	public INamespace getNamespace(String namespaceName) {
		return (INamespace) namespaces.get(namespaceName);
	}

	public INamespace[] getNamespaces() {
		return (INamespace[]) namespaces.values().toArray(
				new INamespace[namespaces.size()]);
	}

	public IPath getPath() {
		return path;
	}

	public IType getType(String typeName) {
		return (IType) types.get(typeName);
	}

	public IType[] getTypes() {
		return (IType[]) types.values().toArray(new IType[types.size()]);
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return SOURCEUNIT;
	}

	public IDotNetElement[] getChildren() {
		List list = new ArrayList(namespaces.values());
		list.addAll(types.values());
		return (IDotNetElement[]) list.toArray(new IDotNetElement[list.size()]);
	}

	public boolean hasChildren() {
		return !namespaces.isEmpty() || !types.isEmpty();
	}

	public String getFileExtension() {
		return fileExtension;
	}

}
