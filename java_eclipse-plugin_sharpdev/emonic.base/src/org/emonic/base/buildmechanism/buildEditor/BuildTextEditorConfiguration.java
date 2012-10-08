package org.emonic.base.buildmechanism.buildEditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class BuildTextEditorConfiguration extends SourceViewerConfiguration {
	private BuildTextEditorDoubleClickStrategy doubleClickStrategy;
	private BuildTextEditorTagScanner tagScanner;
	private BuildTextEditorScanner scanner;
	private ColorManager colorManager;

	public BuildTextEditorConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			BuildTextEditorPartitionScanner.XML_COMMENT,
			BuildTextEditorPartitionScanner.XML_TAG };
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new BuildTextEditorDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected BuildTextEditorScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new BuildTextEditorScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IBuildTextEditorColorConstants.DEFAULT))));
		}
		return scanner;
	}
	protected BuildTextEditorTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new BuildTextEditorTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IBuildTextEditorColorConstants.TAG))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, BuildTextEditorPartitionScanner.XML_TAG);
		reconciler.setRepairer(dr, BuildTextEditorPartitionScanner.XML_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IBuildTextEditorColorConstants.XML_COMMENT)));
		reconciler.setDamager(ndr, BuildTextEditorPartitionScanner.XML_COMMENT);
		reconciler.setRepairer(ndr, BuildTextEditorPartitionScanner.XML_COMMENT);

		return reconciler;
	}

}