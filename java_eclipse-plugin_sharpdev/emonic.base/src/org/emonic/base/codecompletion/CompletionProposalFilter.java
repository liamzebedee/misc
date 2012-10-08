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


public class CompletionProposalFilter {


	
	/**
	 * filter() parses the string which has to be filtered
	 * against the flags and returns true or false...
	 * @param cpFlags
	 * @param valueOfListelement
	 * @return
	 */
	public boolean filter(int cursorpositionFlags, int wordtocompleteFlags, 
						  int idotnetelement, boolean isVariableCompletion,
						  String displayString) {
		
		boolean filterRet = true;
		
		//check for is_this
		if((cursorpositionFlags & ICCFlags.IS_THIS) == ICCFlags.IS_THIS){
			//filterRet |=  
		}
		
		//check for is_in_method
		//IF NOT IN method => return false
		if(!((cursorpositionFlags & ICCFlags.IS_IN_METHOD) == ICCFlags.IS_IN_METHOD)){
			//FIXME at first we show no proposal if not in method
			filterRet = false;
		}			
			
//		//check for is in static and NOT a variablecompletion (always true, even in static methods)
//		if((cursorpositionFlags & ICCFlags.IS_IN_STATIC) == ICCFlags.IS_IN_STATIC &&
//			!isVariableCompletion){
//			//if method is not static => false
//			if(idotnetelement == IDotNetElement.METHOD &&
//				!((wordtocompleteFlags & Flags.STATIC_METHOD) == Flags.STATIC_METHOD)){
//					filterRet = false;
//			}//if field is not static => false
//			else if(idotnetelement == IDotNetElement.FIELD &&
//				!((wordtocompleteFlags & Flags.STATIC_FIELD) == Flags.STATIC_FIELD)){
//				   filterRet = false;
//			}
//		}
		
		//check for is in static
		if((cursorpositionFlags & ICCFlags.FROM_INNER_CLASS) == ICCFlags.FROM_INNER_CLASS){
			//filterRet |=
		}
		
		// don't display the .ctor or .cctor elements
		if (displayString.indexOf("ctor") != -1)
			filterRet = false;
		
		//if at least one of the filter methods returns 0
		return filterRet;
	}

}
