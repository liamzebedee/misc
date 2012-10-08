/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */
package org.emonic.base.editors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codecompletion.session.EmptyStringSession;
import org.emonic.base.codecompletion.session.ISession;
import org.emonic.base.codecompletion.session.StandardSession;
import org.emonic.base.codecompletion.session.ThisSession;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.infostructure.CSharpCodeParser;
//import org.emonic.base.infostructure.CodeInformatorComOut;
import org.emonic.base.preferences.DefaultPrefsSetter;


/**
 * CSharp completion processor.
 */
public class CSharpCompletionProcessor implements IContentAssistProcessor {

	private static final int MIN_SIZE = 0x01;
	
	private static final Pattern COMPLETION_PATTERN = Pattern.compile("((?:(?:\\s*\\.\\s*)|\\w|\\(.*?\\)|\\[.*?\\])*)\\z");

	private static final String MSGNOMORECOMPLETIONS = "No completions available!";
	
	private static final String EMPTYCOMPLETION = "";
	
	private static final String THISREGEX = "(this\\.)[\\w.]*";
	
//	private String previousWordToComplete = "";
	
	private int previousDocumentOffset = 0;
	
	private CSharpEditor fTextEditor = null;
	
	private ISession cpSession = null;
	
	private CSharpCursorPositionContext cursorPositionContext = null;
		
	/*
	 * Constructor
	 */
	public CSharpCompletionProcessor(CSharpEditor textEditor) {
		fTextEditor = textEditor;		
		//System.out.println("CREATE COMPLETIONPROPOSALOBJECT " +this.hashCode());
	}
	
//	private CSharpCompletionProcessor() {

		// nothing to do cause, it's only required for OpenDeclaration
//	}
	
