package org.emonic.base.buildmechanism.buildEditor;


import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;

public class BuildSymbolicEditorPage extends FormPage {
	private static final String NA = "--";

	private static final String NO_LANGUAGE_TARGET = "No language target";

	private BuildMechanismManipulator buildMani;

	private Tree targetTree;

	private TreeItem TreeTop;

	Target actualTarget;

	Text projectName;

	Text sourceDir;

	Text binDir;

	Text actTgtDependencies;

	Label actTgtName;
	
	private BuildEditor editor;

	private Combo actTgtTypeCombo;

	private Combo actTgtWlevelCombo;

	private Text actTgtDefinitions;

	private Text actTgtReferences;

	private Button actTgtDebugCheckBox;

	private Button actTgtOptimazionCheckBox;

	//boolean changed;

	private FormToolkit toolkit;

	private ScrolledForm form;

	private Hyperlink addTargetLnk;

	

	private Composite leftComposite;

	private Composite rightComposite;

	private Label actTgtLanguage;

	private Hyperlink removeActualElementLnk;

	private Hyperlink addSrcToActualTargetLnk;

	private Hyperlink renameActualElementLnk;

	private Label actElementName;

	public BuildSymbolicEditorPage(BuildEditor parent) {
		super(parent, "BuildProps", "BuildProperty Page");
        editor=parent;
	}

	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		form = managedForm.getForm();

