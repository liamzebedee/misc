/*****************************************************************************************************
 * Created on 15.06.2007
 * emonic.base org.emonic.base.infostructure CSharpCodeParser.java
 * Copyright (c) 2007 emonic.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Bernhard Brem - initial implementation
 *   Harald Krapfenbauer, TU Vienna - Changed parseDocument() to take a String instead of an IFile
 *   HK, Dominik Ertl, TU Vienna - Improved parsing of modifiers
 **************************************************************************************************/
package org.emonic.base.infostructure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.filemanipulators.CSharpFileManipulator;
import org.emonic.base.filemanipulators.FileDocument;


/**
 * This class is responsible for parsing the C# code.
 */
public class CSharpCodeParser implements IAbstractParser {
	private static final Pattern USING_CONDENSED_REGEXP_PATTERN = Pattern.compile("using\\s+([\\w]+)\\s*=\\s*(\\w+[\\s*\\.\\w+]*)\\s*;"); //$NON-NLS-1$
	private static final Pattern USING_REGEXP_PATTERN = Pattern.compile("using\\s+(\\w+[\\s*\\.\\w+]*)\\s*;"); //$NON-NLS-1$
	private static final Pattern CLASS_REGEXP_PATTERN = Pattern.compile("class\\s+([\\w_]+)(\\s*<\\s*(\\w+)\\s*>)?\\s*(:\\s*\\w[^{]+)?\\s*\\{"); //$NON-NLS-1$
	private static final Pattern VARIABLE_REGEXP_PATTERN = Pattern.compile("([\\w]+)(\\s*<\\s*[\\w]+[\\s*,\\s*\\w]*\\s*>)?([\\s*\\[\\s*[,\\s*]*\\]]*)\\s+([\\w]+)\\s*(=[^;]+)?;"); //$NON-NLS-1$
	private static final Pattern METHOD_REGEXP_PATTERN = Pattern.compile("([\\w][\\w\\.]*\\[?\\]?)\\s+([\\w][\\w]*)\\s*(\\(.*\\))\\s*([;{])",Pattern.DOTALL | Pattern.MULTILINE); //$NON-NLS-1$
	private static final Pattern PROPERTY_REGEXP_PATTERN = Pattern.compile("([\\w_][\\w\\d_\\.]*\\[?\\]?)\\s+([\\w_][\\w\\d_]*)\\s*\\{"); //$NON-NLS-1$
	private static final Pattern FIELD_REGEXP_PATTERN = Pattern.compile("([\\w]+\\s+)*([\\w]+)(\\s*<\\s*[\\w]+[\\s*,\\s*\\w]*\\s*>)?([\\s*\\[\\s*[,\\s*]\\]]*)\\s+([\\w]+)\\s*(=[^;]+)?;"); //$NON-NLS-1$
	private static final Pattern NAMESPACE_REGEXP_PATTERN = Pattern.compile("^namespace\\s+([^\\s]+)\\s*\\{"); //$NON-NLS-1$
	private static final Pattern INTERFACE_REGEXP_PATTERN = Pattern.compile("interface\\s+([\\w_]+)\\s*(:\\s*\\w[^{]+)?\\s*\\{"); //$NON-NLS-1$
	private static final Pattern STRUCT_REGEXP_PATTERN = Pattern.compile("struct\\s+(\\w+)\\s*(:\\s*((((\\w+\\.)+\\w+)\\s*,\\s*)*((\\w+\\.)+\\w+)))?\\s*\\{"); //$NON-NLS-1$
	private static final Pattern ENUM_REGEXP_PATTERN = Pattern.compile("enum\\s+(\\S+)\\s*\\{"); //$NON-NLS-1$
	
