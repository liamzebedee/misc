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


package org.emonic.debugger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.emonic.debug.core.IRemoteLaunch;
import org.emonic.debugger.launching.EmonicDebugConstants;
import org.emonic.debugger.launching.IRemoteDebug;
import org.emonic.debugger.ui.DefaultPrefsSetter;
import org.osgi.framework.BundleContext;

public class DebuggerPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.emonic.debugger"; //$NON-NLS-1$
	public static final String ID_EXTENSION_POINT_REMOTELAUNCH = PLUGIN_ID + ".remoteLaunching";
	private IRemoteDebug remoteLaunchProvider;
	
	//The shared instance.
	private static DebuggerPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	//Record listening ports of xsp2 (if several instances)
	private HashSet usedPorts = new HashSet();
	
	/**
	 * The constructor.
	 */
	public DebuggerPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.emonic.debugger.DebuggerPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// initialize default settings of "Debug" preference page
		DefaultPrefsSetter.initializeDefaults();
		
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
		.getExtensionPoint(ID_EXTENSION_POINT_REMOTELAUNCH);
		if (extensionPoint == null)
			return;
		IConfigurationElement[] configs = extensionPoint.getConfigurationElements();
		int length = configs.length;
		if (length == 0)
			return;
		remoteLaunchProvider =
			(IRemoteDebug) configs[0].createExecutableExtension("class"); //$NON-NLS-1$
	}
	
	public IRemoteDebug getRemoteLaunchProvider() {
		return remoteLaunchProvider;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static DebuggerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DebuggerPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow w = getActiveWorkbenchWindow();
		if (w != null) {
			return w.getActivePage();
		}
		return null;
	}
	
	
	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}	
	

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	 /* Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * @return
	 */
	public static IWorkspace getWorkspace() {
			return ResourcesPlugin.getWorkspace();
	}	
	
	/**
	 * Returns a free port for the debug web service starting with the port configured
	 * in the preferences.
	 * After xsp2 has quit, the port should be returned with returnPort(int port).
	 * @return 
	 */
	public int getFreePort() {
		int port = DebuggerPlugin.getDefault().getPreferenceStore().getInt(
				EmonicDebugConstants.PREF_STARTING_PORT);
		while (usedPorts.contains(new Integer(port)))
			port++;
		usedPorts.add(new Integer(port));
		return port;
	}
	
	/**
	 * Sets a port free after is was used by the debug web service.
	 * @param port The port number
	 */
	public void returnPort(int port) {
		Iterator iter = usedPorts.iterator();
		while (iter.hasNext()) {
			Integer current = (Integer)iter.next();
			if (current.intValue() == port) {
				usedPorts.remove(current);
				return;
			}
		}
	}
}
