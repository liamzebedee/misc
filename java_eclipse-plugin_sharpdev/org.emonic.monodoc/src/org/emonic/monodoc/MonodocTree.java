/*******************************************************************************
 * Copyright (c) 2002, 2008 Ximian, Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT license which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Contributors:
 *     Miguel de Icaza <miguel@novell.com> - initial API and implementation
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - adapted from C# to Java 
 ******************************************************************************/
package org.emonic.monodoc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;

//import org.emonic.base.codehierarchy.AssemblyParser;
import org.emonic.base.documentation.Documentation;
import org.emonic.base.documentation.ITypeDocumentation;
import org.emonic.base.documentation.LRUCache;
import org.emonic.base.documentation.TypeDocumentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

final class MonodocTree extends MonodocNode {

	private Map cache = new LRUCache();

	private DataInputImpl input;

	private File sourceFile;

	private File zipFile;

	private long lastSourceModified;

	private long lastZipModified;

	MonodocTree(File sourceFile, File zipFile) {
		this.sourceFile = sourceFile;
		this.zipFile = zipFile;
	}

	void loadNode() throws IOException {
		// we check the modification timestamp as we shouldn't parse the file
		// needlessly
		long lastSourceModified = sourceFile.lastModified();
		if (lastSourceModified > this.lastSourceModified) {
			input = new DataInputImpl(sourceFile);
			byte[] sig = new byte[4];
			input.read(sig);

			if (!isValidSig(sig))
				throw new RuntimeException("Invalid file format");

			input.seek(4);
			position = input.readInt();

			tree = this;
			super.loadNode();

			this.lastSourceModified = lastSourceModified;
		} else {
			input = new DataInputImpl(sourceFile);
		}
	}

	DataInputImpl getFile() {
		return input;
	}

	void close() {
		try {
			input.close();
		} catch (IOException e) {
			// ignored
		}
	}

	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	ITypeDocumentation getTypeDocumentation(String namespaceName,
			String typeName) {
		String fullName = namespaceName + '.' + typeName;
		long lastZipModified = zipFile.lastModified();
		if (lastZipModified > this.lastZipModified) {
			cache.clear();
			this.lastZipModified = lastZipModified;
		}

		ITypeDocumentation documentation = (ITypeDocumentation) cache
				.get(fullName);
		if (documentation == null) {
			documentation = findDocumentation(namespaceName, typeName);
			cache.put(fullName, documentation);
		}
		return documentation;
	}

	private ITypeDocumentation findDocumentation(String namespaceName,
			String typeName) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].getElement().substring(2).equals(namespaceName)) {
				MonodocNode[] childNodes = nodes[i].getNodes();
				for (int j = 0; j < childNodes.length; j++) {
					String name = childNodes[j].getElement();
					int pidx = name.indexOf('#');
					int sidx = name.lastIndexOf('/');
					String cname = name.substring(pidx + 1, sidx);
					if (cname.equals(typeName)) {
						int cidx = name.indexOf(':');
						ITypeDocumentation documentation = find(name.substring(
								cidx + 1, pidx));
						return documentation;
					}
				}
			}
		}
		return null;
	}

	private ITypeDocumentation find(String entry) {
		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile);
			InputStream inputStream = zip.getInputStream(zip.getEntry(entry));
			return parse(inputStream);
		} catch (Exception e) {
			return null;
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException e) {
					// ignored
				}
			}
		}
	}

	private static ITypeDocumentation parse(InputStream inputStream)
			throws Exception {
		DocumentBuilder builder = MonodocDocumentationParser.FACTORY
				.newDocumentBuilder();
		Document document = builder.parse(inputStream);
		Element element = (Element) document.getFirstChild();

		NodeList nodes = element.getChildNodes();
		TypeDocumentation type = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			if (name.equals("Docs")) {
				type = new TypeDocumentation(element.getAttribute("FullName"),
						(Element) node);
			} else if (name.equals("Members")) {
				if (type == null) {
					type = new TypeDocumentation(element
							.getAttribute("FullName"), null);
				}
				NodeList members = node.getChildNodes();
				for (int j = 0; j < members.getLength(); j++) {
					node = members.item(j);
					if (node.getNodeName().equals("Member")) {
						Element member = (Element) node;
						name = member.getAttribute("MemberName");
						nodes = member.getChildNodes();
						String memberType = null;
						for (int k = 0; k < nodes.getLength(); k++) {
							node = nodes.item(k);
							String nodeName = node.getNodeName();
							if (nodeName.equals("MemberType")) {
								Text text = ((Text) node.getChildNodes()
										.item(0));
								memberType = text.getData().trim();
							} else if (nodeName.equals("Parameters")) {
								if (!memberType.equals("Method")) {
									continue;
								}
								NodeList parameters = node.getChildNodes();
								int length = parameters.getLength();
								if (length == 0) {
									name = name + "()";
								} else {
									StringBuffer buffer = new StringBuffer(name);
									synchronized (buffer) {
										buffer.append('(');
										for (int l = 0; l < parameters
												.getLength(); l++) {
											Node parameter = parameters.item(l);
											if (parameter instanceof Element) {
												buffer
														.append(((Element) parameter)
																.getAttribute("Type"));
												buffer.append(',');
											}
										}
										buffer.delete(buffer.length() - 1,
												buffer.length());
										buffer.append(')');
										name = org.emonic.base.codehierarchy.CodeHierarchyHelper
												.convertSignature(buffer
														.toString());
									}
								}
							} else if (nodeName.equals("Docs")) {
								if (memberType.equals("Event")
										|| memberType.equals("Constructor")
										|| memberType.equals("Property")
										|| memberType.equals("Method")
										|| memberType.equals("Field")) {
									type.add(new Documentation(name, node));
								}
							}
						}
					}
				}
				break;
			}
		}

		return type;
	}

	private static boolean isValidSig(byte[] sig) {
		if (sig.length != 4)
			return false;
		return sig[0] == (byte) 'M' && sig[1] == (byte) 'o'
				&& sig[2] == (byte) 'H' && sig[3] == (byte) 'P';
	}
}
