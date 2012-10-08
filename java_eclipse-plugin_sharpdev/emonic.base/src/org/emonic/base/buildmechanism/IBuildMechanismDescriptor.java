/***************************************************************************
 * Copyright (c) 2001, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Dec 23, 2005
 ***************************************************************************/
public package org.emonic.base.buildmechanism;

/**
 * @author bb
 *
 */
public interface IBuildMechanismDescriptor {
    
	// Constants to show which languages are supported
	/**
	 * All languages for build supportet
	 */
	final String ALL_LANGUES = "ALL";
	
	/**
	 * The c#-language is supported
	 */
	final String CSHARP_LANGUAGE="csharp";
	
	/**
	 * Build with the mono framework
	 */
	final String FRAMEWORK_MONO="Mono";
	/**
	 * Build with the CSC framework
	 */
	final String FRAMEWORK_MS="Microsoft";
	/**
	 * Build mech does not support different build mechs
	 */
	final String FRAMEWORK_NA="NA";

	final String LANGUAGE_NA = "NA";
	
	
	
	
	
	

	

   
   

}
