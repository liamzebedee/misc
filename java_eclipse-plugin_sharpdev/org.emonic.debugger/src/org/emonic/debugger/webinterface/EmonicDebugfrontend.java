/**
 * EmonicDebugfrontend.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.emonic.debugger.webinterface;

public interface EmonicDebugfrontend extends javax.xml.rpc.Service {

/**
 * A web service that offers an alternative front-end to the Mono
 * Debugger (v0.60).
 */
    public java.lang.String getEmonicDebugfrontendSoapAddress();

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap getEmonicDebugfrontendSoap() throws javax.xml.rpc.ServiceException;

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap getEmonicDebugfrontendSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getEmonicDebugfrontendSoap12Address();

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12 getEmonicDebugfrontendSoap12() throws javax.xml.rpc.ServiceException;

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12 getEmonicDebugfrontendSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
