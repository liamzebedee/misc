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


package org.emonic.debugger.frontend;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;
import org.emonic.debugger.launching.EmonicDebugConstants;

public class EmonicLineBreakpoint extends LineBreakpoint {

	private int mdbID = -1;
	private String name;

	/**
	 * Default constructor is required for the breakpoint manager
	 * to re-create persisted breakpoints. After instantiating a breakpoint,
	 * the <code>setMarker(...)</code> method is called to restore
	 * this breakpoint's attributes.
	 */
	public EmonicLineBreakpoint()  {
	}
	
	public EmonicLineBreakpoint(final IResource resource, final int lineNumber)
	throws DebugException {
		name = resource.getName() + " [line: " + lineNumber + "]";
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker =
					resource.createMarker(EmonicDebugConstants.ID_EMONIC_BREAKPOINT_MARKER);
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, true);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]");
			}
		};
		run(getMarkerRule(resource), runnable);
	}
	
	public String getModelIdentifier() {
		return EmonicDebugConstants.ID_EMONIC_DEBUG_MODEL;
	}
	
	/**
	 * Sets the corresponding ID in MDB of this breakpoint
	 * @param id
	 */
	public void setMdbId(int id) {
		mdbID = id;
	}
	
	/**
	 * Get the corresponding ID in MDB for this breakpoint
	 * @return
	 */
	public int getMdbId() {
		return mdbID;
	}
	
	/**
	 * Gets a description of this breakpoint
	 * @return
	 */
	public String getName() {
		return name; 
	}
}
