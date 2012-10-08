/*
 * Created on Apr 24, 2005
 * emonic org.emonic.base.builders SimpleCommandRunner.java
 */
package org.emonic.base.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.console.MessageConsoleStream;
import org.emonic.base.EMonoPlugin;

/**
 * @author bb
 *
 */
public class SimpleCommandRunner {


	private static int BUFSIZE = 128;

	/**
	 * Just execute the string.
	 * @param executionString
	 * @param workingDir
	 * @return
	 * @throws IOException
	 */
	public static Process createProcess(String executionString, File workingDir) throws IOException {
		return Runtime.getRuntime().exec(executionString, null, workingDir);
	}

	/**
	 * Execute the string 
	 *  
	 */
	public static String runAndGetOutput(String cmd,  String args, String workingDir, IProgressMonitor moni, MessageConsoleStream out) {
		return runAndGetOutput(new String[] {cmd, args}, workingDir,moni,out);
	}


	/**
	 * Executes program in other process and gets its output.
	 * @param executionString The string to be executed.
	 * @param workingDir The working directory of the process.
	 * @return String with process output.
	 */
	public static String runAndGetOutput(String[] args, String workingDir,IProgressMonitor moni) {
		return runAndGetOutput( args, workingDir,moni, null);
	}


	/**
	 * Executes program in other process and gets its output.
	 * @param executionString The string to be executed.
	 * @param workingDir The working directory of the process.
	 * @return String with process output.
	 */
	public static String runAndGetOutput(String[] args, String workingDir,IProgressMonitor moni, MessageConsoleStream out) {

		// try to execute the process
		Process process = null;
		try {
			// TODO DEBUG LOG
			/*for(int i = 0; i < args.length; i++) {
				System.out.print(args[i]+" ");
			}*/
			File dir = new File(workingDir);
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(dir);
			process = pb.start();
		} catch (Exception e) {
			// throw new RuntimeException(e);
			e.printStackTrace();
			return e.getMessage();
		}

		// get its output
		StringBuffer contents = new StringBuffer();
		if (process != null) {
			// first, get its output from stdout
			InputStream is = process.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is), BUFSIZE);
			boolean mayBeClosed= false;
			try {
				String currentLine = "";
				boolean atTheEnd=false;

				// There are processes which do not deliver a \n at the end 
				// of a line so this might hang forever (seen with nant)
				//while   (  ( currentLine = in.readLine (  )  )  != null ){
				while (! atTheEnd){
					currentLine = "";	
					int next = -1;
					while ((next=in.read()) != -1 && (char)next != '\n'){
						currentLine+=(char)next;
					}
					if (next == -1){
						atTheEnd=true;
					}
					contents.append( currentLine + "\n") ; 
					if (out!=null){
						out.println(currentLine);
					}
					Thread.sleep(5);
					if (moni.isCanceled()){
						endProcess(process);
						mayBeClosed=true;
					}
				}

				//                int c;
				//                while ((c = in.read()) != -1) {
				//                    contents.append((char) c);
				//                }
			} catch (Exception e) {
				if (! mayBeClosed)
					throw new RuntimeException(e);
			}
			// then, get its output from stderr - useful if any error occurs!!!
			in = new BufferedReader(new InputStreamReader(process.getErrorStream()), BUFSIZE);
			try {
				//                int c;
				//                while ((c = in.read()) != -1) {
				//                    contents.append((char) c);
				//                }
				String currentLine = null;
				while  (  ( currentLine = in.readLine (  )  )  != null ){  
					contents.append( currentLine + "\n" ) ; 
					Thread.sleep(5);
					if (out != null){
						out.println(currentLine);
					}
					if (moni.isCanceled()){
						endProcess(process);
						mayBeClosed=true;
					}
				}

			} catch (Exception e) {
				if (! mayBeClosed)
					throw new RuntimeException(e);
			}
			// wait until the process completes
			try {
				process.waitFor(); 
			} catch (InterruptedException e) {
				if (! mayBeClosed)
					throw new RuntimeException(e);
			}
		} else {
			try {
				throw new CoreException(EMonoPlugin.makeStatus(IStatus.ERROR, ("Error running  ( "  + args + " )"),new Exception("Error running the build ( "  + args + " )")));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return contents.toString();
	}

	private static void endProcess(Process process) {
		process.destroy();		
	}
}
