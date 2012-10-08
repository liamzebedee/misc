package org.emonic.base.buildmechanism;

import java.util.ArrayList;

public class Target {
	/**
	 * The name of the target
	 */
	protected String name="";
	protected String newName="";
	protected String[] dependencies;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dependencies
	 */
	public String[] getDependencies() {
		if (dependencies == null) return new String[0];
		return dependencies;
	}

	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(String[] dependencies) {
		// For security, we eliminate empty deps
		ArrayList lst = new ArrayList();
		for (int i = 0; i < dependencies.length;i++){
			if ((dependencies[i]!=null) && (! dependencies[i].equals(""))){
				lst.add(dependencies[i]);
			}
		}
		String[] res = new String[lst.size()];
		res=(String[])lst.toArray(res);
		this.dependencies = res;
	}

	/**
	 * If the target is renamed via setNewName, get the new name
	 * @return The new name
	 */
	public String getNewName() {
		return newName;
	}

	/**
	 * Trigger renaming of the target
	 * @param newname
	 */
	public void setNewName(String newname) {
		newName=newname;
	}
	
}
