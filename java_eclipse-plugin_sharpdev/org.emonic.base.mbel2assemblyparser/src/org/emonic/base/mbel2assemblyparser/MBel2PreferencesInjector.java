package org.emonic.base.mbel2assemblyparser;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.emonic.base.preferences.IPreferencesContributor;


public class MBel2PreferencesInjector implements IPreferencesContributor {
	public static final String CODECOMPLETIONCACHE = "CodeCompletionCache";
	public static final String REMOVECODECOMPLETIONCACHE = "CodeCompletionRemoveCache";

	private IntegerFieldEditor CodeCompletionCacheE;
	private BooleanFieldEditor CodeCompletionRemoveCacheE;
	
	
	public FieldEditor[] getEditorList(Composite parent) {
		FieldEditor[] result = new FieldEditor[2];
		CodeCompletionRemoveCacheE=new BooleanFieldEditor(REMOVECODECOMPLETIONCACHE,"&Remove Cached Results",parent);
		result[0]=CodeCompletionRemoveCacheE;
		CodeCompletionCacheE=new IntegerFieldEditor(CODECOMPLETIONCACHE,"&Cache results (seconds)",parent);
		result[1]=CodeCompletionCacheE;
		return result;
	}

	public void storeEditors() {
		CodeCompletionRemoveCacheE.store();
		CodeCompletionCacheE.store();
		
	}

	public void setDefault(IPreferenceStore store) {
		store.setDefault(CODECOMPLETIONCACHE, 100);
		store.setDefault(REMOVECODECOMPLETIONCACHE, false);
		
	}

}
