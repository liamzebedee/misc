package org.emonic.base.natures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.emonic.base.Constants;

/**
 * @author bb
 *
 */
public class EMonoNature implements IProjectNature {
	
	private IProject project;

	public IProject getProject() {
		return project;
	}
	
	public void deconfigure() throws CoreException {
		IProjectDescription desc = getProject().getDescription();
		ICommand[] cmds = desc.getBuildSpec();
		List<ICommand> commands = new ArrayList<ICommand>(cmds.length);
		for (int i=0; i < cmds.length; i++) {
			if (!cmds[i].getBuilderName().equals(Constants.EmonicBuilderId)) {
				commands.add(cmds[i]);
			}
		}
		
		if (commands.size() != cmds.length) {		
			desc.setBuildSpec((ICommand[]) commands.toArray(new ICommand[commands.size()]));
			getProject().setDescription(desc, null);
		}
	}
	
	public void setProject(IProject theProject) {
		project = theProject;
	}
	
	public void configure() throws CoreException {
		IProjectDescription desc = getProject().getDescription();
		ICommand[] cmds = desc.getBuildSpec();
		for (int i=0; i < cmds.length; i++ ){
			if (cmds[i].getBuilderName().equals(Constants.EmonicBuilderId)){
				return;
			}
		}
		ICommand cmd = desc.newCommand();
		cmd.setBuilderName(Constants.EmonicBuilderId);
		ICommand[] newCmds = new ICommand[cmds.length + 1];
		System.arraycopy(cmds,0,newCmds,0,cmds.length);
		newCmds[newCmds.length-1]=cmd;
		desc.setBuildSpec(newCmds);
		getProject().setDescription(desc,null);
	}
}