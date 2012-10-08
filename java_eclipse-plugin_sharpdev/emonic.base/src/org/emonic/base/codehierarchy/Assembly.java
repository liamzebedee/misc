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
import java.util.List;
import java.util.Map;

//import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

//import edu.arizona.cs.mbel2.mbel.AssemblyInfo;

public final class Assembly extends DotNetElement implements IAssembly {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7013373485391106796L;

	private Map namespaces;

	private Map types;

	private String name;

	private String path;

	private int majorVersion;

	private int minorVersion;

	private int buildNumber;

	private int revisionNumber;

	public Assembly(Map namespaces, Map types, String name, IPath path) {
		super(null, null);
		this.namespaces = namespaces;
		this.types = types;
		this.name = name;
		this.path = path.toPortableString();
	}

	public Assembly(String name,int majorVersion,int minorVersion,int  buildNumber,int revisionNumber, Map namespaces, Map types, IPath  path) {
		this(namespaces, types, name, path);

		this.majorVersion =majorVersion ;
		this.minorVersion = minorVersion;
		this.buildNumber = buildNumber;
		this.revisionNumber=revisionNumber;
	}

	public String getElementName() {
		return name;
	}

	public int getElementType() {
		return ASSEMBLY;
	}

	public INamespace getNamespace(String namespaceName) {
		return (INamespace) namespaces.get(namespaceName);
	}

	public INamespace[] getNamespaces() {
		return (INamespace[]) namespaces.values().toArray(
				new INamespace[namespaces.size()]);
	}

	public IType getType(String typeName) {
		return (IType) types.get(typeName);
	}

	public IType[] getTypes() {
		return (IType[]) types.values().toArray(new IType[types.size()]);
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public int getRevisionNumber() {
		return revisionNumber;
	}

	public IDotNetElement[] getChildren() {
		List list = new ArrayList(namespaces.values());
		list.addAll(types.values());
		return (IDotNetElement[]) list.toArray(new IDotNetElement[list.size()]);
	}

	public boolean hasChildren() {
		return !namespaces.isEmpty() || !types.isEmpty();
	}

	public IPath getPath() {
		return Path.fromPortableString(path);
	}
}
