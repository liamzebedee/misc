package org.emonic.base.build.nant.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.preferences.DefaultPrefsSetter;


public class EMonoNantPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

	// the fieldeditors
	//StringFieldEditor p_antcmdE;
	StringFieldEditor p_nantcmdE;

	public EMonoNantPreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
		setDescription("Set preferences for the NAnt build tool");
	}


	public void createFieldEditors() {

//		p_antcmdE=new StringFieldEditor(DefaultPrefsSetter.P_ANTCMD, 
//				"&Ant Command:", getFieldEditorParent());
//		addField(p_antcmdE);
		p_nantcmdE=new StringFieldEditor(DefaultPrefsSetter.P_NANTCMD, 
				"&NAnt Command:", getFieldEditorParent());
		addField(p_nantcmdE);
	}

	public void init(IWorkbench workbench) {

	}

	private void storeValues() {
		IPreferenceStore store = getPreferenceStore();
		//store.setValue(DefaultPrefsSetter.P_ANTCMD,p_antcmdE.getStringValue());	
		store.setValue(DefaultPrefsSetter.P_NANTCMD, p_nantcmdE.getStringValue());				
	}

	/** 
	 * Save the preferences to the preference store
	 */
	public boolean performOk() {
		storeValues() ;
		return super.performOk();
	}
}
