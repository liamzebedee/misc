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
 *      Harald Krapfenbauer - Implementation
 */

package org.emonic.base.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;

public class SyntaxColoringPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	
	// the field editors
	ColorFieldEditor stringColorE;
	BooleanFieldEditor stringBoldE;
	ColorFieldEditor commentColorE;
	BooleanFieldEditor commentBoldE;
	ColorFieldEditor keywordColorE;
	BooleanFieldEditor keywordBoldE;
	ColorFieldEditor docColorE;
	BooleanFieldEditor docBoldE;

	
	public SyntaxColoringPreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
		setDescription("Syntax highlighting preferences for the C# editor");
	}

	
	public void createFieldEditors() {
		keywordColorE = new ColorFieldEditor(DefaultPrefsSetter.KEYWORDCOLOR, "&Keywords: Color", getFieldEditorParent());
		addField(keywordColorE);
		keywordBoldE = new BooleanFieldEditor(DefaultPrefsSetter.KEYWORDBOLD, "Bo&ld style", getFieldEditorParent());
		addField(keywordBoldE);
		stringColorE = new ColorFieldEditor(DefaultPrefsSetter.STRINGCOLOR, "&Strings: Color", getFieldEditorParent());
		addField(stringColorE);
		stringBoldE = new BooleanFieldEditor(DefaultPrefsSetter.STRINGBOLD, "&Bold style", getFieldEditorParent());
		addField(stringBoldE);
		docColorE = new ColorFieldEditor(DefaultPrefsSetter.DOCCOLOR, "&Documentation: Color", getFieldEditorParent());
		addField(docColorE);
		docBoldE = new BooleanFieldEditor(DefaultPrefsSetter.DOCBOLD, "Bold s&tyle", getFieldEditorParent());
		addField(docBoldE);
		commentColorE = new ColorFieldEditor(DefaultPrefsSetter.COMMENTCOLOR, "&Comments: Color", getFieldEditorParent());
		addField(commentColorE);
		commentBoldE = new BooleanFieldEditor(DefaultPrefsSetter.COMMENTBOLD, "B&old style", getFieldEditorParent());
		addField(commentBoldE);
	}
	
	public void init(IWorkbench workbench) {
	}

	
	/** 
	 * Saves the preferences to the preference store
	 */
	public boolean performOk() {
		docColorE.store();
		docBoldE.store();
		commentColorE.store();
		commentBoldE.store();
		keywordColorE.store();
		keywordBoldE.store();
		stringColorE.store();
		stringBoldE.store();
		return super.performOk();
	}
}
