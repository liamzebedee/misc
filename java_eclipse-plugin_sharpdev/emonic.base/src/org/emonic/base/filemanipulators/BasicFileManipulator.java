/***************************************************************************
 * Copyright (c) 2001, 2007 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on May 22, 2005
 * emonic org.emonic.base.FileManipulators BasicFileManipulator.java
 *************************************************************************/
public package org.emonic.base.filemanipulators;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author bb
 *
 */
public abstract class BasicFileManipulator {
	protected IFile file;
	protected IProgressMonitor monitor;
	
	public BasicFileManipulator() {
		super();
		file=null;
		monitor=null;
	}
	
	public BasicFileManipulator(IFile fn) {
		super();
		file = fn;
		monitor = null;
		if (file.exists()){
			parseDocument();
		}
	}
	
	
	public BasicFileManipulator(IFile fn, IProgressMonitor moni) {
		super();
		file = fn;
		monitor = moni;
	}
	
	public abstract void parseDocument();
	
	
}
