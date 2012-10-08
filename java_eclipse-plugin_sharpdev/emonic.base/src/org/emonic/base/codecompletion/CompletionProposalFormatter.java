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

package org.emonic.base.codecompletion;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.emonic.base.codecompletion.session.MemberCompletionProposal;
import org.emonic.base.codecompletion.session.NamespaceCompletionProposal;
import org.emonic.base.codecompletion.session.ParameterCompletionProposal;
import org.emonic.base.codecompletion.session.TypeCompletionProposal;
import org.emonic.base.codehierarchy.IDotNetElement;

public class CompletionProposalFormatter {

	
	private String signature = null;
	private String returnStrOfMethod = null;
	private String replacementString = null;
	private String elementString = null;
	private String typeString = null;
	private String typeWithoutNamespace = null;
	private int idotnetelement;
	private int wordToCompleteFlags;
	private ITextViewer viewer;
	private String assemblyPath = null;
	private String origString = null;
	private String origTypeString = null;
	
	
	public CompletionProposalFormatter(ITextViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * delivers a formatted completion proposal according to the input parameters
	 * for THIS completion
	 */
	public Object getFormattedProposalForThis(
			String wordToComplete, String valueOfListelement,
			int documentOffset, CodeCompletionLabelProvider labelProvider,
			int cpFlags, String assemblyPath) {

		reset();
		this.assemblyPath = assemblyPath;
		//get a substring 
		int wordToCompleteLastDot = wordToComplete.lastIndexOf(".");
		String wordToCompleteTrimmed = "";
		if(wordToCompleteLastDot != -1){
			wordToCompleteTrimmed = wordToComplete.substring(0, wordToCompleteLastDot+1);
		}
		String valueOfListelementSub = valueOfListelement.substring(wordToCompleteLastDot+1);
		
		//parse Elements of String		
		parseMetadataString(valueOfListelement);
		
		//build replacement and elementString
		int idotnetelementbegin = valueOfListelementSub.indexOf(MetadataElements.IDotNetElement); //FIXME: eventuell  + 1
		if(idotnetelementbegin > 0){
			String helpString = valueOfListelementSub.substring(0, idotnetelementbegin - 1);
			elementString = extractElementString(helpString);				
		}
		else{
			replacementString = "ERROR IN REPLACEMENT STRING => CSharpCompletionProposal.java";
		}
				
		StringBuffer displayString = buildDisplayString();
		
		int replacementLength = documentOffset - wordToComplete.length();
		int replacmentOffset = wordToComplete.length(); //0,

		replacementString = elementString;
		int delta = wordToComplete.length() - wordToCompleteTrimmed.length(); //wordToComplete.length() -
		wordToCompleteTrimmed = "this.";
		replacementLength = documentOffset - 5 - delta; // "this."
		replacmentOffset = 5+delta;
		//for empty completion change strings like for this-completion

		CompletionProposalFilter cpFilter = new CompletionProposalFilter();
		//if filter function returns true => completion proposal is not allowed
		//due to rules of code completion
	
		if(!displayString.toString().trim().equals("") && 
		    cpFilter.filter(cpFlags, wordToCompleteFlags, idotnetelement, false,
		    		displayString.toString())){
			
//			  return new CompletionProposal(wordToCompleteTrimmed+replacementString+signature,
//				replacementLength,
//				replacmentOffset, 
//				wordToCompleteTrimmed.length()+replacementString.length()+signature.length(),
//				labelProvider.getImage(idotnetelement, wordToCompleteFlags),
//				displayString.toString(), 
//				null, null);
			  
			  return createProposalObject(wordToCompleteTrimmed+replacementString+signature,
						replacementLength,
						replacmentOffset, 
						wordToCompleteTrimmed.length()+replacementString.length()+signature.length(),
						labelProvider.getImage(idotnetelement, wordToCompleteFlags),
						displayString.toString());
		}
		return null;		
		
	}

	/**
	 * delivers a formatted completion proposal according to the input parameters
	 * for emptystring completion
	 */
	public Object getFormattedProposalForEmpty(
			String wordToComplete, String valueOfListelement,
			int documentOffset, CodeCompletionLabelProvider labelProvider,
			int cpFlags, String assemblyPath) {
		
		//resets the fields of the class
		reset();
		this.assemblyPath = assemblyPath;
		
		//get a substring 
		int wordToCompleteLastDot = wordToComplete.lastIndexOf(".");
		String wordToCompleteTrimmed = "";
		if(wordToCompleteLastDot != -1){
			wordToCompleteTrimmed = wordToComplete.substring(0, wordToCompleteLastDot+1);
		}
		String valueOfListelementSub = valueOfListelement.substring(wordToCompleteLastDot+1);

		//parse Elements of String		
		parseMetadataString(valueOfListelement);
		
		//build replacement and elementString
		int idotnetelementbegin = valueOfListelementSub.indexOf(MetadataElements.IDotNetElement); //FIXME: eventuell  + 1
		if(idotnetelementbegin > 0){
			String helpString = valueOfListelementSub.substring(0, idotnetelementbegin - 1);
			elementString = extractElementString(helpString);				
		}
		else{
			replacementString = "ERROR IN REPLACEMENT STRING => CSharpCompletionProposal.java";
		}
				
		StringBuffer displayString = buildDisplayString();

		int replacementLength = documentOffset - wordToComplete.length();
		int replacmentOffset = wordToComplete.length(); //0,

		replacementString = elementString;
		int delta = wordToComplete.length() - wordToCompleteTrimmed.length();
		wordToCompleteTrimmed = "";
		replacementLength = documentOffset - delta;
		replacmentOffset = delta;
		
		CompletionProposalFilter cpFilter = new CompletionProposalFilter();
		//if filter function returns true => completion proposal is not allowed
		//due to rules of code completion
	
		if(!displayString.toString().trim().equals("") && 
		    cpFilter.filter(cpFlags, wordToCompleteFlags, idotnetelement, false,
		    		displayString.toString())) {
			
//			  return new CompletionProposal(wordToCompleteTrimmed+replacementString+signature,
//				replacementLength,
//				replacmentOffset, 
//				wordToCompleteTrimmed.length()+replacementString.length()+signature.length(),
//				labelProvider.getImage(idotnetelement, wordToCompleteFlags),
//				displayString.toString(), 
//				null,	null);
			  
			return createProposalObject(wordToCompleteTrimmed+replacementString+signature,
						replacementLength,
						replacmentOffset, 
						wordToCompleteTrimmed.length()+replacementString.length()+signature.length(),
						labelProvider.getImage(idotnetelement, wordToCompleteFlags),
						displayString.toString());

		}
		return null;
		
	}

	/**
	 * delivers a formatted completion proposal according to the input parameters
	 * for standard completion
	 */
	public Object getFormattedProposalForStandard(
			String wordToComplete, String valueOfListelement,
			int documentOffset, CodeCompletionLabelProvider labelProvider,
			boolean isVariableCompletion, String variable, boolean isTypeOnly,
			boolean isMemberOfSrcFile, int origStrLength, int cpFlags, String assemblyPath) {
		
		//resets the fields of the class
		reset();
		this.assemblyPath = assemblyPath;
		
		//get a substring 
		int wordToCompleteLastDot = wordToComplete.lastIndexOf(".");
		String wordToCompleteTrimmed = "";
		if(wordToCompleteLastDot != -1){
			wordToCompleteTrimmed = wordToComplete.substring(0, wordToCompleteLastDot+1);
		}
		String valueOfListelementSub = valueOfListelement.substring(wordToCompleteLastDot+1);

		//parse Elements of String		
		parseMetadataString(valueOfListelement);
		
		//build replacement and elementString
		int idotnetelementbegin = valueOfListelementSub.indexOf(MetadataElements.IDotNetElement); //FIXME: eventuell  + 1
		if(idotnetelementbegin > 0){
			String helpString = valueOfListelementSub.substring(0, idotnetelementbegin - 1);
			elementString = extractElementString(helpString);				
		}
		else{
			replacementString = "ERROR IN REPLACEMENT STRING => CSharpCompletionProposal.java";
		}
				
		StringBuffer displayString = buildDisplayString();
		int delta = 0; 
		int replacementLength = 0;
		int replacementOffset = 0;
		if(isVariableCompletion){
			replacementString = elementString; //variable+"."+
			delta = wordToComplete.length() - wordToCompleteTrimmed.length() +1; //because of dot;
			wordToCompleteTrimmed = variable +".";
			replacementLength = documentOffset - variable.length() - delta;
			replacementOffset = variable.length()+delta ;			
		}
		else if(isMemberOfSrcFile){
			replacementString = elementString; //variable+"."+
			delta = wordToComplete.length() - wordToCompleteTrimmed.length() ;
			wordToCompleteTrimmed = "";
			replacementLength = documentOffset - delta;
			replacementOffset = delta ;			
			
		}
		else if(isTypeOnly && wordToComplete.length() > 0){
			// fixed the following bug here: "System.Char.M" <Ctrl-Space>
			//                               choose "MaxValue"
			//                               --> "System.Char.Char.MaxValue"
			// (2008-09-03)

			replacementString = typeWithoutNamespace;
			wordToCompleteTrimmed = "";
			String typeonly = "";
			int additionalChars = 0;
			if (wordToComplete.lastIndexOf('.') >= 0) {
				typeonly = wordToComplete.substring(0, wordToComplete.lastIndexOf('.'));
				additionalChars = wordToComplete.length() - typeonly.length();
			}
			typeonly = typeonly.substring(typeonly.lastIndexOf(".")+1, typeonly.length());
			replacementLength = documentOffset - typeonly.length() -1 - additionalChars + 1;
			replacementOffset = typeonly.length() + 1 + additionalChars - 1;
			
//			replacementString = typeWithoutNamespace; //variable+"."+
//			wordToCompleteTrimmed = "";
//			String typeonly = wordToComplete.substring(0, wordToComplete.length()-1); //remove dot
//			typeonly = typeonly.substring(typeonly.lastIndexOf(".")+1, typeonly.length());
//			replacementLength = documentOffset - typeonly.length() -1; //because of dot;
//			replacementOffset = typeonly.length()+1;//typeWithoutNamespace.length();
		}
		else{
			delta = wordToComplete.length() - origStrLength;
			if(delta < 0)
				delta = 0;
			replacementLength = documentOffset - origStrLength;		
			replacementOffset = wordToComplete.length() - delta; //0,
			wordToCompleteTrimmed = "";
		}

		CompletionProposalFilter cpFilter = new CompletionProposalFilter();
		//if filter function returns true => completion proposal is not allowed
		//due to rules of code completion
			
		if(!displayString.toString().trim().equals("") && 
		    cpFilter.filter(cpFlags, wordToCompleteFlags, idotnetelement, isVariableCompletion,
		    		displayString.toString())){
							
//			  return new CompletionProposal(wordToCompleteTrimmed+replacementString+signature,
//				replacementLength,
//				replacementOffset, 
//				wordToCompleteTrimmed.length()+replacementString.length()+signature.length(),
//				labelProvider.getImage(idotnetelement, wordToCompleteFlags),
//				displayString.toString(), 
//				null,	null);
			
			  return createProposalObject(wordToCompleteTrimmed+replacementString+signature,
						replacementLength,
						replacementOffset, 
						wordToCompleteTrimmed.length()+replacementString.length()+signature.length(),
						labelProvider.getImage(idotnetelement, wordToCompleteFlags),
						displayString.toString());

		}
		return null;		

	}
	
	/**
	 * completion proposal for types and namespaces without elements
	 */
	public Object getFormattedProposalForStandardTypeOnly(
			String value, String metadata, int documentOffset,
			CodeCompletionLabelProvider labelProvider, boolean b,
			int originalStringLength, int cpFlags,
			int levelOfDotforDisplaystring, String assemblyPath) {
		
		//resets the fields of the class
		reset();
		this.assemblyPath = assemblyPath;
		
		//parse Elements of String		
		parseMetadataString(metadata);
		
		int delta = value.length() - originalStringLength;
		if(delta < 0)
			delta = 0;
		
		int replacementLength = 0;
		int replacementOffset = 0;
		String displayString = fitToDotLevel(typeString, levelOfDotforDisplaystring);
		
		if(typeString.toLowerCase().equals(value)){
			replacementOffset = documentOffset - originalStringLength;		
			replacementLength = value.length() - delta; //0,
		}
		else{
			typeString = replacementString;
			replacementOffset = documentOffset - originalStringLength;		
			replacementLength = replacementString.length() - delta; //0,
		}
		if(replacementLength <0){
			System.out.println("DEBUG replalength < 0 " + typeString);
			replacementLength = 0;
		}

		
		if(!typeString.equals("")){
//			return new CompletionProposal(typeString,
//				replacementOffset/*FIXME* required originalstringlength*/,
//				replacementLength, 
//				typeString.length(),
//				labelProvider.getImage(idotnetelement, wordToCompleteFlags),
//				displayString, 
//				null,	null);
			
			return createProposalObject(typeString,
					replacementOffset/*FIXME* required originalstringlength*/,
					replacementLength, 
					typeString.length(),
					labelProvider.getImage(idotnetelement, wordToCompleteFlags),
					displayString);

		}
		return null;			
	}
	
	
	/**
	 * fit string to dot level allowed => 
	 * dot level = 1
	 * stringToFit => System.hugo
	 * result=> hugo
	 * @param stringToFit
	 * @param levelOfDot
	 * @return
	 */
	private String fitToDotLevel(String stringToFit, int levelOfDot) {
		
		int dotIndex = 0;
		int plusDot = 0;
		String sub = stringToFit;
		for(int i=0; i<levelOfDot; i++){
			dotIndex = stringToFit.indexOf(".");
			if(dotIndex > 0){
				sub = sub.substring(dotIndex,sub.length());
				plusDot = 1;
			}
			else{
				dotIndex = 0;
				break;
			}
		}
		
		return stringToFit.substring(dotIndex+plusDot, stringToFit.length());
	}

	/**
	 * parse the metadata of the string which is delivered as a basis for
	 * the completionproposal
	 * @param valueOfListelementSub
	 * @param splittedString
	 */
	private void parseMetadataString(String valueOfListelementSub) {
		String[] splittedString = valueOfListelementSub.split(" ");
		for(int i=0; i<splittedString.length; i++){

			//get replacementstring
			if(i==0){
				//remove possibly existing (
				String[] helpArray = splittedString[i].split("[(\n]");
				replacementString = helpArray[0];
				origString = helpArray[0];
			}
			//parse idotnetelement
			else if(splittedString[i].startsWith(MetadataElements.IDotNetElement)){
				
				idotnetelement = Integer.parseInt(splittedString[i].substring(splittedString[i].lastIndexOf(":")+1, 
															 splittedString[i].length()));
			//parse flags		
			}else if(splittedString[i].startsWith(MetadataElements.Flags)){
				
				wordToCompleteFlags = Integer.parseInt(splittedString[i].substring(splittedString[i].lastIndexOf(":")+1, 
						 splittedString[i].length()));

			//parse signature		
			}else if(splittedString[i].startsWith(MetadataElements.Signature) && 
					 !splittedString[i].equals(MetadataElements.Signature)){ //Then the SIGNATURE: string is empty
				
				String bracketString = valueOfListelementSub.substring(valueOfListelementSub.indexOf("SIGNATURE:")+10);
				int indexFirstBracket = bracketString.indexOf("(");
				int indexLastBracket = indexFirstBracket + bracketString.substring(indexFirstBracket).indexOf(")");
				if(indexFirstBracket < indexLastBracket){
					signature = bracketString.substring(indexFirstBracket, indexLastBracket+1);
					if(signature.startsWith("((")){
						signature = signature.substring(1);
					}

				}else
				{
					signature = "SIGNATURE ERROR => CSHarpcompletionproposal.java";
				}
			//parse Return	
			} else if(splittedString[i].startsWith(MetadataElements.Return)){
				
				returnStrOfMethod = splittedString[i].substring(splittedString[i].lastIndexOf(":")+1, 
						 splittedString[i].length());

			}//parse Type
			else if(splittedString[i].startsWith(MetadataElements.Type)){
				typeString = origTypeString =
					splittedString[i].substring(splittedString[i].lastIndexOf(":")+1, 
						 splittedString[i].length());
						
			}
			else if(splittedString[i].startsWith(MetadataElements.TypeWithoutNamespace)){
				typeWithoutNamespace = splittedString[i].substring(splittedString[i].lastIndexOf(":")+1, 
						 splittedString[i].length());
			}
			else if (splittedString[i].startsWith(MetadataElements.OriginalElementString)) {
				origString = splittedString[i].substring(splittedString[i].lastIndexOf(":")+1,
						splittedString[i].length());
			}
		}
	}

	/**
	 * append string elements to build a display string
	 * @return
	 */
	private StringBuffer buildDisplayString() {
		StringBuffer displayString = new StringBuffer();
		displayString.append(elementString);
		if(!signature.equals("")){
			displayString.append(signature);
		}		
		if(!returnStrOfMethod.equals("")){
			displayString.append(" : ");
			displayString.append(returnStrOfMethod);
		}else if(!typeString.equals("")){
			displayString.append(" : ");
			displayString.append(typeString);
		}
		
		//displayString.append(" - ");
		//displayString has to contain valid output
		return displayString;
	}
	
	/**
	 * calculate the elementString
	 * @param replacementString
	 * @return
	 */
	private String extractElementString(String helpString) {
		int bracketPos = helpString.indexOf("(");
		if(bracketPos == -1){
			bracketPos = helpString.indexOf("<");
		}
		int nextDot = helpString.indexOf(".");
		int nextSpace = helpString.indexOf(" ");
		//String elementStringSearchfield = "";
		//check for dot
		if((nextDot > 0) && (bracketPos == -1)){
			elementString = helpString.substring(0, nextDot);
		}
		else if((nextDot < bracketPos) && (nextDot < nextSpace) &&
		   (nextDot > -1) && (bracketPos > -1)){
			elementString = helpString.substring(0, nextDot); 
		}//check for bracket
		else if((bracketPos > -1) && 
				(nextDot == -1 || nextDot > bracketPos)){
			elementString = helpString.substring(0, bracketPos);
		}//neither dot nor bracket, only space
		else if(nextSpace > 0){
			elementString = helpString.substring(0, nextSpace);
		}else{
			elementString = helpString;
		}

		//DEBUG
		if(elementString.equals("")){
			//elementString = "ELEMENT STRING ERROR => CSharpCompletionProposal.java";
			return "";
		}
		//END DEBUG
		return elementString;
	}

	private void reset() {
		this.signature =  "";//"SIGNATURE";
		this.returnStrOfMethod =  "";//"RETURNSTRING";
		this.replacementString =  "";//"REPLACEMENT";
		this.elementString =  "";//"ELEMENT";
		this.typeWithoutNamespace = ""; 
		this.typeString = ""; //TYPE
		this.idotnetelement = 0;
		this.wordToCompleteFlags = 0;
		this.assemblyPath = "";
		this.origString = "";
		this.origTypeString = "";
		
	}

	private Object createProposalObject(String replacementString, int replacementOffset,
			int replacementLength, int cursorPosition, Image image, String displayString) {
		
		int index;
		String namespace, type, member;
		
		switch (idotnetelement) {
		case IDotNetElement.NAMESPACE:
			// displayString contains namespace
			return (Object)(new NamespaceCompletionProposal(origString, assemblyPath,
					replacementString, replacementOffset, replacementLength, cursorPosition,
					image, displayString));
			
		case IDotNetElement.FIELD:
			index = origString.indexOf(typeWithoutNamespace);
			if (index < 1)
				namespace = "";
			else
				namespace = origString.substring(0, index-1);
			index = typeWithoutNamespace.lastIndexOf(".");
			type = typeWithoutNamespace.substring(0, index);
			member = typeWithoutNamespace.substring(index+1);
			return (Object)(new MemberCompletionProposal(type, namespace, member, assemblyPath,
					replacementString, replacementOffset, replacementLength, cursorPosition,
					image, displayString));

		case IDotNetElement.PROPERTY:
			index = typeWithoutNamespace.lastIndexOf(".");
			type = typeWithoutNamespace.substring(0, index);
			member = typeWithoutNamespace.substring(index+1);
			index = origString.indexOf(typeWithoutNamespace);
			if (index < 1)
				namespace = "";
			else
				namespace = origString.substring(0, index-1);
			return (Object)(new MemberCompletionProposal(type, namespace, member, assemblyPath,
					replacementString, replacementOffset, replacementLength, cursorPosition,
					image, displayString));
			
		case IDotNetElement.METHOD:
		case IDotNetElement.CONSTRUCTOR:
		case IDotNetElement.DESTRUCTOR:
			if (typeWithoutNamespace.equals("")) {
				// these are the Equals(), Finalize(), ... methods
				type = "";
				namespace = "";
				member = origString+signature;
			} else {
				index = typeWithoutNamespace.lastIndexOf(".");
				while (typeWithoutNamespace.charAt(index) == '.')   // for .ctor and .cctor
					index--;
				type = typeWithoutNamespace.substring(0, index+1);
				member = typeWithoutNamespace.substring(index+2)+signature;
				index = origString.indexOf(typeWithoutNamespace);
				if (index < 1)
					namespace = "";
				else
					namespace = origString.substring(0, index-1);
			}
			return (Object)(new ParameterCompletionProposal(namespace, type, member, assemblyPath,
					viewer, replacementString, replacementOffset, replacementLength,
					cursorPosition, image, displayString));
			
		case IDotNetElement.TYPE:
		case IDotNetElement.CLASS:
		case IDotNetElement.INTERFACE:
		case IDotNetElement.STRUCT:
		case IDotNetElement.ENUM:
			index = origString.indexOf(origTypeString);
			if (index < 1)
				namespace = "";
			else
				namespace = origString.substring(0, index-1);
			return (Object)(new TypeCompletionProposal(namespace, origTypeString, assemblyPath,
					replacementString, replacementOffset, replacementLength, cursorPosition,
					image, displayString));
			
		default:
			return new CompletionProposal(replacementString, replacementOffset, replacementLength,
					cursorPosition, image, displayString, null, null);
		}
	}
}



