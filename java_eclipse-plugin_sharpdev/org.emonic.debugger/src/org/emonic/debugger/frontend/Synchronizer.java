/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Harald Krapfenbauer - initial API and implementation
 */


package org.emonic.debugger.frontend;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.emonic.debugger.DebuggerPlugin;
import org.emonic.debugger.launching.EmonicDebugConstants;
import org.emonic.debugger.webinterface.BacktraceData;
import org.emonic.debugger.webinterface.EmonicDebugfrontendLocator;
import org.emonic.debugger.webinterface.EmonicDebugfrontendSoap;
import org.emonic.debugger.webinterface.EventData;
import org.emonic.debugger.webinterface.PrintData;
import org.emonic.debugger.webinterface.ThreadData;


/**
 * This class synchronizes all communication with the web interface. It is unknown if
 * concurrent commands are supported by xsp2 or MDB, so this class keeps us on the safe
 * side.
 * @author Harald Krapfenbauer
 */
public class Synchronizer {
	
	private EmonicDebugfrontendSoap frontend;
	
	public Synchronizer(int port) throws DebugException {
		try { 
			EmonicDebugfrontendLocator locator = new EmonicDebugfrontendLocator();
			String address = "http://localhost:"+String.valueOf(port)+"/EmonicDebuggingService.asmx";
			locator.setEndpointAddress("EmonicDebugfrontendSoap", address);
			frontend = locator.getEmonicDebugfrontendSoap();
		} catch (ServiceException se) {
			Error("Could not connect to debug web service",se);
		}	
	}
	
