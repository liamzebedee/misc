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
package org.emonic.nunit.internal.ui.launching;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.emonic.nunit.internal.ui.launching.messages"; //$NON-NLS-1$

	public static String ArgumentsTab_TabName;
	public static String ArgumentsTab_RuntimeArgumentsGroupLabel;

	public static String MainTab_TabName;
	public static String MainTab_RunApplicationMessage;
	public static String MainTab_ProjectGroupLabel;
	public static String MainTab_ProjectNotSpecifiedError;
	public static String MainTab_ProjectDoesNotExistError;
	public static String MainTab_ProjectIsClosedError;
	public static String MainTab_BrowseProjectButtonLabel;
	public static String MainTab_BrowseProjectDialogTitle;
	public static String MainTab_BrowseProjectDialogMessage;
	public static String MainTab_ExecutableGroupLabel;
	public static String MainTab_ExecutableNotSpecifiedError;
	public static String MainTab_ExecutableDoesNotExistError;
	public static String MainTab_BrowseExecutableButtonLabel;
	public static String MainTab_BrowseExecutableDialogTitle;
	public static String MainTab_BrowseExecutableDialogMessage;
	public static String MainTab_FrameworkGroupLabel;
	public static String MainTab_TargetFrameworkComboLabel;
	public static String MainTab_InstalledFrameworksButtonLabel;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
