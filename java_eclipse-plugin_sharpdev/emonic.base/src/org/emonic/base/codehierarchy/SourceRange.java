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
package org.emonic.base.codehierarchy;

import org.eclipse.swt.graphics.Point;

public class SourceRange implements ISourceRange{

	
	// position related items
	/**
	 * The start point in the source file in the format x:y, e.g. lineoffset:line
	 */
	private Point startPoint;
	/**
	 * The end point in the source file in the format x:y, e.g. lineoffset:line
	 */
	private Point endPoint;
	/**
	 * The start point in the source file in the format x:y, e.g. lineoffset:line
	 */
	protected int offset = 0;
	/**
	 * The length of the element in the source file (from begin to end)
	 */
	protected int length = 0;
	/**
	 * The offset of the name. If -1: undefined If 0: Same as the offset of the element. If != 0: The start of the element name
	 * For example: offset = 0, text = class abc => nameoffset = (start of class =0) + 6 = 6
	 */
	protected int nameoffset = -1;
	/**
     * The valid Offset is the begin of the region, in which the element can have children 
     * For ecample after the curl of namespace abc{
     */
	private int validOffset=-1;
	/**
     * The valid length is length between the begin of the 
     * region, in which the element can have children or is valid 
     * to the end 
     * For example the length from the curl of namespace abc{ to the closing curl
     */
	private int validLength=-1;

	
	public SourceRange(Point startPoint, Point endPoint,
			int offset, int length,
			int nameoffset, int validOffset,
			int validLength){
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.nameoffset = nameoffset;
		this.validOffset = validOffset;
		this.validLength = validLength;
		this.length = length;
		this.offset = offset;
	}

	public int getOffset() {
		return this.offset;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public Point getStartPoint(){
		return this.startPoint;
	}
	
	public Point getEndPoint(){
		return this.endPoint;
	}
	
	public int getNameOffset(){
		return this.nameoffset;
	}
	
	public int getValidOffset(){
		return this.validOffset;
	}
	
	public int getValidLength(){
		return this.validLength;
	}

}