	private void Error(String message, Throwable e) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR,
				DebuggerPlugin.PLUGIN_ID,
				DebugPlugin.INTERNAL_ERROR,
				message,
				e));		
	}
	
	/**
	 * Start the Mono Debugger.
	 * @param cmdLine The arguments as would be given to MDB on the command line.
	 * @throws DebugException
	 */
	public synchronized void StartMdb(String cmdLine)
	throws DebugException {
		try {
			frontend.startMdb(cmdLine);
		} catch (RemoteException re) {
			Error("Could not start MDB",re);
		}
	}

	/**
	 * Starts the debugged application in MDB. The application is not automatically
	 * stopped in Main().
	 * @param workDir The working directory in which the debugged application must
	 * execute.
	 * @throws DebugException
	 */
	public synchronized void RunApp(String workDir) throws DebugException {
		try {
			frontend.runApp(workDir);
		} catch (RemoteException e) {
			Error("Could not start the application",e);
		}
	}
	 
	/**
	 * Get the next event from MDB (FIFO based).
	 * @return the event data
	 * @throws DebugException
	 */
	public synchronized EventData getNextEvent() throws DebugException {
		try {
			EventData evData;
			evData = frontend.getNextEvent();
			return evData;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return null;
		}
	}
	
	/**
	 * Continues the thread in background
	 * @param threadID The thread to continue
	 * @throws DebugException
	 */
	public synchronized void Background(int threadID) throws DebugException {
		this.Thread(threadID);
		this.Background();
	}
	
	private void Background() throws DebugException {
		try {
			frontend.background();
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}

	/**
	 * Gets a backtrace/stacktrace.
	 * @param threadID The thread from which the backtrace should be returned
	 * otherwise just the next frame of the backtrace is returned.
	 * @return An array of which 1 element corresponds to 1 frame of the backtrace
	 * @throws DebugException
	 */
	public synchronized BacktraceData[] Backtrace(int threadID)
	throws DebugException {
		// set thread
		Thread(threadID);
		// get timeout from preferences
		int timeout = DebuggerPlugin.getDefault().getPreferenceStore()
				.getInt(EmonicDebugConstants.PREF_BACKTR_THR_TIMEOUT);
		// get back trace data from MDB
		BacktraceData bd;
		ArrayList backtraceData = new ArrayList();
		long start = new Date().getTime();
		// first call: execute the command
		bd = Backtrace(true);
		// maybe the response is invalid
		if (bd.getFrameNumber() != -1)
			backtraceData.add(bd);
		// execute until first valid response
		while (true) {
			bd = Backtrace(false);
			if (bd.getFrameNumber() != -1) {
				backtraceData.add(bd);
				break;
			}
			if (new Date().getTime()-start > timeout) {
				System.err.println("Timeout while getting backtrace data. You may try to "
						+"increase the timeout value in the preferences.");
				break;
			}
			try {
				Thread.sleep(25);
			} catch (InterruptedException ie) {
				// ignored
			}
		}
		// receive the rest
		BacktraceData newBd;
		while (bd.isMoreData()) {
			newBd = Backtrace(false);
			if (newBd.getFrameNumber() != -1) {
				bd = newBd;
				backtraceData.add(bd);
			}
			if (new Date().getTime()-start > timeout) {
				System.err.println("Timeout while getting backtrace data. You may try to "
						+"increase the timeout value in the preferences.");
				break;
			}
		}
		BacktraceData[] result = new BacktraceData[backtraceData.size()];
		for (int i=0; i<backtraceData.size(); i++) {
			result[i] = (BacktraceData)backtraceData.get(i);
		}
		return result;
	}
	
	private BacktraceData Backtrace(boolean execute) throws DebugException {
		try {
			BacktraceData btData;
			btData = frontend.backtrace(execute);
			return btData;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return null;
		}
	}

	/**
	 * Set a breakpoint
	 * @param file The file name
	 * @param lineNumber The line number
	 * @return MDB's ID for this breakpoint or -1 if request failed.
	 * @throws DebugException
	 */
	public synchronized int Break(String file, int lineNumber) throws DebugException{
		try {
			int bptNr;
			bptNr = frontend._break(file, lineNumber);
			return bptNr;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return -1;
		}
	}

	/**
	 * Deletes a breakpoint
	 * @param breakpointNumber MDB's ID for the breakpoint
	 * @throws DebugException
	 */
	public synchronized void Delete(int breakpointNumber) throws DebugException {
		try {
			frontend.delete(breakpointNumber);
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}

	/**
	 * Disables a breakpoint
	 * @param breakpointNumber MDB's ID for the breakpoint
	 * @throws DebugException
	 */
	public synchronized void Disable(int breakpointNumber) throws DebugException {
		try {
			frontend.disable(breakpointNumber);
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}

	/**
	 * Enabled a breakpoint
	 * @param breakpointNumber MDB's ID for the breakpoint
	 * @throws DebugException
	 */
	public synchronized void Enable(int breakpointNumber) throws DebugException {
		try {
			frontend.enable(breakpointNumber);
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}

	/**
	 * Execute the MDB "finish" command, i.e. until the current stack frame returns
	 * @param threadID The thread on which the command is executed
	 * @throws DebugException
	 */
	public synchronized void Finish(int threadID) throws DebugException {
		Thread(threadID);
		Finish();
	}
	
	private void Finish() throws DebugException {
		try {
			frontend.finish();
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}
	
	private void Frame(int frameNumber) throws DebugException {
		try {
			frontend.frame(frameNumber);
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}
	
	/**
	 * Execute MDB's "next" command
	 * @param threadID The thread on which the command shall be executed
	 * @throws DebugException
	 */
	public synchronized void Next(int threadID) throws DebugException {
		Thread(threadID);
		Next();
	}
	
	private void Next() throws DebugException {
		try {
			frontend.next();
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}
	
	
	/**
	 * Print a variable
	 * @param threadID The current thread
	 * @param frameID The current frame of the stacktrace
	 * @param variableName The name of the variable
	 * @return 
	 * @throws DebugException
	 */
	public synchronized PrintData Print(int threadID, int frameID, String variableName)
	throws DebugException {
		Thread(threadID);
		Frame(frameID);
		return Print(variableName);
	}
	
	private PrintData Print(String variableName) throws DebugException {
		try {
			PrintData pd;
			pd = frontend.print(variableName);
			return pd;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return null;
		}
	}
	
	/**
	 * Determine fields of a class name or the "this" object
	 * @param threadID The current thread
	 * @param frameID The current frame of the stacktrace
	 * @param className The name of the class or "this"
	 * @param staticOnly If only static fields shall be returned
	 * @return
	 * @throws DebugException
	 */
	public synchronized String PtypeFieldsOnly(int threadID, int frameID, String className,
			boolean staticOnly) throws DebugException {
		Thread(threadID);
		Frame(frameID);
		return PtypeFieldsOnly(className, staticOnly);
	}
	
	private String PtypeFieldsOnly(String className, boolean staticOnly) throws DebugException {
		try {
			String result;
			result = frontend.ptypeFieldsOnly(className, staticOnly);
			return result;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return null;
		}
	}
	
	/**
	 * Quits the application, MDB, and the xsp2 web server.
	 * @throws DebugException
	 */
	public synchronized void Quit() throws DebugException {
		try {
			frontend.quit();
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}

	/**
	 * Sets a variable
	 * @param threadID The current thread
	 * @param frameID The current frame of the stack trace
	 * @param variable The variable name
	 * @param value The new value
	 * @throws DebugException
	 */
	public synchronized void SetVariable(int threadID, int frameID, String variable,
			String value) throws DebugException {
		Thread(threadID);
		Frame(frameID);
		SetVariable(variable, value);
	}
	
	private void SetVariable(String variable, String value)
	throws DebugException {
		try {
			frontend.setVariable(variable, value);
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}
	
	/**
	 * Get a list of processes and threads (MDB "show threads" command).
	 * @return An array where each element corresponds to one thread
	 * @throws DebugException
	 */
	public synchronized ThreadData[] GetThreads() throws DebugException {
		// get timeout from preferences
		int timeout = DebuggerPlugin.getDefault().getPreferenceStore()
				.getInt(EmonicDebugConstants.PREF_BACKTR_THR_TIMEOUT);
		// get thread data from MDB
		ThreadData td;
		ArrayList threadData = new ArrayList();
		long start = new Date().getTime();
		// first call: execute the command
		td = GetThreads(true);
		// maybe the response is invalid
		if (td.getProcessNumber() != -1)
			threadData.add(td);
		// wait for first valid response
		// note: if an error occurs (e.g. "No Target"), we never get a valid one!
		while(true) {
			td = GetThreads(false);
			if (td.getProcessNumber() != -1) {
				threadData.add(td);
				break;
			}
			if (new Date().getTime()-start > timeout) {
				System.err.println("Timeout while getting thread data. You may try to " +
						"increase the timeout value in the preferences. (If your application" +
						" is currently terminating, ignore this warning.)");
				break;
			}
			try {
				Thread.sleep(25);
			} catch (InterruptedException ie) {
				// ignored
			}
		}
		// receive the rest
		ThreadData newTd;
		while (td.isMoreData()) {
			newTd = GetThreads(false);
			if (newTd.getProcessNumber() != -1) {
				td = newTd;
				threadData.add(td);
			}
			if (new Date().getTime()-start > timeout) {
				System.err.println("Timeout while getting thread data. You may try to " +
						"increase the timeout value in the preferences. (If your application" +
						" is currently terminating, ignore this warning.)");
				break;
			}
		}
		ThreadData[] result = new ThreadData[threadData.size()];
		for (int i=0; i<threadData.size(); i++) {
			result[i] = (ThreadData)threadData.get(i);
		}
		return result;
	}

	private ThreadData GetThreads(boolean execute) throws DebugException {
		try {
			ThreadData td;
			td = frontend.getThreads(execute);
			return td;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return null;
		}
	}

	/**
	 * Gets names of all local variables
	 * @param threadID The current thread
	 * @param frameID The current frame of the stack trace
	 * @return
	 * @throws DebugException
	 */
	public synchronized String GetLocals(int threadID, int frameID) throws DebugException {
		Thread(threadID);
		Frame(frameID);
		return GetLocals();
	}
	
	private String GetLocals() throws DebugException {
		try {
			String result;
			result = frontend.getLocals();
			return result;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return null;
		}
	}

	/**
	 * Get the names of all parameters valid in the current context. 
	 * @param threadID The current thread
	 * @param frameID The current frame of the stack trace
	 * @return
	 * @throws DebugException
	 */
	public synchronized String GetParameters(int threadID, int frameID) throws DebugException {
		Thread(threadID);
		Frame(frameID);
		return GetParameters();
	}
	
	private String GetParameters() throws DebugException {
		try {
			String result;
			result = frontend.getParameters();
			return result;
		} catch (RemoteException re) {
			Error("Web service call failed",re);
			return null;
		}
	}

	/**
	 * Execute the "step" command.
	 * @param threadID The ID of the thread that should be stepped.
	 * @throws DebugException
	 */
	public synchronized void Step(int threadID) throws DebugException {
		Thread(threadID);
		Step();
	}
	
	private void Step() throws DebugException {
		try {
			frontend.step();
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}

	/**
	 * Stops execution of a thread.
	 * @param threadID The ID of the thread that should be stopped.
	 * @throws DebugException
	 */
	public synchronized void Stop(int threadID) throws DebugException {
		Thread(threadID);
		Stop();
	}
	
	private void Stop() throws DebugException {
		try {
			frontend.stop();
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}
	
	private void Thread(int threadNumber) throws DebugException {
		try {
			frontend.thread(threadNumber);
		} catch (RemoteException re) {
			Error("Web service call failed",re);
		}
	}
}
