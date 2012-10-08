/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.emonic.base.editors;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CSharpEditorMessages {

	private static final String RESOURCE_BUNDLE= "org.emonic.base.editors.CSharpEditorMessages";//$NON-NLS-1$

	private static ResourceBundle fgResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private CSharpEditorMessages() {
	}

	public static String getString(String key) {
		try {
			return fgResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static ResourceBundle getResourceBundle() {
		return fgResourceBundle;
	}

	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#getKeys()
	 */
	public Enumeration getKeys() {

		return fgResourceBundle.getKeys();
	}

	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 */
	protected String handleGetObject(String key) {

		return key;
	}
}
