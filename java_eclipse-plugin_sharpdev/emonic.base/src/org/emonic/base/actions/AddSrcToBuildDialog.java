/*
 * Created on Feb 4, 2006
 * emonic.base org.emonic.base.actions AddSrcToBuildDialog.java
 */
package org.emonic.base.actions;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;

/**
 * @author bb
 *
 */
public class AddSrcToBuildDialog extends Dialog {

	private static final String NECHANISMDOESNOTSUPPORTERR = "Mechanism does not support c#-files"; //$NON-NLS-1$
	private static final String CSENDING = ".cs"; //$NON-NLS-1$
	private static final String MSGWARNLEVEL = "Warn level must be in range 0-4"; //$NON-NLS-1$
	private static final String TXTTARGETNAMEMUSTBESET = "Target name must be set!"; //$NON-NLS-1$
	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	private static final String WARNLEVELPREF = "4"; //$NON-NLS-1$
	private static final String SPACESFORDUMMY = "                    "; //$NON-NLS-1$
	private static final String TXTSELTARGET = "Select existing target"; //$NON-NLS-1$
	private static final String TXTADDFILETOTARGET = "Add the new file to existing target:"; //$NON-NLS-1$
	private static final String TXTTGTTYPEMODULE = "module"; //$NON-NLS-1$
	private static final String TXTTGTTYPELIB = "library"; //$NON-NLS-1$
	private static final String TXTTGTTYPEWINEXE = "winexe"; //$NON-NLS-1$
	private static final String TXTTGTTYPEEXE = "exe"; //$NON-NLS-1$
	private static final String TXTTGTTYPE = "TargetType"; //$NON-NLS-1$
	private static final String TXTDEBUG = "Debug"; //$NON-NLS-1$
	private static final String TXTOPTIMIZE = "Optimize"; //$NON-NLS-1$
	private static final String TXTWARNLEVEL = "Warn Level (0-4):"; //$NON-NLS-1$
	private static final String TXTDEFINES = "Defines (space seperated):"; //$NON-NLS-1$
	private static final String TXTREFERENCES = "References (space seperated):"; //$NON-NLS-1$
	private static final String CFILENAME = "File name:"; //$NON-NLS-1$
	private static final String TXTTGTNAME = "Target Name:"; //$NON-NLS-1$
	private static final String TXTINSERTINTGT = "Insert the new target in:"; //$NON-NLS-1$
	private static final String TXTADNEWTARGET = "Add new target"; //$NON-NLS-1$
	private static final String MSGFILEEXT = "File extension must be \"cs\""; //$NON-NLS-1$
	private static final String CCSHARPEXT = "cs"; //$NON-NLS-1$
	private static final String MSGSPECIFYFILE = "File name must be specified"; //$NON-NLS-1$
	private static final String UNKNOWNFILE = "Unknown file!"; //$NON-NLS-1$
	private static final String TARGETNAME = "target"; //$NON-NLS-1$

	/**
	 * @param parentShell
	 * @param fl
	 */
	public AddSrcToBuildDialog(Shell parentShell, IFile fl) {
		super(parentShell);
		//Init some parts which have to be not empty
		targetName=TARGETNAME;
		//callingFile=fl;
	}

	//private Text containerText;
	//private IFile callingFile;
	private Label fileText;
	// private ISelection selection;
	private boolean canBuildInsert;
	private IFile buildFile;
	private Label buildFileName;
	private Text targetNameFld;
	private boolean automaticTargetName;
	private Button addNewTargetBtn;
	private Button optimizeButton;
	private Button debugButton;
	private Group targetGroup;
	private Button exeB;
	private Button winexeB;
	private Button libraryB;
	private Button moduleB;
	private Button okBtn;
	private Text warnLevelFld;
	private String warnLevel;
	private boolean addNewButtonButtonStatus;
//    private boolean addToExistingButtonButtonStatus;
	private String targetType;
	private boolean optimizeCode; 
	private boolean debugCode;
	private String references;
	private Text referencesFld;
	private Text defineFld;
	private String defines;
	private Button addToExistingTargetBttn;
//	private boolean addToExistingTarget;
	private List targetList;
	private boolean fDialogError;
	private boolean paddDialogError;
	private boolean targetNameError;
	private String  targetName;
	private BuildMechanismManipulator bfm;
	private String _fileText;
	
