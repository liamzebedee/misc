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

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.emonic.base.codecompletion.compression.CompressionTool;
import org.emonic.base.codecompletion.datastructure.CharSequenceKeyAnalyzer;
import org.emonic.base.codecompletion.datastructure.Fifo;
import org.emonic.base.codecompletion.datastructure.ITrie;
import org.emonic.base.codecompletion.datastructure.PatriciaTrie;
import org.emonic.base.codecompletion.session.Session;
import org.emonic.base.codehierarchy.Flags;
import org.emonic.base.codehierarchy.ICompilationUnit;
import org.emonic.base.codehierarchy.IDotNetElement;


/**
 * CCAdministration holds:
 * (a) GAC: the global reachable HashMap of dlls/exes and
 *  their according Tries
 * (b) Project specific Tries with a HashMap (key=project, value=map)
 *  of Hashmaps2 (key = filename, value=trie)
 *  HashMaps2 contain srcs and dlls (the actual src files of the project
 *  plus the srcs and dlls of the project preference page)
 * @author dertl
 *
 */
public class CCAdministration implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * gacMap 
	 * it holds filenames as key and tries as values
	 * The values may represent the complete datastructure of
	 * the file or only the types (see typeLoadedMap)
	 */
	private HashMap gacMap = null;
	
	/*
	 * typeLoadedMap holds a boolean to represent if 
	 * the according file has been searched completely (true)
	 * or only for types (false)
	 * used for ALL files (GAC and project specific maps)
	 */
	private HashMap gactypeLoadedMap = null;
	
	/**
	 * isGACMap holds a boolean value for each assembly file if it 
	 * is part of the gac or project specific
	 */
	private HashMap isGACMap = null;
	
	private HashMap projectCCMap = null;
	
	private HashMap projecttypeLoadedMap = null;
	
	/**
	 * typeMap contains all type names as keys and as values the 
	 * file names where this type exists...
	 */
	//private HashMap typeMap = null;
	private HashMap metatypes;
	private HashMap revertedMetatypes;
	
	/**
	 * the revertedTypeMap contains as keys filenames and as values all types
	 * within such a file => it is needed to keep typeMap coherent if files (thus types!) 
	 * are deleted, removed etc
	 */
	//private HashMap revertedTypeMap = null;
	
	
	/**
	 * The trie which holds all methods from system.object
	 */
	private ITrie systemObjectTrie = null;
	
	
	private Fifo sourceFileCache = null;
	private Fifo assemblyFileCache = null;
	
	private final static CCAdministration INSTANCE = new CCAdministration();
	
	
	
	// Private constructor suppresses generation of
	// a (public) default constructor
	private CCAdministration() {
		gacMap = new HashMap();
		isGACMap = new HashMap();
		gactypeLoadedMap = new HashMap();
		projectCCMap = new HashMap();
		projecttypeLoadedMap = new HashMap();
		metatypes = new HashMap();
		revertedMetatypes = new HashMap();
		sourceFileCache = new Fifo();
		assemblyFileCache = new Fifo();
		buildObjectTrie();
	}
	


	public static CCAdministration getInstance() {
		return INSTANCE;
	}

	/*
	 * return the project hashmap for a given project
	 * @param project
	 * @return
	 */
	public HashMap getProjectCCMap(IProject project){
		HashMap retMap =  (HashMap) projectCCMap.get(project.getLocation().toPortableString());
		if(retMap != null){
			return retMap;
		}
		return new HashMap(); 
	}
	
	/**
	 * return the project type loaded hashmap for a given project
	 * @param project
	 * @return
	 */
	public HashMap getProjectTypeLoadedMap(IProject project){
		HashMap retMap =  (HashMap) projecttypeLoadedMap.get(project.getLocation().toPortableString());
		if(retMap != null){
			return retMap;
		}
		return new HashMap(); 
	}
	
	/**
	 * add a project to the projects hashmap
	 * the key is the projects full path, the value is a newly created hashmap
	 * @param project
	 */
	public void addProject(IProject project){
		String projectPath = project.getLocation().toPortableString();
		this.projectCCMap.put(projectPath, new HashMap());
		this.projecttypeLoadedMap.put(projectPath, new HashMap());
	}
	
	/**
	 * add a trie to the distinct project map
	 * @param project
	 * @param key
	 * @param itrie
	 */
	public void addTrieToProject(IProject project, String key, ITrie itrie) {
		//get distinct project hashmap from projectCCMap
		HashMap projectMap = (HashMap) this.projectCCMap.get(project.getLocation().toPortableString());
		//in case project is opened and project is created after files are added to the trie
		if(projectMap == null){
			addProject(project);
			//reread projectMap
			projectMap = (HashMap) this.projectCCMap.get(project.getLocation().toPortableString());
		}
		
		//compress trie with LZFOutputStream
		CompressionTool comp = new CompressionTool();
		ByteArrayOutputStream compressedITrie = comp.compressObject(itrie);
		if(compressedITrie == null){
			System.out.println("ERROR: project ITrie not compressable. " + key);
			return;
		}
		//add trie to projectCCMap
		projectMap.put(Session.normalizePath(key), compressedITrie); //itrie);
		
		//DEBUG
		/*if(key.toLowerCase().endsWith("myworld.cs")){

			try {
				//itrie
				FileOutputStream file_output = new FileOutputStream (new File("/home/dertl/myworld.trie"));
		        ObjectOutputStream out = new ObjectOutputStream(file_output);
		        out.writeObject(itrie);
		        //Close the output stream
		        out.close();
		        //uncompressed trie
				FileOutputStream file_output2 = new FileOutputStream (new File("/home/dertl/myworld.compressed"));
		        ObjectOutputStream out2 = new ObjectOutputStream(file_output2);
		        out2.writeObject(comp.decompressObject(compressedITrie));
		        //Close the output stream
		        out2.close();
		        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		//end DEBUG
	}



	
	/**
	 * 
	 * @return
	 */
	public HashMap getGACMap(){	
		return gacMap;
	}
	
	/**
	 * add element to gac map
	 * @param portableString
	 * @param buildTrieForAssembly
	 */
	public void putToGACMap(String key, ITrie itrie) {
		//compress trie with LZFOutputStream
		CompressionTool comp = new CompressionTool();
		ByteArrayOutputStream compressedITrie = comp.compressObject(itrie);
		if(compressedITrie == null){
			System.out.println("ERROR: project ITrie not compressable. " + key);
			return;
		}
		this.gacMap.put(key, compressedITrie); //itrie);
		
	}	
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Boolean getTypeLoadedBool(IProject project, String key) {
		String projectName = null;
		if(project != null)
			projectName = project.getLocation().toPortableString();
		
	    Boolean val = Boolean.FALSE;
		if(projectName == null || projectName.equals("")){
 			val =  (Boolean) this.gactypeLoadedMap.get(key);
		}else{
			//get key/value of according hash map
			if (this.projecttypeLoadedMap != null && this.projecttypeLoadedMap.get(projectName) != null)
			   val =  (Boolean)((HashMap)this.projecttypeLoadedMap.get(projectName)).get(key);
		}	
		if(val == null)
		{
			val = Boolean.FALSE;
		}
		return val;
	}	

	/**
	 * add element to type loaded map
	 * @param key
	 * @param value
	 */
	public void putToTLMap(IProject project, String key, Boolean value) {
		String projectName = null;
		if(project != null)
			projectName = project.getLocation().toPortableString();
		
		if(projectName == null || projectName.equals("")){
			this.gactypeLoadedMap.put(key, value);
		}else{
			//put key/value to according hash map
			((HashMap)this.projecttypeLoadedMap.get(projectName)).put(key, value);
		}
				
	}
	
    /**
     * remove entries from type loaded map
     * @param project
     * @param filepath
     */
	public void removeFromProjectTLMap(String path) {
		if(path != null){
			this.projecttypeLoadedMap.remove(path);
		}
	}

	/**
	 * remove entries from projectCCMap
	 * @param filepath
	 */
	public void removeFromProjectCCMap(String path) {
		if(path != null){
			this.projectCCMap.remove(path);
		}
		
	}
	
    /**
     * 
     * @param filename
     */
	public void removeFileFromProjectCCMap(String projectpath, String filename) {
		if(projectpath != null && filename != null){
			((HashMap) this.projectCCMap.get(projectpath)).remove(filename);
		}
		
	}

	/**
	 * 
	 * @param project
	 * @param filename
	 */
	public void removeFileFromProjectTLMap(String projectpath, String filename) {
		if(projectpath != null && filename != null){
			((HashMap) this.projecttypeLoadedMap.get(projectpath)).remove(filename);
		}
		
		
	}	

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public Boolean getIsGACBool(String filepath) {
		Boolean retVal = (Boolean)this.isGACMap.get(filepath);
		if(retVal == null){
			return new Boolean(false);
		}
		return (Boolean)this.isGACMap.get(filepath);
	}

	/**
	 * add element to type loaded map
	 * @param key
	 * @param value
	 */
	public void putToIsGACMap(String key, Boolean value) {
		this.isGACMap.put(key, value);		
	}
	
	/**
	 * put type names with their according filename to map
	 * @param typename
	 * @param filename
	 */
	public void putToMetatypes(String typename, Metatype metatype, String filename){
		
		typename = typename.toLowerCase();
		//UPDATE TYPE MAP
		//check if typename exists
		if(this.metatypes.containsKey(typename)){
			innerUpdateForPutToMetatype(typename, metatype, filename, this.metatypes);
		}
		//no type exists, add hashset which contains filenames where type exist
		else{
			HashSet metatypeSet = new HashSet(1);
			metatypeSet.add(metatype);
			this.metatypes.put(typename, metatypeSet);
		}				
		
		//UPDATE REVERTED TYPE MAP
		if(this.revertedMetatypes.containsKey(filename)){
			innerUpdateForPutToMetatype(filename, metatype, typename, this.revertedMetatypes);		
		}else{
			HashSet metatypeSet = new HashSet(1);
			metatypeSet.add(metatype);
			this.revertedMetatypes.put(filename, metatypeSet);
		}
	}

	private void innerUpdateForPutToMetatype(String key,
			Metatype metatype, String value, HashMap map) {
		//add filename to given hashset
		HashSet alltypes = ((HashSet)map.get(key));
		
		//search through complete list and check if filename exists in hashset of metatypes
		//if yes => remove old and add new one
		HashSet alltypesCopy = new HashSet(1);
		alltypesCopy.addAll(alltypes);
		//copied list needed, otherwise a concurrent modification exception is thrown
		//as items are removed from the set and the iterator is not valid any more.
		Iterator iter = alltypesCopy.iterator();
		boolean exist = false;
		while(iter.hasNext()){
			Metatype mt = (Metatype) iter.next();			
			if(mt.equals(metatype)){
				exist = true;
			}
		}
		if(!exist){
			alltypes.add(metatype);
		}
		
	}
	
	/**
	 * get Type Map
	 * @param typename
	 * @return
	 */
	public HashSet getMetatypes(String typename){				
		
		if(this.metatypes.containsKey(typename.toLowerCase())){
			return (HashSet)this.metatypes.get(typename.toLowerCase());
		}
		return new HashSet();
	}
	
	/**
	 * get all types where parameter "incomplete typename"
	 * matches the typename
	 * @param incTypename
	 * @return
	 */
	public HashSet getMetatypesOfIncompleteType(String incTypename){
		HashSet hset = new HashSet();
		HashSet internalUniqueString = new HashSet(1);
		Set keyset = this.metatypes.keySet();
		Iterator ksit = keyset.iterator();
		while(ksit.hasNext()){			
			String key = (String) ksit.next();
			if(incTypename.length() <= key.length() &&
			   incTypename.toLowerCase().
			   				equals(key.substring(0, incTypename.length()).toLowerCase())){			   	
			   HashSet internalSet = (HashSet)this.metatypes.get(key.toLowerCase());
			   Iterator internalIter = internalSet.iterator();
			   while(internalIter.hasNext()){
				   Metatype mt = (Metatype) internalIter.next();
				   if(!internalUniqueString.contains(mt.getFilepath())){
					   internalUniqueString.add(mt.getFilepath());
					   hset.add(mt);
				   }
			   }			   	
			}
		}

		return hset;
	}
	/**
	 * return reverted type map
	 * @param filepath
	 * @return
	 */
	public HashSet getRevertedMetatypes(String filepath){
		if(this.revertedMetatypes.containsKey(filepath)){
			return (HashSet)this.revertedMetatypes.get(filepath);
		}
		return new HashSet(1);
	}
	
	/**
	 * used if a file is closed, deleted, removed from project...
	 * remove for all types in file their entries of the according
	 * type hashset
	 * + remove hashset of file with types from revertedtypemap
	 */
    public void removeFromMetatypes(String filename){
    	HashSet hset = (HashSet)this.revertedMetatypes.get(filename);
    	
    	//remove elements from typeMap
    	if(hset != null){
	    	Iterator iter = hset.iterator(); //iterator to all elements    	
	    	while(iter.hasNext()){
	    		Metatype mt = (Metatype) iter.next();
	    		String typename = mt.getTypename();
	    		HashSet typeSet = (HashSet)this.metatypes.get(typename);
	    		Iterator iter2 = typeSet.iterator();
	    		//search in hashset of metatypes for the metatype with given filename
	    		while(iter2.hasNext()){
	    			Metatype mt2 = (Metatype) iter2.next();
	    			if(mt2.getFilepath().equals(filename)){
	    				typeSet.remove(mt2);
	    				break;
	    			}
	    		}
	    		if(typeSet.size() == 0){
	    			this.metatypes.remove(typename);
	    		}
	    	}
	    	
	    	//remove from reverted Typ Map
	        this.revertedMetatypes.remove(filename);
    	}else{
    		System.out.println("Unable to remove " + filename);
    	}
    }
    
	private void buildObjectTrie() {
		systemObjectTrie = new PatriciaTrie(new CharSequenceKeyAnalyzer());
		//public virtual void Finalize();
		int flags = Flags.PUBLIC_METHOD;
		String value = "Finalize " + MetadataElements.IDotNetElement +  IDotNetElement.METHOD +
					   " " + MetadataElements.Flags+ new Integer(flags).toString() + 
					   " " + MetadataElements.Signature+"()" +  " " + MetadataElements.Return+"void";					   					   
		systemObjectTrie.add("finalize()", value);
		
		//public virtual bool Equals( object obj );		
		value = "Equals " + MetadataElements.IDotNetElement +  IDotNetElement.METHOD +
			" " + MetadataElements.Flags+ new Integer(flags).toString() + 
			 " " + MetadataElements.Signature+"(object obj)" +  " " + MetadataElements.Return+"bool";		
		systemObjectTrie.add("equals(object obj)", value);
			
		//public virtual int GetHashCode();
		value = "GetHashCode " + MetadataElements.IDotNetElement +  IDotNetElement.METHOD +
		 	" " + MetadataElements.Flags+ new Integer(flags).toString() + 
		 	 " " + MetadataElements.Signature+"()" +  " " + MetadataElements.Return+"int";		
		systemObjectTrie.add("gethashcode()", value);
		
		//public Type GetType();
		value = "GetType " + MetadataElements.IDotNetElement+  IDotNetElement.METHOD +
		 	" " + MetadataElements.Flags+ new Integer(flags).toString() + 
		 	 " " + MetadataElements.Signature+"()" +  " " + MetadataElements.Return+"Type";		
		systemObjectTrie.add("gettype()", value);
		
		//public virtual string ToString();
		value = "ToString " + MetadataElements.IDotNetElement +  IDotNetElement.METHOD +
		 	" " + MetadataElements.Flags+ new Integer(flags).toString() + 
		 	 " " + MetadataElements.Signature+"()" +  " " + MetadataElements.Return+"string";		
		systemObjectTrie.add("tostring()", value);
		
		
		//public static bool Equals( object obj1, object obj2 );
		flags |= Flags.STATIC_METHOD;
		value = "Equals " + MetadataElements.IDotNetElement +  IDotNetElement.METHOD +
		 	" " + MetadataElements.Flags+ new Integer(flags).toString() + 
		 	 " " + MetadataElements.Signature+"(object obj1, object obj2)" + " " +  MetadataElements.Return+"bool";		
		systemObjectTrie.add("equals(object obj1, object obj2)", value);
		
		// public static bool ReferenceEquals( object obj1,object obj2 );	
		value = "ReferenceEquals " + MetadataElements.IDotNetElement +  IDotNetElement.METHOD +
		 	" " + MetadataElements.Flags+ new Integer(flags).toString() + 
		 	 " " + MetadataElements.Signature+"(object obj1, object obj2)" +  " " + MetadataElements.Return+"bool";		
		systemObjectTrie.add("referenceequals(object obj1, object obj2)", value);
		
		//protected object MemberwiseClone();
		flags = 0;
		flags |= Flags.PROTECTED_METHOD;
		value = "MemberwiseClone " + MetadataElements.IDotNetElement +  IDotNetElement.METHOD +
		 	" " + MetadataElements.Flags+ new Integer(flags).toString() + 
		 	 " " + MetadataElements.Signature+"()" +  " " + MetadataElements.Return+"object";		
		systemObjectTrie.add("memberwiseclone()", value);
		
	}


	/**
	 * build a trie for an arbitrary external hashmap with String key/value pair
	 * which contains STRINGS
	 * @return
	 */
	public ITrie buildTrieForExternalList(HashMap extMap){
		PatriciaTrie externalTr = new PatriciaTrie(new CharSequenceKeyAnalyzer());
		
		Iterator iter = extMap.keySet().iterator();
		while(iter.hasNext()){
			String key = (String) iter.next();
			externalTr.add(key, (String)extMap.get(key));
		}
		
		return externalTr;
	}

	/**
	 * return the systemobjecttrie
	 * @return
	 */
	public ITrie getSystemObjectTrie() {
		return this.systemObjectTrie;
	}


	/**
	 * if sourcefilecache contains filenames
	 * @param filepath
	 * @return
	 */
	public boolean isInSourceFileCache(String filepath) {		
		Iterator iter = this.sourceFileCache.iterator();
		while(iter.hasNext()){
			HashMap map = (HashMap) iter.next();
			if(map.containsKey(filepath)){
				return true;
			}
		}
		return false;
	}
	
	

	/**
	 * if assemblyfilecache contains filenames
	 * @param filepath
	 * @return
	 */
	public boolean isInAssemblyCache(String filepath) {
		Iterator iter = this.assemblyFileCache.iterator();
		while(iter.hasNext()){
			HashMap map = (HashMap) iter.next();
			if(map.containsKey(filepath)){
				return true;
			}
		}
		return false;
	}

	/**
	 * add elements to the source cache
	 * @param filepath
	 * @param nestedCompUnit
	 */
	public void addToSourceCache(Object filepath,
			Object nestedCompUnit) {
		
		HashMap element = new HashMap();
		element.put(filepath, nestedCompUnit);
		this.sourceFileCache.add(element);		
	}

	/**
	 * add object to assembly cache
	 * @param filepath
	 * @param nestedCompUnit
	 */
	public void addToAssemblyCache(String filepath,
			ICompilationUnit nestedCompUnit) {
		
		HashMap element = new HashMap();
		//compress nestedCompUnit in assemblycache!
		CompressionTool ct = new CompressionTool();
		element.put(filepath, ct.compressObject(nestedCompUnit));
		this.assemblyFileCache.add(element);	
		
	}
	
//	/**
//	 * get object from assembly cache
//	 * @param filepath
//	 * @return
//	 */
//	public ICompilationUnit getFromAssemblyCache(String filepath, Session session) {
//		Iterator iter = this.assemblyFileCache.iterator();
//		//decompress nested comp unit
//		CompressionTool ct = new CompressionTool();
//		while(iter.hasNext()){
//			HashMap map = (HashMap) iter.next();
//			if(map.containsKey(filepath)){		
//				
//				//time measurement				
////				long start = new Date().getTime();
//				
//				ICompilationUnit unit = session.tryGetUncompressedCompilationUnitQuickly(filepath);
//				if (unit == null) {
//					unit =  (ICompilationUnit) ct.decompressObject((ByteArrayOutputStream)map.get(filepath));
//					session.addUncompressedCompilationUnit(filepath, unit);
//				}
//				
////				long runningTime = new Date().getTime() - start;
////				System.out.println("DEBUG CCAdministration.java: " +filepath +" , time for DEcompression: " + runningTime);
//				
//				return unit;
//			}
//		}
//		return null;
//	}
	
	/**
	 * get object from sourcefile cache
	 * @param filepath
	 * @return
	 */
	public ICompilationUnit getFromSourceFileCache(String filepath) {
		Iterator iter = this.sourceFileCache.iterator();
		while(iter.hasNext()){
			HashMap map = (HashMap) iter.next();
			if(map.containsKey(filepath)){
				return (ICompilationUnit) map.get(filepath);
			}
		}
		return null;
	}
	

	/**
	 * 
	 * @return
	 */
	/*public HashSet getAllFilePathsx() {
		HashSet allFilesSet = new HashSet(1);
		//add GAC
		Set keySet = this.gacMap.keySet();
		Iterator iter = keySet.iterator();
		String path = "";
		while(iter.hasNext()){
			path = (String)iter.next();
			allFilesSet.add(path);
		}
		
		Set keySet2 = this.projectCCMap.keySet();
		Iterator iter2 = keySet2.iterator();
		while(iter2.hasNext()){
			HashMap map = (HashMap) projectCCMap.get(iter2.next());
			Set keySet3 = map.entrySet();
			Iterator iter3 = keySet3.iterator();
			while(iter3.hasNext()){
				path = (String) map.get(iter3.next());
			}
		}
		
		return allFilesSet;
	}*/
	
	/**
	 * debug print out
	 */
	public void printSize(){
		System.out.println("DEBUG: size of metatype " + this.metatypes.size());
		System.out.println("DEBUG: size of revertedmetatype " + this.revertedMetatypes.size());
		
		/*Set iset = this.projectCCMap.keySet();
		Iterator iter = iset.iterator();
		long sum = 0;
		while(iter.hasNext()){
			HashMap projMap = (HashMap)this.projectCCMap.get(iter.next());
			Set iset2 = projMap.keySet();
			Iterator iter2 = iset2.iterator();
			while(iter2.hasNext()){
				ITrie hugo = (ITrie) projMap.get(iter2.next());
				sum = sum + getObjectSize(hugo.toString());				
			}
		}
		
		System.out.println("DEBUG Size of TYPEMAP: "+sum + " bytes, " + sum/(1024) + " kB, "+ sum/(1024*1024) + " MB\n");
		sum = 0;
		Set iset3 = this.gacMap.keySet();
		Iterator iter3 = iset3.iterator();
		while(iter3.hasNext()){
			ITrie hugo = (ITrie) gacMap.get(iter3.next());
			sum = sum + getObjectSize(hugo.toString());				
		}
		System.out.println("DEBUG Size of GACMAP: " + sum + " bytes, " + sum/(1024) + " kB, " + sum/(1024*1024) + " MB\n");
		*/
	}



	public void ClearSourceFileCache(String filepath) {
		Iterator iter = this.sourceFileCache.iterator();
		while(iter.hasNext()){
			HashMap map = (HashMap) iter.next();
			if(map.containsKey(filepath)){
				map.remove(filepath);
			}
		}
		
	}



	

	/**
	 *  THIS IS NOT A Profiler, just for get a feeling about the size of objects
	 * measure size of object
	 * @param obj
	 * @return
	 */
//	private  long getObjectSize(Serializable obj)  {
//		   byte[] ba = null;
//		   try{
//		      ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream();
//		      ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
//		      objStream.writeObject(obj);
//		      objStream.flush();
//		      ba = byteStream.toByteArray();		      
//		   }
//		   catch ( IOException ioe )
//		   {
//		      ioe.printStackTrace();
//		      return 0;
//		   }
//		    return ba.length;
//		 }
	
	
	

}
