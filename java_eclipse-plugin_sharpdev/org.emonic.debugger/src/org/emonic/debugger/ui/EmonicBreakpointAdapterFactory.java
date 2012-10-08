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


package org.emonic.debugger.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.emonic.base.editors.CSharpEditor;

public class EmonicBreakpointAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof CSharpEditor)
			return new EmonicLineBreakpointAdapter();
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{IToggleBreakpointsTarget.class};
	}

}
