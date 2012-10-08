/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.emonic.base.codehierarchy;


import org.emonic.base.documentation.IDocumentation;

//import edu.arizona.cs.mbel2.mbel.Field;


public class BinaryField extends BinaryMember implements IField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1153206151656974821L;

	private String name;

	private String typeSignature;

	public BinaryField(IDotNetElement parent, String theName, String theTypeSignature, int flags, IDocumentation documentation) {
		super(parent, flags, documentation);
		name = theName;
		//typeSignature = BinaryMethod.getParameterType(field.getSignature()
		//		.getType());
		typeSignature=theTypeSignature;
	}

	public BinaryField(IDotNetElement parent, int flags, String name,
			String typeSignature) {
		super(parent, flags, null);
		this.name = name;
		this.typeSignature = typeSignature;
	}

	
	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return FIELD;
	}

	public ISourceRange getSourceRange() {
		return null;
	}

	public String getTypeSignature() {
		return typeSignature;
	}

	public boolean isBinary() {
		return true;
	}

	public String getSource() {
		// TODO Auto-generated method stub
		return null;
	}

}