	public boolean isCommentedOut(CSharpEditor editor, int documentOffset) {
		// We get the comment list of the callingRoot
		IDocument document = editor.getViewer().getDocument();
		IEditorInput input = editor.getEditorInput();
		CodeElement callingRoot = null;
		if (input instanceof IFileEditorInput) {
			IFile callingFile = ((IFileEditorInput) input).getFile();
			callingRoot = (CodeElement) new CSharpCodeParser(document,
					callingFile.getFullPath().toOSString()).parseDocument();
		} else {
			callingRoot = (CodeElement) new CSharpCodeParser(document, "")
					.parseDocument();
		}
		if (callingRoot != null) {
			List CommentList = callingRoot.getCommentList();
			for (int i = 0; i < CommentList.size(); i++) {
				CodeElement actCom = (CodeElement) CommentList.get(i);
				if (documentOffset >= actCom.getOffset()
						&& documentOffset <= actCom.getOffset()
								+ actCom.getLength()) {
					return true;
				}
			}
		}
		return false;
	}

	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int documentOffset) {

		//check if completion proposal wanted by user
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		boolean usecomps = prefs.getBoolean(DefaultPrefsSetter.USECOMPLETION);
		if(!usecomps){
			return new ICompletionProposal[0];
		}
		
		//get the current document's text
		IDocument document = viewer.getDocument();
		//CodeInformatorComOut thisDocInfo = new CodeInformatorComOut();
		//thisDocInfo.setEditor(fTextEditor);
		String wordToComplete = "";
		LinkedList results = new LinkedList();
		
		IFile ifile = (IFile) this.fTextEditor.getEditorInput().getAdapter(IFile.class);
		//FIXME it is time and resource consuming to parse the whole document until the document offset
		//each time a completion proposal is requested...
		//Idea: 
		CSharpCodeParser parser = new CSharpCodeParser(document, ifile.getLocation().toPortableString());		
		ISourceUnit srcUnit = parser.parseDocument();
		//END FIXME
		this.cursorPositionContext = new CSharpCursorPositionContext(srcUnit, documentOffset);
		
		//We do only sth if we are in a type and not in a comment
		if(!isCommentedOut(fTextEditor, documentOffset) && 
			cursorPositionContext.isInType()){
			
			wordToComplete = calculateWordToComplete(document, documentOffset, srcUnit);
			
			calculateSession(wordToComplete, documentOffset, viewer);
			results = cpSession.getCompletionproposal(wordToComplete,cursorPositionContext,
					documentOffset,srcUnit, ifile);
			
			
		}
		
		ICompletionProposal[] compresult = new ICompletionProposal[results.size()];
		compresult = (ICompletionProposal[]) results.toArray(compresult);
		
		return sortCompletionProposalAlphabetically(compresult);
	}
	
	
	/**
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return new IContextInformation[0];
	}

	
	/**
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[]{'.'};
	}

	
	/**
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	
	/**
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor
	 */
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * org eclipse.jface.text.contentassist.IContentAssistProcessor
	 */
	public String getErrorMessage() {
		return MSGNOMORECOMPLETIONS;
	}
	



	/**
	 * create a session or use old session according to wordToComplete
	 * @param wordToComplete
	 * @return
	 */
	private void calculateSession(String wordToComplete, int documentOffset, ITextViewer viewer) {
		
//		boolean lastDot = wordToComplete.endsWith(".");
		wordToComplete = wordToComplete.trim();
		//create a new session only in the following cases
		//otherwise use the old one
		//if this session
		if(!sessionExists(wordToComplete, documentOffset, previousDocumentOffset)){
			if(wordToComplete.matches(THISREGEX)){
				cpSession = new ThisSession(viewer);
				//DEBUG
				//System.out.println("DEBUG new {{This}} Session for CC");
				//END DEBUg
			}
			//if emptycomp
			else if(wordToComplete.matches(EMPTYCOMPLETION)){
				cpSession = new EmptyStringSession(viewer);
				//DEBUG
				//System.out.println("DEBUG new [[EmptyCompletion]] Session for CC");
				//END DEBUg
			}
			//if standardcompletion (ends with dot or wtc>min size) && (! docOffset+-1==previousDocOffset
			else{
				cpSession = new StandardSession(viewer);
				//DEBUG
				//System.out.println("DEBUG new <<Standard>> Session for CC");
				//END DEBUg
			}			
		}
							
//		this.previousWordToComplete = wordToComplete;
		this.previousDocumentOffset = documentOffset;
	}
	
	

	/**
	 * calculate the word to complete
	 * @param document
	 * @param documentOffset
	 * @param srcUnit
	 * @return
	 */
	private String calculateWordToComplete(IDocument document, int documentOffset, ISourceUnit srcUnit) {
		//FIXME docT may be VERY LARGE in case of e.g. Megaclass
		//we have to reduce size of docT
		//idea => start: last ( or [ or { or <
		//end documentoffset
		String toComplete = "";
		String docT = "";
		try {
			docT = document.get(0, documentOffset);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		docT = removeCommentsForCCRegex(srcUnit, docT);
		
		//Do we have a word to complete?
		Matcher mat = COMPLETION_PATTERN.matcher(docT);
		if(mat.find()){
			toComplete = mat.group(1);
		}
		return toComplete;
	}

	/**
	 * remove comments from the docT string for cc regex
	 * @param srcUnit
	 * @param docT
	 * @return
	 */
	private String removeCommentsForCCRegex(ISourceUnit srcUnit, String docT) {
		// remove comments - they possibly impede calculation the "word to complete"
		List comments = ((CodeElement)srcUnit).getCommentList();
		Iterator i = comments.iterator();
		int deletedChars = 0;
		while (i.hasNext()) {
			CodeElement current = (CodeElement)i.next();
			int start = current.getOffset()-deletedChars;
			int length = current.getLength();
			//comments after the curserposition are neglected...
			if(start <= docT.length()){
				String before = docT.substring(0,start-1 < 0 ? 0 : start-1);
				String after = docT.substring(start+length,docT.length());
				docT = before+after;
				deletedChars+=length;
			}
		}
		return docT;
	}
	
	/**
	 * sort the completion proposal alphabetically
	 */
	private ICompletionProposal[] sortCompletionProposalAlphabetically(
			ICompletionProposal[] unsorted) {
		
		HashMap mapEntries = new HashMap();
		
		//again a comparison if an entry is unique is needed
		//variables in a method may have the same name like fields
		HashSet unique = new HashSet();		
		
		for(int i=0; i < unsorted.length; i++){			
			//add key=displaystring, value = completionproposal
			if(!unique.contains(unsorted[i].getDisplayString())){				
				mapEntries.put(unsorted[i].getDisplayString(), unsorted[i]);
				unique.add(unsorted[i].getDisplayString());
			}
		}
		//fill displayStrings
		String[] displayStrings = new String[unique.size()];
		Iterator iter = unique.iterator();
		int i = 0;
		while(iter.hasNext()){
			displayStrings[i] = (String)iter.next();
			i++;
		}
		
		//sort
		java.util.Arrays.sort(displayStrings,java.text.Collator.getInstance(Locale.ENGLISH));
		
		ICompletionProposal[] sorted = new ICompletionProposal[displayStrings.length];
		//fill sorted completionproposal
		for(int j=0; j<displayStrings.length;j++){
			sorted[j] = (ICompletionProposal) mapEntries.get(displayStrings[j]);			
		}
		
		return sorted;
	}	

	/**
	 * check if session exists
	 * @param wordToComplete
	 * @param documentOffset
	 * @param previousDocumentOffset
	 * @return
	 */
	private boolean sessionExists(String wordToComplete, int documentOffset, int previousDocumentOffset){
		return ((wordToComplete.endsWith(".") ||
				(wordToComplete.length() >= MIN_SIZE)) &&
				 ((documentOffset + 1 == previousDocumentOffset) ||
				  (documentOffset - 1 == previousDocumentOffset)));
	}
	

}