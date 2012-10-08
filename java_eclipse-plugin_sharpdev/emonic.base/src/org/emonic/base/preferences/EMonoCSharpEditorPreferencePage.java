package org.emonic.base.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.emonic.base.EMonoPlugin;

public class EMonoCSharpEditorPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	
	
	// the field editors
	BooleanFieldEditor smartHomeEnd;
	IntegerFieldEditor tabspaceE;
	//StringFieldEditor cliInformatorCmdE;
	BooleanFieldEditor useSpaceTabE;
	BooleanFieldEditor useCompletionE;
	
	//BooleanFieldEditor useEmonicInformatorCompletionE;
	BooleanFieldEditor useParsingE;
	StringFieldEditor parsingCommandE;
	BooleanFieldEditor useBracketMatchingE;
	ColorFieldEditor bracketMatchingColorE;
	//private BooleanFieldEditor useEmonicInformatorCompletionWithFramework;
	private BooleanFieldEditor useEmonicParsingWithCommand;
	
	
	public EMonoCSharpEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
	}
	
	public void createFieldEditors() {
		Composite contents = getFieldEditorParent();
		final Shell shell = contents.getShell();

		Link link= new Link(contents, SWT.NONE);
		link.setText("C# editor preferences. Note that some preferences may be set on the <a>Text Editors</a> preference page.");
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(shell, "org.eclipse.ui.preferencePages.GeneralTextEditor", null, null); //$NON-NLS-1$
			}
		});
		link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		
		smartHomeEnd = new BooleanFieldEditor(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END, "Smart caret &positioning at line start and end", getFieldEditorParent());
		addField(smartHomeEnd);
		useSpaceTabE= new BooleanFieldEditor(DefaultPrefsSetter.USESPACEASTABS, "Insert &spaces for tabs", getFieldEditorParent());
		addField(useSpaceTabE);
		tabspaceE = new IntegerFieldEditor(DefaultPrefsSetter.TABSPACE, "&Insert how many spaces for one tab?", getFieldEditorParent());	
		addField(tabspaceE);
		useCompletionE = new BooleanFieldEditor(DefaultPrefsSetter.USECOMPLETION, "&Use completion", getFieldEditorParent());
		addField(useCompletionE);
		
		//useEmonicInformatorCompletionE=new BooleanFieldEditor(DefaultPrefsSetter.USEEMONICINFORMATORCOMPLETION, "Use &completion with Emonic Informator", getFieldEditorParent());
		//addField(useEmonicInformatorCompletionE);
		//useEmonicInformatorCompletionWithFramework=new BooleanFieldEditor(DefaultPrefsSetter.USEEMONICINFORMATORCOMPLETIONWITHCOMMAND, "Override the default Emonicinformator command with the following (Experts only!)", getFieldEditorParent());
		//addField(useEmonicInformatorCompletionWithFramework);
		//cliInformatorCmdE = new StringFieldEditor(DefaultPrefsSetter.CLIINFORMATORCMD, "&Emonic Informator (CLI-Explorer) command", getFieldEditorParent());
		//addField(cliInformatorCmdE);
		
		useParsingE = new BooleanFieldEditor(DefaultPrefsSetter.USEPARSING, "&On-the-fly syntax checking with C# compiler", getFieldEditorParent());
		addField(useParsingE);
		useEmonicParsingWithCommand=new BooleanFieldEditor(DefaultPrefsSetter.USEPARSINGWITHCOMMAND, "Override the default parsing command with the following (Experts only!)", getFieldEditorParent());
		addField(useEmonicParsingWithCommand);
		parsingCommandE = new StringFieldEditor(DefaultPrefsSetter.PARSINGCOMMAND, "S&yntax checking command", getFieldEditorParent());
		addField(parsingCommandE);
		useBracketMatchingE = new BooleanFieldEditor(DefaultPrefsSetter.BRACKETMATCHING, "&Bracket matching", getFieldEditorParent());
		addField(useBracketMatchingE);
		bracketMatchingColorE = new ColorFieldEditor(DefaultPrefsSetter.BRACKETMATCHINGCOLOR, "Co&lor for bracket matching", getFieldEditorParent());
	}
	
	public void init(IWorkbench workbench) {
		// nothing to do
	}
	
	/** 
	 * Saves the preferences to the preference store
	 */
	public boolean performOk() {
		tabspaceE.store();
		//cliInformatorCmdE.store();
		useCompletionE.store();
		//useEmonicInformatorCompletionE.store();
		useParsingE.store();
		parsingCommandE.store();
		useBracketMatchingE.store();
		bracketMatchingColorE.store();
		useSpaceTabE.store();
		
		//useEmonicInformatorCompletionWithFramework.store();
		
		
		return super.performOk();
	}
}
