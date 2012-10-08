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

//import edu.arizona.cs.mbel2.mbel.Method;
//import edu.arizona.cs.mbel2.signature.MethodSignature;
//import edu.arizona.cs.mbel2.signature.ParameterSignature;
//import edu.arizona.cs.mbel2.signature.ReturnTypeSignature;
//import edu.arizona.cs.mbel2.signature.TypeSignature;

public class BinaryProperty extends BinaryMember implements IProperty {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5744277014704381421L;

	private String name;

	private String returnType;

	private String signature;

//	public BinaryProperty(IDotNetElement parent, Method method, String name,
//			IDocumentation documentation) {
//		super(parent, method.getFlags(), documentation);
//		this.name = name;
//
//		initializeReturnType(method);
//		initializeSignature(method);
//	}

	public BinaryProperty(IDotNetElement parent, int flags, String name, String returnType, String signature) {
		super(parent, flags, null);
		this.name = name;
		this.returnType = returnType;
		this.signature = signature;
	}

	public BinaryProperty(IDotNetElement parent, int flags,
			IDocumentation documentation) {
		 super(parent,flags,documentation);
	}

//	void initializeSignature(Method method) {
//		MethodSignature methodSignature = method.getSignature();
//		ParameterSignature[] sig = methodSignature.getParameters();
//		if (sig.length == 0) {
//			signature = ""; //$NON-NLS-1$
//		} else {
//			StringBuffer buffer = new StringBuffer();
//			synchronized (buffer) {
//				buffer.append('(');
//				for (int k = 0; k < sig.length; k++) {
//					TypeSignature typeSignature = sig[k].getType();
//					buffer.append(BinaryMethod.getParameterType(typeSignature));
//					buffer.append(' ').append(
//							sig[k].getParameterInfo().getName());
//					if (k < sig.length - 1) {
//						buffer.append(", "); //$NON-NLS-1$
//					}
//				}
//				buffer.append(')');
//				signature = buffer.toString();
//			}
//		}
//	}
//
//	private void initializeReturnType(Method method) {
//		ReturnTypeSignature retSignature = method.getSignature()
//				.getReturnType();
//		TypeSignature typeSignature = retSignature.getType();
//		returnType = BinaryMethod.getReturnType(typeSignature);
//	}

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

	public boolean isBinary() {
		return true;
	}

	public String getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISourceRange getSourceRange() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(String name2) {
		this.name=name2;
	}

	public void setSignature(String sig) {
		this.signature=sig;
		
	}

	public void setReturnType(String returnType2) {
		this.returnType=returnType2;
		
	}
}
