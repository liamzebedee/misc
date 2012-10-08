package org.emonic.base.codecompletion;

import java.io.File;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.emonic.base.codehierarchy.ICompilationUnit;

public interface IAssemblyExtractor {

	/**
	 * Get the directories for assembly extraction
	 * @return
	 */
	public abstract HashSet getDirs();

	/**
	 * Iterate through the vector of directories of assemblies
	 * and add all assemblies to the return vector
	 */
	public abstract HashSet addFiles(HashSet dirs);

	/**
	 * extract File if ICompilationUnit is available
	 * @param unit
	 * @param isDeep
	 * @param isDll
	 * @param filePath
	 */
	public abstract void extractFile(ICompilationUnit unit, boolean isDeep,
			boolean isDll, String filePath);

	/**
	 * 
	 * @param actualFile
	 */
	public abstract void extractFile(File actualFile, boolean isDeep);
	
	
    /**
     * You can inherit this from AssemblyExtractor
     * @param assemblies
     */
	public abstract void iterateFilesToTrieMapAtStartup(HashSet assemblies);
	
	/**
	 * Set the project in the extrctor
	 * @param project
	 */
	public void setProject(IProject project);
	
	

}