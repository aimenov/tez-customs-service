package kz.ne.railways.tezcustoms.service.service.impl;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kz.ne.railways.tezcustoms.service.model.FormData;
import kz.ne.railways.tezcustoms.service.service.ContractsService;
import kz.ne.railways.tezcustoms.service.service.SftpService;
import kz.ne.railways.tezcustoms.service.service.ForDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ContractsServiceImpl implements ContractsService {

    @Value("${services.external.gateway.contractData.url}")
    private String gatewayContractDataUrl;

    @Value("${services.external.gateway.contractDoc.url}")
    private String gatewayContractDocUrl;

    private final ForDataService forDataService;
    private final SftpService fileServer;
    private final Gson gson = new Gson();

    @Override
    public FormData loadContract(String expCode, String invoiceNum, int year, int month){
        FormData formData = getContractData(expCode, invoiceNum, year, month);

        if (formData != null) {
            forDataService.saveContractData(-1L, formData, formData.getVagonList(), formData.getContainerDatas());
            byte[] arr = getContractDoc(formData.getInvoiceId());
            saveDocIntoFtp(arr, formData.getInvoiceId());
        }

        return formData;
    }

    public byte[] getContractDoc(String invoiceId) {
        // TODO may be WebClient
        String url = gatewayContractDocUrl + "&invoiceId=" + invoiceId;

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("charset", "UTF-8");
        httpGet.addHeader("Accept-Charset", "UTF-8");

        CloseableHttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200) {
                if (entity != null) {

                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    entity.getContent().transferTo(result);
                    return result.toByteArray();
                }
            }
        } catch (RuntimeException e) {
            log.error("RuntimeException in checkNaturalPersonFromStatApi: ", e);
        } catch (Exception e) {
            log.error("Exception in checkNaturalPersonFromStatApi: ", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    log.error("IOException during closing in checkNaturalPersonFromStatApi: ", e);
                }
            }
        }
        return null;
    }

    public FormData getContractData(String expCode, String invoiceNum, int year, int month) {
        String url = gatewayContractDataUrl;
//        String url = "http://localhost:8078/servlet?method=getContractData";
        if (expCode != null)
            url += "&expCode=" + expCode;
        url += "&invoiceNum=" + invoiceNum;
        url += "&year=" + year;
        url += "&month=" + month;

        log.debug(url);

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept-Encoding", "gzip, deflate, br");
        CloseableHttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200) {

                String strResponse;
                if (entity != null) {
                    strResponse = EntityUtils.toString(entity, "UTF-8");
                    return gson.fromJson(strResponse, FormData.class);
                }

            }
        } catch (RuntimeException e) {
            log.error("RuntimeException in checkNaturalPersonFromStatApi: ", e);
        } catch (Exception e) {
            log.error("Exception in checkNaturalPersonFromStatApi: ", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    log.error("IOException during closing in checkNaturalPersonFromStatApi: ", e);
                }
            }
        }
        return null;
    }

    void saveDocIntoFtp(byte[] file, String invoiceId){
        String docName = "Invoice Document";
        String filename = UUID.randomUUID().toString();

        try {
            if (fileServer.sendInvoice(new ByteArrayInputStream(file), filename, invoiceId))
                forDataService.saveDocInfo(invoiceId, docName, new Date(), filename);
        } catch (JSchException | SftpException | FileNotFoundException e) {
            log.debug("in loadContract" + e.getMessage());
        }
    }
}
