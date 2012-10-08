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
 *      Harald Krapfenbauer - added updateValue() and rewrote most of the code
 */


package org.emonic.debugger.frontend;

import java.util.Arrays;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.emonic.debugger.launching.EmonicDebugConstants;
import org.emonic.debugger.webinterface.PrintData;

public class EmonicValue extends EmonicDebugElement implements IValue {

	private EmonicVariable variable;
	private String value;
	private String type;
	private IVariable[] subVariables;
	private boolean hasChanged = false;
	private int lastEvaluation = 0;
	private boolean wasChanged = false;
	
	public EmonicValue(EmonicVariable variable) {
		this.target = (EmonicDebugTarget)variable.getDebugTarget();
		this.variable = variable;
	}

	public String getReferenceTypeName() {
		return type;
	}

	public String getValueString() throws DebugException {
		updateValue();
		return value;
	}

	public IVariable[] getVariables() throws DebugException {
		updateValue();
		return subVariables;
	}

	public boolean hasVariables() throws DebugException {
		updateValue();
		return subVariables != null;
	}

	public boolean isAllocated() throws DebugException {
		updateValue();
		return value.startsWith("<null>");
	}

	public IDebugTarget getDebugTarget() {
		return target;
	}

	public ILaunch getLaunch() {
		return target.getLaunch();
	}

	public String getModelIdentifier() {
		return EmonicDebugConstants.ID_EMONIC_DEBUG_MODEL;
	}
	
	private synchronized void updateValue() throws DebugException {
		// check if we must update at all
		if (
				// value was changed by user
				wasChanged
				// OR value, subvariables and type are null, not evaluated previously
				|| (value == null && subVariables == null && type == null)
				// OR value hasn't been updated at current suspend and corresponding
				// stack frame has changed
				|| !(target.getSuspendCount() == lastEvaluation
						|| !variable.getStackFrame().hasStackFrameChanged())
		) {
			// remember old value
			String oldValue = value;

			Synchronizer frontend = target.getFrontend();
			PrintData pd;
			String fullVariableName;
			if (variable.getName().matches("\\A\\[.*\\]\\z")) {
				// array
				fullVariableName = variable.getPrefix() == null ?
						variable.getName() : variable.getPrefix()+variable.getName();
			} else {
				fullVariableName = variable.getPrefix() == null ?
						variable.getName() : variable.getPrefix()+"."+variable.getName();
			}
			pd = frontend.Print(((EmonicThread)variable.getStackFrame().getThread()).getID(),
					variable.getStackFrame().getFrameID(), fullVariableName);
			type = pd.getType();
			if (type.equals(""))
				type = "<unknown>";
			value = pd.getVarValue();
			if (value.equals("")) {
				value = "<unknown>";
			}
			String subVarNames = pd.getVarNames();
			if (subVarNames.equals("")) {
				subVariables = null;
			} else {
				if (type.indexOf("[]") != -1) {
					// array type!
					int count = Integer.parseInt(subVarNames);
					if (subVariables == null || (subVariables != null
							&& count != subVariables.length)) {
						// discard the old variables (if any) and create new
						subVariables = new EmonicVariable[count];
						for (int i=0; i<count; i++) {
							subVariables[i] = new EmonicVariable(variable.getStackFrame(),
									"["+i+"]", fullVariableName);
						}
					}
				} else {
					String[] subVarsSplitted = subVarNames.split(" ");
					Arrays.sort(subVarsSplitted);
					if (subVariables == null || (subVariables != null 
							&& subVariables.length != subVarsSplitted.length)) {
						// discard the old variables (if any) and create new
						subVariables = new EmonicVariable[subVarsSplitted.length];
						for (int i=0; i<subVarsSplitted.length; i++) {
							subVariables[i] = new EmonicVariable(variable.getStackFrame(),
									subVarsSplitted[i],fullVariableName);
						}
					}
				}
			}

			if (subVariables != null) {
				value = type;
			} else {
				if (type.equals("string") && !value.equals("<null>"))
					value = "\""+value+"\"";

				value = value + " (" + type + ")";
			}
			// check if value has changed
			if (oldValue != null && !oldValue.equals(value))
				hasChanged = true;
			else
				hasChanged = false;

			lastEvaluation = target.getSuspendCount();
			wasChanged = false;
		}
	}
	
	/**
	 * Ask if the value did change
	 * @return <code>true</code> if the value has changed
	 * @throws DebugException
	 */
	public boolean hasValueChanged() throws DebugException {
		updateValue();
		return hasChanged;
	}
	
	public void valueChanged() {
		wasChanged = true;
	}
}