	   protected Control createDialogArea(Composite parent) {
	      Composite container = (Composite)super.createDialogArea(parent);
	      GridLayout layout = new GridLayout();
			container.setLayout(layout);
			layout.numColumns = 3;
			layout.verticalSpacing = 9;
			Label label = new Label(container, SWT.NULL);
			label.setText(CFILENAME);

			fileText = new Label(container, SWT.BORDER | SWT.SINGLE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			fileText.setLayoutData(gd);
			if (_fileText == null) {
				_fileText=UNKNOWNFILE;
			}
			fileText.setText(_fileText);
			gd.horizontalSpan=2;
			
			
			// That was the standard file handling, now the build-properties
			buildExistingTargetGroup(container);
			buildNewTargetGroup(container);
			
	        // All set -> So initialize and run it
			initialize();
			fileEntriesChanged();
			//setControl(container);
	      
	      return container;
	   }
	   
	   private void fileEntriesChanged(){
		
		String fileName = getFileName();
        fDialogError = false;
        //Buildfile-Management
        //IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		try {
			
			if( bfm.isSourceTargetManipulator()){
                //IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) bfm;
				String[] targetListE = bfm
					.getAllTargetNamesOfLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
				enable_addNewTarget();
				if (targetListE.length > 0) {
					targetList.setItems(targetListE);
					targetList.select(0);
					enable_addNewTarget();
					enable_addToExistingTarget();
					addToExistingTargetBttn.setSelection(true);
					select_addToExistingTarget();
					setTargetNameFromList();
				} else {
					
					disable_addToExistingTarget();
					addNewTargetBtn.setSelection(true);
					select_addNewTarget();
					
				}
			} else {
				disable_addNewTarget();
				disable_addToExistingTarget();
			}
		} catch (Exception e1) {
			disable_addNewTarget();
			disable_addToExistingTarget();
		}
//		Error handling
		
		if (fileName.length() == 0) {
			updateStatus(MSGSPECIFYFILE);
			//return;
			fDialogError = true;
		}
		// Do we have a buildfile?
		
        //		 Check wether file allready exists
		
		
		//Check Extension
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase(CCSHARPEXT) == false) {
				updateStatus(MSGFILEEXT);
				//return;
				fDialogError = true;
			}
		}
		// If we changed the file name we have to update the ame of the default target
		this.addTargetValuesChanged();
		if (!fDialogError && ! paddDialogError && ! targetNameError) {
			updateStatus(null);
			//checkAndEnableBuildMechanism(resource);
		}
	}
    
//	   private void handleBrowse() {

