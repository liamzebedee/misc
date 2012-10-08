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


using System;
using System.Xml;
using System.Text;
using System.IO;
using System.Threading;
using System.Reflection;
using System.Collections;
using System.Globalization;
using System.Text.RegularExpressions;
using Mono.Debugger;
using Mono.Debugger.Languages;
using Mono.Debugger.Frontend;

namespace Emonic.Debugfrontend
{
	public class EmonicDebuggerEngine : Mono.Debugger.Frontend.DebuggerEngine
	{
		public EmonicDebuggerEngine (EmonicInterpreter interpreter)
			: base (interpreter)
		{
			RegisterCommand ("background", typeof (BackgroundThreadCommand));
			RegisterCommand ("backtrace", typeof (EmonicBacktraceCommand));
			RegisterCommand ("break", typeof (EmonicBreakCommand));
			RegisterCommand ("delete", typeof (BreakpointDeleteCommand));
			RegisterCommand ("disable", typeof (BreakpointDisableCommand));
			RegisterCommand ("enable", typeof (BreakpointEnableCommand));
			RegisterCommand ("finish", typeof (FinishCommand));
			RegisterCommand ("frame", typeof (SelectFrameCommand));
			RegisterCommand ("next", typeof (NextCommand));
			RegisterCommand ("print", typeof (EmonicPrintExpressionCommand));
			RegisterCommand ("ptype", typeof (EmonicPrintTypeCommand));
			RegisterCommand ("quit", typeof (EmonicQuitCommand));
			RegisterCommand ("set", typeof (SetCommand));
			RegisterCommand ("show", typeof (EmonicShowCommand));
			RegisterCommand ("start", typeof (StartCommand));
			RegisterCommand ("step", typeof (StepCommand));
			RegisterCommand ("stop", typeof (StopThreadCommand));
			RegisterCommand ("thread", typeof (SelectThreadCommand));
		}
	}


	public class EmonicPrintExpressionCommand : PrintExpressionCommand
	{
		
		// copied that from PrintCommand class - we need that here because we need
		// DoResolve() method
		Expression expression;
		DisplayFormat format = DisplayFormat.Object;
		
		// copied that from PrintCommand class - we need that here because we need
		// to know if any exceptions in this method are raised
		protected override bool DoResolve (ScriptingContext context)
		{
			// whole method in a try-catch block - if any exceptions occur, we are able to generate
			// an invalid web service response and release the semaphore
			try {
				if (Argument.StartsWith ("/")) {
					int pos = Argument.IndexOfAny (new char[] { ' ', '\t' });
					string fstring = Argument.Substring (1, pos-1);
					string arg = Argument.Substring (pos + 1);
					
					switch (fstring) {
						case "o":
						case "object":
						format = DisplayFormat.Object;
						break;
						
						case "a":
						case "address":
						format = DisplayFormat.Address;
						break;
						
						case "x":
						case "hex":
						format = DisplayFormat.HexaDecimal;
						break;
						
						case "default":
						format = DisplayFormat.Default;
						break;
						
						default:
						throw new ScriptingException (
						                              "Unknown format: `{0}'", fstring);
					}
					
					expression = DoParseExpression (context, arg);
				} else
					expression = ParseExpression (context);
				
				if (expression == null)
					return false;
				
				expression = expression.Resolve (context);
				return expression != null;
			} catch {
				EmonicInterpreter.printData output = new EmonicInterpreter.printData();
				output.type = "";
				output.varValue = "";
				output.varNames = "";
				EmonicInterpreter.printOutput = output;
				EmonicInterpreter.printSem.Release();
				throw;
			}
		}
		
		protected override object DoExecute (ScriptingContext context)
		{
			return Execute (context, expression, format);
		}
		
