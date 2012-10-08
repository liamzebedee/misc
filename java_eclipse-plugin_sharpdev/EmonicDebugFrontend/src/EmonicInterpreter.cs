/*
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * All rights reserved.
 *
 * This file is made available under terms of the license 
 * of the Mono Debugger v0.60 which is available in the
 * file mono-debugger-0.60/COPYING of the MDB source code.
 *
 * Contributors:
 * 		Harald Krapfenbauer - Initial implementation
 */
 
 
using Mono.Debugger;
using System.Collections;
using System.Threading;

namespace Emonic.Debugfrontend {
	
	public class EmonicInterpreter : Mono.Debugger.Frontend.Interpreter {
		
		private static ArrayList eventList = System.Collections.ArrayList.Synchronized(new ArrayList());
		private static EmonicInterpreter thisReference;
		
		public static ArrayList backtraceList = System.Collections.ArrayList.Synchronized(new ArrayList());
		public static int breakpointNumber;
		public static Semaphore breakpointSem = new Semaphore(0,1);
		public static printData printOutput;
		public static Semaphore printSem = new Semaphore(0,1);
		public static string ptypeOutput;
		public static string ptypeOutputStaticOnly;
		public static Semaphore ptypeSem = new Semaphore(0,1);
		public static ArrayList threadList = System.Collections.ArrayList.Synchronized(new ArrayList());
		public static string localsOutput;
		public static Semaphore localsSem = new Semaphore(0,1);
		public static string paramsOutput;
		public static Semaphore paramsSem = new Semaphore(0,1);
		
		
		public EmonicInterpreter(DebuggerConfiguration config,
		                         DebuggerOptions options)
		: base(true,config,options)
		{
			DebuggerEngine = new EmonicDebuggerEngine(this);
			thisReference = this;
		}

		
		public struct eventData {
			public string eventType;
			public int arg1;
			public int arg2;
			public string arg3;   
		}
		
		
		public static eventData GetNextEvent() {
			eventData result;
			if (eventList.Count <= 0) {
				// no event available				
				result = new eventData();
				result.eventType = "NONE";
				result.arg1 = 0;
				result.arg2 = 0;
				result.arg3 = "";
				return result;
			}
			result = (eventData)eventList[0];
			eventList.RemoveAt(0);
			return result;
		}
		
		
		protected override void OnTargetEvent (Mono.Debugger.Thread thread, TargetEventArgs args) {
			
			switch (args.Type) {

				case TargetEventType.TargetStopped: {
					eventData newEvent = new eventData();
					newEvent.eventType = "TargetStopped";
					newEvent.arg1 = thread.ID;
					newEvent.arg3 = "";
					  					
					newEvent.arg2 = (int)args.Data;

					eventList.Add(newEvent);
					break;
				}
				
				case TargetEventType.TargetRunning: {
					eventData newEvent = new eventData();
					newEvent.eventType = "TargetRunning";
					newEvent.arg1 = thread.ID; 
					newEvent.arg2 = 0;
					newEvent.arg3 = "";
					eventList.Add(newEvent);
					break;
				}
				
				case TargetEventType.TargetInterrupted: {
					eventData newEvent = new eventData();
					newEvent.eventType = "TargetInterrupted";
					newEvent.arg1 = thread.ID;
					newEvent.arg2 = 0;
					newEvent.arg3 = "";
					eventList.Add(newEvent);
					break;
				}
				
				case TargetEventType.FrameChanged: {
					eventData newEvent = new eventData();
					newEvent.eventType = "FrameChanged";
					newEvent.arg1 = thread.ID; 
					newEvent.arg2 = 0;
					newEvent.arg3 = "";
					eventList.Add(newEvent);
					break;
				}
				
				case TargetEventType.TargetHitBreakpoint: {
					eventData newEvent = new eventData();
					newEvent.eventType = "TargetHitBreakpoint";
					newEvent.arg1 = thread.ID;
					newEvent.arg2 = (int)args.Data;
					newEvent.arg3 = "";

					eventList.Add(newEvent);
					break;
				}
				
				case TargetEventType.TargetSignaled: {
					eventData newEvent = new eventData();
					newEvent.eventType = "TargetSignaled";
					newEvent.arg1 = thread.ID;
					newEvent.arg2 = (int)args.Data;
					newEvent.arg3 = "";
					
					eventList.Add(newEvent);
					break;					
				}
				
				case TargetEventType.TargetExited: {
					eventData newEvent = new eventData();
					newEvent.eventType = "TargetExited";
					newEvent.arg1 = thread.ID;
					newEvent.arg2 = (int)args.Data;
					newEvent.arg3 = "";
					
					eventList.Add(newEvent);
					break;
				}
				
				case TargetEventType.Exception: {
					eventData newEvent = new eventData();
					newEvent.eventType = "Exception";
					newEvent.arg1 = thread.ID;
					newEvent.arg2 = 0;
					newEvent.arg3 = "";
					
					eventList.Add(newEvent);
					break;
				}
				
				case TargetEventType.UnhandledException: {
					eventData newEvent = new eventData();
					newEvent.eventType = "UnhandledException";
					newEvent.arg1 = thread.ID;
					newEvent.arg2 = 0;
					newEvent.arg3 = "";
					
					eventList.Add(newEvent);
					break;
				}
			}
			
			base.OnTargetEvent(thread,args);
		}
				