//	}
	  
	   private void buildNewTargetGroup(Composite container) {
		Group newTargetGroup = new Group(container,SWT.NULL);
        GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
        gd1.horizontalSpan=1;
		newTargetGroup.setLayoutData(gd1);
        //gd = new GridData(GridData.FILL_HORIZONTAL);
        GridLayout buildlayout = new GridLayout();
		buildlayout.numColumns = 4;
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 3;
        newTargetGroup.setLayout(buildlayout);
        newTargetGroup.setLayoutData(gd2);
        newTargetGroup.setText(TXTADNEWTARGET);
        addNewTargetBtn = new Button(newTargetGroup,SWT.CHECK);
        addNewTargetBtn.setText(TXTINSERTINTGT);
        addNewTargetBtn.setSelection(true);
        setBuildButtonStatus(true);
        addNewTargetBtn.addSelectionListener(new SelectionListener ()  {
			public void widgetSelected(SelectionEvent e) {
						select_addNewTarget();
					};
			public void widgetDefaultSelected(SelectionEvent e) {
				select_addNewTarget();
			}
		});
        buildFileName=new Label(newTargetGroup,SWT.NULL);
        GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);	
		gd3.horizontalSpan=3;
        buildFileName.setLayoutData(gd3);
        Label lab1 = new Label (newTargetGroup, SWT.NULL);
		lab1.setText(TXTTGTNAME);
		GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);	
		gd4.horizontalSpan=3;
		targetNameFld = new Text(newTargetGroup, SWT.FILL | SWT.SINGLE);
		targetNameFld.setLayoutData(gd4);
		targetNameFld.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkForNewValidTargetName();
			}
		});
		Label lab2 = new Label (newTargetGroup, SWT.NULL);
		lab2.setText(TXTREFERENCES);
		GridData gd5 = new GridData(GridData.FILL_HORIZONTAL);	
		gd5.horizontalSpan=3;
		referencesFld = new Text(newTargetGroup, SWT.FILL | SWT.SINGLE);
		referencesFld.setLayoutData(gd5);
		referencesFld.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				addTargetValuesChanged();
			}
		});
		Label lab3 = new Label (newTargetGroup, SWT.NULL);
		lab3.setText(TXTDEFINES);
		GridData gd6 = new GridData(GridData.FILL_HORIZONTAL);	
		gd6.horizontalSpan=3;
		defineFld = new Text(newTargetGroup, SWT.FILL | SWT.SINGLE);
		defineFld.setLayoutData(gd6);
		defineFld.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				addTargetValuesChanged();
			}
		});
		Label lab4 = new Label (newTargetGroup, SWT.NULL);
		lab4.setText(TXTWARNLEVEL);
		warnLevelFld = new Text(newTargetGroup, SWT.FILL | SWT.SINGLE);
		warnLevelFld.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				addTargetValuesChanged();
			}
		});
		optimizeButton = new Button(newTargetGroup, SWT.TRAIL| SWT.CHECK);
        optimizeButton.setText(TXTOPTIMIZE);
        optimizeButton.setSelection(true);
        optimizeButton.addSelectionListener(new SelectionListener ()  {
			public void widgetSelected(SelectionEvent e) {
				addTargetValuesChanged();
			};
			public void widgetDefaultSelected(SelectionEvent e) {
				addTargetValuesChanged();
			}
        });
        debugButton = new Button(newTargetGroup,SWT.TRAIL|SWT.CHECK);
        debugButton.setText(TXTDEBUG);
        debugButton.setSelection(true);
        debugButton.addSelectionListener(new SelectionListener ()  {
			public void widgetSelected(SelectionEvent e) {
				addTargetValuesChanged();
			};
			public void widgetDefaultSelected(SelectionEvent e) {
				addTargetValuesChanged();
			}
        });
        GridData gd7 = new GridData(GridData.FILL_HORIZONTAL);	
		gd7.horizontalSpan=4;
		GridLayout targetlayout = new GridLayout();
		targetlayout.numColumns = 4;
		targetGroup = new Group(newTargetGroup,SWT.NULL);
		targetGroup.setLayout(targetlayout);
		targetGroup.setLayoutData(gd7);
		targetGroup.setText(TXTTGTTYPE);
		exeB = new Button(targetGroup, SWT.RADIO);
		exeB.setText(TXTTGTTYPEEXE);
		winexeB = new Button(targetGroup, SWT.RADIO);
		winexeB.setText(TXTTGTTYPEWINEXE);
		libraryB = new Button(targetGroup, SWT.RADIO);
		libraryB.setText(TXTTGTTYPELIB);
		moduleB = new Button(targetGroup, SWT.RADIO);
		moduleB.setText(TXTTGTTYPEMODULE); 
		exeB.setSelection(true);
	}
	
		
	/**
	 * @param container
	 */
	private void buildExistingTargetGroup(Composite container) {
		Group existingTargetGroup = new Group(container,SWT.NULL);
		GridLayout buildlayout = new GridLayout();
		buildlayout.numColumns = 2;
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 3;
		existingTargetGroup.setLayout(buildlayout);
        existingTargetGroup.setLayoutData(gd2);
        existingTargetGroup.setText(TXTSELTARGET);
        // Dialogs
        addToExistingTargetBttn = new Button(existingTargetGroup,SWT.CHECK);
        addToExistingTargetBttn.setText(TXTADDFILETOTARGET);
        addToExistingTargetBttn.setSelection(true);
        addToExistingTargetBttn.addSelectionListener(new SelectionListener ()  {
			public void widgetSelected(SelectionEvent e) {
						select_addToExistingTarget();
					};
			public void widgetDefaultSelected(SelectionEvent e) {
				select_addToExistingTarget();
			}
		});
        targetList = new List(existingTargetGroup,SWT.SINGLE);
        //Handler
        targetList.addSelectionListener(new SelectionListener ()  {
			public void widgetSelected(SelectionEvent e) {
				setTargetNameFromList();
			};
			public void widgetDefaultSelected(SelectionEvent e) {
				setTargetNameFromList();
			}
        });    
        for (int i = 0; i<3; i++) {
			targetList.add(SPACESFORDUMMY);
		}
	}

	/**
	 * Tests if the current workbench selection is a suitable
	 * container to use.
	 */
		
	private void initialize() {

		try {

			if ( bfm.isSourceTargetManipulator()){
                //IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) bfm;
				String[] targetListE = bfm.getAllTargetNamesOfLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
				if (targetListE.length > 0) {
					targetList.setItems(targetListE);
					targetList.select(0);
					enable_addNewTarget();
					enable_addToExistingTarget();
					addToExistingTargetBttn.setSelection(true);
					select_addToExistingTarget();
					setTargetNameFromList();
				} else {
					enable_addNewTarget();
					enable_addToExistingTarget();
					addNewTargetBtn.setSelection(true);
					select_addNewTarget();
					disable_addToExistingTarget();
				}
				//container = container.getFolder(container.findMember(
				//		srcDir).getProjectRelativePath());
				// Which targets do we have?
			} else {
				disable_addNewTarget();
				disable_addToExistingTarget();
			}
		} catch (Exception e) {
			disable_addNewTarget();
			disable_addToExistingTarget();
		}

		targetNameFld.setText(targetName);
		automaticTargetName = false;
		warnLevelFld.setText(WARNLEVELPREF);
		setWarnLevel(WARNLEVELPREF);
	}
	
	private void updateStatus(String message) {
		//setErrorMessage(message);
		//setPageComplete(message == null);
	}

	
	public String getFileName() {
		return fileText.getText();
	}
	
	public boolean getBuildInsert(){
		return (canBuildInsert && addNewTargetBtn.getSelection());
	}
	
	
	public boolean getDebug(){
		return debugButton.getSelection();
	}
	
	public void setTargetNameFromList(){
		try{
			targetName=targetList.getSelection()[0];
		} catch (Exception e){};
	}
	
	
	public String getTargetName(){
		return targetName;
//		if (this.addToExistingTargetBttn.getSelection()==true){
//			return targetNameFld.getText();
//		}
//		return targetList.getSelection()[0];
	}

	/**
	 * @param buildFile The buildFile to set.
	 */
	private void setBuildFile(IFile buildFile) {
		this.buildFile = buildFile;
	}

	/**
	 * @return Returns the buildFile.
	 */
	public IFile getBuildFile() {
		return buildFile;
	}
	
	/**
	 * @return Returns the targetType.
	 */
	public String getTargetType() {
		return targetType;
	}
	/**
	 * @param targetType The targetType to set.
	 */


	/**
	 * @return
	 */
	public String getWarnLevel() {
		return warnLevel;
	}
	
	/**
	 * @param string
	 */
	private void setTargetType(String targetType) {
		this.targetType=targetType;
	}

	/**
	 * 
	 */
	private void checkForNewValidTargetName() {
		targetNameError=false;
		targetName =  targetNameFld.getText();
		if (targetName.length()==0){
			updateStatus(TXTTARGETNAMEMUSTBESET);
			targetNameError= true;
		}
		if (! targetNameError && ! fDialogError && ! paddDialogError) {
			updateStatus(null);
		}
	}

	private void disable_addToExistingTarget(){
		addToExistingTargetBttn.setEnabled(false);
		addToExistingTargetBttn.setSelection(false);
		targetList.setEnabled(false);
	}
	
	private void enable_addToExistingTarget(){
		addToExistingTargetBttn.setEnabled(true);
		if (addToExistingTargetBttn.getEnabled()==true){
			targetList.setEnabled(true);
		}
	}
	
	private void disable_addNewTarget(){
		setBuildFile(null);
		buildFileName.setText(EMPTYSTRING);
		canBuildInsert = false;
		targetNameFld.setEnabled(false);
		addNewTargetBtn.setEnabled(false);
		addNewTargetBtn.setSelection(false);
		optimizeButton.setEnabled(false);
		debugButton.setEnabled(false);
		exeB.setEnabled(false);
		winexeB.setEnabled(false);
		libraryB.setEnabled(false);
		moduleB.setEnabled(false);
		warnLevelFld.setEnabled(false);
		referencesFld.setEnabled(false);
		defineFld.setEnabled(false);
	}
	
	private void enable_addNewTarget(){
		canBuildInsert = true;
		addNewTargetBtn.setEnabled(true);
//		if ((IBuildFileManipulator.class.isAssignableFrom(bfm2.getClass()) )){
//			setBuildFile(((IBuildFileManipulator)bfm2).getBuildFile());
//		} else {
//			setBuildFile(null);
//		}
		buildFileName.setText(getBuildFile().getLocation().toOSString());
		if (addNewTargetBtn.getSelection() == true) {
			targetNameFld.setEnabled(true);
			optimizeButton.setEnabled(true);
			debugButton.setEnabled(true);
			exeB.setEnabled(true);
			winexeB.setEnabled(true);
			libraryB.setEnabled(true);
			moduleB.setEnabled(true);
			warnLevelFld.setEnabled(true);
			referencesFld.setEnabled(true);
			defineFld.setEnabled(true);
		}
	}
	
	/**
	 * 
	 */
	private void select_addToExistingTarget() {
		if (addToExistingTargetBttn.getSelection()) {
			addNewTargetBtn.setSelection(false);
			targetList.setEnabled(true);
			targetNameFld.setEnabled(false);
			optimizeButton.setEnabled(false);
			debugButton.setEnabled(false);
			exeB.setEnabled(false);
			winexeB.setEnabled(false);
			libraryB.setEnabled(false);
			moduleB.setEnabled(false);
			warnLevelFld.setEnabled(false);
			referencesFld.setEnabled(false);
			defineFld.setEnabled(false);
			if (okBtn != null) {
				okBtn.setEnabled(true);
			}
			updateAddToExistingButtonButtonStatus();
		} else if (!addNewTargetBtn.getSelection()) {
			if (okBtn != null) {
				okBtn.setEnabled(false);
			}
		}
	}

	private void select_addNewTarget() {
		if (addNewTargetBtn.getSelection()) {
			addToExistingTargetBttn.setSelection(false);
			targetList.setEnabled(false);
			targetNameFld.setEnabled(true);
			optimizeButton.setEnabled(true);
			debugButton.setEnabled(true);
			exeB.setEnabled(true);
			winexeB.setEnabled(true);
			libraryB.setEnabled(true);
			moduleB.setEnabled(true);
			warnLevelFld.setEnabled(true);
			referencesFld.setEnabled(true);
			defineFld.setEnabled(true);
			if (okBtn != null) {
				okBtn.setEnabled(true);
			}
			updateAddNewButtonButtonStatus();
		} else if (!addToExistingTargetBttn.getSelection()) {
			if (okBtn != null) {
				okBtn.setEnabled(false);
			}
		}
	}
	
	private void addTargetValuesChanged(){
		paddDialogError=false;
		targetNameError=false;
		if (automaticTargetName){
			if (isAddNewButtonButtonStatus()) {
				if (targetNameFld.getText().equals(targetName)) {
					// Generate target name from file name: Strip cs
					//System.out.println("I want to set the Target Name to "
					// +fileName );
					targetName = this.getFileName().replaceAll(CSENDING, EMPTYSTRING);
					targetNameFld.setText(targetName);
				} else {
					// In future: Prevent update
					//System.out.println("I dont want to set the target name");
					automaticTargetName = false;
					targetName = targetNameFld.getText();
					targetNameFld.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							addTargetValuesChanged();
						}
					});
				}
			}
			
		}
		if(! automaticTargetName) {
			checkForNewValidTargetName();
		}
