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

import org.eclipse.core.runtime.IPath;
import org.emonic.base.documentation.IDocumentation;

public class SourceEvent  extends SourceMember implements IEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7377995310168428718L;

	private String delegateType;

	private String name;

	private String signature;

	private String[] parameterTypes;

	private String[] parameterNames;

	private int parameters;

	public SourceEvent(IDotNetElement parent, int flags, IDocumentation documentation,
			String delegateType, String name, String signature, 
			String[] parameterNames, String[] parameterTypes,
			ISourceRange sourceRange, String source) {
		super(parent, flags, documentation, sourceRange, source);
		this.delegateType = delegateType;
		this.name = name;
		this.signature = signature;
		this.parameterNames = parameterNames;
		this.parameterTypes = parameterTypes;
		this.parameters = parameterTypes.length;;
		
	}	
	
	public String getDelegateType() {
		return delegateType;
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

	public String getSignature() {
		return signature;
	}

	public boolean isBinary() {
		return false;
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return EVENT;
	}

	public IPath getPath() {
		
		return parent.getPath();
	}	
	


}
