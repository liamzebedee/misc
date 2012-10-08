package org.emonic.base.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;


public class EMonoCSharpDocumentProvider extends TextFileDocumentProvider {
	private static CSharpPartitionScanner fgScanner= null;
	private final static String[] TYPES= new String[] {CSharpPartitionScanner.CSHARP_MULTI_LINE_COMMENT};//, CSharpPartitionScanner.CSharp_COMMENT };
	
	public IDocument getDocument(Object element) {
		IDocument document = super.getDocument(element);
		if (document.getDocumentPartitioner() == null) {
			IDocumentPartitioner partitioner= createCSharpPartitioner();
			document.setDocumentPartitioner(partitioner);
			partitioner.connect(document);	
		}
		return document;
	}
	
	/**
	 * @return
	 */
	private IDocumentPartitioner createCSharpPartitioner() {
		return new FastPartitioner(getCSharpPartitionScanner(), TYPES);
	}

	/**
	 * @return
	 */
	private CSharpPartitionScanner getCSharpPartitionScanner() {
		if (fgScanner == null)
			fgScanner= new CSharpPartitionScanner();
		return fgScanner;
	}
}

