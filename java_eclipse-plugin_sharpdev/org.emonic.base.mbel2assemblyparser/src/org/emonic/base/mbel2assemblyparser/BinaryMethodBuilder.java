/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.mbel2assemblyparser;


import org.emonic.base.codehierarchy.BinaryMethod;
import org.emonic.base.codehierarchy.IDotNetElement;

import edu.arizona.cs.mbel2.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel2.mbel.GenericParam;
import edu.arizona.cs.mbel2.mbel.Method;
import edu.arizona.cs.mbel2.mbel.TypeRef;
import edu.arizona.cs.mbel2.signature.ArrayShapeSignature;
import edu.arizona.cs.mbel2.signature.ArrayTypeSignature;
import edu.arizona.cs.mbel2.signature.ClassTypeSignature;
import edu.arizona.cs.mbel2.signature.GenericInstantiationTypeSignature;
import edu.arizona.cs.mbel2.signature.MethodSignature;
import edu.arizona.cs.mbel2.signature.ParameterSignature;
import edu.arizona.cs.mbel2.signature.PointerTypeSignature;
import edu.arizona.cs.mbel2.signature.ReturnTypeSignature;
import edu.arizona.cs.mbel2.signature.SZArrayTypeSignature;
import edu.arizona.cs.mbel2.signature.SignatureConstants;
import edu.arizona.cs.mbel2.signature.TypeSignature;
import edu.arizona.cs.mbel2.signature.ValueTypeSignature;

