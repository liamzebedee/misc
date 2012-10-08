/***********************************************************************************************************
 * Copyright (c) 2001, 2007 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * *********************************************************************************************************
 * 
 * Created on Feb 18, 2006
 * emonic.base org.emonic.base.FileManipulators CSharpFileManipulator.java
 * 
 * Contributors:
 *   Bernhard Brem - initial implementation
 *   Harald Krapfenbauer, TU Vienna - Added a constructor for creation of the document with a String.
 *        Changed theFile field from IFile to String and modified methods accordingly.
 *        Modified constructors to use the default IDocument implementation
 *          (org.eclipse.jface.text.Document) and not org.emonic.base.FileManipulators.FileDocument.
 *        Added methods createDocument(...) to create a Document from the resource provided.
 *        Fixed bug in toggleComment action (only marked lines are commented now, the cursor
 *          position is not considered)
 **********************************************************************************************************/
package org.emonic.base.filemanipulators;


import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.infostructure.CSharpCodeParser;
import org.emonic.base.preferences.DefaultPrefsSetter;

public class CSharpFileManipulator {

	public static final int spacesForTabsMcs = 8;

	private static final String RIGHTGBRACKET = "}"; //$NON-NLS-1$
	private static final String LEFTGBRACKET = "{"; //$NON-NLS-1$
	private static final String RIGHTRBRACKET = ")"; //$NON-NLS-1$
	private static final String LEFTRBRACKET = "("; //$NON-NLS-1$
	private static final String ONETAB = "\t"; //$NON-NLS-1$
	private static final String AUTOCOMMENTSTART = "//"; //$NON-NLS-1$
	//private static final String MSGCANNOTINSERTCLASS = "Can not insert a class in a non existing namespace"; //$NON-NLS-1$
	//private static final String MSGCANNOTINSERTINTERFACE = "Can not insert the interface in a non existing namespace!";
	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	private static final String ONESPACE = " "; //$NON-NLS-1$

	// debugswitch
	boolean debugScan = false;

	boolean debugTime = true;

	static public char[] CSHARPBOUNDS = {' ','(',')',';','{','}','[',']','\n','\t','.','/','+','-','*','=','\r',','};

	/** @see http://msdn.microsoft.com/en-us/library/x53a06bb.aspx */
	static public final String[] KEYWORDS = {
		/** KEYWORDS */
		"abstract", "as", "base", "break",
		"case", "catch", "checked", "class",
		"const", "continue", "default", "delegate",
		"do", "else", "enum", "event",
		"explicit", "extern", "false", "finally",
		"fixed", "for", "foreach", "goto",
		"if", "implicit", "in", "interface",
		"internal", "is", "lock", "namespace",
		"new", "null", "operator", "out",
		"override", "params", "private", "protected",
		"public", "readonly", "ref", "return",
		"sealed", "sizeof", "stackalloc", "static",
		"struct", "switch", "this", "throw",
		"true", "try", "typeof", "unchecked",
		"unsafe", "using", "virtual", "void",
		"volatile", "while",

		/** TYPES */
		"bool", "byte", "char", "decimal",
		"double", "float", "int", "long",
		"object", "sbyte", "short", "string",
		"uint", "ulong", "ushort",

		/** CONTEXTUAL KEYWORDS */
		"add", "alias", "ascending", "decending",
		"dynamic", "from", "get", "global",
		"group", "into", "join", "let",
		"orderby", "partial", "remove", "select",
		"set", "value", "var", "where",
		"yield"
	};

	static public final String[] KEYWORDS_NO_TYPES = {
		/** KEYWORDS */
		"abstract", "as", "base", "break",
		"case", "catch", "checked", "class",
		"const", "continue", "default", "delegate",
		"do", "else", "enum", "event",
		"explicit", "extern", "false", "finally",
		"fixed", "for", "foreach", "goto",
		"if", "implicit", "in", "interface",
		"internal", "is", "lock", "namespace",
		"new", "null", "operator", "out",
		"override", "params", "private", "protected",
		"public", "readonly", "ref", "return",
		"sealed", "sizeof", "stackalloc", "static",
		"struct", "switch", "this", "throw",
		"true", "try", "typeof", "unchecked",
		"unsafe", "using", "virtual", "void",
		"volatile", "while",

		/** CONTEXTUAL KEYWORDS */
		"add", "alias", "ascending", "decending",
		"dynamic", "from", "get", "global",
		"group", "into", "join", "let",
		"orderby", "partial", "remove", "select",
		"set", "value", "var", "where",
		"yield"
	};

