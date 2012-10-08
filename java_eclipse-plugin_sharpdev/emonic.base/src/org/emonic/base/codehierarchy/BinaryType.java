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
package org.emonic.base.codehierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Point;
import org.emonic.base.documentation.IDocumentation;
import org.emonic.base.documentation.ITypeDocumentation;

public class BinaryType extends BinaryMember implements IType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final IType[] NO_ENCLOSING_TYPES = new IType[0];

	private IEvent[] events;

	private IField[] fields;

	private IMethod[] methods;

	private IProperty[] properties;
	
	private IType[] types = NO_ENCLOSING_TYPES;

	private IType declaringType;

	private String name;

	private String fullName;

//	public long getFlags() {
//		return flags;
//	}

	public void setFlags(long l) {
		this.flags = l;
	}

	public String getName() {
		return name;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setSuperclassName(String superclassName) {
		this.superclassName = superclassName;
	}

	public void setSuperInterfaceNames(String[] superInterfaceNames) {
		this.superInterfaceNames = superInterfaceNames;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	private String superclassName;

	private String[] superInterfaceNames;

	private long flags;

	private boolean isEnum;

	
	

	public BinaryType(ICompilationUnit parent, int flags, String name,
			String fullName, String superclassName,
			String[] superInterfaceNames, boolean isEnum, IPath assemblyPath) {
		super(parent, flags, null);
		this.name = name;
		this.fullName = fullName;
		this.superclassName = superclassName;
		this.superInterfaceNames = superInterfaceNames;
		this.isEnum = isEnum;
		setPath(assemblyPath);
		
		
		events = new IEvent[0];
		fields = new IField[0];
		methods = new IMethod[0];
		properties = new IProperty[0];
	}
	
	public BinaryType(IDotNetElement parent, int flags2,
			IDocumentation documentation) {
			super(parent,flags2,documentation);
	}

//	public BinaryType(IDotNetElement parent, TypeDef typeDef,
//			IDocumentation documentation, IPath path) {
//		super(parent, (int) typeDef.getFlags(), documentation);
//		name = typeDef.getName();
//		fullName = typeDef.getFullName();
//		if (fullName.charAt(0) == '.') {
//			fullName = name;
//		}
//		flags = typeDef.getFlags();
//		isEnum = typeDef.isEnum();
//
//		TypeRef superclass = typeDef.getSuperClass();
//		if (superclass != null) {
//			superclassName = superclass.getNamespace() + '.'
//					+ superclass.getName();
//		}
//        
//		InterfaceImplementation[] implementations = typeDef
//				.getInterfaceImplementations();
//		superInterfaceNames = new String[implementations.length];
//		for (int i = 0; i < implementations.length; i++) {
//			TypeRef ref = implementations[i].getInterface();
//			superInterfaceNames[i] = ref.getNamespace() + '.' + ref.getName();
//		}
//
//		Field[] fields = typeDef.getFields();
//		List typeFields = new ArrayList(fields.length);
//		for (int i = 0; i < fields.length; i++) {
//			//typeFields.add(new BinaryField(this, fields[i],
//			//		getMemberDocumentation(fields[i].getName())));
//			typeFields.add(new BinaryField(this, 
//					fields[i].getName(), 
//					fields[i].getSignature().toString(), 
//					fields[i].getFlags(), 
//					getMemberDocumentation(fields[i].getName())));
//		
//		}
//
//	
//
//		Method[] methods = typeDef.getMethods();
//		List typeMethods = new ArrayList(methods.length);
//		List typeProperties = new ArrayList(methods.length);
//		for (int i = 0; i < methods.length; i++) {
//			MethodSemantics semantics = methods[i].getMethodSemantics();
//			if (semantics == null) {
//				BinaryMethod method = 
//					new BinaryMethod(this, methods[i]);
//				typeMethods.add(method);
//
//				String name = method.getElementName();
//				StringBuffer buffer = new StringBuffer();
//				synchronized (buffer) {
//					if (name.equals(".ctor")) {
//						buffer.append(fullName);
//					} else {
//						buffer.append(name);
//					}
//
//					String[] parameters = method.getParameterTypes();
//					buffer.append('(');
//					if (parameters.length != 0) {
//						for (int j = 0; j < parameters.length; j++) {
//							buffer.append(parameters[j]).append(',');
//						}
//						buffer.deleteCharAt(buffer.length() - 1);
//					}
//					buffer.append(')');
//					method.setDocumentation(getMemberDocumentation(buffer
//							.toString()));
//				}
//			} else {
//				Property property = semantics.getProperty();
//				if (property != null) {
//					String name = property.getName();
//					typeProperties.add(//new BinaryProperty(this, methods[i],
//							//name, getMemberDocumentation(name)));
//							new BinaryProperty(this, methods[i].getFlags(), methods[i].getName(), methods[i].getSignature().getReturnType().toString(), methods[i].getSignature().getParameters().toString()));
//				}
//			}
//		}
//
//		Event[] events = typeDef.getEvents();
//		List typeEvents = new ArrayList(events.length);
//		for (int i = 0; i < events.length; i++) {
//			//BinaryEvent event = new BinaryEvent(this, events[i]);
//			BinaryEvent event = new BinaryEvent(this, events[i].getName(), events[i].getEventFlags(),"", null); 
//			typeEvents.add(event);
//
//			StringBuffer buffer = new StringBuffer(event.getElementName());
//			synchronized (buffer) {
//				String[] parameters = event.getParameterTypes();
//				buffer.append('(');
//				if (parameters != null && parameters.length != 0) {
//					for (int j = 0; j < parameters.length; j++) {
//						buffer.append(parameters[j]).append(',');
//					}
//					buffer.deleteCharAt(buffer.length() - 1);
//				}
//				buffer.append(')');
//				event
//						.setDocumentation(getMemberDocumentation(buffer
//								.toString()));
//			}
//		}
//
//		this.events = (IEvent[]) typeEvents.toArray(new IEvent[typeEvents
//				.size()]);
//		this.fields = (IField[]) typeFields.toArray(new IField[typeFields
//				.size()]);
//		this.methods = (IMethod[]) typeMethods.toArray(new IMethod[typeMethods
//				.size()]);
//		this.properties = (IProperty[]) typeProperties
//				.toArray(new IProperty[typeProperties.size()]);
//		//assemblyPath=definitionPath;
//		setPath(path);
//	}

	public IDocumentation getMemberDocumentation(String name) {
		ITypeDocumentation documentedType = (ITypeDocumentation) getDocumentation();
		if (documentedType != null) {
			List memberDocumentation = documentedType.getDocumentation();
			for (int j = 0; j < memberDocumentation.size(); j++) {
				IDocumentation doc = (IDocumentation) memberDocumentation
						.get(j);
				if (doc.getName().equals(name)) {
					return doc;
				}
			}
		}
		return null;
	}

	public void setEvents(IEvent[] events) {
		this.events = events;
	}

	public void setFields(IField[] fields) {
		this.fields = fields;
	}

	public void setMethods(IMethod[] methods) {
		this.methods = methods;
	}

	public void setProperties(IProperty[] properties) {
		this.properties = properties;
	}

	
	public IEvent[] getEvents() {
		return (IEvent[]) events.clone();
	}

	public IField[] getFields() {
		return (IField[]) fields.clone();
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return TYPE;
	}

	public String getFullName() {
		return fullName;
	}

	public IMethod[] getMethods() {
		return (IMethod[]) methods.clone();
	}

	public IProperty[] getProperties() {
		return (IProperty[]) properties.clone();
	}

	public String getSuperclassName() {
		return superclassName;
	}

	public String[] getSuperInterfaceNames() {
		return superInterfaceNames;
	}
	
	public IType[] getTypes() {
		return types;
	}
	
	public void setTypes(IType[] types) {
		this.types = types;
	}

	public boolean isClass() {
		return Flags.isTypeStruct(flags) ? false : Flags.isTypeClass(flags);
	}

	public boolean isEnum() {
		return isEnum;
	}

	public boolean isInterface() {
		return Flags.isTypeInterface(flags);
	}

	public boolean isStruct() {
		return Flags.isTypeStruct(flags);
	}

	public boolean isBinary() {
		return true;
	}

	public IType getDeclaringType() {
		return declaringType;
	}
	
	public void setDeclaringType(IType declaringType) {
		this.declaringType = declaringType;
	}

	public IDotNetElement[] getChildren() {
		Collection children = new ArrayList();
		children.addAll(Arrays.asList(events));
		children.addAll(Arrays.asList(fields));
		children.addAll(Arrays.asList(methods));
		children.addAll(Arrays.asList(properties));
		if (types != NO_ENCLOSING_TYPES) {
			children.addAll(Arrays.asList(types));
		}
		return (IDotNetElement[]) children.toArray(new IDotNetElement[children
				.size()]);
	}

	public boolean hasChildren() {
		return events.length != 0 || fields.length != 0 || methods.length != 0
				|| properties.length != 0 || types.length != 0;
	}

	public String getSource() {
		return getPath().toPortableString();
	}


	
	public ISourceRange getSourceRange() {
		// TODO Auto-generated method stub
		return new SourceRange(new Point(0,0),new Point(0,0), 0, 0, 0, 0, 0 );
	}
//
//	public INamespace getNamespace(String namespaceName) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public INamespace[] getNamespaces() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public IType getType(String typeName) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public void setName(String name2) {
		name=name2;
		
	}

	


	
	
}
