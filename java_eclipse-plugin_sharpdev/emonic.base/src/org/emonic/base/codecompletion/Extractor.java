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

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.emonic.base.codecompletion.datastructure.CharSequenceKeyAnalyzer;
import org.emonic.base.codecompletion.datastructure.ITrie;
import org.emonic.base.codecompletion.datastructure.PatriciaTrie;
import org.emonic.base.codehierarchy.ICompilationUnit;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IEvent;
import org.emonic.base.codehierarchy.IField;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.codehierarchy.INamespace;
import org.emonic.base.codehierarchy.IProperty;
import org.emonic.base.codehierarchy.IType;

public abstract class Extractor implements IExtractor{

	protected IProject usedProject = null;
	
	
	public Extractor(IProject project){
		usedProject = project;
	}	
	
	/**
	 * This is called for each project!
	 * This method iterates over the vector with cs-files and
	 * adds the builded trieMap together with the file path to
	 * a hashmap 
	 * @param assembliesToTrie
	 * @return
	 */
	public void iterateFilesToTrieMapAtStartup(HashSet filesToTrie) {
		iterateFilesToTrieMap(filesToTrie,false);
	}
	

	
	public void iterateFilesToTrieMap(HashSet filesToTrie,boolean deep) {
		Iterator iter = filesToTrie.iterator();
		while(iter.hasNext()){			
			File actualFile = (File)iter.next();
			extractFile(actualFile, false );			
		}
		
	}
	
	
	/**
	 * Build the trie for the given  file
	 * @param root, the File
	 * @param deep false=only shallow info of root, true=deep info of root
	 * @return
	 */
	protected ITrie buildTrieForFile(ICompilationUnit unit, boolean isDeep, String actualFilePath) {

		ITrie srcTrie = new PatriciaTrie(new CharSequenceKeyAnalyzer());
		if (unit != null){
			INamespace[] namespaces = unit.getNamespaces();
			for(int i=0; i < namespaces.length; i++){
				String namespaceKey = namespaces[i].getElementName();
				//namespace itself has to be added element of file 
				// + src name for identification
				String namespaceValue = namespaceKey + getNamespaceMetadata(namespaceKey);
				//add to metadata
				CCAdministration.getInstance().putToMetatypes(namespaceKey,
						new Metatype("" /*""*/,
								namespaceKey,
								namespaceValue,
								actualFilePath, 
								true),
								actualFilePath);
				insertIntoTrie(srcTrie, namespaceKey, namespaceValue);
				//add types and subelements to trie
				IType[] dotnetTypes = namespaces[i].getTypes();
				if(isDeep){
					addTypesDeepToTrie(srcTrie, dotnetTypes, namespaceKey);
				}else{
					addTypesShallowToTrie(srcTrie, dotnetTypes, namespaceKey,actualFilePath);
				}
			}				

			//get Types[] of file
			IType[] types = unit.getTypes();
			if(isDeep){
				addTypesDeepToTrie(srcTrie, types, "");
			}else{
				addTypesShallowToTrie(srcTrie, types, "",actualFilePath);
			}					
		}
		return srcTrie;
	}

	/**
	 * Inserts a key-value pair into a trie
	 * @param assemblyTrie
	 * @param namespaceName
	 * @param namespaceText
	 */
	protected void insertIntoTrie(ITrie trie, String key, String value) {
		
		//add case sensitivity
		//deprecated!
		value = value + " " + MetadataElements.OriginalElementString + key;
		key = key.toLowerCase();
		//end add case sensitivity
		trie.add(key, value);
	}