		protected override string Execute (ScriptingContext context,
		                                   Expression expression, DisplayFormat format)
		{
			try {
				if (expression is TypeExpression)
					throw new ScriptingException (
					                              "`{0}' is a type, not a variable.", expression.Name);
				object retval = expression.Evaluate (context);
				
				EmonicInterpreter.printData output = new EmonicInterpreter.printData();
				output.type = "";
				output.varValue = "";
				output.varNames = "";
				
				if (retval != null) {
					output.type = ((TargetObject)retval).TypeName;
					if (output.type == null)
						output.type = "";
					
					switch (((TargetObject)retval).Kind) {
						case TargetObjectKind.Fundamental:
						// we send the value of the fundamental type
						TargetFundamentalObject tfo = retval as TargetFundamentalObject;
						if (tfo == null) {
							// element is "null"							
							output.varValue = "<null>";
							break;
						}
						output.varValue = tfo.GetObject(context.CurrentThread).ToString();
						if (output.varValue == null)
							output.varValue = "";	
						break;
						case TargetObjectKind.Array:
						// we send back the number of array elements
						TargetArrayObject tao = retval as TargetArrayObject;
						if (tao == null) {
							// element is "null"
							output.varValue = "<null>";
							break;
						}
						int lower = tao.GetLowerBound (context.CurrentThread, 0);
						int upper = tao.GetUpperBound (context.CurrentThread, 0);
						output.varNames = (upper-lower).ToString();
						break;
						// same for struct and class
						case TargetObjectKind.Struct:
						case TargetObjectKind.Class:
						// we send back the member's names
						// NOTE! we also show static and constant fields
						TargetObject obj = retval as TargetObject;
						if (obj.HasAddress && obj.GetAddress(context.CurrentThread).IsNull) {
							output.varValue = "<null>";
							break;
						}
						Mono.Debugger.Thread thread = context.CurrentThread;
						TargetClass tc = ((TargetClassObject)retval).Type.GetClass(thread);
						if (tc == null)
							break;
						TargetFieldInfo[] tfi = tc.GetFields(thread);
						if (tfi == null)
							break;
						output.varNames = "";
						for (int i=0; i<tfi.Length; i++) {
							if (tfi[i].IsStatic) // do not show static fields, they're not accessible via the instance!
								continue;							
							output.varNames += tfi[i].Name;
							output.varNames += " "; 
						}
						output.varNames = output.varNames.Trim();
						break;
						case TargetObjectKind.Object:
						case TargetObjectKind.Pointer:
						case TargetObjectKind.Unknown:
						case TargetObjectKind.Function:
						case TargetObjectKind.Alias:
						case TargetObjectKind.Enum:
						context.Print("ERROR: Print Command will return no values because of an implementation error");
						break;
					}
				}
				string text = context.FormatObject (retval, format);
				context.Print (text);
				
				EmonicInterpreter.printOutput = output;
				EmonicInterpreter.printSem.Release();
				
				return text;
			} catch {
				EmonicInterpreter.printData output = new EmonicInterpreter.printData();
				output.type = "";
				output.varValue = "";
				output.varNames = "";
				EmonicInterpreter.printOutput = output;
				EmonicInterpreter.printSem.Release();
				throw;
			}
		}
	}

	public class EmonicPrintTypeCommand : PrintTypeCommand
	{
		// copied from the PrintCommand class
		Expression expression;
		DisplayFormat format = DisplayFormat.Object;
		
		// copied from the PrintCommand class - we must be able to catch Exceptions here
		protected override bool DoResolve (ScriptingContext context)
		{
			// big try-catch block: we are able to react to Exceptions this way
			// and release the semaphore waiting for the command answer
			try {
				if (Argument.StartsWith ("/")) {
					int pos = Argument.IndexOfAny (new char[] { ' ', '\t' });
					string fstring = Argument.Substring (1, pos-1);
					string arg = Argument.Substring (pos + 1);
					
					switch (fstring) {
						case "o":
						case "object":
						format = DisplayFormat.Object;
						break;
						
						case "a":
						case "address":
						format = DisplayFormat.Address;
						break;
						
						case "x":
						case "hex":
						format = DisplayFormat.HexaDecimal;
						break;
						
						case "default":
						format = DisplayFormat.Default;
						break;
						
						default:
						throw new ScriptingException (
						                              "Unknown format: `{0}'", fstring);
					}
					
					expression = DoParseExpression (context, arg);
				} else
					expression = ParseExpression (context);
				
				if (expression == null)
					return false;
				
				if (this is PrintTypeCommand) {
					Expression resolved = expression.TryResolveType (context);
					if (resolved != null) {
						expression = resolved;
						return true;
					}
				}
				
				expression = expression.Resolve (context);
				return expression != null;
			} catch {
				EmonicInterpreter.ptypeOutput = "--";
				EmonicInterpreter.ptypeOutputStaticOnly = "--";
				EmonicInterpreter.ptypeSem.Release();
				throw;
			}
		}
		
