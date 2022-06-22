package kz.ne.railways.tezcustoms.service.service.bean;

import kz.ne.railways.tezcustoms.service.entity.asudkr.*;
import kz.ne.railways.tezcustoms.service.model.DataCaneVagInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PrevInfoBeanDAOLocal {

    Integer existLikeInvoiceByNumAndSta(Long invoiceId);

    Country getCountry(String countryCode);

    Country getCountry(Long countryUn);

    NeCustomsOrgs getCustomsOrgs(Long customOrgUn);

    CurrencyCode getCurrencyCode(long curCodeUn);

    List<CurrencyCode> getCurrencyCodeList(Date date);

    Country getCountryByManagNo(int managNo);

    Country getCountryByManagUn(Long managUn);

    Country getCountryBycode(String countryCode);

    Management getManagement(Long managUn);

    Map<String, DataCaneVagInfo> getDatacaneVagInfo(List<String> dataCaneVagons);

    List<NeSmgsTnVed> getGridDatas(Long invoiceUn);

    List<NeVagonLists> getVagonList(Long invoiceUn);

    NeInvoice getInvoice(Long invoiceUn);

    NeInvcRefPi getNeInvcRefPi(final Long invoiceId);

    NeSmgsCargo getNeSmgsCargo(Long invoiceUn);

    NeInvoicePrevInfo getInvoicePrevInfo(Long invoiceUn);

    NeSmgsSenderInfo getSenderInfo(Long invoiceUn);

    Sta getStation(String stationCode);

    String getStationName(String stationCode, boolean onlyName);

    List<NeSmgsTnVedDocuments> getSmgsTnVedDocuments(Long tnVedId);

    List<NeCurrencyRates> getRates(Date date);

    NeSmgsRecieverInfo getRecieverInfo(Long invoiceUn);

    NeSmgsDestinationPlaceInfo getNeSmgsDestinationPlaceInfo(Long invoiceUn);

    List<NeSmgsTnVed> getTnVedList(Long invoiceUn);

    NeVagonGroup getVagonGroup(Long id);

    NeSmgsTnVedDocuments getSmgsTnVedDocumentsById(Long id);

    NeSmgsDeclarantInfo getDeclarantInfo(Long invoiceUn);

    NeSmgsExpeditorInfo getExpeditorInfo(Long invoiceUn);

    List<NeContainerLists> getContinerList(Long id);

    NeSmgsShipList getNeSmgsShipList(Long invoiceUn);

    List<NeUnitType> getUnitType();

    NePersonCategoryType getPersonCategoryType(Long categoryType);

    NeVessel getVessel(Long un);

    void updatePrevInfoStatus(NeInvoicePrevInfo invoicePrevInfo);

}
