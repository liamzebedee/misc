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

public class SourceLocalVariable extends SourceMember implements ILocalVariable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3108862171063366926L;

	private String name;
	
	private String typeSignature;

	public SourceLocalVariable(IDotNetElement parent, int flags,
			String name, String typeSignature,IDocumentation documentation, 
			ISourceRange sourceRange, String source) {
		super(parent, flags, documentation, sourceRange,  source);
		this.name = name;
		this.typeSignature = typeSignature;
	}
	
	public String getTypeSignature() {
		return typeSignature;
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return VARIABLE;
	}

	public boolean isBinary() {
		return false;
	}

}