	/**
	 * Add types and namespaces to the trie
	 * @param fileTrie
	 * @param dotnetTypes
	 * @param namespaceKey
	 */
	protected void addTypesShallowToTrie(ITrie fileTrie, IType[] dotnetTypes, 
								String namespaceKey, String actualFilePath){
		//get dotnetelements of types like classes
		for(int j=0; j<dotnetTypes.length; j++){
			String typeKey = "";
			if(!namespaceKey.equals("")){
				typeKey = namespaceKey + ".";
			}
			typeKey = typeKey + dotnetTypes[j].getElementName();
			String typeValue = typeKey + getTypeMetadata(dotnetTypes[j]);
			
			//this adds types to the typeMap to get a type <=> filename mapping
			CCAdministration.getInstance().putToMetatypes(dotnetTypes[j].getElementName(), 
														  new Metatype(dotnetTypes[j].getElementName(),
															     namespaceKey,
															     typeValue,
															     actualFilePath, 
															     false),
														  actualFilePath);

			//full namespace name + typename
			//FIXME unclear if needed...
			CCAdministration.getInstance().putToMetatypes(typeKey, 
														  new Metatype(typeKey,
																       namespaceKey,
																       typeValue/*typeKey*/,
																       actualFilePath,
																       false),
														  actualFilePath);
			
			
			//The following if-condition tends not be useful
			//many string-compares, only few entries are filtered...
			//----
			//insert only to trie if one of the following conditions is not true
			// string starts with $ArrayType$ or <PrivateImplementationDetails> or ends 
			//with .ctor()
			//if(!typeKey.startsWith("$ArrayType$") &&
			//   !typeKey.startsWith("<PrivateImplementationDetails>") &&
			//   !typeKey.startsWith("<>c__") &&
			//   !typeKey.endsWith(".ctor")){
			insertIntoTrie(fileTrie, typeKey, typeValue);

		}
		
	}
	
	/**
	 * Add types, namespaces and their inner elements to the trie 
	 * TODO inner classes, structs and enums missing
	 * @param assemblyTrie
	 * @param namespaces
	 * @param i
	 * @param namespaceKey
	 */
	protected void addTypesDeepToTrie(ITrie fileTrie, IType[] dotnetTypes, 
								String namespaceKey) {
		
		//get dotnetelements of types like classes
		for(int j=0; j<dotnetTypes.length; j++){
			StringBuffer typeKey = new StringBuffer();
			if(!namespaceKey.equals("")){
				typeKey.append(namespaceKey + ".");
			}
			typeKey.append(dotnetTypes[j].getElementName());
			String typeValue = typeKey + getTypeMetadata(dotnetTypes[j]);
			//DEBUG
			//System.out.println("---------------------");
			//System.out.println("typekey: " + typeKey);
			//DEBUG
					
			insertIntoTrie(fileTrie, typeKey.toString(), typeValue);
			
			//get children of types
			IDotNetElement[] dotnetelements = dotnetTypes[j].getChildren();
			for(int k=0; k<dotnetelements.length;k++){
				String typeElementKey = typeKey + "." + dotnetelements[k].getElementName();
				String shortTypeKey = dotnetTypes[j].getElementName() + "." +dotnetelements[k].getElementName();
				
				String typeElementValue = "";
				int type = dotnetelements[k].getElementType();
				
				switch(type){
					case IDotNetElement.METHOD:
						typeElementKey +=  "(" + ((IMethod)dotnetelements[k]).getSignature() + ")";
						typeElementValue = typeElementKey + getMethodMetadata((IMethod)dotnetelements[k]);
						break;
					case IDotNetElement.EVENT:
						typeElementValue = typeElementKey + getEventMetadata((IEvent)dotnetelements[k]);
						break;
					case IDotNetElement.FIELD:
						typeElementValue = typeElementKey + getFieldMetadata((IField)dotnetelements[k]);
						break;
					case IDotNetElement.PROPERTY:
						typeElementValue = typeElementKey + getPropertyMetadata((IProperty)dotnetelements[k]);
						break;
				}
				typeElementValue = typeElementValue + " " + MetadataElements.TypeWithoutNamespace + shortTypeKey;
				insertIntoTrie(fileTrie, typeElementKey, typeElementValue);					
			}
		}
	
	}