		form.setText("Build Properties");
		// We choose the following layout:
		// 2 colums with independent canvases
		GridLayout layout = new GridLayout(2, true);
		form.getBody().setLayout(layout);
		leftComposite = toolkit.createComposite(form.getBody());
		rightComposite= toolkit.createComposite(form.getBody());
		GridLayout leftlayout = new GridLayout(1, false);
		leftComposite.setLayout(leftlayout);
		leftComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout rightlayout = new GridLayout(1, false);
		rightComposite.setLayout(rightlayout);
		rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); 
		
		createPropertiesSection();
		createTargetsSection();
		createDetailsSection();
	}
	
	private void createPropertiesSection() {
		Section propertyGroup = toolkit.createSection(leftComposite,
				Section.TWISTIE | Section.TITLE_BAR);

		propertyGroup.setText("General Properties");
		propertyGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Composite propertyGroupComposite = toolkit.createComposite(
				propertyGroup, SWT.WRAP);

		propertyGroupComposite.setLayout(new GridLayout(2, false));
		// The project name
		toolkit.createLabel(propertyGroupComposite, "Project name:");
		// pnLabel.setText("ProjectName");
		projectName = toolkit.createText(propertyGroupComposite, buildMani
				.getBuildProjectName(), SWT.SINGLE);
		projectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (!buildMani.isBuildProjectManipulator()) {
			projectName.setEnabled(false);
		}
		projectName.addFocusListener(new syncService(this));
		projectName.addKeyListener(new syncKeyListener(this));
		// The source dir
		toolkit.createLabel(propertyGroupComposite, "Source directory:");
		String srcdr = buildMani.getSrcDir();
		if (srcdr == null)
			srcdr = "";
		sourceDir = toolkit.createText(propertyGroupComposite, srcdr,
				SWT.SINGLE);
		sourceDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (!buildMani.isFileLayoutManipulator()) {
			sourceDir.setEnabled(false);
		}
		sourceDir.addFocusListener(new syncService(this));
		sourceDir.addKeyListener(new syncKeyListener(this));
		
		toolkit.createLabel(propertyGroupComposite, "Output directory:");
		
		// The bin dir
		String bindr = buildMani.getBinDir();
		if (bindr == null)
			bindr = "";
		
		
		binDir = toolkit.createText(propertyGroupComposite, bindr, SWT.SINGLE);
		binDir.addFocusListener(new syncService(this));
		binDir.addKeyListener(new syncKeyListener(this));
		binDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (!buildMani.isFileLayoutManipulator()) {
			binDir.setEnabled(false);
		}
		propertyGroup.setClient(propertyGroupComposite);
		propertyGroup.setExpanded(true);
		
		addTargetLnk=toolkit.createHyperlink(propertyGroupComposite, "Add a new target", SWT.NONE);
		addTargetLnk.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				 AddTargetDialog td=  new AddTargetDialog(getSite().getShell());   
	             int res = td.open();
	             if (res == Dialog.OK){
	             	 buildMani.writeNewTargetInTree(td.getResultTarget(),td.setDependencyToDefault());
	            	 setTargetTreeToBuild();
	           	     syncEditorPage(true);
	             } 
			}
		});
		toolkit.paintBordersFor(propertyGroupComposite);
	}
	
	private void createTargetsSection() {
		Section targetGroup = toolkit.createSection(leftComposite,
				Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION);
		targetGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		targetGroup.setText("Build Targets");
		targetGroup.setDescription("Select the target to modify");
		Composite targetGroupCompo = toolkit.createComposite(targetGroup,
				SWT.NONE);
		GridLayout layout1 = new GridLayout();
		targetGroupCompo.setLayout(layout1);

		GridData tgtgdata = new GridData();
		tgtgdata.horizontalAlignment = SWT.FILL;
		tgtgdata.verticalAlignment = SWT.FILL;
		// tgtgdata.minimumHeight=this.getSize().y-10;
		tgtgdata.grabExcessVerticalSpace = true;
		tgtgdata.grabExcessHorizontalSpace = true;
		targetGroupCompo.setLayoutData(tgtgdata);

		targetTree = toolkit.createTree(targetGroupCompo, SWT.H_SCROLL
				| SWT.V_SCROLL);
		targetTree.addSelectionListener(new TreeSelectionListener());
		//What are the dims of our editor?
		
		targetTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		targetTree.setLinesVisible(true);
		TreeTop = new TreeItem(targetTree, SWT.NONE);
		TreeTop.setText("Targets");
		targetTree.setTopItem(TreeTop);
		// Add all targets under this node
		setTargetTreeToBuild();
		targetGroup.setClient(targetGroupCompo);

		targetGroup.setExpanded(true);
		toolkit.paintBordersFor(targetGroupCompo);

		//Actions for the context menue
//		createTreeActions();
//		// The context menu for the tree
//		MenuManager menuManager = new MenuManager();
//		menuManager.add(new Separator("First"));
//		//GroupMarker marker = new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS);
//		//menuManager.add(marker);
//	    //menuManager.setRemoveAllWhenShown(true);
//	    menuManager.removeAll();
//	    menuManager.setRemoveAllWhenShown(true);
//        menuManager.addMenuListener(new IMenuListener() {
//                public void menuAboutToShow(IMenuManager mgr) {
//                        fillContextMenu(mgr);
//                }
//
//				private void fillContextMenu(IMenuManager mgr) {
//					mgr.removeAll();
//					mgr.add(addTargetAction);
//					
//				}
//        });
//
//		
//		Menu contextMenu = menuManager.createContextMenu(targetTree);
//		MenuItem[] actItems = contextMenu.getItems();
//		for (int i =0; i < actItems.length;i++){
//			actItems[i].setEnabled(false);
//		}
//		MenuItem newContextMenuItem = new MenuItem(contextMenu, SWT.NONE);
//		newContextMenuItem.setText("context.item");
//		TreeViewer tviewer = new TreeViewer(targetTree);
//		targetTree.setMenu(contextMenu);
//        getSite().registerContextMenu(menuManager,tviewer );
	}
	
	private void createDetailsSection() {
		Section actualtargetGroup = toolkit.createSection(rightComposite,
				Section.TITLE_BAR);
		actualtargetGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite actualTargetGroupCombo = toolkit.createComposite(
				actualtargetGroup, SWT.WRAP);
		// actualtargetGroup=new Group(comp,SWT.FILL);
		actualtargetGroup.setText("Properties of selected target");
		GridLayout actTgtGroupLO = new GridLayout();
		actTgtGroupLO.numColumns = 2;
		actualTargetGroupCombo.setLayout(actTgtGroupLO);

		toolkit.createLabel(actualTargetGroupCombo, "Target name:");

		actTgtName = toolkit
				.createLabel(actualTargetGroupCombo, "", SWT.SINGLE);//
		toolkit.createLabel(actualTargetGroupCombo, "Target language:");
		actTgtLanguage=toolkit.createLabel(actualTargetGroupCombo, NO_LANGUAGE_TARGET);
		
		// new Label(actualtargetGroup,SWT.FILL);
		actTgtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		toolkit.createLabel(actualTargetGroupCombo, "Dependencies:");
		actTgtDependencies = toolkit.createText(actualTargetGroupCombo, null, SWT.SINGLE);
		actTgtDependencies.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		actTgtDependencies.addFocusListener(new syncService(this));
		actTgtDependencies.addKeyListener(new syncKeyListener(this));
		
		toolkit.createLabel(actualTargetGroupCombo, "Defines (comma-separated):");

		actTgtDefinitions = toolkit.createText(actualTargetGroupCombo, null, SWT.SINGLE);
		actTgtDefinitions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		actTgtDefinitions.addFocusListener(new syncService(this));
		actTgtDefinitions.addKeyListener(new syncKeyListener(this));
		toolkit
				.createLabel(actualTargetGroupCombo,
						"References (comma-separated):");
		actTgtReferences = toolkit.createText(actualTargetGroupCombo, null, SWT.SINGLE);
		actTgtReferences.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		actTgtReferences.addFocusListener(new syncService(this));
		actTgtReferences.addKeyListener(new syncKeyListener(this));
		toolkit.createLabel(actualTargetGroupCombo, "Compile sources to:");

		actTgtTypeCombo = new Combo(actualTargetGroupCombo, SWT.READ_ONLY
				| SWT.BORDER);
		actTgtTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		actTgtTypeCombo.setItems(new String[] { "exe", "winexe", "library",
				"module", NA });
		actTgtTypeCombo.select(0);
		actTgtTypeCombo.addFocusListener(new syncService(this));
		actTgtTypeCombo.addKeyListener(new syncKeyListener(this));
		toolkit.adapt(actTgtTypeCombo, true, true);
		
		toolkit.createLabel(actualTargetGroupCombo, "Warning level:");
		
		actTgtWlevelCombo = new Combo(actualTargetGroupCombo, SWT.READ_ONLY
				| SWT.BORDER);
		actTgtWlevelCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		actTgtWlevelCombo.setItems(new String[] { "0", "1", "2", "3", "4" });
		actTgtWlevelCombo.select(0);
		actTgtWlevelCombo.addFocusListener(new syncService(this));
		actTgtWlevelCombo.addKeyListener(new syncKeyListener(this));
		toolkit.createLabel(actualTargetGroupCombo, "Produce debugging output:");
		actTgtDebugCheckBox = toolkit.createButton(actualTargetGroupCombo, "",
				SWT.CHECK);
		toolkit.createLabel(actualTargetGroupCombo, "Produce optimized code:");
		actTgtOptimazionCheckBox = toolkit.createButton(actualTargetGroupCombo,
				"", SWT.CHECK);
		actTgtOptimazionCheckBox.addFocusListener(new syncService(this));
		actTgtOptimazionCheckBox.addKeyListener(new syncKeyListener(this));
		
		// Commands

		addSrcToActualTargetLnk =toolkit.createHyperlink(actualTargetGroupCombo, "Add sources to this target", SWT.NONE);
		addSrcToActualTargetLnk.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				  //MessageDialog dia = new MessageDialog(getSite().getShell(),"Really delete?",Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING),"Do You really want to delete this target?",MessageDialog.QUESTION,null,0);
				  FileDialog fd = new FileDialog(getSite().getShell(),SWT.OPEN|SWT.MULTI);
				  IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
					.getRoot();
				  fd.setFilterPath( root.getFullPath().toOSString());
				  // TODO Expand if we support more languages!
				  String[] exts = {"*.cs","*"};
				  fd.setFilterExtensions(exts);
				  String res = fd.open();
				  if (res!=null){
					  String filterPath=fd.getFilterPath();
					  Target act = buildMani.getTarget(actTgtName.getText());
					  if (act != null && act instanceof SourceTarget){
						  SourceTarget sact = (SourceTarget)act;
						  String[] selectedFiles = fd.getFileNames();
						  for (int i=0; i < selectedFiles.length;i++){
							  // Convert in iFile
							  //IFile asfl=new File(selectedFiles[i]);
							  IPath path = new Path(filterPath + File.separator+ selectedFiles[i]);
							  IFile targetFile= root.getFileForLocation(path);
							  sact.addSource(targetFile);
							  buildMani.rewriteTarget(sact);
							  setTargetTreeToBuild();
				           	  syncEditorPage(true);
						  }
					  }
				  }
			}
		});
		actualtargetGroup.setClient(actualTargetGroupCombo);
		actualtargetGroup.setExpanded(true);
		toolkit.paintBordersFor(actualTargetGroupCombo);
		
		/**
	   The selected element itself, not the target!
	   	
       **/
		Section actualSelectedGroup = toolkit.createSection(rightComposite,
				Section.TITLE_BAR);
		actualSelectedGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite actualSelectedGroupCombo = toolkit.createComposite(
				actualSelectedGroup, SWT.WRAP);
		// actualtargetGroup=new Group(comp,SWT.FILL);
		actualSelectedGroup.setText("Properties of selected element");
		GridLayout actSelectedGroupLO = new GridLayout();
		actSelectedGroupLO.numColumns = 2;
		actualSelectedGroupCombo.setLayout(actSelectedGroupLO);

		toolkit.createLabel(actualSelectedGroupCombo, "Element Name:");
		
		actElementName = toolkit
				.createLabel(actualSelectedGroupCombo, NA, SWT.SINGLE);
		actElementName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		removeActualElementLnk=toolkit.createHyperlink(actualSelectedGroupCombo, "Remove", SWT.NONE);
		removeActualElementLnk.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				//MessageDialog dia = new MessageDialog(getSite().getShell(),"Really delete?",Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING),"Do You really want to delete this target?",MessageDialog.QUESTION,null,0);
				String msg = "Do you really want to remove \"" + actElementName.getText() + "\" from build?";
				if (! actTgtName.getText().equals(actElementName.getText())){
					msg += "\n"+actElementName.getText()+" itself will not be removed from disk!";
				}
				if (MessageDialog.openConfirm(getSite().getShell(), "Confirm remove", msg)){
					if ( actTgtName.getText().equals(actElementName.getText())){
					   buildMani.deleteTarget(buildMani.getTarget(actTgtName.getText()));
					} else {
						// remove the source only
						Target tgt = buildMani.getTarget(actTgtName.getText());
						if (SourceTarget.class.isAssignableFrom( tgt.getClass())){
							IFile[] srcs =((SourceTarget)tgt).getSources();
							int nr = -1;
							for (int i = 0; i < srcs.length;i++){
								String shownText = srcs[i].getProjectRelativePath()
								.toOSString();
								if (shownText.equals(actElementName.getText())){
									nr = i;
								}
							}
							if (nr != -1){
								IFile[] newSrcs=new IFile[srcs.length-1];
								for (int i = 0; i < newSrcs.length; i++){
									if (i < nr){
										newSrcs[i]=srcs[i];
									} else {
										newSrcs[i]=srcs[i+1];
									}
									
								}
								((SourceTarget)tgt).setSources(newSrcs);
								buildMani.rewriteTarget(tgt);
							
							}
						}
					}
					setTargetTreeToBuild();
					syncEditorPage(true);
				}
				
			}
		});
		renameActualElementLnk=toolkit.createHyperlink(actualSelectedGroupCombo, "Rename", SWT.NONE);
		renameActualElementLnk.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				String msg = "Rename \"" + actElementName.getText() + "\"\n";
				if (! actTgtName.getText().equals(actElementName.getText())){
					msg += "Please notice: We will also try to rename the source file on disk!\n";
				}
				InputDialog newNameDia= new InputDialog(getSite().getShell(), "Rename", msg+"Please enter the new name:","",new IInputValidator(){

					public String isValid(String newText) {
						if (newText.equals("")){
							return "ERROR: New name is empty";
						}
						if (newText.indexOf(' ')!=-1){
							return "ERROR: Spaces are not allowed";
						}
						return null;
					}}); 
				
				if (newNameDia.open() == InputDialog.OK){
					if (actTgtName.getText().equals(actElementName.getText())){
						// Rename the target
						actualTarget.setNewName(newNameDia.getValue());
						buildMani.rewriteTarget(actualTarget);
					} else {
						// Rename the file in the build editor and on the disk
						Target tgt = buildMani.getTarget(actTgtName.getText());
						if (SourceTarget.class.isAssignableFrom( tgt.getClass())){
							IFile[] srcs =((SourceTarget)tgt).getSources();
							int nr = -1;
							for (int i = 0; i < srcs.length;i++){
								String shownText = srcs[i].getProjectRelativePath()
								.toOSString();
								if (shownText.equals(actElementName.getText())){
									nr = i;
								}
							}
							if (nr != -1){
								// We have actually something to rename
								IFile oldFile=srcs[nr];
								IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
								.getRoot();
								// Normally, the first element of the path is the project name itself. It is not displayed, add it!
								String projectPart=oldFile.getProject().getName();
								 IPath path = new Path( projectPart + File.separator+ newNameDia.getValue());
								 
								 IFile targetFile= root.getFile(path);
								srcs[nr]=targetFile;
								((SourceTarget)tgt).setSources(srcs);
								buildMani.rewriteTarget(tgt);
								// Try to rename the file
								try{
									// Build the path in the form which the "move"-call wants
									IPath oldP = oldFile.getFullPath();
									IPath newFP = targetFile.getFullPath();
									int common=oldP.matchingFirstSegments(newFP);
									IPath wantedPath=newFP.removeFirstSegments(common);
									
									oldFile.move(wantedPath, true, new NullProgressMonitor());
								} catch (Exception ex){
									MessageDialog.openWarning(getSite().getShell(), "Warning", "Could not change resource, error was:" +ex.getMessage());
								}
								// Unvalidate selected element, nothing is selected after this!
								actElementName.setText(NA);
							}
								
							
							
						}
					}
					setTargetTreeToBuild();
					syncEditorPage(true);
				}
				
			}
		});
		//----------------------
		
		
		setEditorToActualTarget();
		
		actualSelectedGroup.setClient(actualSelectedGroupCombo);
		actualSelectedGroup.setExpanded(true);
		toolkit.paintBordersFor(actualSelectedGroupCombo);
		
	}

	private void setTargetTreeToBuild() {
		// if (this.buildMani!=null){
		TreeTop.removeAll();
		Target[] targets = buildMani
				.getAllTargets();
		for (int i = 0; i < targets.length; i++) {
			// TreeTop.clearAll(true);

			TreeItem t = new TreeItem(TreeTop, SWT.NONE);
			t.setImage(EMonoPlugin
					.imageDescriptorFromPlugin("icons/target.gif")
					.createImage());
			//Target tgt = buildMani.getTarget(targets[i]);
			Target tgt = targets[i];
			t.setText(tgt.getName());
			t.setExpanded(true);
			// Add the dependencies

			if (SourceTarget.class.isAssignableFrom(tgt.getClass())) {
				SourceTarget stgt = (SourceTarget) tgt;
				IFile[] fls = stgt.getSources();
				if (fls.length > 0) {
					for (int j = 0; j < fls.length; j++) {
						TreeItem thissrc = new TreeItem(t, SWT.NONE);
						thissrc.setImage(EMonoPlugin.imageDescriptorFromPlugin(
								"icons/bluepoint.gif").createImage());
						String shownText = fls[j].getProjectRelativePath()
								.toOSString();
						thissrc.setText(shownText);
					}
				}
			}
		}
		TreeTop.setExpanded(true);
		for (int i = 0; i < TreeTop.getItemCount(); i++) {
			TreeItem ti = TreeTop.getItem(i);
			ti.setExpanded(true);
			for (int j = 0; j < ti.getItemCount(); j++) {
				TreeItem tj = ti.getItem(j);
				tj.setExpanded(true);
			}
		}
	}

	private void setEditorToActualTarget() {
		if (actualTarget != null) {
			actTgtName.setText(actualTarget.getName());
			actTgtLanguage.setText(NO_LANGUAGE_TARGET);
			removeActualElementLnk.setEnabled(true);
			String dependencies = "";
			String[] d = actualTarget.getDependencies();
			for (int i = 0; i < d.length; i++) {
				dependencies += d[i];
				if (i < d.length - 1) {
					dependencies += ",";
				}
			}
			actTgtDependencies.setEnabled(true);
			actTgtDependencies.setText(dependencies);
			// Source target?
			if (SourceTarget.class.isAssignableFrom(actualTarget.getClass())) {
                actTgtLanguage.setText(((SourceTarget)actualTarget).getLanguage());
				actTgtTypeCombo.setEnabled(true);
				// Select the right type
				String tp = ((SourceTarget) actualTarget).getType();
				int toSelect = 0;
				for (int i = 0; i < actTgtTypeCombo.getItemCount(); i++) {
					if (actTgtTypeCombo.getItem(i).equals(tp)) {
						toSelect = i;
					}
				}
				actTgtTypeCombo.select(toSelect);
				// The warning level
				actTgtWlevelCombo.setEnabled(true);
				toSelect = ((SourceTarget) actualTarget).getWarningLevel();
				if (toSelect >= 0 && toSelect <= 4) {
					actTgtWlevelCombo.select(toSelect);
				}

				// The defines
				actTgtDefinitions.setEnabled(true);
				String defines = "";
				String[] allDefs = ((SourceTarget) actualTarget)
						.getDefinitions();
				for (int i = 0; i < allDefs.length; i++) {
					defines += allDefs[i];
					if (i < allDefs.length - 1)
						defines += ",";
				}
				actTgtDefinitions.setText(defines);
				// The references
				actTgtReferences.setEnabled(true);
				String refs = "";
				String[] allRefs = ((SourceTarget) actualTarget)
						.getReferences();
				for (int i = 0; i < allRefs.length; i++) {
					refs += allRefs[i];
					if (i < allRefs.length - 1)
						refs += ",";
				}
				actTgtReferences.setText(refs);
				// Debugging output
				actTgtDebugCheckBox.setEnabled(true);
				actTgtDebugCheckBox.setSelection(((SourceTarget) actualTarget)
						.isDebuggingOutput());
				// Optimization
				actTgtOptimazionCheckBox.setEnabled(true);
				actTgtOptimazionCheckBox
						.setSelection(((SourceTarget) actualTarget)
								.isOptimization());
				// Commands:
				// Add Sources
				addSrcToActualTargetLnk.setEnabled(true);

			} else {
				actTgtTypeCombo.setEnabled(false);
				actTgtTypeCombo.select(4); // "--"
				actTgtWlevelCombo.setEnabled(false);
				actTgtWlevelCombo.select(0);
				actTgtDefinitions.setEnabled(false);
				actTgtDefinitions.setText("");
				actTgtReferences.setEnabled(false);
				actTgtReferences.setText("");
				actTgtDebugCheckBox.setEnabled(false);
				actTgtDebugCheckBox.setSelection(false);
				actTgtOptimazionCheckBox.setEnabled(false);
				actTgtOptimazionCheckBox.setSelection(false);
				addSrcToActualTargetLnk.setEnabled(false);
			}
		} else {
			actTgtName.setText(NA);
			actTgtDependencies.setText("");
			actTgtDependencies.setEnabled(false);
			actTgtTypeCombo.setEnabled(false);
			actTgtTypeCombo.select(4); // "--"
			actTgtWlevelCombo.setEnabled(false);
			actTgtWlevelCombo.select(0);
			actTgtDefinitions.setEnabled(false);
			actTgtDefinitions.setText("");
			actTgtReferences.setEnabled(false);
			actTgtReferences.setText("");
			actTgtDebugCheckBox.setEnabled(false);
			actTgtDebugCheckBox.setSelection(false);
			actTgtOptimazionCheckBox.setEnabled(false);
			actTgtOptimazionCheckBox.setSelection(false);
			removeActualElementLnk.setEnabled(false);
			addSrcToActualTargetLnk.setEnabled(false);
		}
		

	}
	
	
	public void syncEditorPage(){
		syncEditorPage(false);
	}
	
	public void syncEditorPage(boolean forceTreeUpdate) {
		if (buildMani != null && projectName != null && binDir != null
				&& sourceDir != null) {
			if (!buildMani.getBuildProjectName().equals(projectName.getText())) {
				buildMani.setBuildProjectName(projectName.getText());
				setTetEditorToBuildMani();
				
			}
			if (!buildMani.getSrcDir().equals(sourceDir.getText())) {
				buildMani.setSrcDir(sourceDir.getText());
				setTetEditorToBuildMani();
				
			}
			if (!buildMani.getBinDir().equals(binDir.getText())) {
				buildMani.setBinDir(binDir.getText());
				setTetEditorToBuildMani();
				
			}
			// Actual Target changed?
			setActualTargetToEditor();
			// New target added?
			if (forceTreeUpdate){
				setTetEditorToBuildMani();
			}
		}
	}

	public void setEditorToManipulator() {
		if (buildMani != null) {
			if (projectName != null) {
				if (!buildMani.getBuildProjectName().equals(
						projectName.getText())) {
					projectName.setText(buildMani.getBuildProjectName());
				}
			}
			if (sourceDir != null && binDir != null) {
				if (!buildMani.getSrcDir().equals(sourceDir.getText())) {
					sourceDir.setText(buildMani.getSrcDir());
				}
				if (!buildMani.getBinDir().equals(binDir.getText())) {
					binDir.setText(buildMani.getBinDir());
				}
			}
			if (targetTree != null) {
				// Select the first node of the targets
				if (targetTree.getItemCount() > 0) {
					targetTree.setSelection(targetTree.getItem(0));
					// No target to rewrite in the manipulator
					actualTarget = null;
					setActualTargetToString(targetTree.getItem(0).getText());
				}
				setTargetTreeToBuild();
				setEditorToActualTarget();
			}
		}

	}

	void setActualTargetToEditor() {
		if (actualTarget != null) {
			boolean targetChanged = false;
			// The dependencies
			String asText = actTgtDependencies.getText();
			String[] deps = cleanForEmptyStrings(asText.split("[,\\s]"));
			String[] oldDeps = cleanForEmptyStrings((actualTarget
					.getDependencies()));
			// if (!oldDeps.equals(deps)) {
			// targetChanged = true;
			// actualTarget.setDependencies(deps);
			// }
			boolean differs = false;
			if (deps.length != oldDeps.length) {
				differs = true;
			} else {
				for (int i = 0; i < oldDeps.length && !differs; i++) {
					differs = !oldDeps[i].equals(deps[i]);
				}
			}
			if (differs) {
				targetChanged = true;
				actualTarget.setDependencies(deps);
			}
			if (SourceTarget.class.isAssignableFrom(actualTarget.getClass())) {
				// Type?
				int selectedTypeIndex = actTgtTypeCombo.getSelectionIndex();
				if (selectedTypeIndex != -1) {
					String type = actTgtTypeCombo.getItem(selectedTypeIndex);
					if (!((SourceTarget) actualTarget).getType().equals(type)) {
						((SourceTarget) actualTarget).setType(type);

						targetChanged = true;
					}
				}
				// Warning level?
				int selectedWlevel = actTgtWlevelCombo.getSelectionIndex();
				if (selectedWlevel != -1) {
					if (((SourceTarget) actualTarget).getWarningLevel() != selectedWlevel) {
						((SourceTarget) actualTarget)
								.setWarningLevel(selectedWlevel);

						targetChanged = true;
					}
				}
				// Definitions?
				String[] allDefs = cleanForEmptyStrings(actTgtDefinitions
						.getText().split(","));
				String[] oldDefs = cleanForEmptyStrings(((SourceTarget) actualTarget)
						.getDefinitions());
				differs = false;
				if (allDefs.length != oldDefs.length) {
					differs = true;
				} else {
					for (int i = 0; i < oldDefs.length && !differs; i++) {
						differs = !oldDefs[i].equals(allDefs[i]);
					}
				}
				if (differs) {
					((SourceTarget) actualTarget).setDefinitions(allDefs);

					targetChanged = true;
				}

				// References?
				String[] allRefs = cleanForEmptyStrings(actTgtReferences
						.getText().split(","));
				String[] oldRefs = cleanForEmptyStrings(((SourceTarget) actualTarget)
						.getReferences());
				differs = false;
				if (allRefs.length != oldRefs.length) {
					differs = true;
				} else {
					for (int i = 0; i < oldRefs.length && !differs; i++) {
						differs = !oldRefs[i].equals(allRefs[i]);
					}
				}
				if (differs) {
					((SourceTarget) actualTarget).setReferences(allRefs);

					targetChanged = true;
				}
				// Debugging output?
				boolean dout = actTgtDebugCheckBox.getSelection();
				boolean oldDout = ((SourceTarget) actualTarget)
						.isDebuggingOutput();
				if (dout != oldDout) {
					((SourceTarget) actualTarget).setDebuggingOutput(dout);
					targetChanged = true;
				}
				// Optimization?
				boolean opt = actTgtOptimazionCheckBox.getSelection();
				boolean oldOpt = ((SourceTarget) actualTarget).isOptimization();
				if (opt != oldOpt) {
					((SourceTarget) actualTarget).setOptimization(opt);
					targetChanged = true;
				}
			}

			if (targetChanged) {
				buildMani.rewriteTarget(actualTarget);
				setTetEditorToBuildMani();
			}
		}
	}

	/**
	 * Eliminate all empty strings from a string array
	 * @param orginal
	 * @return
	 */
	private String[] cleanForEmptyStrings(String[] orginal) {
		int nrOfNnEmpty = 0;
		for (int j=0; j < orginal.length; j++){
			if (!orginal[j].equals("")) {
				nrOfNnEmpty++;
			}
		}
		String[] result = new String[nrOfNnEmpty];
		int act =0;
		for (int j =0; j < nrOfNnEmpty; j++){
			// Spool the empty
			while (act < orginal.length && orginal[act].equals("")){
				act++;
			}
			if (! orginal[act].equals("")){
				result[j]=orginal[act];
				act++;
			}
		}
		return result;
		
	}

	void setActualTargetToString(String targetName) {

		if (actualTarget != null) {
			setActualTargetToEditor();
			// buildMani.rewriteTarget(actualTarget);
			String oldtgtN = actualTarget.getName();
			Target tgt = buildMani.getTarget(targetName);
			if (!oldtgtN.equals(tgt.getName())) {
				actualTarget = buildMani.getTarget(targetName);
				setEditorToActualTarget();
			}

		} else {
			actualTarget = buildMani.getTarget(targetName);
			setEditorToActualTarget();
		}
	}

	class TreeSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {
			if (TreeItem.class.isAssignableFrom(e.item.getClass())) {

				TreeItem actTreeItem = (TreeItem) e.item;
				String targetName = getTargetName(actTreeItem);
				setActualTargetToString(targetName);
				String selectedName=getSelectedName(actTreeItem);
				actElementName.setText(selectedName);
			}

		}

		private String getSelectedName(TreeItem actTreeItem) {
			return actTreeItem.getText();
		}

		private String getTargetName(TreeItem actTreeItem) {
			if (actTreeItem.getParent() == null || actTreeItem == null) {
				return "NA";
			}
			if (actTreeItem.getParentItem() == null
					|| actTreeItem.getParentItem().equals(TreeTop)) {
				return actTreeItem.getText();
			}
			return getTargetName(actTreeItem.getParentItem());
		}

	}

