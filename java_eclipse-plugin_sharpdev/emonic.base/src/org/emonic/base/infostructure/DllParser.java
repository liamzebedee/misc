/*******************************************************************************
 * Copyright (c) 2007, 2008,2010 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: B. Brem and others
 * ******************************************************************************/

package org.emonic.base.infostructure;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.emonic.base.codecompletion.AssemblyParserFactory;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IAssembly;
import org.emonic.base.codehierarchy.IDotNetElement;

/**
 * @author bb
 *
 */
public class DllParser implements IAbstractParser {

	private File file;

	/* (non-Javadoc)
	 * @see org.emonic.base.infostructure.IAbstractParser#getRoot()
	 */
	public IDotNetElement getRoot() {
		Map[] maps = null;
		maps = new Map[]{Collections.EMPTY_MAP, Collections.EMPTY_MAP};
		String filePath = file.getAbsolutePath();
		CodeElement root=null;
		try {
			root = new CodeElement(null, IDotNetElement.ROOT, "Root", 0, 0);
			root.setSource(filePath);
			
			IAssembly asse = AssemblyParserFactory.createAssemblyParser().parseAssembly(
					new Path(filePath),
					maps);
			if (asse != null){
			   root.addChild(asse);
			}
			
			
		}  catch (Exception e) {
		}
		return root;
	}

	/* (non-Javadoc)
	 * @see org.emonic.base.infostructure.IAbstractParser#init(java.io.File)
	 */
	public void init(File fl) {
		this.file=fl;
	}

	
}