	/**
	 * returns some of the metadata of the property in a well formed string
	 * Metadata: IDotnetElementType + Flags + signature + return-value
	 * @param dotNetElement
	 * @return
	 */
	protected String getPropertyMetadata(IProperty property) {
		StringBuffer retstr = new StringBuffer();
		retstr.append(" "+MetadataElements.IDotNetElement);
		retstr.append(property.getElementType());
		retstr.append(" "+MetadataElements.Flags);
		retstr.append(property.getFlags());
		retstr.append(" "+MetadataElements.Signature);
		retstr.append(property.getSignature());
		retstr.append(" "+MetadataElements.Return);
		retstr.append(property.getReturnType());
		return retstr.toString();
	}

	/**
	 * returns some of the metadata of the method in a well formed string
	 * Metadata: IDotnetElementType + Flags
	 * @param field
	 * @return
	 */
	protected String getFieldMetadata(IField field) {
		StringBuffer retstr = new StringBuffer();
		retstr.append(" "+MetadataElements.IDotNetElement);
		retstr.append(field.getElementType());
		retstr.append(" "+MetadataElements.Flags);
		retstr.append(field.getFlags());
		retstr.append(" " +MetadataElements.Type);
		retstr.append(field.getTypeSignature());
		return retstr.toString();
	}

	/**
	 * returns some of the metadata of the method in a well formed string
	 * Metadata: IDotnetElementType + Flags + delegatetype
	 * @param event
	 * @return
	 */
	protected String getEventMetadata(IEvent event) {
		StringBuffer retstr = new StringBuffer();
		retstr.append(" "+MetadataElements.IDotNetElement);
		retstr.append(event.getElementType());
		retstr.append(" "+MetadataElements.Flags);
		retstr.append(event.getFlags());
		retstr.append(" "+MetadataElements.DelegateType);
		retstr.append(event.getDelegateType());
		return retstr.toString();
	}

	/**
	 * returns some of the metadata of the method in a well formed string
	 * Metadata: IDotnetElementType + Flags + signature + return-value
	 * @return
	 */
	protected String getMethodMetadata(IMethod method) {
		StringBuffer retstr = new StringBuffer();
		retstr.append(" "+MetadataElements.IDotNetElement);
		retstr.append(method.getElementType());
		retstr.append(" "+MetadataElements.Flags);
		retstr.append(method.getFlags());
		retstr.append(" "+MetadataElements.Signature);
		retstr.append(method.getSignature());
		retstr.append(" "+MetadataElements.Return);
		retstr.append(method.getReturnType());
		return retstr.toString();
	}

	/**
	 * the default namespace metadata string
	 * @return
	 */
	protected String getNamespaceMetadata(String namespaceName){
		StringBuffer retstr = new StringBuffer();
		retstr.append(" " + MetadataElements.IDotNetElement);
		retstr.append(IDotNetElement.NAMESPACE);
		retstr.append(" " + MetadataElements.Type);
		retstr.append(namespaceName);
		return retstr.toString();
	}
	
	/**
	 * returns some of the metadata of the type in a well formed string
	 * Metadata: IDotnetElementType + Flags
	 * @param type
	 * @return
	 */
	protected String getTypeMetadata(IType type) {
		StringBuffer retstr = new StringBuffer();
		retstr.append(" "+MetadataElements.IDotNetElement);
		retstr.append(type.getElementType());
		retstr.append(" "+MetadataElements.Flags);
		retstr.append(type.getFlags());
		retstr.append(" "+MetadataElements.Superclass);
		retstr.append(type.getSuperclassName());
		retstr.append(" " + MetadataElements.Type);
		retstr.append(type.getElementName());
		return retstr.toString();
	}
	
	/**
	 * get recursively all directories of the given folder
	 * @param dir
	 * @param vecToAdd
	 */
	protected void getDirOfDir(File dir, HashSet setToAdd) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				getDirOfDir(new File(dir, children[i]), setToAdd);
				setToAdd.add(dir.getAbsolutePath().toString());
			}
		} 
	}

	public void setProject(IProject project) {
		usedProject=project;
	}


	
}