	// These constants are used in the templates for code generation
	public static final String TEMPLATEVARCOPYRIGHT = "%COPYRIGHT%";
	public static final String TEMPLATEVARNAMESPACE = "%NAMESPACE%";
	public static final String TEMPLATEVARCLASS = "%CLASS%";
	public static final String TEMPLATEVARINTERFACE = "%INTERFACE%";
	public static final String TEMPLATEVARTYPE = "%TYPE%";
	public static final String TEMPLATEVARMODIFIER = "%MODIFIER%";
	public static final String TEMPLATEVARMETHODNAME = "%METHOD%";
	public static final String TEMPLATEVARMETRETURN = "%RETURN%";

	private IDocument theDoc = null;

	protected CodeElement root = null;

	protected String fileName = null;
	protected IFile theXFile = null;

	private CSharpCodeParser CsCp = null;

	public static final String BIGCOMMENTSTARTLINE = "/*=============================================================\n"; //$NON-NLS-1$
	public static final String BIGCOMMENTENDLINE = "\n================================================================*/\n"; //$NON-NLS-1$
	public static final String NAMESPACESTART = "\nnamespace "; //$NON-NLS-1$
	public static final String FCTNBODEY = "{\n\n}\n"; //$NON-NLS-1$
	public static final String FCTNBODEYTODO = "{\n// TODO: Auto-generated Stub\n}\n"; //$NON-NLS-1$
	public static final String FCTNBODEYTODORETURN = "{\n// TODO: Auto-generated Stub\n%RETURN%\n}\n"; //$NON-NLS-1$
	public static final String CLASS_START = "class "; //$NON-NLS-1$
	public static final String INTERFACE_START = "\ninterface ";
	public static final String TEMPLATECASTING = " (%TYPE%) ";
	// RegExp to detect a = followed with spaces
	private static final String REGEXPBEFORECASTTYPE = "=(\\s*)$";
	// RegExp to detect (nearly anything)=(spaces or not)(varname)
	private static final String REGEXPAFTERCASTTYPE = "[\\s\\d\\w_<>]*=(\\s*)([\\d\\w_])";
	// Resd-Write
	//    // Read Only
	//	private IDocument RDocument;


	/**
	 * Constructor. An existing IDocument and the corresponding IFile must be provided.
	 * @param doc The IDocument that shall be used.
	 * @param fn The IFile that shall be used.
	 */
	public CSharpFileManipulator(IDocument doc) {
		theDoc = doc;
		parseDocument();
	}


	/**
	 * Constructor. The IDocument will be created from the file provided. In opposite to the
	 * other constructors, the document will not be parsed immediately here!!!
	 * @param fileName The string containing the name of the file which shall be manipulated.
	 */
	public CSharpFileManipulator(String fileName, boolean parse)
	{
		File file = new File(fileName);
		if (file.exists()) {
			this.fileName = fileName;
			// DEPRECATED with new code completion => parsing necessary for external files!
			// [=>don't parse - this is called by the DocumentMarker which needs no parsing]
			if(parse)
				parseDocument();
		} else {
			this.root = new CodeElement(null, IDotNetElement.ROOT);
		}
	}


	/**
	 * Constructor. The IDocument will be created from the file provided.
	 * @param fn The IFile which shall be manipulated.
	 */
	public CSharpFileManipulator(IFile fn) {

		if (fn.exists()) {
			theXFile = fn;
			parseDocument();
		} else {
			this.root=new CodeElement(null, IDotNetElement.ROOT);
		}
	}

	private IDocument getRDoc() {

		if (theDoc != null)
			return theDoc;
		if (theXFile != null)
			return new FileDocument(theXFile,true);
		if (fileName != null)
			return new FileDocument(fileName);
		theDoc = new Document();
		return theDoc;
	}

	private IDocument getWDoc() {

		if (theDoc != null)
			return theDoc;
		if (theXFile != null)
			return new FileDocument(theXFile,false);
		theDoc = new Document();
		return theDoc;
	}


	/**
	 * Parses the document and creates the AST.
	 */
	private void parseDocument() {
		IDocument RDocument = getRDoc();
		if (theXFile != null)
			fileName = theXFile.getFullPath().toOSString();
		if (fileName == null){
			fileName="NA";
		}
		CsCp = new CSharpCodeParser(RDocument,fileName);
		this.root = (CodeElement)CsCp.parseDocument();
	}


