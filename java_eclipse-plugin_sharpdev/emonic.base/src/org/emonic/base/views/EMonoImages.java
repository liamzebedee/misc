/**
 * Contributors:
 *   Bernhard Brem
 *   Harald Krapfenbauer, TU Vienna - added descriptors for "internal" and "protected internal"
 */

package org.emonic.base.views;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.emonic.base.EMonoPlugin;


public class EMonoImages
{
	
	// base URL of plug-in
	static final URL BASE_URL = EMonoPlugin.getDefault().getBundle().getEntry("/");

	// image descriptors for main icons
	public static final ImageDescriptor ICON_BACKGROUND;
	public static final ImageDescriptor ICON_ITEM;
	public static final ImageDescriptor ICON_NAMESPACE;
	public static final ImageDescriptor[] ICON_CLASS;
	public static final ImageDescriptor[] ICON_INTERFACE;
	public static final ImageDescriptor[] ICON_PROPERTY;
	public static final ImageDescriptor[] ICON_METHOD;
	public static final ImageDescriptor[] ICON_FIELD;
	public static final ImageDescriptor[] ICON_ENUM;
	public static final ImageDescriptor[] ICON_STRUCT;
	public static final ImageDescriptor ICON_USING;
	public static final ImageDescriptor ICON_USING_CONTAINER;
		
	// image data for overlay icons
	public static final ImageData OVERLAY_CONSTRUCTOR;
	public static final ImageData OVERLAY_DESTRUCTOR;
	public static final ImageData OVERLAY_CONST;
	public static final ImageData OVERLAY_ABSTRACT;
	public static final ImageData OVERLAY_STATIC;
	public static final ImageData OVERLAY_VIRTUAL;
	public static final ImageData OVERLAY_OVERRIDE;
	public static final ImageData OVERLAY_ERROR;
	public static final ImageData OVERLAY_WARNING;
	public static final ImageData OVERLAY_VOLATILE;
	public static final ImageData OVERLAY_UNSAFE;
	public static final ImageData OVERLAY_SEALED;
	public static final ImageData OVERLAY_READONLY;
	public static final ImageData OVERLAY_NEW;
	public static final ImageData OVERLAY_EVENT;

