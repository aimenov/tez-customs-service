/**
 * PIServiceLocator.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT)
 * WSDL2Java emitter.
 */

package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

public class PIServiceLocator extends org.apache.axis.client.Service implements PIService {

    public PIServiceLocator() {}


    public PIServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PIServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PIServicePort
    // private java.lang.String PIServicePort_address = "http://pi.kgd.gov.kz/ws/PIService/";
    private String PIServicePort_address = "http://212.154.167.54:8080/ws/PIService/";

    public String getPIServicePortAddress() {
        return PIServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private String PIServicePortWSDDServiceName = "PIServicePort";

    public String getPIServicePortWSDDServiceName() {
        return PIServicePortWSDDServiceName;
    }

    public void setPIServicePortWSDDServiceName(String name) {
        PIServicePortWSDDServiceName = name;
    }

    public PIServicePortType getPIServicePort() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PIServicePort_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPIServicePort(endpoint);
    }

    public PIServicePortType getPIServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            PIServiceBindingStub _stub = new PIServiceBindingStub(portAddress, this);
            _stub.setPortName(getPIServicePortWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPIServicePortEndpointAddress(String address) {
        PIServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given
     * interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (PIServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                PIServiceBindingStub _stub = new PIServiceBindingStub(new java.net.URL(PIServicePort_address), this);
                _stub.setPortName(getPIServicePortWSDDServiceName());
                return _stub;
            }
        } catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
                        + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given
     * interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
                    throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("PIServicePort".equals(inputPortName)) {
            return getPIServicePort();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://new.webservice.namespace", "PIService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://new.webservice.namespace", "PIServicePort"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

        if ("PIServicePort".equals(portName)) {
            setPIServicePortEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address)
                    throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