	public CodeElement getRoot() {
		return root;
	}



	/**
	 * Add the copyright remark at the begin of a file
	 * @param copyright
	 * @param rewriteDoc 
	 * @return the pos. after the copyright
	 */
	public int addCopyright(String copyright, boolean rewriteDoc) {
		IDocument doc = null;
		if (rewriteDoc){ 
			doc=getWDoc();
		} else {
			doc=getRDoc();
		}
		int length=0;
		if (!EMPTYSTRING.equals( copyright )) {

			String _copyright = processTemplate(DefaultPrefsSetter.COPYRIGHTTEMPLATE, TEMPLATEVARCOPYRIGHT,copyright);
			length=_copyright.length();
			try {
				doc.replace(0, 0, _copyright);
			} catch (BadLocationException e) {
				//Empty document, so set the copyright as content
				doc.set(_copyright);
			}
			parseDocument();
			Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
			if (prefs.getBoolean(DefaultPrefsSetter.FORMATCOPYRIGHTTEMPLATE)){
				this.correctIndentation(0,_copyright.length(),rewriteDoc);
			}
		}
		return length;
	}

	/**
	 * Simple template processing
	 * @param templateconstant The templateconstant of the DefaultPrefSetter class
	 * @param var the variable name
	 * @param value the variable valus
	 * @return
	 */
	private String processTemplate(String templateconstant, String var, String value) {
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		String template = prefs.getString(templateconstant);
		return processTemplateString(template,var,value);
	}

	/**
	 * Process a template which is given not by a constant but by a string
	 * @param template
	 * @param var
	 * @param value
	 * @return
	 */
	private String processTemplateString(String template, String var, String value){
		return template.replaceAll(var,value);
	}

	/**
	 * Adds a code element and all his children at a specified position in the document
	 * @param element The code element
	 * @param pos the position
	 * @return the end point of the new CodeElement in the doc
	 */
	public int AddCodeElement(CodeElement element, int pos,boolean rewriteDoc){
		int resLength=0;
		IDocument doc =getRightDoc(rewriteDoc);
		int orgL = doc.getLength();
		recAddCodeElements(element,pos,rewriteDoc);
		resLength=doc.getLength()-orgL;
		return resLength;
	}

	/**
	 * Add a code element as the last element of a class
	 * @param ele
	 * @param classname
	 * @param rewriteDoc
	 * @return The code element inserted with updated position
	 */
	public CodeElement AddCodeElementAsLastElementOfClass(CodeElement ele, String classname,boolean rewriteDoc){
		CodeElement theClass=recSearchFor(root,classname,IDotNetElement.CLASS);
		if (theClass!=null){
			int pos =theClass.getValidOffset()+theClass.getValidLength();
			// pos is after the closing }, so we insert it before
			//			IDocument doc =getRightDoc(rewriteDoc);
			//			if (pos > doc.getLength()){
			//				pos=doc.getLength()-1; // last char of the doc since it starts with 0
			//			}
			//			try {
			//				while(pos > 0 && doc.getChar(pos) !='}'){
			//					pos --;
			//				}
			//			} catch (BadLocationException e) {
			//				e.printStackTrace();
			//			}
			//			pos --;

			AddCodeElement(ele,pos,rewriteDoc);
			this.parseDocument();
			CodeElement res = null;
			res= recSearchFor(root,ele.getElementName(), ele.getElementType());
			if (res!=null) return res;
		}
		return ele;
	}

	private CodeElement recSearchFor(CodeElement actual, String name, int eletp) {
		if ((actual.getElementType() == eletp) && actual.getElementName().equals(name)){
			return actual;
		}
		IDotNetElement[] children = actual.getChildren();
		for (int i = 0; i < children.length; i++){
			CodeElement tmp = recSearchFor((CodeElement) children[i], name, eletp);
			if ( tmp != null){
				return tmp; 
			}
		}
		return null;
	}


	private void recAddCodeElements(CodeElement element, int pos, boolean rewriteDoc) {
		AddSingleCodeElement(element,pos,rewriteDoc);
		parseDocument();
		CodeElement insertedElement = (CodeElement) root.getChildByNameAndCType(element.getElementName(), element.getElementType());
		if (insertedElement!=null){
			int startOffset=insertedElement.getValidOffset();
			if (startOffset!=-1){
				IDotNetElement[] children = element.getChildren();
				for (int i = children.length - 1; i>=0; i--){
					recAddCodeElements((CodeElement) children[i], startOffset, rewriteDoc);
				}
			}
		}
	}