		// copied from PrintCommand class
		protected override object DoExecute (ScriptingContext context)
		{
			return Execute (context, expression, format);
		}


		protected override string Execute (ScriptingContext context,
		                                   Expression expression, DisplayFormat format)
		{
			// try-catch block for whole method: if an exception occurs, we are able to 
			// release the semaphore waiting for the command answer
			try {
				TargetType type = expression.EvaluateType (context);
				
				string fieldNames = "";
				string fieldNamesStaticOnly = "";
				if (type.Kind == TargetObjectKind.Class || type.Kind == TargetObjectKind.Struct) {
					TargetClassType stype = (TargetClassType) type;
					foreach (TargetFieldInfo field in stype.Fields) {
						fieldNames += field.Name;
						fieldNames += " ";
						if (field.IsStatic) {
							fieldNamesStaticOnly += field.Name;
							fieldNamesStaticOnly += " ";
						}			
					}
					fieldNames = fieldNames.Trim();
					fieldNamesStaticOnly = fieldNamesStaticOnly.Trim();
				}
				string text = context.FormatType (type);
				context.Print (text);
				
				EmonicInterpreter.ptypeOutput = fieldNames;
				EmonicInterpreter.ptypeOutputStaticOnly = fieldNamesStaticOnly;
				EmonicInterpreter.ptypeSem.Release();
				
				return text;
			} catch {
				EmonicInterpreter.ptypeOutput = "--";
				EmonicInterpreter.ptypeOutputStaticOnly = "--";
				EmonicInterpreter.ptypeSem.Release();
				throw;
			}
		}
	}

	public class EmonicBacktraceCommand : BacktraceCommand
	{
		int max_frames = -1;
		Backtrace.Mode mode = Backtrace.Mode.Default;

		public new int Max {
			get { return max_frames; }
			set { max_frames = value; }
		}

		public new bool Native {
			get { return mode == Backtrace.Mode.Native; }
			set { mode = Backtrace.Mode.Native; }
		}

		public new bool Managed {
			get { return mode == Backtrace.Mode.Managed; }
			set { mode = Backtrace.Mode.Managed; }
		}

		protected override object DoExecute (ScriptingContext context)
		{
			Backtrace backtrace = null;

			if ((mode == Backtrace.Mode.Default) && (max_frames == -1))
				backtrace = CurrentThread.CurrentBacktrace;

			if (backtrace == null)
				backtrace = CurrentThread.GetBacktrace (mode, max_frames);

			for (int i = 0; i < backtrace.Count; i++) {
				string prefix = i == backtrace.CurrentFrameIndex ? "(*)" : "   ";
				context.Print ("{0} {1}", prefix, backtrace [i]);
				
				EmonicInterpreter.backtraceData bt = new EmonicInterpreter.backtraceData();
				bt.frameNumber = backtrace[i].Level;
				bt.currentFrame = i == backtrace.CurrentFrameIndex;
				if (backtrace[i].Method != null) {
					bt.method = backtrace[i].Method.Name;
					if (bt.method == null)
						bt.method = "";
				} else {
					if (backtrace[i].Name == null)
						bt.method = "";
					else {					
						bt.method = backtrace[i].Name.ToString();
						if (bt.method == null)
							bt.method = "";
					}
				}
				if (backtrace[i].SourceAddress != null && backtrace[i].SourceAddress.SourceFile != null)
					bt.file = backtrace[i].SourceAddress.SourceFile.FileName;
				else
					bt.file = "";
				if (backtrace[i].SourceAddress != null)
					bt.lineNumber = backtrace[i].SourceAddress.Row;
				else
					bt.lineNumber = -1;
				if (i+1 < backtrace.Count)
					bt.moreData = true;
				else
					bt.moreData = false;
				EmonicInterpreter.backtraceList.Add(bt);
			}

			return backtrace;
		}
	}


