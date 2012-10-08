package sharpdev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.console.MessageConsoleStream;

public class CommandRunner {

	private static int BUFSIZE = 128;
	
	/**
	 * Executes program in another process and gets its output.
	 * @param args Arguments of execution. 0 => Program, 1, 2, n => Program Arguments
	 * @param workingDir The working directory of the process.
	 * @return String with process output.
	 * @throws CoreException 
	 */
	public static String executeProcess(String[] args, String workingDir, IProgressMonitor progressMonitor, MessageConsoleStream out) throws CoreException {
		Process process = null;
		
		// Try to execute the process
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(workingDir));
			process = pb.start();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		StringBuffer contents = new StringBuffer();
		if (process != null) {
			// Get process output from stdout
			InputStream is = process.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is), BUFSIZE);
			boolean mayBeClosed= false;
			try {
				String currentLine = "";
				boolean atTheEnd = false;

				// There are processes which do not deliver a \n at the end 
				// of a line so this might hang forever (seen with nant)
				while (!atTheEnd) {
					currentLine = "";	
					int next = -1;
					while ((next=in.read()) != -1 && (char)next != '\n') {
						currentLine += (char) next;
					}
					if (next == -1) {
						atTheEnd=true;
					}
					contents.append(currentLine + "\n") ; 
					if (out!=null) {
						out.println(currentLine);
					}
					Thread.sleep(5);
					if (progressMonitor.isCanceled()) {
						endProcess(process);
						mayBeClosed=true;
					}
				}
			} catch (Exception e) {
				if (!mayBeClosed) throw new RuntimeException(e);
			}
			
			// Get process output from stderr
			in = new BufferedReader(new InputStreamReader(process.getErrorStream()), BUFSIZE);
			try {
				String currentLine = null;
				while((currentLine = in.readLine()) != null) {  
					contents.append(currentLine + "\n"); 
					Thread.sleep(5);
					if (out != null) {
						out.println(currentLine);
					}
					if (progressMonitor.isCanceled()){
						endProcess(process);
						mayBeClosed = true;
					}
				}

			} catch (Exception e) {
				if (!mayBeClosed) throw new RuntimeException(e);
			}
			
			// Wait until the process completes
			try {
				process.waitFor(); 
			} catch (InterruptedException e) {
				if (!mayBeClosed)
					throw new RuntimeException(e);
			}
		
		// If our process execute did not succeed
		} else {
			String errorMessage = "Error executing process: " + Arrays.toString(args);
			throw new CoreException(StatusHelper.makeStatus(IStatus.ERROR, errorMessage, new Exception(errorMessage)));
		}
		
		return contents.toString();
	}
	
	private static void endProcess(Process process) {
		process.destroy();		
	}
}
