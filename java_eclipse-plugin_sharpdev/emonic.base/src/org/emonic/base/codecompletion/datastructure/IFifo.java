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

public interface IFifo {
	  /** Add an object to the end of the FIFO queue */
	  boolean add(Object o);
	  /** Remove an object from the front of the FIFO queue */
	  Object remove();
	  /** Return the number of elements in the FIFO queue */
	  int actualSize();
	  
	  int getMaxSize();
	  
}