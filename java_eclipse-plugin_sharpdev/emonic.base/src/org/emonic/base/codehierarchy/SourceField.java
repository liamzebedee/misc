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

public class SourceField extends SourceMember implements IField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String typeSignature;
	
	private String name;
			
	public SourceField(IDotNetElement parent, int flags, String name, IDocumentation documentation,
			String typeSignature, ISourceRange sourceRange, String source){
		super(parent, flags, documentation, sourceRange,source);
		this.name = name;
		this.typeSignature = typeSignature;
	}
	
	public String getTypeSignature() {
		return typeSignature;
	}

	public boolean isBinary() {
		return false;
	}


	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return FIELD;
	}

	




}
