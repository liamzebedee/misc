/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.nunit.internal.ui.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.emonic.nunit.internal.core.Activator;
import org.emonic.nunit.internal.core.Test;

public class NUnitTestRunnerView extends ViewPart {

	public static final String VIEW_ID = "org.emonic.nunit.internal.views.NUnitTestRunnerView"; //$NON-NLS-1$

	private TreeViewer testsViewer;

	private TableViewer stackTraceViewer;

	public void createPartControl(Composite parent) {
		SashForm form = new SashForm(parent, SWT.VERTICAL);
		testsViewer = new TreeViewer(form, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		testsViewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getChildren(Object parentElement) {
				return new Object[0];
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public Object[] getElements(Object inputElement) {
				return ((Collection) inputElement).toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
		testsViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((Test) element).getLabel();
			}

			public Image getImage(Object element) {
				Test test = (Test) element;
				if (test.isRunning()) {
					return Activator.getImage(Activator.IMG_TEST_RUN);
				} else if (test.isSuccessful()) {
					return Activator.getImage(Activator.IMG_TEST_OK);
				} else if (!test.hasErrors()) {
					return Activator.getImage(Activator.IMG_TEST_FAIL);
				} else {
					return Activator.getImage(Activator.IMG_TEST_ERROR);
				}
			}
		});
		testsViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						IStructuredSelection iss = (IStructuredSelection) e
								.getSelection();
						Test test = (Test) iss.getFirstElement();
						stackTraceViewer.refresh();
						if (test != null) {
							String stackTrace = test.getStackTrace();
							if (stackTrace != null) {
								BufferedReader br = new BufferedReader(
										new StringReader(stackTrace));
								try {
									String input = br.readLine();
									while (input != null) {
										stackTraceViewer.add(input);
										input = br.readLine();
									}
								} catch (IOException e1) {
									// this shouldn't happen
								}
							}
						}
					}
				});
		stackTraceViewer = new TableViewer(form);
		stackTraceViewer.setContentProvider(new ArrayContentProvider());
		stackTraceViewer.setLabelProvider(new LabelProvider());
		stackTraceViewer.setInput(Collections.EMPTY_LIST);
		form.setWeights(new int[] { 50, 50 });

		collection = new LinkedList();
		testsViewer.setInput(collection);
	}

	public void setFocus() {
		testsViewer.getControl().setFocus();
	}

	private LinkedList collection;

	public void clear() {
		collection.clear();
		testsViewer.refresh();
	}

	public void interpret(String input) {
		if (input.startsWith("TestStarted")) {
			Test test = new Test(input.substring(12).trim());
			collection.add(test);
			testsViewer.add(collection, test);
		} else if (input.startsWith("TestFinished")) {
			Test test = (Test) collection.getLast();
			test.setRunning(false);
			test.setIsSuccessful(true);
			testsViewer.refresh(test, true);
		} else if (input.startsWith("TestFailed")) {
			Test test = (Test) collection.getLast();
			test.setRunning(false);
			String[] split = input.split(" ", 3);
			test.setIsSuccessful(false);
			test.setStackTrace(split[2]);
			testsViewer.refresh(test, true);
		} else if (input.startsWith("TestCrashed")) {
			Test test = (Test) collection.getLast();
			test.setRunning(false);
			String[] split = input.split(" ", 2);
			test.setHasErrors(true);
			test.setStackTrace(split[1]);
			testsViewer.refresh(test, true);
		}
	}
}
