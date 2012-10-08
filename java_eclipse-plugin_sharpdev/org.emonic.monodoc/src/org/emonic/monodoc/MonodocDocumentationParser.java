/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT license which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation 
 ******************************************************************************/
package org.emonic.monodoc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.emonic.base.documentation.Documentation;
import org.emonic.base.documentation.IDocumentation;
import org.emonic.base.documentation.IDocumentationParser;
import org.emonic.base.documentation.ITypeDocumentation;
import org.emonic.base.documentation.LRUCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MonodocDocumentationParser implements IDocumentationParser {

	static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory
			.newInstance();

	private static final FilenameFilter TREE_FILTER = new Filter(".tree"); //$NON-NLS-1$

	private static final FilenameFilter ZIP_FILTER = new Filter(".zip"); //$NON-NLS-1$

	private static final Map TREES = new HashMap();

	private static final Map NAMESPACES = new LRUCache();

	private static final String SOURCES_FOLDER = "sources"; //$NON-NLS-1$

	private static final String ZIP_FILE_EXTENSION = "zip"; //$NON-NLS-1$

	public IDocumentation findNamespaceDocumentation(
			String documentationFolder, String assemblyName,
			String namespaceName, IProgressMonitor monitor) {
		IDocumentation doc = (IDocumentation) NAMESPACES.get(namespaceName);
		if (doc != null) {
			return doc;
		}

		File folder = new File(documentationFolder, SOURCES_FOLDER);
		if (!folder.exists()) {
			monitor.done();
			return null;
		}

		File[] files = folder.listFiles(ZIP_FILTER);
		monitor.beginTask("Scanning Monodoc zip files...", files.length);

		try {
			DocumentBuilder builder = FACTORY.newDocumentBuilder();
			for (int i = 0; i < files.length; i++) {
				if (monitor.isCanceled()) {
					return null;
				}

				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(files[i]);
					ZipEntry entry = zipFile.getEntry("mastersummary.xml"); //$NON-NLS-1$
					if (entry != null) {
						InputStream inputStream = zipFile.getInputStream(entry);
						if ((inputStream.read() & 0xFF) != 0xEF
								|| (inputStream.read() & 0xFF) != 0xBB
								|| (inputStream.read() & 0xFF) != 0xBF) {
							inputStream.close();
							inputStream = zipFile.getInputStream(entry);
						}

						Document document = builder.parse(inputStream);
						Node root = document.getFirstChild();
						NodeList children = root.getChildNodes();
						for (int j = 0; j < children.getLength(); j++) {
							Element child = (Element) children.item(j);
							String name = child.getAttribute("ns");
							doc = new Documentation(name, child);
							NAMESPACES.put(name, doc);
						}

						inputStream.close();

						doc = (IDocumentation) NAMESPACES.get(namespaceName);
						if (doc != null) {
							return doc;
						}
					}
				} catch (IOException e) {
					if (zipFile != null) {
						try {
							zipFile.close();
						} catch (IOException ex) {
							// ignored
						}
					}
				} catch (SAXException e) {
					if (zipFile != null) {
						try {
							zipFile.close();
						} catch (IOException ex) {
							// ignored
						}
					}
				}

				monitor.worked(1);
			}
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} finally {
			monitor.done();
		}
	}

	public ITypeDocumentation findTypeDocumentation(String documentationFolder,
			String assemblyName, String namespaceName, String typeName,
			IProgressMonitor monitor) {
		File folder = new File(documentationFolder, SOURCES_FOLDER);
		if (!folder.exists()) {
			monitor.done();
			return null;
		}

		File[] files = folder.listFiles(TREE_FILTER);
		monitor.beginTask("Scanning Monodoc source files...", files.length);

		try {
			for (int i = 0; i < files.length; i++) {
				if (monitor.isCanceled()) {
					return null;
				}
				ITypeDocumentation documentation = getDocumentation(files[i],
						namespaceName, typeName);
				if (documentation != null) {
					return documentation;
				}
				monitor.worked(1);
			}
			return null;
		} finally {
			monitor.done();
		}
	}

	public static ITypeDocumentation getDocumentation(File sourceFile,
			String namespace, String typeName) {
		MonodocTree tree = (MonodocTree) TREES.get(sourceFile);
		try {
			if (tree == null) {
				String sourceName = sourceFile.getAbsolutePath();
				int index = sourceName.lastIndexOf('.');
				File zipFile = new File(sourceName.substring(0, index + 1)
						+ ZIP_FILE_EXTENSION);
				if (!zipFile.exists()) {
					return null;
				}
				tree = new MonodocTree(sourceFile, zipFile);
				TREES.put(sourceFile, tree);
			}
			tree.loadNode();
			return tree.getTypeDocumentation(namespace, typeName);
		} catch (IOException e) {
			return null;
		} finally {
			if (tree != null) {
				tree.close();
			}
		}
	}

	static class Filter implements FilenameFilter {

		private String filter;

		Filter(String filter) {
			this.filter = filter;
		}

		public boolean accept(File dir, String name) {
			return name.endsWith(filter);
		}

	}

}
