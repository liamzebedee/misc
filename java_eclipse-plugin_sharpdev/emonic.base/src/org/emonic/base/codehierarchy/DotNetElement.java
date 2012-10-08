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

import org.eclipse.core.runtime.PlatformObject;
import org.emonic.base.documentation.IDocumentation;

public abstract class DotNetElement extends PlatformObject implements IDotNetElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected IDotNetElement parent;

	protected IDocumentation documentation;
	
	public DotNetElement(IDotNetElement parent, IDocumentation documentation) {
		this.parent = parent;
		this.documentation = documentation;
	}
	
	public IDotNetElement getAncestor(int ancestorType) {
		IDotNetElement ancestor = parent;
		while (ancestor != null) {
			if (ancestor.getElementType() == ancestorType) {
				return ancestor;
			}
			ancestor = ancestor.getParent();
		}
		return null;
	}
	
	public IDocumentation getDocumentation() {
		return documentation;
	}

	public IDotNetElement getParent() {
		return parent;
	}

}
