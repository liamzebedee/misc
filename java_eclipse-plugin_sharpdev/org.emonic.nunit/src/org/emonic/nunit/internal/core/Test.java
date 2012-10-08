/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.nunit.internal.core;

public class Test {

	private String name;

	private String stackTrace;

	private boolean isSuccessful = false;
	
	private boolean hasErrors = false;

	private boolean running = true;

	public Test(String name) {
		this.name = name;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setIsSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}

	public String getStackTrace() {
		return stackTrace;
	}
	
	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}
	
	public boolean hasErrors() {
		return hasErrors;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getLabel() {
		return name;
	}

	public String getName() {
		return name;
	}

}
