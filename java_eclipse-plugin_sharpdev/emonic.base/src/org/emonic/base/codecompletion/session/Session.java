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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextViewer;
import org.emonic.base.codecompletion.AssemblyParserFactory;
import org.emonic.base.codecompletion.CCAdministration;
import org.emonic.base.codecompletion.CodeCompletionLabelProvider;
import org.emonic.base.codecompletion.CompletionProposalFormatter;
import org.emonic.base.codecompletion.ICCFlags;
import org.emonic.base.codecompletion.Metatype;
import org.emonic.base.codecompletion.compression.CompressionTool;
import org.emonic.base.codecompletion.datastructure.ITrie;
import org.emonic.base.codecompletion.jobs.AssemblyDeepExtractorJob;
import org.emonic.base.codecompletion.jobs.SourceDeepExtractorJob;
import org.emonic.base.codehierarchy.IAssembly;
import org.emonic.base.codehierarchy.ICompilationUnit;
import org.emonic.base.codehierarchy.INamespace;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.codehierarchy.IType;
import org.emonic.base.editors.CSharpCursorPositionContext;
import org.emonic.base.helpers.DebugTimer;
import org.emonic.base.helpers.DebugUtil;
import org.emonic.base.infostructure.CSharpCodeParser;
//import org.emonic.base.mbel2assemblyparser.MBel2AssemblyParser;

/**
 * The abstract base class for the different code completion sessions
 * @author dertl
 *
 */
public abstract class Session implements ISession {


	protected boolean triggeredByDot;

	protected String lastWordToComplete = null;

	protected String originalWtcString = null;

	protected String wordToComplete = null;

	protected CodeCompletionLabelProvider labelProvider = null;

	protected int cpFlags;

	protected CSharpCursorPositionContext cpc = null;

	protected LinkedList<String> uniqueEntry = null;

	protected int documentOffset;

	protected ISourceUnit actualSrcUnit = null;

	protected IFile actualFile = null;

	protected IProject actualProject = null;

	protected boolean isThis;

	protected boolean isEmpty;

	protected boolean isVariableCompletion;

	protected String typename;

	protected String variable;
	protected String suffix;

	protected int originalStringLength = 0;

	protected ITextViewer viewer = null;
	//private long removeCacheAfterMs = 10*1000L; // 10 seconds



	private static HashMap<String, ITrie> uncompressedCacheITries = new HashMap<String, ITrie>();
	//private static HashMap uncompressedCacheICompilationUnits = new HashMap();
	private DebugTimer rectimer;
	private DebugTimer xtimer;
	private DebugTimer ytimer;
	private DebugTimer ztimer;
	// DebugPrint?
	private boolean printRealy =true;

	public Session(ITextViewer viewer){
		//Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		//int cacheSeconds = prefs
		//.getInt(DefaultPrefsSetter.CODECOMPLETIONCACHE);
		//removeCacheAfterMs=cacheSeconds*1000L;
		this.lastWordToComplete = "";
		this.labelProvider = new CodeCompletionLabelProvider();
		this.triggeredByDot = false;
		this.cpFlags = 0x00;
		this.uniqueEntry = new LinkedList<String>();
		this.documentOffset = 0x00;
		this.isThis = false;
		this.isEmpty = false;
		this.isVariableCompletion = false;
		this.typename = "";
		this.variable = "";
		this.suffix = "";
		this.originalWtcString = "";
		this.viewer = viewer;
		rectimer=new DebugTimer("RecCodeCompletion");
		xtimer=new DebugTimer("xCodeCompletion");
		ytimer=new DebugTimer("yCodeCompletion");
		ztimer=new DebugTimer("zCodeCompletion");   
		DebugUtil.DebugPrint("New CodeCompletionSession", printRealy);
	}

