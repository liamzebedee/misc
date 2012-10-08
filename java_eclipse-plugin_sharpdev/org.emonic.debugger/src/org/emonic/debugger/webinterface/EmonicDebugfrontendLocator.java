/**
 * EmonicDebugfrontendLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.emonic.debugger.webinterface;

public class EmonicDebugfrontendLocator extends org.apache.axis.client.Service implements org.emonic.debugger.webinterface.EmonicDebugfrontend {


	private static final long serialVersionUID = 1L;

/**
 * A web service that offers an alternative front-end to the Mono
 * Debugger (v0.60).
 */

    public EmonicDebugfrontendLocator() {
    }


    public EmonicDebugfrontendLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EmonicDebugfrontendLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EmonicDebugfrontendSoap
    private java.lang.String EmonicDebugfrontendSoap_address = "http://localhost:8080/EmonicDebuggingService.asmx";

    public java.lang.String getEmonicDebugfrontendSoapAddress() {
        return EmonicDebugfrontendSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EmonicDebugfrontendSoapWSDDServiceName = "Emonic.DebugfrontendSoap";

    public java.lang.String getEmonicDebugfrontendSoapWSDDServiceName() {
        return EmonicDebugfrontendSoapWSDDServiceName;
    }

    public void setEmonicDebugfrontendSoapWSDDServiceName(java.lang.String name) {
        EmonicDebugfrontendSoapWSDDServiceName = name;
    }

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap getEmonicDebugfrontendSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EmonicDebugfrontendSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEmonicDebugfrontendSoap(endpoint);
    }

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap getEmonicDebugfrontendSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.emonic.debugger.webinterface.EmonicDebugfrontendSoapStub _stub = new org.emonic.debugger.webinterface.EmonicDebugfrontendSoapStub(portAddress, this);
            _stub.setPortName(getEmonicDebugfrontendSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEmonicDebugfrontendSoapEndpointAddress(java.lang.String address) {
        EmonicDebugfrontendSoap_address = address;
    }


    // Use to get a proxy class for EmonicDebugfrontendSoap12
    private java.lang.String EmonicDebugfrontendSoap12_address = "http://localhost:8080/EmonicDebuggingService.asmx";

    public java.lang.String getEmonicDebugfrontendSoap12Address() {
        return EmonicDebugfrontendSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EmonicDebugfrontendSoap12WSDDServiceName = "Emonic.DebugfrontendSoap12";

    public java.lang.String getEmonicDebugfrontendSoap12WSDDServiceName() {
        return EmonicDebugfrontendSoap12WSDDServiceName;
    }

    public void setEmonicDebugfrontendSoap12WSDDServiceName(java.lang.String name) {
        EmonicDebugfrontendSoap12WSDDServiceName = name;
    }

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12 getEmonicDebugfrontendSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EmonicDebugfrontendSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEmonicDebugfrontendSoap12(endpoint);
    }

    public org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12 getEmonicDebugfrontendSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12Stub _stub = new org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12Stub(portAddress, this);
            _stub.setPortName(getEmonicDebugfrontendSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEmonicDebugfrontendSoap12EndpointAddress(java.lang.String address) {
        EmonicDebugfrontendSoap12_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.emonic.debugger.webinterface.EmonicDebugfrontendSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.emonic.debugger.webinterface.EmonicDebugfrontendSoapStub _stub = new org.emonic.debugger.webinterface.EmonicDebugfrontendSoapStub(new java.net.URL(EmonicDebugfrontendSoap_address), this);
                _stub.setPortName(getEmonicDebugfrontendSoapWSDDServiceName());
                return _stub;
            }
            if (org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12.class.isAssignableFrom(serviceEndpointInterface)) {
                org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12Stub _stub = new org.emonic.debugger.webinterface.EmonicDebugfrontendSoap12Stub(new java.net.URL(EmonicDebugfrontendSoap12_address), this);
                _stub.setPortName(getEmonicDebugfrontendSoap12WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Emonic.DebugfrontendSoap".equals(inputPortName)) {
            return getEmonicDebugfrontendSoap();
        }
        else if ("Emonic.DebugfrontendSoap12".equals(inputPortName)) {
            return getEmonicDebugfrontendSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "Emonic.Debugfrontend");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "Emonic.DebugfrontendSoap"));
            ports.add(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "Emonic.DebugfrontendSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EmonicDebugfrontendSoap".equals(portName)) {
            setEmonicDebugfrontendSoapEndpointAddress(address);
        }
        else 
if ("EmonicDebugfrontendSoap12".equals(portName)) {
            setEmonicDebugfrontendSoap12EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
