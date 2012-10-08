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
 *      Harald Krapfenbauer - minor changes, code cleanup
 */


package org.emonic.debugger.launching;

public class EmonicDebugConstants {

	public static final String ID_EMONIC_DEBUG_MODEL = "org.emonic.debugger.EmonicDebugModel";
	public static final String ID_EMONIC_BREAKPOINT_MARKER =
		"org.emonic.debugger.lineBreakpointMarker";
	
	// these strings are copied from org.eclipse.debug.ui plug-in due to access
	// restrictions
	public static final String DEBUG_PLUGIN_CONSOLE_SYS_OUT_COLOR= "org.eclipse.debug.ui.outColor";
	public static final String DEBUG_PLUGIN_CONSOLE_SYS_ERR_COLOR= "org.eclipse.debug.ui.errorColor";
	public static final String DEBUG_PLUGIN_CONSOLE_SYS_IN_COLOR= "org.eclipse.debug.ui.inColor";
	public static final String DEBUG_PLUGIN_CONSOLE_BACKGROUND_COLOR= "org.eclipse.debug.ui.consoleBackground";
	
	// path for the debugger web service
	public static final String SERVICES_PATH = "/services";
	
	// preference page related
	public static final String PREF_STARTING_UP_TIMEOUT = "org.emonic.debugger.preferences.startingUpTimeout";
	public static final String PREF_EVENT_POLLING = "org.emonic.debugger.preferences.eventPolling";
	public static final String PREF_BACKTR_THR_TIMEOUT = "org.emonic.debugger.preferences.backtraceThreadsTimeout";
	public static final String PREF_USE_OWN_WEBSERVICE = "org.emonic.debugger.preferences.useOwnWebserviceLocation";
	public static final String PREF_WEBSERVICE_LOCATION = "org.emonic.debugger.preferences.webserviceLocation";
	public static final String PREF_STARTING_PORT = "org.emonic.debugger.preferences.startingPort";
}
