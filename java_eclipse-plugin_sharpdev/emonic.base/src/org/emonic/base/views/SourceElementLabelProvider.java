/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bernhard Brem
 *     Harald Krapfenbauer, TU Vienna - re-implemented getImage() to return
 *         composited images
 *       added more modifiers
 *******************************************************************************/
package  org.emonic.base.views;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.Flags;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IField;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.codehierarchy.IProperty;
import org.emonic.base.codehierarchy.IType;


public class SourceElementLabelProvider extends LabelProvider {	

	// size of outline icons
	public static final Point sizePoint = new Point (16, 16);
	private boolean showSigVars;
	private final Map imageMap = new HashMap();
	
	public Image getImage(Object element) {
		ImageDescriptor descriptor = getImageDescriptor(element);
		Image image = (Image) imageMap.get(descriptor);
		if (image == null && descriptor != null) {
			image = descriptor.createImage();
			imageMap.put(descriptor, image);
		}
		return image;
	}
	
	/**
	 * @see ILabelProvider#getImage(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object element) {
		if (element instanceof PendingUpdateAdapter) {
			return null;
		} else if (!(element instanceof CodeElement) && element instanceof IDotNetElement) {
			switch (((IDotNetElement) element).getElementType()) {
			case IDotNetElement.ASSEMBLY:
				return null;
			case IDotNetElement.NAMESPACE:
				return new CSharpImageDescriptor(EMonoImages.ICON_NAMESPACE,0,sizePoint);
			case IDotNetElement.TYPE:
				IType type = (IType) element;
				if (type.isClass()) {
					ImageDescriptor baseImage = EMonoImages.ICON_CLASS[EMonoImages.PUBLIC];
					return new CSharpImageDescriptor(baseImage, 0,sizePoint);
				} else if (type.isInterface()) {
					ImageDescriptor baseImage = EMonoImages.ICON_INTERFACE[EMonoImages.PUBLIC];
					return new CSharpImageDescriptor(baseImage, 0,sizePoint);
				} else if (type.isStruct()) {
					ImageDescriptor baseImage = EMonoImages.ICON_STRUCT[EMonoImages.PUBLIC];
					return new CSharpImageDescriptor(baseImage, 0,sizePoint);					
				} else {
					ImageDescriptor baseImage = EMonoImages.ICON_ENUM[EMonoImages.PUBLIC];
					return new CSharpImageDescriptor(baseImage, 0,sizePoint);
				}
			case IDotNetElement.FIELD:
				IField field = (IField) element;
				int flags = field.getFlags();
				int modFlags = 0;
				if (Flags.isFieldStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}
				if (Flags.isFieldPublic(flags)) {
					ImageDescriptor baseImage = EMonoImages.ICON_FIELD[EMonoImages.PUBLIC];
					return new CSharpImageDescriptor(baseImage, modFlags, sizePoint);
				}
				return null;	
			case IDotNetElement.METHOD:
				IMethod method = (IMethod) element;
				flags = method.getFlags();
				modFlags = 0;
				
				if (method.isConstructor()) {
					modFlags |= CSharpImageDescriptor.CONSTRUCTOR;
				} else if (Flags.isMethodStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}

				ImageDescriptor baseImage = null;
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
				IProperty property = (IProperty) element;
				flags = property.getFlags();
				modFlags = 0;
				
				if (Flags.isMethodStatic(flags)) {
					modFlags |= CSharpImageDescriptor.STATIC;
				}

				baseImage = null;
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
		
        CodeElement ele = (CodeElement) element;
        
        // namespace
		if (ele.getCodeType() == IDotNetElement.NAMESPACE)
			return new CSharpImageDescriptor(EMonoImages.ICON_NAMESPACE,0,sizePoint);
		// using container
		if (ele.getCodeType() == IDotNetElement.USING_CONTAINER)
			return new CSharpImageDescriptor(EMonoImages.ICON_USING_CONTAINER,0,sizePoint);
		// using element
		if (ele.getCodeType() == IDotNetElement.USING)
			return new CSharpImageDescriptor(EMonoImages.ICON_USING,0,sizePoint);

		int flags = 0;
		ImageDescriptor baseImage = null;
		int accessType = EMonoImages.PRIVATE; // default - if anything goes wrong

		// check all modifiers and set flags accordingly
		if (ele.volatileMod)
			flags |= CSharpImageDescriptor.VOLATILE;
		if (ele.constMod)
			flags |= CSharpImageDescriptor.CONST;
		if (ele.staticMod)
			flags |= CSharpImageDescriptor.STATIC;
		if (ele.readonlyMod)
			flags |= CSharpImageDescriptor.READONLY;
		if (ele.eventMod)
			flags |= CSharpImageDescriptor.EVENT;
		if (ele.getElementType() == IDotNetElement.CONSTRUCTOR)
			flags |= CSharpImageDescriptor.CONSTRUCTOR;
		if (ele.getElementType() == IDotNetElement.DESTRUCTOR)
			flags |= CSharpImageDescriptor.DESTRUCTOR;
		if (ele.sealedMod)
			flags |= CSharpImageDescriptor.SEALED;
		if (ele.abstractMod)
			flags |= CSharpImageDescriptor.ABSTRACT;
		if (ele.virtualMod)
			flags |= CSharpImageDescriptor.VIRTUAL;
		if (ele.overrideMod)
			flags |= CSharpImageDescriptor.OVERRIDE;
		if (ele.newMod)
			flags |= CSharpImageDescriptor.NEW;
		if (ele.unsafeMod)
			flags |= CSharpImageDescriptor.UNSAFE;

		// check access modifier
		String accessTypeName = ele.getAccessType();
		if (accessTypeName != null) {
			if (accessTypeName.equalsIgnoreCase("private")) {
				accessType = EMonoImages.PRIVATE;
			} else if (accessTypeName.equalsIgnoreCase("public")) {
				accessType = EMonoImages.PUBLIC;
			} else if (accessTypeName.equalsIgnoreCase("internal")) {
				accessType = EMonoImages.INTERNAL;
			} else if (accessTypeName.equalsIgnoreCase("protected")) {
				accessType = EMonoImages.PROTECTED;
			} else if (accessTypeName.equalsIgnoreCase("protected internal")) {
				accessType = EMonoImages.PROTECTEDINTERNAL;
			}
		}
		
		switch (ele.getElementType()) {
		case IDotNetElement.CLASS:
			baseImage = EMonoImages.ICON_CLASS[accessType];
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		case IDotNetElement.INTERFACE:
			baseImage = EMonoImages.ICON_INTERFACE[accessType];
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		case IDotNetElement.PROPERTY:
			baseImage = EMonoImages.ICON_PROPERTY[accessType];
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		case IDotNetElement.METHOD:
			baseImage = EMonoImages.ICON_METHOD[accessType];
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		case IDotNetElement.FIELD:
			baseImage = EMonoImages.ICON_FIELD[accessType];
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		case IDotNetElement.ENUM:
			baseImage = EMonoImages.ICON_ENUM[accessType];
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		case IDotNetElement.STRUCT:
			baseImage = EMonoImages.ICON_STRUCT[accessType];
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		default:
			baseImage = EMonoImages.ICON_ITEM;
			return new CSharpImageDescriptor(baseImage,flags,sizePoint);
		}
	}
	
	public void dispose() {
		for (Iterator it = imageMap.values().iterator(); it.hasNext();) {
			Image image = (Image) it.next();
			image.dispose();
		}
		imageMap.clear();
 		super.dispose();
	}
	
	static String getCodeElementText(boolean showParameterNames, CodeElement ele) {
		   String val = ele.getElementName();
		   
		   switch (ele.getElementType()) {
		   case IDotNetElement.METHOD:
		   case IDotNetElement.CONSTRUCTOR:
		   case IDotNetElement.DESTRUCTOR:
			   if (showParameterNames) {
				   val += ele.getSignature();   
			   } else {
				   val += ele.getNormedSignature();				   
			   }
			   break;
		   case IDotNetElement.FIELD:
		   case IDotNetElement.VARIABLE:
			   val += " : " + ele.getTypeSignature();
			   break;
		   }
		   
		   return val;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof CodeElement) {
		   CodeElement ele = (CodeElement) element;
		   String val = ele.getElementName();
		   
		   switch (ele.getElementType()) {
		   case IDotNetElement.METHOD:
		   case IDotNetElement.CONSTRUCTOR:
		   case IDotNetElement.DESTRUCTOR:
			   String signature=ele.getSignature();
			   if (! showSigVars) signature=ele.getNormedSignature();
			   val +=  signature ;
			   break;
		   case IDotNetElement.FIELD:
		   case IDotNetElement.VARIABLE:
			   val += " : " + ele.getTypeSignature();
			   break;
		   }
		   
		   return val;
		} else if (element instanceof PendingUpdateAdapter) {
			return ((PendingUpdateAdapter) element).getLabel(element);
		}
		switch (((IDotNetElement) element).getElementType()) {
		case IDotNetElement.FIELD:
			IField field = (IField) element;
			return field.getElementName() + " : " + field.getTypeSignature();
		case IDotNetElement.METHOD:
			IMethod method = (IMethod) element;
			StringBuffer buffer = new StringBuffer();
			if (method.isConstructor()) {
				buffer.append(method.getParent().getElementName());
			} else {
				String name = method.getElementName();
				if (name.equals(".cctor")) {
					return name;
				}
				buffer.append(name);
			}
			synchronized (buffer) {
				buffer.append('(');
				String[] types = method.getParameterTypes();
				for (int i = 0; i < types.length; i++) {
					int index = types[i].lastIndexOf('.');
					if (index == -1) {
						buffer.append(types[i]).append(", "); //$NON-NLS-1$
					} else {
						buffer.append(types[i].substring(index + 1)).append(
								", "); //$NON-NLS-1$
					}
				}
				if (types.length != 0) {
					buffer.delete(buffer.length() - 2, buffer.length());
				}
				buffer.append(')');
				return buffer.toString();
			}
		default:
			return ((IDotNetElement) element).getElementName();
		}
	}


	/**
	 * Show the varnames in signatures
	 * @param b
	 */
	public void setShowSigVars(boolean b) {
		showSigVars=b;
		
	}
}
