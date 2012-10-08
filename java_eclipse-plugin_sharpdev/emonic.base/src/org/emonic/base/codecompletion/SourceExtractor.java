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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.emonic.base.codecompletion.datastructure.ITrie;
import org.emonic.base.codehierarchy.ICompilationUnit;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;
import org.emonic.base.infostructure.CSharpCodeParser;

/*
 * A source extractor object is needed for each project
 */
public class SourceExtractor extends Extractor{

	private final String CS_LOWER_CASE = ".cs";
	private final String CS_HIGHER_CASE = ".CS";
	private HashSet srcDirs;
	
	public SourceExtractor(IProject project){
		super(project);
		srcDirs = new HashSet();
	}

	/**
	 * Get the directories for cs file extraction
	 * @return
	 */
	public HashSet getDirs() {
				
		//1)get srcDirs from actual project configuration
		//... make use of usedProject!
		getDirOfDir(new File(usedProject.getLocation().toPortableString()), srcDirs);
				
		//2) get src-files from project itself
		//... make use of usedProject!
		//srclist contains strings of paths for src dirs
		ArrayList srclst = new ProjectPreferencesManipulator(usedProject).getCodeCompletionSrc();
		Iterator iter = srclst.iterator();

		while (iter.hasNext()) {
			//recursive search			
			getDirOfDir(new File((String) iter.next()), srcDirs);
		}
		return srcDirs;				
		
	}
	
	/**
	 * Iterate through the vector of directories of cs-files
	 * and add all cs-files to the return vector
	 */
	
	public HashSet addFiles(HashSet dirs) {
		HashSet fileSet = new HashSet();
		
		Iterator dirsIterator = dirs.iterator();
		while(dirsIterator.hasNext()){
			
			File srcDir = new File((String)dirsIterator.next());
			
			if(srcDir.isDirectory()){
				
				String[] files = srcDir.list();
				
				for(int i = 0; i < files.length; i++){
					//search for cs files
					if((files[i].endsWith(CS_LOWER_CASE) || 
					    files[i].endsWith(CS_HIGHER_CASE))){							
							fileSet.add(new File(srcDir, files[i]));						
					}
				}
			}
		}
		return fileSet;

	}

	
	public void extractFile(ICompilationUnit unit, String filepath, boolean isDeep){
		ITrie itrie = buildTrieForFile(unit, isDeep, filepath);
		CCAdministration.getInstance().addTrieToProject(usedProject, filepath, itrie);						 
		CCAdministration.getInstance().putToTLMap(usedProject, filepath, new Boolean(isDeep));
	}

	
	
	public void extractFile(File actualFile, boolean isDeep) {		
//		Map[] maps = null;
//		maps = new Map[]{Collections.EMPTY_MAP, Collections.EMPTY_MAP};
		CSharpCodeParser parser = new CSharpCodeParser();
		parser.init(actualFile);
		ISourceUnit srcUnit = parser.parseDocument();
		
		ITrie itrie = buildTrieForFile(srcUnit, isDeep, actualFile.getAbsolutePath());
		CCAdministration.getInstance().addTrieToProject(usedProject, actualFile.getAbsolutePath(), itrie);						 
		CCAdministration.getInstance().putToTLMap(usedProject, actualFile.getAbsolutePath(), new Boolean(isDeep));
		CCAdministration.getInstance().ClearSourceFileCache(actualFile.getAbsolutePath());
		//INFO: putToTypeMap happens in Extractor.java => shallow add of types!
	}

	
}
