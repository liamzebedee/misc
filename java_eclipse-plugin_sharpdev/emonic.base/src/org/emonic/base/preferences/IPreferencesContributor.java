package org.emonic.base.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

public interface IPreferencesContributor {

	FieldEditor[] getEditorList(Composite parent);

	void storeEditors();

	void setDefault(IPreferenceStore store);

}
