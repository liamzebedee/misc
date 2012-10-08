package sharpdev.util;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.NoneBuildfileManipulator;
import org.emonic.base.codecompletion.AssemblyParserFactory;
import org.emonic.base.codecompletion.IAssemblyParser;
import org.emonic.base.filemanipulators.CSharpFileManipulator;
import org.emonic.base.preferences.PreferencesInjector;

import sharpdev.SharpdevPlugin;

public class DefaultPreferences {
	private static final String NANT_CMD = "NAnt"; //$NON-NLS-1$
	public static final String BUILD_FOLDERS_SRC = "buildFoldersSrcName"; //$NON-NLS-1$
	public static final String BUILD_FOLDERS_BIN = "buildFoldersBinName"; //$NON-NLS-1$
	
	public static final String TABSPACE = "sharpdev.ui.spacesForTab";
	public static final String USESPACEASTABS = "sharpdev.ui.useSpaceAsTab";
	
	public static final String CLIINFORMATORCMD = "cliInformatorCmd";
	public static final String USECOMPLETION = "useCompletion";
	public static final String USEEMONICINFORMATORCOMPLETION = "useEmonicInformatorCompletion";
	public static final String STRINGCOLOR = "stringColor";
	public static final String STRINGBOLD = "stringBold";
	public static final String COMMENTCOLOR = "commentColor";
	public static final String COMMENTBOLD = "commentBold";
	public static final String KEYWORDCOLOR = "keywordColor";
	public static final String KEYWORDBOLD = "keywordBold";
	public static final String DOCCOLOR = "docColor";
	public static final String DOCBOLD = "docBold";
	public static final String USEPARSING = "useParsing";
	public static final String PARSINGCOMMAND = "parsingCommand";
	public static final String BRACESCOMPLETION = "bracesCompletion";
	public static final String QUOTESCOMPLETION = "quotesCompletion";
	public static final String BRACKETCOMPLETION = "bracketCompletion";
	public static final String BRACKETMATCHING = "bracketMatching";
	public static final String BRACKETMATCHINGCOLOR = "bracketMatchingColor";
	public static final String CS_TODO_TAGS = "CS_TODO_TAGS";
	public static final String DEFAULT_CS_TODO_TAGS = "TODO: TODO FIXME XXX";
	public static final String COPYRIGHTTEMPLATE = "copyRightTemplate";
	public static final String FORMATCOPYRIGHTTEMPLATE = "formatCopyRightTemplate";
	public static final String NAMESPACETEMPLATE = "nameSpaceTemplate";
	public static final String FORMATNAMESPACETEMPLATE = "formatNameSpaceTemplate";
	public static final String CLASSTEMPLATE = "classTemplate";
	public static final String FORMATCLASSTEMPLATE = "formatClassTemplate";
	public static final String INTERFACETEMPLATE = "interfaceTemplate";
	public static final String FORMATINTERFACETEMPLATE = "formatInterfaceTemplate";
	public static final String CASTINGTEMPLATE="castingTemplate";
	public static final String METHODTEMPLATE = "methodTemplate";
	public static final String FORMATMETHODTEMPLATE = "formatMethodTemplate";
	public static final String USEEMONICINFORMATORCOMPLETIONWITHCOMMAND = "useEmonicInformatorCompletionWithCommand";
	public static final String USEPARSINGWITHCOMMAND = "useCodeparsingWithCommand";
	public static final String DEFAULTBUILDMECHANISM = "defaultBuildMechanism";

	public static final String BINARYSEARCHMETHOD = "BinarySearchMethod";
	
	public static void initializeDefaults() {
		IPreferenceStore store = SharpdevPlugin.getDefault().getPreferenceStore();
		
		// Build prefs
		store.setDefault(NANT_CMD, "C:\\Program Files\\NAnt\\NAnt.exe");
		store.setDefault(BUILD_FOLDERS_SRC, "src"); //$NON-NLS-1$
		store.setDefault(BUILD_FOLDERS_BIN, "bin"); //$NON-NLS-1$
		
		// Editor prefs
		store.setDefault(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END, true);
		
		store.setDefault(USESPACEASTABS, false);
		store.setDefault(TABSPACE, 4);
		
		store.setDefault(USECOMPLETION,true);
		store.setDefault(USEEMONICINFORMATORCOMPLETION,true);
		store.setDefault(USEPARSING,true);
		store.setDefault(PARSINGCOMMAND, "gmcs --parse");
		store.setDefault(BRACESCOMPLETION,true);
		store.setDefault(QUOTESCOMPLETION,true);
		store.setDefault(BRACKETCOMPLETION, true);
		store.setDefault(BRACKETMATCHING, true);
		store.setDefault(USEEMONICINFORMATORCOMPLETIONWITHCOMMAND,false);
		store.setDefault(USEPARSINGWITHCOMMAND,false);
		store.setDefault(CS_TODO_TAGS, DEFAULT_CS_TODO_TAGS);
		
		// Editor color prefs
		store.setDefault(BRACKETMATCHINGCOLOR, "192,192,192");

		// for color settings use PreferenceConverter, because a color value is not
		// a simple string
		store.setDefault(STRINGCOLOR, "255,0,0");
		store.setDefault(STRINGBOLD,false);
		store.setDefault(COMMENTCOLOR, "0,128,0");
		store.setDefault(COMMENTBOLD,false);
		store.setDefault(KEYWORDCOLOR, "0,0,255");
		store.setDefault(KEYWORDBOLD,true);
		store.setDefault(DOCCOLOR, "150,150,150");
		store.setDefault(DOCBOLD,false);

		PreferencesInjector.setDefaults(store);
	}
}
