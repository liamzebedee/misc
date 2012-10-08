/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2007 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */

package org.emonic.base.infostructure;

import org.emonic.base.codehierarchy.IDotNetElement;

/*
 *  CodeCompletionElement is an encapsulating
 *  datastructure for the code completion mechanism.
 *  It holds the CodeElement plus the display text
 *  for the code completion
 */
public class CodeCompletionElement {
	
	private IDotNetElement element;
	
	private String displayString;
	
	public CodeCompletionElement(IDotNetElement element, String display){
		this.element = element;
		this.displayString = display;
	}
	
	public IDotNetElement getElement(){
		return this.element;
	}
	
	public void setDisplayString(String display){
		this.displayString = display;
	}
	
	public String getDisplayString(){
		return this.displayString;
	}

}
