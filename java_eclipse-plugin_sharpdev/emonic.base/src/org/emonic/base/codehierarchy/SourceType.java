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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.emonic.base.documentation.IDocumentation;

public class SourceType extends SourceMember implements IType, ISourceReference{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4626393551703093626L;

	private static final IType[] NO_ENCLOSING_TYPES = new IType[0];

	private IEvent[] events;

	private IField[] fields;

	private IMethod[] methods;

	private IProperty[] properties;
	
	private IType[] types = NO_ENCLOSING_TYPES;

	private int type;

	private String name;

	private String fullName;

	private String superclassName;

	private String[] superInterfaceNames;

	
	public SourceType(IDotNetElement parent, int flags, IDocumentation documentation, 
			IEvent[] events, IField[] fields,IMethod[] methods, IProperty[] properties, IType[] types, int type,
			String name, String fullName, String superclassName, String[] superInterfaceNames,
			ISourceRange sourceRange, String source) {
		super(parent, flags, documentation, sourceRange,source);
		this.events = events;
		this.fields = fields;
		this.methods = methods;
		this.properties = properties;
		this.types = types;
		this.type = type;
		this.name = name;
		this.fullName = fullName;
		this.superclassName = superclassName;
		this.superInterfaceNames = superInterfaceNames;

	}

	//FIXME
	//additional constructor needed which allows setting of
	//events, fields, methods, properties, INNER TYPES!
	//and documentation(?is doc possible?)
	
	public IEvent[] getEvents() {
		return this.events;
	}

	public IField[] getFields() {
		return this.fields;
	}

	public String getFullName() {
		return this.fullName;
	}

	public IMethod[] getMethods() {
		return this.methods;
	}

	public IProperty[] getProperties() {
		return this.properties;
	}

	public String[] getSuperInterfaceNames() {
		return this.superInterfaceNames;
	}

	public String getSuperclassName() {
		return this.superclassName;
	}

	public IType[] getTypes() {
		return this.types;
	}

	public boolean isBinary() {
		return false;
	}

	public String getElementName() {
		return this.name;
	}

	public int getElementType() {
		return this.type;
	}

	public IDotNetElement[] getChildren() {
		Collection children = new ArrayList();
		children.addAll(Arrays.asList(events));
		children.addAll(Arrays.asList(fields));
		children.addAll(Arrays.asList(methods));
		children.addAll(Arrays.asList(properties));
		//FIXME what about LocalVars?
		return (IDotNetElement[]) children.toArray(new IDotNetElement[children
				.size()]);
	}

	public boolean hasChildren() {
		return events.length != 0 || fields.length != 0 || methods.length != 0
				|| properties.length != 0;
	}

	public boolean isClass() {
		if(type == CLASS)
			return true;
		return false;
	}

	public boolean isEnum() {
		if(type == ENUM)
			return true;
		return false;
	}

	public boolean isInterface() {
		if(type == INTERFACE)
			return true;
		return false;
	}

	public boolean isStruct() {
		if(type == STRUCT)
			return true;
		return false;
	}


}
