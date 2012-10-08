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
package org.emonic.debug.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EmonicDebugUI extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.emonic.debug.ui"; //$NON-NLS-1$
	public static final String ID_EXTENSION_POINT_REMOTETAB = PLUGIN_ID +
			".remoteLaunchingTab";

	// The shared instance
	private static EmonicDebugUI plugin;
	
	private AbstractLaunchConfigurationTab remoteLaunchTab;
	
	/**
	 * The constructor
	 */
	public EmonicDebugUI() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(ID_EXTENSION_POINT_REMOTETAB);
		if (extensionPoint == null)
			return;
		IConfigurationElement[] configs = extensionPoint.getConfigurationElements();
		int length = configs.length;
		if (length == 0)
			return;
		remoteLaunchTab =
			(AbstractLaunchConfigurationTab)
					configs[0].createExecutableExtension("class"); //$NON-NLS-1$

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EmonicDebugUI getDefault() {
		return plugin;
	}
	
	public AbstractLaunchConfigurationTab getRemoteTabProvider() {
		return remoteLaunchTab;
	}

}