	public class EmonicQuitCommand : QuitCommand
	{
		protected override bool DoResolve (ScriptingContext context)
		{
			// do not query "yes or no?", simply quit			
			return true;
		}
	}


	public class EmonicShowCommand : NestedCommand
	{
		private class EmonicShowThreadsCommand : DebuggerCommand
		{
			protected override object DoExecute (ScriptingContext context)
			{
				int current_id = -1;
				if (context.Interpreter.HasCurrentThread)
					current_id = context.Interpreter.CurrentThread.ID;
				
				bool printed_something = false;
				Process[] processes = context.Interpreter.Processes;
				for (int i=0; i<processes.Length; i++) {
					context.Print ("Process {0}:",
					               context.Interpreter.PrintProcess (processes[i]));
					
					Mono.Debugger.Thread[] threads = processes[i].GetThreads();
					for (int j=0; j<threads.Length; j++) {
						string prefix = threads[j].ID == current_id ? "(*)" : "   ";
						context.Print ("{0} {1} ({2}:{3:x}) {4}", prefix, threads[j],
						               threads[j].PID, threads[j].TID, threads[j].State);
						printed_something = true;
						
						EmonicInterpreter.threadData td = new EmonicInterpreter.threadData();
						td.processNumber = processes[i].ID;
						td.PID = processes[i].MainThread.PID;
						td.processCmdLine = context.Interpreter.PrintCommandLineArgs(processes[i]);
						td.threadNumber = threads[j].ID;
						if ((threads[j].ThreadFlags & Mono.Debugger.Thread.Flags.Daemon) != 0)
							td.daemonThread = true;
						else
							td.daemonThread = false;
						td.threadState = threads[j].State != Mono.Debugger.TargetState.Stopped;
						td.currentThread = threads[j].ID == current_id;
						td.TID = threads[j].PID; // we take the PID of the thread here, not really the TID
						td.moreData = j+1<threads.Length || i+1<processes.Length;
						
						EmonicInterpreter.threadList.Add(td);
					}
				}
				
				if (!printed_something) {
					context.Print ("No target.");
					
					// generate an invalid response					
					EmonicInterpreter.threadData td = new EmonicInterpreter.threadData();
					td.processNumber = -1;
					td.PID = -1;
					td.processCmdLine = "";
					td.threadNumber = -1;
					td.daemonThread = false;
					td.threadState = false;
					td.currentThread = false;
					td.TID = -1;
					td.moreData = false;
					EmonicInterpreter.threadList.Add(td);
				}
				return null;
			}
		}
		
		private class EmonicShowParametersCommand : FrameCommand
		{
			TargetVariable[] param_vars;
			
			protected override bool DoResolve (ScriptingContext context)
			{
				// catch exceptions and release the semaphore in case of one
				try {
					if (CurrentFrame.Method == null)
						throw new ScriptingException (
						                              "Selected stack frame has no method.");
					
					param_vars = CurrentFrame.Method.GetParameters (context.CurrentThread);
					return true;
				} catch {
					EmonicInterpreter.paramsOutput = "--";
					EmonicInterpreter.paramsSem.Release();
					throw;
				}
			}
			
			protected override object DoExecute (ScriptingContext context)
			{
				// catch exceptions and release the semaphore in case of one
				try {
					EmonicInterpreter.paramsOutput = "";
					foreach (TargetVariable var in param_vars) {
						
						if (!var.IsInScope (CurrentFrame.TargetAddress)) {
							continue;
						}
						
						EmonicInterpreter.paramsOutput += var.Name;
						EmonicInterpreter.paramsOutput += " ";
						
						string msg = context.Interpreter.Style.PrintVariable (var, CurrentFrame);
						context.Interpreter.Print (msg);
					}
					EmonicInterpreter.paramsOutput = EmonicInterpreter.paramsOutput.Trim();
					EmonicInterpreter.paramsSem.Release();
					return null;
				} catch {
					EmonicInterpreter.paramsOutput = "--";
					EmonicInterpreter.paramsSem.Release();
					throw;
				}
			}
		}
		