//		 Valid warn-level?
		String lvl = warnLevelFld.getText();
		int lvli = 4;
		try{
		  lvli = Integer.parseInt(lvl);
		} catch (NumberFormatException e){
			// Nothing to do:lvli is already right 
		}
		if (lvli < 0 || lvli > 4 ){
			updateStatus(MSGWARNLEVEL);
			paddDialogError = true;
		} else {
			setWarnLevel(lvl);
		}
		// ReferencesFld
		setReferences(referencesFld.getText());
		// DefinesFld
		setDefines(defineFld.getText());
		//addNewTargetBtn
		setBuildButtonStatus(addNewTargetBtn.getSelection());
		// Target Type
		if (exeB.getSelection()) {
			setTargetType(  TXTTGTTYPEEXE);
		}
		if (winexeB.getSelection()) {
			setTargetType(TXTTGTTYPEWINEXE);
		}
		if (libraryB.getSelection()) {
			setTargetType(TXTTGTTYPELIB);
		}
		if (moduleB.getSelection()) {
			setTargetType (TXTTGTTYPEMODULE);
		}
		// Optimze code?
		setOptimizeCode(optimizeButton.getSelection());
		// Generate debug code?
		setDebugCode(debugButton.getSelection());
		if ( !fDialogError && ! paddDialogError && ! targetNameError) updateStatus(null);
	}
	
	public void setBuildButtonStatus(boolean buildButtonStatus) {
		this.addNewButtonButtonStatus = buildButtonStatus;
	}
	
	public void setWarnLevel(String warnLevel) {
		this.warnLevel = warnLevel;
	}
	
	public void updateAddToExistingButtonButtonStatus() {
		//this.addToExistingButtonButtonStatus = addToExistingTargetBttn.getSelection();
	}
	
	public void updateAddNewButtonButtonStatus() {
		this.addNewButtonButtonStatus = addNewTargetBtn.getSelection();
	}
	
	public boolean isAddNewButtonButtonStatus() {
		return addNewButtonButtonStatus;
	}
	
	public void setDefines(String defines) {
		this.defines = defines;
	}
	
	public void setReferences(String references) {
		this.references = references;
	}
	
	
	public void setDebugCode(boolean debugCode) {
		this.debugCode = debugCode;
	}
	
	public void setOptimizeCode(boolean Code) {
		this.optimizeCode = Code;
	}
	
	public void setFileText(String fn){
		_fileText =fn;
	}
	
	/**
	 * Set the build mechanism manipulator
	 * @param bfm
	 */
	public void setBuildMechanismManipulator(BuildMechanismManipulator bfm){
		this.bfm=bfm;
		if (bfm.isBuildFileManipulator()){
			setBuildFile(bfm.getBuildFile());
		}
		
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		okBtn = getButton(IDialogConstants.OK_ID);
	}
	
	protected void okPressed(){
		//New target or add to target?
		if (bfm.isSourceTargetManipulator()){
		//if (IBuildSourceTargetManipulator.class.isAssignableFrom(bfm.getClass())){
			//IBuildSourceTargetManipulator tgm = (IBuildSourceTargetManipulator) bfm;
			
			
			
			if (bfm.supportsLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE)){
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IPath path = new Path(_fileText);
				IFile sfile=root.getFile(path);
				
				boolean isNewTarget=false;
				Target target=bfm.getTarget(targetName);
				if (target==null){
					isNewTarget=true;
					target = new SourceTarget();
					target.setName(targetName);
					((SourceTarget)target).setLanguage("IBuildMechanismDescriptor.CSHARP_LANGUAGE");
					// TODO: In cases of new language support: Decide the type according the file ending
					
				}
				if (SourceTarget.class.isAssignableFrom(target.getClass())){
					SourceTarget sTarget = (SourceTarget) target;
					if (addNewButtonButtonStatus){
						addTargetValuesChanged();
						sTarget.setType(getTargetType());
						sTarget.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
						if (isNewTarget){
							// TODO: Let the user decide whether the target should be referenced
							bfm.writeNewTargetInTree(target, true);
							isNewTarget=false;
						} else {
							bfm.rewriteTarget(sTarget);
						}
					} 
					sTarget.addSource(sfile);
					//ToExistingLanguageTarget(getTargetName(),relpath, fileName,IBuildMechanismDescriptor.CSHARP_LANGUAGE);
					sTarget.setOptimization(this.optimizeCode);
					sTarget.setDebuggingOutput(this.debugCode);
					if (this.references!=null && ! this.references.equals("")){
						sTarget.setReferences(this.references.split("\\s+"));
					} else {
						sTarget.setReferences(new String[0]);
					}
					String definitions = this.defines;
					if (definitions!=null && ! definitions.equals("")){
						sTarget.setDefinitions(definitions.split("\\s+"));
					} else {
						sTarget.setDefinitions(new String[0]);
					}
					if (isNewTarget){
						// TODO: Let the user decide whether the target should be referenced
						bfm.writeNewTargetInTree(target, true);
						isNewTarget=false;
					} else {
						bfm.rewriteTarget(sTarget);
					}
					bfm.save();
				}
			} else {
				ErrorDialog.openError(null,NECHANISMDOESNOTSUPPORTERR,"It seems the choosen build mechanism is not able to handle targets", null);
			}

		} else {
			
			ErrorDialog.openError(null,NECHANISMDOESNOTSUPPORTERR,"It seems the choosen build mechanism does not support c#-files", null);
		}
		this.close();
	}
	
}
