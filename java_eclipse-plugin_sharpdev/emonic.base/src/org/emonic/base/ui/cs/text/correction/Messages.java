/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.emonic.base.ui.cs.text.correction;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.emonic.base.ui.cs.text.correction.messages"; //$NON-NLS-1$
	
	public static String CastCompletionProposal_DisplayString;
	public static String InsertUsingStatementProposal_DisplayString;
	public static String InsertUsingStatementProposal_ReplacementString;
	public static String InterfaceAddMethodProposal_DisplayString;
	public static String RemoveUnusedVariableProposal_DisplayString;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