	/**
	 * set the flags according to cpFlags
	 */
	protected void setFlags(boolean isThis){

		//add flags
		cpFlags = 0x00;
		if(cpc.isStatic()){
			cpFlags |= ICCFlags.IS_IN_STATIC;
		}
		if(cpc.isInMethod()){
			cpFlags |= ICCFlags.IS_IN_METHOD;
		}
		if(isThis){
			cpFlags |= ICCFlags.IS_THIS;
		}
	}

	/**
	 * search for namespace elements
	 * @param unit
	 * @param searchstring
	 * @param isGAC
	 * @param isDll
	 * @param filepath
	 * @param project
	 * @return
	 */
	protected LinkedList<Object> getElementsOfNamespace(ICompilationUnit unit, String searchString,
			boolean isGAC, boolean isDll, String filepath, IProject project){
		LinkedList<Object> results = new LinkedList<Object>();
		HashMap<?, ?> projectMap = null;
		Boolean isDeep = new Boolean(false);
		if(project != null && !isGAC){
			projectMap = CCAdministration.getInstance().getProjectCCMap(project);
		}else{
			projectMap = CCAdministration.getInstance().getGACMap();
		}
		isDeep = (Boolean) (CCAdministration.getInstance().getTypeLoadedBool(project, filepath));
		extractFileIfNecessary(unit, isDll, filepath, project, isDeep.booleanValue());

		ITrie trie = getDecompressedTrie(projectMap, filepath);

		//get all elements of the given file
		HashMap<String, ITrie> oneFileMap = new HashMap<String, ITrie>();
		oneFileMap.put(filepath,trie);
		results.addAll(searchInTries(searchString, oneFileMap, false));
		return results;	
	}

	/**
	 * search Namespaces for elements
	 * @param unit
	 * @param inamespace
	 * @param project
	 * @param isGAC
	 * @param isDll
	 * @param filepath
	 * @return
	 */
	protected LinkedList<Object> getElementsOfNS(String insname, String filepath,
			ITrie trie){
		LinkedList<Object> results = new LinkedList<Object>();
		//get all elements of the given file
		HashMap<String, ITrie> oneFileMap = new HashMap<String, ITrie>();
		oneFileMap.put(filepath,trie);
		String searchString = insname;//+"."+suffix;//FIXME this.wordToComplete ?;
		//needed for variable search
		results.addAll(searchInTries(searchString, oneFileMap, false));
		//show all types in namespace, too!
		//FIXME Missing
		//inamespace.getTypes();
		return results;
	}

	/**
	 * get Trie from Project
	 * @param project
	 * @param isGAC
	 * @param filepath
	 * @return
	 */
	protected ITrie getTrieFromProject(IProject project, boolean isGAC,
			String filepath) {
		HashMap<?, ?> projectMap = null;
		if(project != null && !isGAC){
			projectMap = CCAdministration.getInstance().getProjectCCMap(project);
		}else{
			projectMap = CCAdministration.getInstance().getGACMap();
		}
		//if isGAC => project == null, otherwise project = IProject!
		//shallow loading should be enough! => no file extract
		ITrie trie = getDecompressedTrie(projectMap, filepath);
		return trie;
	}

