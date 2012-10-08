package org.emonic.base.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.preferences.DefaultPrefsSetter;


public class EMonoCSharpConfiguration extends SourceViewerConfiguration {
	
	private EMonoCSharpDoubleClickStrategy doubleClickStrategy;
	private EMonoCSharpCodeScanner codeScanner;
	private CSharpPartitionScanner partScanner;
	private CommentSingleTokenScanner multiLineCommentScanner;
	private ColorManager colorManager;
	private CSharpEditor fTextEditor; 
	private CSharpAutoIndentStrategy cSharpStrategy;
	private DefaultIndentLineAutoEditStrategy defaultStrategy;
	private IReconciler reconciler;
	private IQuickAssistAssistant assistant;
	
	/**
	 * Single token scanner for scanning multi-line-comments.
	 */
	private class CommentSingleTokenScanner extends BufferedRuleBasedScanner {
		
		private Token commentToken = new Token(null);
		private ColorManager colManager;
		
		public CommentSingleTokenScanner(ColorManager manager) {
			colManager = manager;
			updateToken();
			setDefaultReturnToken(commentToken);
		}
		
		/**
		 * updates the text style of multi-line comments from the Preference store
		 * @param manager The color manager used by the C# plugin
		 */
		public void updateToken()
		{
			IPreferenceStore store = EMonoPlugin.getDefault().getPreferenceStore();
			commentToken.setData(new TextAttribute(colManager.getColor(
					PreferenceConverter.getColor(store,DefaultPrefsSetter.COMMENTCOLOR)),
					null,
					store.getBoolean(DefaultPrefsSetter.COMMENTBOLD) == true ?
							org.eclipse.swt.SWT.BOLD :
								org.eclipse.swt.SWT.NORMAL));	
		}
	}

	
	/**
	 * Default constructor
	 * @param colorManager The shared color manager instance
	 * @param editor The C Sharp Editor instance
	 */
	public EMonoCSharpConfiguration(ColorManager colorManager, CSharpEditor editor) {
		this.colorManager = colorManager;
		fTextEditor = editor;
	}
	
	
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
//		System.out.println("DEBUG_ New contentassistant");
		ContentAssistant assistant = new ContentAssistant();

		//assistant.setContentAssistProcessor(new
		// PerlCompletionProcessor(fTextEditor),
		// IDocument.DEFAULT_CONTENT_TYPE);
		// Enable content assist for all content types
		String[] contentTypes = this.getConfiguredContentTypes(sourceViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			assistant.setContentAssistProcessor(new CSharpCompletionProcessor(
					fTextEditor), contentTypes[i]);
		}

		assistant.enableAutoActivation(true);
		assistant.enableAutoInsert(true);
		assistant.setAutoActivationDelay(500);
		assistant
				.setProposalPopupOrientation(ContentAssistant.PROPOSAL_OVERLAY);
		assistant
				.setContextInformationPopupOrientation(ContentAssistant.CONTEXT_INFO_ABOVE);
		//assistant.setContextInformationPopupBackground(PerlColorProvider
		//		.getColor(new RGB(150, 150, 0)));
		//assistant.setProposalSelectorBackground(PerlColorProvider
		//		.getColor(new RGB(254, 241, 233)));
		assistant
				.setInformationControlCreator(getInformationControlCreator(sourceViewer));

		return assistant;
	}
	
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		return new String[] {IDocument.DEFAULT_CONTENT_TYPE,
				CSharpPartitionScanner.CSHARP_MULTI_LINE_COMMENT};
	}
	
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null) {
			doubleClickStrategy = new EMonoCSharpDoubleClickStrategy();
		}
		return doubleClickStrategy;
	}

	
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new MarkerAnnotationHover(fTextEditor);
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (reconciler == null) {
			reconciler = new MonoReconciler(
					new CSharpReconcilingStrategy(fTextEditor.getExamineJob()),
					false);
		}
		return reconciler;
	}

	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
		if (assistant == null) {
			assistant = new QuickAssistAssistant();
			assistant.setQuickAssistProcessor(new CSharpQuickAssistProcessor(sourceViewer,fTextEditor));
		}
		return assistant;
	}	
	
	/**
	 * Updates colors for syntax highlighting after a change in the preference store.
	 */
	public void updateScannerColors()
	{
		if (codeScanner != null) {
			codeScanner.updateColors();
		}
		if (multiLineCommentScanner != null) {
			multiLineCommentScanner.updateToken();
		}
	}
	
	
	protected CSharpPartitionScanner getCSharpPartitionScanner() {
	
		if (partScanner == null) {
			partScanner = new CSharpPartitionScanner();
		}
		return partScanner;
	}
	
	
	protected EMonoCSharpCodeScanner getCSharpCodeScanner() {
		if (codeScanner == null) {
			codeScanner = new EMonoCSharpCodeScanner(colorManager);
		}
		return codeScanner;
	}

	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCSharpCodeScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
        multiLineCommentScanner = new CommentSingleTokenScanner(colorManager);
        dr = new DefaultDamagerRepairer(multiLineCommentScanner);
		reconciler.setDamager(dr, CSharpPartitionScanner.CSHARP_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, CSharpPartitionScanner.CSHARP_MULTI_LINE_COMMENT);

		return reconciler;
	}
	
	
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy[] strategy = new IAutoEditStrategy[1];
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
			if (cSharpStrategy == null) {
				cSharpStrategy = new CSharpAutoIndentStrategy();
			}
			strategy[0] = cSharpStrategy;
		}
		else {
			if (defaultStrategy == null) {
				defaultStrategy = new DefaultIndentLineAutoEditStrategy();
			}
			strategy[0] = defaultStrategy;
		}
		return strategy;
	}

	private IInformationControlCreator getOutlinePresenterControlCreator(
			final ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new QuickOutlinePopupDialog(parent, sourceViewer);
			}
		};
	}

	public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer) {
		InformationPresenter presenter = new InformationPresenter(
				getOutlinePresenterControlCreator(sourceViewer)) {
			public IInformationProvider getInformationProvider(
					String contentType) {
				return new IInformationProvider() {

					public String getInformation(ITextViewer textViewer,
							IRegion subject) {
						return "a";
					}

					public IRegion getSubject(ITextViewer textViewer, int offset) {
						return new Region(0, 0);
					}

				};
			}
		};
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}
	
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType, int stateMask) {
		if (stateMask == ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK) {
			return getTextHover(sourceViewer, contentType);
		}
		return null;
	}
			
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		return new DefaultTextHover(sourceViewer) {
			protected boolean isIncluded(Annotation annotation) {
				return annotation instanceof MarkerAnnotation;
			}
		};
	}
}