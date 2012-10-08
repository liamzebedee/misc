/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.emonic.base.codecompletion.AssemblyParserFactory;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IAssembly;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.codehierarchy.IParent;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.filemanipulators.FileDocument;
import org.emonic.base.infostructure.CSharpCodeParser;
//import org.emonic.base.mbel2assemblyparser.MBel2AssemblyParser;

public class NavigatorSourceElementContentProvider implements
		IDeferredWorkbenchAdapter, ITreeContentProvider {

	private DeferredTreeContentManager manager;

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		manager = new DeferredTreeContentManager(this, (TreeViewer) viewer) {
			protected IDeferredWorkbenchAdapter getAdapter(Object element) {
				return NavigatorSourceElementContentProvider.this;
			}
		};
	}

	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	public Object getParent(Object element) {
		if (element instanceof IDotNetElement) {
			return ((IDotNetElement) element).getParent();
		} else if (element instanceof IResource) {
			return ((IResource) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof PendingUpdateAdapter
				|| element instanceof IResource) {
			return true;
		} else if (element instanceof IParent) {
			return ((IParent) element).hasChildren();
		} else {
			return getChildren(element).length != 0;
		}
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IFile) {
			return manager.getChildren(inputElement);
		} else {
			return getChildElements(inputElement);
		}
	}

	private Object[] getChildElements(Object inputElement) {
		if (inputElement instanceof IFile) {
			IFile file = (IFile) inputElement;
			String extension = file.getFileExtension();
			extension = extension == null ? null : extension.toLowerCase();
			if (extension == null) {
				return new Object[0];
			} else if (extension.equals("cs")) {
				ISourceUnit srcUnit = new CSharpCodeParser(new FileDocument(
						file, true), file.getFullPath().toOSString())
						.parseDocument();
				IDotNetElement[] children = srcUnit.getChildren();
				List ret = new ArrayList();
				for (int i = 0; i < children.length; i++) {
					switch (children[i].getElementType()) {
					case IDotNetElement.NAMESPACE:
					case IDotNetElement.CLASS:
					case IDotNetElement.INTERFACE:
						ret.add(children[i]);
						break;
					}
				}
				return ret.toArray();
			} else {
				try {
				  IAssembly asse = AssemblyParserFactory.createAssemblyParser().parseAssembly(
							//file.getContents(),
							file.getLocation(),
							new Map[] { Collections.EMPTY_MAP,
									Collections.EMPTY_MAP });
				  if (asse != null) {
					  return asse.getNamespaces();
				  } else {
					  return new Object[0];
				  }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new Object[0];
				}
			}
		} else if (inputElement instanceof IParent) {
			IDotNetElement[] children = ((IParent) inputElement).getChildren();
			List res = new ArrayList();
			for (int i = 0; i < children.length; i++) {
				switch (children[i].getElementType()) {
				case IDotNetElement.CLASS:
				case IDotNetElement.INTERFACE:
				case IDotNetElement.METHOD:
				case IDotNetElement.FIELD:
				case IDotNetElement.CONSTRUCTOR:
				case IDotNetElement.DESTRUCTOR:
				case IDotNetElement.ENUM:
				case IDotNetElement.STRUCT:
				case IDotNetElement.PROPERTY:
					res.add(children[i]);
					break;
				}
			}
			return res.toArray();
		} else {
			return new Object[0];
		}
	}

	public void fetchDeferredChildren(Object object,
			IElementCollector collector, IProgressMonitor monitor) {
		collector.add(getChildElements(object), monitor);
		monitor.done();
	}

	public ISchedulingRule getRule(Object object) {
		return null;
	}

	public boolean isContainer() {
		return true;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		if (o instanceof IResource) {
			return ((IResource) o).getName();
		} else if (o instanceof CodeElement) {
			return SourceElementLabelProvider.getCodeElementText(false,
					(CodeElement) o);
		} else {
			switch (((IDotNetElement) o).getElementType()) {
			case IDotNetElement.METHOD:
				IMethod method = (IMethod) o;
				return method.getElementName() + method.getSignature();
			default:
				return ((IDotNetElement) o).getElementName();
			}
		}
	}

}
