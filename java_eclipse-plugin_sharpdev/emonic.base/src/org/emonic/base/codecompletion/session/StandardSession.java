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

package org.emonic.base.codecompletion.session;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextViewer;
import org.emonic.base.codecompletion.CCAdministration;
import org.emonic.base.codecompletion.CompletionProposalFormatter;
import org.emonic.base.codecompletion.MetadataElements;
import org.emonic.base.codecompletion.Metatype;
import org.emonic.base.codehierarchy.ICompilationUnit;
import org.emonic.base.codehierarchy.IField;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.codehierarchy.IType;
import org.emonic.base.editors.CSharpCursorPositionContext;
import org.emonic.base.infostructure.CSharpCodeParser;

/**
 * represents a standard session which 
 * (not this or empty string)
 * @author dertl
 *
 */
public class StandardSession extends Session {

	
	protected int currentDotLevel;

	public StandardSession(ITextViewer viewer){
		super(viewer);
		this.currentDotLevel = 0;
	}
	
	public LinkedList getCompletionproposal(String wordToComplete_,
			CSharpCursorPositionContext cpc_, int documentOffset_,
			ISourceUnit actualSrcUnit_, IFile actualFile_) {

			
			this.documentOffset = documentOffset_;
			this.cpc = cpc_;
			this.actualSrcUnit = actualSrcUnit_;
			this.actualFile = actualFile_;
			
			//reset unique entries
			this.uniqueEntry = new LinkedList();
			LinkedList results = new LinkedList ();
			//set actual project
			if(actualFile != null){
				this.actualProject = actualFile.getProject();
			}
			
			//add case sensitivity && remove preambel this
			this.originalWtcString = wordToComplete_;
			this.wordToComplete = wordToComplete_.toLowerCase().trim();
			this.originalStringLength = wordToComplete.length();
			this.suffix = "";
			//set flags
			setFlags(false);
			if(this.wordToComplete.endsWith("]")){
				//FIXME
				System.out.print("DEBUG Code Completion for ...[] not implemented");
				if(results.size() > 0){
					return results;
				}
				return new LinkedList();
			}
			
			IType itype = cpc.getIType();
			String filepath = actualFile.getLocation().toPortableString();
			results = getStandardCompletionElements(itype, filepath, actualProject);
			
			//DEBUG
			//CCAdministration.getInstance().printSize();
			//END DEBUG
			return results;
	}

	/**
	 * 
	 * @param itype
	 * @param b
	 * @param c
	 * @param filepath
	 * @param actualProject
	 * @return
	 */
	protected LinkedList getStandardCompletionElements(IType actualitype, String actualfilepath, 
														IProject actualProject) {

		LinkedList returnList = new LinkedList();
		LinkedList testList  = new LinkedList();
		//multilevel check for standard completion
	    testList = getElementsForString();
		returnList.addAll(testList);						
		
		return returnList;
		
	}

