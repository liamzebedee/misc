/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.nunit.internal.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.UIJob;
import org.emonic.base.framework.IFrameworkInstall;
import org.emonic.debug.internal.core.DotNetLaunchDelegate;
import org.emonic.nunit.internal.ui.views.NUnitTestRunnerView;

public class NUnitLaunchConfiguration extends DotNetLaunchDelegate {

	private String executablePath;

	protected String constructCommand(IFrameworkInstall install,
			String executableName, String executablePath, String programArgs,
			String runtimeArgs) throws IOException {
		this.executablePath = executablePath;
		StringBuffer command = new StringBuffer();
		synchronized (command) {
			if (IFrameworkInstall.MONO_FRAMEWORK.equals(install.getType())) {
				File file = findMonoBinary(install.getInstallLocation());
				if (file != null) {
					executableName = file.getAbsolutePath();
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

				URL url = FileLocator.find(Activator.getDefault().getBundle(),
						new Path("bridge/NUnitBridge.exe"), null);
				url = FileLocator.toFileURL(url);
				command.append(url.getPath());
				command.append(' ');
			}
			return command.toString();
		}
	}

	protected IProcess createProcess(String commandLine, String[] envVars,
			File workingDirectory, ILaunch launch, String executableName)
			throws IOException {
		final ServerSocket serverSocket = new ServerSocket();
		serverSocket.bind(null);

		clearJob.schedule();

		final IProcess process = super.createProcess(commandLine
				+ serverSocket.getLocalPort() + ' ' + executablePath, envVars,
				workingDirectory, launch, executableName);

		Job job = new Job("Processing NUnit tests") {
			public IStatus run(IProgressMonitor monitor) {
				try {
					Socket socket = serverSocket.accept();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					final char[] buffer = new char[8192];
					while (true) {
						final int read = reader.read(buffer);
						if (read == -1) {
							break;
						}
						new InterpretNUnitJob(new String(buffer, 0, read))
								.schedule();
					}
					return Status.OK_STATUS;
				} catch (IOException e) {
					try {
						process.terminate();
					} catch (DebugException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
							"An error has occurred", e);
				} finally {
					try {
						serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					monitor.done();
				}
			};
		};
		job.schedule();

		return process;
	}

	private static ClearJob clearJob = new ClearJob();

	static class ClearJob extends UIJob {
		public ClearJob() {
			super("Refresh NUnit view");
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			NUnitTestRunnerView view = (NUnitTestRunnerView) page
					.findView(NUnitTestRunnerView.VIEW_ID);
			if (view == null) {
				try {
					view = (NUnitTestRunnerView) page.showView(
							NUnitTestRunnerView.VIEW_ID, null,
							IWorkbenchPage.VIEW_VISIBLE);
				} catch (PartInitException e) {
					return e.getStatus();
				}
			}
			view.clear();
			IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view
					.getSite().getAdapter(IWorkbenchSiteProgressService.class);
			service.warnOfContentChange();
			return Status.OK_STATUS;
		}
	}

	class InterpretNUnitJob extends UIJob {

		private String text;

		public InterpretNUnitJob(String text) {
			super("NUnit update job");
			this.text = text;
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			NUnitTestRunnerView view = (NUnitTestRunnerView) page
					.findView(NUnitTestRunnerView.VIEW_ID);
			if (view == null) {
				try {
					view = (NUnitTestRunnerView) page.showView(
							NUnitTestRunnerView.VIEW_ID, null,
							IWorkbenchPage.VIEW_VISIBLE);
				} catch (PartInitException e) {
					return e.getStatus();
				}
			}
			view.interpret(text);
			IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view
					.getSite().getAdapter(IWorkbenchSiteProgressService.class);
			if (service != null) {
				service.warnOfContentChange();
			}
			return Status.OK_STATUS;
		}
	}
}
