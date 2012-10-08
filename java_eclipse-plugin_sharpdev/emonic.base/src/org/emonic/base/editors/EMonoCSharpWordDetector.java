/*
 * Created on May 12, 2005
 * emonic org.emonic.base.editors EMonoCSharpWordDetector.java
 */
package org.emonic.base.editors;

import org.eclipse.jface.text.rules.IWordDetector;
import org.emonic.base.filemanipulators.CSharpFileManipulator;

/**
 * @author bb
 *
 */
public class EMonoCSharpWordDetector implements IWordDetector {

	/**
	 * 
	 */
	public EMonoCSharpWordDetector() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c) {
		String[] kwds = CSharpFileManipulator.getKeywords();
			for (int i = 0; i < kwds.length; i++) {
				//System.out.println(kwds[i]);
				if (kwds[i].charAt(0) == c)
					return true;
			}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c) {
		String[] kwds = CSharpFileManipulator.getKeywords();
			for (int i = 0; i < kwds.length; i++) {
				if (kwds[i].indexOf(c) != -1)
					return true;
			}	
		return false;
	}

}
