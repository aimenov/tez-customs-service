package kz.ne.railways.tezcustoms.service.service.bean;

import kz.ne.railways.tezcustoms.service.entity.asudkr.*;
import kz.ne.railways.tezcustoms.service.model.DataCaneVagInfo;
import kz.ne.railways.tezcustoms.service.model.VagonItem;
import kz.ne.railways.tezcustoms.service.payload.response.VagInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrevInfoBeanDAO implements PrevInfoBeanDAOLocal {

    private final EntityManager entityManager;
    private final RestTemplate restTemplate;

    @Value("${services.external.gateway.vagonInfo.url}")
    private String gatewayVagInfoUrl;


    public Integer existLikeInvoiceByNumAndSta(Long invoiceId) {
        Integer count = 0; // Если 0 даем отправить!

        /*-->TDG-5086 Ввиду совершенствования логики определения дублированных из ПИ и IFTMIN, приостановить публикацию на промышленный сервер задачи 4763
        NeInvoice invoicePI = getInvoice(invoiceId);
        if (invoicePI!=null && invoicePI.getInvcNum()!=null && invoicePI.getReciveStationCode()!=null && invoicePI.getDestStationCode()!=null) {
        	String sqlText = "";
        
        	sqlText = "SELECT count(*) AS cnt FROM KTZ.NE_INVOICE ni " +
        			"INNER JOIN ktz.NE_INVOICE_INFO nii ON nii.INVOICE_UN = ni.INVC_UN " +
        			"WHERE ni.INVC_NUM = '" + invoicePI.getInvcNum() + "' " +
        			"AND ni.DEST_STATION_CODE = '" + invoicePI.getDestStationCode() + "' " +
        			"AND ni.RECIVE_STATION_CODE = '" + invoicePI.getReciveStationCode() + "' " +
        			"AND nii.INVOICE_CREATED_BY = 'IFTMIN' " +
        			"AND ni.INVOICE_DATETIME > CURRENT_TIMESTAMP - 1 MONTH ";
        
        	try {
        		System.out.println("* * * existLikeInvoiceByNumAndSta * * *  " + sqlText);
        		count = (Integer) em.createNativeQuery(sqlText).getSingleResult();
        	} catch (NoResultException e) {
        	}
        	System.out.println("* * * existLikeInvoiceByNumAndSta * * *  count = " + count);
        	count = (count==null ? 0 : count);
        }
        System.out.println("* * * existLikeInvoiceByNumAndSta * * *  count = " + count);
        */

        return count;
    }

    public List<NeSmgsTnVed> getGridDatas(Long invoiceUn) {
        List<NeSmgsTnVed> result;
        result = entityManager.createQuery("select a from NeSmgsTnVed a where a.invoiceUn = ?1", NeSmgsTnVed.class)
                        .setParameter(1, invoiceUn).getResultList();
        return result;
    }

    public Map<String, DataCaneVagInfo> getDatacaneVagInfo(List<String> dataCaneVagons) {
        Map<String, DataCaneVagInfo> map = new HashMap<>();
        if (dataCaneVagons.isEmpty())
            return map;
        Map<String, String> cacheMap = new HashMap<>();
        for (String dataCaneVagon : dataCaneVagons) {
            String toDataCane = StringUtils.leftPad(dataCaneVagon, 12, "0");
            cacheMap.put(toDataCane, dataCaneVagon);
        }
        List<Object[]> result = new ArrayList<>();
        try {
            result = getVagInfo(dataCaneVagons);
            if (result == null)
                return map;
            //result = entityManager.createNativeQuery(buffer.toString()).getResultList();
        } catch (RuntimeException e) {
        }
        for (Object[] row : result) {
            log.info("row: " + row[0]);
            DataCaneVagInfo info = new DataCaneVagInfo();
            String vagNo = (String) row[0];
            info.setVagNoDC(vagNo.trim());
            int prop = (int) row[1];
            info.setPropertyDC(prop);
            info.setOwnerCodeDC((Integer) row[2]);
            int depo = (int) row[3];
            info.setDepoDC(depo);
            info.setOwnerNameDC((String) row[4]);
            int type = (int) row[5];
            info.setTypeVagDC(type);
            int tara = (int) row[6];
            info.setTaraDC(tara);
            int rail = (int) row[7];
            info.setRailwayNoDC(rail);
            int gp = (int) row[8];
            info.setGpDC(gp);

            info.setAxisCount((int) row[13]);
            // info.setYellowMileage((String) row[14]);
            // info.setRedMileage((String) row[15]);
            if (info.getDepoDC() != null && info.getPropertyDC() != null) {
                if (info.getDepoDC() == 6010 && info.getPropertyDC() == 0
                                && StringUtils.isBlank(info.getOwnerNameDC())) {
                    info.setOwnerCodeDC(680524);
                    info.setOwnerNameDC("АО \"Казтемиртранс\"");
                }
            }
            int prizAr = (int) (row[11] == null ? 0 : row[11]);
            int prizArMg = (int) (row[12] == null ? 0 : row[12]);
            if (prizAr > 0 || prizArMg > 0) {
                int ownerCode = (Integer) (row[9] == null ? 0 : row[9]);
                info.setOwnerCodeDC(ownerCode);
                info.setPropertyDC(3);
            }
            if (info.getDepoDC() != null && info.getPropertyDC() != null && info.getOwnerCodeDC() != null
                            && info.getRailwayNoDC() != null) {
                if (info.getDepoDC() == 0 && info.getPropertyDC() == 0 && info.getOwnerCodeDC() == 0
                                && (info.getRailwayNoDC() == 27 || info.getRailwayNoDC() == 0)) {
                    info.setOwnerCodeDC(680524);
                    info.setOwnerNameDC("АО \"Казтемиртранс\"");
                }
            }
            String key = cacheMap.get(info.getVagNoDC().trim());
            info.setVagNo(key);
            map.put(key, info);

        }
        cacheMap = null;
        result = null;
        return map;
    }

    private List<Object[]> getVagInfo (List<String> dataCaneVagons){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> values = new HashMap<>();
        values.put("list", dataCaneVagons);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(values, headers);
        ResponseEntity<VagInfoResponse> response = restTemplate.postForEntity(gatewayVagInfoUrl, entity, VagInfoResponse.class);
        return response.getBody().getLists();
    }

    public NeCustomsOrgs getCustomsOrgs(Long customOrgUn) {
        log.debug("customOrgUn is: " + customOrgUn);
        List<NeCustomsOrgs> list = entityManager.createQuery("select a from NeCustomsOrgs a where a.customsOrgUn = ?1")
                        .setParameter(1, customOrgUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public CurrencyCode getCurrencyCode(long curCodeUn) {
        return entityManager.find(CurrencyCode.class, curCodeUn);
    }

    public List<CurrencyCode> getCurrencyCodeList(Date date) {
        String sql = "select a from CurrencyCode a where (?1 between a.curCodeBgn and a.curCodeEnd)";
        return entityManager.createQuery(sql).setParameter(1, date).getResultList();
    }

    @SuppressWarnings("unchecked")
    public Map<Long, String> getTnVedDocuments(Long invoiceUn) {
        Map<Long, String> result = new HashMap<>();
        String sqlText = "select d1.SMGS_TNVED_DOCUMENTS_UN, d1.SMGS_TNVED_UN, d1.DOCUMENT_CODE, d1.DOCUMENT_NAME, d1.DOCUMENT_NUMBER, d1.DOCUMENT_DATE,  d1.DOCUMENT_DATE_TO, d1.COPY_COUNT, d1.LIST_COUNT "
                        + "from KTZ.NE_SMGS_TN_VED t1 "
                        + "JOIN KTZ.NE_SMGS_TNVED_DOCUMENTS d1 ON d1.SMGS_TNVED_UN=t1.SMGS_TN_VED_UN where t1.INVOICE_UN = ?1";
        List<NeSmgsTnVedDocuments> list = entityManager.createNativeQuery(sqlText, NeSmgsTnVedDocuments.class)
                        .setParameter(1, invoiceUn).getResultList();
        for (NeSmgsTnVedDocuments item : list) {
            String docs = result.get(item.getSmgsTnVedUn());
            docs = (docs != null ? docs + ", " : "");
            docs += item.getDocumentCode();
            result.put(item.getSmgsTnVedUn(), docs);
        }
        return result;
    }

    public List<NeCurrencyRates> getRates(Date date) {
        String sql = "select * from NSI.NE_CURRENCY_RATES where (? between RATE_BGN and RATE_END) and CURRENCU_CODE_UN IN (select b.CUR_CODE_UN from nsi.CURRENCY_CODE b where ? between b.CUR_CODE_BGN and b.CUR_CODE_END)";

        return (List<NeCurrencyRates>) entityManager.createNativeQuery(sql, NeCurrencyRates.class)
                .setParameter(1, date)
                .setParameter(2, date).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<NeVagonLists> getVagonList(Long invoiceUn) {
        return entityManager.createQuery("select a from NeVagonLists a where a.invcUn = :inv").setParameter("inv", invoiceUn)
                        .getResultList();
    }

    public Integer checkSend(String invoiceId, String invNum, String conteinerNum, List<VagonItem> vagonList,
                    String statusPI) {

        if (statusPI == null)
            statusPI = "";

        StringBuilder vagNo = new StringBuilder();

        if (!vagonList.isEmpty()) {
            for (VagonItem vagonItem : vagonList) {
                if (vagNo.toString().equals(""))
                    vagNo = new StringBuilder("'" + vagonItem.getNumber() + "'");
                else
                    vagNo.append(",'").append(vagonItem.getNumber()).append("'");
            }
        } else {
            vagNo = new StringBuilder("''");
        }

        String sqlText;
        // Контейнер
        if (conteinerNum != null && !conteinerNum.equals("false")) {
            String[] strings = conteinerNum.split("-");
            sqlText = "SELECT count(inv.INVC_NUM) FROM KTZ.NE_INVOICE inv "
                            + "JOIN KTZ.NE_INVOICE_PREV_INFO pi ON inv.INVC_UN=pi.INVOICE_UN "
                            + "JOIN KTZ.NE_CONTAINER_LISTS cl ON cl.INVOICE_UN = pi.INVOICE_UN "
                            + "WHERE inv.INVC_NUM = '" + invNum + "' AND cl.CONTAINER_NO = '" + strings[1]
                            + "' AND cl.CONTAINER_MARK = '" + strings[0] + "' ";
            sqlText += "AND pi.RESPONSE_DATETIME > (CURRENT_TIMESTAMP - 12 hour) ";

            if (statusPI.equals("1") || statusPI.equals("2")) { // принят/отклоненный
                sqlText = "SELECT count(ri.INVC_UN) " + " FROM ktz.NE_INVC_REF_PI ri "
                                + " LEFT JOIN ktz.NE_INVOICE inv ON inv.INVC_UN = ri.INVC_UN " +
                                // " LEFT JOIN KTZ.NE_CONTAINER_LISTS cl ON cl.INVOICE_UN = ri.PREV_INFO_INVC_UN "+
                                " WHERE ri.PREV_INFO_INVC_UN = " + invoiceId
                                + " AND inv.INVC_STATUS NOT IN(100,101) AND inv.INVC_NUM = '" + invNum + "'";
                // "' AND cl.CONTAINER_NO = '"+strings[1]+"' AND cl.CONTAINER_MARK = '"+strings[0]+"' ";
            }
        } // Вагон
        else {
            sqlText = "SELECT count(inv.INVC_NUM) FROM KTZ.NE_INVOICE inv "
                            + "JOIN KTZ.NE_INVOICE_PREV_INFO pi ON inv.INVC_UN=pi.INVOICE_UN "
                            + "JOIN KTZ.NE_VAGON_LISTS vl ON vl.INVC_UN = pi.INVOICE_UN ";
            sqlText += "WHERE inv.INVC_NUM = '" + invNum + "' AND vl.VAG_NO in(" + vagNo + ") "
                            + "AND pi.RESPONSE_DATETIME IS NOT NULL ";
            sqlText += "AND pi.RESPONSE_DATETIME > (CURRENT_TIMESTAMP - 12 hour) ";

            if (statusPI.equals("1") || statusPI.equals("2")) { // принят/отклоненный
                sqlText = "SELECT count(ri.INVC_UN) " + " FROM ktz.NE_INVC_REF_PI ri "
                                + " LEFT JOIN ktz.NE_INVOICE inv ON inv.INVC_UN = ri.INVC_UN " +
                                // " LEFT JOIN KTZ.NE_VAGON_LISTS vl ON vl.INVC_UN = ri.PREV_INFO_INVC_UN "+
                                " WHERE ri.PREV_INFO_INVC_UN = " + invoiceId + " AND inv.INVC_STATUS NOT IN(100,101) ";
                // "AND inv.INVC_NUM = '"+invNum+"' AND vl.VAG_NO IN("+vagNo+")";
            }
        }

        Integer no = 0; // Если 0 даем отправить!
        try {
            System.out.println(sqlText);
            no = (Integer) entityManager.createNativeQuery(sqlText).getSingleResult();
        } catch (NoResultException e) {
        }

        return no == null ? 0 : no;
    }

    public String getStationName(String stationCode, boolean onlyName) {
        List<Sta> list = entityManager.createQuery("select a from Sta a where a.staNo = ?1 and a.stEnd > CURRENT_TIMESTAMP")
                        .setParameter(1, stationCode).getResultList();
        if (list.size() > 0) {
            Sta sta = list.get(0);
            if (onlyName)
                return sta.getStaName();
            else
                return sta.getStaNo() + " - " + sta.getStaName();
        }
        return null;
    }

    public NeInvoice getInvoice(Long invoiceUn) {
        List<NeInvoice> list = entityManager.createQuery("select a from NeInvoice a where a.invcUn = ?1", NeInvoice.class)
                        .setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeInvcRefPi getNeInvcRefPi(final Long piId) {
        String sql = "select * from ktz.NE_INVC_REF_PI  where PREV_INFO_INVC_UN = ?";
        List<NeInvcRefPi> list = entityManager.createNativeQuery(sql, NeInvcRefPi.class).setParameter(1, piId).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public NeSmgsCargo getNeSmgsCargo(Long invoiceUn) {
        List<NeSmgsCargo> list = entityManager.createQuery("select a from NeSmgsCargo a where a.invUn = ?1", NeSmgsCargo.class)
                        .setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeInvoicePrevInfo getInvoicePrevInfo(Long invoiceUn) {
        List<NeInvoicePrevInfo> list = entityManager.createQuery("select a from NeInvoicePrevInfo a where a.invoiceUn = ?1",
                        NeInvoicePrevInfo.class).setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeSmgsSenderInfo getSenderInfo(Long invoiceUn) {
        List<NeSmgsSenderInfo> list =
                        entityManager.createQuery("select a from NeSmgsSenderInfo a where a.invUn = ?1", NeSmgsSenderInfo.class)
                                        .setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeSmgsDeclarantInfo getDeclarantInfo(Long invoiceUn) {
        List<NeSmgsDeclarantInfo> list = entityManager.createQuery("select a from NeSmgsDeclarantInfo a where a.invUn = ?1",
                        NeSmgsDeclarantInfo.class).setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    // DUPLICATE
    // public NeSmgsDeclarantInfo getDeclarant(Long id) {
    // List<NeSmgsDeclarantInfo> list = em.createQuery("select a from NeSmgsDeclarantInfo a where
    // a.invUn = ?")
    // .setParameter(1,id)
    // .getResultList();
    // if (list != null && !list.isEmpty()) {
    // return list.get(0);
    // }
    // return null;
    // }

    public NeSmgsExpeditorInfo getExpeditorInfo(Long invoiceUn) {
        List<NeSmgsExpeditorInfo> list = entityManager.createQuery("select a from NeSmgsExpeditorInfo a where a.invUn = ?1",
                        NeSmgsExpeditorInfo.class).setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeSmgsRecieverInfo getRecieverInfo(Long invoiceUn) {
        List<NeSmgsRecieverInfo> list = entityManager
                        .createQuery("select a from NeSmgsRecieverInfo a where a.invUn = ?1", NeSmgsRecieverInfo.class)
                        .setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeSmgsDestinationPlaceInfo getNeSmgsDestinationPlaceInfo(Long invoiceUn) {
        List<NeSmgsDestinationPlaceInfo> list =
                        entityManager.createQuery("select a from NeSmgsDestinationPlaceInfo a where a.invoiceUn = ?1",
                                        NeSmgsDestinationPlaceInfo.class).setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeSmgsShipList getNeSmgsShipList(Long invoiceUn) {
        List<NeSmgsShipList> list =
                        entityManager.createQuery("select a from NeSmgsShipList a where a.invUn = ?1", NeSmgsShipList.class)
                                        .setParameter(1, invoiceUn).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public NeVessel getVessel(Long un) {
        return entityManager.find(NeVessel.class, un);
    }

    public List<NeSmgsTnVed> getTnVedList(Long invoiceUn) {
        return entityManager.createQuery("select a from NeSmgsTnVed a where a.invoiceUn = ?1", NeSmgsTnVed.class)
                        .setParameter(1, invoiceUn).getResultList();
    }

    public NeVagonGroup getVagonGroup(Long id) {
        List<NeVagonGroup> list = entityManager.createNativeQuery(
                        "SELECT a.* FROM KTZ.NE_VAGON_GROUP a join ktz.NE_VAGON_LISTS b on a.VAG_GROUP_UN = b.VAG_GROUP_UN where b.INVC_UN = ?1 fetch first row only",
                        NeVagonGroup.class).setParameter(1, id).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public Country getCountry(String countryCode) {
        List<Country> list = entityManager.createQuery("select a from Country a where a.countryNo = ?1")
                        .setParameter(1, countryCode).getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public Sta getStation(String stationCode) {
        List<Sta> list = entityManager.createQuery("select a from Sta a where a.staNo = ?1 and a.stEnd > CURRENT_TIMESTAMP")
                        .setParameter(1, stationCode).getResultList();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public Management getManagement(Long managUn) {
        List<Management> list = entityManager.createQuery("select a from Management a where a.managUn = ?1 ")
                        .setParameter(1, managUn).getResultList();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public Country getCountry(Long countryUn) {
        List<Country> list = entityManager.createQuery("select a from Country a where a.couUn = ?1").setParameter(1, countryUn)
                        .getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<NeSmgsTnVedDocuments> getSmgsTnVedDocuments(Long tnVedId) {
        return entityManager.createQuery(
                        "select a from NeSmgsTnVedDocuments a where a.smgsTnVedUn = ?1 order by a.smgsTnVedDocumentsUn asc")
                        .setParameter(1, tnVedId).getResultList();
    }

    @Transactional
    public NeSmgsTnVedDocuments saveSmgsTnVedDocuments(NeSmgsTnVedDocuments saveDoc) {
        saveDoc = entityManager.merge(saveDoc);
        entityManager.flush();
        return saveDoc;
    }

    @Transactional
    public void deleteTnVedDocuments(Long id) {
        if (id != null) {
            NeSmgsTnVedDocuments doc = getSmgsTnVedDocumentsById(id);
            if (doc != null) {
                entityManager.remove(doc);
                entityManager.flush();
            }
        }
    }

    @Transactional
    public void updateTnVedDocuments(Long id, NeSmgsTnVedDocuments updateDoc) {
        NeSmgsTnVedDocuments doc = getSmgsTnVedDocumentsById(id);
        if (doc != null) {
            doc.setDocumentDate(updateDoc.getDocumentDate());
            doc.setDocumentName(updateDoc.getDocumentName());
            doc.setDocumentNumber(updateDoc.getDocumentNumber());
            doc.setDocumentCode(updateDoc.getDocumentCode());
            doc.setCopyCount(updateDoc.getCopyCount());
            doc.setListCount(updateDoc.getListCount());
            entityManager.merge(doc);
            entityManager.flush();
        }

    }

    @Transactional
    public void updateNeInvoicePrevInfo(Long id, NeInvoicePrevInfo updateDoc) {
        if (updateDoc != null) {
            entityManager.merge(updateDoc);
            entityManager.flush();
        }

    }

    public NeSmgsTnVedDocuments getSmgsTnVedDocumentsById(Long id) {
        List<NeSmgsTnVedDocuments> list =
                        entityManager.createQuery("select a from NeSmgsTnVedDocuments a where a.smgsTnVedDocumentsUn = ?1")
                                        .setParameter(1, id).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public NeSmgsExpeditorInfo getExpeditor(Long id) {
        List<NeSmgsExpeditorInfo> list = entityManager.createQuery("select a from NeSmgsExpeditorInfo a where a.invUn = ?1")
                        .setParameter(1, id).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public List<NeUnitType> getUnitType() {
        return entityManager.createQuery("select a from NeUnitType a ").getResultList();
    }

    public Country getCountryBycode(String countryCode) {
        return (Country) entityManager.createNativeQuery("select * from NSI.COUNTRY s where s.COUNTRY_NO = ?1", Country.class)
                        .setParameter(1, countryCode).getSingleResult();
    }

    public List<NeContainerLists> getContinerList(Long id) {
        return (List<NeContainerLists>) entityManager.createQuery("select a from NeContainerLists a where a.invoiceUn = ?1")
                        .setParameter(1, id).getResultList();
    }

    public void bigUpdateTnVedDocuments(Long id, NeSmgsTnVedDocuments updateDoc) {
        NeSmgsTnVedDocuments doc = getSmgsTnVedDocumentsById(id);
        if (doc != null) {
            doc.setDocumentDate(updateDoc.getDocumentDate());
            doc.setDocumentName(updateDoc.getDocumentName());
            doc.setDocumentNumber(updateDoc.getDocumentNumber());
            doc.setDocumentCode(updateDoc.getDocumentCode());
            entityManager.merge(doc);
            entityManager.flush();
        }
    }

    public String getTnVedDocumentsIds(Long invoiceUn) {
        String res = null;
        String sql = "select 1 as num, REPLACE(REPLACE(REPLACE(VARCHAR( XML2CLOB( XMLAGG( XMLELEMENT( NAME a ,D.SMGS_TN_VED_UN) ) ) ,2048 ) ,'</A><A>',',') ,'<A>','') ,'</A>','') AS ids "
                        + "from KTZ.NE_SMGS_TN_VED D WHERE D.INVOICE_UN IN(" + invoiceUn + ") " + "GROUP BY 1";
        try {
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> list = query.getResultList();
            for (Object[] object : list) {
                res = (String) object[1];
            }
        } catch (Exception nre) {
            nre.getLocalizedMessage();
            return null;
        }
        return res;
    }

    public NePersonCategoryType getPersonCategoryType(Long categoryType) {
        if (categoryType != null) {
            return entityManager.find(NePersonCategoryType.class, categoryType);
        } else {
            return null;
        }
    }

    public Country getCountryByManagNo(int managNo) {
        String sql = "select * from nsi.COUNTRY where COU_UN in (select COU_UN from NSI.MANAGEMENT where MANAG_NO = ?1 and CURRENT_TIMESTAMP between MANAG_BGN and MANAG_END )";
        List<Country> list = entityManager.createNativeQuery(sql, Country.class).setParameter(1, managNo).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Country getCountryByManagUn(Long managUn) {
        if (managUn == null) {
            return null;
        }
        String sql = "select * from nsi.COUNTRY where COU_UN in (select COU_UN from NSI.MANAGEMENT where MANAG_UN = ?1 and MANAG_END > CURRENT_TIMESTAMP FETCH FIRST 1 ROWS ONLY)";
        List<Country> list = entityManager.createNativeQuery(sql, Country.class).setParameter(1, managUn).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void updatePrevInfoStatus(NeInvoicePrevInfo invoicePrevInfo) {
        entityManager.merge(invoicePrevInfo);
    }

}
