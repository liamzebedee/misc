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

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.emonic.debugger.frontend.EmonicDebugTarget;
import org.emonic.debugger.frontend.EmonicLineBreakpoint;
import org.emonic.debugger.frontend.EmonicStackFrame;
import org.emonic.debugger.frontend.EmonicThread;

public class EmonicModelPresentation extends LabelProvider implements
		IDebugModelPresentation {



	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		}
		listener.detailComputed(value, detail);
	}

	public void setAttribute(String attribute, Object value) {
	}

	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint) {
			return org.emonic.base.editors.CSharpEditor.EMONIC_CSHARP_EDITOR_ID;
		}
		return null;
	}

	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker()
					.getResource());
		}
		return null;
	}
	
	public String getText(Object element) {
		if (element == null)
			return "";
		if (element instanceof EmonicDebugTarget)
			return ((EmonicDebugTarget)element).getName();
		if (element instanceof EmonicThread)
			return ((EmonicThread)element).getName();
		if (element instanceof EmonicStackFrame)
			return ((EmonicStackFrame)element).getName();
		if (element instanceof EmonicLineBreakpoint)
			return ((EmonicLineBreakpoint)element).getName();
		// fall back
		return element.toString();
	}
	
}
