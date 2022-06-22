package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;
import lombok.extern.slf4j.Slf4j;
import ru.customs.information.customsdocuments.esadout_cu._5_11.ESADoutCUType;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.ObjectFactory;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.PIRWInformationCUType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Slf4j
public class CustomsRequestDeclaration implements ICustomsReqcuestDecarartion {

    private String proxyHost;
    private String proxyPort;

    public SaveDeclarationResponseType send(String user, String password, String customsCode, String request) {
        SaveDeclarationResponseType response = null;
        PIService service = new PIServiceLocator();

        PIServicePortType port = null;

        // Костыль для убирания &amp; и &
        request = request.replaceAll("&amp;", " and ");
        request = request.replaceAll("&", " and ");

        log.debug("После костыля");
        log.debug(request);

        try {
            SendOnProxy sender = new SendOnProxy(user, password, customsCode, proxyHost, proxyPort);
            response = sender.send(request);
            // org.apache.cxf.endpoint.Client
        } catch (Exception e) {
            response = new SaveDeclarationResponseType();
            response.setCode("1001");
            response.setValue("Сервис временно недоступен. Пожалуйста, повторите запрос позднее.("
                            + e.getLocalizedMessage() + ")");
            log.error(e.getLocalizedMessage(), e);
        }

        return response;
    }

    public SaveDeclarationResponseType sendTD(String user, String password, String customsCode, String request) {
        SaveDeclarationResponseType response = null;
        PIService service = new PIServiceLocator();

        PIServicePortType port = null;

        // Костыль для убирания &amp; и &
        request = request.replaceAll("&amp;", " and ");
        request = request.replaceAll("&", " and ");

        log.debug("После костыля");
        log.debug(request);

        try {
            SendOnProxy sender = new SendOnProxy(user, password, customsCode, proxyHost, proxyPort);
            response = sender.send(request);
            // org.apache.cxf.endpoint.Client
        } catch (Exception e) {
            e.printStackTrace(System.out);
            response = new SaveDeclarationResponseType();
            response.setCode("1001");
            response.setValue("Сервис временно недоступен. Пожалуйста, повторите запрос позднее.("
                            + e.getLocalizedMessage() + ")");
            log.error(e.getLocalizedMessage(), e);
        }

        return response;
    }

    public SaveDeclarationResponseType send(String user, String password, String customsCode,
                    PIRWInformationCUType request) throws Exception {
        return send(user, password, customsCode, objectToString(request));
    }

    public SaveDeclarationResponseType sendTD(String user, String password, String customsCode, ESADoutCUType request)
                    throws Exception {
        return sendTD(user, password, customsCode, objectToStringTD(request));
    }

    public String objectToString(PIRWInformationCUType request) {
        String result = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PIRWInformationCUType.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter sw = new StringWriter();
            ObjectFactory objectFactory = new ObjectFactory();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new PINamespacePrefixMapper());
            marshaller.marshal(objectFactory.createPIRWInformationCU(request), sw);
            result = sw.toString();
            // .replaceAll("ns2","PIRWCU");
            // .replaceAll("ns3","catESAD_cu");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        log.debug(result);
        return result;
    }

    public String objectToStringTD(ESADoutCUType request) {
        String result = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ESADoutCUType.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            ru.customs.information.customsdocuments.esadout_cu._5_11.ObjectFactory ob =
                            new ru.customs.information.customsdocuments.esadout_cu._5_11.ObjectFactory();
            StringWriter sw = new StringWriter();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new PINamespacePrefixMapper());
            marshaller.marshal(ob.createESADoutCU(request), sw);
            result = sw.toString().replaceAll("ns2", "ESADout_CU").replaceAll("ns3", "catESAD_cu");
            // .replaceAll("5.8.0", "5.10.0");
            // result = sw.toString();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        log.debug(result);
        return result;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }


}