	/**
	 * get the elements (fields, methods) of a type in a dll or src file recursively
	 * @param itype
	 * @param isGAC
	 * @param isDll
	 * @param filepath
	 * @param project
	 * @return
	 */
	protected LinkedList<Object> getElementsOfTypeRecursively(ICompilationUnit unit, IType itype, boolean isGAC, boolean isDll, 
			String filepath, IProject project, boolean isMemberOfSrcFile){
		rectimer.Start();
		LinkedList<Object> results = new LinkedList<Object>();

		String searchType = itype.getFullName().toLowerCase() + ".";
		HashMap<?, ?> projectMap = null;
		Boolean isDeep = new Boolean(false);
		if(project != null && !isGAC){
			projectMap = CCAdministration.getInstance().getProjectCCMap(project);
		}else{
			projectMap = CCAdministration.getInstance().getGACMap();
		}
		//if isGAC => project == null, otherwise project = IProject!
		isDeep = (Boolean) (CCAdministration.getInstance().getTypeLoadedBool(project, filepath));
		extractFileIfNecessary(unit, isDll, filepath, project, isDeep.booleanValue());

		System.out.println("Getting decompressed tree: "+filepath);

		ITrie trie = getDecompressedTrie(projectMap, filepath);		

		//get all elements of the given file
		HashMap<String, ITrie> oneFileMap = new HashMap<String, ITrie>();
		oneFileMap.put(filepath, trie);
		String searchString = searchType + suffix; //FIXME this.wordToComplete ?;
		//needed for variable search
		results.addAll(searchInTries(searchString, oneFileMap, isMemberOfSrcFile));
		//needed for namespace.type[1..n] search
		//results.addAll(searchInTries(itypename, oneFileMap));

		//search the elements of the base type
		String scName = itype.getSuperclassName().trim();
		//System.object not allowed!
		if(!scName.equals("") && !scName.equals("System.Object")){
			// get FilePath
			// add all possible elements of the different files
			HashSet<?> scPathSet = getFilepathOfMetatypes(scName);
			Iterator<?> iter = scPathSet.iterator();
			String scPath = "<nousefultypename>";
			while(iter.hasNext()){
				Metatype mt = (Metatype) iter.next();
				scPath = mt.getFilepath();
				if(scPath == null){
					scPath = "";
				}

				ICompilationUnit forNestedCompUnit = getICompilationUnitFromFile(scPath);
				if(forNestedCompUnit == null){
					System.out.println("ERROR no ICompilationUnit found for type " + scName);
					return new LinkedList<Object>();
				}
				//prepare scName => e.g. "System.UriParser" => "UriParser"
				int lastdot = scName.lastIndexOf(".");
				if(lastdot > 0){
					scName = scName.substring(lastdot+1, scName.length());
				}
				IType scIType = getITypeRecursively(scName.toLowerCase(), forNestedCompUnit);
				if(scIType == null){
					System.out.println("ERROR no IType found in file for type " + scName);
					return results;
					//throw new NullPointerException("no src/dll file found for scType " + scName);
				}
				boolean scIsDll = checkIfDll(scPath);
				boolean scIsGAC = checkIfGAC(scPath);
				IProject scProject = getProjectFromFile(scPath, isGAC);
				//call getThisElements recursively
				LinkedList<Object> scElemList = getElementsOfTypeRecursively(forNestedCompUnit, scIType,scIsGAC, scIsDll, scPath, scProject, isMemberOfSrcFile);
				results.addAll(scElemList);
			}
		}				

		//add system object entries
		HashMap<String, ITrie> objectMap = new HashMap<String, ITrie>();
		objectMap.put("System.Object", CCAdministration.getInstance().getSystemObjectTrie());
		results.addAll(searchInTries(suffix,objectMap, false));	
		rectimer.Stop();
		rectimer.Output(printRealy);
		return results;
	}


	/**
	 * return the ICompilationUnit from the src or dll
	 * @param filepath
	 */
	protected ICompilationUnit getICompilationUnitFromFile(String filepath) {
		ICompilationUnit compUnit = null;
		//for cs and dll/exes
		xtimer.Start();
		if(filepath.toLowerCase().endsWith(".cs")){
			//search in cs-file cache first
			if(CCAdministration.getInstance().isInSourceFileCache(filepath)){
				compUnit = CCAdministration.getInstance().getFromSourceFileCache(filepath);
			}
			else{
				CSharpCodeParser forNestedParser = new CSharpCodeParser();
				forNestedParser.init(new File(filepath));
				compUnit = forNestedParser.parseDocument();
				CCAdministration.getInstance().addToSourceCache(filepath, compUnit);
			}
		}else if(filepath.toLowerCase().endsWith(".dll") || 
				filepath.toLowerCase().endsWith(".exe")){
			//			//search in assemblyCache
			//			if(CCAdministration.getInstance().isInAssemblyCache(filepath)){
			//				ytimer.Start();
			//				compUnit = CCAdministration.getInstance().getFromAssemblyCache(filepath,this);					
			//			    ytimer.Stop();
			//			}else{
			//				ztimer.Start();
			compUnit = getCompUnitFromAssembly(filepath);
			//				CCAdministration.getInstance().addToAssemblyCache(filepath, compUnit);
			//				ztimer.Stop();
			//			}
		}
		xtimer.Stop();
		xtimer.Output(printRealy);
		ytimer.Output(printRealy);
		ztimer.Output(printRealy);
		return compUnit;
	}

