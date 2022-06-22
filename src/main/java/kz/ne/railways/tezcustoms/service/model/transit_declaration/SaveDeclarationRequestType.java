/**
 * SaveDeclarationRequestType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT)
 * WSDL2Java emitter.
 */

package kz.ne.railways.tezcustoms.service.model.transit_declaration;


/**
 * Запрос на сожранение декларации в БД
 */
public class SaveDeclarationRequestType implements java.io.Serializable {
    /* Имя пользователя (login) */
    private String username;

    /* Пароль пользователя */
    private String password;

    /* Код таможенного органа */
    private String customsCode;

    /* Содержимое декларации */
    private String xml;

    public SaveDeclarationRequestType() {}

    public SaveDeclarationRequestType(String username, String password, String customsCode, String xml) {
        this.username = username;
        this.password = password;
        this.customsCode = customsCode;
        this.xml = xml;
    }


    /**
     * Gets the username value for this SaveDeclarationRequestType.
     * 
     * @return username * Имя пользователя (login)
     */
    public String getUsername() {
        return username;
    }


    /**
     * Sets the username value for this SaveDeclarationRequestType.
     * 
     * @param username * Имя пользователя (login)
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Gets the password value for this SaveDeclarationRequestType.
     * 
     * @return password * Пароль пользователя
     */
    public String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this SaveDeclarationRequestType.
     * 
     * @param password * Пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Gets the customsCode value for this SaveDeclarationRequestType.
     * 
     * @return customsCode * Код таможенного органа
     */
    public String getCustomsCode() {
        return customsCode;
    }


    /**
     * Sets the customsCode value for this SaveDeclarationRequestType.
     * 
     * @param customsCode * Код таможенного органа
     */
    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }


    /**
     * Gets the xml value for this SaveDeclarationRequestType.
     * 
     * @return xml * Содержимое декларации
     */
    public String getXml() {
        return xml;
    }


    /**
     * Sets the xml value for this SaveDeclarationRequestType.
     * 
     * @param xml * Содержимое декларации
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SaveDeclarationRequestType))
            return false;
        SaveDeclarationRequestType other = (SaveDeclarationRequestType) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.username == null && other.getUsername() == null)
                        || (this.username != null && this.username.equals(other.getUsername())))
                        && ((this.password == null && other.getPassword() == null)
                                        || (this.password != null && this.password.equals(other.getPassword())))
                        && ((this.customsCode == null && other.getCustomsCode() == null) || (this.customsCode != null
                                        && this.customsCode.equals(other.getCustomsCode())))
                        && ((this.xml == null && other.getXml() == null)
                                        || (this.xml != null && this.xml.equals(other.getXml())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getUsername() != null) {
            _hashCode += getUsername().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getCustomsCode() != null) {
            _hashCode += getCustomsCode().hashCode();
        }
        if (getXml() != null) {
            _hashCode += getXml().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
                    new org.apache.axis.description.TypeDesc(SaveDeclarationRequestType.class, true);

    static {
        typeDesc.setXmlType(
                        new javax.xml.namespace.QName("http://new.webservice.namespace", "SaveDeclarationRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("username");
        elemField.setXmlName(new javax.xml.namespace.QName("http://new.webservice.namespace", "username"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("http://new.webservice.namespace", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customsCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://new.webservice.namespace", "customsCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xml");
        elemField.setXmlName(new javax.xml.namespace.QName("http://new.webservice.namespace", "xml"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
    public static org.apache.axis.encoding.Serializer getSerializer(String mechType, Class _javaType,
                    javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(String mechType, Class _javaType,
                    javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

}
