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
package org.emonic.base.preferences;
import org.eclipse.osgi.util.NLS;

public class AddFrameworkDialogMessages extends NLS {

	private static final String BUNDLE_NAME = "org.emonic.base.preferences.addframeworkdialogmessages"; //$NON-NLS-1$

	public static String AddFrameworkDialog_Title;
	public static String AddFrameworkDialog_FrameworkTypeLabel;
	public static String AddFrameworkDialog_FrameworkNameLabel;
	public static String AddFrameworkDialog_FrameworkHomeDirectoryLabel;
	public static String AddFrameworkDialog_FrameworkHomeDirectoryBrowseButton;
	public static String AddFrameworkDialog_FrameworkDocumentationDirectoryLabel;
	public static String AddFrameworkDialog_FrameworkDocumentationDirectoryBrowseButton;
	public static String AddFrameworkDialog_FrameworkDefaultRuntimeArgumentsLabel;
	public static String AddFrameworkDialog_NoMonoBinaryError;
	public static String AddFrameworkDialog_NoLocationError;
	public static String AddFrameworkDialog_FrameworkLocationDoesNotExistError;
	public static String AddFrameworkDialog_FrameworkLocationIsFileError;
	public static String AddFrameworkDialog_FrameworkLocationCannotBeReadError;
	public static String AddFrameworkDialog_DocumentationLocationDoesNotExistError;
	public static String AddFrameworkDialog_DocumentationLocationIsFileError;
	public static String AddFrameworkDialog_DocumentationLocationCannotBeReadError;
	public static String AddFrameworkDialog_NoNameError;
	public static String AddFrameworkDialog_NameConflictError;
	public static String AddFrameworkDialog_FrameworkGACDirectoryLabel;
	public static String AddFrameworkDialog_GACLocationDoesNotExistError;
	public static String AddFrameworkDialog_GACLocationIsFileError;
	public static String AddFrameworkDialog_GACLocationCannotBeReadError;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, AddFrameworkDialogMessages.class);
	}

}
