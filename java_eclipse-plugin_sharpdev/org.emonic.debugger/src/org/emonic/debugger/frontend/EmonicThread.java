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
 * 		Bernhard Brem - initial implementation
 *      Harald Krapfenbauer - added updateStackFrames(), some API changes
 */


package org.emonic.debugger.frontend;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.emonic.debugger.launching.EmonicDebugConstants;
import org.emonic.debugger.webinterface.BacktraceData;


public class EmonicThread extends EmonicDebugElement implements IThread {

	// status of thread
	private boolean suspended = false;
	private boolean stepping = false;
	private boolean explicitSuspend = false;
	// dynamic data of thread
	private IBreakpoint breakpointHit;
	private IStackFrame[] stackFrames;
	private int signal = 0;
	private boolean exception;
	// static data of thread
	private int procID = -1;
	private int threadID = -1;
	private boolean isDaemon = false;
	private int PID = -1;
	private int lastUpdated = 0;
    
	public EmonicThread(EmonicDebugTarget target, int processNumber,
			String processCmdLine, int threadNumber, boolean daemonThread, int pid) {
    	this.target = target;
		this.procID = processNumber;
		this.threadID = threadNumber;
		this.isDaemon = daemonThread;
		this.PID = pid;
		// processCmdLine intentionally ignored
	}

	/** 
	 * Sets state of this thread. 
	 * @param suspended True for suspended, false for running.
	 */
	public void setSuspendedFlag(boolean suspended) {
		this.suspended = suspended;
	}
	
	/**
	 * Lets this thread know that its MDB pendant has suspended. 
	 * @param signal The signal which caused suspension. A value of zero means "no signal".
	 * @param breakpoint The ID of the breakpoint that has caused suspension. A value
	 * of zero means "no breakpoint".
	 * @param exception True if an exception has occured.
	 */
	public void suspended(int signal, int breakpoint, boolean exception) {
		// Note: The following suspend event details do not initiate unfolding
		// of the suspended thread in the Debug view:
		// - UNSPECIFIED: Eclipse 3.3.1.1, Eclipse 3.2.2
		// - STEP_END: Eclipse 3.2.2
		// so we pretend BREAKPOINT every time, because this works
		suspended = true;
		this.signal = signal;
		this.exception = exception;
		if (explicitSuspend) {
			explicitSuspend = false;
			fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
			target.fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
		} else if (breakpoint != 0) {
			EmonicLineBreakpoint bpt = findBreakpoint(breakpoint);
			if (bpt == null)
				System.err.println("error: breakpoints out of sync!!!");
			breakpointHit = bpt;
			fireSuspendEvent(DebugEvent.BREAKPOINT);
			target.fireSuspendEvent(DebugEvent.BREAKPOINT);
		} else {
			// this case is for stepping end, exception occured, signal occured
			stepping = false;
			// This works for Eclipse 3.3.1.1, but not for 3.2.2 
//			fireSuspendEvent(DebugEvent.STEP_END);
			fireSuspendEvent(DebugEvent.BREAKPOINT);
			target.fireSuspendEvent(DebugEvent.STEP_END);
		}
	}
	
	private synchronized void updateStackFrames() throws DebugException {
		// check if update is necessary
		if (stackFrames != null && lastUpdated == target.getSuspendCount())
			return;
		
		// get backtrace data
		Synchronizer frontend = target.getFrontend();
		BacktraceData[] backtraceData = frontend.Backtrace(this.getID());
		EmonicStackFrame[] tempStackFrames = new EmonicStackFrame[backtraceData.length];
		// compare old and new frames bottom-up
		// reuse old frame objects if they did not change
		// drop out as soon as a frame differs
		int i=0;
		if (stackFrames != null) {
			int compareCount = Math.min(backtraceData.length, stackFrames.length);
			for (i=0; i<compareCount; i++) {
				BacktraceData bd = backtraceData[backtraceData.length-i-1];
				// TODO: BUG HERE IF YOU JUMP BETWEEN STACKFRAMES
				EmonicStackFrame sf = (EmonicStackFrame)stackFrames[stackFrames.length-i-1];
				// compare to EmonicStackFrame object
				if (sf.equalsBacktraceData(bd)) {
					tempStackFrames[tempStackFrames.length-i-1] = sf;
					sf.updateLineNumber(bd.getLineNumber());
					sf.updateFrameNumber(bd.getFrameNumber());
				}
				else
					break;
			}
		}
		// create new stack frame objects
		for (int j=0; j<backtraceData.length-i; j++) {
			BacktraceData bd = backtraceData[j];
			int frameNumber = bd.getFrameNumber(); // always exists
			String method = bd.getMethod();
			if (method == null || method.equals(""))
				method = "<unknown>";
			String file = bd.getFile();
			if (file == null || file.equals(""))
				file = "<unknown>";
			int lineNumber = bd.getLineNumber();
			EmonicStackFrame sf = new EmonicStackFrame(this,frameNumber,method,file,lineNumber);
			tempStackFrames[j] = sf;
		}
		stackFrames = tempStackFrames;
		lastUpdated = target.getSuspendCount();
	}

