package org.emonic.base.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;


public class EMonoBuildPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	RadioGroupFieldEditor defaulteditor;
   
	public EMonoBuildPreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
		setDescription("Set the Emonic preferences for building");
	}


	public void createFieldEditors() {
//		Composite contents = getFieldEditorParent();
//		final Shell shell = contents.getShell();
		String[] mechs= BuildDescriptionFactory.getAvailableMechanisms();
		String[][] labelsAndVals=new String[mechs.length][];
		for (int i  = 0; i < mechs.length;i++){
			String[] entry = new String[2];
			entry[0]=mechs[i];
			entry[1]=mechs[i];
			labelsAndVals[i]=entry;
		}
		
		defaulteditor= new RadioGroupFieldEditor(
				
				
				DefaultPrefsSetter.DEFAULTBUILDMECHANISM, "Default Build Tool", 1,
//				new String[][] {
//					{"Open Browser", "open"},
//					{"Expand Tree", "expand"}
//				},
			  labelsAndVals,
	          getFieldEditorParent(),true);	
		      addField(defaulteditor);
	}

	public void init(IWorkbench workbench) {

	}

	

	/** 
	 * Save the preferences to the preference store
	 */
	public boolean performOk() {
		defaulteditor.store();
		return super.performOk();
	}
}
