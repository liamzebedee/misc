/**
 * PrintData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.emonic.debugger.webinterface;

public class PrintData  extends org.emonic.debugger.webinterface.ValueType  implements java.io.Serializable {
   
	private static final long serialVersionUID = 1L;

	private java.lang.String type;

    private java.lang.String varValue;

    private java.lang.String varNames;

    public PrintData() {
    }

    public PrintData(
           java.lang.String type,
           java.lang.String varValue,
           java.lang.String varNames) {
        this.type = type;
        this.varValue = varValue;
        this.varNames = varNames;
    }


    /**
     * Gets the type value for this PrintData.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this PrintData.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the varValue value for this PrintData.
     * 
     * @return varValue
     */
    public java.lang.String getVarValue() {
        return varValue;
    }


    /**
     * Sets the varValue value for this PrintData.
     * 
     * @param varValue
     */
    public void setVarValue(java.lang.String varValue) {
        this.varValue = varValue;
    }


    /**
     * Gets the varNames value for this PrintData.
     * 
     * @return varNames
     */
    public java.lang.String getVarNames() {
        return varNames;
    }


    /**
     * Sets the varNames value for this PrintData.
     * 
     * @param varNames
     */
    public void setVarNames(java.lang.String varNames) {
        this.varNames = varNames;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PrintData)) return false;
        PrintData other = (PrintData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.varValue==null && other.getVarValue()==null) || 
             (this.varValue!=null &&
              this.varValue.equals(other.getVarValue()))) &&
            ((this.varNames==null && other.getVarNames()==null) || 
             (this.varNames!=null &&
              this.varNames.equals(other.getVarNames())));
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getVarValue() != null) {
            _hashCode += getVarValue().hashCode();
        }
        if (getVarNames() != null) {
            _hashCode += getVarNames().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PrintData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "printData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "varValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varNames");
        elemField.setXmlName(new javax.xml.namespace.QName("http://debugfrontend.emonic.org/", "varNames"));
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
