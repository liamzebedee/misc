/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Harald Krapfenbauer, TU Vienna - adaptation for Emonic editor (taken from Java Editor)
 *******************************************************************************/
package org.emonic.base.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.emonic.base.infostructure.BracketInfo;
import org.emonic.base.infostructure.CSharpEditorExamineJob;
import org.emonic.base.infostructure.SimpleTaskInterface;


/**
 * Class that matches pairs of brackets.
 */
public final class CSharpBracketMatcher implements ICharacterPairMatcher,SimpleTaskInterface {

	private static final char[] pairs = {'{', '}', '(', ')', '[', ']', '<', '>'};
	//private IDocument document;
	private int offset;

	private int startPos;
	private int endPos;
	private int anchor;
	private BracketInfo info;
	public CSharpBracketMatcher(CSharpEditorExamineJob job){
		super();
		 job.registerForBracketInfo(this);
	}
	

	/**
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#match(org.eclipse.jface.text.IDocument, int)
	 */
	public IRegion match(IDocument document, int offset) {
		// bail out early
		if (offset < 0)
			return null;
		if (document == null)
			return null;
		this.offset = offset;
		//this.document = document;
		// bail out early if character is no bracket
		char _char = '0';
		try {
			_char = document.getChar(Math.max(offset - 1, 0));
		} catch (BadLocationException e) {
			return null;
		}
		boolean found = false;
		for (int i=0; i<pairs.length; i++) {
			if (pairs[i] == _char) {
				found = true;
				break;
			}
		}
		if (!found)
			return null;
		// Parse the doc in the info.
		if (info == null){
			info=new BracketInfo(document);
		}
				
		// do the matching
		if (matchPairsAt(document) && startPos != endPos)
			return new Region(startPos, endPos - startPos + 1);

		return null;
	}

	
	/**
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#getAnchor()
	 */
	public int getAnchor() {
		return anchor;
	}

	
	/**
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#dispose()
	 */
	public void dispose() {
		clear();
		//document = null;
	}

	
	/**
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#clear()
	 */
	public void clear() {
	}

	
	private boolean matchPairsAt(IDocument document) {

		

		startPos = -1;
		endPos = -1;

		// get the char preceding the start position
		try {
			startPos = Math.max(offset-1, 0);
			char prevChar = document.getChar(startPos);
			endPos = info.getCorresponding(prevChar,startPos);
			anchor=LEFT;
			if (endPos < startPos){
				int tmp = endPos;
				endPos = startPos;
				startPos=tmp;
				anchor=RIGHT;
			}
			if (startPos != endPos){
				return true;
			}
	

		} catch (BadLocationException x) {
		}

		return false;
	}


	public void runTask(Object info) {
		this.info=(BracketInfo)info;
	}

	
}
