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
 * 		Bernhard Brem - initial implementation
 *      Harald Krapfenbauer - rewrote most of the code / copied from org.emonic.debug.core
 */


package org.emonic.debugger.launching;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.emonic.base.framework.IFrameworkInstall;
import org.emonic.debug.core.EmonicDebugCore;
import org.emonic.debug.core.IDotNetLaunchConfigurationConstants;
import org.emonic.debugger.DebuggerPlugin;
import org.emonic.debugger.frontend.EmonicDebugTarget;


public class DotNetLaunchDelegate extends LaunchConfigurationDelegate {


	// function copied from org.emonic.debug.internal.core
	// will later me migrated!
	// changes: removed remote stuff
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {

		monitor.beginTask(null, 6);
		monitor.subTask("Preparing launch");

		String frameworkId = configuration.getAttribute(
				IDotNetLaunchConfigurationConstants.ATTR_FRAMEWORK_ID, ""); //$NON-NLS-1$
		try {
			IFrameworkInstall[] installs = org.emonic.base.framework.FrameworkFactory.getFrameworkInstalls();
			if (installs.length == 0) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								EmonicDebugCore.PLUGIN_ID,
								0,
								"There are no frameworks configured in this workspace. "
										+ "Please configure your framework definitions in the preferences",
								null));
			}
			for (int i = 0; i < installs.length; i++) {
				if (installs[i].getId().equals(frameworkId)) {
					String projectName = configuration
							.getAttribute(
									IDotNetLaunchConfigurationConstants.ATTR_PROJECT_NAME,
									(String) null);
					String executableName = configuration
							.getAttribute(
									IDotNetLaunchConfigurationConstants.ATTR_EXECUTABLE_NAME,
									(String) null);
					String workDir = configuration
							.getAttribute(
									IDotNetLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
									(String)null);
					String programArgs = configuration
							.getAttribute(
									IDotNetLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
									""); //$NON-NLS-1$
					String runtimeArgs = configuration
							.getAttribute(
									IDotNetLaunchConfigurationConstants.ATTR_RUNTIME_ARGUMENTS,
									""); //$NON-NLS-1$
					// get complete environment we should set
					String[] envVars = DebugPlugin.getDefault()
							.getLaunchManager().getEnvironment(configuration);
					// get the full path of the executable
					IProject project = ResourcesPlugin.getWorkspace().getRoot()
							.getProject(projectName);
					IFile file = project.getFile(executableName);
					String executable = file.getRawLocation().toOSString();

					IRemoteDebug remoteLaunch = DebuggerPlugin.getDefault()
							.getRemoteLaunchProvider();

					monitor.worked(1);
					if (monitor.isCanceled())
						return;
					
					if (remoteLaunch != null && remoteLaunch.isLaunchRemote(configuration)) {
						monitor.worked(1);
						monitor.subTask("Remote launch");
//						File localMonoBinary = null;
//						if (IFrameworkInstall.MONO_FRAMEWORK.equals(installs[i].getType())) {
//							localMonoBinary = findMonoBinary(installs[i].getInstallLocation());
//						}
//						remoteLaunch.launchRemote(project, configuration, 
//								localMonoBinary.getName(), monitor);

						String remoteArgs =
							remoteLaunch.remoteDebugSetup(configuration, project, monitor);
						if (remoteArgs == null) {
						throw new CoreException(
								new Status(
										IStatus.ERROR,
										DebuggerPlugin.PLUGIN_ID,
										0,
										"Error starting remote debugging. See the error log for details.",
										null));
						}
						runtimeArgs += (" "+remoteArgs);
						launch(launch,installs[i], executable, programArgs, runtimeArgs,
								envVars, null, monitor);

					} else {
						monitor.worked(1);
						monitor.subTask("Local launch");
						launch(launch, installs[i], executable, programArgs, runtimeArgs,
								envVars, workDir, monitor);
					}
					return;
				}
			}

			throw new CoreException(
					new Status(
							IStatus.ERROR,
							EmonicDebugCore.PLUGIN_ID,
							0,
							"The framework definition associated with this launch configuration could not be found.",
							null));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					EmonicDebugCore.PLUGIN_ID, 0,
					"An IOException has occurred", e));
		}
	}
	
	private static final String[] MONO_BINARY_CANDIDATES = { "mono", //$NON-NLS-1$
		"mono.exe", "bin" + File.separatorChar + "mono", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"bin" + File.separatorChar + "mono.exe" }; //$NON-NLS-1$ //$NON-NLS-2$

	public static File findMonoBinary(File installLocation) {
		for (int i = 0; i < MONO_BINARY_CANDIDATES.length; i++) {
			File candidate = new File(installLocation,
					MONO_BINARY_CANDIDATES[i]);
			if (candidate.exists()) {
				return candidate;
			}
		}
		return null;
	}

	private void launch(ILaunch launch, IFrameworkInstall install,
			String executablePath, String programArgs, String runtimeArgs,
			String[] envVars, String workDir, IProgressMonitor monitor)
	throws DebugException, IOException {
		
		// start xsp2, the tiny web server
		monitor.worked(1);
		if (monitor.isCanceled())
			return;
		monitor.subTask("Checking for xsp2 web server");
		File xsp2Executable = findXsp2Binary(install.getInstallLocation());
		if (xsp2Executable == null)
			// file not found
			throw new DebugException(new Status(IStatus.ERROR,
					DebuggerPlugin.PLUGIN_ID,
					DebugPlugin.INTERNAL_ERROR,
					"Could not find xsp2!",
					null));
		// check if xsp2 work dir was overridden in the preferences
		boolean ownPath = DebuggerPlugin.getDefault().getPreferenceStore().getBoolean(
				EmonicDebugConstants.PREF_USE_OWN_WEBSERVICE);
		String serviceWorkDir = "";
		if (ownPath) {
			// check if work dir exists
			String pref = DebuggerPlugin.getDefault().getPreferenceStore().getString(
					EmonicDebugConstants.PREF_WEBSERVICE_LOCATION);
			if (new File(pref).isDirectory())
				serviceWorkDir = pref;
			else
				serviceWorkDir = "";
		}
		if (serviceWorkDir.equals("")) {
			URL a = FileLocator.find(
					DebuggerPlugin.getDefault().getBundle(),
					new Path(org.emonic.debugger.launching.EmonicDebugConstants.SERVICES_PATH),
					null);
			URL b = FileLocator.resolve(a);
			serviceWorkDir = b.getFile();
		}
		int port = DebuggerPlugin.getDefault().getFreePort();
		String command = xsp2Executable.getAbsolutePath()+" --nonstop --port "+String.valueOf(port);

		monitor.worked(1);
		if (monitor.isCanceled())
			return;
		monitor.subTask("Starting debugger web service");
		
		Process process = Runtime.getRuntime().exec(command, envVars,
				new File(serviceWorkDir));
		// determine visible name of xsp2 in debug view
		String name;
		if (runtimeArgs.indexOf("-remote-target:") == -1)
			name = "Web service (xsp2.exe)";
		else
			name = "Web service: Remote debugging (xsp2.exe)";
		IProcess ipr = DebugPlugin.newProcess(launch,process,name);
	
		EmonicDebugTarget target = new EmonicDebugTarget(launch, ipr, executablePath,
				programArgs, runtimeArgs, workDir, port);
		launch.addDebugTarget(target);
		
		monitor.done();
	}

	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		
		if (super.preLaunchCheck(configuration, mode, monitor)) {
			String projectName = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					(String) null);
			if (projectName == null || projectName.equals("")) {
				throw new CoreException(new Status(IStatus.ERROR,
						EmonicDebugCore.PLUGIN_ID, 0, "Project name is not valid", null));
			}

			String executableName = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_EXECUTABLE_NAME,
					(String) null);
			if (executableName == null || executableName.equals("")) {
				throw new CoreException(new Status(IStatus.ERROR,
						EmonicDebugCore.PLUGIN_ID, 0,
						"Executable is not valid", null));
			}

			String frameworkName = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_FRAMEWORK_ID,
					(String) null);
			if (frameworkName == null || frameworkName.equals("")) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								EmonicDebugCore.PLUGIN_ID,
								0,
								"This launch configuration does not have an associated framework definition.",
								null));
			}

			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			IFile file = project.getFile(executableName);
			if (!file.exists()) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								EmonicDebugCore.PLUGIN_ID,
								0,
								"Could not find the executable in the given project",
								null));
			}

			IRemoteDebug remoteLaunch = DebuggerPlugin.getDefault()
					.getRemoteLaunchProvider();
			if (remoteLaunch != null && !remoteLaunch.remotePreLaunchCheck(configuration, mode)) {
					throw new CoreException(
							new Status(
									IStatus.ERROR,
									EmonicDebugCore.PLUGIN_ID,
									0,
									"Settings for remote launching are not valid",
									null));
			}
			if (remoteLaunch == null || !remoteLaunch.isLaunchRemote(configuration)) {
				// check local working directory
				String workingDirectory = configuration.getAttribute(
						IDotNetLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
						(String)null);
				if (workingDirectory == null || workingDirectory.equals("")
						|| !new File(workingDirectory).isDirectory()) {
					throw new CoreException(
							new Status(
									IStatus.ERROR,
									EmonicDebugCore.PLUGIN_ID,
									0,
									"Working directory is not valid",
									null));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	
	private static final String[] XSP2_BINARY_CANDIDATES = { "xsp2",
		"bin" + File.separatorChar + "xsp2"};
	
	public static File findXsp2Binary(File installLocation) {
		for (int i = 0; i < XSP2_BINARY_CANDIDATES.length; i++) {
			File candidate = new File(installLocation,
					XSP2_BINARY_CANDIDATES[i]);
			if (candidate.exists()) {
				return candidate;
			}
		}
		return null;
	}
}
