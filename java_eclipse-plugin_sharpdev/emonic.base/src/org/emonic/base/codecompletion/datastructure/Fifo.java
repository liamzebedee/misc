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

import java.util.LinkedList;


/**
 * this implements a Fifo
 * @author dertl
 *
 */
public class Fifo extends LinkedList implements IFifo {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 44473036344353112L;
	private static final int maxsize = 15; //fixed size of fifo 
	private int counter = 0;
	
	public Object remove() {
		counter = counter - 1;
		return remove(0);
	}
	
	//adding is always possible
	public boolean add(Object o){
		//remove first element is fifo is full
		if(counter >= maxsize){
			remove();
		}
		super.add(o);		
		counter++;
		return true;		
	}

	public int actualSize() {
		return counter;
	}

	public int getMaxSize() {
		return maxsize;
	}
		
}