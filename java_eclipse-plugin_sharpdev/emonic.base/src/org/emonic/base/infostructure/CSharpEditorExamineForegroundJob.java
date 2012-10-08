/*
 * Created on 19.11.2007
 * emonic.base org.emonic.base.infostructure CSharpEditorExamineForegroundJob.java
 */
package org.emonic.base.infostructure;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.emonic.base.codehierarchy.CodeElement;

/**
 * @author bb
 *
 */
public class CSharpEditorExamineForegroundJob extends UIJob {
    ArrayList registeredTasks;
    CodeElement root;
    static boolean alreadyRunning = false;

	public CSharpEditorExamineForegroundJob(ArrayList registeredTasks, CodeElement root) {
		super("SurfaceUpdater");
		this.registeredTasks=registeredTasks;
		this.root=root;
		this.setSystem(true);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus runInUIThread(IProgressMonitor monitor) {
		if (! alreadyRunning){
			try{
				alreadyRunning=true;
				for (int count = 0; count < registeredTasks.size(); count++){
					if (monitor.isCanceled()) {
						// That can happen and should not lead to a error
						//	throw new OperationCanceledException();
						alreadyRunning=false;
						return Status.CANCEL_STATUS;
					} else {
						SimpleTaskInterface task = (SimpleTaskInterface) registeredTasks.get(count);
						task.runTask(root);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		alreadyRunning=false;	
		return Status.OK_STATUS;
	}

}
