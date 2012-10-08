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

import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.editors.CSharpCursorPositionContext;

/**
 * ISession is is an interface for code completion session.
 * If code completion is used via CTRl+SPACE or "."-Completion,
 * a new session object is created which is in use until a new 
 * completion proposal session is created (one session per time)
 * @author dertl
 *
 */
public interface ISession {


	/**
	 * calculate the completion proposal
	 * @param wordToComplete
	 */
	public LinkedList getCompletionproposal(String wordToComplete_, CSharpCursorPositionContext cpc_,
			int documentOffset_, ISourceUnit actualSrcUnit_, IFile actualFile_);
	
	
}
