/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2007 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Ertl Dominik - Implementation
 */

package org.emonic.base.preferences;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;

public class EMonoTaskPreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	StringFieldEditor taskTags;
	
	public EMonoTaskPreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
		setDescription("Strings indicating tasks in C# comments");		
	}

	public void createFieldEditors() {
		
		taskTags=new StringFieldEditor(DefaultPrefsSetter.CS_TODO_TAGS, 
				"&Task tags (seperated by spaces):", getFieldEditorParent());
		addField(taskTags);	
		taskTags.setStringValue(DefaultPrefsSetter.DEFAULT_CS_TODO_TAGS);
	}

	public void init(IWorkbench workbench) {
	}

	/** 
	 * Save the preferences to the preference store
	 */
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(DefaultPrefsSetter.CS_TODO_TAGS,taskTags.getStringValue());	
		return super.performOk();
	}
	
}
