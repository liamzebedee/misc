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
package org.emonic.base.views;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledFormText;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codecompletion.AssemblyParserFactory;
import org.emonic.base.codehierarchy.CodeHierarchyHelper;
import org.emonic.base.codehierarchy.Flags;
import org.emonic.base.codehierarchy.IAssembly;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IField;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.codehierarchy.INamespace;
import org.emonic.base.codehierarchy.IProperty;
import org.emonic.base.codehierarchy.IType;

public class ObjectBrowserView extends ViewPart {

	public static final String VIEW_ID = "org.emonic.base.views.objectBrowser"; //$NON-NLS-1$

	private TreeViewer assemblyViewer;

	private TableViewer classViewer;

	private FormText memberDocumentation;

	private Composite parent;

	private Link noticeText;

	public void createPartControl(Composite parent) {
		this.parent = parent;

		noticeText = new Link(parent, SWT.LEAD);
		noticeText.setText("<a>Open</a> an assembly to browse...");
		noticeText.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				inspectAssembly();
			}
		});

		getViewSite().getActionBars().getMenuManager().add(
				new Action("View Assembly") {
					public void run() {
						inspectAssembly();
					}
				});
	}

	private void createObjectBrowser() {
		SashForm horizontal = new SashForm(parent, SWT.HORIZONTAL);
		horizontal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		horizontal.setLayout(new FillLayout());

		assemblyViewer = new TreeViewer(horizontal, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		assemblyViewer.setContentProvider(new TreeContentProvder());
		assemblyViewer
				.setLabelProvider(new NavigatorSourceElementLabelProvider());
		assemblyViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						IStructuredSelection iss = (IStructuredSelection) e
								.getSelection();
						Object o = iss.getFirstElement();
						if (o instanceof IDotNetElement) {
							classViewer.setInput(o instanceof IType ? o : null);
							memberDocumentation.setText(CodeHierarchyHelper
									.getFormText((IDotNetElement) o), true,
									false);
						}
					}
				});

		SashForm rightVertical = new SashForm(horizontal, SWT.VERTICAL);

		classViewer = new TableViewer(rightVertical);
		classViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				if (inputElement == null) {
					return new Object[0];
				}
				Collection c = new ArrayList();
				IType type = (IType) inputElement;

				IMethod[] methods = type.getMethods();
				for (int i = 0; i < methods.length; i++) {
					if (!Flags.isMethodPrivate(methods[i].getFlags())
							&& methods[i].isConstructor()) {
						c.add(methods[i]);
					}
				}
				for (int i = 0; i < methods.length; i++) {
					if (!Flags.isMethodPrivate(methods[i].getFlags())
							&& !methods[i].isConstructor()) {
						c.add(methods[i]);
					}
				}

				IField[] fields = type.getFields();
				for (int i = 0; i < fields.length; i++) {
					if ((fields[i].getFlags() & Flags.PUBLIC_FIELD) != 0) {
						c.add(fields[i]);
					}
				}

				IProperty[] properties = type.getProperties();
				// remove identical properties so only one shows up in the view
				for (int i = 0; i < properties.length; i++) {
					if (properties[i] == null) {
						continue;
					}
					for (int j = i + 1; j < properties.length; j++) {
						if (properties[j] != null
								&& properties[i].getElementName().equals(
										properties[j].getElementName())) {
							properties[j] = null;
							break;
						}
					}
				}

				for (int i = 0; i < properties.length; i++) {
					if (properties[i] != null
							&& !Flags.isMethodPrivate(properties[i].getFlags())) {
						c.add(properties[i]);
					}
				}

				c.addAll(Arrays.asList(type.getEvents()));

				return c.toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
		classViewer.setLabelProvider(new NavigatorSourceElementLabelProvider());
		classViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						IStructuredSelection iss = (IStructuredSelection) e
								.getSelection();
						Object o = iss.getFirstElement();
						if (o instanceof IDotNetElement) {
							memberDocumentation.setText(CodeHierarchyHelper
									.getFormText((IDotNetElement) o), true,
									false);
						}
					}
				});

		ScrolledFormText scrolledFormText = new ScrolledFormText(rightVertical,
				SWT.V_SCROLL, false);
		memberDocumentation = new FormText(scrolledFormText, SWT.MULTI);
		memberDocumentation.setBackground(memberDocumentation.getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
		scrolledFormText.setBackground(scrolledFormText.getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
		scrolledFormText.setFormText(memberDocumentation);
		scrolledFormText.setExpandVertical(true);

		rightVertical.setLayout(new FillLayout());

		horizontal.setWeights(new int[] { 40, 60 });
	}

	private void inspectAssembly() {
		FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.dll" });
		final String file = dialog.open();
		if (file != null) {
			if (!noticeText.isDisposed()) {
				noticeText.dispose();
				createObjectBrowser();
				parent.layout();
			}
			Job job = new Job("Parsing...") {
				public IStatus run(IProgressMonitor monitor) {
					try {
						monitor.beginTask("Analyzing " + file, 10);
						File documentation = new File(file.substring(0, file
								.lastIndexOf('.'))
								+ ".xml");
						Map[] maps;
						if (!documentation.exists()) {
							maps = new Map[] { Collections.EMPTY_MAP,
									Collections.EMPTY_MAP };
						} else {
							maps = AssemblyParserFactory.createAssemblyParser()
									.parseDocumentation(new FileInputStream(
											documentation));
						}
						monitor.worked(5);
						show(AssemblyParserFactory.createAssemblyParser().parseAssembly( new Path(file), maps));
						return Status.OK_STATUS;
					} catch (Exception e) {
						e.printStackTrace();
						return new Status(IStatus.ERROR, EMonoPlugin.PLUGIN_ID,
								0, "An exception has occurred", e);
					} finally {
						monitor.done();
					}
				}
			};
			job.schedule();
		}
	}

	private void show(final IAssembly assembly) {
		if (assembly != null){
			UIJob job = new UIJob(assemblyViewer.getControl().getDisplay(),
			"Refreshing assembly view") {
				public IStatus runInUIThread(IProgressMonitor monitor) {
					assemblyViewer.setInput(assembly);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	public void setFocus() {
		if (noticeText.isDisposed()) {
			assemblyViewer.getControl().setFocus();
		} else {
			noticeText.setFocus();
		}
	}

	class TreeContentProvder implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			IType[] types = ((INamespace) parentElement).getTypes();
			List publicTypes = new ArrayList(Arrays.asList(types));
			for (int i = 0; i < publicTypes.size(); i++) {
				IType type = (IType) publicTypes.get(i);
				if ((type.getFlags() & Flags.PUBLIC_TYPE) == 0) {
					publicTypes.remove(type);
					i--;
				}
			}
			return publicTypes.toArray();
		}

		public Object getParent(Object element) {
			return ((IDotNetElement) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return !(element instanceof IType);
		}

		public Object[] getElements(Object inputElement) {
			IAssembly assembly = (IAssembly) inputElement;
			INamespace[] namespaces = assembly.getNamespaces();
			List elements = new ArrayList();
			for (int i = 0; i < namespaces.length; i++) {
				IType[] types = namespaces[i].getTypes();
				for (int j = 0; j < types.length; j++) {
					if (Flags.isTypePublic(types[j].getFlags())) {
						elements.add(namespaces[i]);
						break;
					}
				}
			}

			IType[] types = assembly.getTypes();
			for (int i = 0; i < types.length; i++) {
				if (Flags.isTypePublic(types[i].getFlags())) {
					elements.add(types[i]);
				}
			}

			return elements.toArray();
		}

		public void dispose() {
			// nothing to do
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}