		protected override void OnThreadCreated (Mono.Debugger.Thread thread)
		{
			eventData newEvent = new eventData();
			newEvent.eventType = "OnThreadCreated";
			newEvent.arg1 = 0;
			newEvent.arg2 = 0;
			newEvent.arg3 = "";

			eventList.Add(newEvent);
			
			base.OnThreadCreated(thread);
		}
		
		protected override void OnThreadExited (Mono.Debugger.Thread thread)
		{
			if (thread != thread.Process.MainThread) {
				eventData newEvent = new eventData();
				newEvent.eventType = "OnThreadExited";
				newEvent.arg1 = 0;
				newEvent.arg2 = 0;
				newEvent.arg3 = "";
				
				eventList.Add(newEvent);
			}
			
			base.OnThreadExited(thread);
		}

		protected override void OnProcessCreated (Process process)
		{
			eventData newEvent = new eventData();
			newEvent.eventType = "OnProcessCreated";
			newEvent.arg1 = 0;
			newEvent.arg2 = 0;
			newEvent.arg3 = "";
				
			eventList.Add(newEvent);
			
			base.OnProcessCreated(process);
		}
		
		protected override void OnProcessExited (Process process)
		{
			eventData newEvent = new eventData();
			newEvent.eventType = "OnProcessExited";
			newEvent.arg1 = 0;
			newEvent.arg2 = 0;
			newEvent.arg3 = "";
				
			eventList.Add(newEvent);
			
			base.OnProcessExited(process);
		}
		
		protected override void OnProcessExecd (Process process)
		{
			eventData newEvent = new eventData();
			newEvent.eventType = "OnProcessExecd";
			newEvent.arg1 = 0;
			newEvent.arg2 = 0;
			newEvent.arg3 = "";
				
			eventList.Add(newEvent);
			
			base.OnProcessExecd(process);
		}
		
		protected override void OnTargetExited ()
		{
			eventData newEvent = new eventData();
			newEvent.eventType = "OnTargetExited";
			newEvent.arg1 = 0;
			newEvent.arg2 = 0;
			newEvent.arg3 = "";
				
			eventList.Add(newEvent);
			
			base.OnTargetExited();
		}
		
		protected override void OnTargetOutput (bool is_stderr, string line)
		{
			eventData newEvent = new eventData();
			newEvent.eventType = "OnTargetOutput";
			newEvent.arg1 = is_stderr ? 1 : 0;
			newEvent.arg2 = 0;
			newEvent.arg3 = line;
				
			eventList.Add(newEvent);
			
			base.OnTargetOutput(is_stderr,line);
		}
		
		protected override void OnMainProcessCreated (Process process)
		{
			eventData newEvent = new eventData();
			newEvent.eventType = "OnMainProcessCreated";
			newEvent.arg1 = newEvent.arg2 = 0;
			newEvent.arg3 = "";
			eventList.Add(newEvent);
			base.OnMainProcessCreated(process);			
		}
		
		public struct backtraceData {
			public int frameNumber;
			public bool currentFrame;
			public string method;
			public string file;
			public int lineNumber;
			public bool moreData;
		}
		
		public static backtraceData GetNextBacktraceLine() {
			backtraceData result;
			if (backtraceList.Count <= 0) {
				result = new backtraceData();
				result.frameNumber = -1; // flag invalid response
				result.currentFrame = false;
				result.method = "";
				result.file = "";
				result.lineNumber = 0;
				result.moreData = false;
				return result;
			}
			result = (backtraceData)backtraceList[0];
			backtraceList.RemoveAt(0);
			return result;
		}
		
		public static int GetBreakpointNumber() {
			breakpointSem.WaitOne();
			return breakpointNumber;
		}
		
		public struct printData {
			public string type;
			public string varValue;
			public string varNames;
		}
		
		public static printData GetPrintOutput() {
			printSem.WaitOne();
			return printOutput;
		}
		
		public static string GetPTypeOutput() {
			ptypeSem.WaitOne();
			return ptypeOutput;
		}
		
		public static string GetPTypeOutputStaticOnly() {
			ptypeSem.WaitOne();
			return ptypeOutputStaticOnly;
		}
		
		public struct threadData {
			public int processNumber;
			public int PID;
			public string processCmdLine;
			public int threadNumber;
			public bool daemonThread;
			public bool threadState;
			public bool currentThread;
			public int TID;
			public bool moreData;
		}
		
		public static threadData GetNextThreadLine() {
			threadData result;
			if (threadList.Count <= 0) {
				result = new threadData();
				result.processNumber = -1; // flag invalid response
				result.PID = -1;
				result.processCmdLine = "";
				result.threadNumber = -1;
				result.daemonThread = false;
				result.threadState = false;
				result.currentThread = false;
				result.TID = -1;
				result.moreData = false;
				return result;
			}
			result = (threadData)threadList[0];
			threadList.RemoveAt(0);
			return result;
		}
		
		public static string GetLocalsOutput() {
			localsSem.WaitOne();
			return localsOutput;
		}
		
		public static string GetParamsOutput() {
			paramsSem.WaitOne();
			return paramsOutput;
		}
		
		
		public static bool IsCurrentThreadStopped() {
			Mono.Debugger.Thread curThread;
			try {
				curThread = thisReference.CurrentThread;
			} catch  {
				return false;
			}
			if (curThread != null && curThread.IsStopped)
				return true;
			return false;
		}
	}
}
