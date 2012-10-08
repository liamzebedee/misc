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

public class Metatype {
	
	private String typename_;
	
	private String namespace_;
	
	private String metadata_;
	
	private String filepath_;
	
	private boolean isNamespace_;
	
	public Metatype(String typename, String namespace, String metadata, String filepath, boolean isNamespace){
		if(namespace.equals("") && metadata.equals("") && typename.equals("")){
			System.out.println("DEBUG: Empty Metadata entry in " + filepath);
		}
		this.typename_ = typename.toLowerCase();
		this.namespace_ = namespace.toLowerCase();
		this.metadata_ = metadata;
		this.filepath_ = filepath;
		this.isNamespace_ = isNamespace;
	}

	public String getTypename(){
		return this.typename_;
	}
	
	public String getNamespace(){
		return this.namespace_;
	}
	
	public String getMetadata(){
		return this.metadata_;
	}
	
	public String getFilepath(){
		return this.filepath_;
	}
	
	public boolean isNamespace(){
		return this.isNamespace_;
	}
	
	public boolean equals(Object o){
		Metatype type = (Metatype) o;
		
		if(this.filepath_.equals(type.getFilepath()) &&
		   this.namespace_.equals(type.getNamespace()) &&
		   this.typename_.equals(type.getTypename())){
			return true;
		}
		
		return false;
		
	}
}
	