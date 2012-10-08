/*******************************************************************************
 * Copyright (c) 2007 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.preferences;

import org.eclipse.osgi.util.NLS;

public class FrameworkMessages extends NLS {

	private static final String BUNDLE_NAME = "org.emonic.base.preferences.frameworkmessages"; //$NON-NLS-1$

	public static String FrameworksPreferencePage_Description;
	public static String FrameworksPreferencePage_InstalledFrameworksLabel;
	public static String FrameworksPreferencePage_NameColumn;
	public static String FrameworksPreferencePage_LocationColumn;
	public static String FrameworksPreferencePage_TypeColumn;
	public static String FrameworksPreferencePage_AddButton;
	public static String FrameworksPreferencePage_EditButton;
	public static String FrameworksPreferencePage_RemoveButton;
	public static String FrameworksPreferencePage_SelectDefaultFrameworkError;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, FrameworkMessages.class);
	}

}