		private class EmonicShowLocalsCommand : FrameCommand
		{
			TargetVariable[] locals;
			
			protected override bool DoResolve (ScriptingContext context)
			{
				// try-catch: on exceptions, we must release the semaphore
				try {
					if (CurrentFrame.Method == null)
						throw new ScriptingException (
						                              "Selected stack frame has no method.");
					
					locals = CurrentFrame.Method.GetLocalVariables (context.CurrentThread);
					return true;
				} catch {
					EmonicInterpreter.localsOutput = "--";
					EmonicInterpreter.localsSem.Release();
					throw;	
				}
			}
			
			protected override object DoExecute (ScriptingContext context)
			{
				// try-catch: on exceptions, we must release the semaphore
				try {
					EmonicInterpreter.localsOutput = "";
					foreach (TargetVariable var in locals) {
						
						if (!var.IsInScope (CurrentFrame.TargetAddress)) {
							continue;
						}

						EmonicInterpreter.localsOutput += var.Name;
						EmonicInterpreter.localsOutput += " ";
						
						string msg = context.Interpreter.Style.PrintVariable (var, CurrentFrame);
						context.Interpreter.Print (msg);
					}
					EmonicInterpreter.localsOutput = EmonicInterpreter.localsOutput.Trim();
					EmonicInterpreter.localsSem.Release();
					return null;
				} catch {
					EmonicInterpreter.localsOutput = "--";
					EmonicInterpreter.localsSem.Release();
					throw;	
				}
			}
		}
		
		public EmonicShowCommand ()
		{
			RegisterSubcommand ("threads", typeof (EmonicShowThreadsCommand));
			RegisterSubcommand ("locals", typeof (EmonicShowLocalsCommand));
			RegisterSubcommand ("parameters", typeof (EmonicShowParametersCommand));
		}
	}

	
	public class EmonicBreakCommand : BreakCommand
	{
		SourceLocation location;
		LocationType type = LocationType.Default;
		TargetAddress address = TargetAddress.Null;
		int p_index = -1, t_index = -1, f_index = -1;
		string group;
		bool global, local;
		bool lazy;
		int domain = 0;
		ThreadGroup tgroup;

		public new string Group {
			get { return group; }
			set { group = value; }
		}

		public new bool Global {
			get { return global; }
			set { global = value; }
		}

		public new bool Local {
			get { return local; }
			set { local = value; }
		}

		public new bool Lazy {
			get { return lazy; }
			set { lazy = true; }
		}

		public new int Domain {
			get { return domain; }
			set { domain = value; }
		}

		public new bool Ctor {
			get { return type == LocationType.Constructor; }
			set { type = LocationType.Constructor; }
		}

		public new bool Get {
			get { return type == LocationType.PropertyGetter; }
			set { type = LocationType.PropertyGetter; }
		}

		public new bool Set {
			get { return type == LocationType.PropertySetter; }
			set { type = LocationType.PropertySetter; }
		}

		public new bool Add {
			get { return type == LocationType.EventAdd; }
			set { type = LocationType.EventAdd; }
		}

		public new bool Remove {
			get { return type == LocationType.EventRemove; }
			set { type = LocationType.EventRemove; }
		}

		public new bool Invoke {
			get { return type == LocationType.DelegateInvoke; }
			set { type = LocationType.DelegateInvoke; }
		}

		public new int Process {
			get { return p_index; }
			set { p_index = value; }
		}

		public new int Thread {
			get { return t_index; }
			set { t_index = value; }
		}

		public new int Frame {
			get { return f_index; }
			set { f_index = value; }
		}


