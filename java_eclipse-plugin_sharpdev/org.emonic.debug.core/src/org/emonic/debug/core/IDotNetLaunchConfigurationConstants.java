/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.debug.core;

public interface IDotNetLaunchConfigurationConstants {

	public static final String ID_DOT_NET_APPLICATION = EmonicDebugCore.PLUGIN_ID
			+ ".dotNetApplication"; //$NON-NLS-1$

	public static final String ATTR_PROJECT_NAME = EmonicDebugCore.PLUGIN_ID
			+ ".PROJECT"; //$NON-NLS-1$

	public static final String ATTR_EXECUTABLE_NAME = EmonicDebugCore.PLUGIN_ID
			+ ".EXECUTABLE_NAME"; //$NON-NLS-1$

	public static final String ATTR_WORKING_DIRECTORY = EmonicDebugCore.PLUGIN_ID
			+ ".WORKING_DIRECTORY"; //$NON-NLS-1$

	public static final String ATTR_FRAMEWORK_ID = EmonicDebugCore.PLUGIN_ID
			+ ".FRAMEWORK_NAME"; //$NON-NLS-1$

	public static final String ATTR_PROGRAM_ARGUMENTS = EmonicDebugCore.PLUGIN_ID
			+ ".PROGRAM_ARGUMENTS"; //$NON-NLS-1$

	public static final String ATTR_RUNTIME_ARGUMENTS = EmonicDebugCore.PLUGIN_ID
			+ ".RUNTIME_ARGUMENTS"; //$NON-NLS-1$
}
