/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.emonic.base.mbel2assemblyparser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.emonic.base.codehierarchy.BinaryEvent;
import org.emonic.base.codehierarchy.BinaryField;
import org.emonic.base.codehierarchy.BinaryMethod;
import org.emonic.base.codehierarchy.BinaryType;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IEvent;
import org.emonic.base.codehierarchy.IField;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.codehierarchy.IProperty;
import org.emonic.base.documentation.IDocumentation;

import edu.arizona.cs.mbel2.mbel.Event;
import edu.arizona.cs.mbel2.mbel.Field;
import edu.arizona.cs.mbel2.mbel.InterfaceImplementation;
import edu.arizona.cs.mbel2.mbel.Method;
import edu.arizona.cs.mbel2.mbel.MethodSemantics;
import edu.arizona.cs.mbel2.mbel.Property;
import edu.arizona.cs.mbel2.mbel.TypeDef;
import edu.arizona.cs.mbel2.mbel.TypeRef;

public class BinaryTypeBuilder  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private static final IType[] NO_ENCLOSING_TYPES = new IType[0];

//	private IEvent[] events;
//
//	private IField[] fields;
//
//	private IMethod[] methods;
//
//	private IProperty[] properties;
//	
//	private IType[] types = NO_ENCLOSING_TYPES;
//
//	private IType declaringType;
//
//
//	private String superclassName;
//
//	private String[] superInterfaceNames;
//
//	private long flags;
//
//	private boolean isEnum;

	
	


	public static BinaryType buildBinaryType(IDotNetElement parent, TypeDef typeDef,
			IDocumentation documentation, IPath path) {
		BinaryType result = new BinaryType(parent, (int) typeDef.getFlags(), documentation);
		result.setName(typeDef.getName());
		String tfullName = typeDef.getFullName();
		if (tfullName.charAt(0) == '.') {
			tfullName = typeDef.getName();
		}
		result.setFullName(tfullName);
		
		result.setFlags(typeDef.getFlags());
		result.setEnum( typeDef.isEnum());

		TypeRef superclass = typeDef.getSuperClass();
		if (superclass != null) {
			result.setSuperclassName( superclass.getNamespace() + '.'
					+ superclass.getName());
		}
        
		InterfaceImplementation[] implementations = typeDef
				.getInterfaceImplementations();
		String[] superInterfaceNames = new String[implementations.length];
		for (int i = 0; i < implementations.length; i++) {
			TypeRef ref = implementations[i].getInterface();
			superInterfaceNames[i] = ref.getNamespace() + '.' + ref.getName();
		}
		result.setSuperInterfaceNames(superInterfaceNames);

		Field[] fields = typeDef.getFields();
		List typeFields = new ArrayList(fields.length);
		for (int i = 0; i < fields.length; i++) {
			//typeFields.add(new BinaryField(this, fields[i],
			//		getMemberDocumentation(fields[i].getName())));
			typeFields.add(new BinaryField(result, 
					fields[i].getName(), 
					BinaryMethodBuilder.getParameterType( fields[i].getSignature().getType()), 
					fields[i].getFlags(), 
					result.getMemberDocumentation(fields[i].getName())));
		
		}

		Method[] methods = typeDef.getMethods();
		List typeMethods = new ArrayList(methods.length);
		List typeProperties = new ArrayList(methods.length);
		for (int i = 0; i < methods.length; i++) {
			MethodSemantics semantics = methods[i].getMethodSemantics();
			if (semantics == null) {
				BinaryMethod method = BinaryMethodBuilder.buildBinaryMethod(result, methods[i]);
					//new BinaryMethod(result, methods[i]);
				typeMethods.add(method);

				String name = method.getElementName();
				StringBuffer buffer = new StringBuffer();
				synchronized (buffer) {
					if (name.equals(".ctor")) {
						buffer.append(tfullName);
					} else {
						buffer.append(name);
					}

					String[] parameters = method.getParameterTypes();
					buffer.append('(');
					if (parameters.length != 0) {
						for (int j = 0; j < parameters.length; j++) {
							buffer.append(parameters[j]).append(',');
						}
						buffer.deleteCharAt(buffer.length() - 1);
					}
					buffer.append(')');
					method.setDocumentation(result.getMemberDocumentation(buffer
							.toString()));
				}
			} else {
				Property property = semantics.getProperty();
				if (property != null) {
					String name = property.getName();
					typeProperties.add(BinaryPropertyBuilder.buildBinaryProoperty(result, methods[i],
							name,result.getMemberDocumentation(name)));
							//new BinaryProperty(result, methods[i].getFlags(), methods[i].getName(), methods[i].getSignature().getReturnType().toString(), methods[i].getSignature().getParameters().toString()));
				}
			}
		}

		Event[] events = typeDef.getEvents();
		List typeEvents = new ArrayList(events.length);
		for (int i = 0; i < events.length; i++) {
			//BinaryEvent event = new BinaryEvent(this, events[i]);
			BinaryEvent event = new BinaryEvent(result, events[i].getName(), events[i].getEventFlags(),"", null); 
			typeEvents.add(event);

			StringBuffer buffer = new StringBuffer(event.getElementName());
			synchronized (buffer) {
				String[] parameters = event.getParameterTypes();
				buffer.append('(');
				if (parameters != null && parameters.length != 0) {
					for (int j = 0; j < parameters.length; j++) {
						buffer.append(parameters[j]).append(',');
					}
					buffer.deleteCharAt(buffer.length() - 1);
				}
				buffer.append(')');
				event
						.setDocumentation(result.getMemberDocumentation(buffer
								.toString()));
			}
		}
        result.setEvents((IEvent[]) typeEvents.toArray(new IEvent[typeEvents
                                                  				.size()]));
		result.setFields((IField[]) typeFields.toArray(new IField[typeFields
				.size()]));
		result.setMethods( (IMethod[]) typeMethods.toArray(new IMethod[typeMethods
				.size()]));
		result.setProperties((IProperty[]) typeProperties
				.toArray(new IProperty[typeProperties.size()]));
		//assemblyPath=definitionPath;
		result.setPath(path);
		return result;
		
	}



	
	
}
