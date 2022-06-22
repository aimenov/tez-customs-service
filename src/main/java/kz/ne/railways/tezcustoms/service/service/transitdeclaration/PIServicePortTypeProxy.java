package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationRequestType;
import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;

public class PIServicePortTypeProxy implements PIServicePortType {
    private String _endpoint = null;
    private PIServicePortType pIServicePortType = null;

    public PIServicePortTypeProxy() {
        _initPIServicePortTypeProxy();
    }

    public PIServicePortTypeProxy(String endpoint) {
        _endpoint = endpoint;
        _initPIServicePortTypeProxy();
    }

    private void _initPIServicePortTypeProxy() {
        try {
            pIServicePortType = (new PIServiceLocator()).getPIServicePort();
            if (pIServicePortType != null) {
                if (_endpoint != null)
                    ((javax.xml.rpc.Stub) pIServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address",
                                    _endpoint);
                else
                    _endpoint = (String) ((javax.xml.rpc.Stub) pIServicePortType)
                                    ._getProperty("javax.xml.rpc.service.endpoint.address");
            }

        } catch (javax.xml.rpc.ServiceException serviceException) {
        }
    }

    public String getEndpoint() {
        return _endpoint;
    }

    public void setEndpoint(String endpoint) {
        _endpoint = endpoint;
        if (pIServicePortType != null)
            ((javax.xml.rpc.Stub) pIServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

    }

    public PIServicePortType getPIServicePortType() {
        if (pIServicePortType == null)
            _initPIServicePortTypeProxy();
        return pIServicePortType;
    }

    public SaveDeclarationResponseType saveDeclaration(SaveDeclarationRequestType parameter)
                    throws java.rmi.RemoteException {
        if (pIServicePortType == null)
            _initPIServicePortTypeProxy();
        return pIServicePortType.saveDeclaration(parameter);
    }


}