	private static final Pattern PRIVATE_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)private\\s"); //$NON-NLS-1$
	private static final Pattern PUBLIC_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)public\\s"); //$NON-NLS-1$
	private static final Pattern PROTECTED_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)protected\\s"); //$NON-NLS-1$
	private static final Pattern INTERNAL_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)internal\\s"); //$NON-NLS-1$
	private static final Pattern STATIC_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)static\\s"); //$NON-NLS-1$
	private static final Pattern CONST_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)const\\s"); //$NON-NLS-1$
	private static final Pattern ABSTRACT_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)abstract\\s"); //$NON-NLS-1$
	private static final Pattern VIRTUAL_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)virtual\\s"); //$NON-NLS-1$
	private static final Pattern OVERRIDE_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)override\\s"); //$NON-NLS-1$
	private static final Pattern EVENT_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)event\\s"); //$NON-NLS-1$
	private static final Pattern READONLY_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)readonly\\s"); //$NON-NLS-1$
	private static final Pattern SEALED_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)sealed\\s"); //$NON-NLS-1$
	private static final Pattern UNSAFE_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)unsafe\\s"); //$NON-NLS-1$
	private static final Pattern VOLATILE_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)volatile\\s"); //$NON-NLS-1$
	private static final Pattern NEW_MODIFIER_REGEXP_PATTERN = Pattern.compile("(\\A|\\s)new\\s"); //$NON-NLS-1$
	
	private static final String USINGDECLARATIONS = "using declarations";
    String sourceFile = "";
	private CodeElement actualParsingElement;
	private int curlCounter;
	IDocument Document = null;
	private FindReplaceDocumentAdapter finder;

	/**
	 * Constructor
	 * @param document The document for which this parser instance is generated.
	 */	
	public CSharpCodeParser(IDocument document,String sourceFile){
		Document=document;
		this.sourceFile=sourceFile;
		finder = new FindReplaceDocumentAdapter(Document);
	}
	
	public CSharpCodeParser() {
		// empty constructor for CodeInformator
	}
	
	public void init(File fl){
		Document=new FileDocument(fl.getAbsolutePath(),true);
		this.sourceFile=fl.getAbsolutePath();
		finder = new FindReplaceDocumentAdapter(Document);
	}
	
	private void setOffsetAndStartpoint(CodeElement ele, int offset) {
		ele.setOffset(offset);
		if (ele.getNameOffset() == -1) {
			ele.setNameOffset(offset);
		}
		int LineOfOffset = 0;
		try {
			LineOfOffset = Document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			
			e.printStackTrace();
		}
		int OffsetInLine = 0;
		try {
			OffsetInLine = offset - Document.getLineOffset(LineOfOffset);
			if (OffsetInLine ==0 )
				OffsetInLine = 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		Point sp = new Point(OffsetInLine,LineOfOffset+1);
		ele.setStartPoint(sp);
	}
	
	
	private void setLengthAndEndpoint(CodeElement ele, int length) {
		ele.setLength(length);
		int offset = ele.getOffset()+length;
		int LineOfOffset = 0;
		try {
			if (Document.getLength()>offset && offset > 0)
				LineOfOffset = Document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		int OffsetInLine = 0;
		try {
			OffsetInLine = offset - Document.getLineOffset(LineOfOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		Point sp = new Point(OffsetInLine,LineOfOffset+1);
		ele.setEndPoint(sp);
	}
	
	
	/**
	 * Parses the whole document and generates an AST of CodeElement's.
	 * @param theFile The file to parse
	 * @return The CodeElement representing the root of the AST
	 */
	//public CodeElement parseDocument() {
	public ISourceUnit parseDocument(){
//		DebugTimer timer = new DebugTimer("parseTimer");
//		timer.Start();
		CodeElement root = new CodeElement(null, IDotNetElement.ROOT, "Root", 0, Document.getLength());
		root.setSource(sourceFile);
		actualParsingElement = root;
		// We try to parse the document in one piece without recursions
		int pos = 0; // Position in the document
		int lastpos = -1; // Remember last position
		pos = consumeWhitespace(pos);
		try {
			while (pos < Document.getLength()) {
				if (pos == lastpos) { // increase position if there was no progress
					pos++;
					if (pos >= Document.getLength())
						break;
				}
				lastpos = pos;
				int oldpos = pos;
				// check if comment starts
				pos = checkForCommentedOut(pos,true);
				if (pos != oldpos) {
					pos++;
				}
				// check if string starts
				if (oldpos == pos) {
					pos = checkForString(pos,true);	
				}
				if (oldpos == pos) {
					pos = checkForDirectives(pos);
				}
				if (oldpos == pos)
					pos = checkForOpenCurlies(pos);
				// Get the next token to analyze
				String nextToken = ""; //$NON-NLS-1$
				if (oldpos == pos) {
					try {
						IRegion end = finder.find(pos, "[{};]", true, true, false, true); //$NON-NLS-1$
						if (end != null) {
						nextToken = finder.subSequence(pos, end.getOffset() + end.getLength()).toString();
						nextToken = removeComments(nextToken);
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					if (!nextToken.equals("")) { //$NON-NLS-1$
						if (oldpos == pos)
							pos = checkForUsing(pos, nextToken);
						if (oldpos == pos)
							pos = checkForNamespaceBegin(pos, nextToken);
						if (oldpos == pos)
							pos = checkForClass_begin(pos, nextToken);
						if (oldpos == pos)
							pos = checkForInterface_begin(pos, nextToken);
						if (oldpos == pos)
							pos = checkForField(pos, nextToken);
						if (oldpos == pos)						
							pos = checkForConDestructorStart(pos, nextToken);
						if (oldpos == pos)
							pos = checkForEnumBegin(pos, nextToken);
						if (oldpos == pos)
							pos = checkForStructBegin(pos, nextToken);
						if (oldpos == pos)
							pos = checkForProperty_begin(pos, nextToken);
						if (oldpos == pos)
							pos = checkForMethod_begin(pos, nextToken);
						if (oldpos == pos)
							pos = checkForVariable(pos, nextToken);
					}
				}
				pos = checkForElementEndByCurl(pos);
				
				// if our position never changed, we should consume the current token
				if (pos == oldpos) {
					int length = nextToken.length();
					if (length != 0) {
						int index = nextToken.indexOf('"');
						if (index == -1) {
							switch (nextToken.charAt(length - 1)) {
							case '}':
								pos = checkForElementEndByCurl(pos + length - 1);
								break;
							case '{':
								curlCounter++;
							default:
								pos += length;
								break;
							}
						} else {
							int offset = pos + index;
	 						int newOffset = checkForString(offset, true);
	 						if (offset != newOffset) {
	 							pos = newOffset;
	 						}
						}
					} else {
						pos = consumeToken(pos);						
					}
				}
				// consume succeeding whitespace
				pos = consumeWhitespace(pos);
			}
			// End all open here
			rec_set_end_pos(root, pos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		timer.Stop();
//		timer.Output(true);
		return root;
	}

	private int consumeWhitespace(int pos) {
		// Jump over whitespaces
		try  {
			while (pos < Document.getLength()) {
				if (Character.isWhitespace(Document.getChar(pos))) {
					pos++;
				} else { 
					break;
				}
			}
		} catch (BadLocationException e) {
			pos++;
		}
		return pos;
	}

	private int consumeToken(int pos) {
		int endpos = pos;
		// Jump over whitespaces
		try  {
			char ch = Document.getChar(endpos);
			while (Character.isLetter(ch) || Character.isDigit(ch)) {
				endpos++;
				ch = Document.getChar(endpos);
			}
			while (Character.isWhitespace(Document.getChar(endpos))) {
				endpos++;
			}
		} catch (BadLocationException e) {
			endpos++;
		}
		return endpos;
	}
	
	
	/**
	 * This method spools over strings.
	 * @param pos The position to start searching from.
	 * @return The new position after the string.
	 */
	public int checkForString(int pos, boolean generateCodeElement) {
		int endpos = pos;
		try {
			char actC = Document.getChar(pos);
			if (actC == '\'' || actC == '"') {
				int index = Document.get().indexOf(actC, endpos + 1);
				while (index != -1) {
					if (Document.getChar(index - 1) != '\\') {
						endpos = index + 1;
						break;
					}
					index = Document.get().indexOf(actC, index + 1);
				}
				
				if (Document.getLineOfOffset(pos) != Document.getLineOfOffset(endpos)) {
					return pos;
				}
				
				if (generateCodeElement) {
					CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.STRING);
					com.setElementName(Document.get(pos, endpos-pos));
					com.setSource(sourceFile);
					setOffsetAndStartpoint(com,pos);
					setLengthAndEndpoint(com,endpos-pos);
					actualParsingElement.addChild(com);
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return endpos;
	}
	
	public int checkForDirectives(int pos) {
		try {
			int length = Document.getLength();
			char ch = Document.getChar(pos);
			while (Character.isWhitespace(ch)) {
				pos++;
				if (pos == length) {
					return pos;
				}
				ch = Document.getChar(pos);
			}
			
			if (ch == '#') {
				int line = Document.getLineOfOffset(pos);
				if (line < Document.getNumberOfLines() - 1) {
					return Document.getLineOffset(line + 1);
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * @param pos
	 * @param nextToken
	 * @return
	 */
	private int checkForVariable(int pos, String nextToken) {
		// Starts a local variable here
		// Only in a method or a property
		switch (actualParsingElement.getElementType()) {
		case IDotNetElement.CONSTRUCTOR:
		case IDotNetElement.DESTRUCTOR:
		case IDotNetElement.METHOD:
		case IDotNetElement.PROPERTY:
			Matcher mat = VARIABLE_REGEXP_PATTERN.matcher(nextToken);
			if (mat.find()) {
				int endpos = pos;
				//System.out.println("Found var in " + nextToken);
				String type = mat.group(1);
				String parameter = mat.group(2);
				String array = mat.group(3);
				if (parameter != null) {
					StringBuffer buffer = new StringBuffer(type);
					synchronized (buffer) {
						int sIndex = parameter.indexOf('<');
						int eIndex = parameter.lastIndexOf('>');
						buffer.append('<');
						char[] ch = parameter.substring(sIndex + 1, eIndex).trim().toCharArray();
						for (int i = 0; i < ch.length; i++) {
							if (ch[i] == ',') {
								buffer.append(", ");
							} else if (!Character.isWhitespace(ch[i])) {
								buffer.append(ch[i]);
							}
						}
						buffer.append('>');
						if (array != null) {
								ch = array.toCharArray();
								for (int i = 0; i < ch.length; i++) {
									if (!Character.isWhitespace(ch[i])) {
										buffer.append(ch[i]);
									}
								}
								type = buffer.toString();
						} 
						type = buffer.toString();
					}
				} else if (array != null) {
					StringBuffer buffer = new StringBuffer(type);
					synchronized (buffer) {
						char[] ch = array.toCharArray();
						for (int i = 0; i < ch.length; i++) {
							if (!Character.isWhitespace(ch[i])) {
								buffer.append(ch[i]);
							}
						}
						type = buffer.toString();
					}
				} 
				String name = mat.group(4);
				
				// Really a type or a keyword like e.g. return?
				boolean iskwd = (name == "null");
				for (int i = 0; i < CSharpFileManipulator.KEYWORDS_NO_TYPES.length; i++) {
					if (type.equals(CSharpFileManipulator.KEYWORDS_NO_TYPES[i])) {
						iskwd = true;
						break;
					}
				}
				if (!iskwd) {
					endpos = pos + mat.end();
					CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.VARIABLE);
					com.setTypeSignature(type);
					setOffsetAndStartpoint(com,pos+mat.start());
					com.setNameOffset(pos+mat.start(4));
					setLengthAndEndpoint(com,-1);
					com.setElementName(name);
					com.setSource(sourceFile);
					actualParsingElement.addChild(com);
//					DebugUtil.DebugPrint("Adding variable: "
//							+ com.getElementName() + " at: " + com.getOffset(),
//							debugScan);
				} else {
					// Jump over the offending word
					endpos = pos + type.length();
				}
				return endpos;
			}	
		}
		return pos;
	}

	/**
	 * Search begin of a method
	 * @param pos
	 * @param nextToken
	 * @return
	 */
	private int checkForMethod_begin(int pos, String nextToken) {
		switch (actualParsingElement.getElementType()) {
		case IDotNetElement.CLASS:
		case IDotNetElement.INTERFACE:
		case IDotNetElement.STRUCT:
			Matcher mat = METHOD_REGEXP_PATTERN.matcher(nextToken);
			if (mat.find()) {
				int endpos = pos;
				boolean isImplemented = true;
//				DebugUtil
//						.DebugPrint("Found method  in " + nextToken, debugScan);
				String type = mat.group(1);
				// Not found if the type is the new keyword: Then it is a initialization
				if (type.equals("new")){
					return endpos;
				}
				String name = mat.group(2);
				String signature = getMethodSignature(mat.group(3));
				endpos = pos + mat.end();
				if (mat.group(4).equals(";")) {
					isImplemented = false;
				} else {
					curlCounter++; // We match the too
				}
				CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.METHOD);
				
				com.setTypeSignature(type);
				int pos0 = setModifiers(com, nextToken);
				if (pos0 == -1){
				   setOffsetAndStartpoint(com,pos+mat.start());
				} else {
					 setOffsetAndStartpoint(com,pos+pos0);
				}
				com.setNameOffset(pos+mat.start(2));
				com.setSignature(signature);
				com.setSource(sourceFile);
				if (isImplemented){
				  setLengthAndEndpoint(com,-1);
				  com.setValidOffset(pos+mat.end()); //+mat.start() previously
				} else {
					setLengthAndEndpoint(com,mat.end());
				}
				com.setElementName(name);
				actualParsingElement.addChild(com);
				com.setCurlCounter(curlCounter);
				if (isImplemented) {
				  actualParsingElement = com;
				}
				//curlCounter++;
//				DebugUtil.DebugPrint("Adding method: " + com.getElementName()
//						+ " at: " + com.getOffset(), debugScan);
				return endpos;
			}
		}
		return pos;
	}
	
	private static String getMethodSignature(String signature) {
		if (signature == null) {
			return null;
		}

		signature = signature.substring(1, signature.length() - 1);
		StringBuffer buffer = new StringBuffer();
		synchronized (buffer) {
			buffer.append('(');
			char[] ch = signature.trim().toCharArray();
			for (int i = 0; i < ch.length; i++) {
				if (Character.isWhitespace(ch[i])) {
					if (!Character.isWhitespace(buffer.charAt(buffer.length() - 1))) {
						buffer.append(' ');	
					}
				} else {
					buffer.append(ch[i]);
				}
			}
			buffer.append(')');
			return buffer.toString();
		}
	}

	/**
	 * @param pos
	 * @param nextToken
	 * @return
	 */
	private int checkForProperty_begin(int pos, String nextToken) {
		// Parent of a property can only be a class or a interface!
		switch (actualParsingElement.getElementType()) {
		case IDotNetElement.CLASS:
		case IDotNetElement.INTERFACE:
			Matcher mat = PROPERTY_REGEXP_PATTERN.matcher(nextToken);
			if (mat.find() && !mat.group(1).equals("class")) {
				int endpos = pos;
//				DebugUtil.DebugPrint("Found property in " + nextToken,
//						debugScan);
				String type = mat.group(1);
				String name = mat.group(2);
				endpos = pos + mat.end();
				curlCounter++; // we match the { too
				CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.PROPERTY);
				com.setTypeSignature(type);
				int pos0 = setModifiers(com, nextToken);
				if (pos0 == -1){
				   setOffsetAndStartpoint(com,pos+mat.start());
				} else {
					 setOffsetAndStartpoint(com,pos+pos0);
				}
				com.setNameOffset(pos+mat.start(2));
				setLengthAndEndpoint(com,-1);
				com.setValidOffset(pos+mat.end());
				com.setElementName(name);
				com.setSource(sourceFile);
				actualParsingElement.addChild(com);
				com.setCurlCounter(curlCounter);
				actualParsingElement = com;
				return endpos;
			}
		}
		return pos;
	}

	private int checkForConDestructorStart(int pos, String nextToken) {
		int endpos = pos;

		String regExp = "(?:\\A|\\s)(~?)(" + actualParsingElement.getElementName()
		+ ")\\s*?(\\(.*?\\))\\s*(:\\s*base.*?)?\\s*\\{";
		Pattern sep = Pattern.compile(regExp,Pattern.DOTALL | Pattern.MULTILINE);

		Matcher mat = sep.matcher(nextToken);

		if (mat.find()) {
//			DebugUtil.DebugPrint("Found constructor  in " + nextToken,
//			debugScan);
			String name = actualParsingElement.getElementName();
			String signature = getMethodSignature(mat.group(3));
			endpos = pos + mat.end();
			curlCounter++; // We match the too
			CodeElement com = new CodeElement(actualParsingElement,
					mat.group(1).equals("") ? IDotNetElement.CONSTRUCTOR
							: IDotNetElement.DESTRUCTOR);
			com.setNameOffset(pos + mat.start(2));
			com.setSignature(signature);
			com.setAccessType("");
			setLengthAndEndpoint(com,-1);
			com.setValidOffset(pos+mat.end());
			com.setElementName(name);
			com.setSource(sourceFile);
			actualParsingElement.addChild(com);
			int pos0 = setModifiers(com, nextToken);
			if (pos0 == -1) {
				setOffsetAndStartpoint(com,pos+mat.start());
			} else {
				setOffsetAndStartpoint(com,pos+pos0);
			}
			com.setCurlCounter(curlCounter);
			actualParsingElement = com;
//			DebugUtil.DebugPrint("Adding constructor: " + com.getElementName()
//			+ " at: " + com.getOffset(), debugScan);
		}
		return endpos;
	}

	/**
	 * @param pos
	 * @param nextToken
	 * @return
	 */
	private int checkForField(int pos, String nextToken) {
		switch (actualParsingElement.getElementType()) {
		case IDotNetElement.CLASS:
		case IDotNetElement.STRUCT:
			Matcher mat = FIELD_REGEXP_PATTERN.matcher(nextToken);
			if (mat.find()) {
				int endpos = pos;
				String type = mat.group(2);
				String parameter = mat.group(3);
				String array = mat.group(4);
				
				if (parameter != null) {
					StringBuffer buffer = new StringBuffer(type);
					synchronized (buffer) {
						int sIndex = parameter.indexOf('<');
						int eIndex = parameter.lastIndexOf('>');
						buffer.append('<');
						char[] ch = parameter.substring(sIndex + 1, eIndex).trim().toCharArray();
						for (int i = 0; i < ch.length; i++) {
							if (ch[i] == ',') {
								buffer.append(", ");
							} else if (!Character.isWhitespace(ch[i])) {
								buffer.append(ch[i]);
							}
						}
						buffer.append('>');
						
						if (array != null) {
							ch = array.toCharArray();
							for (int i = 0; i < ch.length; i++) {
								if (!Character.isWhitespace(ch[i])) {
									buffer.append(ch[i]);
								}
							}
						}
						
						type = buffer.toString();
					}
				} else if (array != null) {
					StringBuffer buffer = new StringBuffer(type);
					synchronized (buffer) {
						char[] ch = array.toCharArray();
						for (int i = 0; i < ch.length; i++) {
							if (!Character.isWhitespace(ch[i])) {
								buffer.append(ch[i]);
							}
						}
						type = buffer.toString();	
					}
				}
				
				String name = mat.group(5);
				endpos = pos + mat.end();
				CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.FIELD);
				com.setTypeSignature(type);
				int pos0 = setModifiers(com, nextToken);
				if (pos0 == -1){
				   setOffsetAndStartpoint(com,pos+mat.start());
				} else {
					 setOffsetAndStartpoint(com,pos+pos0);
				}
				com.setNameOffset(pos+mat.start(5));
				setLengthAndEndpoint(com,endpos - pos);
				com.setElementName(name);
				com.setSource(sourceFile);
				actualParsingElement.addChild(com);
//				DebugUtil.DebugPrint("Adding field: " + com.getElementName()
//						+ " at: " + com.getOffset(), debugScan);
				return endpos;
			}
		}
		return pos;
	}

	/**
	 *  
	 */
	private int checkForElementEndByCurl(int pos) {
		// Do we have a closing curl?
		char c;
		try {
			c = Document.getChar(pos);
		} catch (BadLocationException e) {
			return pos;
		}
		if (c != '}')
			return pos;
		
		curlCounter--;
		// Are we in the right declaration?
		switch (actualParsingElement.getElementType()) {
		case IDotNetElement.NAMESPACE:
		case IDotNetElement.CLASS:
		case IDotNetElement.METHOD:
		case IDotNetElement.CONSTRUCTOR:
		case IDotNetElement.DESTRUCTOR:
		case IDotNetElement.PROPERTY:
		case IDotNetElement.INTERFACE:
		case IDotNetElement.STRUCT:
		case IDotNetElement.ENUM:
			// Check if we are in the right curl level
			if (curlCounter+1 == actualParsingElement.getCurlCounter()) {
				if (pos > Document.getLength()) {
					// if the length would exceed the document end, set length so that 
					// document end is reached
					setLengthAndEndpoint(actualParsingElement, 
							Document.getLength()-actualParsingElement.getOffset());
					if (actualParsingElement.getValidOffset()!=-1){
						actualParsingElement.setValidLength(Document.getLength() -1-actualParsingElement.getValidOffset());
					}
				} else {
					setLengthAndEndpoint(actualParsingElement, pos + 1 - actualParsingElement.getOffset());
					if (actualParsingElement.getValidOffset() != -1){
						actualParsingElement.setValidLength(pos  - actualParsingElement.getValidOffset());
					}
				}
				// Set this also as the end position of all children
				rec_set_end_pos(actualParsingElement, pos);
				if (actualParsingElement.getParent() != null) {
					actualParsingElement = (CodeElement) actualParsingElement.getParent();
				}
			}
		}
		return pos+1;
	}
	

	/**
	 * @param actualParsingElement2
	 * @param pos
	 */
	private void rec_set_end_pos(CodeElement element, int pos) {
		int lastvalid = pos - 1;
		if (element.getLength() == -1) {
			setLengthAndEndpoint(element,lastvalid - element.getOffset());
		}
		IDotNetElement[] children = element.getChildren();
		for (int i = 0; i < children.length; i++) {
			rec_set_end_pos((CodeElement) children[i], pos);
		}
	}

	/**
	 * @param pos
	 * @param nextToken
	 * @return
	 */
	private int checkForNamespaceBegin(int pos, String nextToken) {
		int endpos = pos;
		Matcher mat = null;
		//	 Starts here a namespace?
		if (nextToken.startsWith("namespace")) {
			mat = NAMESPACE_REGEXP_PATTERN.matcher(nextToken);
			if (mat.find()) {
				String name = mat.group(1);
				endpos = pos + mat.end();
				CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.NAMESPACE);
				setOffsetAndStartpoint(com,pos+mat.start());
				com.setNameOffset(pos+mat.start(1));
				com.setValidOffset(pos+mat.end());
				setLengthAndEndpoint(com,-1);
				com.setElementName(name);
				com.setSource(sourceFile);
				actualParsingElement.addChild(com);
				actualParsingElement = com;
				curlCounter++;
				com.setCurlCounter(curlCounter);
			}
		}
		return endpos;
	}

	/**
	 * @param pos
	 * @param nextToken
	 * @return
	 */
	private int checkForClass_begin(int pos, String nextToken) {
		int endpos = pos;
		Matcher mat = CLASS_REGEXP_PATTERN.matcher(nextToken);
		if (mat.find()) {
			curlCounter++;
			CodeElement com = new CodeElement(actualParsingElement ,IDotNetElement.CLASS);
			//setOffsetAndStartpoint(com,pos+mat.start());
			setLengthAndEndpoint(com,-1);
			String name = mat.group(1);
			String template = mat.group(3);
			if (template!= null) {
				name += '<' + template + '>';
			}
			
			com.setElementName(name);
			com.setNameOffset(pos+mat.start(1));
			//com.setValidOffset(pos + nextToken.length() + 1);
			com.setValidOffset(pos + nextToken.length());
			int pos0 = setModifiers(com, nextToken);
			if (pos0 == -1){
			   setOffsetAndStartpoint(com,pos+mat.start());
			} else {
				 setOffsetAndStartpoint(com,pos+pos0);
			}
			com.setCurlCounter(curlCounter);
			com.setSource(sourceFile);
			actualParsingElement.addChild(com);
			actualParsingElement = com;
			
			String derived = mat.group(4);
			
			if (derived != null) {
				if (derived.indexOf(':')!=-1){
					derived=derived.substring(derived.indexOf(':')+1);
				}
				com.setDerived(derived);
			}
			
			endpos = pos + mat.end();

		}

		return endpos;
	}

	private int checkForInterface_begin(int pos, String nextToken) {
		int endpos = pos;
		Matcher mat = INTERFACE_REGEXP_PATTERN.matcher(nextToken);
		if (mat.find()) {
			curlCounter++;
			CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.INTERFACE);
			int pos0 = setModifiers(com, nextToken);
			if (pos0 == -1){
			   setOffsetAndStartpoint(com,pos+mat.start());
			} else {
				 setOffsetAndStartpoint(com,pos+pos0);
			}
			com.setNameOffset(pos+mat.start(1));
			com.setValidOffset(pos+mat.end());
			setLengthAndEndpoint(com,-1);
			com.setCurlCounter(curlCounter);
			com.setSource(sourceFile);
			actualParsingElement.addChild(com);
			actualParsingElement = com;
			String name = mat.group(1);
			com.setElementName(name);
			
			String derived = mat.group(2);
			if (derived != null) {
				if (derived.indexOf(':')!=-1){
					derived=derived.substring(derived.indexOf(':')+1);
				}
				com.setDerived(derived);
			}
			
			endpos = pos + mat.end();
		}
		return endpos;
	}
	
	private int checkForStructBegin(int pos, String nextToken){
		int endpos = pos;
		Matcher mat = STRUCT_REGEXP_PATTERN.matcher(nextToken);
		if (mat.find()) {
			curlCounter++;
			CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.STRUCT);
			setOffsetAndStartpoint(com,pos+mat.start());
			com.setNameOffset(pos+mat.start(1));
			setLengthAndEndpoint(com,-1);
			setModifiers(com, nextToken);
			com.setCurlCounter(curlCounter);
			com.setSource(sourceFile);
			actualParsingElement.addChild(com);
			actualParsingElement = com;
			String name = mat.group(1);
			com.setElementName(name);
			endpos = pos + mat.end();
			
			String derived = mat.group(3);
			if (derived != null) {
				com.setDerived(derived);
			}
		}
		return endpos;		
	}

	private int checkForEnumBegin(int pos, String nextToken){
		int endpos = pos;
		Matcher mat = ENUM_REGEXP_PATTERN.matcher(nextToken);
		if (mat.find()) {
			curlCounter++;
			CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.ENUM);
			com.setNameOffset(pos+mat.start(1));
			setLengthAndEndpoint(com,-1);
			int pos0 = setModifiers(com, nextToken);
			if (pos0 == -1){
			   setOffsetAndStartpoint(com,pos+mat.start());
			} else {
				 setOffsetAndStartpoint(com,pos+pos0);
			};
			com.setCurlCounter(curlCounter);
			com.setSource(sourceFile);
			actualParsingElement.addChild(com);
			actualParsingElement = com;
			String name = mat.group(1);
			com.setElementName(name);
			endpos = pos + mat.end();
		}
		return endpos;		
	}

	/**
	 * This method sets the access type of the element (public, private, protected, internal,
	 * protected internal) and the information whether the element is static
	 * 
	 * @param com The code element for which the access type and the static info shall be
	 * determined
	 * @param hasType True if the element has/returns a type (like e.g. "int" in "int a"). Important
	 * for the regexps which scan the code.
	 */
	private int setModifiers(CodeElement com, String search) {
        int offset=Integer.MAX_VALUE;
		// access modifiers
		boolean prot=false, intern=false;

		Matcher mat = PRIVATE_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()) {
			com.setAccessType("private");
			offset=mat.start();
		} else {
			mat = PUBLIC_MODIFIER_REGEXP_PATTERN.matcher(search);
			if (mat.find()) {
				com.setAccessType("public");
				if (mat.start()<offset) offset=mat.start();
			} else {
				mat = PROTECTED_MODIFIER_REGEXP_PATTERN.matcher(search);
				if (mat.find()) {
					com.setAccessType("protected");
					prot = true;
					if (mat.start()<offset) offset=mat.start();
				} else {
					mat = INTERNAL_MODIFIER_REGEXP_PATTERN.matcher(search);
					if (mat.find()) {
						com.setAccessType("internal");
						intern = true;
						if (mat.start()<offset) offset=mat.start();
					}
				}
			}
		}
		if (intern && prot)
			com.setAccessType("protected internal");

		if (com.getAccessType() == null) {
			// Nothing found --> public if the CodeElement is child of namespace
			IDotNetElement parent = com.getParent();
			if (parent != null && ((parent.getElementType() == IDotNetElement.NAMESPACE)
					|| (parent.getElementType() == IDotNetElement.ROOT)))
				com.setAccessType("public");
			else
				com.setAccessType("private");
		}

		// other modifiers
		mat = STATIC_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.staticMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = CONST_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.constMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = ABSTRACT_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.abstractMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = VIRTUAL_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.virtualMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = OVERRIDE_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.overrideMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = EVENT_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.eventMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = READONLY_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.readonlyMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = SEALED_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.sealedMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = UNSAFE_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.unsafeMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		mat = VOLATILE_MODIFIER_REGEXP_PATTERN.matcher(search);
		if (mat.find()){
			com.volatileMod = true;
			if (mat.start()<offset) offset=mat.start();
		}
		
		if (com.getElementType() != IDotNetElement.FIELD) {
			mat = NEW_MODIFIER_REGEXP_PATTERN.matcher(search);
			if (mat.find()){
				com.newMod = true;
				if (mat.start()<offset) offset=mat.start();
			}	
		}
		
		if (offset !=Integer.MAX_VALUE ) return offset;
		return -1;
	}

	
	/**
	 * Checks for an open curly brace.
	 * @param pos The current position
	 * @return The new position
	 */
	private int checkForOpenCurlies(int pos) {
		int endpos = pos;
		try {
			char c = Document.getChar(pos);
			if (c == '{') {
				curlCounter++;
				endpos++;
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return endpos;
	}

	/**
	 * Checks for a using statement. If one is found, a "Using" code element is appended to the tree
	 * and the position is incremented to the position after the statement.
	 * @param pos The starting position
	 * @param nextToken A string with the token that has to be analyzed
	 * @return
	 */
	private int checkForUsing(int pos, String nextToken) {
		int endpos = pos;
		Matcher mat = null;
		if (nextToken.startsWith("using")) {
			String name = "";
			// match a using statement like "using ST = System.Threading"
			// display it in the Outline as "ST (System.Threading)"
			mat = USING_CONDENSED_REGEXP_PATTERN.matcher(nextToken);
			if (mat.find()) {
				StringBuffer buffer = new StringBuffer(mat.group(1));
				synchronized (buffer) {
					buffer.append(" (");
					char[] namespace = mat.group(2).toCharArray();
					for (int i = 0; i < namespace.length; i++) {
						if (!Character.isWhitespace(namespace[i])) {
							buffer.append(namespace[i]);
						}
					}
					buffer.append(')');
					name = buffer.toString();
				}
				
				endpos = pos + mat.end();
			}
			else
			{
				// above one did not match; try normal using statement
				mat = USING_REGEXP_PATTERN.matcher(nextToken);
				if (mat.find()) {
					StringBuffer buffer = new StringBuffer();
					synchronized (buffer) {
						char[] namespace = mat.group(1).toCharArray();
						for (int i = 0; i < namespace.length; i++) {
							if (!Character.isWhitespace(namespace[i])) {
								buffer.append(namespace[i]);
							}
						}
						name = buffer.toString();
					}					
					endpos = pos + mat.end();
				}
			}
			
			// we found one
			if (endpos != pos) {
				// search the parent node of using elements
				IDotNetElement[] rootChildren = actualParsingElement.getChildren();
				boolean usingChildFound = false;
				for (int i=0; i<rootChildren.length; i++) {
					if (rootChildren[i].getElementType() == IDotNetElement.USING_CONTAINER) {
						actualParsingElement = (CodeElement) rootChildren[i];
						usingChildFound = true;
						break;
					}
				}
				if (!usingChildFound) // there is no root child for using's yet - create it
				{
					CodeElement cm = new CodeElement(actualParsingElement, IDotNetElement.USING_CONTAINER);
					setOffsetAndStartpoint(cm,pos+mat.start());
					cm.setNameOffset(cm.getOffset());
//					setLengthAndEndpoint(cm,endpos-pos);
					cm.setElementName(USINGDECLARATIONS);
					cm.setSource(sourceFile);
					actualParsingElement.addChild(cm);
					actualParsingElement = cm;
				}
				
				// add a new Using code element
				CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.USING);
				setOffsetAndStartpoint(com,pos+mat.start());
				com.setNameOffset(pos+mat.start(1));
				setLengthAndEndpoint(com,endpos - pos);
				// set length of parent (= using container)
				setLengthAndEndpoint(actualParsingElement,endpos-actualParsingElement.getOffset());
				com.setElementName(name);
				com.setSource(sourceFile);
				actualParsingElement.addChild(com);
				// let the length of the using container element be as long as all using elements
				setLengthAndEndpoint(actualParsingElement,endpos-actualParsingElement.getOffset()+1);
				
				// set actual to root element
				actualParsingElement = (CodeElement) actualParsingElement.getParent();
			}
		}
		return endpos;
	}

	/**
	 * Checks if a comment starts at given position. If so, the position is incremented to the
	 * position after the comment and a new COMMENT code element is created in the AST if
	 * generatedCodeElement parameter is true.
	 * @param pos The position to start searching.
	 * @param generateCodeElement If a new Comment code element should be generated if a comment
	 * is found.
	 * @return The new position (maybe identical with start position)
	 */
	public int checkForCommentedOut(int pos, boolean generateCodeElement) {
		if (pos > Document.getLength()) {
			return pos;
		}
		int endpos = pos;
		boolean multiLineComment = false;

		try {
			int line = Document.getLineOfOffset(endpos);
			IRegion openComment = Document.getLineInformation(line);
			openComment = new Region(endpos, openComment.getLength() - (endpos - openComment.getOffset()));
			String text = Document.get(openComment.getOffset(), openComment.getLength());
			if (text.startsWith("//")) { //$NON-NLS-1$
				int offset = openComment.getOffset();
				String delimiter = Document.getLineDelimiter(Document.getLineOfOffset(offset));
				if (delimiter != null) {
					openComment = new Region(offset, openComment.getLength() + delimiter.length());	
				}
			} else {
				openComment = null;
			}
			
			if (openComment != null && endpos == openComment.getOffset()) {
				endpos = openComment.getOffset() + openComment.getLength() - 1;
			} else {
				if (Document.getNumberOfLines() == Document.getLineOfOffset(endpos) - 1) {
					// search for single line comments at the end of the document
					openComment = finder.find(endpos, "//.*", true, true, false, true); //$NON-NLS-1$
				} else {
					openComment = null;
				}
				if (openComment != null && endpos == openComment.getOffset()) {
					// could not find comment with newline at end, but without newline, so
					// it must be the document end!!
					endpos = Document.getLength();
				} else {
					int length = Document.getLength();
					text = Document.get(endpos, length - endpos);
					if (text.startsWith("/*")) { //$NON-NLS-1$
						int endIndex = text.indexOf("*/"); //$NON-NLS-1$
						if (endIndex != -1) {
							// add two for the length of '*/'
							endpos = endpos + endIndex + 2;
							multiLineComment = true;
						}
					}
				}
			}
			// create a comment code element if a comment was found
			if (endpos != pos && generateCodeElement) {
				CodeElement com = new CodeElement(actualParsingElement, IDotNetElement.COMMENT);
				setOffsetAndStartpoint(com,pos);
				// add 1 to the length for multi-line comments (for folding)
				if (!multiLineComment || endpos+1 > Document.getLength())
					setLengthAndEndpoint(com,endpos-pos);
				else
					setLengthAndEndpoint(com,endpos-pos+1);
				com.setSource(sourceFile);
				actualParsingElement.addChild(com);
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return endpos;
	}

	
	/**
	 * Searches for comments in the String and replaces it by whitespaces.
	 * @param token The token string
	 * @return The modified token string
	 */
	private String removeComments(String token)
	{
		boolean comment=false;
		char[] charArray = token.toCharArray();
		int pos = 0;
		while (pos < charArray.length-1)
		{
			switch (charArray[pos])
			{
			case '\n':
				comment = false;
				break;
			case '*':
				if (comment && charArray[pos+1]=='/')
				{
					charArray[pos] = charArray[pos+1] = ' ';
					comment = false;
				}
				break;
			case '/':
				if (charArray[pos+1]=='/')
				{
					comment = true;
					break;
				}
				if (charArray[pos+1]=='*')
					comment = true;
				break;
			}
			if (comment)
				charArray[pos]=' ';
			pos++;
		}
		return new String(charArray);
	}

	/**
	 * This parses the actual document to find references to the codeElement. For each hit a 
	 * codeElement will be generated
	 * @param toFind: The codeElement to find
	 * @return the list of CodeElements
	 */
	public CodeElement[] parseDocumentForReferences(CodeElement toFind) {
		if (actualParsingElement==null){
			CodeElement root = new CodeElement(null, IDotNetElement.ROOT, "Root", 0, Document.getLength());
			root.setSource(sourceFile);
			actualParsingElement = root;
		}
		int pos = 0; // Position in the document
		int lastpos = -1; // Remember last position
		List resList = new ArrayList();
		try {
			while (pos < Document.getLength()) {
				if (pos == lastpos) { // increase position if there was no progress
					pos++;
					if (pos >= Document.getLength())
						break;
				}
				lastpos = pos;
				int oldpos = pos;
				// check if comment starts
				pos = checkForCommentedOut(pos,true);
				// check if string starts
				pos = checkForString(pos,true);
				if (oldpos == pos)
					pos = checkForOpenCurlies(pos);
				if (oldpos == pos) {
					// Get the next token to analyze
					String nextToken = "";
					try {
						IRegion end = finder.find(pos, "[{};]", true, true, false, true);
						if (end != null) {
							nextToken = finder.subSequence(pos, end.getOffset() + end.getLength()).toString();
							nextToken = removeComments(nextToken);
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					if (!nextToken.equals("")) {
							
						if (oldpos == pos)
							pos=checkForUnspecReference(pos,nextToken,toFind,resList);
					}
				}

				pos = checkForElementEndByCurl(pos);
				//pos++;
				pos = spoolToNextRelevantForRefs(pos);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

        CodeElement[] res = new CodeElement[resList.size()];
        res= (CodeElement[]) resList.toArray(res);
		return res;
	}

	private int spoolToNextRelevantForRefs(int pos) {
		int pos1 = pos;
		boolean bounderFound = false;
		try{
			while (pos1 < Document.getLength()){
				boolean isBounder = false;
				char act = Document.getChar(pos1);
				// We need to return if the char marks the begin of a quotet area
				if (act == '"' || act == '\''){
					return pos1;
				}
				// We need also to return if we are at the begin of a comment
				if (pos1 +1 < Document.getLength()){
					char act1 = Document.getChar(pos1+1);
					if (act == '/' && (act1 =='/' || act1 == '*')){
						return pos1;
					}
				}
				for (int i = 0; i <CSharpFileManipulator.CSHARPBOUNDS.length;i++){
					if (act == CSharpFileManipulator.CSHARPBOUNDS[i]){
						isBounder = true;
						bounderFound = true;
					} 
				}
				if (bounderFound && ! isBounder){
						return pos1;
				}
				pos1++;
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return pos1;
	}

	private int checkForUnspecReference(int pos, String nextToken, CodeElement toFind, List resList) {
            String limitedToken = "";
            boolean sepFound = false;
            int pos1 = 0;
            while (! sepFound && pos1 < nextToken.length() ){
            	char actC = nextToken.charAt(pos1);
            	for (int i = 0; i < CSharpFileManipulator.CSHARPBOUNDS.length; i++){
            		if (actC == CSharpFileManipulator.CSHARPBOUNDS[i]){
            			sepFound = true;
            		}
            	}
            	if (!sepFound){
            		limitedToken = limitedToken+actC;
            	}
            	pos1++;
            }
            
		
//		    String seps = ".\\s{;}+-=\\(\\)/\\*";
//			String regExp = "([^" + seps + "]+)[" +seps + "]";
//			Pattern sep = Pattern.compile(regExp);
//			Matcher mat = sep.matcher(nextToken);
//			
//			if (mat.find()) {
//				if (mat.group(1).equals(toFind.elementName)){
            if (limitedToken.equals(toFind.getElementName())){
					CodeElement ref = CodeElement.getCopy(toFind);
					ref.setOffset(pos);
					ref.setNameOffset(pos);
					setOffsetAndStartpoint(ref,pos);
					ref.setSource(this.sourceFile);
					resList.add(ref);
//					pos += mat.group(1).length();
					pos += limitedToken.length();
			}
			
		
		return pos;
	}

	public IDotNetElement getRoot() {
		return parseDocument();
	}

	public void setProj(IProject proj) {
		// TODO Auto-generated method stub
		
	}

//	public void init(File fl, CodeInformator informator) {
//		init(fl);
//	}

	
}
