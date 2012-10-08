/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Harald Krapfenbauer - initial implementation
 */

package org.emonic.debug.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;


public interface IRemoteLaunch {

	/**
	 * Determines if we should launch remote.
	 * @param configuration The ILaunchConfiguration the information is read out.
	 * @return true if we should launch remote
	 * @throws CoreException
	 */
	public boolean isLaunchRemote(ILaunchConfiguration configuration)
	throws CoreException;
	
	/**
	 * Launch remotely.
	 * @param project The .NET project the launch is associated with.
	 * @param configuration The launch configuration contains informations from the
	 * @param monoExecutableName The name of the Mono executable or null for .NET
	 * framework. This is either "mono" or "mono.exe"
	 * @param monitor An IProgressMonitor instance
	 * Remote Launching Launch Configuration Tab.
	 */
	public void launchRemote(IProject project, ILaunchConfiguration configuration,
			String monoExecutableName, IProgressMonitor monitor);
	
	/**
	 * Check the configuration for remote launching before actual launching is started.
	 * @param configuration The launch configuration
	 * @param mode The launch mode, "run" or "debug"
	 * @return true if configuration for remote launching is valid
	 * @throws CoreException
	 */
	public boolean remotePreLaunchCheck(ILaunchConfiguration configuration, String mode)
	throws CoreException;
	
}
