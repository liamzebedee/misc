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
 *      Bernhard Brem - Basic implementation of a web service
 * 		Harald Krapfenbauer - Expansion of available web functions
 */


using System;
using System.IO;
using System.Web.Services;

using Mono.Debugger;
using Mono.Debugger.Frontend;
using Mono.Debugger.Backend;
using System.Collections;

namespace Emonic.Debugfrontend {
	
	[WebService(Namespace="http://debugfrontend.emonic.org/",Name="Emonic.Debugfrontend",
	Description="A web service that offers an alternative front-end to the Mono Debugger (v0.60).")]
	class Webservice: System.Web.Services.WebService {
		
		[WebMethod(Description = "Start MDB")]
		public void StartMdb(string cmdLine) {
			string[] args = cmdLine.Split(' ');
			DebuggerConfiguration config = new DebuggerConfiguration ();
			config.LoadConfiguration ();
			DebuggerOptions options = DebuggerOptions.ParseCommandLine (args);
			System.Console.WriteLine ("Mono Debugger");
			EmonicLineInterpreter interpreter = new EmonicLineInterpreter (config, options);
			interpreter.RunMainLoop();
			// we don't want an automatic breakpoint in Main(), and we want to be able
			// to set a breakpoint there, so we delete the automatic one
			EmonicLineInterpreter.AddCmd("delete 1");
		}
		
		[WebMethod(Description = "Run application in MDB (not halting in Main)")]
		public void RunApp(string workDir) {
			if (workDir != null)
				EmonicLineInterpreter.AddCmd("cd "+workDir);
			EmonicLineInterpreter.AddCmd("run");			
		}
		
		[WebMethod(Description = "Get next Event")]
		public EmonicInterpreter.eventData GetNextEvent() {
			return EmonicInterpreter.GetNextEvent();
		}
		
		[WebMethod(Description = "MDB background command")]
		public void Background() {
			EmonicLineInterpreter.AddCmd("background");
		}
		
		[WebMethod(Description = "MDB backtrace command")]
		public EmonicInterpreter.backtraceData Backtrace(bool execute) {
			if (execute) {
				EmonicInterpreter.backtraceList.Clear(); // remove all previous elements from the list				
				EmonicLineInterpreter.AddCmd("backtrace");
			}
			EmonicInterpreter.backtraceData btd = EmonicInterpreter.GetNextBacktraceLine();
			return btd;
		}
		
		[WebMethod(Description = "MDB break command")]
		public int Break(string file, int lineNumber) {
			EmonicLineInterpreter.AddCmd("break "+file+":"+lineNumber);
			int result = EmonicInterpreter.GetBreakpointNumber();
			return result;
		}
		
		[WebMethod(Description = "MDB delete command")]
		public void Delete(int breakpointNumber) {
			EmonicLineInterpreter.AddCmd("delete "+breakpointNumber);		
		}
		
		[WebMethod(Description = "MDB disable command")]
		public void Disable(int breakpointNumber) {
			EmonicLineInterpreter.AddCmd("disable "+breakpointNumber);		
		}
		
		[WebMethod(Description = "MDB enable command")]
		public void Enable(int breakpointNumber) {
			EmonicLineInterpreter.AddCmd("enable "+breakpointNumber);		
		}
		
		[WebMethod(Description = "MDB finish command")]
		public void Finish() {
			EmonicLineInterpreter.AddCmd("finish");		
		}
		
		[WebMethod(Description = "MDB frame command")]
		public void Frame(int frameNumber) {
			EmonicLineInterpreter.AddCmd("frame "+frameNumber);		
		}

		[WebMethod(Description = "MDB next command")]
		public void Next() {
			EmonicLineInterpreter.AddCmd("next");		
		}
		
		[WebMethod(Description = "MDB print command")]
		public EmonicInterpreter.printData Print(string variableName) {
			if (!EmonicInterpreter.IsCurrentThreadStopped()) {
				// would block if current thread is not stopped!
				EmonicInterpreter.printData error = new EmonicInterpreter.printData();
				error.type = error.varValue = error.varNames = "";
				return error;
			}
			EmonicLineInterpreter.AddCmd("print "+variableName);
			EmonicInterpreter.printData pd = EmonicInterpreter.GetPrintOutput();
			return pd;
		}
		
		[WebMethod(Description = "MDB ptype command - fields only")]
		public string PtypeFieldsOnly(string className, bool staticOnly) {
			if (!EmonicInterpreter.IsCurrentThreadStopped()) {
				// would block if current thread is not stopped!
				return "--";
			}
			EmonicLineInterpreter.AddCmd("ptype "+className);
			string result;
			if (staticOnly)
				result = EmonicInterpreter.GetPTypeOutputStaticOnly();
			else
				result = EmonicInterpreter.GetPTypeOutput();
			return result;
		}
		
		[WebMethod(Description = "MDB quit command")]
		public void Quit() {
			EmonicLineInterpreter.AddCmd("quit");
		}
		
		[WebMethod(Description = "MDB set X = Y command")]
		public void SetVariable(string variable, string value) {
			EmonicLineInterpreter.AddCmd("set "+variable+" = "+value);
		}
		
		[WebMethod(Description = "MDB show threads command")]
		public EmonicInterpreter.threadData GetThreads(bool execute) {
			if (execute) {
				EmonicInterpreter.threadList.Clear(); // remove all previous elements from the list
				EmonicLineInterpreter.AddCmd("show threads");
			}
			EmonicInterpreter.threadData td = EmonicInterpreter.GetNextThreadLine();
			return td;
		}
		
		[WebMethod(Description = "MDB show locals command")]
		public string GetLocals() {
			if (!EmonicInterpreter.IsCurrentThreadStopped()) {
				// would block if current thread is not stopped!
				return "--";
			}
			EmonicLineInterpreter.AddCmd("show locals");
			string result = EmonicInterpreter.GetLocalsOutput();
			return result;
		}
		
		[WebMethod(Description = "MDB show parameters command")]
		public string GetParameters() {
			if (!EmonicInterpreter.IsCurrentThreadStopped()) {
				// would block if current thread is not stopped!
				return "--";
			}
			EmonicLineInterpreter.AddCmd("show parameters");
			string result = EmonicInterpreter.GetParamsOutput();
			return result;
		}

		[WebMethod(Description = "MDB step command")]
		public void Step() {
			EmonicLineInterpreter.AddCmd("step");
		}
		
		[WebMethod(Description = "MDB stop command")]
		public void Stop() {
			EmonicLineInterpreter.AddCmd("stop");
		}
		
		[WebMethod(Description = "MDB thread command")]
		public void Thread(int threadNumber) {
			EmonicLineInterpreter.AddCmd("thread "+threadNumber);
		}
	}
}
