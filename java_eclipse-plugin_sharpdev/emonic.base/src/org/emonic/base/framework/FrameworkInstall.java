/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.framework;

import java.io.File;


public class FrameworkInstall implements IFrameworkInstall {
	
	public String id;

	private String name;

	private String type;

	private File installLocation;

	private String runtimeArgs;

	private String GAC;
	
	private String documentationLocation;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setInstallLocation(File installLocation) {
		this.installLocation = installLocation;
	}

	public File getInstallLocation() {
		return installLocation;
	}

	public void setRuntimeArguments(String frameworkArgs) {
		this.runtimeArgs = frameworkArgs;
	}

	public String getRuntimeArguments() {
		return runtimeArgs;
	}
	
	public void setDocumentationLocation(String documentationLocation) {
		this.documentationLocation = documentationLocation;
	}

	public String getDocumentationLocation() {
		return documentationLocation;
	}

	public void setGACLocation(String gacLocation){
		this.GAC = gacLocation;
	}
	
	public String getGACLocation(){
		return GAC;
	}
	

}
