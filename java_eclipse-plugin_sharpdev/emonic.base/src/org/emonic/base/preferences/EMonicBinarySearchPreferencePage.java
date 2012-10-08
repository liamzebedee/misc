package org.emonic.base.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codecompletion.AssemblyParserFactory;


public class EMonicBinarySearchPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage, IInjectablePreferencePage {
	RadioGroupFieldEditor binarySearchMechanismE;
	public EMonicBinarySearchPreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
		setDescription("Set the Emonic preferences searching assemblies");
	}


	public void createFieldEditors() {
		      String[][] labelAndValues=AssemblyParserFactory.getLabelsAndValues();
			  binarySearchMechanismE=new RadioGroupFieldEditor(DefaultPrefsSetter.BINARYSEARCHMETHOD, "Search Mechanism for Assemblies", labelAndValues.length, labelAndValues, getFieldEditorParent());
			  addField(binarySearchMechanismE);
			  PreferencesInjector.injectPreferences(this,getFieldEditorParent());
	}

	public void init(IWorkbench workbench) {

	}

	

	/** 
	 * Save the preferences to the preference store
	 */
	public boolean performOk() {
		binarySearchMechanismE.store();
		return super.performOk();
	}


	public void injectOneFieldEditor(FieldEditor editor) {
		addField(editor);
		
	}
}