	private EmonicLineBreakpoint findBreakpoint(int mdbID) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
			.getBreakpointManager().getBreakpoints(
					EmonicDebugConstants.ID_EMONIC_DEBUG_MODEL);
		for (int i=0; i<breakpoints.length; i++) {
			if (((EmonicLineBreakpoint)breakpoints[i]).getMdbId() == mdbID)
				return (EmonicLineBreakpoint)breakpoints[i];
		}
		return null;
	}
	
	public IBreakpoint[] getBreakpoints() {
		if (breakpointHit == null)
			return new IBreakpoint[0];
		IBreakpoint[] bpts = new IBreakpoint[1];
		bpts[0] = breakpointHit;
		return bpts;
	}

	public String getName() {
		String name = "";
		if (isDaemon)
			name += "Daemon thread";
		else
			name += "Thread";
		name += " @" + threadID + " [PID " + PID + ", Process #" + procID + "]";
		if (isSuspended()) {
			name += " (Suspended";
			if (signal != 0)
				name += " (Received signal " + signal + ")";
			else if (breakpointHit != null)
				name += " (Hit breakpoint)";
			else if (exception)
				name += " (Caught exception)";
			name += ")";
		} else {
			name += " (Running)";
		}
		return name;
	}

	
	public int getPriority() {
		// we don't deal with priorities
		return -1;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		if (!suspended)
			return new IStackFrame[0];
		
		updateStackFrames();
		return stackFrames;
	}
	
	public IStackFrame getTopStackFrame() throws DebugException {
		if (!suspended)
			return null;
		
		updateStackFrames();
		if (stackFrames.length > 0)
			return stackFrames[0];
		return null;
	}

	public boolean hasStackFrames() throws DebugException {
		if (!suspended)
			return false;
		
		updateStackFrames();
		if (stackFrames.length > 0)
			return true;
		return false;
	}
	
	public boolean canResume() {
		return suspended;
	}

	public boolean canSuspend() {
		return !suspended;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void resume() throws DebugException {
		Synchronizer frontend = target.getFrontend();
		// continue thread in background
		frontend.Background(threadID);
		
		// must set suspended here - we don't get any resume event from MDB
		suspended = false;
		stepping = false;
		breakpointHit = null;
		exception = false;
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
		target.possiblyResumed();
	}

	public void suspend() throws DebugException {
		Synchronizer frontend = target.getFrontend();
		frontend.Stop(threadID);
		explicitSuspend = true;
	}

	public boolean canStepInto() {
		return suspended;
	}

	public boolean canStepOver() {
		return suspended;
	}

	public boolean canStepReturn() {
		// Note: If we are somewhere deep in the Mono C code, MDB is sometimes not
		// able to find the bounds of the current method, so we disable this if
		// the method name of the top stack frame is "<unknown>".
		try {
			if (((EmonicStackFrame)getTopStackFrame()).isMethodNameKnown())
				return suspended;
			return false;
		} catch (NullPointerException e) {
			return false;
		} catch (DebugException e) {
			return false;
		}
	}

	public boolean isStepping() {
		return stepping;
	}

	public void stepInto() throws DebugException {
		Synchronizer frontend = target.getFrontend();
		frontend.Step(threadID);
		stepping = true;
		breakpointHit = null;
		suspended = false;
		fireResumeEvent(DebugEvent.STEP_INTO);
		target.possiblyResumed();
	}

	public void stepOver() throws DebugException {
		Synchronizer frontend = target.getFrontend();
		frontend.Next(threadID);
		stepping = true;
		breakpointHit = null;
		suspended = false;
		fireResumeEvent(DebugEvent.STEP_OVER);
		target.possiblyResumed();
	}

	public void stepReturn() throws DebugException {
		Synchronizer frontend = target.getFrontend();
		frontend.Finish(threadID);
		stepping = true;
		breakpointHit = null;
		suspended = false;
		fireResumeEvent(DebugEvent.STEP_RETURN);
		target.possiblyResumed();
	}

	public boolean canTerminate() {
		// killing individual threads not supported
		return target.canTerminate();
	}

	public boolean isTerminated() {
		return target.isTerminated();
	}

	public void terminate() throws DebugException {
		// we don't support per-thread termination
	    target.terminate();
	}
	
	/**
	 * Get the corresponding thread ID in MDB
	 * @return
	 */
	public int getID() {
		return threadID;
	}
}
