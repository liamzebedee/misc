/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This file is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Harald Krapfenbauer (University of Technology Vienna, ICT) - adaptation
 *       for the Emonic Plug-in
 *******************************************************************************/


package org.emonic.base.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;


/**
 * This class was taken from
 * org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor.BracketInserter
 * and adapted to our needs.
 */
public class CSharpBracketInserter implements VerifyKeyListener {

	
	/**
	 * This class was taken from
	 * org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor
	 * and adapted to our needs.
	 */
	private class ExclusivePositionUpdater implements IPositionUpdater {

		/** The position category. */
		private final String fCategory;

		/**
		 * Creates a new updater for the given <code>category</code>.
		 *
		 * @param category the new category.
		 */
		private ExclusivePositionUpdater(String category) {
			fCategory= category;
		}

		/**
		 * @see org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.text.DocumentEvent)
		 */
		public void update(DocumentEvent event) {

			int eventOffset= event.getOffset();
			int eventOldLength= event.getLength();
			int eventNewLength= event.getText() == null ? 0 : event.getText().length();
			int deltaLength= eventNewLength - eventOldLength;

			try {
				Position[] positions= event.getDocument().getPositions(fCategory);

				for (int i= 0; i != positions.length; i++) {

					Position position= positions[i];

					if (position.isDeleted()) {
						continue;
					}

					int offset= position.getOffset();
					int length= position.getLength();
					int end= offset + length;

					if (offset >= eventOffset + eventOldLength) {
						// position comes
						// after change - shift
						position.setOffset(offset + deltaLength);
					} else if (end <= eventOffset) {
						// position comes way before change -
						// leave alone
					} else if (offset <= eventOffset && end >= eventOffset + eventOldLength) {
						// event completely internal to the position - adjust length
						position.setLength(length + deltaLength);
					} else if (offset < eventOffset) {
						// event extends over end of position - adjust length
						int newEnd= eventOffset;
						position.setLength(newEnd - offset);
					} else if (end > eventOffset + eventOldLength) {
						// event extends from before position into it - adjust offset
						// and length
						// offset becomes end of event, length adjusted accordingly
						int newOffset= eventOffset + eventNewLength;
						position.setOffset(newOffset);
						position.setLength(end - newOffset);
					} else {
						// event consumes the position - delete it
						position.delete();
					}
				}
			} catch (BadPositionCategoryException e) {
				// ignore and return
			}
		}

		/**
		 * Returns the position category.
		 *
		 * @return the position category
		 */
		public String getCategory() {
			return fCategory;
		}
	}
	

	/**
	 * This class was taken from
	 * org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor
	 * and adapted to our needs.
	 */
	private class BracketLevel {
//		int fOffset;
//		int fLength;
		LinkedModeUI fUI;
		Position fFirstPosition;
		Position fSecondPosition;
	}
	

	/**
	 * This class was taken from
	 * org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor
	 * and adapted to our needs.
	 */
	private class ExitPolicy implements IExitPolicy {

		final char fExitCharacter;
		final char fEscapeCharacter;
		final BracketLevel level;

		public ExitPolicy(char exitCharacter, char escapeCharacter, BracketLevel level) {
			fExitCharacter= exitCharacter;
			fEscapeCharacter= escapeCharacter;
			this.level = level;
		}

		/**
		 * @see org.eclipse.jdt.internal.ui.text.link.LinkedPositionUI.ExitPolicy#doExit(org.eclipse.jdt.internal.ui.text.link.LinkedPositionManager, org.eclipse.swt.events.VerifyEvent, int, int)
		 */
		public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length) {

			if (!isMasked(offset) && (event.character == fExitCharacter)) {
				if (level.fFirstPosition.offset > offset || level.fSecondPosition.offset < offset) {
					return null;
				}
				if (level.fSecondPosition.offset == offset && length == 0) {
					// don't enter the character if if its the closing peer
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
				}
			}
			return null;
		}

