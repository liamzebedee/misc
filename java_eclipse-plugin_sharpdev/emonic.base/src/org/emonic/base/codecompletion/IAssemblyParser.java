package org.emonic.base.codecompletion;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.emonic.base.codehierarchy.IAssembly;

public interface IAssemblyParser {

	/**
	 * Get the label for the PrefPage
	 */
	public String getPrefLabel();
	
    /**
     * Returns the importance of the search mechanism
     * @return 0: NullMechanism, 2: System Default; > 2 more important than system default; 1: less important 
     */
	public abstract int getImportance();
	
	public  Map[] parseDocumentation(InputStream inputStream);
	public IAssembly parseAssembly( IPath path,Map[] maps); 
}