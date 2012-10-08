/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.emonic.base.mbel2assemblyparser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codecompletion.IAssemblyParser;
import org.emonic.base.codehierarchy.Assembly;
import org.emonic.base.codehierarchy.AssemblyHashElement;
import org.emonic.base.codehierarchy.BinaryNamespace;
import org.emonic.base.codehierarchy.BinaryType;
import org.emonic.base.codehierarchy.CodeHierarchyHelper;
import org.emonic.base.codehierarchy.IAssembly;
import org.emonic.base.codehierarchy.INamespace;
import org.emonic.base.codehierarchy.IType;
//import org.emonic.base.codehierarchy.TypeRef;
import org.emonic.base.documentation.Documentation;
import org.emonic.base.documentation.IDocumentation;
import org.emonic.base.documentation.TypeDocumentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.arizona.cs.mbel2.mbel.AssemblyInfo;
import edu.arizona.cs.mbel2.mbel.ClassParser;
import edu.arizona.cs.mbel2.mbel.Module;
import edu.arizona.cs.mbel2.mbel.TypeDef;

public class MBel2AssemblyParser implements IAssemblyParser {

	private  DocumentBuilder builder;

	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private static  HashMap assemblyHashMap;

	

	private  Map[] print(NodeList childNodes) {
		Map documentedNamespaces = new HashMap();
		Map documentedTypes = new HashMap();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				String memberName = element.getAttribute("name");
				switch (memberName.charAt(0)) {
				case 'T':
					memberName = memberName.substring(2);
					documentedTypes.put(memberName, new TypeDocumentation(
							memberName, element));
					break;
				case 'N':
					memberName = memberName.substring(2);
					documentedNamespaces.put(memberName, new Documentation(
							memberName, element));
					break;
				case 'E':
				case 'P':
				case 'F':
					memberName = memberName.substring(2);
					String typeName = memberName;
					int index = typeName.indexOf('(');
					if (index != -1) {
						// remove the stuff in the parentheses if applicable
						typeName = typeName.substring(0, index);
					}
					// find the last period
					index = typeName.lastIndexOf('.');
					// retrieve the type name
					typeName = typeName.substring(0, index);
					// set the member name
					memberName = memberName.substring(typeName.length() + 1);
					memberName = CodeHierarchyHelper.convertSignature(memberName);

					TypeDocumentation documentedType = (TypeDocumentation) documentedTypes
							.get(typeName);
					if (documentedType == null) {
						documentedType = new TypeDocumentation(typeName, null);
						documentedTypes.put(typeName, documentedType);
					}
					documentedType.add(new Documentation(memberName, element));
					break;
				case 'M':
					memberName = memberName.substring(2);
					typeName = memberName;
					index = typeName.indexOf('(');
					if (index != -1) {
						// remove the stuff in the parentheses if applicable
						typeName = typeName.substring(0, index);
					} else {
						memberName += "()";
					}
					// find the last period
					index = typeName.lastIndexOf('.');
					// retrieve the type name
					typeName = typeName.substring(0, index);
					// set the member name
					memberName = memberName.substring(typeName.length() + 1);
					if (memberName.startsWith("#ctor")) { //$NON-NLS-1$
						if (memberName.length() == 5) {
							memberName = typeName + "()";
						} else {
							memberName = typeName
									+ CodeHierarchyHelper.convertSignature(memberName.substring(5));
						}
					} else {
						memberName = CodeHierarchyHelper.convertSignature(memberName);
					}

					documentedType = (TypeDocumentation) documentedTypes
							.get(typeName);
					if (documentedType == null) {
						documentedType = new TypeDocumentation(typeName, null);
						documentedTypes.put(typeName, documentedType);
					}
					documentedType.add(new Documentation(memberName, element));
					break;
				}
			}
		}
		return new Map[] { documentedNamespaces, documentedTypes };
	}

	public  Map[] parseDocumentation(InputStream inputStream) {
		try {
			Document document = builder.parse(inputStream);
			Element element = document.getDocumentElement();
			NodeList children = element.getChildNodes();

			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeName().equals("members")) {
					return print(node.getChildNodes());
				}
			}
			return new Map[] { Collections.EMPTY_MAP, Collections.EMPTY_MAP };
		} catch (IOException e) {
			return new Map[] { Collections.EMPTY_MAP, Collections.EMPTY_MAP };
		} catch (SAXException e) {
			return new Map[] { Collections.EMPTY_MAP, Collections.EMPTY_MAP };
		}
	}

	public IAssembly parseAssembly( IPath path,
			Map[] maps) {
		
		boolean cacheMechanism=true;
		if (cacheMechanism){
			if (assemblyHashMap==null){
				assemblyHashMap=new HashMap();
			}
			if (assemblyHashMap.containsKey(path.toPortableString())){
				AssemblyHashElement ele = (AssemblyHashElement) assemblyHashMap.get(path.toPortableString());
				if (path.toFile().lastModified() <= ele.getModifyDate()){
					return ele.getAssembly();
				} else {
					// Assembly modified, we have to change!
					assemblyHashMap.remove(path.toPortableString());
				}
			}
		}
	
	
	
		// We buffer this stream for faster access
		TypeDef[] definitions = new TypeDef[0];
		if (path.toFile().exists() && path.toFile().canRead()){
			InputStream stream;
			try {
				stream = new FileInputStream(path.toFile());
			
			InputStream is = new BufferedInputStream( stream );
			ClassParser parser = new ClassParser(is);
			stream.close();
			is.close();
			Module module = parser.parseModule(false);
			Map namespaces = new TreeMap();
			Map types = new TreeMap();
	
			AssemblyInfo ai = module.getAssemblyInfo();
			IAssembly assembly = new Assembly(ai.getName(),ai.getMajorVersion(),ai.getMinorVersion(),ai.getBuildNumber(),ai.getRevisionNumber(), namespaces,
					types, path);
			definitions = module.getTypeDefs();
			parser = null;
			module = null;
	
			for (int i = 0; i < definitions.length; i++) {
				String namespaceName = definitions[i].getNamespace();
				if (definitions[i].getName().equals("<Module>")) {
					continue;
				}
	
				if (namespaceName.equals("")) { //$NON-NLS-1$
					String fullName = definitions[i].getName();
					TypeDocumentation docType = (TypeDocumentation) maps[1]
					                                                     .get(fullName);
					edu.arizona.cs.mbel2.mbel.TypeRef superclass = definitions[i].getSuperClass();
					String superclassName = "";
					if (superclass != null) {
						superclassName = superclass.getNamespace() + '.'
								+ superclass.getName();
					}
				
					String name=definitions[i].getName();
					//int flags=(int) definitions[i].getFlags();
					//String theFullName=definitions[i].getFullName();
					if (fullName.charAt(0) == '.') {
						fullName = name;
					}
					//BinaryType type = new BinaryType(assembly,name, theFullName,superclassName,flags, docType,assembly.getPath());
					
					//BinaryType type = new BinaryType(assembly,definitions[i],docType,assembly.getPath()); //name, theFullName,superclassName,flags, docType,assembly.getPath());
					BinaryType type = BinaryTypeBuilder.buildBinaryType(assembly,definitions[i],docType,assembly.getPath());
					type.setSuperclassName(superclassName);
					types.put(type.getElementName(), type);
				} else {
					String fullName = definitions[i].getFullName();
					TypeDocumentation docType = (TypeDocumentation) maps[1]
					                                                     .get(fullName);
					BinaryNamespace namespace = (BinaryNamespace) namespaces.get(namespaceName);
					if (namespace == null) {
						namespace = new BinaryNamespace(assembly, namespaceName, path,
								(IDocumentation) maps[0].get(namespaceName));
						namespaces.put(namespaceName, namespace);
					}
					//IType type = new BinaryType(namespace, definitions[i], docType,assembly.getPath());
					IType type = BinaryTypeBuilder.buildBinaryType(namespace, definitions[i], docType,assembly.getPath());
					String key = type.getFullName();
					int index = key.lastIndexOf('.');
					namespace.put(key.substring(index + 1), type);
				}
			}
	
			List allTypes = new LinkedList(types.values());
			for (Iterator iterator = namespaces.values().iterator(); iterator
			.hasNext();) {
				INamespace namespace = (INamespace) iterator.next();
				allTypes.addAll(Arrays.asList(namespace.getTypes()));
			}
	
			for (int i = 0; i < definitions.length; i++) {
				TypeDef[] nestedClasses = definitions[i].getNestedClasses();
				if (nestedClasses.length == 0) {
					continue;
				}
				String nestingFullName = definitions[i].getFullName();
				for (Iterator nestingTypeIterator = allTypes.iterator(); nestingTypeIterator
				.hasNext();) {
					BinaryType nestingType = (BinaryType) nestingTypeIterator.next();
					if (nestingType.getFullName().equals(nestingFullName)) {
						IType[] nestedTypes = new IType[nestedClasses.length];
						for (int j = 0; j < nestedClasses.length; j++) {
							String nestedName = nestedClasses[j].getFullName();
							for (Iterator nestedIterator = allTypes.iterator(); nestedIterator
							.hasNext();) {
								BinaryType nestedType = (BinaryType) nestedIterator.next();
								if (nestedType.getFullName().equals(nestedName)) {
									//nestedType.setDeclaringType(nestingType);
									nestedTypes[j] = nestedType;
									break;
								}
							}
						}
						nestingType.setTypes(nestedTypes);
						break;
					}
				}
			}
			
	
			if (cacheMechanism){
				AssemblyHashElement ele = new AssemblyHashElement();
				ele.setAssembly(assembly);
				ele.setModifyDate(path.toFile().lastModified());
				assemblyHashMap.put(path.toPortableString(), ele);
				org.eclipse.core.runtime.Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
				boolean removeCache= prefs.getBoolean(org.emonic.base.mbel2assemblyparser.MBel2PreferencesInjector.REMOVECODECOMPLETIONCACHE);
				if (removeCache){
					int cacheSeconds = prefs.getInt(org.emonic.base.mbel2assemblyparser.MBel2PreferencesInjector.CODECOMPLETIONCACHE);
					long removeCacheAfterMs=cacheSeconds*1000L;
					new MBel2AssemblyParser.RemoveCacheJob(path.toPortableString()).schedule(removeCacheAfterMs);
				}
			}
	
			return assembly;
			} catch (Exception e) {
				// OK if the file is not found
			}
			}
		
	
		return null;
	}

	
	static class RemoveCacheJob extends Job {

			final String filename;
		
			public RemoveCacheJob(String filename) {
				super("Remove uncompressed CC Cache");
				this.filename = filename;
				
			}
			protected IStatus run(IProgressMonitor monitor) {
                assemblyHashMap.remove(filename);				
				return Status.OK_STATUS;
		}
    }
	
	
	public String getPrefLabel() {
		return "mbel2";
	}

	/**
	 * Importance 2: System standard
	 */
	public int getImportance() {
		return 2;
	}

}


