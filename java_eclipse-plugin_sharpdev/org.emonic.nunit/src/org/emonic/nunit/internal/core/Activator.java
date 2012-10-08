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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.emonic.nunit"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	public static final String IMG_TEST_ERROR = "IMG_TEST_ERROR";

	public static final String IMG_TEST_FAIL = "IMG_TEST_FAIL";

	public static final String IMG_TEST_OK = "IMG_TEST_OK";

	public static final String IMG_TEST_RUN = "IMG_TEST_RUN";

	public Activator() {
		plugin = this;
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
		registry.put(IMG_TEST_ERROR, imageDescriptorFromPlugin(PLUGIN_ID,
				"icons/full/obj16/testerr.gif"));
		registry.put(IMG_TEST_FAIL, imageDescriptorFromPlugin(PLUGIN_ID,
				"icons/full/obj16/testfail.gif"));
		registry.put(IMG_TEST_OK, imageDescriptorFromPlugin(PLUGIN_ID,
				"icons/full/obj16/testok.gif"));
		registry.put(IMG_TEST_RUN, imageDescriptorFromPlugin(PLUGIN_ID,
				"icons/full/obj16/testrun.gif"));
	}

	/*
	 * (non-Javadoc)
	 * 
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
	public static Activator getDefault() {
		return plugin;
	}

	public static Image getImage(String key) {
		return plugin.getImageRegistry().get(key);
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return plugin.getImageRegistry().getDescriptor(key);
	}
}
