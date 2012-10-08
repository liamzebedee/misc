/**
 * (c) Copyright IBM Corp. 2000, 2007.
 * All Rights Reserved.
 *
 * Contributors:
 *     IBM Corp.
 *     Bernhard Brem - Adaptation for the Emonic plug-in
 *     Harald Krapfenbauer, TU Vienna - Added automatic curly braces completion
 */
package org.emonic.base.editors;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.preferences.DefaultPrefsSetter;


/**
 * Auto indent strategy sensitive to curly braces { }
 */
public class CSharpAutoIndentStrategy extends DefaultIndentLineAutoEditStrategy {

	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	private static final String RIGHTBRACE = "}"; //$NON-NLS-1$
	private static final String LINEBREAK = System.getProperty("line.separator");
	/**
	 * True if braces completion shall be done
	 */
	private boolean doBracesCompletion =  
		EMonoPlugin.getDefault().getPreferenceStore().getBoolean(DefaultPrefsSetter.BRACESCOMPLETION);
	
	/**
	 * Constructor
	 */
	public CSharpAutoIndentStrategy() {
		super();
	}

	/**
	 * Sets or unsets braces completion
	 */
	public void setBracesCompletion(boolean value) {
		doBracesCompletion = value;
	}
	
	
	/**
	 * @see org.eclipse.jface.text.DefaultIndentLineAutoStrategy#customizeDocumentCommand  
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {

		if (c.doit == false){
			return;
		}
		if (c.length == 0 && c.text != null && endsWithDelimiter(d, c.text)) {
			smartIndentAfterNewLine(d, c);
			return;
		}
		if (RIGHTBRACE.equals(c.text)) { //$NON-NLS-1$
			smartInsertAfterBracket(d, c);
			return;
		}
	}
	
	/**
	 * Returns whether or not the string ends with a line delimiter
	 * @param d the IDocument
	 * @param txt the string
	 */
	private boolean endsWithDelimiter(IDocument d, String txt) {

		String[] delimiters = d.getLegalLineDelimiters();

		for (int i = 0; i < delimiters.length; i++) {
			if (txt.endsWith(delimiters[i])){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the line number of the next bracket after end
	 * @returns the line number of the next matching bracket after end
	 * @param document The document being parsed
	 * @param line The line to start searching back from
	 * @param end The end position to search back from
	 * @param closingBracketIncrease The number of brackets to skip
	 */
	protected int findMatchingOpenBracket(IDocument document, int line, int end, int closingBracketIncrease)
	throws BadLocationException {

		int start = document.getLineOffset(line);
		int brackcount =
			getBracketCount(document, start, end, false)
				- closingBracketIncrease;

		// sum up the brackets counts of each line (closing brackets count negative, 
		// opening positive) until we find a line the brings the count to zero
		while (brackcount < 0) {
			line--;
			if (line < 0) {
				return -1;
			}
			start = document.getLineOffset(line);
			int _end = start + document.getLineLength(line) - 1;
			brackcount += getBracketCount(document, start, _end, false);
		}
		return line;
	}

	
	/**
	 * Returns the bracket count of a section of text. Closing brackets have a value of -1 and 
	 * open brackets have a value of 1.
	 * @returns the bracket count
	 * @param document the document being parsed
	 * @param start the start position for the search
	 * @param end the end position for the search
	 * @param ignoreCloseBrackets whether or not to ignore closing brackets in the count
	 */
	private int getBracketCount(IDocument document, int start, int end, boolean ignoreCloseBrackets)
	throws BadLocationException {
        boolean _ignoreCloseBrackets = ignoreCloseBrackets;
		int begin = start;
		int bracketcount = 0;
		while (begin < end) {
			char curr = document.getChar(begin);
			begin++;
			switch (curr) {
				case '/' :
					if (begin < end) {
						char next = document.getChar(begin);
						if (next == '*') {
							// a comment starts, advance to the comment end
							begin = getCommentEnd(document, begin + 1, end);
						} else if (next == '/') {
							// '//'-comment: nothing to do anymore on this line 
							begin = end;
						}
					}
					break;
				case '*' :
					if (begin < end) {
						char next = document.getChar(begin);
						if (next == '/') {
							// we have been in a comment: forget what we read before
							bracketcount = 0;
							begin++;
						}
					}
					break;
				case '{' :
					bracketcount++;
					_ignoreCloseBrackets = false;
					break;
				case '}' :
					if (!_ignoreCloseBrackets) {
						bracketcount--;
					}
					break;
				case '"' :
				case '\'' :
					begin = getStringEnd(document, begin, end, curr);
					break;
				default :
					}
		}
		return bracketcount;
	}

	
	/**
	 * Returns the end position a comment starting at pos.
	 * @returns the end position a comment starting at pos
	 * @param document - the document being parsed
	 * @param position - the start position for the search
	 * @param end - the end position for the search
	 */
	private int getCommentEnd(IDocument document, int position, int end)
		throws BadLocationException {
		int currentPosition = position;
		while (currentPosition < end) {
			char curr = document.getChar(currentPosition);
			currentPosition++;
			if (curr == '*') {
				if (currentPosition < end
					&& document.getChar(currentPosition) == '/') {
					return currentPosition + 1;
				}
			}
		}
		return end;
	}

	/**
	 * Returns the String at line with the leading whitespace removed.
	 * @returns the String at line with the leading whitespace removed.
	 * @param document - the document being parsed
	 * @param line - the line being searched
	 */
	protected String getIndentOfLine(IDocument document, int line)
		throws BadLocationException {
		if (line > -1) {
			int start = document.getLineOffset(line);
			int end = start + document.getLineLength(line) - 1;
			int whiteend = findEndOfWhiteSpace(document, start, end);
			return document.get(start, whiteend - start);
		} else {
			return EMPTYSTRING; //$NON-NLS-1$
		}
	}

	/**
	 * Returns the position of the character in the document after position.
	 * @returns the next location of character.
	 * @param document - the document being parsed
	 * @param position - the position to start searching from
	 * @param end - the end of the document
	 * @param character - the character you are trying to match
	 */
	private int getStringEnd(
		IDocument document,
		int position,
		int end,
		char character)
		throws BadLocationException {
		int currentPosition = position;
		while (currentPosition < end) {
			char currentCharacter = document.getChar(currentPosition);
			currentPosition++;
			if (currentCharacter == '\\') {
				// ignore escaped characters
				currentPosition++;
			} else if (currentCharacter == character) {
				return currentPosition;
			}
		}
		return end;
	}

	
	/**
	 * Set the indent of a new line based on the command provided in the supplied document.
	 * @param document - the document being parsed
	 * @param command - the command being performed
	 */
	protected void smartIndentAfterNewLine(IDocument document, DocumentCommand command) {
		
		int docLength = document.getLength();
		if (command.offset == -1 || docLength == 0) {
			return;
		}

		try {
			int p = (command.offset == docLength ? command.offset-1 : command.offset);
			int line = document.getLineOfOffset(p);

			StringBuffer buf = new StringBuffer(command.text);
			//System.out.println("getChar(command.offset) = "+document.getChar(command.offset));
			
			// after the caret is "}"
			if (command.offset < docLength && document.getChar(command.offset) == '}') {
				int indLine = findMatchingOpenBracket(document, line, command.offset, 0);
				if (indLine == -1) {
					indLine = line;
				}
				buf.append(getIndentOfLine(document, indLine));
			} else {
				int start = document.getLineOffset(line);
				int whiteend = findEndOfWhiteSpace(document, start, command.offset);
				// append indenting characters of last line
				buf.append(document.get(start, whiteend - start));
				if (getBracketCount(document, start, command.offset, true) > 0) {
					// Indent as many spaces as specified in preferences
					buf.append(getTabSpace());
				}
				// bracket completion for "{"
				if ((document.getChar(command.offset-1) == '{') && doBracesCompletion
						&& !match('{', '}', document.get().toCharArray())) {
					command.caretOffset = command.offset + buf.length();
					command.shiftsCaret = false;
					buf.append(LINEBREAK);
					buf.append(document.get(start, whiteend - start));
					buf.append(RIGHTBRACE);
				}
			}
			command.text = buf.toString();

		} catch (BadLocationException excp) {
//			excp.printStackTrace();//$NON-NLS-1$
		}
	}

	
	/**
	 * Returns a string consisting of as many spaces as set in the "replace tab by spaces"
	 * preference.
	 * @return the string.
	 */
	protected String getTabSpace(){
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		if (prefs.getBoolean(DefaultPrefsSetter.USESPACEASTABS)) {
			int c= prefs.getInt(DefaultPrefsSetter.TABSPACE);
			if (c == -1){
				c = 3;
			}
			String res = EMPTYSTRING;
			for (int i = 0; i < c; i++) {
				res += ' ';
			}
			return res;
		}
		return "\t"; //$NON-NLS-1$
	}
	
	/**
	 * Set the indent of a bracket based on the command provided in the supplied document.
	 * @param document - the document being parsed
	 * @param command - the command being performed
	 */
	protected void smartInsertAfterBracket(IDocument document, DocumentCommand command) {
		
		//System.out.println("smartInsert!, " + command.text + " , " + command.offset + " , ");
		if (command.offset == -1 || document.getLength() == 0) {
			return;
		}

		try {
			int p =
				(command.offset == document.getLength()
					? command.offset - 1
					: command.offset);
			int line = document.getLineOfOffset(p);
			int start = document.getLineOffset(line);
			int whiteend = findEndOfWhiteSpace(document, start, command.offset);

			// shift only when line does not contain any text up to the closing bracket
			if (whiteend == command.offset) {
				// evaluate the line with the opening bracket that matches out closing bracket
				int indLine =
					findMatchingOpenBracket(document, line, command.offset, 1);
				if (indLine != -1 && indLine != line) {
					// take the indent of the found line
					StringBuffer replaceText =
						new StringBuffer(getIndentOfLine(document, indLine));
					// add the rest of the current line including the just added close bracket
					replaceText.append(
						document.get(whiteend, command.offset - whiteend));
					replaceText.append(command.text);
					// modify document command
					command.length = command.offset - start;
					command.offset = start;
					command.text = replaceText.toString();
				}
			}
		} catch (BadLocationException excp) {
			excp.printStackTrace();
		}
	}

	private static boolean match(char match, char end, char[] ch) {
		int count = 0;
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == '\'') {
				for (int j = i + 1; j < ch.length; j++) {
					if (ch[j] == '\r' || ch[j] == '\n' || (ch[j] == '\'' && ch[j - 1] != '\\')) {
						i = j;
						break;
					}
				}
			} else if (ch[i] == '"') {
				for (int j = i + 1; j < ch.length; j++) {
					if (ch[j] == '\r' || ch[j] == '\n' || (ch[j] == '"' && ch[j - 1] != '\\')) {
						i = j;
						break;	
					}
				}
			} else if (ch[i] == match) {
				count++;
			} else if (ch[i] == end) {
				count--;
			}
		}
		return count == 0;
	}
}
