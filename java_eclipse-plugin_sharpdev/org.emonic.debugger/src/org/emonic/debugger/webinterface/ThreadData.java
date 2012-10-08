/**
 * ThreadData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.emonic.debugger.webinterface;

public class ThreadData  extends org.emonic.debugger.webinterface.ValueType  implements java.io.Serializable {
    
	private static final long serialVersionUID = 1L;

	private int processNumber;

    private int PID;

    private java.lang.String processCmdLine;

    private int threadNumber;

    private boolean daemonThread;

    private boolean threadState;

    private boolean currentThread;

    private int TID;

    private boolean moreData;

    public ThreadData() {
    }

    public ThreadData(
           int processNumber,
           int PID,
           java.lang.String processCmdLine,
           int threadNumber,
           boolean daemonThread,
           boolean threadState,
           boolean currentThread,
           int TID,
           boolean moreData) {
        this.processNumber = processNumber;
        this.PID = PID;
        this.processCmdLine = processCmdLine;
        this.threadNumber = threadNumber;
        this.daemonThread = daemonThread;
        this.threadState = threadState;
        this.currentThread = currentThread;
        this.TID = TID;
        this.moreData = moreData;
    }


    /**
     * Gets the processNumber value for this ThreadData.
     * 
     * @return processNumber
     */
    public int getProcessNumber() {
        return processNumber;
    }


    /**
     * Sets the processNumber value for this ThreadData.
     * 
     * @param processNumber
     */
    public void setProcessNumber(int processNumber) {
        this.processNumber = processNumber;
    }


    /**
     * Gets the PID value for this ThreadData.
     * 
     * @return PID
     */
    public int getPID() {
        return PID;
    }


    /**
     * Sets the PID value for this ThreadData.
     * 
     * @param PID
     */
    public void setPID(int PID) {
        this.PID = PID;
    }


    /**
     * Gets the processCmdLine value for this ThreadData.
     * 
     * @return processCmdLine
     */
    public java.lang.String getProcessCmdLine() {
        return processCmdLine;
    }


    /**
     * Sets the processCmdLine value for this ThreadData.
     * 
     * @param processCmdLine
     */
    public void setProcessCmdLine(java.lang.String processCmdLine) {
        this.processCmdLine = processCmdLine;
    }


    /**
     * Gets the threadNumber value for this ThreadData.
     * 
     * @return threadNumber
     */
    public int getThreadNumber() {
        return threadNumber;
    }


    /**
     * Sets the threadNumber value for this ThreadData.
     * 
     * @param threadNumber
     */
    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }


    /**
     * Gets the daemonThread value for this ThreadData.
     * 
     * @return daemonThread
     */
    public boolean isDaemonThread() {
        return daemonThread;
    }


    /**
     * Sets the daemonThread value for this ThreadData.
     * 
     * @param daemonThread
     */
    public void setDaemonThread(boolean daemonThread) {
        this.daemonThread = daemonThread;
    }


    /**
     * Gets the threadState value for this ThreadData.
     * 
     * @return threadState
     */
    public boolean isThreadState() {
        return threadState;
    }


    /**
     * Sets the threadState value for this ThreadData.
     * 
     * @param threadState
     */
    public void setThreadState(boolean threadState) {
        this.threadState = threadState;
    }


    /**
     * Gets the currentThread value for this ThreadData.
     * 
     * @return currentThread
     */
    public boolean isCurrentThread() {
        return currentThread;
    }


    /**
     * Sets the currentThread value for this ThreadData.
     * 
     * @param currentThread
     */
    public void setCurrentThread(boolean currentThread) {
        this.currentThread = currentThread;
    }


    /**
     * Gets the TID value for this ThreadData.
     * 
     * @return TID
     */
    public int getTID() {
        return TID;
    }


    /**
     * Sets the TID value for this ThreadData.
     * 
     * @param TID
     */
    public void setTID(int TID) {
        this.TID = TID;
    }


    /**
     * Gets the moreData value for this ThreadData.
     * 
     * @return moreData
     */
    public boolean isMoreData() {
        return moreData;
    }


    /**
     * Sets the moreData value for this ThreadData.
     * 
     * @param moreData
     */
    public void setMoreData(boolean moreData) {
        this.moreData = moreData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ThreadData)) return false;
        ThreadData other = (ThreadData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.processNumber == other.getProcessNumber() &&
            this.PID == other.getPID() &&
            ((this.processCmdLine==null && other.getProcessCmdLine()==null) || 
             (this.processCmdLine!=null &&
              this.processCmdLine.equals(other.getProcessCmdLine()))) &&
            this.threadNumber == other.getThreadNumber() &&
            this.daemonThread == other.isDaemonThread() &&
            this.threadState == other.isThreadState() &&
            this.currentThread == other.isCurrentThread() &&
            this.TID == other.getTID() &&
            this.moreData == other.isMoreData();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        _hashCode += getProcessNumber();
        _hashCode += getPID();
        if (getProcessCmdLine() != null) {
            _hashCode += getProcessCmdLine().hashCode();
        }
        _hashCode += getThreadNumber();
        _hashCode += (isDaemonThread() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isThreadState() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCurrentThread() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getTID();
        _hashCode += (isMoreData() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ThreadData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "threadData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "processNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "PID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processCmdLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "processCmdLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("threadNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "threadNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("daemonThread");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "daemonThread"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("threadState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "threadState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentThread");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "currentThread"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("TID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "TID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moreData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "moreData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