	public void AddSingleCodeElement(CodeElement element, int pos,boolean rewriteDoc){
		IDocument doc =getRightDoc(rewriteDoc);

		String resultString = EMPTYSTRING;
		switch (element.getElementType()) {
		case IDotNetElement.NAMESPACE:
			resultString+= getNameSpaceString(element);
			break;
		case IDotNetElement.CLASS:
			resultString+= getClassString(element);
			break;
		case IDotNetElement.INTERFACE:
			resultString+= getInterfaceString(element);
			break;
		case IDotNetElement.CONSTRUCTOR:
			resultString+= getConstructorString(element);
			break;
		case IDotNetElement.METHOD:
			resultString+= getMethodString(element);
			break;
		case IDotNetElement.USING:
			resultString+= getUsingString(element);
			break;
		case IDotNetElement.FIELD:
			resultString+= getFieldString(element);
			break;
		}

		//doc.set(doc.get() + resultString);
		try {
			doc.replace(pos,0,resultString);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		if (prefs.getBoolean(DefaultPrefsSetter.FORMATNAMESPACETEMPLATE)){
			this.correctIndentation(pos,doc.getLength()-pos, rewriteDoc);
		}

	}


	private String getFieldString(CodeElement element) {
		return "\n" + element.getAccessType() + " " + element.getTypeSignature() + " " + element.getElementName() + ";";
	}


	private String getUsingString(CodeElement element) {
		return "\nusing " + element.getElementName() + ";" ;
	}


	private String getConstructorString(CodeElement element) {
		String cName = element.getElementName();
		cName += element.getExtendedSignature();
		if (!EMPTYSTRING.equals( cName )) {
			String s = processTemplate(DefaultPrefsSetter.METHODTEMPLATE, TEMPLATEVARMETHODNAME,cName);
			s=processTemplateString(s,TEMPLATEVARTYPE,EMPTYSTRING);
			s=processTemplateString(s,TEMPLATEVARMODIFIER,element.getModifierAsString());
			s=processTemplateString(s,TEMPLATEVARMETRETURN,"");
			s=insertIntoBrackets(s,element.getBody());
			return s;
		}
		return "";
	}

	private String getMethodString(CodeElement element) {
		String cName = element.getElementName();
		cName += element.getExtendedSignature();
		if (!EMPTYSTRING.equals( cName )) {
			String s = "\n" + processTemplate(DefaultPrefsSetter.METHODTEMPLATE, TEMPLATEVARMETHODNAME,cName);
			s=processTemplateString(s,TEMPLATEVARTYPE,element.getTypeSignature());
			s=processTemplateString(s,TEMPLATEVARMODIFIER,element.getModifierAsString());
			if (element.getTypeSignature().equals("void") || element.getTypeSignature().equals("System.Void") ){
				s=processTemplateString(s,TEMPLATEVARMETRETURN,"");
			} else {
				s=processTemplateString(s,TEMPLATEVARMETRETURN,"return null;");
			}
			// Insert method body
			s=insertIntoBrackets(s,element.getBody());

			return s;
		}
		return "";
	}

	private String insertIntoBrackets(String s, String bodey) {
		int place = s.indexOf("{");
		int closing = s.indexOf("}");
		if (! bodey.equals("") && place >= 0 && closing > place ){
			s = s.substring(0, place+1) + "\n" + bodey + "\n" + s.substring(closing,s.length()-1);
		}
		return s;
	}


	private String getInterfaceString(CodeElement element) {
		String iName = element.getElementName();
		String derived = element.getDerived();
		if (!EMPTYSTRING.equals( iName )) {
			String inw=iName;
			if (derived != ""){
				inw+=":"+derived;
			}
			String s = processTemplate(DefaultPrefsSetter.INTERFACETEMPLATE, TEMPLATEVARINTERFACE,inw);
			s=insertIntoBrackets(s,element.getBody());
			return s;
		}
		return "";
	}


	/**
	 * Return the string for a namespace
	 * @param element.getElementName()
	 * @param rewriteDoc 
	 */
	private String getNameSpaceString(IDotNetElement element) {
		if (!EMPTYSTRING.equals( element.getElementName() )) {
			String s = processTemplate(DefaultPrefsSetter.NAMESPACETEMPLATE, TEMPLATEVARNAMESPACE,element.getElementName());
			return s;
		}
		return "";
	}
	/**
	 * Return the string for a class
	 * @param namespaceName
	 * @param rewriteDoc 
	 */
	private String getClassString(CodeElement element) {
		String className = element.getElementName();
		String derived = element.getDerived();
		if (!EMPTYSTRING.equals( className )) {
			String cnw=className;
			if (derived != ""){
				cnw+=":"+derived;
			}
			String s = processTemplate(DefaultPrefsSetter.CLASSTEMPLATE, TEMPLATEVARCLASS,cnw);
			s=processTemplateString(s,TEMPLATEVARMODIFIER,element.getModifierAsString());
			return s;

		}
		return "";
	}

	/**
	 * Get the position by a line
	 * @param line
	 * @param linepos
	 * @return
	 */
	public int getPosInDoc(int line, int linepos, IDocument RDocument) {
		int pos = 0;
		//int ActLine = 1;
		try {
			while (pos < RDocument.getLength()) {
				int ActL = RDocument.getLineOfOffset(pos);
				//System.out.println("Pos: " + pos  + " ActLine: " + ActL + " line: " + line + " offset: " + linepos );
				// ActL starts with 0, for us line 1 is line 1!
				if (ActL + 1 == line) {
					return pos + linepos - 1;
				}
				pos++;
			}
		} catch (Exception e) {
			return pos + linepos;
		}

		return pos;
	}


	/**
	 * Get a line number by the abs. pos in the doc. We start with line number 1.
	 * @param pos
	 * @return
	 */
	public int getLineInDoc(int pos){
		try {
			IDocument RDocument = getRDoc();
			return RDocument.getLineOfOffset(pos)+1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String[] getKeywords() {
		return KEYWORDS;
	}

	/**
	 * Toggle a comment block
	 * @param offset
	 * @param length
	 * @return the new length of the insertion
	 */
	public int[] toggleComment(int offset, int length,boolean rewriteDoc) {
		try {
			int startLine;
			startLine = getWDoc().getLineOfOffset(offset);

			int endLine;
			endLine = getWDoc().getLineOfOffset(Math.max(offset, offset+length-1));
			if (! isAutomaticallyCommentedOut(startLine,endLine)){
				return automaticallyCommentOut(startLine,endLine,rewriteDoc, offset, length);
			} else {
				return automaticallyUncomment(startLine,endLine,rewriteDoc, offset, length);
			}
		} catch (BadLocationException e) {

			e.printStackTrace();
		}// this.getLineInDoc(offset + length);
		return new int[] { offset, length };			
	}

	public int[] automaticallyUncomment(int startLine, int endLine,boolean rewriteDoc, int offset, int length) {
		int _startLine = startLine;
		int _endLine = endLine;
		IDocument doc = null;
		if (rewriteDoc){ 
			doc=getWDoc();
		} else {
			doc=getRDoc();
		}

		if (isAutomaticallyCommentedOut(_startLine,_endLine)){

			if (_startLine > _endLine) {
				int med = _endLine;
				_endLine = _startLine;
				_startLine = med;
			}

			String newLns = "";
			for (int i = _startLine; i <= _endLine; i++){
				try {
					String tLine = doc.get(doc.getLineInformation(i).getOffset(),doc.getLineInformation(i).getLength());
					String delimiter = doc.getLineDelimiter(i);
					if (delimiter != null) {
						tLine += delimiter;
					}
					if (tLine.indexOf(AUTOCOMMENTSTART) == 0){
						tLine=tLine.replaceFirst(AUTOCOMMENTSTART,EMPTYSTRING);
					}
					newLns+=tLine;
				} catch (BadLocationException e) {
					e.printStackTrace();
				}				
			}
			try {
				int firstPos=doc.getLineInformation(_startLine).getOffset();
				IRegion endRegion = doc.getLineInformation(_endLine);
				String delimiter = doc.getLineDelimiter(_endLine);
				int endPos = endRegion.getOffset() + endRegion.getLength();
				if (delimiter != null) {
					endPos += delimiter.length();
				}
				doc.replace(firstPos,endPos-firstPos,newLns);
				int lineDiff = _endLine - _startLine;
				return new int[] { offset - 2, length - (lineDiff * 2) };
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			parseDocument();
		}
		return new int[] { offset, length };
	}


	/**
	 * Comment a block
	 * @param startLine
	 * @param endLine
	 * @param length 
	 */
	public int[] automaticallyCommentOut(int startLine, int endLine,boolean rewriteDoc, int offset, int length) {
		int _startLine = startLine;
		int _endLine = endLine;
		IDocument doc = null;
		if (rewriteDoc){ 
			doc=getWDoc();
		} else {
			doc=getRDoc();
		}
		if (! isAutomaticallyCommentedOut(_startLine,_endLine)){
			if (_startLine > _endLine) {
				int med = _endLine;
				_endLine = _startLine;
				_startLine = med;
			}
			String newLns = "";
			for (int i = _startLine; i <= _endLine; i++){
				try {
					newLns+=AUTOCOMMENTSTART+doc.get(doc.getLineInformation(i).getOffset(),doc.getLineInformation(i).getLength());
					String delimiter = doc.getLineDelimiter(i);
					if (delimiter != null) {
						newLns += delimiter;
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			try {
				int firstPos=doc.getLineInformation(_startLine).getOffset();
				int endPos=doc.getLineInformation(_endLine).getOffset()+doc.getLineInformation(_endLine).getLength();
				String delimiter = doc.getLineDelimiter(_endLine);
				if (delimiter != null) {
					endPos += delimiter.length();
				}
				doc.replace(firstPos,endPos-firstPos,newLns);
				parseDocument();
				int lineDiff = _endLine - _startLine;
				return new int[] { offset + 2, length + (lineDiff * 2) };
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return  new int[] { offset, length };
	}


	/**
	 * Check wether the region is automaticly commentet out
	 * @param startLine
	 * @param endLine
	 * @return
	 */

	public boolean isAutomaticallyCommentedOut(int startLine, int endLine) {
		boolean result = true;
		IDocument doc = getRDoc();

		int _startLine = startLine;
		int _endLine = endLine;
		if (_startLine > _endLine) {
			int med = _endLine;
			_endLine = _startLine;
			_startLine = med;
		}
		int length = doc.getLength();
		for (int i = _startLine; i <= _endLine; i++){
			try {
				int offset = doc.getLineOffset(i);
				if (offset <= length && length <= offset + 1) {
					return false;
				}
				if (! doc.get(offset, 2).startsWith(AUTOCOMMENTSTART)) {
					result = false;
				}
			} catch (BadLocationException e) {
				return result;
			}
		}

		return result;
	}



	/**
	 * Correct the indentation
	 * @param offset
	 * @param length
	 * @param rewriteDoc 
	 */
	public void correctIndentation(int offset, int length, boolean rewriteDoc) {
		IDocument orgdoc = getRightDoc(rewriteDoc);
		// We work on a clone of the document so that we need only one  undo to rewrite all 
		Document doc = new Document();
		doc.set(orgdoc.get());
		int firstLine = this.getLineInDoc(1);
		int startLine = this.getLineInDoc(offset);
		int endLine = this.getLineInDoc(offset + length);
		int lastgbracks = 0;
		// Additional insert if the last line is not finished right
		boolean addInsert = false;
		// We want the indent deep caused by the round ( as far as the location of the ( determines
		// So we have to do this via a stack in which we collect the strings
		ArrayList<String> rbrackStack = new ArrayList<String>();
		for (int actLine = firstLine; actLine<startLine;actLine++){
			// Get the {
			lastgbracks += getGBracksOfLine(actLine,doc);
			// Get the (
			rbrackStack = getRBracksOfLine(actLine,rbrackStack,doc);

		}
		// Loop over all lines we have to indent
		for (int actLine = startLine; actLine<=endLine;actLine++){
			// Get the pos in doc of the first letter of the line
			int newgbracks = lastgbracks + getGBracksOfLine(actLine,doc);
			int newrightbracks = lastgbracks - getRightGBracksOfLine(actLine,doc);
			String insert = EMPTYSTRING;
			// Is the rbrackStack empty, we have to indent according the {
			if (rbrackStack.size() <= 0){ 
				// We indent so far like the status of the { wants it
				int nc = 1;
				if (useSpaces()){
					nc=getIndentSpace();
				}

				int num = lastgbracks * nc;
				if (newrightbracks < lastgbracks) {
					num = newrightbracks * nc;
				}
				if (addInsert) num++;
				for (int i = 0; i < num;i++) {
					if (useSpaces()){
						insert += ONESPACE;
					} else {
						insert += ONETAB;
					}
				}
			} else {
				// rbrackStack is not empty, we indent according to (
				insert = (String) rbrackStack.get(rbrackStack.size()-1);
			}

			// How many spaces do we have
			int p = getPosInDoc(actLine,1,doc);
			try{
				while (p < doc.getLength() && (ONESPACE.equals( doc.get(p,1))|| (ONETAB.equals( doc.get(p,1))) )  ) {
					p++;
				} 
				int stpos = getPosInDoc(actLine,1,doc);
				// We don't replace comments starting there!
				int docpos = CsCp.checkForCommentedOut(stpos,false);
				if (docpos == stpos) {
					doc.replace(stpos,p-stpos,insert);
				}
				// Calculate the brackets of the current line to remember for the next line
				lastgbracks = newgbracks;
				//	Get the (
				rbrackStack = getRBracksOfLine(actLine,rbrackStack,doc);
				// If we have no ( and the current line contains a if or else without a { we want to indent the next line, too
				// Typical: 
				// if (condition)
				//    do-something      
				if (rbrackStack.size()<=0){
					String ln = doc.get(doc.getLineOffset(actLine-1),doc.getLineLength(actLine-1));
					if (ln.indexOf("//") > 0) {
						ln=ln.substring(0,ln.indexOf("//")-1);
					}
					ln=ln.trim();
					if ((ln.lastIndexOf("if") >= 0 && ( ln.lastIndexOf("{") < ln.lastIndexOf("if") && ln.lastIndexOf(";") < ln.lastIndexOf("if") )) ||
							(ln.lastIndexOf("else") >= 0 && (ln.lastIndexOf("{") < ln.lastIndexOf("else") && ln.lastIndexOf(";") < ln.lastIndexOf("else") ) )	){
						addInsert = true;
					} else {
						addInsert = false;
					}

				} else {
					addInsert = false;
				}
			} catch(BadLocationException e) {
				e.printStackTrace();
			}
		}
		// Set the org doc to the new
		orgdoc.set(doc.get());
		this.parseDocument();
	}

	private int getRightGBracksOfLine(int actLine, Document RDocument) {
		int bracks = 0; 
		//IDocument RDocument = getRDoc();
		try{
			// These functions seem to start the line numberations with 0!
			int lineBegin = RDocument.getLineOffset(actLine-1);
			int lineLength = RDocument.getLineLength(actLine-1);
			CSharpCodeParser CsCp1 = new CSharpCodeParser(RDocument,fileName);
			boolean noLeftFound = true;
			for (int i = lineBegin; i < lineBegin+lineLength && i < RDocument.getLength() && noLeftFound;i ++){

				//	CsCp1.parseDocument();
				i=CsCp1.checkForCommentedOut(i,false);
				i = CsCp1.checkForString(i,false);
				if (i < lineBegin+lineLength){
					if (LEFTGBRACKET.equals( RDocument.get(i,1) )) {
						noLeftFound=false;
					}
					if (RIGHTGBRACKET.equals( RDocument.get(i,1) )) {
						bracks ++;
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return bracks;
	}


	/**
	 * @param rewriteDoc
	 * @return
	 */
	private IDocument getRightDoc(boolean rewriteDoc) {
		IDocument doc = null;
		if (rewriteDoc){ 
			doc=getWDoc();
		} else {
			doc=getRDoc();
		}
		return doc;
	}


	/**
	 * Get the stack of indentions according to (
	 * @param actLine
	 * @param rbracketStack 
	 * @param rbracketStack
	 * @return
	 */
	private ArrayList<String> getRBracksOfLine(int actLine, ArrayList<String> rbracketStack, IDocument RDocument) {
		int bracks = 0;
		//IDocument RDocument =  getRDoc();
		try{
			// These functions seem to start the line numberations with 0!
			int lineBegin = RDocument.getLineOffset(actLine-1);
			int lineLength = RDocument.getLineLength(actLine-1);
			CSharpCodeParser CsCp1 = new CSharpCodeParser(RDocument,fileName);
			for (int i = lineBegin; i < lineBegin+lineLength && i < RDocument.getLength();i ++){
				//	CsCp1.parseDocument();
				i = CsCp1.checkForCommentedOut(i,false);
				i = CsCp1.checkForString(i,false);
				if (i < lineBegin+lineLength) {
					if (LEFTRBRACKET.equals( RDocument.get(i,1) )){
						bracks ++;
						// Build the indention string: Replace all non-whitespace-chars with ' '
						String ln = RDocument.get(lineBegin,i-lineBegin);
						ln=ln.replaceAll("\\S"," ");
						// Add a single space to indent one space after the (
						ln+=" ";
						// Put it on the stack
						rbracketStack.add(ln);
					}
					if (RIGHTRBRACKET.equals( RDocument.get(i,1) )) {
						// Remove the last element of the stack
						if (rbracketStack.size()>0){
							rbracketStack.remove(rbracketStack.size()-1);
						}
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return rbracketStack;
	}


	/**
	 * Get the count of the opening or closing {
	 * @param actLine the line to be parsed
	 * @return the difference of the numbers of { or }
	 */
	private int getGBracksOfLine(int actLine, IDocument RDocument) {
		int bracks = 0; 
		//IDocument RDocument = getRDoc();
		try{
			// These functions seem to start the line numberations with 0!
			int lineBegin = RDocument.getLineOffset(actLine-1);
			int lineLength = RDocument.getLineLength(actLine-1);
			CSharpCodeParser CsCp1 = new CSharpCodeParser(RDocument,fileName);
			for (int i = lineBegin; i < lineBegin+lineLength && i < RDocument.getLength();i ++){

				//	CsCp1.parseDocument();
				i=CsCp1.checkForCommentedOut(i,false);
				i = CsCp1.checkForString(i,false);
				if (i < lineBegin+lineLength){
					if (LEFTGBRACKET.equals( RDocument.get(i,1) )) {
						bracks ++;
					}
					if (RIGHTGBRACKET.equals( RDocument.get(i,1) )) {
						bracks --;
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return bracks;
	}

	/**
	 * Return the space
	 * @return
	 */
	private int getIndentSpace() {
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		if (useSpaces()) {
			return  prefs.getInt(DefaultPrefsSetter.TABSPACE);
		}
		return 1;
	}

	private boolean useSpaces(){
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		return (prefs.getBoolean(DefaultPrefsSetter.USESPACEASTABS));
	}

	/**
	 * Insert a cast to a type
	 * @param suggestedType The type in which to cast
	 * @param offset The offset in the doc
	 * @param rewriteDoc Force rewrite
	 */
	public void insertCast(String suggestedType, int offset, boolean rewriteDoc) {
		IDocument doc = getRightDoc(rewriteDoc);
		String s = processTemplate(DefaultPrefsSetter.CASTINGTEMPLATE, TEMPLATEVARTYPE,suggestedType);
		try {
			// We have to react on the fact, that various compilers report the problem in the neighbourhood
			// of the place, not at the place where the cast has to be insertet.
			// So we have to determine the right position here.
			// Say we have something like
			//
			// double bx;
			// int ax  = bx;
			//
			// then the cast error can be reportet either on the begin of int or at the begin of bx
			// We accept anything between.
			//
			// We replace only in one code line
			int lineOfOffset=doc.getLineOfOffset(offset);
			IRegion lineRegion = doc.getLineInformation(lineOfOffset);
			int lineStart=lineRegion.getOffset();
			int lineLength=lineRegion.getLength();
			String lineBeforeOffset=doc.get(lineStart,offset-lineStart);
			String lineAfterOffset=doc.get(offset,lineLength+lineStart-offset);
			String regExpBefore = REGEXPBEFORECASTTYPE;
			Pattern sep = Pattern.compile(regExpBefore);
			Matcher mat = sep.matcher(lineBeforeOffset);
			String resultLine=doc.get(lineStart,lineLength);
			if (mat.find()){
				// The = is in lineBeforeOffset, only seperated by spaces. That is the last
				// position we accept
				// To format the doc right we remove the spaceswhere we insert the cast
				lineBeforeOffset=lineBeforeOffset.replaceAll("\\s+$","");
				lineAfterOffset=lineAfterOffset.replaceAll("^\\s+","");
				resultLine=lineBeforeOffset+s+lineAfterOffset;
			} else {
				// The = is not in lineBeforeOffset, only seperated by spaces.
				// That means it should be in lineAfterOffset at a later pos
				String regExpAfter = REGEXPAFTERCASTTYPE;
				Pattern sepAfter = Pattern.compile(regExpAfter);
				Matcher matAfter = sepAfter.matcher(lineAfterOffset);
				if (matAfter.find()){
					if (matAfter.group(1)!=null){
						resultLine=lineAfterOffset.substring(0,matAfter.start(1));
					} else {
						resultLine=lineAfterOffset.substring(0,matAfter.start(2));
					} 
					resultLine+=s;
					resultLine=lineBeforeOffset + resultLine+ lineAfterOffset.substring(matAfter.start(2));
				}
			}
			doc.replace(lineStart, lineLength,resultLine);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} 

	}

}
