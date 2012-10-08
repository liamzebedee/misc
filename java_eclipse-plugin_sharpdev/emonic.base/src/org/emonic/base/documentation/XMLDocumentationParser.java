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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.emonic.base.codehierarchy.CodeHierarchyHelper;
//import org.emonic.base.codehierarchy.MBel2AssemblyParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDocumentationParser implements IDocumentationParser {

	private static final Map DOCUMENTATION = new HashMap();

	public IDocumentation findNamespaceDocumentation(
			String documentationFolder, String assemblyName,
			String namespaceName, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeDocumentation findTypeDocumentation(String documentationFolder,
			String assemblyPath, String namespaceName, String typeName,
			IProgressMonitor monitor) {
		if (!namespaceName.equals("")) { //$NON-NLS-1$
			typeName = namespaceName + '.' + typeName;
		}

		Map map = (Map) DOCUMENTATION.get(assemblyPath);
		if (map != null) {
			ITypeDocumentation doc = (ITypeDocumentation) map.get(typeName);
			if (doc != null) {
				return doc;
			}
		}

		File assembly = new File(assemblyPath);
		String assemblyName = assembly.getName();
		int index = assemblyName.lastIndexOf('.');
		if (index == -1) {
			return null;
		}

		File xml = new File(assembly.getParent(), assemblyName.substring(0,
				index + 1)
				+ "xml");
		if (xml.isDirectory() || !xml.canRead()) {
			xml = new File(documentationFolder, assemblyName.substring(0,
					index + 1)
					+ "xml");
			if (xml.isDirectory() || !xml.canRead()) {
				return null;
			}
		}

		if (map == null) {
			map = new LRUCache();
			DOCUMENTATION.put(assemblyPath, map);
		}

		TypeHandler handler = new TypeHandler(typeName);
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(new FileInputStream(xml), handler);
		} catch (InterruptParseException e) {
			ITypeDocumentation documentation = handler.getTypeDocumentation();
			if (documentation != null) {
				map.put(typeName, documentation);
			}
			return documentation;
		} catch (SAXException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (FactoryConfigurationError e) {
			return null;
		}
		return null;
	}

	class TypeHandler extends DefaultHandler {

		private String typeName;
		private int typeNameLength;

		private StringBuffer buffer;

		private boolean found = false;

		private XMLTypeDocumentation type;

		private XMLDocumentation current;

		private String parameterName;

		TypeHandler(String typeName) {
			this.typeName = typeName;
			typeNameLength = typeName.length();
		}

		public ITypeDocumentation getTypeDocumentation() {
			return type;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("member")) {
				String name = attributes.getValue("name");
				if (name != null) {
					String memberName = name.substring(2);
					switch (name.charAt(0)) {
					case 'T':
						if (memberName.equals(typeName)) {
							type = new XMLTypeDocumentation(typeName);
							current = type;
							found = true;
							break;
						} else if (found) {
							throw new SAXException("bcd");
						}
					case 'E':
					case 'F':
						if (memberName.startsWith(typeName)) {
							int index = memberName.lastIndexOf('.');
							if (memberName.substring(0, index).equals(typeName)) {
								if (type == null) {
									type = new XMLTypeDocumentation(typeName);
								}
								current = new XMLDocumentation(memberName
										.substring(typeNameLength + 1));
								type.add(current);
								found = true;
							} else if (found) {
								throw new InterruptParseException();
							}
						} else if (found) {
							throw new InterruptParseException();
						}
						break;
					case 'P':
						if (memberName.startsWith(typeName)) {
							if (found) {
								memberName = memberName
										.substring(typeNameLength + 1);
								if (memberName.indexOf('.') != -1) {
									throw new InterruptParseException();
								}
								current = new XMLDocumentation(memberName);
								type.add(current);
							} else {
								String part = memberName.substring(typeName
										.length() + 1);
								int index = part.indexOf('.');
								if (index == -1
										|| index == part.lastIndexOf('.')) {
									found = true;
									type = new XMLTypeDocumentation(typeName);
									memberName = memberName
											.substring(typeNameLength + 1);
									current = new XMLDocumentation(memberName
											.substring(typeNameLength + 1));
									type.add(current);
								}
							}
						} else if (found) {
							throw new InterruptParseException();
						}
						break;
					case 'M':
						if (memberName.startsWith(typeName)) {
							if (found) {
								String part = memberName.substring(typeName
										.length() + 1);
								int index = part.indexOf('.');
								int pIndex = part.indexOf('(');
								if ((index != -1 && pIndex == -1)
										|| (index < pIndex)) {
									throw new InterruptParseException();
								}
								memberName = memberName
										.substring(typeNameLength + 1);
								if (memberName.equals("#ctor")) {
									index = typeName.lastIndexOf('.');
									memberName = typeName.substring(index + 1)
											+ "()";
								} else if (memberName.startsWith("#ctor")) {
									index = typeName.lastIndexOf('.');
									memberName = typeName.substring(index + 1)
											+ memberName.substring(5);
								}
								memberName = CodeHierarchyHelper
										.convertSignature(memberName);
								if (memberName.indexOf('(') == -1) {
									memberName += "()";
								}
								current = new XMLDocumentation(memberName);
								type.add(current);
							} else {
								String part = memberName.substring(typeName
										.length() + 1);
								int index = part.indexOf('.');
								int pIndex = part.indexOf('(');
								if (index == -1
										|| (pIndex != -1 && pIndex < index)) {
									found = true;
									type = new XMLTypeDocumentation(typeName);
									memberName = memberName
											.substring(typeNameLength + 1);
									if (memberName.equals("#ctor")) {
										index = typeName.lastIndexOf('.');
										memberName = typeName
												.substring(index + 1)
												+ "()";
									} else if (memberName.startsWith("#ctor")) {
										index = typeName.lastIndexOf('.');
										memberName = typeName
												.substring(index + 1)
												+ memberName.substring(5);
									}
									memberName = CodeHierarchyHelper
											.convertSignature(memberName);
									if (memberName.indexOf('(') == -1) {
										memberName += "()";
									}
									current = new XMLDocumentation(memberName
											.substring(typeNameLength + 1));
									type.add(current);
								}
							}
						} else if (found) {
							throw new InterruptParseException();
						}
						break;
					}
				}
			} else if (found) {
				if (qName.equals("summary") || qName.equals("returns")
						|| qName.equals("value")) {
					buffer = new StringBuffer();
				} else if (qName.equals("param")) {
					parameterName = attributes.getValue("name");
					buffer = new StringBuffer();
				} else if (qName.equals("see")) {
					String cref = attributes.getValue("cref");
					if (cref == null) {
						buffer.append(attributes.getValue("langword"));
					} else {
						buffer.append(cref.substring(2));
					}
					buffer.append(' ');
				}
			}
		}

		public void endElement(String uri, String localName, String qName) {
			if (found) {
				if (qName.equals("summary")) {
					current.setSummary(buffer.toString().trim());
				} else if (qName.equals("returns")) {
					current.setReturns(buffer.toString().trim());
				} else if (qName.equals("value")) {
					current.setValue(buffer.toString().trim());
				} else if (qName.equals("param")) {
					current.addParameter(parameterName, buffer.toString());
				}
			}
		}

		public void characters(char ch[], int start, int length) {
			if (found) {
				for (int i = 0; i < length; i++) {
					if (!Character.isWhitespace(ch[start + i])) {
						String string = new String(ch, start + i, length - i)
								.trim();
						String[] split = string.split("\\s"); //$NON-NLS-1$
						for (int j = 0; j < split.length; j++) {
							if (!split[j].equals("")) { //$NON-NLS-1$
								buffer.append(split[j]).append(' ');
							}
						}
						break;
					}
				}
			}
		}
	}

	class InterruptParseException extends SAXException {

		private static final long serialVersionUID = -1359849121462453996L;

		InterruptParseException() {
			super("Stop parsing");
		}

	}

}
