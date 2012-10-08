/*
 * Created on Feb 4, 2006
 * emonic.base org.emonic.base.actions AddSrcToBuildDialog.java
 */
package org.emonic.base.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;
/**
 * @author bb
 *
 */
public class RemoveSrcFromBuildDialog extends Dialog {

	private static final String LABELMSGSELECTTARGET = "Select targets from which to remove the file from:"; //$NON-NLS-1$
	private static final String UNKNOWNFILE = "Unknown file!"; //$NON-NLS-1$
	private static final String FILENAME = "File:"; //$NON-NLS-1$
	private static final String BUILDFILENAME = "Build file:"; //$NON-NLS-1$
	private static final String TARGET = "target"; //$NON-NLS-1$
	private static final String SHELL_TITLE = "Remove file from build file"; //$NON-NLS-1$

	/**
	 * @param parentShell
	 * @param fl
	 */
	public RemoveSrcFromBuildDialog(Shell parentShell, IFile fl) {
		super(parentShell);
		targetName=TARGET;
	}

	private Text fileText;
	private IFile buildFile;
	private List targetList;
	private String  targetName;
	private BuildMechanismManipulator bfm;
	private String _fileText;
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(SHELL_TITLE);
	}
	
   protected Control createDialogArea(Composite parent) {
	  Composite container = (Composite)super.createDialogArea(parent);
	  GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		Label label = new Label(container, SWT.LEAD);
		label.setText(FILENAME);
	
		fileText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		if (_fileText == null) {
			_fileText=UNKNOWNFILE;
		}
		fileText.setText(_fileText);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		label = new Label(container, SWT.LEAD);
		label.setText(BUILDFILENAME);
		
		Text text = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
//		if (IBuildFileManipulator.class.isAssignableFrom(bfm.getClass())){
//			text.setText(((IBuildFileManipulator)bfm).getBuildFile().getFullPath().toOSString());
//			text.setEnabled(true);
//		} else {
//			text.setEnabled(false);
//		}
		buildExistingTargetGroup(container);
		
	    // All set -> So initialize and run it
		initialize();
	  
	  return container;
   }
	   
	private void buildExistingTargetGroup(Composite container) {
		Label label = new Label(container, SWT.LEAD);
		label.setText(LABELMSGSELECTTARGET);
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		targetList = new List(container, SWT.MULTI | SWT.BORDER);
		targetList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}
	
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		if (point.y < 250) {
			point.y = 250;
		}
		return point;
	}

	private void initialize() {
		if (bfm.isSourceTargetManipulator()){
			//IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) bfm;
			String[] targetListE = bfm.getAllTargetNamesOfFile(getFileAsIFile());
			if (targetListE.length != 0) {
				targetList.setItems(targetListE);
				targetList.select(0);
			}
		}		
	}
	
	
	 private IFile getFileAsIFile() {
		 IPath path = new Path(fileText.getText());
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
					.getRoot();
		return root.getFileForLocation(path);
	}

	
	
	public String getFileName() {
		return fileText.getText();
	}
	
//	public String getFileNameOnly() {
//		File f = new File(fileText.getText());
//		return f.getName();
//	}
//	
	public String getTargetName(){
		return targetName;
	}

	/**
	 * @return Returns the buildFile.
	 */
	public IFile getBuildFile() {
		return buildFile;
	}
	
	public void setFileText(String fn){
		_fileText =fn;
	}
	
	public void setBuildMechanismManipulator(BuildMechanismManipulator bfm){
		this.bfm= bfm;
		
	}
	
	protected void okPressed(){
		// Get the selected target names
		if (bfm.isSourceTargetManipulator()){
			//IXBuildSourceTargetManipulator targetmani = (IBuildSourceTargetManipulator) bfm;
			String[] targets = targetList.getSelection();
			for (int i = 0; i < targets.length; i++){
			   //bfm.removeSrcFromTarget( targets[i],getFileNameOnly());
				Target st = bfm.getTarget(targets[i]);
				if (SourceTarget.class.isAssignableFrom(st.getClass())){
					SourceTarget st1 = (SourceTarget)st;
					IFile[] fls = st1.getSources();
					ArrayList newSrc=new ArrayList();
					IFile actF = getFileAsIFile();
					for (int j =0; j < fls.length;j++){
						if (!fls[i].equals(actF)){
							newSrc.add(fls[i]);
						}
					}
					IFile[] ns = new IFile[newSrc.size()];
					ns = (IFile[]) newSrc.toArray(ns);
					st1.setSources(ns);
					bfm.rewriteTarget(st);
					bfm.save();
				}
			}
			super.okPressed();
		}
	}
	
}
