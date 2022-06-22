package kz.ne.railways.tezcustoms.service.service;

import kz.ne.railways.tezcustoms.service.model.*;
import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;

import java.util.Date;
import java.util.List;

public interface ForDataService {

    FormData getContractData(String invNum);

    void saveContractData(Long id, FormData formData, List<VagonItem> vagonList, ContainerDatas containerDatas);

    void saveInvoiceData(FormData formData);

    void saveCustomsResponse(Long invoiceId, SaveDeclarationResponseType result, String uuid);

    void saveDocInfo(String invoiceId, String filename, Date date, String uuid);

    boolean checkExpeditorCode(Long code);

    FormData getFormData(String invoiceId);

}
