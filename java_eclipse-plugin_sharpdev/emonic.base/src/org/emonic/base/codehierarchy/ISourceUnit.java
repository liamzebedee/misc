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
package org.emonic.base.codehierarchy;

/**
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ISourceUnit extends ICompilationUnit  {

	/**
	 * This method returns the file extension of the given 
	 * source file; 
	 * On 03/03/08 only csharp files are supported, BUT vb-files may 
	 * be an option for the future
	 * @return
	 */
	public String getFileExtension();
}
