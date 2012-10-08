/****************************************************************************
 * Copyright (c) 2001, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors: B.Brem, Remy Chi Jian Suen 
 * **************************************************************************/
package org.emonic.base.wizards.newcsharplibraryprojectwizard;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildFileManipulator;
import org.emonic.base.preferences.DefaultPrefsSetter;
import org.emonic.base.wizards.Messages;

public class NewCSharpLibraryProjectCreationPage extends WizardNewProjectCreationPage {

	private Text sourceText;

	private Text outputText;

	private String sourceFolder;

	private String outputFolder;

	private Combo bmCombo;

	private Text buildFileText;

	private String buildMechanism;

	private String buildFile;

	private Combo btfCombo;

	private String buildTargetFramework;
	
	private boolean fileNameChanged=false;
	private String initialProjectName="Project";

	public NewCSharpLibraryProjectCreationPage() {
		super(NewCSharpLibraryProjectCreationPage.class.getName());
		setTitle(Messages.getString("NewDotNetProjectCreationPage_NewProjectTitle")); //$NON-NLS-1$
		setDescription(Messages.getString("NewDotNetProjectCreationPage_NewProjectDescription")); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		Composite composite = (Composite) getControl();

		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.getString("NewDotNetProjectCreationPage_ProjectLayout")); //$NON-NLS-1$
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setLayout(new GridLayout(2, false));

		Label label = new Label(group, SWT.LEAD);
		label.setText(Messages.getString("NewDotNetProjectCreationPage_SourceDirName")); //$NON-NLS-1$
		sourceText = new Text(group, SWT.SINGLE | SWT.BORDER);
		sourceText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		sourceText.setText(EMonoPlugin.getDefault().getPreferenceStore().getString(DefaultPrefsSetter.SRCBIN_SRCNAME));
		label = new Label(group, SWT.LEAD);
		label.setText(Messages.getString("NewDotNetProjectCreationPage_BinDirName")); //$NON-NLS-1$
		outputText = new Text(group, SWT.SINGLE | SWT.BORDER);
		outputText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		outputText.setText(EMonoPlugin.getDefault().getPreferenceStore().getString(DefaultPrefsSetter.SRCBIN_BINNAME));

		Group buildGroup = new Group(composite, SWT.NONE);
		buildGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		buildGroup.setLayout(new GridLayout(2, false));
		buildGroup.setText(Messages.getString("NewDotNetProjectCreationPage_BuildGroup")); //$NON-NLS-1$

		label = new Label(buildGroup, SWT.WRAP);
		label.setText(Messages.getString("NewDotNetProjectCreationPage_BuildTool")); //$NON-NLS-1$

