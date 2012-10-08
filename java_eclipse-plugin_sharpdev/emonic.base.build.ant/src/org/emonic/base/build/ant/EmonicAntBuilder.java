package org.emonic.base.build.ant;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.console.MessageConsoleStream;
import org.emonic.base.buildmechanism.IInternalBuilder;

public class EmonicAntBuilder implements IInternalBuilder {

	private String buildFileName;
	boolean isFullBuild = false;
	String[] targets;
	String homedir;
	String buildLocation;

	public EmonicAntBuilder(IFile buildFile) {
		buildFileName = buildFile.getName();
		String f = ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toOSString();
		String bfl = buildFile.getFullPath().removeLastSegments(1).toOSString();
		buildLocation = f + File.separator + bfl;
	}

	public void buildFull(String dir, IProgressMonitor monitor,
			MessageConsoleStream buildConsoleStream) {
		// AntBuildLogger.setMessageStream(buildConsoleStream);

		homedir = dir;
		isFullBuild = true;
		build(buildConsoleStream);
	}

	public void buildInc(String dir, String[] affectedTargets,
			OutputStream buildConsoleStream) {
		// AntBuildLogger.setMessageStream(buildConsoleStream);
		homedir = dir;
		isFullBuild = false;
		targets = affectedTargets;
		build(buildConsoleStream);

	}

	private void build(OutputStream buildConsoleStream) {
		AntRunner runner = new AntRunner();

		runner.setAntHome(buildLocation);
		runner.setBuildFileLocation(buildLocation + File.separator
				+ buildFileName);
		runner.setAntHome(homedir);

		PrintStream savedSysOut = System.out;
		PrintStream savedSysErr = System.out;
		System.out.flush();
		System.err.flush();
		System.setOut(new PrintStream(buildConsoleStream));
		System.setErr(new PrintStream(buildConsoleStream));
		runner.addBuildLogger("org.apache.tools.ant.DefaultLogger");
		if (!isFullBuild) {
			runner.setExecutionTargets(targets);
		}
		try {
			runner.run(new NullProgressMonitor());
		} catch (CoreException e) {
			// e.printStackTrace();
		}
		System.out.flush();
		System.err.flush();
		System.setOut(savedSysOut);
		System.setErr(savedSysErr);
	}

}
