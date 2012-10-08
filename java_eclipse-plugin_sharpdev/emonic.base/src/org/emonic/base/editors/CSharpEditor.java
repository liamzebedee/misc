/*******************************************************************************
 * Copyright (c) 2001, 2008 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************
 * Parts of the code are derivrd from plugins standing under the CPL-license 
 * (improve c#-plugin http://www.improve-technologies.com/alpha/esharp,
 * epic-plugin https://sourceforge.net/projects/e-p-i-c/). You can get the code
 * under the original license from their homepage.
 *******************************************************************************
 * Contributors:
 *   Bernhard Brem - Initial implementation
 *   Harald Krapfenbauer, TU Vienna - added property change listener that listens on preference store
 *       changes
 *     added chained preference store to keep standard editor colors
 *     implemented affectsTextPresentation()
 *     added creation and setup of bracket inserter
 *     added document listener that triggers the examine job
 *********************************************************************************/

package org.emonic.base.editors;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.actions.DefaultCSharpSourceAction;
import org.emonic.base.actions.DefaultCSharpSourceDelegate;
import org.emonic.base.infostructure.CSharpEditorExamineJob;
import org.emonic.base.preferences.DefaultPrefsSetter;
import org.emonic.base.views.EMonoOutlinePage;


/**
 * This class represents the CSharpEditor
 */
public class CSharpEditor extends TextEditor {

	private ColorManager colorManager;
	private EMonoCSharpFolder fFolder;
	private ISourceViewer sourceViewer;
	private IDocument document;
	//private IdleTimer idleTimer;
	private ProjectionSupport projectionSupport;
	private EMonoCSharpConfiguration configuration;
	protected  EMonoOutlinePage page;
	protected CSharpEditorExamineJob examinJob;
	private CSharpBracketInserter bracketInserter;
	protected CSharpBracketMatcher bracketMatcher;// = new CSharpBracketMatcher();
	private TaskTracker taskTracker;
	private ArrayList<String> menuActionName;
	private ArrayList<String> menuActionSection;

	public static final String EMONIC_CSHARP_EDITOR_ID = "org.emonic.base.editors.CSharpEditor"; //$NON-NLS-1$

