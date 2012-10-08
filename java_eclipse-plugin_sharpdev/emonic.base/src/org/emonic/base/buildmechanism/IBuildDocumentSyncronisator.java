package org.emonic.base.buildmechanism;

import org.eclipse.jface.text.IDocument;

public interface IBuildDocumentSyncronisator {
  void initFromDocument(IDocument doc);
  IDocument getAsDocument();
}
