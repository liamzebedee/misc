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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.debugger.frontend.EmonicLineBreakpoint;
import org.emonic.debugger.launching.EmonicDebugConstants;


public class EmonicLineBreakpointAdapter implements IToggleBreakpointsTarget {

	public boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return getEditor(part) != null;
	}

	public boolean canToggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		// not supported
		return false;
	}

	public boolean canToggleWatchpoints(IWorkbenchPart part,
			ISelection selection) {
		// not supported
		return false;
	}

	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		ITextEditor textEditor = getEditor(part);
		if (textEditor != null) {
			IResource resource = (IResource) textEditor.getEditorInput()
				.getAdapter(IResource.class);
			if (resource == null) {
				// only files that are in the workspace can have a breakpoint set
				return;
			}
			
			ITextSelection textSelection = (ITextSelection) selection;
			int lineNumber = textSelection.getStartLine();
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(EmonicDebugConstants.ID_EMONIC_DEBUG_MODEL);
			for (int i = 0; i < breakpoints.length; i++) {
				IBreakpoint breakpoint = breakpoints[i];
				if (resource.equals(breakpoint.getMarker().getResource())) {
					if (((ILineBreakpoint)breakpoint).getLineNumber() == (lineNumber + 1)) {
						// remove
						breakpoint.delete();
						return;
					}
				}
			}
			// create line breakpoint (doc line numbers start at 0)
			EmonicLineBreakpoint lineBreakpoint = new EmonicLineBreakpoint(resource, lineNumber + 1);
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
		}
	}

	public void toggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) throws CoreException {
		// not supported
	}

	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		// not supported
	}
	
	private ITextEditor getEditor(IWorkbenchPart part) {
		if (part instanceof CSharpEditor)
			return (ITextEditor)part;
		return null;		
	}
}
