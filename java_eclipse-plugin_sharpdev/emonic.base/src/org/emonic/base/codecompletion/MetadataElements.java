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
 * This interface contains the elements of the metadata strings
 * which are stored in the tries
 * @author dertl
 *
 */
public interface MetadataElements {
	
	public static final String IDotNetElement = "@I:";

	public static final String Flags = "@F:";
	
	public static final String Signature = "@S:";
	
	public static final String Return = "@R:";
	
	public static final String DelegateType = "@D:";
	
	public static final String Superclass = "@SC:";
	
	public static final String OriginalElementString = "@O:";
	
	public static final String TypeWithoutNamespace ="@WN:";
	//Type declares for e.g. variables (like params in a method)
	//which type this variable belongs to
	//also needed for fields!
	public static final String Type = "@T:";
}