	/**
	 * The property change listener gets notified if the preferences for this plug-in change
	 */
	private final IPropertyChangeListener prefChangeListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(DefaultPrefsSetter.QUOTESCOMPLETION) ||
					event.getProperty().equals(DefaultPrefsSetter.BRACESCOMPLETION) ||
					event.getProperty().equals(DefaultPrefsSetter.BRACKETCOMPLETION)) {

				// update completions of bracket inserter
				bracketInserter.setCloseBracketsEnabled(getPreferenceStore().getBoolean(
						DefaultPrefsSetter.BRACKETCOMPLETION));
				bracketInserter.setCloseStringsEnabled(getPreferenceStore().getBoolean(
						DefaultPrefsSetter.QUOTESCOMPLETION));
				// get the CSharpAutoIndentStrategy via our source viewer configuration and
				// call its setBracesCompletion() method
				((CSharpAutoIndentStrategy)configuration.getAutoEditStrategies(
						sourceViewer,IDocument.DEFAULT_CONTENT_TYPE)[0]).setBracesCompletion( 
								EMonoPlugin.getDefault().getPreferenceStore().getBoolean(DefaultPrefsSetter.BRACESCOMPLETION)
								);	
			}
		}
	};


	/**
	 * Constructor
	 */
	public CSharpEditor() {
		super();
		examinJob = new CSharpEditorExamineJob(this,"ExamineEditor");
		bracketMatcher=new CSharpBracketMatcher(examinJob);
		colorManager = new ColorManager();
		configuration = new EMonoCSharpConfiguration(colorManager,this);
		setSourceViewerConfiguration(configuration);
		setDocumentProvider(new EMonoCSharpDocumentProvider());
		setKeyBindingScopes(new String[] { "org.emonic.editors.csharpEditorScope" });
		// get the general preference store for editors (which has colors for annotation types etc.)
		IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
		// make a ChainPreferenceStore which has general settings and this plug-in's specific settings
		ChainedPreferenceStore preferenceStore = new ChainedPreferenceStore(new IPreferenceStore[] {EMonoPlugin.getDefault().getPreferenceStore(), generalTextStore});
		// set this preference store as our editor's one, so that affectedTextPresentation will be called on
		// preference change
		setPreferenceStore(preferenceStore);

		taskTracker = new TaskTracker();
	}


	/**
	 * Is called on changes in the preferences. Triggers update of editor colors
	 * on changes in syntax coloring.
	 * @param event The event containing the preference changes
	 */
	protected boolean affectsTextPresentation(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(DefaultPrefsSetter.STRINGCOLOR) ||
				event.getProperty().equals(DefaultPrefsSetter.STRINGBOLD) ||
				event.getProperty().equals(DefaultPrefsSetter.DOCBOLD) ||
				event.getProperty().equals(DefaultPrefsSetter.DOCCOLOR) ||
				event.getProperty().equals(DefaultPrefsSetter.COMMENTBOLD) ||
				event.getProperty().equals(DefaultPrefsSetter.COMMENTCOLOR) ||
				event.getProperty().equals(DefaultPrefsSetter.KEYWORDBOLD) ||
				event.getProperty().equals(DefaultPrefsSetter.KEYWORDCOLOR)) {

			// this will update the tokens of the scanners - must be done before
			configuration.updateScannerColors();
			// this will kick the scanner
			return true;
		}
		return false;
	}


	/**
	 * @see org.eclipse.ui.editors.text.TextEditor#dispose()
	 */
	public void dispose() {
		if (colorManager != null) colorManager.dispose();
		if (examinJob != null) examinJob.cancel();
		String[] actionIds = DefaultCSharpSourceDelegate.getEditorActions();
		for (int i = 0; i < actionIds.length; i++) {
			IAction action = getAction(actionIds[i]);
			if (action instanceof DefaultCSharpSourceAction)
				((DefaultCSharpSourceAction) action).dispose();
		}
		if (sourceViewer instanceof ITextViewerExtension)
			((ITextViewerExtension) sourceViewer).removeVerifyKeyListener(bracketInserter);
		// remove property listener of preference store
		getPreferenceStore().removePropertyChangeListener(prefChangeListener);
		super.dispose();
	}


	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#rulerContextMenuAboutToShow(IMenuManager)
	 */
	public void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		// We dont use it quite now, but may be we add some ations to the ruler menu some time

		//		ViewerActionBuilder builder = new ViewerActionBuilder();
		//		builder.readViewerContributions("#CSharpRulerContext",
		//				getSelectionProvider(), this);
		//		builder.contribute(menu, null, true);
	}


	/**
	 * Returns the ExamineJob that is responsible for various jobs (scanning, parsing, ...)
	 * @return The CSharpEditorExamineJob instance of this editor
	 */
	public CSharpEditorExamineJob getExamineJob(){
		return examinJob;
	}

	// Force a run of the examine job
	public void forceRunExamineJob(){
		examinJob.run(new NullProgressMonitor());
	}
	
	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
	 */
	protected void createActions() {
		super.createActions();
		IDocumentProvider provider = getDocumentProvider();
		document = provider.getDocument(getEditorInput());
		sourceViewer = getSourceViewer();
		sourceViewer.setDocument(document);
		sourceViewer.setAnnotationHover(new MarkerAnnotationHover(this));
		// set up the Folding
		if (fFolder == null) {
			fFolder = new EMonoCSharpFolder(this,document);
			examinJob.registerForCodeElement(fFolder);
		}
		wireAction("Add to build", 
				DefaultCSharpSourceDelegate.ADD_TO_BUILD_COMMAND,
				DefaultCSharpSourceDelegate.ADD_TO_BUILD_ACTION,
				"additions");
		wireAction("Remove from build", 
				DefaultCSharpSourceDelegate.REMOVE_FROM_BUILD_COMMAND,
				DefaultCSharpSourceDelegate.REMOVE_FROM_BUILD_ACTION,
				"additions");
		wireAction("Quick Outline",
				DefaultCSharpSourceDelegate.QUICK_OUTLINE_COMMAND,
				DefaultCSharpSourceDelegate.QUICK_OUTLINE_ACTION,
				"additions");

		wireAction("Open Declaration", 
				DefaultCSharpSourceDelegate.OPEN_DECLARATION_COMMAND,
				DefaultCSharpSourceDelegate.OPEN_DECLARATION_ACTION,
				"additions");
		wireAction("Toggle Comment",
				DefaultCSharpSourceDelegate.TOGGLE_COMMENT_COMMAND,
				DefaultCSharpSourceDelegate.TOGGLE_COMMENT_ACTION,
				"additions");
		wireAction("Correct Intendation", 
				DefaultCSharpSourceDelegate.CORRECT_INDENTATION_COMMAND,
				DefaultCSharpSourceDelegate.CORRECT_INDENTATION_ACTION,
				"additions");

		wireAction("Search Declaration", 
				DefaultCSharpSourceDelegate.SEARCH_DECLARATION_COMMAND,
				DefaultCSharpSourceDelegate.SEARCH_DECLARATION_ACTION
				,"additions");
		wireAction("Search References", 
				DefaultCSharpSourceDelegate.SEARCH_REFERENCES_COMMAND,
				DefaultCSharpSourceDelegate.SEARCH_REFERENCES_ACTION,
				"additions");
		wireAction("ContentAssistProposal.",
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS,
				"org.emonic.base.CSharpEditor.ContentAssist",
				"");



		// create BracketInserter; do it here because we need the sourceViewer
		bracketInserter = new CSharpBracketInserter(this, sourceViewer);
		bracketInserter.setCloseBracketsEnabled(getPreferenceStore().getBoolean(DefaultPrefsSetter.BRACKETCOMPLETION));
		bracketInserter.setCloseStringsEnabled(getPreferenceStore().getBoolean(DefaultPrefsSetter.QUOTESCOMPLETION));
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#editorContextMenuAboutToShow(IMenuManager)
	 */
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		Separator s = new Separator();
		menu.appendToGroup("additions", s);
		if (menuActionName != null && menuActionSection != null) {
			for (int i = 0; i < menuActionName.size(); i++) {
				addAction(menu, (String) menuActionSection.get(i),
						(String) menuActionName.get(i));
			}
		}
	}

	/**
	 * Sets up an action.
	 * @param description The description
	 * @param commandId The command ID
	 * @param CSharpActionId The action ID of this plug-in
	 * @param menuGroup The menu group of the context menu. If "" then not added to menu.
	 */
	private void wireAction(String description, String commandId, String CSharpActionId, String menuGroup)
	{
		DefaultCSharpSourceAction action = new DefaultCSharpSourceAction(this,commandId,description);
		action.setActionDefinitionId(commandId);
		setAction(CSharpActionId, action);
		action.setEnabled(true);
		if (menuActionName==null){
			menuActionName=new ArrayList<String>();
		}
		if (menuActionSection==null){
			menuActionSection=new ArrayList<String>();
		}
		if (! menuGroup.equals("")){
			menuActionName.add(CSharpActionId);
			menuActionSection.add(menuGroup);
		}
	}

	/**
	 * @see org.eclipse.ui.editors.text.TextEditor#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {

		if (ProjectionAnnotationModel.class.equals(adapter)) {
			if (projectionSupport != null) {
				// forward request
				Object result = projectionSupport.getAdapter(getSourceViewer(), adapter);
				if (result != null) {
					return result;
				}
			}
		}

		// Adapter for the Outline view
		if (adapter.equals(IContentOutlinePage.class)) {
			if (page == null)
				page = new EMonoOutlinePage(this, getSourceViewer());
			//this.registerIdleListener(page);
			//page.addSelectionChangedListener(this);
			return page;
		}
		return super.getAdapter(adapter);
	}


	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {

		super.createPartControl(parent);
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		projectionSupport = new ProjectionSupport(viewer,
				getAnnotationAccess(), getSharedColors());
		projectionSupport
		.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error");
		projectionSupport
		.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning");
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);

		// Verify key listener for Bracket Inserter
		if (sourceViewer instanceof ITextViewerExtension) {
			((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(bracketInserter);
		}

		// listen for property changes
		getPreferenceStore().addPropertyChangeListener(prefChangeListener);
	}


	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(Composite, IVerticalRuler, int)
	 */
	protected final ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		//	fAnnotationAccess = createAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());
		ISourceViewer sourceViewer = new CSharpSourceViewer(parent, ruler,
				fOverviewRuler, isOverviewRulerVisible(), styles);
		// ensure source viewer decoration support has been created and
		// configured
		getSourceViewerDecorationSupport(sourceViewer);
		return sourceViewer;
	}


	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(bracketMatcher);
		support.setMatchingCharacterPainterPreferenceKeys(DefaultPrefsSetter.BRACKETMATCHING, DefaultPrefsSetter.BRACKETMATCHINGCOLOR);
		super.configureSourceViewerDecorationSupport(support);
	}

	protected void editorSaved(){
		super.editorSaved();
		IResource resource = (IResource) getEditorInput().getAdapter(IResource.class);
		if (resource != null) {
			taskTracker.checkForTasks(document, resource);
		}
	}


	/**
	 * Returns the source viewer
	 * @return the source viewer
	 */
	public ISourceViewer getViewer(){
		return sourceViewer;
	}

	public String getWordAtCursorpos(){
		char[] dels = {' ', '\n','\t', ';', '+','-','*','/','{','}','=','[',']','(',')'};
		Point p = getViewer().getSelectedRange();
		IDocument doc = getViewer().getDocument();
		String  toSearch = "";
		//int PosInSearch = 0;
		// Region marked?
		if (p.y != 0){
			try {
				toSearch=doc.get(p.x,p.y);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else {
			// If we are at the end of a word, jump in it
			boolean found = false;
			int start1=p.x;
			for (int i = 0; (i< dels.length && !found); i++){
				try {
					char c = doc.getChar(start1);
					found = (dels[i] == c);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			if (found){
				start1--;
			}
			// If we are surrounded from delimiters, return at once
			// If there was one before us, we already stepped back one char
			// So we are surrounded if there is still one before us.
			found=false;
			for (int i = 0; (i< dels.length && !found); i++){
				try {
					char c = doc.getChar(start1);
					found = (dels[i] == c);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			if (found){
				return "";
			}
			// Backward to the next seperator
			//		  boolean notfound=true;
			// We have to add this functionality if we do refactorings with it.....
			// Backward to the next seperator
			boolean notfound = true;
			int start;
			for (start = start1; (start > 0 && notfound); start-- ){
				for (int i = 0; (i< dels.length && notfound); i++){
					try {
						notfound = (dels[i] != doc.getChar(start));
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			}
			start++; start++;
			int end;
			notfound=true;
			for (end = p.x; (end < doc.getLength() && notfound); end++ ){
				for (int i = 0; (i< dels.length && notfound); i++){
					try {
						notfound = (dels[i] != doc.getChar(end));
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			}
			end--;
			//PosInSearch=p.x-start;
			try {

				toSearch=doc.get(start,end-start).trim();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return toSearch;
	}
}
