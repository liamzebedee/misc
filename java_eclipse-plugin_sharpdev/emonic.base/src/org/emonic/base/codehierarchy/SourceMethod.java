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


public class SourceMethod extends SourceMember implements IMethod, ISourceReference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2989667735653990188L;

	private String name;

	private String signature;

	private String returnType;

	private String[] parameterTypes;

	private String[] parameterNames;

	private int parameters;

	private boolean isConstructor;
	
	private ILocalVariable[] localVars;

	public SourceMethod(IDotNetElement parent, String name, int flags,
			String returnType, String signature, boolean isConstructor, IDocumentation documentation,
			String[] parameterTypes, String[] parameterNames, ILocalVariable[] localVars,
			ISourceRange sourceRange, String source) {
	
		super(parent, flags, documentation, sourceRange, source);
		this.name = name;
		this.returnType = returnType;
		this.signature = signature;
		this.isConstructor = isConstructor;	
		this.parameterTypes = parameterTypes;
		this.parameterNames = parameterNames;
		this.localVars = localVars;
		parameters = parameterTypes.length;
	}
	
	public int getNumberOfParameters() {
		return parameters;
	}

	public String[] getParameterNames() {
		return parameterNames;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getSignature() {
		return signature;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public boolean isBinary() {
		return false;
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return METHOD;
	}
	
	public ILocalVariable[] getLocalVars(){
		return this.localVars;
	}

	

}
