package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;
import ru.customs.information.customsdocuments.esadout_cu._5_11.ESADoutCUType;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.PIRWInformationCUType;

public interface ICustomsReqcuestDecarartion {

    SaveDeclarationResponseType send(String user, String password, String customsCode, String request);

    SaveDeclarationResponseType send(String user, String password, String customsCode,
                    PIRWInformationCUType request) throws Exception;

    String objectToString(PIRWInformationCUType request);

    SaveDeclarationResponseType sendTD(String user, String password, String customsCode, String request);

    SaveDeclarationResponseType sendTD(String user, String password, String customsCode, ESADoutCUType request)
                    throws Exception;

    String objectToStringTD(ESADoutCUType request);

    void setProxyHost(String proxyHost);

    void setProxyPort(String proxyPort);

}
