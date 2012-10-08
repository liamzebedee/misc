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
 *                                     - Extension code in start()
 *******************************************************************************/
package org.emonic.debug.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class EmonicDebugCore implements BundleActivator {

	public static final String PLUGIN_ID = "org.emonic.debug.core"; //$NON-NLS-1$
	public static final String ID_EXTENSION_POINT_REMOTELAUNCH = PLUGIN_ID + ".remoteLaunching";
	private IRemoteLaunch remoteLaunchProvider;
	private static EmonicDebugCore instance;

	public EmonicDebugCore() {
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(ID_EXTENSION_POINT_REMOTELAUNCH);
		if (extensionPoint == null)
			return;
		IConfigurationElement[] configs = extensionPoint.getConfigurationElements();
		int length = configs.length;
		if (length == 0)
			return;
		remoteLaunchProvider =
			(IRemoteLaunch) configs[0].createExecutableExtension("class"); //$NON-NLS-1$
	}
	
	public IRemoteLaunch getRemoteLaunchProvider() {
		return remoteLaunchProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		instance = null;
	}

//	public IFrameworkInstall getDefaultFrameworkInstall() {
//		try {
//			Preferences preferences = new InstanceScope()
//					.getNode(EmonicDebugCore.PLUGIN_ID);
//
//			String framework = preferences.get(
//					IDotNetDebugConstants.DEFAULT_FRAMEWORK_INSTALL, null);
//
//			if (framework != null) {
//				String[] frameworkIds = preferences.childrenNames();
//				if (frameworkIds.length != 0) {
//					for (int i = 0; i < frameworkIds.length; i++) {
//						if (framework.equals(frameworkIds[i])) {
//							FrameworkInstall install = new FrameworkInstall();
//							install.setId(framework);
//							Preferences node = preferences.node(framework);
//							install
//									.setName(node
//											.get(
//													IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME,
//													"")); //$NON-NLS-1$
//							install
//									.setType(node
//											.get(
//													IDotNetDebugConstants.FRAMEWORK_INSTALL_TYPE,
//													"")); //$NON-NLS-1$
//							install
//									.setInstallLocation(new File(
//											node
//													.get(
//															IDotNetDebugConstants.FRAMEWORK_INSTALL_LOCATION,
//															""))); //$NON-NLS-1$
//							install
//									.setRuntimeArguments(node
//											.get(
//													IDotNetDebugConstants.FRAMEWORK_INSTALL_RUNTIME_ARGUMENTS,
//													"")); //$NON-NLS-1$
//							return install;
//						}
//					}
//				}
//			}
//		} catch (BackingStoreException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public IFrameworkInstall[] getFrameworkInstalls() {
//		try {
//			IEclipsePreferences debugPreferences = new InstanceScope()
//					.getNode(PLUGIN_ID);
//			String[] keys = debugPreferences.childrenNames();
//			int length = keys.length;
//			FrameworkInstall[] installs = new FrameworkInstall[length];
//			for (int i = 0; i < length; i++) {
//				installs[i] = new FrameworkInstall();
//				installs[i].setId(keys[i]);
//				Preferences node = debugPreferences.node(keys[i]);
//				installs[i].setName(node.get(
//						IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME, "")); //$NON-NLS-1$
//				installs[i].setType(node.get(
//						IDotNetDebugConstants.FRAMEWORK_INSTALL_TYPE, "")); //$NON-NLS-1$
//				installs[i].setInstallLocation(new File(node.get(
//						IDotNetDebugConstants.FRAMEWORK_INSTALL_LOCATION, ""))); //$NON-NLS-1$
//				installs[i]
//						.setRuntimeArguments(node
//								.get(
//										IDotNetDebugConstants.FRAMEWORK_INSTALL_RUNTIME_ARGUMENTS,
//										"")); //$NON-NLS-1$
//			}
//			return installs;
//		} catch (BackingStoreException e) {
//			return new IFrameworkInstall[0];
//		}
//	}

	public static EmonicDebugCore getInstance() {
		return instance;
	}

}
