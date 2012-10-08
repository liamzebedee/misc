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
*		Bernhard Brem - Initial implementation
* 		Harald Krapfenbauer - Adapted to MDB v0.60 (Minor changes)
*/


using System;
using Mono.Debugger;
using Mono.Debugger.Frontend;
using ST = System.Threading;
using System.Collections;
using System.Runtime.InteropServices;


namespace Emonic.Debugfrontend {
	
	class EmonicLineInterpreter {
		Interpreter interpreter;
		DebuggerEngine engine;
		LineParser parser;
		ST.Thread command_thread; 
		ST.Thread main_thread;
		
		private static ArrayList commands = System.Collections.ArrayList.Synchronized(new ArrayList());
		public static ST.Semaphore commandsSem = new ST.Semaphore(0,Int32.MaxValue);
		
		[DllImport("monodebuggerserver")]
		static extern int mono_debugger_server_get_pending_sigint ();
		[DllImport("monodebuggerserver")]
		static extern int mono_debugger_server_static_init ();
		
		static EmonicLineInterpreter()
		{
			mono_debugger_server_static_init ();
		}
		
		public EmonicLineInterpreter (DebuggerConfiguration config, DebuggerOptions options)
		{
			if (options.HasDebugFlags)
				Report.Initialize (options.DebugOutput, options.DebugFlags);
			else
				Report.Initialize ();
			
			interpreter = new EmonicInterpreter (config, options);
			engine = interpreter.DebuggerEngine;
			parser = new LineParser (engine);
			main_thread = new ST.Thread (new System.Threading.ThreadStart(main_thread_main));
			main_thread.IsBackground = true;
			
			command_thread = new ST.Thread (new ST.ThreadStart (command_thread_main));
			command_thread.IsBackground = true;
		} 
		
		public void MainLoop ()
		{
			string s;
			bool is_complete = true;
			
			parser.Reset ();
			while ((s = ReadInput (is_complete)) != null) {
				System.Console.WriteLine("*** Command: " +s );
				parser.Append (s);
				if (parser.IsComplete ()){
					interpreter.ClearInterrupt ();
					parser.Execute ();
					parser.Reset ();
					is_complete = true;
				} else
					is_complete = false;
			}
		}
		
		void main_thread_main ()
		{
			while (true) {
				try {
					interpreter.ClearInterrupt ();
					MainLoop ();
				} catch (ST.ThreadAbortException) {
					ST.Thread.ResetAbort ();
				}
			}
		}
		
		public void RunMainLoop ()
		{
			command_thread.Start ();
			
			try {
				if (interpreter.Options.StartTarget)
					interpreter.Start ();
				
				main_thread.Start ();
			} catch (ScriptingException ex) {
				interpreter.Error (ex);
			} catch (TargetException ex) {
				interpreter.Error (ex);
			} catch (Exception ex) {
				interpreter.Error ("ERROR: {0}", ex);
			} 
		}
		
		public string ReadInput (bool is_complete) {
			commandsSem.WaitOne();
			string result = (string)commands[0];
			commands.RemoveAt(0);
			return result;
		}
		
		void command_thread_main ()
		{
			do {
				Mono.Debugger.Backend.Semaphore.Wait();
				if (mono_debugger_server_get_pending_sigint () == 0)
					continue;
				
				if (interpreter.Interrupt () > 2)
					main_thread.Abort ();
			} while (true);
		}
		
		public static void AddCmd(string cmd) {
			commands.Add(cmd);
			commandsSem.Release();
		}
	}
}
