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
 *      Harald Krapfenbauer - added updateVariables() and updated other functions
 */


package org.emonic.debugger.frontend;

import java.util.Arrays;

import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.emonic.debugger.webinterface.BacktraceData;

public class EmonicStackFrame extends EmonicDebugElement implements IStackFrame {

	private EmonicThread thread;
	private int lineNumber = -1, oldLineNumber = -1;
	private String fileName;
	private int frameNumber = -1;
	private IVariable[] variables;
	private String methodName;
	private int lastUpdated = 0;
	
	public EmonicStackFrame(EmonicThread thread, int number, String method,
			String file, int lineNumber) {
		target = (EmonicDebugTarget)thread.getDebugTarget();
		frameNumber = number;
		this.thread = thread;
		methodName = method;
		if (file.equals("<unknown>"))
			fileName = file;
		else
			fileName = (new Path(file)).lastSegment();
		this.lineNumber = lineNumber;
	}

	/**
	 * Gets the corresponding frame ID in MDB for this stack frame
	 * @return
	 */
	public int getFrameID() {
		return frameNumber;
	}
	
	/**
	 * Returns if the method name of this stack frame is known.
	 * @return
	 */
	public boolean isMethodNameKnown() {
		if (methodName == null || methodName.equals("<unknown>"))
			return false;
		return true;
	}
	
	private synchronized void updateVariables() throws DebugException {
		// check if we need to update at all
		if (lastUpdated == target.getSuspendCount()
				|| (variables != null && oldLineNumber == lineNumber))
			return;
		
		Synchronizer frontend = target.getFrontend();
		if (frontend != null) {
			// get local variables
			String localsSausage = frontend.GetLocals(thread.getID(),frameNumber);
			String[] locals;
			if (localsSausage == null || localsSausage.equals("--")
					|| localsSausage.equals(""))
				locals = new String[0];
			else
				locals = localsSausage.split(" ");
			// get parameters
			String paramsSausage = frontend.GetParameters(thread.getID(),frameNumber);
			String[] params;
			if (paramsSausage == null || paramsSausage.equals("--")
					|| paramsSausage.equals(""))
				params = new String[0];
			else
				params = paramsSausage.split(" ");
			// get static fields of the current class
			String className;
			try {
				className = methodName.substring(0, methodName.lastIndexOf('.'));
			} catch (StringIndexOutOfBoundsException e) {
				className = "";
			}
			String staticsSausage = frontend.PtypeFieldsOnly(thread.getID(),frameNumber,
					className,true);
			String[] statics;
			if (staticsSausage == null || staticsSausage.equals("--")
					|| staticsSausage.equals(""))
				statics = new String[0];
			else
				statics = staticsSausage.split(" ");
			// determine if "this" is valid
			boolean _this = true;
			if (frontend.PtypeFieldsOnly(thread.getID(),frameNumber,"this",false).equals("--"))
				_this = false;
			
			// create variable objects
			IVariable[] tempVariables = new EmonicVariable[locals.length + params.length
			                                               + statics.length + (_this ? 1 : 0)];

			if (variables == null || variables.length != tempVariables.length) {
				// something changed
				int index = 0;
				if (_this) {
					tempVariables[0] = new EmonicVariable(this,"this",null);
					index++;
				}
				Arrays.sort(locals);
				for (int i=0; i<locals.length; i++) {
					tempVariables[i+index] = new EmonicVariable(this,locals[i],null);
				}
				index+=locals.length;
				Arrays.sort(params);
				for (int i=0; i<params.length; i++) {
					tempVariables[i+index] = new EmonicVariable(this,params[i],null);
				}
				index+=params.length;
				Arrays.sort(statics);
				for (int i=0; i<statics.length; i++) {
					tempVariables[i+index] = new EmonicVariable(this,statics[i],null);
				}
				variables = tempVariables;
			} 
		}
		lastUpdated = target.getSuspendCount();
//		oldLineNumber = lineNumber;
	}
	
	public IThread getThread() {
		return thread;
	}

	public IVariable[] getVariables() throws DebugException {
		updateVariables();
		return variables;
	}

	public boolean hasVariables() throws DebugException {
		updateVariables();
		if (variables.length >= 0)
			return true;
		else
			return false;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getCharStart() {
		// MDB doesn't support expression level stepping
		return -1;
	}

	public int getCharEnd() {
		// MDB doesn't support expression level stepping
		return -1;
	}

	public String getName() {
		if (lineNumber != -1)
			return methodName+" at "+fileName+":"+lineNumber;
		else
			return methodName+" at "+fileName;
	}

	public IRegisterGroup[] getRegisterGroups() {
		// not supported
		return null;
	}

	public boolean hasRegisterGroups() {
		return false;
	}

	public boolean canStepInto() {
		return thread.canStepInto();
	}

	public boolean canStepOver() {
		return thread.canStepOver();
	}

	public boolean canStepReturn() {
		return thread.canStepReturn();
	}

	public boolean isStepping() {
		return thread.isStepping();
	}

	public void stepInto() throws DebugException {
		thread.stepInto();
	}

	public void stepOver() throws DebugException {
		thread.stepOver();
	}

	public void stepReturn() throws DebugException {
		thread.stepReturn();
	}

	public boolean canResume() {
		return thread.canResume();
	}

	public boolean canSuspend() {
		return thread.canSuspend();
	}

	public boolean isSuspended() {
		return thread.isSuspended();
	}

	public void resume() throws DebugException {
		thread.resume();
	}

	public void suspend() throws DebugException {
		thread.suspend();
	}

	public boolean canTerminate() {
		return thread.canTerminate();
	}

	public boolean isTerminated() {
		return thread.isTerminated();
	}

	public void terminate() throws DebugException {
		thread.terminate();
	}


	/**
	 * Returns the name of the source file this stack frame is associated
	 * with.
	 */
	public String getSourceName() {
		return fileName;
	}

	/**
	 * Returns true if this object corresponds to the backtrace data given.
	 * Method name, file name and line number are compared.
	 * @param bd 
	 * @return
	 */
	public boolean equalsBacktraceData(BacktraceData bd) {
		try {
			String bdFileName = new Path(bd.getFile()).lastSegment();
			if (bd.getMethod().equals(methodName)
					&& bdFileName.equals(fileName))
				return true;
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * Update this frame's line number.
	 * @param lineNumber
	 */
	public void updateLineNumber(int lineNumber) {
		// remember the old value
		oldLineNumber = this.lineNumber;
		this.lineNumber = lineNumber;
	}
	
	/**
	 * Update this frame's frame number.
	 */
	public void updateFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}
	
	/**
	 * Determines if the stack frame did change (file, line number) since
	 * the last suspend
	 */
	public boolean hasStackFrameChanged() {
		return lineNumber != oldLineNumber;
	}
}
