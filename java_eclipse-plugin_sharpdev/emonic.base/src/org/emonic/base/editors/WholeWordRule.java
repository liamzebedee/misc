/*
 * Created on Oct 27, 2005
 * emonic org.emonic.base.editors WholeWordRule.java
 */
package org.emonic.base.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.emonic.base.filemanipulators.CSharpFileManipulator;

/**
 * @author bb
 * This class extends a WordRule: It assures that the words to find are not part of
 * other words but surrounded with spaces and brackets
 */
public class WholeWordRule extends WordRule {

	private StringBuffer fBuffer;
	
	/**
	 * @param detector
	 */
	public WholeWordRule(IWordDetector detector) {
		super(detector);
		fBuffer=new StringBuffer();		
	}
	
	public WholeWordRule(IWordDetector detector,IToken defaultToken){
		super(detector,defaultToken);
		fBuffer=new StringBuffer();
		
	}
	
	// this method is basically copied from the super class,
	// but looks whether the word stands alone
	public IToken evaluate(ICharacterScanner scanner){

		boolean isWordStart = false;
		scanner.unread();
		int b = scanner.read();
		if (b == -1)
			// if read() returns -1, we are at beginning of document, so this could be a word start
			isWordStart = true;
		else
			for (int i = 0; i< CSharpFileManipulator.CSHARPBOUNDS.length;i++)
				if (b == CSharpFileManipulator.CSHARPBOUNDS[i])
					isWordStart = true;
		
		int c = scanner.read();
		isWordStart = fDetector.isWordStart((char)c) && isWordStart;
		
		if (isWordStart) {
			if (fColumn == UNDEFINED || fColumn == scanner.getColumn()-1) {
				fBuffer.setLength(0);
				do {
					fBuffer.append((char)c);
					c = scanner.read();
				} while (c != ICharacterScanner.EOF && fDetector.isWordPart((char)c));
				// Assure the next token is a boundary
				boolean realWordEnd = false;
				scanner.unread();
				if (c == ICharacterScanner.EOF) {
					realWordEnd = true;
				} else {
					// Look ahead
					c = scanner.read();
					scanner.unread();
					if (c == ICharacterScanner.EOF) {
						realWordEnd = true;
					} else {
						for (int i = 0; i< CSharpFileManipulator.CSHARPBOUNDS.length;i++) {
							if (c == CSharpFileManipulator.CSHARPBOUNDS[i])
								realWordEnd=true;
						}
					}
				}
				
				if (realWordEnd) {
					IToken token = (IToken)fWords.get(fBuffer.toString());
					if (token != null)
						return token;
				}
				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);
					
				return fDefaultToken;
			}
		}
		scanner.unread();
		return Token.UNDEFINED;
		
	}
}
