/***************************************************************************
 *  Copyright (c) 2006, 2007 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Apr 27, 2006
 * emonic.base org.emonic.base.infostructure SourceElementComparer.java
 **************************************************************************/
package org.emonic.base.codehierarchy;

import org.eclipse.jface.viewers.IElementComparer;

/**
 * @author bb
 *
 */
public class CodeElementComparer implements IElementComparer {

	/**
	 * 
	 */
	public CodeElementComparer() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object a, Object b) {
	    if (hashCode(a) ==  hashCode(b)) return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
	 */
	public int hashCode(Object element) {
		if (element instanceof CodeElement) {
			CodeElement c = (CodeElement) element;

			int res = c.getCodeType();
			if (c.getElementName() != null)
				res += c.getElementName().hashCode();
			// We must not compare the position and tha length; they can
			// vary!
			// res += c.getOffset();
			// res += c.getLength() * 1234;
			if (c.getAccessType() != null)
				res += c.getAccessType().hashCode();
			if (c.getSignature() != null)
				res += c.getSignature().hashCode();
			return res;
		}
			
		return element.hashCode();	
	}

}