	// constants for access modifiers	
	public static final int PRIVATE = 0;
	public static final int PUBLIC = 1;
	public static final int INTERNAL = 2;
	public static final int PROTECTED = 3;
	public static final int PROTECTEDINTERNAL = 4;

	
	// create descriptors and data
	static
	{
		String iconPath = "icons/";

		ICON_BACKGROUND = createImageDescriptor(iconPath + "background.gif");
		ICON_ITEM = createImageDescriptor(iconPath + "variable.gif");
		ICON_NAMESPACE = createImageDescriptor(iconPath + "namespace.gif");
		ICON_USING = createImageDescriptor(iconPath + "using.gif");
		ICON_USING_CONTAINER = createImageDescriptor(iconPath + "using_container.gif");

		ICON_CLASS = new ImageDescriptor[5];
		ICON_CLASS[PRIVATE] = createImageDescriptor(iconPath + "class_intern.gif");
		ICON_CLASS[PUBLIC] = createImageDescriptor(iconPath + "class_public.gif");
		ICON_CLASS[INTERNAL] = createImageDescriptor(iconPath + "class_intern.gif");
		ICON_CLASS[PROTECTED] = createImageDescriptor(iconPath + "class_intern.gif");
		ICON_CLASS[PROTECTEDINTERNAL] = createImageDescriptor(iconPath + "class_intern.gif");
		ICON_INTERFACE = new ImageDescriptor[5];
		ICON_INTERFACE[PRIVATE] = createImageDescriptor(iconPath + "interface_intern.gif");
		ICON_INTERFACE[PUBLIC] = createImageDescriptor(iconPath + "interface_public.gif");
		ICON_INTERFACE[INTERNAL] = createImageDescriptor(iconPath + "interface_intern.gif");
		ICON_INTERFACE[PROTECTED] = createImageDescriptor(iconPath + "interface_intern.gif");
		ICON_INTERFACE[PROTECTEDINTERNAL] = createImageDescriptor(iconPath + "interface_intern.gif");
		ICON_PROPERTY = new ImageDescriptor[5];
		ICON_PROPERTY[PRIVATE] = createImageDescriptor(iconPath + "property_intern.gif");
		ICON_PROPERTY[PUBLIC] = createImageDescriptor(iconPath + "property_public.gif");
		ICON_PROPERTY[INTERNAL] = createImageDescriptor(iconPath + "property_intern.gif");
		ICON_PROPERTY[PROTECTED] = createImageDescriptor(iconPath + "property_intern.gif");
		ICON_PROPERTY[PROTECTEDINTERNAL] = createImageDescriptor(iconPath + "property_intern.gif");
		ICON_METHOD = new ImageDescriptor[5];
		ICON_METHOD[PRIVATE] = createImageDescriptor(iconPath + "method_intern.gif");
		ICON_METHOD[PUBLIC] = createImageDescriptor(iconPath + "method_public.gif");
		ICON_METHOD[INTERNAL] = createImageDescriptor(iconPath + "method_intern.gif");
		ICON_METHOD[PROTECTED] = createImageDescriptor(iconPath + "method_intern.gif");
		ICON_METHOD[PROTECTEDINTERNAL] = createImageDescriptor(iconPath + "method_intern.gif");
		ICON_FIELD = new ImageDescriptor[5];
		ICON_FIELD[PRIVATE] = createImageDescriptor(iconPath + "field_intern.gif");
		ICON_FIELD[PUBLIC] = createImageDescriptor(iconPath + "field_public.gif");
		ICON_FIELD[INTERNAL] = createImageDescriptor(iconPath + "field_intern.gif");
		ICON_FIELD[PROTECTED] = createImageDescriptor(iconPath + "field_intern.gif");
		ICON_FIELD[PROTECTEDINTERNAL] = createImageDescriptor(iconPath + "field_intern.gif");
		ICON_ENUM = new ImageDescriptor[5];
		ICON_ENUM[PRIVATE] = createImageDescriptor(iconPath + "enum_intern.gif");
		ICON_ENUM[PUBLIC] = createImageDescriptor(iconPath + "enum_public.gif");
		ICON_ENUM[INTERNAL] = createImageDescriptor(iconPath + "enum_inter.gif");
		ICON_ENUM[PROTECTED] = createImageDescriptor(iconPath + "enum_intern.gif");
		ICON_ENUM[PROTECTEDINTERNAL] = createImageDescriptor(iconPath + "enum_intern.gif");
		ICON_STRUCT = new ImageDescriptor[5];
		ICON_STRUCT[PRIVATE] = createImageDescriptor(iconPath + "struct_intern.gif");
		ICON_STRUCT[PUBLIC] = createImageDescriptor(iconPath + "struct_public.gif");
		ICON_STRUCT[INTERNAL] = createImageDescriptor(iconPath + "struct_intern.gif");
		ICON_STRUCT[PROTECTED] = createImageDescriptor(iconPath + "struct_intern.gif");
		ICON_STRUCT[PROTECTEDINTERNAL] = createImageDescriptor(iconPath + "struct_intern.gif");
		
		OVERLAY_CONSTRUCTOR = createImageData(iconPath + "constr_ovr.gif");
		OVERLAY_DESTRUCTOR = createImageData(iconPath + "destr_ovr.gif");
		OVERLAY_CONST = createImageData(iconPath + "const.gif");
		OVERLAY_STATIC = createImageData(iconPath + "static.gif");
		OVERLAY_ABSTRACT = createImageData(iconPath + "abstract.gif");
		OVERLAY_VIRTUAL = createImageData(iconPath + "virtual.gif");
		OVERLAY_OVERRIDE = createImageData(iconPath + "override.gif");
		OVERLAY_ERROR = createImageData(iconPath + "error.gif");
		OVERLAY_WARNING = createImageData(iconPath + "warning.gif");
		OVERLAY_READONLY = createImageData(iconPath + "readonly.gif");
		OVERLAY_EVENT = createImageData(iconPath + "event.gif");
		OVERLAY_NEW = createImageData(iconPath + "new.gif");
		OVERLAY_SEALED = createImageData(iconPath + "sealed.gif");
		OVERLAY_VOLATILE = createImageData(iconPath + "volatile.gif");
		OVERLAY_UNSAFE = createImageData(iconPath + "unsafe.gif");
	}


	private static ImageDescriptor createImageDescriptor(String path) {
		try {
			URL url = new URL(BASE_URL, path);
			return ImageDescriptor.createFromURL(url);
		}
		catch(MalformedURLException e) {
			e.printStackTrace();
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}


	private static ImageData createImageData(String relativeFilePath) {
		try {
			URL url = new URL(BASE_URL, relativeFilePath);
			InputStream stream = url.openStream();
			return new ImageData(stream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
