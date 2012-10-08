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
 *      Harald Krapfenbauer - Implementation
 */


package org.emonic.base.views;


import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;


public class CSharpImageDescriptor extends CompositeImageDescriptor {

	
	private ImageDescriptor _baseImage;
	private ImageDescriptor _backgroundImage;
	private Point _size;
	private int _flags;
	

	// flags
	public final static int VOLATILE = 0x1;
	public final static int CONST = 0x2;
	public final static int STATIC = 0x4;
	public final static int READONLY = 0x8;
	
	public final static int EVENT = 0x10;
	public final static int CONSTRUCTOR = 0x20;
	public final static int DESTRUCTOR = 0x40;
	
	public final static int WARNING = 0x80;
	public final static int ERROR = 0x100;

	public final static int SEALED = 0x200;
	public final static int ABSTRACT = 0x400;
	public final static int VIRTUAL = 0x800;
	public final static int OVERRIDE = 0x1000;
	public final static int NEW = 0x2000;
	public final static int UNSAFE = 0x4000;
		
	public CSharpImageDescriptor(ImageDescriptor baseImage, int flags, Point size) {
		this._backgroundImage = EMonoImages.ICON_BACKGROUND; 
		this._baseImage = baseImage;
		this._size = size;
		this._flags = flags;
	}
	
	//sets the size of the image created by calling createImage()
	public void setImageSize(Point size){
		this._size = size;
	}
	
	//return the size of the image created by calling createImage()
	public Point getImageSize(){
		return new Point(_size.x, _size.y);
	}
	
	protected Point getSize(){
		return _size;
	}
	
	protected void drawCompositeImage(int width, int height) {

		ImageData background = _backgroundImage.getImageData();	
		ImageData bg = _baseImage.getImageData();
		if (bg != null){
			//draw background
			drawImage(background,0,0);
			
			drawImage(bg, 0, 0);

			drawTopRight();
			drawTopLeft();
			drawBottomRight();
			drawBottomLeft();
		}
	}
	
	// top right corner: icons for "event" modifier and con/destructor
	private void drawTopRight() {
		int x = _size.x;
		// draw constructor overlay icon
		if ((_flags & CONSTRUCTOR) != 0) {
			ImageData data = EMonoImages.OVERLAY_CONSTRUCTOR;
			x -= data.width;
			drawImage(data, x, 0);
		}
		if ((_flags & DESTRUCTOR) != 0) {
			ImageData data = EMonoImages.OVERLAY_DESTRUCTOR;
			x -= data.width;
			drawImage(data, x, 0);
		}
		// draw event overlay icon
		if ((_flags & EVENT) != 0) {
			ImageData data = EMonoImages.OVERLAY_EVENT;
			x -= data.width;
			drawImage(data, x, 0);
		}
	}
	
	
	// bottom right corner: modifiers sealed, abstract, virtual, override, new, unsafe
	private void drawBottomRight() {
		int x = _size.x;
		// draw override overlay icon
		if ((_flags & OVERRIDE) != 0) {
			ImageData data = EMonoImages.OVERLAY_OVERRIDE;
			x -= data.width;
			drawImage(data, x, _size.y - data.height);
		}
		// draw abstract overlay icon
		if ((_flags & ABSTRACT) != 0) {
			ImageData data = EMonoImages.OVERLAY_ABSTRACT;
			x -= data.width;
			drawImage(data, x, _size.y - data.height);
		}
		// draw virtual overlay icon
		if ((_flags & VIRTUAL) != 0) {
			ImageData data = EMonoImages.OVERLAY_VIRTUAL;
			x -= data.width;
			drawImage(data, x, _size.y - data.height);
		}
		// draw sealed overlay icon
		if ((_flags & SEALED) != 0) {
			ImageData data = EMonoImages.OVERLAY_SEALED;
			x -= data.width;
			drawImage(data, x, _size.y - data.height);
		}
		// draw new overlay icon
		if ((_flags & NEW) != 0) {
			ImageData data = EMonoImages.OVERLAY_NEW;
			x -= data.width;
			drawImage(data, x, _size.y - data.height);
		}
		// draw unsafe overlay icon
		if ((_flags & UNSAFE) != 0) {
			ImageData data = EMonoImages.OVERLAY_UNSAFE;
			x -= data.width;
			drawImage(data, x, _size.y - data.height);
		}
	}


	// top left corner: volatile, const, static, readonly modifiers
	private void drawTopLeft() {
		int x = 0;
		// draw static overlay icon
		if ((_flags & STATIC) != 0) {
			ImageData data = EMonoImages.OVERLAY_STATIC;
			drawImage(data, x, 0);
			x += data.width;
		}
		// draw const overlay icon
		if ((_flags & CONST) != 0) {
			ImageData data = EMonoImages.OVERLAY_CONST;
			drawImage(data, x, 0);
			x += data.width;
		}
		// draw readonly overlay icon
		if ((_flags & READONLY) != 0) {
			ImageData data = EMonoImages.OVERLAY_READONLY;
			drawImage(data, x, 0);
			x += data.width;
		}
		// draw volatile overlay icon
		if ((_flags & VOLATILE) != 0) {
			ImageData data = EMonoImages.OVERLAY_VOLATILE;
			drawImage(data, x, 0);
			x += data.width;
		}
	}


	// bottom left corner: error, warning annotations
	private void drawBottomLeft() {
		// draw error overlay icon
		if ((_flags & ERROR) != 0) {
			ImageData data = EMonoImages.OVERLAY_ERROR;
			drawImage(data, 0, _size.y - data.height);
			// don't draw warning if error exists
			return;
		}
		// draw warning overlay icon
		if ((_flags & WARNING) != 0) {
			ImageData data = EMonoImages.OVERLAY_WARNING;
			drawImage(data, 0, _size.y - data.height);
		}
	}

	public boolean equals(Object object) {
		if (super.equals(object)) {
			return true;
		} else if (object instanceof CSharpImageDescriptor) {
			CSharpImageDescriptor other = (CSharpImageDescriptor) object;
			return _baseImage.equals(other._baseImage) && _size == other._size && _flags == other._flags;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return _baseImage.hashCode() ^ _size.hashCode() ^ _flags;
	}

}
