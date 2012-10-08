package org.emonic.debugger.ui;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.emonic.debugger.DebuggerPlugin;
import org.emonic.debugger.launching.EmonicDebugConstants;

public class DefaultPrefsSetter {

	public static void initializeDefaults() {
		IPreferenceStore store = DebuggerPlugin.getDefault().getPreferenceStore();
		store.setDefault(EmonicDebugConstants.PREF_STARTING_UP_TIMEOUT, "5000");
		store.setDefault(EmonicDebugConstants.PREF_EVENT_POLLING, "200");
		store.setDefault(EmonicDebugConstants.PREF_BACKTR_THR_TIMEOUT, "1000");
		store.setDefault(EmonicDebugConstants.PREF_USE_OWN_WEBSERVICE, false);
		store.setDefault(EmonicDebugConstants.PREF_STARTING_PORT, "8080");
		try {
			URL a = FileLocator.find(
					DebuggerPlugin.getDefault().getBundle(),
					new Path(org.emonic.debugger.launching.EmonicDebugConstants.SERVICES_PATH),
					null);
			URL b = FileLocator.resolve(a);
			String serviceWorkDir = b.getFile();
			store.setDefault(EmonicDebugConstants.PREF_WEBSERVICE_LOCATION, serviceWorkDir);
		} catch (IOException e) {
			store.setDefault(EmonicDebugConstants.PREF_WEBSERVICE_LOCATION,"");
		}
	}
}
