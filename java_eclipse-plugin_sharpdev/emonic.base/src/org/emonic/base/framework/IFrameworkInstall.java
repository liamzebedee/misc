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

import java.io.File;

/**
 * This interface is not intended to be implemented by clients.
 */
public interface IFrameworkInstall {
	
	public static final String MICROSOFT_DOT_NET_FRAMEWORK = "Microsoft .NET"; //$NON-NLS-1$

	public static final String MONO_FRAMEWORK = "Mono"; //$NON-NLS-1$

	public String getId();

	public String getName();

	public File getInstallLocation();

	public String getType();

	public String getRuntimeArguments();
 
	public String getDocumentationLocation();
	
	public String getGACLocation();
}