		protected override bool DoResolve (ScriptingContext context)
		{
			// set whole method in a try-catch block: if anything goes wrong, an invalid breakpoint
			// number is set and the semaphore is released.
			try {
				if (global) {
					if (local)
						throw new ScriptingException (
						                              "Cannot use both -local and -global.");
					
					if (Group != null)
						throw new ScriptingException (
						                              "Cannot use both -group and -global.");
					
					tgroup = ThreadGroup.Global;
				} else if (local) {
					if (Group != null)
						throw new ScriptingException (
						                              "Cannot use both -group and -local.");
					
					tgroup = context.Interpreter.GetThreadGroup (Group, false);
				} else if (Group != null) {
					tgroup = context.Interpreter.GetThreadGroup (Group, false);
				} else {
					tgroup = ThreadGroup.Global;
				}
				
				if (context.Interpreter.HasTarget) {
					context.CurrentProcess = context.Interpreter.GetProcess (p_index);
					context.CurrentThread = context.Interpreter.GetThread (t_index);
					
					Mono.Debugger.Thread thread = context.CurrentThread;
					if (!thread.IsStopped)
						throw new Mono.Debugger.TargetException (TargetError.NotStopped);
					
					Backtrace backtrace = thread.GetBacktrace ();
					
					StackFrame frame;
					if (f_index == -1)
						frame = backtrace.CurrentFrame;
					else {
						if (f_index >= backtrace.Count)
							throw new ScriptingException (
							                              "No such frame: {0}", f_index);
						
						frame = backtrace [f_index];
					}
					
					context.CurrentFrame = frame;
					
					if (ExpressionParser.ParseLocation (
					                                    thread, thread.CurrentFrame, Argument, out location))
					return true;
				}
				
				if (Argument.IndexOf (':') > 0)
					return true;
				
				try {
					UInt32.Parse (Argument);
					return true;
				} catch {
				}
				
				Expression expr = context.Interpreter.ExpressionParser.Parse (Argument);
				if (expr == null)
					throw new ScriptingException ("Cannot resolve expression `{0}'.", Argument);
				
				if (expr is PointerExpression) {
					address = ((PointerExpression) expr).EvaluateAddress (context);
					return true;
				}
				
				if (!context.Interpreter.HasTarget)
					return true;
				
				MethodExpression mexpr;
				try {
					mexpr = expr.ResolveMethod (context, type);
				} catch {
					if (lazy)
						return true;
					throw new ScriptingException ("No such method: `{0}'.", Argument);
				}
				
				if (mexpr != null)
					location = mexpr.EvaluateSource (context);
				else
					location = context.FindMethod (Argument);
				
				if (lazy)
					return true;
				
				if (location == null)
					throw new ScriptingException ("No such method: `{0}'.", Argument);
				
				return true;
			} catch {
				EmonicInterpreter.breakpointNumber = -1;
				EmonicInterpreter.breakpointSem.Release();
				throw;
			}
		}


		protected override object DoExecute (ScriptingContext context)
		{
			Event handle;

			if (!address.IsNull) {
				handle = context.Interpreter.Session.InsertBreakpoint (
					context.CurrentThread, tgroup, address);
				context.Print ("Breakpoint {0} at {1}", handle.Index, address);
			} else if (location != null) {
				// line breakpoints use this method!
				handle = context.Interpreter.Session.InsertBreakpoint (
					tgroup, location);
				context.Print ("Breakpoint {0} at {1}", handle.Index, location.Name);
			} else {
				handle = context.Interpreter.Session.InsertBreakpoint (
					tgroup, type, Argument);
				context.Print ("Breakpoint {0} at {1}", handle.Index, Argument);
			}

			if (!context.Interpreter.HasTarget) {
				EmonicInterpreter.breakpointNumber = handle.Index;
				EmonicInterpreter.breakpointSem.Release();
				return handle.Index;
			}
			
			try {
				handle.Activate (context.Interpreter.CurrentThread);
			} catch {
				if (!lazy) {
					EmonicInterpreter.breakpointNumber = -1;
					EmonicInterpreter.breakpointSem.Release();
					throw;
				}
			}

			EmonicInterpreter.breakpointNumber = handle.Index;
			EmonicInterpreter.breakpointSem.Release();

			return handle.Index;
		}
	}
}
