/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.emonic.base.documentation;

import java.util.ArrayList;
import java.util.List;

class XMLDocumentation implements IDocumentation {

	private List exceptions;
	private List parameters;

	private String name;
	private String remarks;
	private String returns;
	private String seeAlso;
	private String summary;
	private String value;

	XMLDocumentation(String name) {
		this.name = name;
	}

	public IDocumentation[] getExceptions() {
		return exceptions == null ? new IDocumentation[0]
				: (IDocumentation[]) exceptions
						.toArray(new IDocumentation[exceptions.size()]);
	}

	public String getName() {
		return name;
	}

	void addParameter(String name, String summary) {
		if (parameters == null) {
			parameters = new ArrayList();
		}
		parameters.add(new Parameter(name, summary));
	}

	public IDocumentation[] getParams() {
		return parameters == null ? new IDocumentation[0]
				: (IDocumentation[]) parameters
						.toArray(new IDocumentation[parameters.size()]);
	}

	public String getRemarks() {
		return remarks;
	}

	void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReturns() {
		return returns;
	}

	void setReturns(String returns) {
		this.returns = returns;
	}

	public String getSeeAlso() {
		return seeAlso;
	}

	void setSeeAlso(String seeAlso) {
		this.seeAlso = seeAlso;
	}

	public String getSummary() {
		return summary;
	}

	void setSummary(String summary) {
		this.summary = summary;
	}

	public String getValue() {
		return value;
	}

	void setValue(String value) {
		this.value = value;
	}

	class Parameter extends AbstractDocumentation {

		private final String name;
		private final String summary;

		Parameter(String name, String summary) {
			this.name = name;
			this.summary = summary;
		}

		public String getName() {
			return name;
		}

		public String getSummary() {
			return summary;
		}

	}

}
