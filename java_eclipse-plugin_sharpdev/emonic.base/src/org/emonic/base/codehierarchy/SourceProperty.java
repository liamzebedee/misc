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

import org.emonic.base.documentation.IDocumentation;

public class SourceProperty extends SourceMember implements IProperty {

	/**
	 * 
	 */
	private static final long serialVersionUID = 898322399669454830L;
	private String name;
	private String returnType;
	private String signature;

	public SourceProperty(IDotNetElement parent, int flags,
			String name, String returnType, String signature,
			IDocumentation documentation, ISourceRange sourceRange,
			String source) {
		super(parent, flags, documentation, sourceRange, source);
		this.name = name;
		this.returnType = returnType;
		this.signature = signature;
	}

	public boolean isBinary() {
		return false;
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return PROPERTY;
	}
	
	public String getReturnType() {
		return returnType;
	}

	public String getSignature() {
		return signature;
	}	

}
