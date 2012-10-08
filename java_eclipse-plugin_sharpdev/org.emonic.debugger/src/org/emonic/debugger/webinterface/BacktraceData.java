/**
 * BacktraceData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.emonic.debugger.webinterface;

public class BacktraceData  extends org.emonic.debugger.webinterface.ValueType  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int frameNumber;

    private boolean currentFrame;

    private java.lang.String method;

    private java.lang.String file;

    private int lineNumber;

    private boolean moreData;

    public BacktraceData() {
    }

    public BacktraceData(
           int frameNumber,
           boolean currentFrame,
           java.lang.String method,
           java.lang.String file,
           int lineNumber,
           boolean moreData) {
        this.frameNumber = frameNumber;
        this.currentFrame = currentFrame;
        this.method = method;
        this.file = file;
        this.lineNumber = lineNumber;
        this.moreData = moreData;
    }


    /**
     * Gets the frameNumber value for this BacktraceData.
     * 
     * @return frameNumber
     */
    public int getFrameNumber() {
        return frameNumber;
    }


    /**
     * Sets the frameNumber value for this BacktraceData.
     * 
     * @param frameNumber
     */
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }


    /**
     * Gets the currentFrame value for this BacktraceData.
     * 
     * @return currentFrame
     */
    public boolean isCurrentFrame() {
        return currentFrame;
    }


    /**
     * Sets the currentFrame value for this BacktraceData.
     * 
     * @param currentFrame
     */
    public void setCurrentFrame(boolean currentFrame) {
        this.currentFrame = currentFrame;
    }


    /**
     * Gets the method value for this BacktraceData.
     * 
     * @return method
     */
    public java.lang.String getMethod() {
        return method;
    }


    /**
     * Sets the method value for this BacktraceData.
     * 
     * @param method
     */
    public void setMethod(java.lang.String method) {
        this.method = method;
    }


    /**
     * Gets the file value for this BacktraceData.
     * 
     * @return file
     */
    public java.lang.String getFile() {
        return file;
    }


    /**
     * Sets the file value for this BacktraceData.
     * 
     * @param file
     */
    public void setFile(java.lang.String file) {
        this.file = file;
    }


    /**
     * Gets the lineNumber value for this BacktraceData.
     * 
     * @return lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }


    /**
     * Sets the lineNumber value for this BacktraceData.
     * 
     * @param lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    /**
     * Gets the moreData value for this BacktraceData.
     * 
     * @return moreData
     */
    public boolean isMoreData() {
        return moreData;
    }


    /**
     * Sets the moreData value for this BacktraceData.
     * 
     * @param moreData
     */
    public void setMoreData(boolean moreData) {
        this.moreData = moreData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BacktraceData)) return false;
        BacktraceData other = (BacktraceData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.frameNumber == other.getFrameNumber() &&
            this.currentFrame == other.isCurrentFrame() &&
            ((this.method==null && other.getMethod()==null) || 
             (this.method!=null &&
              this.method.equals(other.getMethod()))) &&
            ((this.file==null && other.getFile()==null) || 
             (this.file!=null &&
              this.file.equals(other.getFile()))) &&
            this.lineNumber == other.getLineNumber() &&
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
        _hashCode += getFrameNumber();
        _hashCode += (isCurrentFrame() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getMethod() != null) {
            _hashCode += getMethod().hashCode();
        }
        if (getFile() != null) {
            _hashCode += getFile().hashCode();
        }
        _hashCode += getLineNumber();
        _hashCode += (isMoreData() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BacktraceData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "backtraceData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("frameNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "frameNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentFrame");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "currentFrame"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("method");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "method"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "file"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lineNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "lineNumber"));
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
