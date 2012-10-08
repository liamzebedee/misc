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
 *      Dominik Ertl - Implementation
 */
package org.emonic.base.codecompletion;

/**
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ICCFlags {

	/**
	 * This interface provides the flags for the rules which
	 * are needed during the creation of the code completion
	 * proposal; thus they are set according to the place in 
	 * code where the completion call is triggered
	 */
	
	//if "this" completion is used
	public static final int IS_THIS = 0x0000000001;
	
	//if completion call happens in static method/class
	public static final int IS_IN_STATIC = 0x0000000010 ;
	                                       
	//if completion call happens in a method =>then the 
	//parameters of the method itself shall be added to the
	//proposal
	public static final int IS_IN_METHOD = 0x0000000100;
	
	//members of an inner class shall not be suggested by cc
	//if curser position is not in inner class
	public static final int FROM_INNER_CLASS = 0x0000001000;
}