	/**
	 * getElementsForStringWithDotAtLast
	 * @return
	 */
	protected LinkedList getElementsForString() {
		//scan local file for wordToComplete Part
		//get typename of wordToComplete-variable
		this.suffix = "";
		this.typename = "";
		this.currentDotLevel = calculateDotLevel(this.wordToComplete);
		CompletionProposalFormatter cpFormatter = new CompletionProposalFormatter(viewer);
		this.typename = fineGrainedTypeNameCalculation();
		
		//get all files of the set and search them!
		HashSet filepathSet = getFilePathMetatypesForStandardSession(typename); 
		LinkedList testList = new LinkedList();
		String filepath = "";
		
		//long start = new Date().getTime();
		
		//at first search for types and namespaces
		//if it is a ctrol+space completion without dot!
		if(!wordToComplete.endsWith(".") && 
		   !filepathSet.isEmpty() &&
		   suffix.equals("")){
			// searchForTypesAndNamespaces updates testList!
			searchForTypesAndNamespaces(cpFormatter, testList, filepathSet);
		}
		else if(!filepathSet.isEmpty() 
				/* && suffix.equals("") */){
			typename = typename + ".";
			searchForTypesAndNamespaces(cpFormatter, testList, filepathSet);
			typename = typename.substring(0, typename.length()-1);
		}
		//long runningTime = new Date().getTime() - start;
		//System.out.println("DEBUG StandardSession.java: Time for creating ns + typeproposals " + runningTime);
	
		//only add elements if no namespaces+types are found!
		if(testList.size() == 0 && !filepathSet.isEmpty()){
			boolean baseIsDll = false;
			boolean baseIsGAC = false;
			/*if(filepathSet.size() > 1){
				System.out.println("DEBUG: Error in StandardSession: filepathSet should contain only one file!");
				return testList;
			}*/
			Iterator iter2 = filepathSet.iterator(); //Filepath-Set should contain only one entry!
			
			//System.out.println("DEBUG: filepathSet size " + filepathSet.size());
			//CCAdministration.getInstance().printSize();			

			while(iter2.hasNext()){
				Metatype mt = (Metatype) iter2.next();
				filepath = mt.getFilepath();
				ICompilationUnit nestedCompUnit = null;				
				if(filepath.toLowerCase().endsWith(".cs")){
					//search in cs-file cache first
					if(CCAdministration.getInstance().isInSourceFileCache(filepath)){
						nestedCompUnit = CCAdministration.getInstance().getFromSourceFileCache(filepath);
					}else{
						CSharpCodeParser forNestedParser = new CSharpCodeParser();
						forNestedParser.init(new File(filepath));
						nestedCompUnit = forNestedParser.parseDocument();
						CCAdministration.getInstance().addToSourceCache(filepath, nestedCompUnit);
					}
				}//check if dll/exe
				else if(filepath.toLowerCase().endsWith(".dll")){
					baseIsDll = true;
					baseIsGAC = ((Boolean)CCAdministration.getInstance().getIsGACBool(filepath)).booleanValue();
//					if(CCAdministration.getInstance().isInAssemblyCache(filepath)){
//						nestedCompUnit = CCAdministration.getInstance().getFromAssemblyCache(filepath,this);
//						
//					}else{
						nestedCompUnit = getCompUnitFromAssembly(filepath);
//						CCAdministration.getInstance().addToAssemblyCache(filepath, nestedCompUnit);
//						
//					}
				}							
		
				if(nestedCompUnit == null){
					//FIXME for string with xxx<incomplete> +ctrl+space =>
					//must search ALL elements of typemap which start with string compareable
					//to incomplete-typename => get a map with namespaces and types
					//take all this values and make completion proposals with them
					//must know if ns or types, thats all...
					System.out.println("ERROR: no src/dll file found for type: " + typename);
					return new LinkedList();
					//throw new NullPointerException("ERROR: no src(dll file found for type: " + typename);
				}
				//need wordToComplete exclusive type variable
				int lastDot = wordToComplete.lastIndexOf(".");
				this.wordToComplete = wordToComplete.substring(lastDot+1, wordToComplete.length());
				
					IType itype = getITypeRecursively(typename, nestedCompUnit);
					if(itype == null){			
						System.out.println("ERROR no IType or INamespace found in File according to wordToComplete");
						return new LinkedList();
						//throw new NullPointerException("ERROR no IType found in Assembly according to wordToComplete");
					}
		
					//search in types
					if(itype != null){
						IProject project = getProjectFromFile(filepath, baseIsGAC);
						//PROBLEM
						//TODO
						//each call fo getElemensOfTypeRecursively tries to extract file and 
						//decompresses the itrie
						// as on filepath may contain numerous types => happens n-times instead one time...
						testList.addAll(this.getElementsOfTypeRecursively(nestedCompUnit, itype, baseIsGAC, baseIsDll, filepath, project, false));
						//end TODO
					}
				
			}
		}		
		
		//try this, local vars and begin of namespaces		
		//get elements of base type
		//only allowed if wordToComplete contains no dot!
		if(originalWtcString.indexOf(".") == -1){
			suffix = wordToComplete;
			
			//search ALL files of Hashmaps projectCC and gac for the originalWtcString...
			
			
			//add all elements of file (comparable to this)
			testList.addAll(this.getElementsOfTypeRecursively(null, cpc.getIType(), 
						false, false, this.actualFile.getLocation().toPortableString(), this.actualProject,
						true));			
	
			//add entries from method-signature
			testList.addAll(this.getSignatureParamsOfDocOffsetMethod());
			
			//add entries from method => method variables
			testList.addAll(this.getVariablesOfDocOffsetMethod());
		}
		
		//System.out.println("DEBUG StandardSession.java: Number of completion proposals "+ testList.size());
		
		return testList;
	}

