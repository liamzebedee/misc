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

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IPath;
import org.emonic.base.documentation.IDocumentation;

public class SourceNamespace extends DotNetElement implements INamespace {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4124411303053765267L;

	private Map types;

	private String name;
	
	private IPath path;
	
	private ISourceRange sourceRange;

	private String source;
	
	/**
	 * A counter of the open {. You need this info for code formations.....
	 */
	private int curlCounter;
	
	public SourceNamespace(IDotNetElement parent, String name, IPath path,
			IDocumentation documentation, ISourceRange sourceRange, String source) {
		super(parent, documentation);
		this.name = name;
		this.path = path;
		types = new TreeMap();
		this.curlCounter = -1;
		this.sourceRange = sourceRange;
		this.source = source;
	}	

	public void put(String name, IType type) {
		types.put(name, type);
	}
	
	public IType getType(String name) {
		return (IType) types.get(name);
	}

	public IType[] getTypes() {
		return (IType[]) types.values().toArray(new IType[types.size()]);
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return NAMESPACE;
	}

	public IDotNetElement[] getChildren() {
		return getTypes();
	}

	public boolean hasChildren() {
		return !types.isEmpty();
	}
	
	public ISourceRange getSourceRange() {
		return sourceRange;
	}

	public String getSource() {
		return source;
	}

	public IPath getPath() {
		return path;
	}
	
	/**
	 * Set the curlcounter
	 * @param curlcounter
	 */
	public void setCurlCounter(int curlcounter) {
		curlCounter = curlcounter;
	}
	
	/**
	 * Get the curlcounter
	 * @return
	 */
	public int getCurlCounter() {
		return curlCounter;
	}	
		
}
