/*
 * Copyright emonic.org
 * License EPL
 * Created on 09.01.2008
 * emonic.base org.emonic.base.infostructure IAbstractParser.java
 */
package org.emonic.base.infostructure;

import java.io.File;

import org.emonic.base.codehierarchy.IDotNetElement;

/**
 * Parser-Interface for code-parser
 * It is expected that the classes implement additionally a default constructor.
 * @author bb
 *
 */
public interface IAbstractParser {
    
    /**
     * This method delivers the parsed structure
     * @return the parsed structure
     */ 
	IDotNetElement getRoot();

	
	/**
	 * Initialize fith a file. It is expected that the parser 
	 * converts this in the final structure
	 * @param file the file to savr
	 * @param informator The code informator, usefull if the parser needs further infos
	*/
	//void init(File fl, CodeInformator informator);
	void init(File fl);

}
