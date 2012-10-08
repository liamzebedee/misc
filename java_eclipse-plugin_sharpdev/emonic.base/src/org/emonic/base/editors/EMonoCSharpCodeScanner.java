package org.emonic.base.editors;

import java.util.ArrayList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.filemanipulators.CSharpFileManipulator;
import org.emonic.base.preferences.DefaultPrefsSetter;

/**
 * Creates the rules for the C# code scanner.
 */
public class EMonoCSharpCodeScanner extends RuleBasedScanner {

	// configurable tokens
	private Token string;
	private Token comment;
	private Token keyword;
	private Token doc;
	private ColorManager manager;

	/**
	 * Default constructor.
	 * @param manager The color manager used by the C# plugin.
	 */
	public EMonoCSharpCodeScanner(ColorManager manager) {

		// remember the color manager
		this.manager = manager; 
		IPreferenceStore store = EMonoPlugin.getDefault().getPreferenceStore();
		
		string = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.STRINGCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.STRINGBOLD) == true ? 
						org.eclipse.swt.SWT.BOLD : 
							org.eclipse.swt.SWT.NORMAL));
		
		comment = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.COMMENTCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.COMMENTBOLD) == true ?
						org.eclipse.swt.SWT.BOLD :
							org.eclipse.swt.SWT.NORMAL));
				
		keyword = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.KEYWORDCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.KEYWORDBOLD) == true ?
						org.eclipse.swt.SWT.BOLD :
							org.eclipse.swt.SWT.NORMAL));
		
		doc = new Token(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.DOCCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.DOCBOLD) == true ?
						org.eclipse.swt.SWT.BOLD :
							org.eclipse.swt.SWT.NORMAL));


		// default token; not configurable
		Token dflt = new Token(new TextAttribute(manager.getColor(IEMonoColorConstants.DEFAULT)));
					
		setDefaultReturnToken(dflt);
		ArrayList<IRule> rules = new ArrayList<IRule>();
		// Rule for documentation
		rules.add(new EndOfLineRule("///", doc));
		// Add rule for single line comments.
        rules.add(new EndOfLineRule("//", comment));
		// Add rule for double quotes
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		// Add a rule for single quotes
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		// Add syntax highlighting for the keywords
        WordRule wordRule = new WholeWordRule(new EMonoCSharpWordDetector(), dflt);
        String[] kwds = CSharpFileManipulator.getKeywords();
	    for (int i=0; i<kwds.length; i++)
	    	wordRule.addWord(kwds[i], keyword);
	    rules.add(wordRule);
		IRule[] res = new IRule[rules.size()];
        rules.toArray(res);
        setRules(res);
	}
	
	
	/**
	 * Updates the text attributes of Tokens used by the scanner.
	 *
	 */
	public void updateColors()
	{
		IPreferenceStore store = EMonoPlugin.getDefault().getPreferenceStore();
		
		string.setData(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.STRINGCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.STRINGBOLD) == true ? 
						org.eclipse.swt.SWT.BOLD : 
							org.eclipse.swt.SWT.NORMAL));
		
		comment.setData(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.COMMENTCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.COMMENTBOLD) == true ?
						org.eclipse.swt.SWT.BOLD :
							org.eclipse.swt.SWT.NORMAL));
				
		keyword.setData(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.KEYWORDCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.KEYWORDBOLD) == true ?
						org.eclipse.swt.SWT.BOLD :
							org.eclipse.swt.SWT.NORMAL));
		
		doc.setData(new TextAttribute(
				manager.getColor(PreferenceConverter.getColor(store,DefaultPrefsSetter.DOCCOLOR)),
				null,
				store.getBoolean(DefaultPrefsSetter.DOCBOLD) == true ?
						org.eclipse.swt.SWT.BOLD :
							org.eclipse.swt.SWT.NORMAL));

		
	}
}
