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
 * 		Bernhard Brem - initial implementation
 *      Harald Krapfenbauer - rewrote most of the code due to new web interface
 */


package org.emonic.debugger.frontend;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.themes.ColorUtil;
import org.emonic.debugger.DebuggerPlugin;
import org.emonic.debugger.launching.EmonicDebugConstants;
import org.emonic.debugger.webinterface.EventData;
import org.emonic.debugger.webinterface.ThreadData;


public class EmonicDebugTarget extends EmonicDebugElement implements
IDebugTarget {
	
	// associated system process (Mono)
	private IProcess process;
	// port of web service
	private int port;
	// containing launch object
	private ILaunch launch;
	// synchronizes web service commands
	private Synchronizer frontend;
	// threads
	private HashMap threadList = new HashMap(); // <Integer (Thread ID), EmonicThread>
	// event dispatch job
	private EventDispatchJob eventDispatch;
	// string of application plus arguments
	private String name;
	// application's console
	IOConsole cons;
	// console streams for stdout and stderr
	private IOConsoleOutputStream stdout, stderr;
	// console stdin stream
	private IOConsoleInputStream stdin;
	// count how often target has suspended
	private int suspendCount = 0;
	// console input reader job
	private Job readerJob;


	/**
	 * Only constructor for an EmonicDebugTarget.
	 * @param launch The corresponding ILaunch object
	 * @param process The process that executes xsp2 and is connected to this debug target
	 * @param executable The full path of the inferior application
	 * @param programArgs Arguments for the inferior application
	 * @param runtimeArgs Arguments for the Mono runtime
	 * @param workDir The working directory of the inferior application
	 * @throws DebugException
	 */
	public EmonicDebugTarget(ILaunch launch, IProcess process, String executable,
			String programArgs, String runtimeArgs, String workDir, int port)
	throws DebugException {
		// remember some information in fields
		this.launch = launch;
		target = this;
		this.process = process;
		this.port = port;
		name = executable + " " + programArgs;
		// create the frontend to communicate with MDB
		frontend = new Synchronizer(this.port);
		// register as breakpoint listener
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
		// prepare the command line for mdb/xsp2
		String cmdLine = runtimeArgs + " " + executable + " " + programArgs;
		// start MDB: at the beginning, when xsp2 is not yet ready, java.net.ConnectExceptions
		// may occur, we catch these. We throw a DebugException after the timeout of the
		// preferences is reached.
		int timeout = DebuggerPlugin.getDefault().getPreferenceStore().getInt(
				EmonicDebugConstants.PREF_STARTING_UP_TIMEOUT);
		long start = new Date().getTime();
		while (true) {
			try {
				frontend.StartMdb(cmdLine);
				break;
			} catch (DebugException e) {
				if (new Date().getTime()-start > timeout)
					// timeout reached
					throw(e);
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
				}
			}
		}
		// install breakpoints
		installDeferredBreakpoints();
		// start the debugged application
		frontend.RunApp(workDir);
		// create a console that displays the debuggee's output
		setupConsole();
		// start event dispatcher job
		eventDispatch = new EventDispatchJob(this);
		eventDispatch.schedule();
	}


	private void setupConsole() {
		// note: the console will be in background if "allocate console" was chosen
		// in the launch configuration. if not, this console will show up.
		cons = new IOConsole(name,null);
		IConsoleManager consMan = ConsolePlugin.getDefault().getConsoleManager();
		consMan.addConsoles(new IConsole[]{cons});
		stdout = cons.newOutputStream();
		stderr = cons.newOutputStream();
		stdin = cons.getInputStream();
		// set colors from the preferences of the debug plugin!
		IPreferenceStore store = DebugUITools.getPreferenceStore();
		String stdoutColor = store.getString(
				EmonicDebugConstants.DEBUG_PLUGIN_CONSOLE_SYS_OUT_COLOR);
		String stderrColor = store.getString(
				EmonicDebugConstants.DEBUG_PLUGIN_CONSOLE_SYS_ERR_COLOR);
		String stdinColor = store.getString(
				EmonicDebugConstants.DEBUG_PLUGIN_CONSOLE_SYS_IN_COLOR);
		stdout.setColor(new Color(Display.getDefault(),
				ColorUtil.getColorValue(stdoutColor)));
		stderr.setColor(new Color(Display.getDefault(),
				ColorUtil.getColorValue(stderrColor)));
//		String backgroundColor = store.getString(
//				EmonicDebugConstants.DEBUG_PLUGIN_CONSOLE_BACKGROUND_COLOR);
		// this code only works on Eclipse >3.3, so we disable it
//		cons.setBackground(new Color(Display.getDefault(),
//				ColorUtil.getColorValue(backgroundColor)));
		stdin.setColor(new Color(Display.getDefault(),
				ColorUtil.getColorValue(stdinColor)));
		cons.activate();
		// the following job reads input from "our" IOConsole and feeds it to the process' stdin
		readerJob = new Job("Console read job") {
			public IStatus run(IProgressMonitor pm) {
				IStreamsProxy proxy = process.getStreamsProxy();
				if (proxy == null)
					return new Status(IStatus.OK,DebuggerPlugin.PLUGIN_ID,
					"Process streams proxy is null, nothing to do.",new Exception("Process streams proxy is null, nothing to do"));				
				try {
					while (true) {
						char character = (char)stdin.read();
						proxy.write(new String(new char[]{character}));
					}
				} catch (IOException e) {
					return new Status(IStatus.OK,DebuggerPlugin.PLUGIN_ID,
							"Console Input Stream closed, job completed.", e);
				}
			}
		};
		
		readerJob.setSystem(true);
		readerJob.schedule();
	}

	/**
	 * Returns the object for communication with the web service frontend
	 * @return
	 */
	public Synchronizer getFrontend() {
		return frontend;
	}
	
	public IProcess getProcess() {
		return process;
	}

	public IThread[] getThreads() {
		IThread[] result = new IThread[threadList.size()];
		Iterator iter = threadList.values().iterator();
		for (int i=0; i<threadList.size(); i++)
		result[i] = (IThread) iter.next();
		return result;
	}

	public boolean hasThreads() throws DebugException {
		return threadList.size() > 0;
	}

	public String getName() {
		if (isTerminated())
			return "<terminated>"+name;
		else
			return name;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint.getModelIdentifier().equals(
				EmonicDebugConstants.ID_EMONIC_DEBUG_MODEL))
			return true;
		return false;
	}


	public IDebugTarget getDebugTarget() {
		return this;
	}


	public ILaunch getLaunch() {
		return launch;
	}


	public boolean canTerminate() {
		if (process == null)
			return false;
		return process.canTerminate();
	}


	public boolean isTerminated() {
		if (process == null)
			return true;
		return process.isTerminated();
	}


	private void terminateWebServer() throws DebugException {
		if (process != null) {
			try {
				// give xsp2 some time before it is killed
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignored
			}
			process.terminate();
		}
	}
	
	public void terminate() throws DebugException {
		if (canTerminate()) {
			// quit MDB
			frontend.Quit();
			frontend = null;
			// quit XSP2
			terminateWebServer();
		}
		terminated();
	}


	public boolean canResume() {
		return !isTerminated() && isSuspended();
	}


	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
	}


	public boolean isSuspended() {
		// check all threads, if we find at least 1 suspended -> true
		Iterator iter = threadList.values().iterator();
		while (iter.hasNext()) {
			EmonicThread thread = (EmonicThread)iter.next();
			if (thread.isSuspended())
				return true;
		}
		return false;
	}


	public void resume() throws DebugException {
		// resume all suspended threads
		Iterator iter = threadList.values().iterator();
		while (iter.hasNext()) {
			EmonicThread thread = (EmonicThread)iter.next();
			if (thread.canResume())
				thread.resume();
		}
		resumed(true);
	}
	
	/**
	 * Notify the target that one of its threads has resumed. If all threads are resumed
	 * now, the target is treated as "resumed" and fires an appropriate event.
	 */
	public void possiblyResumed() {
		Iterator iter = threadList.values().iterator();
		while (iter.hasNext()) {
			IThread thread = (IThread)iter.next();
			if (thread.isSuspended())
				return;
		}
		// all threads are resumed
		resumed(false);
	}
	
	/**
	 * Notify the target that it has resumed.
	 * @param clientRequest true if the resume was a client request (i.e. if the user
	 * marked the target in the Debug view and clicked the resume button)
	 */
	public void resumed(boolean clientRequest) {
		if (clientRequest) {
			fireResumeEvent(DebugEvent.CLIENT_REQUEST);
			return;
		}
		fireResumeEvent(DebugEvent.UNSPECIFIED);
	}

	public void suspend() throws DebugException {
		// suspend all threads
		Iterator iter = threadList.values().iterator();
		while (iter.hasNext()) {
			EmonicThread thread = (EmonicThread)iter.next();
			if (thread.canSuspend())
				thread.suspend();
		}
		suspended(true);
	}

	/**
	 * Notify the target that it has suspended.
	 * @param clientRequest true if the suspend was a client request (i.e. if the user
	 * marked the target in the Debug view and clicked the suspend button)
	 */
	public void suspended(boolean clientRequest) {
		if (clientRequest) {
			fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
			return;
		}
		fireSuspendEvent(DebugEvent.UNSPECIFIED);
	}
	
	public void breakpointAdded(IBreakpoint breakpoint) {

		if (supportsBreakpoint(breakpoint)) {
			try {
				int number = frontend.Break(
						((ILineBreakpoint)breakpoint).getMarker().getResource().getLocation().
						toOSString(),
						((ILineBreakpoint)breakpoint).getLineNumber());
				if (number == -1) {
					// adding breakpoint failed, so we delete the marker which the
					// user can see
					breakpoint.delete();
					return;
				}
				if (!breakpoint.isEnabled()) {
					// disable the breakpoint
					frontend.Disable(number);
				}
				((EmonicLineBreakpoint)breakpoint).setMdbId(number);
			} catch (DebugException de) {
				de.printStackTrace();
			} catch (CoreException ce) {
				ce.printStackTrace();
			}
		}
	}

	
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {

		if (supportsBreakpoint(breakpoint)) {
			int number = ((EmonicLineBreakpoint)breakpoint).getMdbId();
			if (number == -1)
				return;
			try {
				frontend.Delete(number);
			} catch (DebugException de) {
				de.printStackTrace();
			}
		}
	}


	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {

		if (supportsBreakpoint(breakpoint)) {
			int number = ((EmonicLineBreakpoint)breakpoint).getMdbId();
			if (number == -1)
				return;
			try {
				if (breakpoint.isEnabled())
					frontend.Enable(number);
				else
					frontend.Disable(number);
			} catch (DebugException de) {
				de.printStackTrace();
			} catch (CoreException ce) {
				ce.printStackTrace();
			}
		}
	}


	public boolean canDisconnect() {
		return false;
	}

	
	public void disconnect() {
		// unsupported
	}


	public boolean isDisconnected() {
		return false;
	}


	public boolean supportsStorageRetrieval() {
		// not supported
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length) {
		// not supported
		return null;
	}


	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	private void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager().getBreakpoints(
						EmonicDebugConstants.ID_EMONIC_DEBUG_MODEL);
		for (int i=0; i<breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}

	
	/**
	 * Called when this debug target terminates.
	 */
	private void terminated() {
		// delete all threads
		Iterator iter = threadList.values().iterator();
		while (iter.hasNext()) {
			EmonicThread thread = (EmonicThread)iter.next();
			thread.fireTerminateEvent();
		}
		threadList = new HashMap();
		
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		fireTerminateEvent();
		DebuggerPlugin.getDefault().returnPort(port);
		// this terminates the input reader job
		try {
			stdin.close();
		} catch (IOException e) {
			// ignored
		}
	}

	class EventDispatchJob extends Job {
		
        private boolean canWait = false;
        private int timeout;
        
		public EventDispatchJob(EmonicDebugTarget target) {
			
			super("Emonic Event Dispatch");
			setSystem(true);
			timeout = DebuggerPlugin.getDefault().getPreferenceStore()
					.getInt(EmonicDebugConstants.PREF_EVENT_POLLING);
		}

		protected IStatus run(IProgressMonitor monitor) {

			while (!isTerminated()) {
				EventData event = null;
				try {
					event = frontend.getNextEvent();
					String eventType = event.getEventType();
					// we poll for events uninterruptedly as long as events are available
					// if none are available, we wait
					if (!eventType.equals("NONE")) {
						canWait = false;
					} else {
						canWait = true;
					}
//					System.out.println("Event: "+eventType);

					if (eventType.equals("TargetStopped")) {
						int threadNumber = event.getArg1();
						int signal = event.getArg2();

						suspendCount++;
						// update list of threads
						updateThreadList();
						// handle suspension to thread
						((EmonicThread)threadList.get(new Integer(threadNumber)))
						.suspended(signal,0,false);
						continue;
					}
					if (eventType.equals("TargetInterrupted")) {
						// we treat this like a stopped event since the thread is shown
						// as stopped
						int threadNumber = event.getArg1();
						suspendCount++;
						updateThreadList();
						((EmonicThread)threadList.get(new Integer(threadNumber)))
							.suspended(0,0,false);
						continue;
					}
					if (eventType.equals("OnMainProcessCreated")) {
						updateThreadList();
						fireCreationEvent();
						continue;
					}
					
					if (eventType.equals("TargetHitBreakpoint")) {
						int threadNumber = event.getArg1();
						int breakpointNumber = event.getArg2();
						suspendCount++;
						// update thread list
						updateThreadList();
						// handle suspension to thread
						((EmonicThread)threadList.get(new Integer(threadNumber)))
							.suspended(0,breakpointNumber,false);
						continue;
					}
					if (eventType.equals("TargetSignaled")
//							|| eventType.equals("TargetExited")
							|| eventType.equals("OnThreadCreated")	
							|| eventType.equals("OnThreadExited")
							|| eventType.equals("OnProcessCreated")
							|| eventType.equals("OnProcessExited")
							|| eventType.equals("OnProcessExecd")) {
						updateThreadList();
						continue;
					}
					if (eventType.equals("Exception")
							|| eventType.equals("UnhandledException")) {
						/* Note: I observed the following behavior of MDB if an
						   unhandled exception occurs:
						   1) The thread gets signal 8 and stops (TargetStopped event)
						   2) On resume, the thread catches the exception (UnhandledException
						      event)
						   3) On resume, the thread gets signal 8 again and stops (TargetStopped
						      event)
						*/
						int threadNumber = event.getArg1();
						suspendCount++;
						// update list of threads
						updateThreadList();
						// handle suspension to thread
						((EmonicThread)threadList.get(new Integer(threadNumber)))
							.suspended(0,0,true);
						continue;
					}
					if (eventType.equals("FrameChanged")) {
						// Never saw this event generated
						continue;
					}
					if (eventType.equals("OnTargetExited")) {
						// note: if we kill the target, we won't get this event
						frontend.Quit(); // quits MDB
						frontend = null;
						terminateWebServer(); // quits XSP2
						break;
					}
					if (eventType.equals("OnTargetOutput")) {
						int where = event.getArg1(); // 1=stderr, 0=stdout
						String what = event.getArg3();
						try {
						if (where == 1)
							stderr.write(what);
						else
							stdout.write(what);
						} catch (IOException ioe) {
							// ignored
						}
						continue;
					}
				} catch (Exception e) {
					// xsp2 likely has terminated
					break;
				}
				if (canWait) {
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException ie) {
					}
				}
			}
			terminated();
			return Status.OK_STATUS;
		}
	}

	/**
	 * Updates the internal list of threads. If a new thread is detected,
	 * <code>fireCreationEvent</code> is executed. If a thread has vanished,
	 * <code>fireTerminationEvent</code> is executed.
	 * @throws DebugException
	 */
	private void updateThreadList() throws DebugException {
		if (isTerminated())
			return;
		
		ThreadData[] threadData = frontend.GetThreads();
		// compare to previous list of threads and update
		HashMap newThreads = new HashMap();
		ThreadData td;
		for (int i=0; i<threadData.length; i++) {
			td = threadData[i];
			Integer ID = new Integer(td.getThreadNumber());
			if (threadList.containsKey(ID)) {
				// thread exists already --> copy it to new hashmap
				EmonicThread thread = (EmonicThread)threadList.get(ID);
				newThreads.put(ID, thread);
				thread.setSuspendedFlag(!td.isThreadState()); // threadState: true == running
				threadList.remove(ID);
			} else {
				// new thread
				EmonicThread thread = new EmonicThread(
						this,
						td.getProcessNumber(),
						td.getProcessCmdLine(),
						td.getThreadNumber(),
						td.isDaemonThread(),
						td.getTID());
				thread.setSuspendedFlag(!td.isThreadState());
				newThreads.put(new Integer(td.getThreadNumber()), thread);
				thread.fireCreationEvent();
			}
		}
		// fire termination events for all threads that vanished
		Iterator iter = threadList.values().iterator();
		while (iter.hasNext()) {
			EmonicThread thread = (EmonicThread) iter.next();
			thread.fireTerminateEvent();
		}
		threadList = newThreads;
	}
	
	/**
	 * Returns a positive value how often the target has suspended since launching.
	 * This allows other debug elements to determine if they must update. 
	 */
	public int getSuspendCount() {
		return suspendCount;
	}
}
