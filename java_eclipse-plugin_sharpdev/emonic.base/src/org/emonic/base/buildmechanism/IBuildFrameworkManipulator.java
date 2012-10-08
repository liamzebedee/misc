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
 * This interface encapsulates the ability of a build mechanism to handle different target frameworks
 * The decission whether You implement this interface or not for a build mechanism results in different
 * behavour on 2 different parts of emonic:
 * 1) If You implement this interface, You are able to set the framework via the surface (project creation wizard(, 
 * if not the wizard disabeles the choosing box
 * 2) If You implement this interface and the choosen framework is configured in emonic, all operations which
 * use or scan binaries of the framework (code completion and so on) use the framwork choosen by the build mechanism. So, for example,
 * if the user chooses the build mechabnism  mono-1.0, he gets the code completion for this framework. 
 * If You don't implement this interface, in a project where this build mechanism is choosen the default framework set in the emonic 
 * preferences are taken.  
 * Example for a mechanism implementing this feature: NAnt 
 * @author bb
 *
 */
public interface IBuildFrameworkManipulator {
	 
    /**
     * Set the framework for which the project should be build.
     * One buld file mechanism can support more than one, for example mono and microsoft. 
     * It is expected that  @link #getSupportedFrameworks()} delivers strings which code framework and release (for example mono-1.0)
     * One of these strings  has to be set.
     * @param targetFramework The framework to be taken. Has to be one of the strings delivered by {@link #getSupportedFrameworks()}.
     */
	void setTargetFramework(String targetFramework, String releaseNr);
    
	/**
	 * Get the target framework
	 * @return
	 */
    String getTargetFrameworkType();
    
    
    /**
     * Return the release of the target framework
     * @return
     */
    String getTargetFrameworkRelease();

    /**
	 * This function determines which frameworks are supported by the mechanism
	 * @return the supported 
	 */
	String[] getSupportedFrameworks();

	
	// This function returns the supported release nmber of the frameworks
	String[] getSupportedReleases(String string);
	
	
}
