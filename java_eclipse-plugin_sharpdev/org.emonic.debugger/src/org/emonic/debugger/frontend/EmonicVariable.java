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
 *      Harald Krapfenbauer - rewrote constructor
 */


package org.emonic.debugger.frontend;

import java.math.BigInteger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.emonic.debugger.DebuggerPlugin;
import org.emonic.debugger.launching.EmonicDebugConstants;

public class EmonicVariable extends EmonicDebugElement implements IVariable {

	private String name;
	private EmonicStackFrame frame;
	private EmonicValue value;
	private String prefix;
	
	public EmonicVariable(EmonicStackFrame frame, String name, String prefix) {
		this.target = (EmonicDebugTarget)frame.getThread().getDebugTarget();
		this.name = name;
		this.frame = frame;
		this.prefix = prefix;
		if (this.prefix != null && this.prefix.equals(""))
			this.prefix = null;
	}

	/**
	 * Get the prefix of this variable, i.e. the full qualifier except the last
	 * segment.
	 * @return
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Get the stack frame this variable belongs to
	 * @return
	 */
	public EmonicStackFrame getStackFrame() {
		return frame;
	}

	public String getName() {
		return name;
	}

	public String getReferenceTypeName() throws DebugException {
		return getValue().getReferenceTypeName();
	}

	public IValue getValue() {
		if (value == null) {
			value = new EmonicValue(this);
		}
		return value;
	}

	public boolean hasValueChanged() throws DebugException {
		return ((EmonicValue)getValue()).hasValueChanged();
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

	public void setValue(String expression) throws DebugException {
		// check
		if (!verifyValue(expression))
			throw new DebugException(new Status(IStatus.ERROR,
					DebuggerPlugin.PLUGIN_ID,
					DebugPlugin.INTERNAL_ERROR,
					"Cannot set this value of the variable "+name,
					null));
		// prepare
		String type = value.getReferenceTypeName();
		String trimmedExpr = removeTypeAndTrim(expression);
		// "int" needs no preparation
		if (type.equals("uint"))
			trimmedExpr += "u";
		else if (type.equals("long"))
			trimmedExpr += "l";
		else if (type.equals("ulong"))
			trimmedExpr += "ul";
		else if (type.equals("bool"))
			trimmedExpr = trimmedExpr.toLowerCase();
		// "string" needs no preparation
		else if (type.equals("float"))
			trimmedExpr += "f";
		else if (type.equals("double"))
			trimmedExpr += "d";
		// modify
		Synchronizer frontend = target.getFrontend();
		String fullName;
		if (prefix != null) {
			if (name.matches("\\A\\[\\d+?\\]\\z"))
				// array
				fullName = prefix+name;
			else
				fullName = prefix+"."+name;
		} else
			fullName = name;
		frontend.SetVariable(((EmonicThread)getStackFrame().getThread()).getID(),
				getStackFrame().getFrameID(), fullName, trimmedExpr);
		// notify value
		value.valueChanged();
		// fire event
		fireChangeEvent();
	}

	public void setValue(IValue value) throws DebugException {
		// check
		if (!verifyValue(value))
			throw new DebugException(new Status(IStatus.ERROR,
					DebuggerPlugin.PLUGIN_ID,
					DebugPlugin.INTERNAL_ERROR,
					"Cannot set the value of variable"+name,
					null));
		// modify
		this.value = (EmonicValue)value;
		// notify value (necessary?)
		((EmonicValue)value).valueChanged();
		//let the UI know that this variable changed its content
		fireChangeEvent();
	}

	public boolean supportsValueModification() {
		EmonicValue value = (EmonicValue)getValue();
		String type = value.getReferenceTypeName();
		if (type == null)
			return false;
		
		/*
		 * Changing the following primitive types in MDB v0.60 does not work:
		 * byte - is recognized as System.Void (bug) -> no conversion exists, no suffix
		 * sbyte - no conversion exists from int, no suffix
		 * char - does not work with "set _char = 'a'"
		 * short - no conversion exists from int, no suffix
		 * ushort - no conversion exits from int, no suffix
		 * decimal - suffix is 'M', but setting does not work (bug)
		 */
		if (type.equals("int") || type.equals("uint") || type.equals("long")
				|| type.equals("ulong") || type.equals("bool") || type.equals("string")
				|| type.equals("float") || type.equals("double"))
			return true;
		return false;
	}

	public String removeTypeAndTrim(String expression) {
		if (expression.endsWith(")"))
			return (expression.substring(0, expression.lastIndexOf('('))).trim();
		else
			return expression.trim();
	}
	
	public boolean verifyValue(String expression) throws DebugException {
		// check
		if (!supportsValueModification())
			return false;
		String type = getValue().getReferenceTypeName();
		
		String trimmedExpr = removeTypeAndTrim(expression);
		
		/*
		 * Note: MDB v0.60 does not support a minus when setting variables (bug)
		 */
		if (type.equals("int")) {
			int integer;
			try {
				integer = Integer.parseInt(trimmedExpr);
			} catch (NumberFormatException e) {
				return false;
			}
			if (integer < 0)
				return false;
			return true;
		}
		if (type.equals("uint")) {
			long uInteger;
			try {
				uInteger = Long.parseLong(trimmedExpr);
			} catch (NumberFormatException e) {
				return false;
			}
			if (uInteger < 0 || uInteger > 4294967295l)
				return false;
			return true;
		}
		if (type.equals("long")) {
			long longVar;
			try {
				longVar = Long.parseLong(trimmedExpr);
			} catch (NumberFormatException e) {
				return false;
			}
			if (longVar < 0)
				return false;
			return true;
		}
		if (type.equals("ulong")) {
			BigInteger uLong;
			try {
				uLong = new BigInteger(trimmedExpr);
			} catch (NumberFormatException e) {
				return false;
			}
			if (uLong.compareTo(new BigInteger("0")) < 0
					|| uLong.compareTo(new BigInteger("18446744073709551615")) > 0)
				return false;
			return true;
		}
		if (type.equals("bool")) {
			// {true,false}, case insensitive
			String lowerCaseExpr = trimmedExpr.toLowerCase();
			if (lowerCaseExpr.equals("true") || lowerCaseExpr.equals("false"))
				return true;
			return false;
		}
		if (type.equals("string")) {
			// Note: MDB does not support \" within the string
			if (trimmedExpr.matches("\\A\"[^\"]*\"\\z"))
				return true;
			return false;
		}
		if (type.equals("float") || type.equals("double")) {
			// Note: May have a dot followed by decimal places
			// Too large values may be entered, but MDB can not set them anyway
			// Do Java and .NET floating point types have equal range?
			if (trimmedExpr.matches("\\A[\\d\\.]+\\z")) {
				if (trimmedExpr.indexOf('.') != -1) {
					if (trimmedExpr.length() >= 2)
						return true;
					return false;
				} 
				return true;
			} 
			return false;
		}
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException {
		// if it is an EmonicValue and if the type is equal, we can modify
		// the variable
		if (value == null || !(value instanceof EmonicValue)
				|| value.getReferenceTypeName() != this.value.getReferenceTypeName())
			return false;
		return true;
	}
}
