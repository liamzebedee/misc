/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.framework;

import org.emonic.base.EMonoPlugin;

public interface IDotNetDebugConstants {

	public static final String DEFAULT_FRAMEWORK_INSTALL = EMonoPlugin.PLUGIN_ID
			+ ".defaultFrameworkInstall"; //$NON-NLS-1$

	public static final String FRAMEWORK_INSTALL_NAME = EMonoPlugin.PLUGIN_ID
			+ ".frameworkInstallName"; //$NON-NLS-1$

	public static final String FRAMEWORK_INSTALL_TYPE = EMonoPlugin.PLUGIN_ID
			+ ".frameworkInstallType"; //$NON-NLS-1$

	public static final String FRAMEWORK_INSTALL_LOCATION = EMonoPlugin.PLUGIN_ID
			+ ".frameworkInstallLocation"; //$NON-NLS-1$

	public static final String FRAMEWORK_INSTALL_RUNTIME_ARGUMENTS = EMonoPlugin.PLUGIN_ID
			+ ".frameworkInstallRuntimeArguments"; //$NON-NLS-1$

	public static final String FRAMEWORK_DOCUMENTATION_LOCATION = EMonoPlugin.PLUGIN_ID
	+ ".frameworkDocumentationLocation"; //$NON-NLS-1$
	
	public static final String FRAMEWORK_GAC_LOCATION = EMonoPlugin.PLUGIN_ID
	+ ".frameworkGACLocation"; //$NON-NLS-1$

}
