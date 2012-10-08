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


package org.emonic.debugger.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.emonic.debugger.frontend.EmonicStackFrame;

public class EmonicSourceLookupParticipant extends
		AbstractSourceLookupParticipant {

	public String getSourceName(Object object) throws CoreException {
		if (object instanceof EmonicStackFrame) {
			return ((EmonicStackFrame)object).getSourceName();
		}
		return null;
	}

}
