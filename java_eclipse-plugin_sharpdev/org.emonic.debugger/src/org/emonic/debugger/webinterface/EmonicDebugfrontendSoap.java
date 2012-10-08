/**
 * EmonicDebugfrontendSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.emonic.debugger.webinterface;

public interface EmonicDebugfrontendSoap extends java.rmi.Remote {

    /**
     * Start MDB
     */
    public void startMdb(java.lang.String cmdLine) throws java.rmi.RemoteException;

    /**
     * Run application in MDB (not halting in Main)
     */
    public void runApp(java.lang.String workDir) throws java.rmi.RemoteException;

    /**
     * Get next Event
     */
    public org.emonic.debugger.webinterface.EventData getNextEvent() throws java.rmi.RemoteException;

    /**
     * MDB background command
     */
    public void background() throws java.rmi.RemoteException;

    /**
     * MDB backtrace command
     */
    public org.emonic.debugger.webinterface.BacktraceData backtrace(boolean execute) throws java.rmi.RemoteException;

    /**
     * MDB break command
     */
    public int _break(java.lang.String file, int lineNumber) throws java.rmi.RemoteException;

    /**
     * MDB delete command
     */
    public void delete(int breakpointNumber) throws java.rmi.RemoteException;

    /**
     * MDB disable command
     */
    public void disable(int breakpointNumber) throws java.rmi.RemoteException;

    /**
     * MDB enable command
     */
    public void enable(int breakpointNumber) throws java.rmi.RemoteException;

    /**
     * MDB finish command
     */
    public void finish() throws java.rmi.RemoteException;

    /**
     * MDB frame command
     */
    public void frame(int frameNumber) throws java.rmi.RemoteException;

    /**
     * MDB next command
     */
    public void next() throws java.rmi.RemoteException;

    /**
     * MDB print command
     */
    public org.emonic.debugger.webinterface.PrintData print(java.lang.String variableName) throws java.rmi.RemoteException;

    /**
     * MDB ptype command - fields only
     */
    public java.lang.String ptypeFieldsOnly(java.lang.String className, boolean staticOnly) throws java.rmi.RemoteException;

    /**
     * MDB quit command
     */
    public void quit() throws java.rmi.RemoteException;

    /**
     * MDB set X = Y command
     */
    public void setVariable(java.lang.String variable, java.lang.String value) throws java.rmi.RemoteException;

    /**
     * MDB show threads command
     */
    public org.emonic.debugger.webinterface.ThreadData getThreads(boolean execute) throws java.rmi.RemoteException;

    /**
     * MDB show locals command
     */
    public java.lang.String getLocals() throws java.rmi.RemoteException;

    /**
     * MDB show parameters command
     */
    public java.lang.String getParameters() throws java.rmi.RemoteException;

    /**
     * MDB step command
     */
    public void step() throws java.rmi.RemoteException;

    /**
     * MDB stop command
     */
    public void stop() throws java.rmi.RemoteException;

    /**
     * MDB thread command
     */
    public void thread(int threadNumber) throws java.rmi.RemoteException;
}
