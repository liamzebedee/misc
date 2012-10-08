package org.emonic.base.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.filemanipulators.CSharpFileManipulator;

public class EMonoCSharpCodeTemplatePreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	
	
	// the field editors
	
	private static final String MSGCORRECTAFTERAPPLY = "Correct indentation after applying"; //$NON-NLS-1$
	private static final String MSGASVAR = " as var"; //$NON-NLS-1$
	private static final String MSGUSE = "\nuse "; //$NON-NLS-1$
	private static final String COPYRIGHTLABEL = "Copyright: "; //$NON-NLS-1$
	private static final String DESCRPAGE = "Templates for automatic code generation"; //$NON-NLS-1$
	private static final String MSGCODETEMPLATE = "Code Template "; //$NON-NLS-1$
	private static final String NAMESPACELABEL = "Namespace: ";
	private static final String CLASSLABEL = "Class: ";
	private static final String INTERFACELABEL = "Interface: ";
	private static final String MSGCASTTEMPLATE = "Casting: ";
	private static final String METHODLABEL = "Method/Constructor: ";
	
	MultiStringFieldEditor CopyrightEditor;
	BooleanFieldEditor CorrectAfterCopyright;
	MultiStringFieldEditor NamespaceEditor;
	BooleanFieldEditor CorrectAfterNamespace;
    MultiStringFieldEditor ClassEditor;
	BooleanFieldEditor CorrectAfterClass;
	private MultiStringFieldEditor InterfaceEditor;
	private BooleanFieldEditor CorrectAfterInterface;
	private StringFieldEditor TypeCastEditor;
	private MultiStringFieldEditor MethodEditor;
	private BooleanFieldEditor CorrectAfterMethod;
	
	public EMonoCSharpCodeTemplatePreferencePage() {
		super(GRID);
		setPreferenceStore(EMonoPlugin.getDefault().getPreferenceStore());
		setDescription(DESCRPAGE);
	}
	
	public void createFieldEditors() {
		
		CopyrightEditor = new MultiStringFieldEditor(DefaultPrefsSetter.COPYRIGHTTEMPLATE, MSGCODETEMPLATE + COPYRIGHTLABEL + MSGUSE + CSharpFileManipulator.TEMPLATEVARCOPYRIGHT + MSGASVAR , getFieldEditorParent());
		addField(CopyrightEditor);
		CorrectAfterCopyright = new BooleanFieldEditor(DefaultPrefsSetter.FORMATCOPYRIGHTTEMPLATE, MSGCORRECTAFTERAPPLY, getFieldEditorParent());
		addField(CorrectAfterCopyright);
		NamespaceEditor = new MultiStringFieldEditor(DefaultPrefsSetter.NAMESPACETEMPLATE, MSGCODETEMPLATE + NAMESPACELABEL + MSGUSE + CSharpFileManipulator.TEMPLATEVARNAMESPACE + MSGASVAR , getFieldEditorParent());
		addField(NamespaceEditor);
		CorrectAfterNamespace = new BooleanFieldEditor(DefaultPrefsSetter.FORMATNAMESPACETEMPLATE, MSGCORRECTAFTERAPPLY, getFieldEditorParent());
		addField(CorrectAfterNamespace);
		ClassEditor = new MultiStringFieldEditor(DefaultPrefsSetter.CLASSTEMPLATE, MSGCODETEMPLATE + CLASSLABEL + MSGUSE + CSharpFileManipulator.TEMPLATEVARCLASS + MSGASVAR , getFieldEditorParent());
		addField(ClassEditor);
		CorrectAfterClass = new BooleanFieldEditor(DefaultPrefsSetter.FORMATCLASSTEMPLATE, MSGCORRECTAFTERAPPLY, getFieldEditorParent());
		addField(CorrectAfterClass);
		InterfaceEditor = new MultiStringFieldEditor(DefaultPrefsSetter.INTERFACETEMPLATE, MSGCODETEMPLATE + INTERFACELABEL + MSGUSE + CSharpFileManipulator.TEMPLATEVARINTERFACE + MSGASVAR , getFieldEditorParent());
		addField(InterfaceEditor);
		CorrectAfterInterface = new BooleanFieldEditor(DefaultPrefsSetter.FORMATINTERFACETEMPLATE, MSGCORRECTAFTERAPPLY, getFieldEditorParent());
		addField(CorrectAfterInterface);
		MethodEditor = new MultiStringFieldEditor(DefaultPrefsSetter.METHODTEMPLATE, MSGCODETEMPLATE + METHODLABEL + MSGUSE + CSharpFileManipulator.TEMPLATEVARMODIFIER +','+  CSharpFileManipulator.TEMPLATEVARTYPE+','+  CSharpFileManipulator.TEMPLATEVARMETHODNAME  + MSGASVAR +','+  CSharpFileManipulator.TEMPLATEVARMETHODNAME, getFieldEditorParent());
		addField(MethodEditor);
		CorrectAfterMethod = new BooleanFieldEditor(DefaultPrefsSetter.FORMATMETHODTEMPLATE, MSGCORRECTAFTERAPPLY, getFieldEditorParent());
		addField(CorrectAfterMethod);
		TypeCastEditor = new StringFieldEditor(DefaultPrefsSetter.CASTINGTEMPLATE, MSGCODETEMPLATE +MSGCASTTEMPLATE  + MSGUSE + CSharpFileManipulator.TEMPLATEVARTYPE + MSGASVAR , getFieldEditorParent());
		addField(TypeCastEditor);
	}
	
	public void init(IWorkbench workbench) {
	}
	
	/** 
	 * Saves the preferences to the preference store
	 */
	public boolean performOk() {
		
		CopyrightEditor.store();
		CorrectAfterCopyright.store();
		NamespaceEditor.store();
		CorrectAfterNamespace.store();
		InterfaceEditor.store();
		CorrectAfterInterface.store();
		return super.performOk();
	}
}
