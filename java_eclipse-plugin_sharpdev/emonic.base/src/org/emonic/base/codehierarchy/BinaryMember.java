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
package org.emonic.base.codehierarchy;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.emonic.base.documentation.IDocumentation;

public abstract class BinaryMember extends DotNetElement implements IMember {
	
	
	private static final long serialVersionUID = -8544239318509324336L;
	
	private int flags;
	private  String assemblyPath;

	
	
	public BinaryMember(IDotNetElement parent, int flags, IDocumentation documentation) {
		super(parent, documentation);
		this.flags = flags;
		if (parent != null) {
		   this.assemblyPath=parent.getPath().toPortableString();
		}
	}

	public IType getDeclaringType() {
		return (IType) getParent();
	}

	public int getFlags() {
		return flags;
	}
	
	public IDotNetElement[] getChildren() {
		return new IDotNetElement[0];
	}
	
	public boolean hasChildren() {
		return false;
	}

	public boolean isBinary() {
		return true;
	}

	public IPath getPath() {
		return Path.fromPortableString(assemblyPath);
	}

	
	public IFile getPathAsResource(){
		 IWorkspace workspace = ResourcesPlugin.getWorkspace();
		 // If the path is in the workspace and absolute, make it relative to the workspace
		 String orgPath = workspace.getRoot().getLocation().toOSString();
		 String file = getPath().toOSString();
		 String sfile = file;
		 // Grr: Win does not care about capitals!
		 // So since I don't know which system i have I have to allow generally both
		 if (file.toLowerCase().startsWith(orgPath.toLowerCase())) 
				sfile = file.substring(orgPath.length()); 
		 return workspace.getRoot().getFile(new Path(sfile));
	}
	
    public void setPath(IPath path){
       assemblyPath=path.toPortableString();	
    }
	
	
}
