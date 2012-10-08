package org.emonic.base.buildmechanism.buildEditor;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class BuildTextEditorScanner extends RuleBasedScanner {

	public BuildTextEditorScanner(ColorManager manager) {
		IToken procInstr =
			new Token(
				new TextAttribute(
					manager.getColor(IBuildTextEditorColorConstants.PROC_INSTR)));

		IRule[] rules = new IRule[2];
		//Add rule for processing instructions
		rules[0] = new SingleLineRule("<?", "?>", procInstr);
		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule(new BuildTextEditorWhitespaceDetector());

		setRules(rules);
	}
}
