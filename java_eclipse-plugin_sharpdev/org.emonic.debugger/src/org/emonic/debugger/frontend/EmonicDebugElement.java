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
 * 		Bernhard Brem - initial API and implementation
 *      Harald Krapfenbauer - code cleanups
 */


package org.emonic.debugger.frontend;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.emonic.debugger.DebuggerPlugin;
import org.emonic.debugger.launching.EmonicDebugConstants;

public abstract class EmonicDebugElement extends PlatformObject implements
		IDebugElement {

	// containing target
	protected EmonicDebugTarget target;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return EmonicDebugConstants.ID_EMONIC_DEBUG_MODEL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == IDebugElement.class) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	protected void abort(String message, Throwable e) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, DebuggerPlugin.PLUGIN_ID,
				DebugPlugin.INTERNAL_ERROR, message, e));
	}


	private void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	/**
	 * Fires a <code>CREATE</code> event for this element.
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}
	/**
	 * Fires a <code>Change</code> event for this element.
	 */
	protected void fireChangeEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}

	/**
	 * Fires a <code>RESUME</code> event for this element with the given
	 * detail.
	 * 
	 * @param detail
	 *            event detail code
	 */
	protected void fireResumeEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.RESUME, detail));
	}

	/**
	 * Fires a <code>SUSPEND</code> event for this element with the given
	 * detail.
	 * 
	 * @param detail
	 *            event detail code
	 */
	protected void fireSuspendEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND, detail));
	}

	/**
	 * Fires a <code>TERMINATE</code> event for this element.
	 */
	protected void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

}
