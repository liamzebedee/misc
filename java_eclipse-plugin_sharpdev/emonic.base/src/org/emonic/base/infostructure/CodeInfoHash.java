/**************************************************************************
* Copyright (c) 2005, 2007 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Nov 4, 2005
 * emonic org.emonic.base.infostructure CodeInfoHash.java
 **************************************************************************/
package org.emonic.base.infostructure;

import org.emonic.base.codehierarchy.IDotNetElement;

/**
 * @author bb
 *
 */
public class CodeInfoHash {
	
	private long timestamp = 0;
	
	private IDotNetElement element;
	
	public boolean hasToBeUpdated(long time){
		return time > timestamp;
	}
	
	public IDotNetElement returnHash() {
		return element;
	}
	
	public void setHash(IDotNetElement element, long timestamp) {
		this.element = element;
		this.timestamp = timestamp;
	}
	
}
