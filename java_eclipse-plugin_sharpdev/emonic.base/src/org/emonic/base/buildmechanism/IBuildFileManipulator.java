/****************************************************************************
 * Copyright (c) 2001, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors: B.Brem 
 * **************************************************************************/

package org.emonic.base.buildmechanism;

import org.eclipse.core.resources.IFile;

/**
 * This interface has to be created if the build mechanism stores the
 * information of a build in a build file. 
 * @author bb
 *
 */
public interface IBuildFileManipulator {
	
	final static String BUILDFILENAMEPROJECT="PROJECT";
	
	/**
	 * @return the actual build file
	 */
	IFile getBuildFile();
	
	/**
	 * Set the build file
	 * @param file
	 */
	void setBuildFile(IFile file);
	
	/**
	 * Save the file. 
	 */
	void save();
	
	/**
	 * Suggest a name for a new build file. A part of the name can be the constant  BUILDFILENAMEPROJECT,
	 * the wizards for creating a new buildfile will replace it with the project name.
	 * @return
	 */
	String suggestFileName(); 
}
