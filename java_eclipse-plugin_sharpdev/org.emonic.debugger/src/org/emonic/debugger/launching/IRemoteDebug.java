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

package org.emonic.debugger.launching;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;


public interface IRemoteDebug {

	/**
	 * Determines if we should launch remote.
	 * @param configuration The ILaunchConfiguration the information is read out.
	 * @return true if we should launch remote
	 * @throws CoreException
	 */
	public boolean isLaunchRemote(ILaunchConfiguration configuration)
	throws CoreException;

	/**
	 * Sets up everything for remote debugging, i.e. environment, starts mdbserver.
	 * @param configuration 
	 * @param project
	 * @param monitor
	 * @return A string containing additional arguments for the debugger (for remote
	 * debugging)
	 */
	public String remoteDebugSetup(ILaunchConfiguration configuration, IProject project,
			IProgressMonitor monitor);

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
