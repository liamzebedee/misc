/*
 * Created on 30.07.2006
 * emonic.base org.emonic.base.helpers DebugTimer.java
 */
package org.emonic.base.helpers;

import java.util.Date;

public class DebugTimer{
		public DebugTimer(String name){
			startTime=0;
			totalTime=0;
            calls=0;
            this.name=name;
		}
		
		long startTime;
		int calls;
		long totalTime;
		String name;
		
		public void Start(){
	       Date d = new Date();
	       startTime=d.getTime();
		}
       
		public void Stop(){
			Date d = new Date();
		    totalTime=d.getTime()-startTime;
		    calls ++;
		}
		
		public void Output(boolean printRealy){
			String result = "Timer: " + name + " Total Time: " + totalTime + " Calls: " + calls;
			if (calls !=0) result+= " Average: " + totalTime/calls;
			DebugUtil.DebugPrint(result,printRealy);
		}
}
