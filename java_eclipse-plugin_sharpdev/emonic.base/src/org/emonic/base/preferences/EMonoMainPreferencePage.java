/*
 * Created on 16.06.2007
 * emonic.base org.emonic.base.preferences EMonoMainPreferencePage.java
 */
package org.emonic.base.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;

public class EMonoMainPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	public EMonoMainPreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
		setDescription("Set the Emonic preferences");
		noDefaultAndApplyButton();
	}
	
	protected void createFieldEditors() {
		// Main page with no entries - might change some day

	}

	public void init(IWorkbench workbench) {
		// Nothing to do here

	}

}
