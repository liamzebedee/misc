package org.emonic.base.buildmechanism.buildEditor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;



public class AddTargetDialog extends Dialog {

	private Text targetNameText;
	private Combo tgtTypeCombo;
    private Target resultTarget = null;
	private Button linkWithDefaultButton;
	private boolean linkWithDefault=true;
	

	protected AddTargetDialog(Shell parentShell) {
		super(parentShell);
		
		
		this.setBlockOnOpen(true);
		
	}

	
	 protected Control createDialogArea(Composite parent) {
		 Composite composite = new Composite(parent, SWT.NULL);
		 GridLayout layout = new GridLayout(2, false);
		 composite.setLayout(layout);
		 composite.setLayoutData(new GridData(GridData.FILL_BOTH)); 
		 Label la = new Label(composite,SWT.FILL);
		 la.setText("Targetname:");
		 GridData gd = new GridData();
		 gd.widthHint = 150;
		 targetNameText= new Text(composite,SWT.FILL);
		 targetNameText.setLayoutData(gd);
		 Label lb = new Label(composite,SWT.FILL);
		 lb.setText("Targettype:");
		 tgtTypeCombo = new Combo(composite, SWT.READ_ONLY
					| SWT.BORDER);
		 gd = new GridData();
		 gd.widthHint = 150;
		 tgtTypeCombo.setLayoutData(gd);
	     tgtTypeCombo.setItems(new String[] { "No sources", "CSharp" });
		 tgtTypeCombo.select(0);
		 linkWithDefaultButton=new Button(composite,SWT.CHECK);
		 linkWithDefaultButton.setText("Add dependency in default target");
		 linkWithDefaultButton.setSelection(true);
		 return composite;
	 }
    
	

	 protected void okPressed() { 
		 if (tgtTypeCombo.getSelectionIndex()==0){
			 resultTarget=new Target();
			 resultTarget.setName(targetNameText.getText());
		 } else {
			resultTarget=new SourceTarget();
			resultTarget.setName(targetNameText.getText());
			String language ="";
			// Add here the different possible languages!
			if (tgtTypeCombo.getSelectionIndex()==1) language=IBuildMechanismDescriptor.CSHARP_LANGUAGE;
			((SourceTarget)resultTarget).setLanguage(language);
		 }
		 linkWithDefault=linkWithDefaultButton.getSelection();
		 super.okPressed();
	 }
	 
	 public Target getResultTarget(){
		 return this.resultTarget;
	 }
	 
	 public boolean setDependencyToDefault(){
		 return linkWithDefault;
	 }


}
