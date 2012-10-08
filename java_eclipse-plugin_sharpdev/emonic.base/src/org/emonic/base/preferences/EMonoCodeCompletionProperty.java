/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2007 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */

package org.emonic.base.preferences;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;


public class EMonoCodeCompletionProperty extends PropertyPage implements
IWorkbenchPropertyPage {
	
	private IProject project;
	private TabFolder tabFolder;
	private TableViewer srcTableViewer;
	private TableViewer dllDirTableViewer;
	private TableViewer dllFileTableViewer;
	private ArrayList usedSrc;
	private ArrayList usedDirDlls;
	private ArrayList usedFileDlls;
	private Button addSrcDirBtn;
	private Button removeSrcDirBtn;
	private Button addDllDirBtn;
	private Button addDllFileBtn;
	private Button removeDllFileBtn;
	private Button removeDllDirBtn;

	
	public EMonoCodeCompletionProperty() {
		super();
	}
	
	public void createControl(Composite parent) {
		noDefaultAndApplyButton();
		super.createControl(parent);		
	}	

	protected Control createContents(Composite parent) {
		
		if (usedSrc == null)
			usedSrc = new ArrayList();
		if (usedFileDlls == null)
			usedFileDlls = new ArrayList();
		if (usedDirDlls == null)
			usedDirDlls = new ArrayList();
		
		// 1) create a tabbedpane
		tabFolder = new TabFolder(parent, SWT.NULL);
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		project = resource.getProject();
		
		// 2) create a tab for src dirs to include
		// via an add button internal and external 
		// src-dirs are added
		TabItem srcTab = new TabItem(tabFolder, SWT.NULL);
		srcTab.setText("Add Source Code");

		fillSrcTab(srcTab);
		
		// 3) create a tab for external dlls to 
		// be added
		TabItem dllTab = new TabItem(tabFolder, SWT.NULL);
		dllTab.setText("Add Assemblies");
		fillDllTab(dllTab);
			
		
		try {
			// Fill with values
			ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
			
			usedSrc = mani.getCodeCompletionSrc();	
			
			Iterator iter = usedSrc.iterator();
			while (iter.hasNext()) {
				srcTableViewer.add(iter.next());
			}
			if (srcTableViewer.getTable().getItems().length == 0) {
				// list is empty
				removeSrcDirBtn.setEnabled(false);
			}
			
			usedDirDlls = mani.getCodeCompletionDirDll();
			iter = usedDirDlls.iterator();
			while(iter.hasNext()) {
				dllDirTableViewer.add(iter.next());
			}
			if (dllDirTableViewer.getTable().getItems().length == 0) {
				// list is empty
				removeDllDirBtn.setEnabled(false);
			}
			
			usedFileDlls = mani.getCodeCompletionFileDll();
			iter = usedFileDlls.iterator();
			while(iter.hasNext()) {
				dllFileTableViewer.add(iter.next());
			}
			if (dllFileTableViewer.getTable().getItems().length == 0) {
				// list is empty
				removeDllFileBtn.setEnabled(false);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tabFolder;
	}
	

	protected void fillSrcTab(TabItem tabItem) {
		Composite srcComposite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(srcComposite);
		srcComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		srcComposite.setLayout(layout);
		
		Label label = new Label(srcComposite, SWT.LEAD);
		label.setText("Source folders for code completion (searched recursively):");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));

		srcTableViewer = new TableViewer(srcComposite, SWT.SINGLE  | SWT.MULTI | SWT.FULL_SELECTION);
		srcTableViewer.setContentProvider(new ArrayContentProvider());
		srcTableViewer.setLabelProvider(new EMonoCodeCompletionLabelProvider());
		
		Table srcTable = srcTableViewer.getTable();
		srcTable.setLinesVisible(false);
		srcTable.setHeaderVisible(true);
		srcTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		TableColumn column = new TableColumn(srcTable, SWT.LEAD);
		column.setText("Directories");
		column.setWidth(400);
		
		srcTableViewer.setInput(usedSrc);		
		
		addSrcDirBtn = new Button(srcComposite, SWT.PUSH);
		addSrcDirBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true, false));
		addSrcDirBtn.setText("Add folder");
		
		removeSrcDirBtn = new Button(srcComposite, SWT.PUSH);
		removeSrcDirBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true, false));
		removeSrcDirBtn.setText("Remove folder");
		
		addSrcListeners();
		
		//TBD: Do sth with the list input....
	}

	
	protected void fillDllTab(TabItem tabItem) {
		Composite dllComposite = new Composite(tabFolder, SWT.NONE);		
		tabItem.setControl(dllComposite);
		dllComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		dllComposite.setLayout(layout);		
					
		Label dirlabel = new Label(dllComposite, SWT.LEAD); 
		dirlabel.setText("Folders with DLLs/EXEs for code completion (NOT searched recursively):");
		dirlabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));
		
		// add dllDirTable
		dllDirTableViewer = new TableViewer(dllComposite, SWT.SINGLE  | SWT.MULTI | SWT.FULL_SELECTION);
		dllDirTableViewer.setContentProvider(new ArrayContentProvider());
		dllDirTableViewer.setLabelProvider(new EMonoCodeCompletionLabelProvider());
	
		Table dllDirTable = dllDirTableViewer.getTable();
		dllDirTable.setLinesVisible(false);
		dllDirTable.setHeaderVisible(true);
		dllDirTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		TableColumn dirColumn = new TableColumn(dllDirTable, SWT.LEAD);
		dirColumn.setText("Directories");
		dirColumn.setWidth(400);
		
		dllDirTableViewer.setInput(usedDirDlls);	
		
		// set Buttons
		addDllDirBtn = new Button(dllComposite, SWT.PUSH);
		addDllDirBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true, false));
		addDllDirBtn.setText("Add folder");
				
		removeDllDirBtn = new Button(dllComposite, SWT.PUSH);
		removeDllDirBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true, false));
		removeDllDirBtn.setText("Remove folder");			
					
		// add dllFileTable
		Label filelabel = new Label(dllComposite, SWT.LEAD);
		filelabel.setText("Distinct DLLs/EXEs for code completion:");
		filelabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));

		dllFileTableViewer = new TableViewer(dllComposite, SWT.SINGLE  | SWT.MULTI | SWT.FULL_SELECTION);
		dllFileTableViewer.setContentProvider(new ArrayContentProvider());
		dllFileTableViewer.setLabelProvider(new EMonoCodeCompletionLabelProvider());
		
		Table dllFileTable = dllFileTableViewer.getTable();
		dllFileTable.setLinesVisible(false);
		dllFileTable.setHeaderVisible(true);
		dllFileTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		TableColumn fileColumn = new TableColumn(dllFileTable, SWT.LEAD);
		fileColumn.setText("Files");
		fileColumn.setWidth(400);		
		
		dllFileTableViewer.setInput(usedFileDlls);	
		
		// set Buttons		
		addDllFileBtn = new Button(dllComposite, SWT.PUSH);
		addDllFileBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true, false));
		addDllFileBtn.setText("Add DLL/EXE");
		
		removeDllFileBtn = new Button(dllComposite, SWT.PUSH);
		removeDllFileBtn.setLayoutData(new GridData(SWT.FILL, SWT.LEAD, true, false));
		removeDllFileBtn.setText("Remove DLL/EXE");		
		
		addDirDllListeners();
		addFileDllListeners();
		
		//TBD: Do sth with the list input....
	}	


	protected void addSrcListeners(){
	  
		addSrcDirBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(addSrcDirBtn.getShell());
				
				String path = dirDialog.open();
				if (path != null) {
					//String srcDir = dirDialog.getText();
					usedSrc.add(path);
					srcTableViewer.add(path);
				}
				
				if (usedSrc.size() == 0)
					removeSrcDirBtn.setEnabled(false);
				else
					removeSrcDirBtn.setEnabled(true);
			}
		});		
		
		removeSrcDirBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iss = (IStructuredSelection) srcTableViewer.getSelection();
				Object[] array = iss.toArray();
				usedSrc.removeAll(Arrays.asList(array));
				srcTableViewer.remove(array);
				
				if (srcTableViewer.getTable().getItems().length == 0) {
					removeSrcDirBtn.setEnabled(false);
				}
			}
		});
	}
	
	
	protected void addDirDllListeners(){
		
		addDllDirBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(addDllDirBtn.getShell());
				
				String path = dirDialog.open();
				if (path != null) {
					//String srcDir = dirDialog.getText();
					usedDirDlls.add(path);
					dllDirTableViewer.add(path);
				}
				
				if (usedDirDlls.size() == 0)
					removeDllDirBtn.setEnabled(false);
				else
					removeDllDirBtn.setEnabled(true);
			}
		});		
		
		removeDllDirBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iss = (IStructuredSelection) dllDirTableViewer.getSelection();
				Object[] array = iss.toArray();
				usedDirDlls.removeAll(Arrays.asList(array));
				dllDirTableViewer.remove(array);
				
				if (dllDirTableViewer.getTable().getItems().length == 0) {
					removeDllDirBtn.setEnabled(false);
				}
			}
		});		
	}		

	
	protected void addFileDllListeners(){
		
		addDllFileBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(addDllFileBtn.getShell());
				String[] extensions = { "*.dll", "*.DLL", "*.exe", "*.EXE"};
				fileDialog.setFilterExtensions(extensions);
				
				String path = fileDialog.open();
				if (path != null) {
					usedFileDlls.add(path);
					dllFileTableViewer.add(path);
				}
				if (usedFileDlls.size() == 0)
					removeDllFileBtn.setEnabled(false);
				else
					removeDllFileBtn.setEnabled(true);				
			}
		});
		
		removeDllFileBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iss = (IStructuredSelection) dllFileTableViewer.getSelection();
				Object[] array = iss.toArray();
				usedFileDlls.removeAll(Arrays.asList(array));
				dllFileTableViewer.remove(array);
				
				if (dllFileTableViewer.getTable().getItems().length == 0){
					removeDllFileBtn.setEnabled(false);
				}
			}
		});		
	}


	public boolean performOk() {
		try {
			ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
			mani.setCodeCompletionProperties(this.usedSrc, this.usedDirDlls, this.usedFileDlls);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}	
}
