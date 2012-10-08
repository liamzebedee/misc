/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *     Harald Krapfenbauer, TU Vienna <krapfenbauer@ict.tuwien.ac.at>
 *                                     - Remote launching via extension
 *******************************************************************************/
package org.emonic.debug.internal.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.emonic.base.framework.IFrameworkInstall;
import org.emonic.debug.core.EmonicDebugCore;
import org.emonic.debug.core.IDotNetLaunchConfigurationConstants;
import org.emonic.debug.core.IRemoteLaunch;

public class DotNetLaunchDelegate extends LaunchConfigurationDelegate {

	private static final Map PROCESS_ATTRIBUTES = new HashMap();

	private static final String[] MONO_BINARY_CANDIDATES = { "mono", //$NON-NLS-1$
			"mono.exe", "bin" + File.separatorChar + "mono", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"bin" + File.separatorChar + "mono.exe" }; //$NON-NLS-1$ //$NON-NLS-2$

	static {
		PROCESS_ATTRIBUTES.put(IProcess.ATTR_PROCESS_TYPE, "exe"); //$NON-NLS-1$
	}

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

	protected String constructCommand(IFrameworkInstall install,
			String executablePath, String programArgs,
			String runtimeArgs) throws IOException {
		StringBuffer command = new StringBuffer();
		synchronized (command) {
			if (IFrameworkInstall.MONO_FRAMEWORK.equals(install.getType())) {
				File file = findMonoBinary(install.getInstallLocation());
				if (file != null) {
					String executableName = file.getAbsolutePath();
					command.append(executableName);
					command.append(' ');
					command.append(install.getRuntimeArguments());
					command.append(' ');
					// explicitly append the debugging support argument for Mono
					// runtimes
					command.append("--debug"); //$NON-NLS-1$
					command.append(' ');
					command.append(runtimeArgs);
					command.append(' ');
				}
			}
			// append any arguments that the user had specified in the launch
			// configuration
			command.append(executablePath);
			command.append(' ');
			command.append(programArgs);
			return command.toString();
		}
	}

	protected IProcess createProcess(String commandLine, String[] envVars,
			File workingDirectory, ILaunch launch, String executableName)
			throws IOException {
		// run the executable
		Process process = Runtime.getRuntime().exec(commandLine, envVars,
				workingDirectory);
		PROCESS_ATTRIBUTES.put(IProcess.ATTR_CMDLINE, commandLine);
		return DebugPlugin.newProcess(launch, process, executableName,
				PROCESS_ATTRIBUTES);
	}

	private void launch(ILaunch launch, IFrameworkInstall install,
			String executablePath, String programArgs, String runtimeArgs,
			String[] envVars, String workingDirectory) throws IOException {
		
		String commandLine = constructCommand(install, executablePath, programArgs,
				runtimeArgs);
		createProcess(commandLine, envVars, new File(workingDirectory),
				launch, executablePath);
		// open console view
		PlatformUI.getWorkbench().getDisplay().syncExec(
				new Runnable() {
					public void run() {
						IConsoleManager consMan = ConsolePlugin.getDefault().getConsoleManager();
						IConsole[] allCons = consMan.getConsoles();
						if (allCons != null && allCons.length > 0)
							consMan.showConsoleView(allCons[allCons.length-1]);
					}
				});
	}

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
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

					IRemoteLaunch remoteLaunch = EmonicDebugCore.getInstance()
							.getRemoteLaunchProvider();

					if (monitor.isCanceled())
						return;
					
					if (remoteLaunch != null && remoteLaunch.isLaunchRemote(configuration)) {
						monitor.subTask("Remote launch");
						File localMonoBinary = null;
						if (IFrameworkInstall.MONO_FRAMEWORK.equals(installs[i].getType())) {
							localMonoBinary = findMonoBinary(installs[i].getInstallLocation());
						}
						remoteLaunch.launchRemote(project, configuration, 
								localMonoBinary.getName(), monitor);
					} else {
						monitor.subTask("Local launch");
						launch(launch, installs[i], executable, programArgs, runtimeArgs,
								envVars, workDir);
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

			IRemoteLaunch remoteLaunch = EmonicDebugCore.getInstance()
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
}
