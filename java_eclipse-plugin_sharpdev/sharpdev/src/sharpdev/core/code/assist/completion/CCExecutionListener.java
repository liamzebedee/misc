package sharpdev.core.code.assist.completion;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.emonic.base.codecompletion.CCAdministration;
import org.emonic.base.codecompletion.SourceExtractor;

import sharpdev.util.UIHelper;

/**
 * Triggers creations/saves of projects/files to the code completion mechanism BEFORE command executes
 */
public class CCExecutionListener implements IExecutionListener {
	
	// We only reprocess files if they have changed
	private final String FILE_SAVED = "org.eclipse.ui.file.save";
	private final String FILE_CREATED = "org.eclipse.ui.newWizard";
	// org.eclipse.ui.file.refresh
	// org.eclipse.ui.file.revert
	// org.eclipse.ui.file.import
	
	@Override
	public void notHandled(String commandId, NotHandledException exception) {}

	@Override
	public void postExecuteFailure(String commandId,
			ExecutionException exception) {}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {}
	
	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		IEditorPart editorPart = UIHelper.getActiveEditor(event);
		
		if(editorPart != null) {
			IEditorInput editorInput = editorPart.getEditorInput();
			IResource resource = (IResource) editorInput.getAdapter(IResource.class);
			if(event.getCommand() != null) {
				String eventCommand = event.getCommand().getId();
				
				// File Saved
				if(eventCommand.equals(FILE_SAVED)) {
					IFile file = (IFile) resource;
					fileSaved(file, file.getProject());
				}
				
				// File Created
				if(eventCommand.equals(FILE_CREATED)) {
					IFile file = (IFile) resource;
					fileCreated(file, file.getProject());
				}
				
				//case create file
				/*else if(event_command.equals(FILE_CREATED)){
					IFile file = (IFile) resource;
					fileCreated(file, file.getProject());						
				}*/
				//case open project || Case create project
				/*else if(event_command.equals(PROJECT_OPENED) || 
						 event_command.equals(PROJECT_CREATED)){
					IProject project = (IProject) resource;
					projectCreatedOrOpened(project);
				}*/			
				//case save project preferences
				/*else if()
				{

				}*/
			}
		}

	}
	
	private static void fileSaved(IFile file, IProject project) {
		// Check if file is shallow or deep
		// Extract source from file into source model
		
		//check if file is shallow or deep
		boolean isDeep = CCAdministration.getInstance().getProjectTypeLoadedMap(project).containsValue(project.getFullPath().toPortableString());
		SourceExtractor srcEx = new SourceExtractor(project);
		srcEx.extractFile(new File(file.getLocation().toPortableString()), isDeep);
	}
	
	private static void fileCreated(IFile file, IProject project) {
		// Extract source from file into source model
	}
}
