/*********************************************************************
 *  Copyright (c) 2005, 2007 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Mar 24, 2005
 *
 *************************************************************************/

package org.emonic.base.buildmechanism;


/**
 * @author bb
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NoneBuildfileManipulator implements IBuildMechanismDescriptor { 
public NoneBuildfileManipulator() {
		
	}


    public static String getName(){
	   return "none";
	}
}
