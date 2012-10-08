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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.emonic.base.codehierarchy.Flags;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.views.CSharpImageDescriptor;
import org.emonic.base.views.EMonoImages;

public class CodeCompletionLabelProvider extends LabelProvider {

	// size of outline icons
	public static final Point sizePoint = new Point (16, 16);
	private final Map imageMap = new HashMap();
	
	public Image getImage(int idotnetelement, int flags) {
		ImageDescriptor descriptor = getImageDescriptor(idotnetelement, flags);
		Image image = (Image) imageMap.get(descriptor);
		if (image == null && descriptor != null) {
			image = descriptor.createImage();
			imageMap.put(descriptor, image);
		}
		return image;
	}
	
	
	public ImageDescriptor getImageDescriptor(int idotnetelement, int flags) {
		
		ImageDescriptor baseImage = null;
		
		switch(idotnetelement){
		  
			case IDotNetElement.ASSEMBLY:
				return null;
			case IDotNetElement.NAMESPACE:
				return new CSharpImageDescriptor(EMonoImages.ICON_NAMESPACE,0,sizePoint);
			case IDotNetElement.USING_CONTAINER:
					return new CSharpImageDescriptor(EMonoImages.ICON_USING_CONTAINER,0,sizePoint);
			case IDotNetElement.USING:
					return new CSharpImageDescriptor(EMonoImages.ICON_USING,0,sizePoint);
			case IDotNetElement.TYPE:
					int access = 0;
					if(Flags.isTypePublic(flags)){
						access = EMonoImages.PUBLIC;
					}else{
						access = EMonoImages.PRIVATE;
					}
					
					if(Flags.isTypeClass(flags)){
						baseImage = EMonoImages.ICON_CLASS[access];
					}else if(Flags.isTypeInterface(flags)){
						baseImage = EMonoImages.ICON_INTERFACE[access];
					}else if(Flags.isTypeStruct(flags)){
						baseImage = EMonoImages.ICON_STRUCT[access];
					}else{
						baseImage = EMonoImages.ICON_ENUM[access];
					}
			case IDotNetElement.CLASS:
					if(Flags.isTypePublic(flags)){
						baseImage = EMonoImages.ICON_CLASS[EMonoImages.PUBLIC];
					}else{
						baseImage = EMonoImages.ICON_CLASS[EMonoImages.PRIVATE];
					}
					return new CSharpImageDescriptor(baseImage, 0,sizePoint);
			case IDotNetElement.INTERFACE:
				if(Flags.isTypePublic(flags)){
					baseImage = EMonoImages.ICON_INTERFACE[EMonoImages.PUBLIC];
				}else{
					baseImage = EMonoImages.ICON_INTERFACE[EMonoImages.PRIVATE];
				}
				return new CSharpImageDescriptor(baseImage, 0,sizePoint);
			case IDotNetElement.STRUCT:
				if(Flags.isTypePublic(flags)){
					baseImage = EMonoImages.ICON_STRUCT[EMonoImages.PUBLIC];
				}else{
					baseImage = EMonoImages.ICON_STRUCT[EMonoImages.PRIVATE];
				}
				return new CSharpImageDescriptor(baseImage, 0,sizePoint);
			case IDotNetElement.ENUM:
				if(Flags.isTypePublic(flags)){
					baseImage = EMonoImages.ICON_ENUM[EMonoImages.PUBLIC];
				}else{
					baseImage = EMonoImages.ICON_ENUM[EMonoImages.PRIVATE];
				}
				return new CSharpImageDescriptor(baseImage, 0,sizePoint);
			case IDotNetElement.FIELD:
				int modFlags = 0;
				if (Flags.isFieldStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}
				if (Flags.isFieldPublic(flags)) {
					baseImage = EMonoImages.ICON_FIELD[EMonoImages.PUBLIC];
					
				}else{
					baseImage = EMonoImages.ICON_FIELD[EMonoImages.PRIVATE];
				}
				return new CSharpImageDescriptor(baseImage, modFlags, sizePoint);
				
			case IDotNetElement.EVENT:
				modFlags = 0;
				modFlags |= CSharpImageDescriptor.EVENT;
				if (Flags.isMethodStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}
	
				if (Flags.isMethodPublic(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PUBLIC];
				} else if (Flags.isMethodInternal(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.INTERNAL];
				} else if (Flags.isMethodProtected(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PROTECTED];
				} else if (Flags.isMethodProtectedInternal(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PROTECTEDINTERNAL];
				} else {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PRIVATE];
				}			
				return new CSharpImageDescriptor(baseImage,modFlags,sizePoint);
				
			case IDotNetElement.METHOD:
				modFlags = 0;
				//FIXME dont know how to check if constructor
				if (Flags.isMethodStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}
	
				if (Flags.isMethodPublic(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PUBLIC];
				} else if (Flags.isMethodInternal(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.INTERNAL];
				} else if (Flags.isMethodProtected(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PROTECTED];
				} else if (Flags.isMethodProtectedInternal(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PROTECTEDINTERNAL];
				} else {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PRIVATE];
				}			
				return new CSharpImageDescriptor(baseImage,modFlags,sizePoint);
				
			case IDotNetElement.CONSTRUCTOR:
				modFlags = 0;
				modFlags |= CSharpImageDescriptor.CONSTRUCTOR;
				if (Flags.isMethodStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}
	
				if (Flags.isMethodPublic(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PUBLIC];
				} else if (Flags.isMethodInternal(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.INTERNAL];
				} else if (Flags.isMethodProtected(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PROTECTED];
				} else if (Flags.isMethodProtectedInternal(flags)) {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PROTECTEDINTERNAL];
				} else {
					baseImage = EMonoImages.ICON_METHOD[EMonoImages.PRIVATE];
				}			
				return new CSharpImageDescriptor(baseImage,modFlags,sizePoint);

				
			case IDotNetElement.PROPERTY:
				modFlags = 0;
				
				if (Flags.isMethodStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}

				if (flags == Flags.PUBLIC_METHOD) {
					baseImage = EMonoImages.ICON_PROPERTY[EMonoImages.PUBLIC];
				} else if (flags == Flags.INTERNAL_METHOD) {
					baseImage = EMonoImages.ICON_PROPERTY[EMonoImages.INTERNAL];
				} else if (flags == Flags.PROTECTED_METHOD) {
					baseImage = EMonoImages.ICON_PROPERTY[EMonoImages.PROTECTED];
				} else if (flags == Flags.PROTECTED_INTERNAL_METHOD) {
					baseImage = EMonoImages.ICON_PROPERTY[EMonoImages.PROTECTEDINTERNAL];
				} else {
					baseImage = EMonoImages.ICON_PROPERTY[EMonoImages.PRIVATE];
				}
				
				return new CSharpImageDescriptor(baseImage,modFlags,sizePoint);
				
			case IDotNetElement.VARIABLE:
				// local variable
				baseImage = EMonoImages.ICON_ITEM;
				return new CSharpImageDescriptor(baseImage, 0, sizePoint);
			default:
				return null;
	   }
	
	}
}
