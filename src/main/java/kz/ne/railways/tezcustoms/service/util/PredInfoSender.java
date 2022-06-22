package kz.ne.railways.tezcustoms.service.util;

import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;
import kz.ne.railways.tezcustoms.service.service.transitdeclaration.CustomsRequestDeclaration;
import kz.ne.railways.tezcustoms.service.service.transitdeclaration.ICustomsReqcuestDecarartion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.customs.information.customsdocuments.esadout_cu._5_11.ESADoutCUType;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.PIRWInformationCUType;

@Component
@Slf4j
public class PredInfoSender implements PredInfoSenderLocal {

    @Value("${services.external.astana1.user}")
    private String user;
    @Value("${services.external.astana1.password}")
    private String password;
    @Value("${services.external.astana1.customsCode}")
    private String customsCode;
    @Value("${services.external.astana1.port}")
    private String proxyPort;
    @Value("${services.external.astana1.host}")
    private String proxyHost;

    private final ICustomsReqcuestDecarartion service = new CustomsRequestDeclaration();

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }


    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }


    public SaveDeclarationResponseType send(String user, String password, String customsCode,
                    PIRWInformationCUType data) {
        try {
            service.setProxyHost(proxyHost);
            service.setProxyPort(proxyPort);
            return service.send(user, password, customsCode, data);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return getError();
    }

    public SaveDeclarationResponseType sendTD(String user, String password, String customsCode, ESADoutCUType data) {
        try {
            service.setProxyHost(proxyHost);
            service.setProxyPort(proxyPort);
            return service.sendTD(user, password, customsCode, data);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return getError();
    }

    public static SaveDeclarationResponseType getError() {
        SaveDeclarationResponseType result = new SaveDeclarationResponseType();
        result.setCode("-1");
        result.setValue("Server Error");
        return result;
    }

    public SaveDeclarationResponseType send(PIRWInformationCUType data) {
        return send(user, password, customsCode, data);
    }

    public SaveDeclarationResponseType sendTD(ESADoutCUType data) {
        return sendTD(user, password, customsCode, data);
    }

    public String getXml(PIRWInformationCUType value) {
        return service.objectToString(value);
    }
}
