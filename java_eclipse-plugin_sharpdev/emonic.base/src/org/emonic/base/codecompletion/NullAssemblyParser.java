package org.emonic.base.codecompletion;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.emonic.base.codehierarchy.IAssembly;


public class NullAssemblyParser implements IAssemblyParser {


	public String getPrefLabel() {
		return "none";
	}

	/**
	 * Importance 0: Not default
	 */
	public int getImportance() {
		return 0;
	}

	/**
	 * The Assembly Parse Method
	 */
	public IAssembly parseAssembly(IPath path, Map[] maps) {
		return null;
	}

	/**
	 * The Doc Parse Method
	 */
	public Map[] parseDocumentation(InputStream inputStream) {
		return new Map[0];
	}

}