		/**
		 * Checks whether the character at the specified offset is masked by an escape character.
		 * @param offset The offset in the document.
		 * @return True for "is masked".
		 */
		private boolean isMasked(int offset) {
			IDocument document= sourceViewer.getDocument();
			try {
				return fEscapeCharacter == document.getChar(offset - 1);
			} catch (BadLocationException e) {
			}
			return false;
		}
	}


	/**
	 * Bracket completion enabled
	 */
	private boolean fCloseBrackets= true;
	/**
	 * Quotes completion enabled
	 */
	private boolean fCloseStrings= true;
	private final String CATEGORY = toString();
	private IPositionUpdater fUpdater= new ExclusivePositionUpdater(CATEGORY);
	/**
	 * Reference to the current text editor
	 */
	private final TextEditor textEditor;
	/**
	 * Reference to the current source viewer
	 */
	private final ISourceViewer sourceViewer;

	
	/**
	 * Constructor
	 * @param viewer The editor's source viewer
	 * @param editor The editor
	 */
	public CSharpBracketInserter(TextEditor editor, ISourceViewer viewer) {
		textEditor = editor;
		sourceViewer = viewer;
	}

	/**
	 * Enables or disables bracket completion 
	 */
	public void setCloseBracketsEnabled(boolean enabled) {
		fCloseBrackets= enabled;
	}

	/**
	 * Enables or disables quotes completion (single and double quotes)
	 */
	public void setCloseStringsEnabled(boolean enabled) {
		fCloseStrings= enabled;
	}

 	/**
	 * @see org.eclipse.swt.custom.VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
	 */
	public void verifyKey(VerifyEvent event) {

		// early pruning to slow down normal typing as little as possible
		if (!event.doit) {
			return;
		}
		
		switch (event.character) {
		case '(':
		case '[':
		case '\'':
		case '\"':
			break;
		default:
			return;
		}

		IDocument document= sourceViewer.getDocument();
		final Point selection= sourceViewer.getSelectedRange();
		final int offset= selection.x;
		final int length= selection.y;

		try {
			switch (event.character) {
			case '(':
			case '[':
			if (!fCloseBrackets) {
					return;
				}
				break;
			case '\'':
			case '"':
				if (!fCloseStrings) {
					return;
				}
				break;
			default:
				return;
			}

			// consider escape characters for " and '
			if (document.getChar(offset-1) == getEscapeCharacter(event.character)) {
				return;
			}

			if (!textEditor.validateEditorInputState()) {
				return;
			}

			// do the replacing
			final char character= event.character;
			final char closingCharacter= getPeerCharacter(character);
			final StringBuffer buffer= new StringBuffer();
			buffer.append(character);
			buffer.append(closingCharacter);
			document.replace(offset, length, buffer.toString());
			

			BracketLevel level= new BracketLevel();
			LinkedPositionGroup group= new LinkedPositionGroup();
			group.addPosition(new LinkedPosition(document, offset + 1, 0, LinkedPositionGroup.NO_STOP));
			LinkedModeModel model= new LinkedModeModel();
			model.addGroup(group);
			model.forceInstall();

			//level.fOffset= offset;
			//level.fLength= 2;
			level.fFirstPosition= new Position(offset, 1);
			level.fSecondPosition= new Position(offset + 1, 1);
			
			document.addPositionCategory(CATEGORY);
			document.addPositionUpdater(fUpdater);
			document.addPosition(CATEGORY, level.fFirstPosition);
			document.addPosition(CATEGORY, level.fSecondPosition);
			
			level.fUI= new EditorLinkedModeUI(model, sourceViewer);
			level.fUI.setSimpleMode(true);
			level.fUI.setExitPolicy(new ExitPolicy(closingCharacter, getEscapeCharacter(closingCharacter), level));
			level.fUI.setExitPosition(sourceViewer, offset + 2, 0, Integer.MAX_VALUE);
			level.fUI.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
			level.fUI.enter();

			IRegion newSelection= level.fUI.getSelectedRegion();
			sourceViewer.setSelectedRange(newSelection.getOffset(), newSelection.getLength());

			event.doit= false;

		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Gets the counterpart to these characters: (  [ " '
	 * @param character The character
	 * @return The counterpart character
	 */
	private static char getPeerCharacter(char character) {
		switch (character) {
		case '(':
			return ')';
		case '<':
			return '>';
		case '[':
			return ']';
		case '"':
		case '\'':
			return character;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	
	/**
	 * Gets the escape character for these characters: " '
	 * @param character The character
	 * @return The escape character
	 */
	private static char getEscapeCharacter(char character) {
		switch (character) {
			case '"':
			case '\'':
				return '\\';
			default:
				return 0;
		}
	}
}
