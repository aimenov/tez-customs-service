/**
 * PIServicePortType.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT)
 * WSDL2Java emitter.
 */

package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationRequestType;
import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;

public interface PIServicePortType extends java.rmi.Remote {
    public SaveDeclarationResponseType saveDeclaration(SaveDeclarationRequestType parameter)
                    throws java.rmi.RemoteException;
}
