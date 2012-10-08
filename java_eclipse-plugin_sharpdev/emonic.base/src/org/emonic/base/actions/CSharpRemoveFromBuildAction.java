package org.emonic.base.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.helpers.DebugUtil;

//import com.sun.java.util.jar.pack.Package.Class.Method;

public class CSharpRemoveFromBuildAction implements IEditorActionDelegate,IWorkbenchWindowActionDelegate,IViewActionDelegate,IObjectActionDelegate {
	private static final String MNOTARGET = "No targets found to remove this file from!"; //$NON-NLS-1$
	private static final String MWARNING = "Warning!"; //$NON-NLS-1$
	private static final String MNOTABLETOWORK = "The choosen build mechanism is not able to do the operation!";
	private Shell shell;
	private IFile editorfile;
	/**
	 * The constructor.
	 */
	public CSharpRemoveFromBuildAction() {
	}

    boolean debugit = false; 
	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		try{
			IFile fl=editorfile;
			//System.out.println(fl.getFullPath());
			BuildMechanismManipulator bfm= BuildDescriptionFactory.getBuildMechanismManipulator(fl.getProject());
			// bfm able to handle sources in targets?
			if (bfm.isSourceTargetManipulator()){
				//IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) bfm;
				if (shell != null){
					// Do we have realy a target to remove the file from?
					String[] posTargets = bfm.getAllTargetNamesOfFile(fl);
					if (posTargets == null || posTargets.length == 0){
						MessageBox m = new MessageBox(shell);
						m.setText(MWARNING);
						m.setMessage(MNOTARGET);
						m.open();
					} else {
						RemoveSrcFromBuildDialog dialog = new RemoveSrcFromBuildDialog(
								shell, fl);
						dialog.setFileText(fl.getFullPath().toOSString());
						dialog.setBuildMechanismManipulator(bfm);
						dialog.open();
					}	
				} else {
					DebugUtil.DebugPrint("Action called!",debugit);
				}
			} else {
				MessageBox m = new MessageBox(shell);
				m.setText(MWARNING);
				m.setMessage(MNOTABLETOWORK);
				m.open();
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IFile) {
				editorfile = (IFile) element;
			} else if (element instanceof IAdaptable) {
				editorfile = (IFile) ((IAdaptable) element).getAdapter(IFile.class);
			}
		}
		
		action.setEnabled(editorfile != null);
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		if (window != null) {
			shell=window.getShell();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor!=null){
			shell=targetEditor.getSite().getShell();
		    // Where to get the associated fle name?
			IEditorInput input = targetEditor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				editorfile = ((IFileEditorInput) input).getFile();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		shell = view.getSite().getShell();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell=targetPart.getSite().getShell();
	}
}