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

import org.eclipse.core.runtime.IPath;
import org.emonic.base.documentation.IDocumentation;


public abstract class SourceMember extends DotNetElement implements IMember {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1146432926938928718L;
	private int flags;
	private int curlCounter;
	
	private ISourceRange sourceRange;

	
	private String source;
	
	public SourceMember(IDotNetElement parent, int flags, IDocumentation documentation,
				ISourceRange sourceRange, String source) {
		super(parent, documentation);
		this.flags = flags;
		this.curlCounter = -1;
		this.sourceRange = sourceRange;
		this.source = source;
		
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
	
	/**
	 * Set the curlcounter
	 * @param curlcounter
	 */
	public void setCurlCounter(int curlcounter) {
		curlCounter = curlcounter;
	}
	
	/**
	 * Get the curlcounter
	 * @return
	 */
	public int getCurlCounter() {
		return curlCounter;
	}	
	
	/**
	 * Returns the source range for this member's name, or <code>null</code>
	 * if this member does not have an associated source (if it is a binary
	 * member, for example).
	 * 
	 * @return the source range of this member's name, or <code>null</code> if
	 *         this member does not have an associated source
	 */
	public ISourceRange getSourceRange() {
		return this.sourceRange;
	}
	
	public String getSource() {
		return this.source;
	}	
	
	public IPath getPath() {
		return parent.getPath();
	}
			
}
