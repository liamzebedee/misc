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

public interface IExtractor {

	/**
	 * Get the directories for file extraction
	 * @return
	 */
	public HashSet getDirs();
	
	/**
	 * Iterate through the vector of directories of dlls
	 * and add all distinct files (e.g.: dlls, cs)
	 * to the return vector
	 * @param dirs
	 * @return
	 */
	public HashSet addFiles(HashSet dirs);
	
	/**
	 * This method iterates over the vector with files (e.g.: dlls, cs) and
	 * adds the builded trieMap together with the file path to
	 * a hashmap  
	 * @param assembliesToTrie
	 * @return
	 */
	public void iterateFilesToTrieMapAtStartup(HashSet filesToTrie);
	
	/**
	 * extract a distinct file and add the content to the 
	 * according data structure
	 * @param actualFile
	 * @param isDeep
	 */
	public void extractFile(File actualFile, boolean isDeep); 
}
