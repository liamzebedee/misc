package org.emonic.base.preferences;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

public class PreferencesInjector {
	public static void injectPreferences(IInjectablePreferencePage thePage,
			Composite parent) {
		String pagename = thePage.getClass().getName();
		ArrayList plugins = getPlugins(pagename);
		for (int i = 0; i < plugins.size(); i++) {
			FieldEditor[] editors = ((IPreferencesContributor) (plugins.get(i)))
					.getEditorList(parent);
			for (int k = 0; k < editors.length; k++) {
				thePage.injectOneFieldEditor(editors[k]);
			}
		}
	}

	public static void storePreferences(IInjectablePreferencePage thePage) {
		String pagename = thePage.getClass().getName();
		ArrayList plugins = getPlugins(pagename);
		for (int i = 0; i < plugins.size(); i++) {
			((IPreferencesContributor) (plugins.get(i))).storeEditors();
		}
	}

	private static ArrayList getPlugins(String pagename) {
		ArrayList result = new ArrayList();

		try {
			IExtensionRegistry er = Platform.getExtensionRegistry();
			IExtensionPoint ep = er.getExtensionPoint("org.emonic.base",
					"preferencescontributor");
			IExtension[] extensions = ep.getExtensions();

			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] ConfigElements = extensions[i]
						.getConfigurationElements();
				for (int j = 0; j < ConfigElements.length; j++) {
					IConfigurationElement actual = ConfigElements[j];
					Object o = actual.createExecutableExtension("class");
					String targetName = actual
							.getAttribute("PreferencePageClassName");
					if (o instanceof IPreferencesContributor) {
						if (pagename == null || targetName.equals(pagename)) {
						       result.add(o);
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void setDefaults(IPreferenceStore store) {
		ArrayList plugins = getPlugins(null);
		for (int i = 0; i < plugins.size(); i++) {
			((IPreferencesContributor) (plugins.get(i))).setDefault(store);
		}
		
	}

}
