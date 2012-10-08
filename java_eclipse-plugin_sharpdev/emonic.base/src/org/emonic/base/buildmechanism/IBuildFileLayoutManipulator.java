/****************************************************************************
 * Copyright (c) 2001, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors: B.Brem 
 * **************************************************************************/

package org.emonic.base.buildmechanism;

/**
 * This interface represents the capability of the build mechanism to store the layout
 * of the project in different directories - especially in a bin and source directory.
 * For example the ant- and nant- buildmechanisms are capable to store the location
 * of the src and bin dir in variables and use them.
 * 
 * @author bb
 *
 */
public interface IBuildFileLayoutManipulator {
	
	/**
	 * Set the src dir
	 * @param dir
	 */
	void setSrcDir(String dir);
	
	/**
	 * Get the src dir
	 * @return the source directory
	 */
	String getSrcDir();
	
	/**
     * Set the bin dir
     * @param dir
     */
    void setBinDir(String dir);
    
    /**
     * Get the bin dir
     * @return
     */
    
    public String getBinDir();
	
}