public class BinaryMethodBuilder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2334196007157230895L;






	static String[][] initializeSignature(Method method) {
		MethodSignature methodSignature = method.getSignature();
		ParameterSignature[] sig = methodSignature.getParameters();

		String[] parameterNames = new String[sig.length];
		String[] parameterTypes = new String[sig.length];

		StringBuffer buffer = new StringBuffer();
		synchronized (buffer) {
			GenericParam[] parameters = methodSignature.getGenericParameters();
			if (parameters.length != 0) {
				buffer.append('<');
				for (int i = 0; i < parameters.length; i++) {
					buffer.append(parameters[i].getName());
					buffer.append(", "); //$NON-NLS-1$
				}
				buffer.delete(buffer.length() - 2, buffer.length());
				buffer.append('>');
			}
			buffer.append('(');
			for (int i = 0; i < sig.length; i++) {
				TypeSignature typeSignature = sig[i].getType();
				if (typeSignature == null) {
					parameterTypes[i] = "TypedReference";
					buffer.append("TypedReference");
				} else {
					parameterTypes[i] = getParameterType(typeSignature);
					buffer.append(getParameterType(typeSignature));
				}

				parameterNames[i] = sig[i].getParameterInfo().getName();
				buffer.append(' ').append(sig[i].getParameterInfo().getName());
				if (i < sig.length - 1) {
					buffer.append(", "); //$NON-NLS-1$
				}
			}
			buffer.append(')');
			if (parameters.length != 0) {
				for (int i = 0; i < parameters.length; i++) {
					AbstractTypeReference constraint = parameters[i]
							.getConstraint();
					if (constraint != null) {
						buffer.append(" where "); //$NON-NLS-1$
						buffer.append(parameters[i].getName()).append(" : ") //$NON-NLS-1$
								.append(
										getReferenceName(parameters[i]
												.getConstraint()));
					}
				}
				buffer.delete(buffer.length(), buffer.length());
			}
			return new String[][] { { buffer.toString() }, parameterTypes,
					parameterNames };
		}
	}

	static String getReturnType(TypeSignature typeSignature) {
		if (typeSignature == null) {
			return "void"; //$NON-NLS-1$
		} else {
			return getParameterType(typeSignature);
		}
	}

	static String getParameterType(TypeSignature typeSignature) {
		switch (typeSignature.getType()) {
		case SignatureConstants.ELEMENT_TYPE_BOOLEAN:
			return "bool";
		case SignatureConstants.ELEMENT_TYPE_CHAR:
			return "char";
		case SignatureConstants.ELEMENT_TYPE_I1:
			return "sbyte";
		case SignatureConstants.ELEMENT_TYPE_U1:
			return "byte";
		case SignatureConstants.ELEMENT_TYPE_I2:
			return "short";
		case SignatureConstants.ELEMENT_TYPE_U2:
			return "ushort";
		case SignatureConstants.ELEMENT_TYPE_I4:
			return "int";
		case SignatureConstants.ELEMENT_TYPE_U4:
			return "uint";
		case SignatureConstants.ELEMENT_TYPE_I8:
			return "long";
		case SignatureConstants.ELEMENT_TYPE_U8:
			return "ulong";
		case SignatureConstants.ELEMENT_TYPE_R4:
			return "single";
		case SignatureConstants.ELEMENT_TYPE_R8:
			return "double";
		case SignatureConstants.ELEMENT_TYPE_STRING:
			return "string";
		case SignatureConstants.ELEMENT_TYPE_PTR:
			TypeSignature pointerTypeSignature = ((PointerTypeSignature) typeSignature)
					.getPointerType();
			if (pointerTypeSignature == null) {
				return "void*";
			} else {
				return getParameterType(pointerTypeSignature) + '*';
			}
		case SignatureConstants.ELEMENT_TYPE_VALUETYPE:
			return getReferenceName(((ValueTypeSignature) typeSignature)
					.getValueType());
		case SignatureConstants.ELEMENT_TYPE_CLASS:
			return getReferenceName(((ClassTypeSignature) typeSignature)
					.getClassType());
		case SignatureConstants.ELEMENT_TYPE_VAR:
			return "T";
		case SignatureConstants.ELEMENT_TYPE_ARRAY:
			ArrayTypeSignature arrayTypeSignature = (ArrayTypeSignature) typeSignature;
			StringBuffer buffer = new StringBuffer(
					getParameterType(arrayTypeSignature.getElementType()));
			synchronized (buffer) {
				buffer.append('[');
				ArrayShapeSignature shape = arrayTypeSignature.getArrayShape();
				for (int i = 1; i < shape.getRank(); i++) {
					buffer.append(',');
				}
				buffer.append(']');
				return buffer.toString();
			}
		case SignatureConstants.ELEMENT_TYPE_GENERICINST:
			typeSignature = ((GenericInstantiationTypeSignature) typeSignature)
					.getElementType();
			if (typeSignature instanceof ClassTypeSignature) {
				return getReferenceName(((ClassTypeSignature) typeSignature)
						.getClassType());
			} else if (typeSignature instanceof ValueTypeSignature) {
				return getReferenceName(((ValueTypeSignature) typeSignature)
						.getValueType());
			}
			System.out.println(">>>" + typeSignature.getType() + "<<<");
			throw new RuntimeException();
		case SignatureConstants.ELEMENT_TYPE_I:
			return "System.IntPtr";
		case SignatureConstants.ELEMENT_TYPE_U:
			return "System.UIntPtr";
		case SignatureConstants.ELEMENT_TYPE_FNPTR:
			System.out.println(">>>" + typeSignature.getType() + "<<<");
			throw new RuntimeException();
		case SignatureConstants.ELEMENT_TYPE_OBJECT:
			return "object";
		case SignatureConstants.ELEMENT_TYPE_SZARRAY:
			return getParameterType(((SZArrayTypeSignature) typeSignature)
					.getElementType())
					+ "[]"; //$NON-NLS-1$
		default:
			System.out.println(">>>" + typeSignature.getType() + "<<<");
			throw new RuntimeException();
		}
	}

	static String getReferenceName(AbstractTypeReference reference) {
		String name = internalGetReferenceName(reference);
		int index = name.indexOf('`');
		if (index != -1) {
			return name.substring(0, index);
		}
		return name;
	}

	private static String internalGetReferenceName(
			AbstractTypeReference reference) {
		if (reference instanceof TypeRef) {
			TypeRef typeRef = (TypeRef) reference;
			String namespace = typeRef.getNamespace();
			if (namespace.equals("")) { //$NON-NLS-1$
				return typeRef.getName();
			} else {
				return namespace + '.' + typeRef.getName();
			}
		}
		System.err.println(reference.getClass().getName());
		throw new RuntimeException();
	}

	public static BinaryMethod buildBinaryMethod(IDotNetElement parent, Method method) {
		BinaryMethod result = new BinaryMethod(parent, method.getFlags(), null);
		result.setName(method.getName());
		result.setConstructor(method.getName().equals(".ctor"));
		result.setReturnType(initializeReturnType(method));
		String[][] signatureInfo = initializeSignature(method);
		result.setSignature(signatureInfo[0][0]);
		result.setParameterTypes(signatureInfo[1]);
		result.setParameterNames(signatureInfo[2]);
		result.setParameters(signatureInfo[1].length);
		return result;
	}

	public static BinaryMethod buildBinaryMethod(IDotNetElement parent, String name, int flags,
			String returnType, String signature, boolean isConstructor) {
		BinaryMethod result = new BinaryMethod(parent, flags, null);
		result.setName(name);
		result.setReturnType(returnType);
		result.setSignature(signature);
		result.setConstructor(isConstructor);
        String[] parameterTypes;
		String[] parameterNames;
		int parameters;

		
		if (signature.length() == 2) {
			parameters = 0;
			parameterTypes = new String[0];
			parameterNames = new String[0];
		} else {
			String[] split = signature.substring(1, signature.length() - 1)
					.split(",\\s*"); //$NON-NLS-1$
			parameters = split.length;
			parameterTypes = new String[parameters];
			parameterNames = new String[parameters];
			if (parameters != 0) {
				for (int i = 0; i < split.length; i++) {
					String[] parameter = split[i].split("\\s+"); //$NON-NLS-1$
					if (parameter.length>=2){
						parameterTypes[i] = parameter[0];
						parameterNames[i] = parameter[1];
					}
					else {
						parameterTypes[i] = split[i]; 
						parameterNames[i] = "paraName";
					}
				}
			}
		}
		result.setParameters(parameters);
		result.setParameterTypes(parameterTypes);
		result.setParameterNames(parameterNames);
		result.setSignature(signature);
		return result;
	}

	private static String initializeReturnType(Method method) {
		ReturnTypeSignature retSignature = method.getSignature()
				.getReturnType();
		TypeSignature typeSignature = retSignature.getType();
		return getReturnType(typeSignature);
	}


}