	/**
	 * get number of dots of existing strings
	 * @param wordToComplete
	 * @return
	 */
	private int calculateDotLevel(String toCalc) {
		int level = 0;
		char ch;
        for ( int i = 0;  i < toCalc.length();  i++ ) {
            ch = toCalc.charAt(i);  // Get the i-th character in str.
            if ( ch == '.'){
               level++;
            }
        }     
		
		return level;
	}

	private void searchForTypesAndNamespaces(
			CompletionProposalFormatter cpFormatter, LinkedList testList,
			HashSet filepathSet) {
		String filepath;
		Iterator iter = filepathSet.iterator();
		
		while(iter.hasNext()){
			Metatype mt = (Metatype)iter.next();
			filepath  = mt.getFilepath();
			if(filepath == null){
				filepath = "";
			}	
			
			//check for namespaces and types
			HashSet metatypes = CCAdministration.getInstance().getRevertedMetatypes(filepath);
			Iterator miter = metatypes.iterator();
			while(miter.hasNext()){
				Metatype mety = (Metatype)miter.next();
				String metadata = mety.getMetadata();
				String value = "";
				if(mety.isNamespace()){
					value = mety.getNamespace();
				}else{
					value = mety.getTypename();
				}

				//check if contained in wordToComplete
				if(value.length() >= wordToComplete.length() &&
					wordToComplete.equals(value.substring(0, wordToComplete.length())) &&/*PART OF value!*/
					
					// Typing "System.Console" should display "Console", so I removed this check
					// - HK
//				   !wordToComplete.equals(value)/* typename must not be added to itself again*/ &&
				   this.currentDotLevel == calculateDotLevel(value)) /*compare number of dots in strings*/{
						//only adjust if namespace
						if(mety.isNamespace()){
							value = adjustDotLevelOfValue(value, typename);
						}
						if(!uniqueEntry.contains(value)){							
//							CompletionProposal compProp = cpFormatter.getFormattedProposalForStandardTypeOnly(value, metadata,
							Object compProp = cpFormatter.getFormattedProposalForStandardTypeOnly(
									value, metadata, documentOffset, labelProvider, false,
									originalStringLength, cpFlags, currentDotLevel, filepath);
							if(compProp != null){					
								uniqueEntry.add(value);
								testList.add(compProp);
							}									
						}
				}				
			}
		}
	}

	/**
	 * adjust the dot level of value according to the number of dots in typename
	 * @param value
	 * @param typename
	 * @return
	 */
	private String adjustDotLevelOfValue(String value, String typename) {
		String cuttedValue = null;
		int lastDotVal = value.lastIndexOf(".");
		int lastDotType = typename.lastIndexOf(".");
				
		//value: abcd.abc typename: abcd.a
		if(lastDotVal == lastDotType){
			cuttedValue = value;
		}//value: abcd.xyz.fgh.uf typename: abcd.x
		else{
			if(lastDotType < 0){
				lastDotType = 0;
			}
			String subTemp = value.substring(lastDotType+1, value.length());
			int nextDotVal = subTemp.indexOf(".");
			cuttedValue = value.substring(0, lastDotType+nextDotVal+1);
		}
		
		return cuttedValue;
	}

