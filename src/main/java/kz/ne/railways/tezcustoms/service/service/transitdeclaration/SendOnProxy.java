package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class SendOnProxy {

    private static final Logger log = LoggerFactory.getLogger(SendOnProxy.class);

    private String proxyHost;
    private String proxyPort;

    private String customsUser;
    private String customsPassword;
    private String customsCode;

    private static final String userTemplate = "___user___";
    private static final String passwordTemplate = "___password___";
    private static final String customsCodeTemplate = "___customsCode___";
    private static final String xmlTemplate = "___xml___";
    private static final String requestBodyTemplate =
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:new=\"http://new.webservice.namespace\"><soap:Header/><soap:Body><new:SaveDeclaration><parameter><new:username>"
                                    + userTemplate + "</new:username><new:passwod>" + passwordTemplate
                                    + "</new:passwod><new:customsCode>" + customsCodeTemplate
                                    + "</new:customsCode><new:xml><![CDATA[" + xmlTemplate
                                    + "]]></new:xml></parameter></new:SaveDeclaration></soap:Body></soap:Envelope>";
    private static final String soapAction = "SaveDeclaration";
    // private static final String soapUrl = "http://pi.kgd.gov.kz/ws/PIService/";
    private static final String soapUrl = "http://212.154.167.54:8080/ws/PIService";
    // private static final String soapUrl = "http://212.154.167.54:8080/ws/PIService?wsdl";


    public SendOnProxy() {

    }

    public SendOnProxy(String customsUser, String customsPassword, String customsCode, String proxyHost,
                    String proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.customsUser = customsUser;
        this.customsPassword = customsPassword;
        this.customsCode = customsCode;
        // System.out.printf("proxy:%s\n",this.proxyHost);
    }


    public SaveDeclarationResponseType send(String xml) throws Exception {
        SaveDeclarationResponseType result = null;

        // System.out.println("inbound xml");
        System.out.println(xml);

        String sendXml = createRequestBody(xml);

        log.info(sendXml);
        System.out.println("Send xml");
        System.out.println(sendXml);
        String response = httpSend(sendXml);

        System.out.println("Response from service");
        System.out.println(response);
        log.info(response);

        result = toObject(response);

        return result;
    }

    private SaveDeclarationResponseType toObject(String response) throws Exception {
        SaveDeclarationResponseType result = null;
        String codeStr = null;
        String valueStr = null;
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getBytes("utf-8"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(stream);

        NodeList nodes = doc.getElementsByTagName("parameter");
        if (nodes != null && nodes.getLength() > 0) {
            Element parameter = (Element) nodes.item(0);
            nodes = parameter.getElementsByTagName("ns2:code");
            if (nodes != null && nodes.getLength() > 0) {
                Element code = (Element) nodes.item(0);
                codeStr = getCharacterDataFromElement(code);
            }
            nodes = parameter.getElementsByTagName("ns2:value");
            if (nodes != null && nodes.getLength() > 0) {
                Element value = (Element) nodes.item(0);
                valueStr = getCharacterDataFromElement(value);
            }
            if (codeStr != null && valueStr != null) {
                result = new SaveDeclarationResponseType();
                result.setCode(codeStr);
                result.setValue(valueStr);
            }
        }

        return result;
    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    private String httpSend(String sendXml) throws Exception {
        // StringEntity stringEntity = new StringEntity(sendXml, "UTF-8");
        HttpEntity stringEntity = new ByteArrayEntity(sendXml.getBytes("UTF-8"));
        // stringEntity.setChunked(true);
        HttpPost httpPost = new HttpPost(soapUrl);
        httpPost.setEntity(stringEntity);
        httpPost.addHeader("Accept-Encoding", "gzip,deflate");
        httpPost.addHeader("Accept", "text/xml");
        // httpPost.addHeader("SOAPAction", soapAction);
        httpPost.addHeader("Content-Type", "text/xml;charset=UTF-8");

        HttpClient httpClient = new DefaultHttpClient();
        // TODO: отключаем прокси для локальной проверки
        // if (proxyHost != null && !"".equals(proxyHost)) {
        // HttpHost proxy = new HttpHost(getProxyHost(), Integer.parseInt(getProxyPort()), "http");
        // httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        // }

        HttpResponse response = httpClient.execute(httpPost);
        System.out.println(response.toString());
        HttpEntity entity = response.getEntity();
        Header contentEncodingHeader = entity.getContentEncoding();

        if (contentEncodingHeader != null) {
            HeaderElement[] encodings = contentEncodingHeader.getElements();
            for (int i = 0; i < encodings.length; i++) {
                if (encodings[i].getName().equalsIgnoreCase("gzip")) {
                    entity = new GzipDecompressingEntity(entity);
                    break;
                }
            }
        }

        String strResponse = null;
        if (entity != null) {
            // -->
            // String inputLine ;
            // BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
            // try {
            // while ((inputLine = br.readLine()) != null) {
            // System.out.println(inputLine);
            // }
            // br.close();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            // <--
            strResponse = EntityUtils.toString(entity);
        }
        return strResponse;
    }

    private String createRequestBody(String xml) {
        return requestBodyTemplate.replace(userTemplate, (getCustomsUser() != null ? getCustomsUser() : ""))
                        .replace(passwordTemplate, (getCustomsPassword() != null ? getCustomsPassword() : ""))
                        .replace(customsCodeTemplate, (getCustomsCode() != null ? getCustomsCode() : ""))
                        .replace(xmlTemplate, (xml != null ? xml : ""));
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

    public String getCustomsUser() {
        return customsUser;
    }

    public void setCustomsUser(String customsUser) {
        this.customsUser = customsUser;
    }

    public String getCustomsPassword() {
        return customsPassword;
    }

    public void setCustomsPassword(String customsPassword) {
        this.customsPassword = customsPassword;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }


}
