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

package org.emonic.base.codecompletion.datastructure;

import java.io.Serializable;
import java.util.Map;

/**
 * This interface has to be implemented by datastructures
 * which shall be used for storing elements (mostly strings) 
 * for code completion;
 * @author dertl
 *
 */
public interface ITrie extends Serializable {

	/*
	 * Add a string to the datastructure  
	 */
	void add(String key, String value);

	/**
	 * If the result of a search is an ITrie, then one has to use
	 * getSortedList on the object
	 * @return
	 */
	Map getSortedMap(String searchstring);

}
