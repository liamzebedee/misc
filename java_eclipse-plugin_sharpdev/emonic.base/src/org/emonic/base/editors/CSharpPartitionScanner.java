package org.emonic.base.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class CSharpPartitionScanner extends RuleBasedPartitionScanner {

	private static final String COMEND = "*/"; //$NON-NLS-1$
	private static final String COMBEGIN = "/*"; //$NON-NLS-1$
	public static final String CSHARP_MULTI_LINE_COMMENT = "__csharp_multiline_comment";

	
	public CSharpPartitionScanner() {
		super();
		
		IToken csharpMultiLineCom = new Token(CSHARP_MULTI_LINE_COMMENT);
		
		List rules = new ArrayList();
		
		rules.add(new MultiLineRule(COMBEGIN, COMEND, csharpMultiLineCom, (char) 0, true));
		
		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
