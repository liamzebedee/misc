package org.emonic.base.buildmechanism;

import java.io.OutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.MessageConsoleStream;

public interface IInternalBuilder {

	void buildFull(String dir, IProgressMonitor monitor, MessageConsoleStream buildConsoleStream);

	void buildInc(String dir, String[] affectedTargets,  OutputStream OutStream);
 
}
