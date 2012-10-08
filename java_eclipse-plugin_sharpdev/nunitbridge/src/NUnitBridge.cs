/*=============================================================
(c) all rights reserved
================================================================*/

using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using NUnit.Core;
using NUnit.Util;

namespace EMonic.NUnit
{
		/// <summary>
		/// An EventListener implementation for sending information about the
		/// tests being run to a Java socket that was initiated by the Eclipse
		/// NUnit plug-in.
		/// </summary>
	class NUnitBridge : MarshalByRefObject, EventListener
	{
		private static ASCIIEncoding ENCODING = new ASCIIEncoding();
	    
	    private Stream stream;

	    public NUnitBridge(int port)
	    {
			// create a new TcpClient to talk to the Java socket
			TcpClient client = new TcpClient("localhost", port);
			// get the stream for sending information
	        stream = client.GetStream();
	    }
	    
	    public void Close()
	    {
	        stream.Close();
	    }
		
		public void RunStarted(Test[] tests)
		{
			
		}
		
		public void RunStarted(TestSuite suite)
		{
			
		}
		
		public void SuiteStarted(TestSuite suite)
		{
			
		}
		
		public void SuiteFinished(TestSuiteResult result)
		{
			
		}
		
		public void RunFinished(TestResult[] results)
		{
			
		}
		
		public void RunFinished(Exception e)
		{
			
		}
		
		public void TestStarted(TestCase testCase)
		{
			byte[] buffer = ENCODING.GetBytes("TestStarted " + testCase.FullName);
			stream.Write(buffer, 0, buffer.Length);
			stream.Flush();
		}
		
		public void TestFinished(TestCaseResult result)
		{
			ITest test = result.Test;
			if (result.IsSuccess)
			{
				byte[] buffer = ENCODING.GetBytes("TestFinished " + test.FullName);
				stream.Write(buffer, 0, buffer.Length);
			}
			else if (result.Message.IndexOf("thrown") != -1)
			{
				byte[] buffer = ENCODING.GetBytes("TestCrashed " + test.FullName + ' ' + result.StackTrace);
				stream.Write(buffer, 0, buffer.Length);
			}
			else
			{
				byte[] buffer = ENCODING.GetBytes("TestFailed " + test.FullName + ' ' + result.StackTrace);
				stream.Write(buffer, 0, buffer.Length);
			}
			stream.Flush();
		}
		
		public void UnhandledException(Exception e)
		{
		}
	    
		public static void Main(string[] args)
		{
			NUnitBridge listener = null;
			try {
				listener = new NUnitBridge(Int32.Parse(args[0]));
				TestDomain domain = new TestDomain();
				domain.Load(args[1]);
				domain.Run(listener);
				listener.Close();
			} finally {
				if (listener != null)
				{
					listener.Close();
				}
			}
		}
	}
	
}
