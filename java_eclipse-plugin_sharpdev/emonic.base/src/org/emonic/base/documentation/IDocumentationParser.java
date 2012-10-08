/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.emonic.base.documentation;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IDocumentationParser {

	public IDocumentation findNamespaceDocumentation(
			String documentationFolder, String assemblyName,
			String namespaceName, IProgressMonitor monitor);

	/**
	 * Retrieves the documentation for a given type, or <code>null</code> if
	 * no documentation could be found.
	 * 
	 * @param documentationFolder
	 *            the folder that should be used for searching the documentation
	 * @param assemblyPath
	 *            the path to the assembly that the documentation should be
	 *            retrieved for
	 * @param namespaceName
	 *            the namespace of the type, or the empty string if the type is
	 *            not defined within a namespace
	 * @param typeName
	 *            the name of the type
	 * @param monitor
	 *            a progress monitor for displaying the current status of the
	 *            documentation retrieval process
	 * @return the documentation defined for the specified type, or
	 *         <code>null</code> if it could not be found
	 */
	public ITypeDocumentation findTypeDocumentation(String documentationFolder,
			String assemblyPath, String namespaceName, String typeName,
			IProgressMonitor monitor);

}
