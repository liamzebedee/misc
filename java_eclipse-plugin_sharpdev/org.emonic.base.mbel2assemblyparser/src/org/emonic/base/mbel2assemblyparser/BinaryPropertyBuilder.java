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
package org.emonic.base.mbel2assemblyparser;


import org.emonic.base.codehierarchy.BinaryProperty;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.documentation.IDocumentation;

import edu.arizona.cs.mbel2.mbel.Method;
import edu.arizona.cs.mbel2.signature.MethodSignature;
import edu.arizona.cs.mbel2.signature.ParameterSignature;
import edu.arizona.cs.mbel2.signature.ReturnTypeSignature;
import edu.arizona.cs.mbel2.signature.TypeSignature;

public class BinaryPropertyBuilder {
	private static final long serialVersionUID = 5744277014704381421L;

	static BinaryProperty buildBinaryProoperty(IDotNetElement parent, Method method, String name,
			IDocumentation documentation) {
		BinaryProperty result = new BinaryProperty(parent, method.getFlags(), documentation);
		result.setName(name);

		result=initializeReturnType(result,method);
		result=initializeSignature(result,method);
		return result;
	}

	

	private static BinaryProperty initializeSignature(BinaryProperty in, Method method) {
		MethodSignature methodSignature = method.getSignature();
		ParameterSignature[] sig = methodSignature.getParameters();
		if (sig.length == 0) {
			in.setSignature(""); //$NON-NLS-1$
		} else {
			StringBuffer buffer = new StringBuffer();
			synchronized (buffer) {
				buffer.append('(');
				for (int k = 0; k < sig.length; k++) {
					TypeSignature typeSignature = sig[k].getType();
					buffer.append(BinaryMethodBuilder.getParameterType(typeSignature));
					buffer.append(' ').append(
							sig[k].getParameterInfo().getName());
					if (k < sig.length - 1) {
						buffer.append(", "); //$NON-NLS-1$
					}
				}
				buffer.append(')');
				in.setSignature( buffer.toString());
			}
		}
		return in;
	}

	private static BinaryProperty initializeReturnType(BinaryProperty in, Method method) {
		ReturnTypeSignature retSignature = method.getSignature()
				.getReturnType();
		TypeSignature typeSignature = retSignature.getType();
		in.setReturnType(BinaryMethodBuilder.getReturnType(typeSignature));
		return in;
	}

	
}
