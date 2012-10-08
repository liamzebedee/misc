/*******************************************************************************
 * Copyright (c) 2007, 2008, 2009, 2010 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     B. Brem - initial API and implementation
 ******************************************************************************/

package org.emonic.base.codehierarchy;

public class AssemblyHashElement {

	private  long modifyDate;
    private IAssembly assembly;
	
	public void setModifyDate(long modifyDate) {
		this.modifyDate = modifyDate;
	}

	public long getModifyDate() {
		return modifyDate;
	}

	public void setAssembly(IAssembly assembly) {
		this.assembly = assembly;
	}

	public IAssembly getAssembly() {
		return assembly;
	} 

}