	private HashSet getFilePathMetatypesForStandardSession(String tn) {
		HashSet filepathSet = getFilepathOfMetatypes(tn);
		//System.out.println("DEBUG Size of filepathSet: " + filepathSet.size());
		//if filePathSet = empty, the typename may be incomplete, thus, get all filenames of project and ccmap
		if(filepathSet.size() == 0){
			filepathSet = CCAdministration.getInstance().getMetatypesOfIncompleteType(tn);			
		}
		return filepathSet;
	}

	private String fineGrainedTypeNameCalculation() {
		typename = getTypeName();
		//possible types has filepaths as keys, and for each filepaths all
		//types which (at least begin) with the typename-string

		//if typename empty 
		if(typename.equals("")){			
			//if nothing found search in typemap!
			String[] wtc = wordToComplete.split("\\.");
			if(wtc.length == 1 && !wordToComplete.endsWith(".")){
				typename = wordToComplete;
			}
			else if(wordToComplete.endsWith(".")){
			  typename = wtc[wtc.length-1];
			}else{
			  typename = wtc[wtc.length-2];
			}
		}
		return typename;
	}

	/**
	 * get the typename of the distinct wordToComplete String
	 * @return
	 */
	protected String getTypeName() {
		
		String typename = "";
		String wordToCompleteWithoutDot = "";
		if(this.wordToComplete.endsWith(".")){
			wordToCompleteWithoutDot = wordToComplete.substring(0, wordToComplete.length() - 1);			
		}else{
			wordToCompleteWithoutDot = wordToComplete;
			//still a dot there
			int dot = wordToCompleteWithoutDot.lastIndexOf(".");
			if(dot > 0){
				wordToCompleteWithoutDot = wordToComplete.substring(0, dot);//wordToCompleteWithoutDot.substring(dot+1, wordToCompleteWithoutDot.length());			
				//this.variable = wordToComplete.substring(0, dot);
				//this.wordToComplete = wordToComplete.substring(dot+1, wordToComplete.length());
				this.suffix = wordToComplete.substring(dot+1, wordToComplete.length());
			}
		}

		//check fields of actualType!
		IField[] fieldsOfInterest = this.actualSrcUnit.getTypes()[0].getFields();
		for(int i=0; i<fieldsOfInterest.length;i++){
			if(fieldsOfInterest[i].getElementName().toLowerCase().equals(wordToCompleteWithoutDot.toLowerCase())){
				typename = fieldsOfInterest[i].getTypeSignature();
				//if type found => set variable!
				this.variable = wordToCompleteWithoutDot;
			}
		}
		
		//if nothing found try signature of method and its variables
		if(typename.equals("")){
			HashMap searchIn = this.cpc.getMethodSignatureElements();
			searchIn.putAll(cpc.getVariablesOfMethod());
			Iterator iter = searchIn.keySet().iterator();
			while(iter.hasNext()){
				String nextString = (String)iter.next();
				if(nextString.toLowerCase().equals(wordToCompleteWithoutDot.toLowerCase())){
					String metadata = (String)searchIn.get(nextString);
					//parse for Typename
					typename = parseForType(metadata);
					this.variable = wordToCompleteWithoutDot;
					break;
				}
			}
		}
				
		return typename;
	}

	/**
	 * parse for the return string
	 * @param metadata
	 * @return
	 */
	private String parseForType(String metadata) {
		String retStr = "";
		String[] splittedString = metadata.split(" ");
		for(int i=0; i<splittedString.length; i++){
			 if(splittedString[i].startsWith(MetadataElements.Type)){
				 retStr = splittedString[i].substring(splittedString[i].lastIndexOf(":")+1, 
							 splittedString[i].length());
							
				}
		}
		
		return retStr;
	}
	
	
}
