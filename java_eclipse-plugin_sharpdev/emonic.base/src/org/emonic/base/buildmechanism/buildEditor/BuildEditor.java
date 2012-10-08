package org.emonic.base.buildmechanism.buildEditor;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;


public class BuildEditor extends FormEditor implements IResourceChangeListener{

	/** The text editor used in page 0. */
	private BuildTextEditorPage textEditorPage;
	private BuildMechanismManipulator buildMani;
	private BuildSymbolicEditorPage symbolEditorPage;
	//private  BuildEditorPage buildPage;

	public BuildEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

	}

	public BuildTextEditorPage getTextEditor(){
		if (textEditorPage != null ){
			return textEditorPage;
		}
		return null;
	}


	/**
	 * Creates page 1 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage1() {
		try {
			if (textEditorPage == null) textEditorPage = new BuildTextEditorPage();
			int index = addPage(textEditorPage, getEditorInput());


			// Get the project of the input of this editor
			IEditorInput input = this.textEditorPage.getEditorInput();
			IFile editorFile = (IFile) input.getAdapter(IFile.class);
			setPageText(index, textEditorPage.getTitle());
			IProject proj = editorFile.getProject();
			// Get the BuildMechMani
			buildMani=BuildDescriptionFactory.getBuildMechanismManipulator(proj);

		} catch (PartInitException e) {
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating nested text editor",
					null,
					e.getStatus());
		}
	}


	void createPage0() {
		symbolEditorPage = new BuildSymbolicEditorPage(this);	
		int index;
		try {
			index = addPage(symbolEditorPage);
			setPageText(index, "Properties");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createInternalPages() {

		createPage0();
		createPage1();
		IDocument doc = this.textEditorPage.getDocument();
		if (doc != null) buildMani.initFromDocument(doc);
		symbolEditorPage.setBuildMani(buildMani);

	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		//		symbolEditorPage.updateManipulator();
		//		if (buildMani.isDocumentSyncronisator()){
		//			   IDocument content = buildMani.getAsDocument();
		//			   this.textEditorPage.getDocumentProvider().getDocument(textEditorPage).set(content.get());
		//	    }
		textEditorPage.doSave(monitor);
		setInput(textEditorPage.getEditorInput());
	}
	
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		//IEditorPart editor = getEditor(0);

		// Sync with page 0
		//		symbolEditorPage.updateManipulator();
		//		if (buildMani.isDocumentSyncronisator()){
		//			   IDocument content = buildMani.getAsDocument();
		//			   this.textEditorPage.getDocumentProvider().getDocument(textEditorPage).set(content.get());
		//	    }
		textEditorPage.doSaveAs();
		setPageText(1, textEditorPage.getTitle());
		setInput(textEditorPage.getEditorInput());
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		String filename = ((IFile) editorInput.getAdapter(IFile.class)).getName();
		//String extension = ((IFile) editorInput.getAdapter(IFile.class)).getFileExtension();
		//if (extension != null) filename = filename + "." + extension;
		this.setPartName(filename);
		super.init(site, editorInput);
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	/**
	 * Calculates the contents of the other page when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		try{
			super.pageChange(newPageIndex);
		} catch(Exception e){
			e.printStackTrace();
		}
		// Sync the pages
		if (newPageIndex == 1){
			symbolEditorPage.syncEditorPage();

		} else {
			buildMani.initFromDocument(textEditorPage.getDocument());
			symbolEditorPage.setEditorToManipulator();
		}
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)textEditorPage.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(textEditorPage.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}

	protected void addPages() {
		createInternalPages();
	}

}
