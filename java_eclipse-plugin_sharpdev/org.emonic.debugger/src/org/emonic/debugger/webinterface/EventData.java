/**
 * EventData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.emonic.debugger.webinterface;

public class EventData  extends org.emonic.debugger.webinterface.ValueType  implements java.io.Serializable {
    
	private static final long serialVersionUID = 1L;

	private java.lang.String eventType;

    private int arg1;

    private int arg2;

    private java.lang.String arg3;

    public EventData() {
    }

    public EventData(
           java.lang.String eventType,
           int arg1,
           int arg2,
           java.lang.String arg3) {
        this.eventType = eventType;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }


    /**
     * Gets the eventType value for this EventData.
     * 
     * @return eventType
     */
    public java.lang.String getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this EventData.
     * 
     * @param eventType
     */
    public void setEventType(java.lang.String eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the arg1 value for this EventData.
     * 
     * @return arg1
     */
    public int getArg1() {
        return arg1;
    }


    /**
     * Sets the arg1 value for this EventData.
     * 
     * @param arg1
     */
    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }


    /**
     * Gets the arg2 value for this EventData.
     * 
     * @return arg2
     */
    public int getArg2() {
        return arg2;
    }


    /**
     * Sets the arg2 value for this EventData.
     * 
     * @param arg2
     */
    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }


    /**
     * Gets the arg3 value for this EventData.
     * 
     * @return arg3
     */
    public java.lang.String getArg3() {
        return arg3;
    }


    /**
     * Sets the arg3 value for this EventData.
     * 
     * @param arg3
     */
    public void setArg3(java.lang.String arg3) {
        this.arg3 = arg3;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventData)) return false;
        EventData other = (EventData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType()))) &&
            this.arg1 == other.getArg1() &&
            this.arg2 == other.getArg2() &&
            ((this.arg3==null && other.getArg3()==null) || 
             (this.arg3!=null &&
              this.arg3.equals(other.getArg3())));
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
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        _hashCode += getArg1();
        _hashCode += getArg2();
        if (getArg3() != null) {
            _hashCode += getArg3().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EventData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "eventData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "eventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arg1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "arg1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arg2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "arg2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arg3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "arg3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