	/**
	 * extracts a source or dll file if needed (Deep!)
	 * @param isDll
	 * @param filepath
	 * @param project
	 * @param isDeep
	 */
	protected void extractFileIfNecessary(ICompilationUnit unit, boolean isDll,
			String filepath, IProject project, boolean isDeep) {


		if(!isDeep && !isDll){
			/*FIXME: Problem unit seems to be not a deep version of a file
			 * if(unit != null){
					SourceExtractor extractor = new SourceExtractor(project);
					extractor.extractFile(unit,filepath, isDeep);
				}else*/{
					SourceDeepExtractorJob srce = new SourceDeepExtractorJob(filepath, project);
					srce.schedule();
					try{
						srce.join(); //wait until srce.schedule is finished
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
		}else if(!isDeep && isDll){
			/*FIXME
			 * if(unit != null){
					AssemblyExtractor extractor = new AssemblyExtractor(project);
					extractor.extractFile(unit, isDeep, isDll, filepath);
				}
				else*/{
					AssemblyDeepExtractorJob adej = new AssemblyDeepExtractorJob(filepath, project);
					adej.schedule();
					try{
						adej.join();
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
		}

	}

	/**
	 * 
	 * @param wordToComplete
	 * @param documentOffset
	 * @param searchMap
	 * @return
	 */
	protected ArrayList<Object> searchInTries(String wordToCompleteArg, HashMap<String, ITrie> searchMap, boolean isMemberOfSrcFile) {
		String wordToComplete = wordToCompleteArg;

		ArrayList<Object> results = new ArrayList<Object>();

		Iterator<String> trieMapKeySetIterator = searchMap.keySet().iterator();
		CompletionProposalFormatter cpFormatter = new CompletionProposalFormatter(viewer);

		while(trieMapKeySetIterator.hasNext()) {
			String keyIterator = (String) trieMapKeySetIterator.next();

			// System.Object elements HAVE to be included => wordtocomplete => hindering
			// System.Object is always the last map which is checked...
			if(keyIterator.equals("System.Object")){
				//prepare scName => e.g. "System.UriParser" => "UriParser"
				int lastdot = wordToComplete.lastIndexOf(".");
				if(lastdot > 0){
					wordToComplete = wordToComplete.substring(lastdot+1, wordToComplete.length());
				}
			}

			// Multiple matches, data will hold sorted list of matching objects
			ITrie trie = (ITrie) searchMap.get(keyIterator);

			if(trie == null) {
				return results;
			}

			SortedMap<?, ?> trieSpecificData = (SortedMap<?, ?>) trie.getSortedMap(wordToComplete); // Sort the trie

			Iterator<?> dataIt = trieSpecificData.keySet().iterator();
			while(dataIt.hasNext()) {
				String valueOfListelement = (String) trieSpecificData.get((String) dataIt.next());

				// DEBUG with dot-completion
				// Needed as too many elements slow down code completion		
				// add only elements from wordtocomplete to the next dot		
				// addstr = returnstring MINUS wordtocomplete
				createCompletionProposalEntry(wordToComplete, results,	cpFormatter, valueOfListelement,
						isMemberOfSrcFile, keyIterator);			
			}

		}		
		return results;
	}

	/**
	 * create completion proposal Entry
	 * @param wordToComplete
	 * @param data
	 * @param cpFormatter
	 * @param valueOfListelement
	 */
	protected void createCompletionProposalEntry(String wordToComplete,
			ArrayList<Object> data, CompletionProposalFormatter cpFormatter,
			String valueOfListelement, boolean isMemberOfSrcFile, String assemblyPath) {

		//		CompletionProposal compProp = null;
		Object compProp = null;
		if(valueOfListelement != ""){
			String addStr = valueOfListelement.substring(wordToComplete.length());
			//take first substring after wordtocomplete ended by "." or " "
			String uniqueEntryString = wordToComplete + addStr.split("[. ]")[0]; 
			//END DEBUG with dot-Completion
			if(!uniqueEntry.contains(uniqueEntryString)){
				if(isThis){
					compProp = cpFormatter.getFormattedProposalForThis(wordToComplete,
							valueOfListelement, documentOffset, labelProvider, cpFlags, assemblyPath);
				}else if(this.isEmpty){
					compProp = cpFormatter.getFormattedProposalForEmpty(wordToComplete,
							valueOfListelement, documentOffset, labelProvider, cpFlags, assemblyPath);
				}else if(!wordToComplete.equals(variable)){
					boolean isVarCompl = false;
					if(!this.variable.equals("")){
						isVarCompl = true;
					}
					boolean isTypeOnly = false;
					if(!typename.equals(wordToComplete)){
						isTypeOnly = true;
					}
					compProp = cpFormatter.getFormattedProposalForStandard(wordToComplete,
							valueOfListelement, documentOffset, labelProvider, isVarCompl, variable,
							isTypeOnly, isMemberOfSrcFile, originalStringLength, cpFlags,
							assemblyPath);
					//System.out.println("DEBUG Standardsession: " + wordToComplete + " " + valueOfListelement);
				}

				if(compProp != null){
					data.add(compProp);						
					uniqueEntry.add(uniqueEntryString);
				}						
			}					 
		}
	}

	/**
	 * get the filepath for the according typename
	 * @param scName
	 * @return
	 */
	protected HashSet<?> getFilepathOfMetatypes(String typename) {
		return CCAdministration.getInstance().getMetatypes(typename);
	}


	/*protected HashSet getAllFilePathsOfGACandProjects(){
		return CCAdministration.getInstance().getAllFilePaths();
	}*/
	/**
	 * 
	 * @param actualFile
	 */
	protected ICompilationUnit getCompUnitFromAssembly(String filePath) {
		Map[] maps = null;
		IAssembly asse = null;
		maps = new Map[]{Collections.EMPTY_MAP, Collections.EMPTY_MAP};
		asse =AssemblyParserFactory.createAssemblyParser().parseAssembly(
				new Path(filePath),
				maps);			
		return asse;
	}

	/**
	 * get IType recursively out of namespace
	 * @param typename
	 * @param nestedCompUnit
	 * @return
	 */
	protected IType getITypeRecursively(String typename,
			ICompilationUnit nestedCompUnit) {
		IType itype = null;
		IType[] types = nestedCompUnit.getTypes();
		INamespace[] namespaces = nestedCompUnit.getNamespaces();
		String typeNameInAssembly = "<nousefultypename>";
		boolean found = false;
		//search types
		for(int i=0;i<types.length;i++){
			int lastDotInOriginalTypeName = types[i].getElementName().lastIndexOf(".");
			if(lastDotInOriginalTypeName > 0){
				typeNameInAssembly = types[i].getElementName().substring(lastDotInOriginalTypeName, 
						types[i].getElementName().length());
			}else{
				typeNameInAssembly = types[i].getElementName();
			}								
			//IMPORTANT: compare lowercase-strings!
			if(typeNameInAssembly.toLowerCase().equals(typename.toLowerCase())){
				itype = types[i];
				found = true;
				break;
			}
			//DEBUG

		}
		//search types in namespaces
		if(!found){
			for(int i =0; i<namespaces.length;i++){
				types = namespaces[i].getTypes();

				for(int j=0; j<types.length;j++){
					int lastDotInOriginalTypeName = types[j].getElementName().lastIndexOf(".");
					if(lastDotInOriginalTypeName > 0){
						typeNameInAssembly = types[j].getElementName().substring(lastDotInOriginalTypeName, 
								types[j].getElementName().length());
					}else{
						typeNameInAssembly = types[j].getElementName();
					}								
					if(typeNameInAssembly.toLowerCase().equals(typename.toLowerCase())){
						itype = types[j];
						found = true;
						break;
					}
					//DEBUG
					//System.out.println(typeNameInAssembly);
				}
			}
		}
		return itype;
	}

	/**
	 * search for namespaces in file
	 * @param typename
	 * @param nestedCompUnit
	 * @return
	 */
	protected HashSet<String> getNamespaceNamesRecursively(String filepath) {
		HashSet<String> nsSet = new HashSet<String>();		
		HashSet<?> insSet = CCAdministration.getInstance().getRevertedMetatypes(filepath);
		String name = "";
		Iterator<?> iter = insSet.iterator();
		int sizeTS = wordToComplete.length();
		//get level of typename => only dot+1 level is allowed (e.g. typename: system, allowed: system.xxx, not system.xxx.yyy)
		int countDots = wordToComplete.split("\\.").length;
		if(wordToComplete.endsWith(".")){
			countDots++;
		}			
		while(iter.hasNext()){
			Metatype mt = (Metatype)iter.next();
			name = mt.getTypename();
			String[] nameSplitted = name.split("\\.", countDots+1); // ".split(".")"
			if(nameSplitted.length >= countDots){
				String subName="";
				subName = nameSplitted[0];
				for(int i=1;i<countDots;i++){
					subName = subName + "." +nameSplitted[i];
				}
				//check if typename equals subName-String
				if(sizeTS <= subName.length()){
					if(subName.substring(0, sizeTS).toLowerCase().equals(wordToComplete.toLowerCase())){					  
						nsSet.add(subName);
					}
				}
			}
		}
		return nsSet;
	}

	/**
	 * 
	 * @param scPath
	 * @return
	 */
	protected IProject getProjectFromFile(String scPath, boolean isGAC) {
		// FIXME at the moment not implemented to get project according to scPath-File... 
		//		IProject project = null;
		if(isGAC){
			return null;
		}
		return actualProject;
	}

	/*
	 * check if file is in GAC
	 */
	protected boolean checkIfGAC(String scPath) {
		HashMap<?, ?> gacMap = CCAdministration.getInstance().getGACMap();
		//filepath = key
		if(gacMap.containsKey(scPath)){
			return true;
		}		
		return false;
	}


	/*
	 * check if file is dll or exe
	 */
	protected boolean checkIfDll(String scPath) {
		if(scPath.toLowerCase().endsWith(".dll") || 
				scPath.toLowerCase().endsWith(".exe")){
			return true;
		}
		return false;
	}

	/**
	 * get the method variables of the CurrentCursorPosition object
	 * @return
	 */
	protected LinkedList<Object> getVariablesOfDocOffsetMethod(){
		LinkedList<Object> returnList = new LinkedList<Object>();
		HashMap<String, ITrie> emptyStringAdditionalMap = new HashMap<String, ITrie>();
		HashMap<?, ?> esl = this.cpc.getVariablesOfMethod();
		emptyStringAdditionalMap.put("EmptyStringVariables", CCAdministration.getInstance().
				buildTrieForExternalList(esl));
		returnList.addAll(searchInTries(wordToComplete, emptyStringAdditionalMap, false));
		return returnList;
	}

	/**
	 * get the signature parameters of the CurrentCursorPosition object
	 * @return
	 */
	protected LinkedList<Object> getSignatureParamsOfDocOffsetMethod(){
		LinkedList<Object> returnList = new LinkedList<Object>();
		HashMap<String, ITrie> emptyStringAdditionalMap = new HashMap<String, ITrie>();
		HashMap<?, ?> esl = this.cpc.getMethodSignatureElements();
		emptyStringAdditionalMap.put("EmptyStringMethods", CCAdministration.getInstance().
				buildTrieForExternalList(esl));
		returnList.addAll(searchInTries(wordToComplete, emptyStringAdditionalMap, false));
		return returnList;
	}

	/*
	 * get decompressed trie
	 */
	protected ITrie getDecompressedTrie(HashMap<?, ?> projectMap, String filepath){
		String thePath = normalizePath(filepath);
		ITrie itrie = null;
		//time measurement
		//		long start = new Date().getTime();	

		if (uncompressedCacheITries.containsKey(thePath))
			itrie = (ITrie)uncompressedCacheITries.get(thePath);
		else {
			ByteArrayOutputStream baos = (ByteArrayOutputStream) projectMap.get(thePath);
			if(baos != null) {
				CompressionTool ct = new CompressionTool();
				itrie = (ITrie) ct.decompressObject(baos);
				uncompressedCacheITries.put(thePath, itrie);
			}
			//scheduleRemoveUncompressedCache(filepath, false);
		}

		//		long runningTime = new Date().getTime() - start;
		//		System.out.println("DEBUG Session.java: " + filepath + ", time for DEcompression: " + runningTime);

		return itrie;
		//return (ITrie) projectMap.get(filepath);
	}

	//	public ICompilationUnit tryGetUncompressedCompilationUnitQuickly(String filename) {
	//		String theFileName = normalizePath(filename);
	//		if (uncompressedCacheICompilationUnits.containsKey(theFileName))
	//			return (ICompilationUnit)uncompressedCacheICompilationUnits.get(theFileName);
	//		else
	//			return null;
	//	}

	//	public void addUncompressedCompilationUnit(String filename, ICompilationUnit unit) {
	//		uncompressedCacheICompilationUnits.put(filename, unit);
	//		// schedule removal
	//		scheduleRemoveUncompressedCache(filename, true);
	//	}
	//	

	//	public void scheduleRemoveUncompressedCache(String filename, boolean compUnitHashmap) {
	//		new RemoveUncompressedCacheJob(filename,compUnitHashmap).schedule(removeCacheAfterMs);
	//	}

	public void finalize(){
		DebugUtil.DebugPrint("End of Code Completion Session!", printRealy);
		rectimer.Stop();
		rectimer.Output(printRealy);
		xtimer.Stop();
		xtimer.Output(printRealy);
		ytimer.Stop();
		ytimer.Output(printRealy);
		ztimer.Stop();
		ztimer.Output(printRealy);
	}

	//	class RemoveUncompressedCacheJob extends Job {
	//
	//		final String filename;
	//		final boolean compUnitHashmap;
	//		
	//		public RemoveUncompressedCacheJob(String filename, boolean compUnitHashmap) {
	//			super("Remove uncompressed CC Cache");
	//			this.filename = filename;
	//			this.compUnitHashmap = compUnitHashmap;
	//		}
	//		protected IStatus run(IProgressMonitor monitor) {
	//			if (compUnitHashmap)
	//				uncompressedCacheICompilationUnits.remove(filename);
	//			else
	//				uncompressedCacheITries.remove(filename);
	//			return Status.OK_STATUS;
	//		}
	//	}

	public static String normalizePath(String inpath){
		String result = inpath.replace('\\','/'); // replace / by \
		result = result.replaceAll("/+", "/");  // replace // by /
		result=result.toLowerCase();
		return result;
	}
}
