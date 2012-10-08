package org.emonic.base.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;

public class EMonoProjectPropertyPage extends PropertyPage {
	//The list that displays the current bad words
	private Text copyrightTextFld;

	private IProject project;
	private Text namespaceTextFld;
	private Text buildFileTextFld;
	private Combo bmCombo; 

	public EMonoProjectPropertyPage() {
		super();
	}

	public void createControl(Composite parent) {
		noDefaultAndApplyButton();
		//fParent = parent;
		super.createControl(parent);
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite entryTable = new Composite(parent, SWT.NULL);
		IResource resource = (IResource) getElement().getAdapter(
				IResource.class);
		project = resource.getProject();
		//Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		GridLayout layout = new GridLayout();
		entryTable.setLayout(layout);
        // Copyright group
		Group copyrightGroup = new Group(entryTable,SWT.NULL);
        GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
        gd1.horizontalSpan=1;
        gd1.grabExcessHorizontalSpace = true;
        //gd1.grabExcessVerticalSpace=true;
		copyrightGroup.setLayoutData(gd1);
        //gd = new GridData(GridData.FILL_HORIZONTAL);
        GridLayout l1 = new GridLayout();
		l1.numColumns = 1;
		copyrightGroup.setLayout(l1);
        copyrightGroup.setText("Copyright of the project");
		copyrightTextFld = new Text(copyrightGroup, SWT.MULTI);
		//copyrightTextFld.s
		copyrightTextFld.setText("");
		GridData gd2 = new GridData(GridData.FILL_BOTH);
		gd2.heightHint=100;
		gd2.grabExcessVerticalSpace=true;
		copyrightTextFld.setLayoutData(gd2);
		//Namespace group
		Group namespaceGroup = new Group(entryTable,SWT.NULL);
        GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
        gd3.horizontalSpan=1;
        gd3.grabExcessHorizontalSpace = true;
        //gd1.grabExcessVerticalSpace=true;
		namespaceGroup.setLayoutData(gd3);
        //gd = new GridData(GridData.FILL_HORIZONTAL);
        GridLayout l3 = new GridLayout();
		l3.numColumns = 1;
		namespaceGroup.setLayout(l3);
        namespaceGroup.setText("Default Namespace");
		namespaceTextFld = new Text(namespaceGroup, SWT.MULTI);
		//copyrightTextFld.s
		namespaceTextFld.setText("");
		GridData gd4 = new GridData(GridData.FILL_BOTH);
		//gd4.grabExcessVerticalSpace=true;
		namespaceTextFld.setLayoutData(gd4);
		///
		
		
		Group buildGroup = new Group(entryTable,SWT.NULL);
		GridData gd5 = new GridData(GridData.FILL_BOTH);
		buildGroup.setLayoutData(gd5);
        GridLayout l4 = new GridLayout();
		l4.numColumns = 4;
		buildGroup.setLayout(l4);
        buildGroup.setText("Build");
        Label bml = new Label(buildGroup,SWT.WRAP);
        bml.setText("Build Tool");
        //GridData gd5 = new GridData(GridData.FILL_BOTH);
        //bml.setLayoutData(gd5);
        
        
        
        
        bmCombo = new Combo(buildGroup, SWT.READ_ONLY | SWT.BORDER);
		String[] availableMechanisms = BuildDescriptionFactory.getAvailableMechanisms();
		bmCombo.setItems(availableMechanisms); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		bmCombo.select(0);
		bmCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Label bfl = new Label(buildGroup,SWT.WRAP);
        bfl.setText("        Build file");
        buildFileTextFld = new Text(buildGroup, SWT.WRAP);
        buildFileTextFld.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// Purge input from white spaces and new lines
				String s = buildFileTextFld.getText();
				if (s.indexOf(' ')!=-1 || s.indexOf('\n')!=-1){
					s=s.replaceAll(" ", "");
					s=s.replaceAll("\n","");
					buildFileTextFld.setText(s);
				}
			}
		});
		buildFileTextFld.setText("");
		GridData gd6 = new GridData(GridData.FILL_HORIZONTAL);
		buildFileTextFld.setLayoutData(gd6);
		try {
			// Fill with values
			ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
	        copyrightTextFld.setText(mani.getCopyright());
			namespaceTextFld.setText(mani.getNamespace());
			String mech = new ProjectPreferencesManipulator(project).getBuildMechanism();
			String[] allsels = bmCombo.getItems();
			for (int i = 0; i < allsels.length;i++){
				if (allsels[i].equals(mech)) bmCombo.select(i);
			}
			buildFileTextFld.setText( new ProjectPreferencesManipulator(project).getBuildFileName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entryTable;
	}

	public boolean performOk() {
		try {
			ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
			String mechanism = "";
			if (bmCombo.getSelectionIndex() != -1){
				mechanism=bmCombo.getItem(bmCombo.getSelectionIndex());
			}
			mani.setPrefs(copyrightTextFld.getText(),namespaceTextFld.getText(),mechanism,buildFileTextFld.getText());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