//	public boolean isDirty(){
//		return getChanged();
//	}
//	/**
//	 * @return the changed
//	 */
//	public boolean getChanged() {
//		return changed;
//	}

	/**
	 * @param changed
	 *            the changed to set
	 */
	public void setTetEditorToBuildMani() {
			// Sync with the text editor
			BuildTextEditorPage txtEditor = editor.getTextEditor();
			IDocument editorDoc=txtEditor.getDocument();
			IDocument actDoc=buildMani.getAsDocument();
			if (editorDoc != null && ! editorDoc.equals(actDoc)){
				// Set the editor to the new doc
				txtEditor.setUndoPoint();
				editorDoc.set(actDoc.get());
				txtEditor.selectAndReveal(0, actDoc.getLength());
			}

		//this.changed = changed;
	}

	/**
	 * @return the buildMani
	 */
	public BuildMechanismManipulator getBuildMani() {
		return buildMani;
	}

	/**
	 * @param buildMani
	 *            the buildMani to set
	 */
	public void setBuildMani(BuildMechanismManipulator buildMani) {
		this.buildMani = buildMani;
	}

	// Internal class: FocusListener which syncs with the text editor
	class syncService implements FocusListener {
		private BuildSymbolicEditorPage page;

		public syncService(BuildSymbolicEditorPage page) {
			this.page = page;
		}

		public void focusGained(FocusEvent e) {
			// Nothing to do there

		}

		public void focusLost(FocusEvent e) {
			page.syncEditorPage();

		}

	}

	class syncKeyListener implements KeyListener {

		private BuildSymbolicEditorPage page;

		public syncKeyListener(BuildSymbolicEditorPage page) {
			this.page = page;
		}

		public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
			
		}

		public void keyReleased(org.eclipse.swt.events.KeyEvent e) {
			page.syncEditorPage();
		}

		

	}	
}
	
	
	