		bmCombo = new Combo(buildGroup, SWT.READ_ONLY | SWT.BORDER);
		String[] availableMechanisms = BuildDescriptionFactory.getAvailableMechanisms();
		bmCombo.setItems(availableMechanisms); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		bmCombo.select(0);
		bmCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		bmCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String buildFile = buildFileText.getText().trim();
				if (bmCombo.getSelectionIndex() != bmCombo.getItemCount() - 1) {
					if (buildFile.equals("")) { //$NON-NLS-1$
						setErrorMessage(Messages.getString("NewDotNetProjectCreationPage_MessageMissingBuildFile")); //$NON-NLS-1$
						setPageComplete(false);
					} else {
						setErrorMessage(null);
						setPageComplete(true);
					}
				} else {
					setErrorMessage(null);
					setPageComplete(true);
				}
				if (btfCombo != null) {
					BuildMechanismManipulator bfm =BuildDescriptionFactory.createNewBuildMechamism(bmCombo.getText()); 
					if (bfm.isFrameworkManipulator()){
						btfCombo.setEnabled(true);
						String[] items = getSupportedFrameworksAsSArray(bfm);
						btfCombo.setItems(items);
						btfCombo.select(0);
						
					} else {
						btfCombo.setEnabled(false);
						String[] na = {"NA"};
						btfCombo.setItems(na);
						btfCombo.select(0);
						btfCombo.setEnabled(false);
					}
					if (bfm.isFileLayoutManipulator()){
						if (sourceText != null){
							sourceText.setEnabled(true);
						}
						if (outputText != null){
							outputText.setEnabled(true);
						}
					} else {
						if (sourceText != null){
							sourceText.setEnabled(false);
						}
						if (outputText != null){
							outputText.setEnabled(false);
						}
					}
					if (! fileNameChanged){
						setBuildFileTextAccordingSuggestion(bfm);
					}
				}
			}
		});
		label = new Label(buildGroup, SWT.WRAP);
		label.setText(Messages.getString("NewDotNetProjectCreationPage_TargetFramework")); //$NON-NLS-1$
        btfCombo = new Combo(buildGroup, SWT.READ_ONLY | SWT.BORDER);
        BuildMechanismManipulator bfm= BuildDescriptionFactory.createNewBuildMechamism(bmCombo.getText());
        if (bfm.isFrameworkManipulator()){
			btfCombo.setEnabled(true);
			String[] items = getSupportedFrameworksAsSArray(bfm);
			btfCombo.setItems(items);
			btfCombo.select(0);
			
		} else {
			btfCombo.setEnabled(false);
			String[] na = {"NA"};
			btfCombo.setItems(na);
			btfCombo.select(0);
			btfCombo.setEnabled(false);
		}
        
        btfCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label = new Label(buildGroup, SWT.BEGINNING);
		label.setText(Messages.getString("NewDotNetProjectCreationPage_BuildFile")); //$NON-NLS-1$
		buildFileText = new Text(buildGroup, SWT.SINGLE | SWT.BORDER);
		buildFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		setBuildFileTextAccordingSuggestion(bfm); //$NON-NLS-1$
		buildFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fileNameChanged=true;
				String buildFile = buildFileText.getText().trim();
				if (bmCombo.getSelectionIndex() != bmCombo.getItemCount() - 1) {
					if (buildFile.equals("")) { //$NON-NLS-1$
						setErrorMessage(Messages.getString("NewDotNetProjectCreationPage_MessageMissingBuildFile")); //$NON-NLS-1$
						setPageComplete(false);
					} else {
						setErrorMessage(null);
						setPageComplete(true);
					}
				} else {
					setErrorMessage(null);
					setPageComplete(true);
				}
			}
		});
		
		
		
	}
	
	private String getSuggestedFileName(BuildMechanismManipulator bfm){
		String result = bfm.suggestFileName();
		result=result.replaceFirst(IBuildFileManipulator.BUILDFILENAMEPROJECT, initialProjectName);
		return result;
	}
	
	private String[] getSupportedFrameworksAsSArray(BuildMechanismManipulator bfm) {
		ArrayList res = new ArrayList();
		if (bfm.isFrameworkManipulator()){
			
			String [] frameworks = bfm.getSupportedFrameworks();
			for (int i = 0; i < frameworks.length; i++){
				String[] releases = bfm.getSupportedReleases(frameworks[i]);
				for (int j=0; j< releases.length; j++){
					res.add(frameworks[i] +"-"+ releases[j]);
				} 
			}
			String[] result = new String[res.size()];
			result=(String[]) res.toArray(result);
			return result;
		}
		return new String[0];
	}

	void commitData() {
		sourceFolder = sourceText.getText().trim();
		outputFolder = outputText.getText().trim();
		buildMechanism = bmCombo.getText();
		buildFile = buildFileText.getText().trim();
		buildTargetFramework=btfCombo.getText();
	}

	String getSourceFolder() {
		return sourceFolder;
	}

	String getOutputFolder() {
		return outputFolder;
	}

	String getBuildMechanism() {
		return buildMechanism;
	}

	String getBuildFile() {
		return buildFile;
	}
	
	String getBuildTargetFramework() {
		return buildTargetFramework;
	}

	protected boolean validatePage() {
		String actualProjectName=getProjectName();
		if (!actualProjectName.equals("")){
		   if (! actualProjectName.equals(initialProjectName) && ! fileNameChanged){
			   initialProjectName=actualProjectName;
			   BuildMechanismManipulator bfm= BuildDescriptionFactory.createNewBuildMechamism(bmCombo.getText());
			   setBuildFileTextAccordingSuggestion(bfm);
		   }
				
		}  
		return super.validatePage();
    }
	
	private void setBuildFileTextAccordingSuggestion(BuildMechanismManipulator bfm){
		boolean restoreChanged=fileNameChanged;
		this.buildFileText.setText(getSuggestedFileName(bfm));
		fileNameChanged=restoreChanged;
	}
	

}
