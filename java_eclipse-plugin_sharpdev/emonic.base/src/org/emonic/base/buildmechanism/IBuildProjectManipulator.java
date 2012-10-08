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
 * Some build mechanisms use the name of the project. So this attribute is saved in the "project"-node
 * of the ant- and nant- bulildmechanism. Immplement this interface if You want emonic to add the
 * name of the project in Your build file.
 * 
 * @author bb
 *
 */
public interface IBuildProjectManipulator {
	/**
	 * Set the name of the project
	 * @param name
	 */
	void setProject(String name);

	String getProject();
	
	
}
