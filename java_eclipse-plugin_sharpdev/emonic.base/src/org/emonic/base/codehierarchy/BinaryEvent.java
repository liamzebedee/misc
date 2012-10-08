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

//import edu.arizona.cs.mbel2.mbel.Event;
//import edu.arizona.cs.mbel2.mbel.Method;
//import edu.arizona.cs.mbel2.mbel.TypeDef;
//import edu.arizona.cs.mbel2.mbel.TypeRef;

public class BinaryEvent extends BinaryMember implements IEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6235207265659355424L;

	private String delegateType;

	private String name;

	private String signature;

	private String[] parameterTypes;

	private String[] parameterNames;

	private int parameters;

	// BinaryEvent(IDotNetElement parent, Event event) {
	public BinaryEvent(IDotNetElement parent, String name, int flags,
			String delegatetype, String[][] signatureInfo) {
		super(parent, flags, null);
		this.name = name;
		if (signatureInfo != null) {
			signature = signatureInfo[0][0];
			parameterTypes = signatureInfo[1];
			parameterNames = signatureInfo[2];
			parameters = parameterTypes.length;
		}
	}

	

	public void setDocumentation(IDocumentation documentation) {
		this.documentation = documentation;
	}

	public String getDelegateType() {
		return delegateType;
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return EVENT;
	}

	public boolean isBinary() {
		return false;
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

	public String getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISourceRange getSourceRange() {
		// TODO Auto-generated method stub
		return null;
	}

}
