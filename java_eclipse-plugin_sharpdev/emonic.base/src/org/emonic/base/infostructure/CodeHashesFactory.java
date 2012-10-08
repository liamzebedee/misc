/*********************************************************************** 
 * Copyright (c) 2005, 2007 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Nov 4, 2005
 * emonic org.emonic.base.infostructure CodeHashesFactory.java
 **********************************************************************/
package org.emonic.base.infostructure;

import java.util.Hashtable;

/**
 * @author bb
 *
 */
public class CodeHashesFactory {
	private static Hashtable hashes = new Hashtable();
	public static CodeInfoHash getCodeHash(String key){
		if (hashes.containsKey(key)) return (CodeInfoHash) hashes.get(key);
		CodeInfoHash h = new CodeInfoHash();
		hashes.put(key,h);
		return h;
	}
}
