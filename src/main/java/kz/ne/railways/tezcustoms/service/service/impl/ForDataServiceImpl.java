package kz.ne.railways.tezcustoms.service.service.impl;

import kz.ne.railways.tezcustoms.service.entity.asudkr.*;
import kz.ne.railways.tezcustoms.service.model.*;
import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;
import kz.ne.railways.tezcustoms.service.service.ForDataService;
import kz.ne.railways.tezcustoms.service.service.bean.PrevInfoBeanDAOLocal;
import kz.ne.railways.tezcustoms.service.util.PIHelper;
import kz.ne.railways.tezcustoms.service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ForDataServiceImpl implements ForDataService {

    private final PrevInfoBeanDAOLocal prevInfoBeanDAOLocal;
    private final EntityManager entityManager;

    private static final Long NEW_INVOICE = -1L;

    // Статусы ПИ
    public static int PI_STATUS_IN_WORK = 0; // В работе
    public static int PI_STATUS_SUCCESS_SEND = 1; // Принят в таможне
    public static int PI_STATUS_FAIL_SEND = 2; // Не принят в таможне
    public static int PI_STATUS_TO_CHECK = 3; // Оправлен на проверку
    public static int PI_STATUS_RETURN = 4; // Возврат на оформление
    public static int PI_STATUS_EDIT = 5; // Изменение ТД
    public static int PI_STATUS_EXPORT_TD = 6; // Сформировать ТД


    // Станции где должно указываться транспорт - судно
    List<String> vesselStaUns = Arrays.asList("691607", "693807", "663804", "689202");

    @Override
    public FormData getContractData(String invNum) {
        StringBuilder sqlWhe = new StringBuilder(" WHERE ");
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder sqlB = new StringBuilder();
        StringBuilder sqlSelsFields = new StringBuilder("select inv.INVC_UN as id \n");

        sqlB.append(" FROM KTZ.NE_INVOICE inv \n");

        sqlWhe.append("inv.INVC_NUM = ?1 ");

        sqlBuilder.append(sqlSelsFields);
        sqlBuilder.append(sqlB);
        sqlBuilder.append(sqlWhe);

        Query searchPIQuery = entityManager.createNativeQuery(sqlBuilder.toString());
        searchPIQuery.setParameter(1, invNum);
        List<Long> qResult = searchPIQuery.getResultList();
        String invoiceId = String.valueOf(qResult.get(0));

        return getFormData(invoiceId);
    }

    @Override
    public FormData getFormData(String invoiceId) {
        FormData result = new FormData();
        result.setInvoiceId(invoiceId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        NeInvoice neInvoice = prevInfoBeanDAOLocal.getInvoice(Long.parseLong(invoiceId));
        if (neInvoice != null) { // neInvoice @Table(name="NE_INVOICE")
            result.setTrainIndex(neInvoice.getTrainIndex());
            result.setInvoiceNumber(neInvoice.getInvcNum());
            result.setConteinerRef(Byte.toString(neInvoice.getIsContainer()));
            result.setDocType(neInvoice.getDocType());
            if (neInvoice.getInvoiceDatetime() != null) {
                Date date = new Date(neInvoice.getInvoiceDatetime().getTime());
                result.setInvDateTime(dateFormat.format(date));
            }
            result.setStartStation(neInvoice.getReciveStationCode());
            result.setStartStationName(prevInfoBeanDAOLocal.getStationName(neInvoice.getReciveStationCode(), false));
            result.setDestStation(neInvoice.getDestStationCode());
            result.setDestStationName(prevInfoBeanDAOLocal.getStationName(neInvoice.getDestStationCode(), false));
        }

        GngModel gngModel = getGngModel(Long.parseLong(invoiceId));
        if (gngModel != null) {
            result.setGngCode(gngModel.getCode());
            result.setGngName(gngModel.getShortName1());
        }
        NeInvoicePrevInfo neInvoicePrevInfo = prevInfoBeanDAOLocal.getInvoicePrevInfo(Long.parseLong(invoiceId));
        if (neInvoicePrevInfo != null) {
            Date date = new Date(neInvoicePrevInfo.getCreateDatetime().getTime());
            result.setCreateDate(dateFormat.format(date));
            result.setTransitDirectionCode(neInvoicePrevInfo.getPrevInfoType());
            result.setFeatureType(neInvoicePrevInfo.getPrevInfoFeatures());
            result.setCustomOrgUn(neInvoicePrevInfo.getCustomOrgUn());
            result.setCustomCode(neInvoicePrevInfo.getCustomCode());
            result.setCustomName(neInvoicePrevInfo.getCustomName());
            result.setArriveStation(neInvoicePrevInfo.getArriveStaNo());
            if (neInvoicePrevInfo.getArriveTime() != null) {
                result.setArrivalDate(neInvoicePrevInfo.getArriveTime());
                result.setArrivalTime(neInvoicePrevInfo.getArriveTime());
            }
            neInvoicePrevInfo.getPrevInfoStatus();
            result.setStartStaCountry(neInvoicePrevInfo.getStartStaCouNo());
            result.setDestStationCountry(neInvoicePrevInfo.getDestStationCouNo());
            result.setResponseMessage(neInvoicePrevInfo.getResponseText());
        }
        NeSmgsSenderInfo senderInfo = prevInfoBeanDAOLocal.getSenderInfo(Long.parseLong(invoiceId));
        if (senderInfo != null) {
            result.setSenderCountry(senderInfo.getSenderCountryCode());
            result.setSenderCountryName(getCountryName(senderInfo.getSenderCountryCode()));
            result.setSenderIndex(senderInfo.getSenderPostIndex());
            result.setSenderShortName(senderInfo.getSenderName());
            result.setSenderName(senderInfo.getSenderFullName());
            result.setSenderOblast(senderInfo.getSenderRegion());
            result.setSenderPoint(senderInfo.getSenderSity());
            result.setSenderStreetNh(senderInfo.getSenderStreet());
            result.setSenderBIN(senderInfo.getSenderBin());
            result.setSenderIIN(senderInfo.getSenderIin());
            result.setSenderKpp(senderInfo.getKpp());
            result.setSenderKatFace(senderInfo.getCategoryType());
            NePersonCategoryType personCategoryType = getPersonCategoryType(senderInfo.getCategoryType());
            if (personCategoryType != null) {
                result.setSenderKatFaceCode(personCategoryType.getCategoryCode());
                result.setSenderKatFaceName(personCategoryType.getCategoryName());
            }
            NeKatoType neKatoType =
                            getKatoType(senderInfo.getKatoType() != null ? senderInfo.getKatoType().toString() : null);
            if (neKatoType != null) {
                result.setSenderKATO(neKatoType.getKatoCode());
                result.setSenderKATOName(neKatoType.getKatoName());
            }
            result.setSenderITNreserv(senderInfo.getItn());
        }
        NeSmgsRecieverInfo recieverinfo = prevInfoBeanDAOLocal.getRecieverInfo(Long.parseLong(invoiceId));
        if (recieverinfo != null) {
            result.setRecieverCountry(recieverinfo.getRecieverCountryCode());
            result.setRecieverCountryName(getCountryName(recieverinfo.getRecieverCountryCode()));
            result.setRecieverIndex(recieverinfo.getRecieverPostIndex());
            result.setRecieverShortNam(recieverinfo.getRecieverName());
            result.setRecieverName(recieverinfo.getRecieverFullName());
            result.setRecieverOblast(recieverinfo.getRecieverRegion());
            result.setRecieverPoint(recieverinfo.getRecieverSity());
            result.setRecieverStreetNh(recieverinfo.getRecieverStreet());
            result.setRecieverBIN(recieverinfo.getRecieverBin());
            result.setRecieverIIN(recieverinfo.getRecieverIin());
            result.setRecieverKPP(recieverinfo.getKpp());
            result.setRecieverKatFace(recieverinfo.getCategoryType());
            NePersonCategoryType personCategoryType = getPersonCategoryType(recieverinfo.getCategoryType());
            if (personCategoryType != null) {
                result.setRecieverKatFaceCode(personCategoryType.getCategoryCode());
                result.setRecieverKatFaceName(personCategoryType.getCategoryName());
            }
            NeKatoType neKatoType = getKatoType(
                            recieverinfo.getKatoType() != null ? recieverinfo.getKatoType().toString() : null);
            if (neKatoType != null) {
                result.setRecieverKATO(neKatoType.getKatoCode());
                result.setRecieverKATOName(neKatoType.getKatoName());
            }
            result.setRecieverITNreserv(recieverinfo.getItn());
        }
        NeSmgsDestinationPlaceInfo neSmgsDestinationPlaceInfo =
                        prevInfoBeanDAOLocal.getNeSmgsDestinationPlaceInfo(Long.parseLong(invoiceId));
        if (neSmgsDestinationPlaceInfo != null) {
            String destPlaceSta = neSmgsDestinationPlaceInfo.getDestPlaceSta();
            result.setDestPlace(neSmgsDestinationPlaceInfo.getDestPlace());
            result.setDestPlaceStation(destPlaceSta);
            result.setDestPlaceStationName(prevInfoBeanDAOLocal.getStationName(destPlaceSta, true));
            result.setDestPlaceCountryCode(neSmgsDestinationPlaceInfo.getDestPlaceCountryCode());
            result.setDestPlaceIndex(neSmgsDestinationPlaceInfo.getDestPlaceIndex());
            result.setDestPlacePoint(neSmgsDestinationPlaceInfo.getDestPlaceCity());
            result.setDestPlaceOblast(neSmgsDestinationPlaceInfo.getDestPlaceRegion());
            result.setDestPlaceStreet(neSmgsDestinationPlaceInfo.getDestPlaceStreet());
            result.setDestPlaceCustomCode(neSmgsDestinationPlaceInfo.getDestPlaceCustomCode());
            result.setDestPlaceCustomName(neSmgsDestinationPlaceInfo.getDestPlaceCustomName());
            result.setDestPlaceCustomOrgUn(neSmgsDestinationPlaceInfo.getDestPlaceCustomOrgUn());
        }

//        NeSmgsDeclarantInfo declarant = dao.getDeclarantInfo(Long.parseLong(invoiceId));
//        if (declarant != null) {
//            Declarant dec = new Declarant();
//            Address add = new Address();
//            result.setDeclarant(dec);
//            result.getDeclarant().setAddress(add);
//
//            result.getDeclarant().getAddress().setAddress(declarant.getDeclarantAddress());
////            result.setDeclarantAMNZOU(declarant.getDeclarantAMNZOU());
////            result.setDeclarantAMUNN(declarant.getDeclarantAMUNN());
////            result.setDeclarantBYIN(declarant.getDeclarantBYIN());
////            result.setDeclarantBYUNP(declarant.getDeclarantBYUNP());
//            result.getDeclarant().getAddress().setCity(declarant.getDeclarantCity());
//            result.getDeclarant().setСountryCode(declarant.getDeclarantCountry());
//            result.getDeclarant().setCountryName(getCountryNameByCode(declarant.getDeclarantCountry()));
//            result.getDeclarant().setIndex(declarant.getDeclarantIndex());
////            result.setDeclarantKGINN(declarant.getDeclarantKGINN());
////            result.setDeclarantKGOKPO(declarant.getDeclarantKGOKPO());
//            result.getDeclarant().getPersonal().setBin(declarant.getDeclarantKZBin());
//            result.getDeclarant().getPersonal().setIin(declarant.getDeclarantKZIin());
//            result.getDeclarant().getPersonal().setItn(declarant.getDeclarantKZITN());
//            result.getDeclarant().getPersonal().setKato(declarant.getDeclarantKZKATO());
//            result.getDeclarant().getPersonal().setPersonsCategory(declarant.getDeclarantKZPersonsCategory());
//            result.getDeclarant().setName(declarant.getDeclarantName());
//            result.getDeclarant().getAddress().setRegion(declarant.getDeclarantRegion());
////            result.setDeclarantRUINN(declarant.getDeclarantRUINN());
////            result.setDeclarantRUKPP(declarant.getDeclarantRUKPP());
////            result.setDeclarantRUOGRN(declarant.getDeclarantRUOGRN());
//            result.getDeclarant().setShortName(declarant.getDeclarantShortName());
//        }
//
//        NeSmgsExpeditorInfo expeditor = dao.getExpeditorInfo(Long.parseLong(invoiceId));
//        if (expeditor != null) {
//            result.getExpeditor().getAddress().setAddress(expeditor.getExpeditorAddress());
////            result.setExpeditorAMNZOU(expeditor.getExpeditorAMNZOU());
////            result.setExpeditorAMUNN(expeditor.getExpeditorAMUNN());
////            result.setExpeditorBYIN(expeditor.getExpeditorBYIN());
////            result.setExpeditorBYUNP(expeditor.getExpeditorBYUNP());
//            result.getExpeditor().getAddress().setCity(expeditor.getExpeditorCity());
//            result.getExpeditor().setСountryCode(expeditor.getExpeditorCountry());
//            result.getExpeditor().setCountryName(getCountryNameByCode(expeditor.getExpeditorCountry()));
//            result.getExpeditor().setIndex(expeditor.getExpeditorIndex());
////            result.setExpeditorKGINN(expeditor.getExpeditorKGINN());
////            result.setExpeditorKGOKPO(expeditor.getExpeditorKGOKPO());
//            result.getExpeditor().getPersonal().setBin(expeditor.getExpeditorKZBin());
//            result.getExpeditor().getPersonal().setIin(expeditor.getExpeditorKZIin());
//            result.getExpeditor().getPersonal().setItn(expeditor.getExpeditorKZITN());
//            result.getExpeditor().getPersonal().setKato(expeditor.getExpeditorKZKATO());
//            result.getExpeditor().getPersonal().setPersonsCategory(expeditor.getExpeditorKZPersonsCategory());
//            result.getExpeditor().setName(expeditor.getExpeditorName());
//            result.getExpeditor().getAddress().setRegion(expeditor.getExpeditorRegion());
////            result.setExpeditorRUINN(expeditor.getExpeditorRUINN());
////            result.setExpeditorRUKPP(expeditor.getExpeditorRUKPP());
////            result.setExpeditorRUOGRN(expeditor.getExpeditorRUOGRN());
//            result.getExpeditor().setShortName(expeditor.getExpeditorShortName());
//        }
        if (vesselStaUns.contains(result.getArriveStation())) {
            NeSmgsShipList ship = prevInfoBeanDAOLocal.getNeSmgsShipList(Long.parseLong(invoiceId));
            if (ship != null) {
                result.setVesselUn(ship.getNeVesselUn());
                if (ship.getNeVesselUn() != null) {
                    NeVessel vessel = entityManager.find(NeVessel.class, ship.getNeVesselUn());
                    result.setVessel(new DicDao(vessel.getNeVesselUn(), vessel.getVesselName()));
                }
            }
        }
        List<VagonItem> vagonItems = prevInfoBeanDAOLocal.getVagonList(Long.parseLong(invoiceId))
                .stream()
                .map(neVagonLists -> {
                    VagonItem vagonItem = new VagonItem();
                    vagonItem.setId(neVagonLists.getVagListsUn());
                    vagonItem.setNumber(neVagonLists.getVagNo());
                    return vagonItem;
                })
                .collect(Collectors.toList());
        result.setVagonList(vagonItems);

        if ("1".equals(result.getConteinerRef())) {

            /*
             * NeContainerLists containerLists = contList!=null && !contList.isEmpty() ? contList.get(0):null;
             * if(containerLists != null){ result.setNumContainer(containerLists.getContainerNo());
             * result.setContainerFilled(containerLists.getFilledContainer()); System.out.println("ManagUn " +
             * containerLists.getManagUn()); result.setVagonAccessory(containerLists.getManagUn());
             * result.setContainerMark(containerLists.getContainerMark());
             * result.setContainerCode(containerLists.getConUn()); }
             */
            List<NeContainerLists> contList = prevInfoBeanDAOLocal.getContinerList(Long.parseLong(invoiceId));
            for (NeContainerLists neContainerLists : contList) {
                ContainerData container = new ContainerData();
                container.setContainerListUn(neContainerLists.getContainerListsUn());
                container.setNumContainer(neContainerLists.getContainerNo());
                container.setContainerFilled(neContainerLists.getFilledContainer());
                container.setVagonAccessory(neContainerLists.getManagUn());
                container.setContainerMark(neContainerLists.getContainerMark());
                container.setContainerCode(neContainerLists.getConUn());
                result.addContainer(container);
                Container code = entityManager.find(Container.class, PIHelper.getLongVal(neContainerLists.getConUn()));
                if (code != null) {
                    result.setContainerCode(new DicDao(code.getConUn(), code.getConCode()));
                    container.setContainerCodeName(code.getConCode());
                }

                Management mng = entityManager.find(Management.class, neContainerLists.getManagUn());
                if (mng != null) {
                    Country cntry = entityManager.find(Country.class, mng.getCouUn());
                    if (cntry != null) {
                        result.setContainerCountry(new DicDao(mng.getManagUn(), cntry.getCountryName()));
                        container.setVagonAccessoryName(cntry.getCountryName());
                    }
                }
            }
        } else if ("0".equals(result.getConteinerRef())) {
            result.setVagonAccessory(getManagUnByInvoiceUn(Long.parseLong(invoiceId)));
        }
        return result;
    }

    @Override
    @Transactional
    public void saveInvoiceData(FormData formData) {

        /*TODO:
           set Currency Code (Un?)
           total goods number
           total package number
        */
        if(formData.getInvoiceData() == null || formData.getInvoiceData().getInvoiceItems() == null)
            return;

        for (InvoiceRow invoiceRow: formData.getInvoiceData().getInvoiceItems()) {
            NeSmgsTnVed neTnved = new NeSmgsTnVed();
            neTnved.setBruttoWeight(invoiceRow.getBrutto());
            neTnved.setNettoWeight(invoiceRow.getNetto());
            neTnved.setCountByUnit(invoiceRow.getQuantity());
            neTnved.setInvoiceUn((long) Integer.parseInt(formData.getInvoiceId()));
            neTnved.setUnitName(invoiceRow.getUnit());
            neTnved.setPriceByOne(invoiceRow.getPrice());
            neTnved.setPriceByFull(invoiceRow.getTotalPrice());
            neTnved.setTnVedCode(invoiceRow.getCode());
            neTnved.setTnVedName(invoiceRow.getName());
            neTnved.setCurrencyCodeUn(invoiceRow.getCurrencyCode());
            neTnved.setTnVedDescription(invoiceRow.getDescription());

            BigInteger cnt = (BigInteger) entityManager.createNativeQuery(
                            "select count(*) from ktz.ne_smgs_tn_ved a WHERE a.invoice_un = (?1) and a.tn_ved_code = (?2)")
                            .setParameter(1, neTnved.getInvoiceUn()).setParameter(2, neTnved.getTnVedCode())
                            .getSingleResult();
            if (cnt.intValue() == 0)
                entityManager.persist(neTnved);
        }

    }

    @Override
    @Transactional
    public void saveContractData(Long id, FormData formData, List<VagonItem> vagonList, ContainerDatas containerDatas) {
        Long invoiceUn;
        NeInvoice invoice = null;
        NeSmgsCargo neSmgsCargo = null;
        NeInvoicePrevInfo neInvoicePrevInfo = null;
        NeSmgsSenderInfo senderInfo = null;
        NeSmgsRecieverInfo recieverinfo = null;
        NeVagonGroup vagonGroup = null;
        NeSmgsDestinationPlaceInfo neSmgsDestinationPlaceInfo = null;
        NeSmgsShipList neSmgsShipList = null;
        NeSmgsDeclarantInfo neSmgsDeclarantInfo = null;
        NeSmgsExpeditorInfo neSmgsExpeditorInfo = null;
        Map<Long, NeSmgsTnVed> neSmgsTnVedMap = new HashMap<>();
        Map<String, NeVagonLists> neVagonListsMap = new HashMap<>();
        Map<Long, NeContainerLists> containerListsMap = new HashMap<>();

        if (!NEW_INVOICE.equals(id)) {
            invoice = prevInfoBeanDAOLocal.getInvoice(id);
            neInvoicePrevInfo = prevInfoBeanDAOLocal.getInvoicePrevInfo(id);
            senderInfo = prevInfoBeanDAOLocal.getSenderInfo(id);
            recieverinfo = prevInfoBeanDAOLocal.getRecieverInfo(id);
            neSmgsDestinationPlaceInfo = prevInfoBeanDAOLocal.getNeSmgsDestinationPlaceInfo(id);
            neSmgsShipList = prevInfoBeanDAOLocal.getNeSmgsShipList(id);
            neSmgsDeclarantInfo = prevInfoBeanDAOLocal.getDeclarantInfo(id);
            neSmgsExpeditorInfo = prevInfoBeanDAOLocal.getExpeditorInfo(id);
            neSmgsTnVedMap = getSmgsTnVedMap(id);
            vagonGroup = prevInfoBeanDAOLocal.getVagonGroup(id);
            neVagonListsMap = getNeVagonListsMap(id);
            neSmgsCargo = prevInfoBeanDAOLocal.getNeSmgsCargo(id);
            containerListsMap = getNeContainerListsMap(id);
        }

        invoice = createInvoice(invoice, formData);
        if (invoice.getInvcUn() == null) {
            entityManager.persist(invoice);
        } else {
            entityManager.merge(invoice);
            entityManager.flush();
        }

        invoiceUn = invoice.getInvcUn();
        System.out.println("invc_UN:::::::::::::::::::::::::::::::" + invoiceUn);
        System.out.println("getGngCode:::::::::::::::::::::::::::::::" + formData.getGngCode());
        if (StringUtils.isNotBlank(formData.getGngCode())) {
            neSmgsCargo = createNeSmgsCargo(neSmgsCargo, formData, invoiceUn);
            entityManager.merge(neSmgsCargo);
        }
        neInvoicePrevInfo = createInvoicePrevInfo(neInvoicePrevInfo, formData, invoiceUn);
        entityManager.merge(neInvoicePrevInfo);
        NeSmgsSenderInfo senderInfoToCheckForSolrUpdate = prevInfoBeanDAOLocal.getSenderInfo(invoiceUn);
        NeSmgsRecieverInfo receiverInfoToCheckForSolrUpdate = prevInfoBeanDAOLocal.getRecieverInfo(invoiceUn);
        // NeSmgsDeclarantInfo declarantInfoToCheckForSolrUpdate = dao.getDeclarantInfo(invoiceUn);
        Map<String, String> solrPropertyMap = new HashMap<>();
        solrPropertyMap.put("senderSolrUUID", formData.getSenderSolrUUID());
        solrPropertyMap.put("receiverSolrUUID", formData.getRecieverSolrUUID());
        solrPropertyMap.put("declarantSolrUUID", formData.getDeclarantSolrUUID());
        if (formData.getSenderSolrUUID() == null) {
            if (senderInfoToCheckForSolrUpdate == null) {
                solrPropertyMap.put("senderSolrUUID", UUID.randomUUID().toString());
            } else if (senderInfoToCheckForSolrUpdate.getSenderBin() != null
                            && !senderInfoToCheckForSolrUpdate.getSenderBin().equals(formData.getSenderBIN())) {
                solrPropertyMap.put("senderSolrUUID", UUID.randomUUID().toString());
            } else if (senderInfoToCheckForSolrUpdate.getSenderIin() != null
                            && !senderInfoToCheckForSolrUpdate.getSenderIin().equals(formData.getSenderIIN())) {
                solrPropertyMap.put("senderSolrUUID", UUID.randomUUID().toString());
            }
        }
        if (formData.getRecieverSolrUUID() == null) {
            if (receiverInfoToCheckForSolrUpdate == null) {
                solrPropertyMap.put("receiverSolrUUID", UUID.randomUUID().toString());
            } else if (receiverInfoToCheckForSolrUpdate.getRecieverBin() != null
                            && !receiverInfoToCheckForSolrUpdate.getRecieverBin().equals(formData.getRecieverBIN())) {
                solrPropertyMap.put("receiverSolrUUID", UUID.randomUUID().toString());
            } else if (receiverInfoToCheckForSolrUpdate.getRecieverIin() != null
                            && !receiverInfoToCheckForSolrUpdate.getRecieverIin().equals(formData.getRecieverIIN())) {
                solrPropertyMap.put("receiverSolrUUID", UUID.randomUUID().toString());
            }
        }

        if (declarantInfoFieldsAreNotNull(formData)) {
            neSmgsDeclarantInfo = createDeclarantInfo(neSmgsDeclarantInfo, formData, invoiceUn);
            System.out.println("invc_UN:::::::::::::::::::::::::::::::::" + neSmgsDeclarantInfo.getInvUn());
            entityManager.merge(neSmgsDeclarantInfo);
        }

        if (expeditorInfoFieldsAreNotNull(formData)) {
            neSmgsExpeditorInfo = createExpeditorInfo(neSmgsExpeditorInfo, formData, invoiceUn);
            System.out.println("neSmgsExpeditorInfo invc_UN:::::::::::::::::::::::::::::::::"
                            + neSmgsExpeditorInfo.getInvUn());
            entityManager.merge(neSmgsExpeditorInfo);
        } else if (neSmgsExpeditorInfo != null && neSmgsExpeditorInfo.getInvUn() == invoiceUn) {
            entityManager.remove(neSmgsExpeditorInfo);
        }

        if (senderInfoFieldsAreNotNull(formData)) {
            senderInfo = createSenderInfo(senderInfo, formData, invoiceUn);
            entityManager.merge(senderInfo);
        }
        if (receiverInfoFieldsAreNotNull(formData)) {
            recieverinfo = createRecieverInfo(recieverinfo, formData, invoiceUn);
            entityManager.merge(recieverinfo);
        }

        neSmgsDestinationPlaceInfo = createNeSmgsDestPlaceInfo(neSmgsDestinationPlaceInfo, formData, invoiceUn);
        entityManager.merge(neSmgsDestinationPlaceInfo);

        if (vesselStaUns.contains(formData.getArriveStation())) {
            neSmgsShipList = createNeSmgsShipList(neSmgsShipList, formData, invoiceUn);
            entityManager.merge(neSmgsShipList);
        }


        /* удалить ниже этой строки */
        if (formData.getConteinerRef() != null && "1".equals(formData.getConteinerRef())) {
            // containerLists = createNeContainerLists(invoiceUn,containerLists,containerData);
            // em.merge(containerLists);

            if (containerDatas != null) {
                if (containerDatas.getContainerData() != null && !containerDatas.getContainerData().isEmpty()) {
                    for (ContainerData item : containerDatas.getContainerData()) {
                        NeContainerLists cl = containerListsMap.get(item.getContainerListUn());
                        boolean persist = cl == null;
                        cl = createNeContainerLists(invoiceUn, cl, item);
                        if (persist) {
                            entityManager.persist(cl);
                            System.out.println("++++Insert CL" + cl.getContainerListsUn());
                        } else {
                            entityManager.merge(cl);
                            System.out.println("++++Update CL" + cl.getContainerListsUn());
                        }
                    }
                }

                if (containerDatas.getContainerRemData() != null && !containerDatas.getContainerRemData().isEmpty()) {
                    for (ContainerData item : containerDatas.getContainerRemData()) {
                        NeContainerLists cl = containerListsMap.get(item.getContainerListUn());
                        if (cl != null) {
                            entityManager.remove(cl);
                        }
                    }
                }
            }
        }

        if (vagonList != null && !vagonList.isEmpty()) {
            if (vagonGroup == null) {
                vagonGroup = createNeVagonGroup(vagonGroup);
                entityManager.persist(vagonGroup);
            }
            for (VagonItem vagonItem : vagonList) {
                NeVagonLists neVagonLists = neVagonListsMap.get(vagonItem.getNumber());
                if (formData.getVagonAccessory() != null) {
                    String owner = getManagNoByManagUn(formData.getVagonAccessory());
                    neVagonLists = createNeVagonLists(neVagonLists, vagonItem, invoiceUn, vagonGroup.getVagGroupUn(),
                                    owner);
                } else {
                    neVagonLists = createNeVagonLists(neVagonLists, vagonItem, invoiceUn, vagonGroup.getVagGroupUn(),
                                    null);
                }
                entityManager.merge(neVagonLists);
            }
            entityManager.merge(vagonGroup);
            // Удаление вагонов
            List<String> deletedVagon = getDeletedVagons(neVagonListsMap, vagonList);
            if (deletedVagon != null && !deletedVagon.isEmpty()) {
                // deleteVagonListUnByInvUn(invoiceUn);
                deleteVagonList(deletedVagon, invoiceUn);
            }
        } else if (!neVagonListsMap.isEmpty()) { // Удалить вагоны вместе с группой
            // deleteVagonListUnByInvUn(invoiceUn);
            List<String> deletedVagon = getDeletedVagons(neVagonListsMap, vagonList);
            deleteVagonList(deletedVagon, invoiceUn);
            deleteVagonGroup(vagonGroup, invoiceUn);
        }
        entityManager.flush();
    }

    private Timestamp convertToTimestamp(String timestamp_str) {
        Timestamp result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = new Timestamp(sdf.parse(timestamp_str).getTime());
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage(), e);
        }
        return result;
    }

    private void deleteVagonGroup(NeVagonGroup vagonGroup, Long invoiceUn) {
        if (vagonGroup != null) {
            Integer count = (Integer) entityManager.createNativeQuery(
                            "select count(*) from ktz.NE_VAGON_LISTS WHERE VAG_GROUP_UN = (?1) and INVC_UN <> (?2)")
                            .setParameter(1, vagonGroup.getVagGroupUn()).setParameter(2, invoiceUn).getSingleResult();
            if (count == 0) {
                entityManager.remove(vagonGroup);
            }
        }

    }

    @Transactional
    public void saveCustomsResponse(Long invoiceId, SaveDeclarationResponseType result, String uuid) {
        try {
            NeInvoicePrevInfo invoicePrevInfo = prevInfoBeanDAOLocal.getInvoicePrevInfo(invoiceId);
            if (invoicePrevInfo != null && result != null && result.getValue() != null) {
                String[] message = result.getValue().split("/n");
                if (message.length > 0) {
                    invoicePrevInfo.setResponseText(message[0]);
                }
                if (message.length > 1) {
                    Timestamp timestamp = convertToTimestamp(message[1]);
                    invoicePrevInfo.setResponseDatetime(timestamp);
                }
                if (result.getCode() != null) {
                    Long code;
                    try {
                        code = Long.parseLong(result.getCode());
                        invoicePrevInfo.setPrevInfoStatus(
                                        (code.intValue() == 0 ? PI_STATUS_SUCCESS_SEND : PI_STATUS_FAIL_SEND));
                    } catch (Exception e) {
                        log.warn(e.getLocalizedMessage(), e);
                    }
                }
                invoicePrevInfo.setResponseUUID(uuid);
                entityManager.merge(invoicePrevInfo);
                entityManager.flush();
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private void deleteVagonList(List<String> deletedVagon, Long invoiceUn) {
        if (deletedVagon != null && !deletedVagon.isEmpty() && invoiceUn != null) {
            StringBuilder list_str = new StringBuilder();
            for (String item : deletedVagon) {
                list_str.append(",");
                list_str.append("'").append(item).append("'");
            }
            String str1 = list_str.toString();
            str1 = str1.substring(1);
            String sql = "DELETE FROM KTZ.NE_VAGON_LISTS where VAG_NO IN (" + str1 + ") AND INVC_UN = "
                            + invoiceUn.toString();
            entityManager.createNativeQuery(sql).executeUpdate();
            entityManager.flush();
        }

    }

    private List<String> getDeletedVagons(Map<String, NeVagonLists> neVagonListsMap, List<VagonItem> vagonList) {
        List<String> result = null;
        if (neVagonListsMap != null && !neVagonListsMap.isEmpty()) {
            Set<String> forDelete = neVagonListsMap.keySet();
            for (VagonItem item : vagonList) {
                forDelete.remove(item.getNumber());
            }
            result = new ArrayList<String>();
            for (String item : forDelete) {
                result.add(item);
            }
        }
        return result;
    }

    private String getManagNoByManagUn(Long managUn) {
        String answer = null;
        String sql = "select m.MANAG_NO from NSI.MANAGEMENT m where m.MANAG_UN=" + managUn.toString();
        try {
            answer = String.valueOf(entityManager.createNativeQuery(sql).getSingleResult());
        } catch (NoResultException e) {
            return null;
        }
        return answer;
    }

    private NeInvoice createInvoice(NeInvoice invoice, FormData formData) {
        if (invoice == null) {
            invoice = new NeInvoice();
        }
        invoice.setTrainIndex(formData.getTrainIndex());
        invoice.setInvcNum(formData.getInvoiceNumber());
        if (formData.getConteinerRef() != null) {
            invoice.setIsContainer(Byte.parseByte(formData.getConteinerRef()));
        }
        invoice.setReciveStationCode(formData.getStartStation());
        invoice.setDestStationCode(formData.getDestStation());
        invoice.setUserId(SecurityUtils.getCurrentUserId());
        invoice.setInvcDt(Timestamp.valueOf(formData.getCreateDate()));
        return invoice;
    }

    private NeInvoicePrevInfo createInvoicePrevInfo(NeInvoicePrevInfo neInvoicePrevInfo, FormData formData,
                    Long invoiceUn) {
        if (neInvoicePrevInfo == null) {
            neInvoicePrevInfo = new NeInvoicePrevInfo();
            neInvoicePrevInfo.setUserUn(formData.getUserUn());
        }
        neInvoicePrevInfo.setPrevInfoType(formData.getTransitDirectionCode());

        neInvoicePrevInfo.setCustomOrgUn(formData.getCustomOrgUn());

//        // TODO: After transformation to combobox (customCode) sends us _UN
//        if (formData.getCustomCode() != null) {
//            neInvoicePrevInfo.setCustomOrgUn(Long.valueOf(formData.getCustomCode()));
//        }
        // neInvoicePrevInfo.setCustomCode(formData.getCustomCode());
        // neInvoicePrevInfo.setCustomName(formData.getCustomName());
        neInvoicePrevInfo.setPrevInfoFeatures(formData.getFeatureType());
        neInvoicePrevInfo.setInvoiceUn(invoiceUn);
        long dateTime = new Date().getTime();
        if (neInvoicePrevInfo.getCreateDatetime() == null) {
            neInvoicePrevInfo.setCreateDatetime(new Timestamp(dateTime));
        }
        // FIXME: after integration with ws
        // neInvoicePrevInfo.setResponseDatetime(new Timestamp(dateTime));
        // FIXME: response true for now (need to fix response value)
        neInvoicePrevInfo.setArriveStaNo(formData.getArriveStation());
        if (formData.getArrivalTime() != null && formData.getArrivalDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(formData.getArrivalTime().getTime());
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            calendar.setTimeInMillis(formData.getArrivalDate().getTime());
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            Timestamp ts = new Timestamp(calendar.getTimeInMillis());
            neInvoicePrevInfo.setArriveTime(ts);
        }
        neInvoicePrevInfo.setStartStaCouNo(formData.getStartStaCountry());
        neInvoicePrevInfo.setDestStationCouNo(formData.getDestStationCountry());
        return neInvoicePrevInfo;
    }

    private Map<Long, NeSmgsTnVed> getSmgsTnVedMap(Long invoiceUn) {
        List<NeSmgsTnVed> list = prevInfoBeanDAOLocal.getTnVedList(invoiceUn);
        Map<Long, NeSmgsTnVed> result = new HashMap<Long, NeSmgsTnVed>();
        if (list != null && list.size() > 0) {
            for (NeSmgsTnVed item : list) {
                result.put(item.getSmgsTnVedUn(), item);
            }
        }
        return result;
    }

    private Map<String, NeVagonLists> getNeVagonListsMap(Long invoiceUn) {
        Map<String, NeVagonLists> result = new HashMap<String, NeVagonLists>();
        List<NeVagonLists> list = prevInfoBeanDAOLocal.getVagonList(invoiceUn);
        if (list != null && !list.isEmpty()) {
            for (NeVagonLists item : list) {
                result.put(item.getVagNo(), item);
            }
        }
        return result;
    }

    private Map<Long, NeContainerLists> getNeContainerListsMap(Long invoiceUn) {
        Map<Long, NeContainerLists> result = new HashMap<Long, NeContainerLists>();
        List<NeContainerLists> list = prevInfoBeanDAOLocal.getContinerList(invoiceUn);
        if (list != null && !list.isEmpty()) {
            for (NeContainerLists item : list) {
                result.put(item.getContainerListsUn(), item);
            }
        }
        return result;
    }

    private boolean receiverInfoFieldsAreNotNull(FormData formData) {
        return StringUtils.isNotBlank(formData.getRecieverName())
                        || StringUtils.isNotBlank(formData.getRecieverShortNam())
                        || StringUtils.isNotBlank(formData.getRecieverCountry())
                        || StringUtils.isNotBlank(formData.getRecieverCountryName())
                        || StringUtils.isNotBlank(formData.getRecieverIndex())
                        || StringUtils.isNotBlank(formData.getRecieverPoint())
                        || StringUtils.isNotBlank(formData.getRecieverOblast())
                        || StringUtils.isNotBlank(formData.getRecieverStreetNh())
                        || StringUtils.isNotBlank(formData.getRecieverBIN())
                        || StringUtils.isNotBlank(formData.getRecieverIIN())
                        || StringUtils.isNotBlank(formData.getRecieverKatFaceCode())
                        || (formData.getRecieverKatFace() != null)
                        // We are not using this properties for now (not need to check)
                        // || StringUtils.isNotBlank(formData.getRecieverKatFaceCode())
                        // || StringUtils.isNotBlank(formData.getRecieverKatFaceName())
                        || StringUtils.isNotBlank(formData.getRecieverKATO())
                        // We are not using this properties for now (not need to check)
                        // || StringUtils.isNotBlank(formData.getRecieverKATOName())
                        || StringUtils.isNotBlank(formData.getRecieverITNreserv())
                        || StringUtils.isNotBlank(formData.getRecieverKPP());
    }

    private boolean declarantInfoFieldsAreNotNull(FormData formData) {
        if (formData.getDeclarant() == null)
            return false;
        return
                StringUtils.isNotBlank(formData.getDeclarant().getAddress().getAddress())
//                        || StringUtils.isNotBlank(formData.getDeclarantAMNZOU())
//                        || StringUtils.isNotBlank(formData.getDeclarantAMUNN())
//                        || StringUtils.isNotBlank(formData.getDeclarantBYIN())
//                        || StringUtils.isNotBlank(formData.getDeclarantBYUNP())
//                        || StringUtils.isNotBlank(formData.getDeclarantKGINN())
//                        || StringUtils.isNotBlank(formData.getDeclarantKGOKPO())
                    || StringUtils.isNotBlank(formData.getDeclarant().getAddress().getCity())
                    || StringUtils.isNotBlank(formData.getDeclarant().getСountryCode())
                    || StringUtils.isNotBlank(formData.getDeclarant().getIndex())
                    || StringUtils.isNotBlank(formData.getDeclarant().getPersonal().getBin())
                    || StringUtils.isNotBlank(formData.getDeclarant().getPersonal().getIin())
                    || StringUtils.isNotBlank(formData.getDeclarant().getPersonal().getItn())
                    || StringUtils.isNotBlank(formData.getDeclarant().getPersonal().getKato())
                    || StringUtils.isNotBlank(formData.getDeclarant().getPersonal().getPersonsCategory())
                    || StringUtils.isNotBlank(formData.getDeclarant().getName())
                    || StringUtils.isNotBlank(formData.getDeclarant().getAddress().getRegion())
//                        || StringUtils.isNotBlank(formData.getDeclarantRUINN())
//                        || StringUtils.isNotBlank(formData.getDeclarantRUKPP())
//                        || StringUtils.isNotBlank(formData.getDeclarantRUOGRN())
                    || StringUtils.isNotBlank(formData.getDeclarant().getShortName());
    }

    private boolean expeditorInfoFieldsAreNotNull(FormData formData) {
        if (formData.getExpeditor() == null)
            return false;
        return StringUtils.isNotBlank(formData.getExpeditor().getAddress().getAddress())
//                        || StringUtils.isNotBlank(formData.getExpeditorAMNZOU())
//                        || StringUtils.isNotBlank(formData.getExpeditorAMUNN())
//                        || StringUtils.isNotBlank(formData.getExpeditorBYIN())
//                        || StringUtils.isNotBlank(formData.getExpeditorBYUNP())
                        || StringUtils.isNotBlank(formData.getExpeditor().getAddress().getCity())
                        || StringUtils.isNotBlank(formData.getExpeditor().getСountryCode())
                        // || StringUtils.isNotBlank(formData.getExpeditorIndex())
//                        || StringUtils.isNotBlank(formData.getExpeditorKGINN())
//                        || StringUtils.isNotBlank(formData.getExpeditorKGOKPO())
                        || StringUtils.isNotBlank(formData.getExpeditor().getPersonal().getBin())
                        || StringUtils.isNotBlank(formData.getExpeditor().getPersonal().getIin())
                        || StringUtils.isNotBlank(formData.getExpeditor().getPersonal().getItn())
                        || StringUtils.isNotBlank(formData.getExpeditor().getPersonal().getKato())
                        || StringUtils.isNotBlank(formData.getExpeditor().getPersonal().getPersonsCategory())
                        || StringUtils.isNotBlank(formData.getExpeditor().getName())
                        || StringUtils.isNotBlank(formData.getExpeditor().getAddress().getRegion())
//                        || StringUtils.isNotBlank(formData.getExpeditorRUINN())
//                        || StringUtils.isNotBlank(formData.getExpeditorRUKPP())
//                        || StringUtils.isNotBlank(formData.getExpeditorRUOGRN())
                        || StringUtils.isNotBlank(formData.getExpeditor().getShortName());
    }

    private boolean senderInfoFieldsAreNotNull(FormData formData) {
        return StringUtils.isNotBlank(formData.getSenderName()) || StringUtils.isNotBlank(formData.getSenderShortName())
                        || StringUtils.isNotBlank(formData.getSenderCountry())
                        || StringUtils.isNotBlank(formData.getSenderCountryName())
                        || StringUtils.isNotBlank(formData.getSenderIndex())
                        || StringUtils.isNotBlank(formData.getSenderPoint())
                        || StringUtils.isNotBlank(formData.getSenderOblast())
                        || StringUtils.isNotBlank(formData.getSenderStreetNh())
                        || StringUtils.isNotBlank(formData.getSenderBIN())
                        || StringUtils.isNotBlank(formData.getSenderIIN()) || (formData.getSenderKatFace() != null)
                        // We are not using this properties for now (not need to check)
                        // || StringUtils.isNotBlank(formData.getSenderKatFaceCode())
                        // || StringUtils.isNotBlank(formData.getSenderKatFaceName())
                        || StringUtils.isNotBlank(formData.getSenderKATO())
                        // We are not using this properties for now (not need to check)
                        // || StringUtils.isNotBlank(formData.getRecieverKATOName())
                        || StringUtils.isNotBlank(formData.getSenderITNreserv())
                        || StringUtils.isNotBlank(formData.getSenderKpp());
    }

    private NeContainerLists createNeContainerLists(Long invoiceUn, NeContainerLists containerLists,
                    ContainerData containerData/* , Long vagonListUn */) {
        if (containerLists == null) {
            containerLists = new NeContainerLists();
            containerLists.setInvoiceUn(invoiceUn);
        }
        containerLists.setContainerNo(containerData.getNumContainer());
        containerLists.setFilledContainer(containerData.getContainerFilled());
        if (StringUtils.isNotBlank(containerData.getContainerMark())) {
            containerLists.setContainerMark(containerData.getContainerMark().toUpperCase());
        }
        containerLists.setManagUn(containerData.getVagonAccessory());
        containerLists.setConUn(containerData.getContainerCode());
        // containerLists.setVagonListUn(vagonListUn);
        return containerLists;
    }

    private NeSmgsDeclarantInfo createDeclarantInfo(NeSmgsDeclarantInfo declarantInfo, FormData formData,
                    Long invoiceUn) {
        if (declarantInfo == null) {
            declarantInfo = new NeSmgsDeclarantInfo();
        }
        declarantInfo.setInvUn(invoiceUn);
        if (formData.getDeclarant() != null){
            if (formData.getDeclarant().getAddress() != null){
                declarantInfo.setDeclarantAddress(formData.getDeclarant().getAddress().getAddress());
                declarantInfo.setDeclarantCity(formData.getDeclarant().getAddress().getCity());
                declarantInfo.setDeclarantRegion(formData.getDeclarant().getAddress().getRegion());
            }
            if (formData.getDeclarant().getPersonal() != null){
                declarantInfo.setDeclarantKZBin(formData.getDeclarant().getPersonal().getBin());
                declarantInfo.setDeclarantKZIin(formData.getDeclarant().getPersonal().getIin());
                declarantInfo.setDeclarantKZITN(formData.getDeclarant().getPersonal().getItn());
                declarantInfo.setDeclarantKZKATO(formData.getDeclarant().getPersonal().getKato());
                declarantInfo.setDeclarantKZPersonsCategory(formData.getDeclarant().getPersonal().getPersonsCategory());
            }
            declarantInfo.setDeclarantCountry(formData.getDeclarant().getСountryCode());
            declarantInfo.setDeclarantIndex(formData.getDeclarant().getIndex());
            declarantInfo.setDeclarantName(formData.getDeclarant().getName());
            declarantInfo.setDeclarantShortName(formData.getDeclarant().getShortName());
        }

//        declarantInfo.setDeclarantRUINN(formData.getDeclarantRUINN());
//        declarantInfo.setDeclarantRUKPP(formData.getDeclarantRUKPP());
//        declarantInfo.setDeclarantRUOGRN(formData.getDeclarantRUOGRN());
//        declarantInfo.setDeclarantAMNZOU(formData.getDeclarantAMNZOU());
//        declarantInfo.setDeclarantAMUNN(formData.getDeclarantAMUNN());
//        declarantInfo.setDeclarantBYIN(formData.getDeclarantBYIN());
//        declarantInfo.setDeclarantBYUNP(formData.getDeclarantBYUNP());
//        declarantInfo.setDeclarantKGINN(formData.getDeclarantKGINN());
//        declarantInfo.setDeclarantKGOKPO(formData.getDeclarantKGOKPO());

        return declarantInfo;
    }

    private NeSmgsExpeditorInfo createExpeditorInfo(NeSmgsExpeditorInfo expeditorInfo, FormData formData,
                    Long invoiceUn) {
        if (expeditorInfo == null) {
            expeditorInfo = new NeSmgsExpeditorInfo();
        }

        expeditorInfo.setInvUn(invoiceUn);
        if(formData.getExpeditor() != null){
            if(formData.getExpeditor().getAddress() != null){
                expeditorInfo.setExpeditorAddress(formData.getExpeditor().getAddress().getAddress());
                expeditorInfo.setExpeditorRegion(formData.getExpeditor().getAddress().getRegion());
                expeditorInfo.setExpeditorCity(formData.getExpeditor().getAddress().getCity());
            }

            if(formData.getExpeditor().getPersonal() != null){
                expeditorInfo.setExpeditorKZBin(formData.getExpeditor().getPersonal().getBin());
                expeditorInfo.setExpeditorKZIin(formData.getExpeditor().getPersonal().getIin());
                expeditorInfo.setExpeditorKZITN(formData.getExpeditor().getPersonal().getItn());
                expeditorInfo.setExpeditorKZKATO(formData.getExpeditor().getPersonal().getKato());
                expeditorInfo.setExpeditorKZPersonsCategory(formData.getExpeditor().getPersonal().getPersonsCategory());
            }

            expeditorInfo.setExpeditorCountry(formData.getExpeditor().getСountryCode());
            expeditorInfo.setExpeditorIndex(formData.getExpeditor().getIndex());
            expeditorInfo.setExpeditorName(formData.getExpeditor().getName());
            expeditorInfo.setExpeditorShortName(formData.getExpeditor().getShortName());

//        expeditorInfo.setExpeditorRUINN(formData.getExpeditorRUINN());
//        expeditorInfo.setExpeditorRUKPP(formData.getExpeditorRUKPP());
//        expeditorInfo.setExpeditorRUOGRN(formData.getExpeditorRUOGRN());
//        expeditorInfo.setExpeditorAMNZOU(formData.getExpeditorAMNZOU());
//        expeditorInfo.setExpeditorAMUNN(formData.getExpeditorAMUNN());
//        expeditorInfo.setExpeditorBYIN(formData.getExpeditorBYIN());
//        expeditorInfo.setExpeditorBYUNP(formData.getExpeditorBYUNP());
//        expeditorInfo.setExpeditorKGINN(formData.getExpeditorKGINN());
//        expeditorInfo.setExpeditorKGOKPO(formData.getExpeditorKGOKPO());

        }

        return expeditorInfo;
    }

    private NeSmgsSenderInfo createSenderInfo(NeSmgsSenderInfo senderInfo, FormData formData, Long invoiceUn) {
        if (senderInfo == null) {
            senderInfo = new NeSmgsSenderInfo();
        }
        String senderCountryCode = formData.getSenderCountry();
        senderInfo.setInvUn(invoiceUn);
        senderInfo.setSenderCountryCode(senderCountryCode);
        senderInfo.setSenderPostIndex(formData.getSenderIndex());
        senderInfo.setSenderName(formData.getSenderShortName());
        senderInfo.setSenderFullName(formData.getSenderName());
        senderInfo.setSenderRegion(formData.getSenderOblast());
        senderInfo.setSenderSity(formData.getSenderPoint());
        senderInfo.setSenderStreet(formData.getSenderStreetNh());
        senderInfo.setSenderBin(formData.getSenderBIN());
        senderInfo.setSenderIin(formData.getSenderIIN());
        senderInfo.setKpp(formData.getSenderKpp());
        senderInfo.setCategoryType(formData.getSenderKatFace());
        if (formData.getSenderKATO() != null) {
            senderInfo.setKatoType(Long.parseLong(formData.getSenderKATO()));
        }
        if (formData.getSenderITNreserv() != null) {
            senderInfo.setItn(formData.getSenderITNreserv());
        }
        return senderInfo;
    }

    private NeSmgsRecieverInfo createRecieverInfo(NeSmgsRecieverInfo recieverinfo, FormData formData, Long invoiceUn) {
        if (recieverinfo == null) {
            recieverinfo = new NeSmgsRecieverInfo();
        }
        String recieverCountryCode = formData.getRecieverCountry();
        recieverinfo.setInvUn(invoiceUn);
        recieverinfo.setRecieverCountryCode(recieverCountryCode);
        recieverinfo.setRecieverPostIndex(formData.getRecieverIndex());
        recieverinfo.setRecieverName(formData.getRecieverShortNam());
        recieverinfo.setRecieverFullName(formData.getRecieverName());
        recieverinfo.setRecieverSity(formData.getRecieverPoint());
        recieverinfo.setRecieverRegion(formData.getRecieverOblast());
        recieverinfo.setRecieverStreet(formData.getRecieverStreetNh());
        recieverinfo.setRecieverBin(formData.getRecieverBIN());
        recieverinfo.setRecieverIin(formData.getRecieverIIN());
        recieverinfo.setKpp(formData.getRecieverKPP());
        recieverinfo.setCategoryType(formData.getRecieverKatFace());
        if (formData.getRecieverKATO() != null) {
            recieverinfo.setKatoType(Long.parseLong(formData.getRecieverKATO()));
        }
        if (formData.getRecieverITNreserv() != null) {
            recieverinfo.setItn(formData.getRecieverITNreserv());
        }
        return recieverinfo;
    }

    private NeSmgsDestinationPlaceInfo createNeSmgsDestPlaceInfo(NeSmgsDestinationPlaceInfo neSmgsDestinationPlaceInfo,
                    FormData formData, Long invoiceUn) {
        if (neSmgsDestinationPlaceInfo == null) {
            neSmgsDestinationPlaceInfo = new NeSmgsDestinationPlaceInfo();
        }
        neSmgsDestinationPlaceInfo.setInvoiceUn(invoiceUn);
        neSmgsDestinationPlaceInfo.setDestPlace(formData.getDestPlace());
        neSmgsDestinationPlaceInfo.setDestPlaceSta(formData.getDestPlaceStation());
        neSmgsDestinationPlaceInfo.setDestPlaceCountryCode(formData.getDestPlaceCountryCode());
        neSmgsDestinationPlaceInfo.setDestPlaceIndex(formData.getDestPlaceIndex());
        neSmgsDestinationPlaceInfo.setDestPlaceCity(formData.getDestPlacePoint());
        neSmgsDestinationPlaceInfo.setDestPlaceRegion(formData.getDestPlaceOblast());
        neSmgsDestinationPlaceInfo.setDestPlaceStreet(formData.getDestPlaceStreet());
        neSmgsDestinationPlaceInfo.setDestPlaceCustomCode(formData.getDestPlaceCustomCode());
        neSmgsDestinationPlaceInfo.setDestPlaceCustomName(formData.getDestPlaceCustomName());
        neSmgsDestinationPlaceInfo.setDestPlaceCustomOrgUn(formData.getDestPlaceCustomOrgUn());

        System.out.println("orgUN: " + formData.getDestPlaceCustomOrgUn());

        return neSmgsDestinationPlaceInfo;
    }

    private NeSmgsShipList createNeSmgsShipList(NeSmgsShipList neSmgsShipList, FormData formData, Long invoiceUn) {
        if (neSmgsShipList == null) {
            neSmgsShipList = new NeSmgsShipList();
        }
        neSmgsShipList.setInvUn(invoiceUn);
        neSmgsShipList.setNeVesselUn(formData.getVesselUn());

        return neSmgsShipList;
    }

    private NeVagonGroup createNeVagonGroup(NeVagonGroup vagonGroup) {
        if (vagonGroup == null) {
            /*
             * sender_UN =4397050017; st_UN = 3848500007; vag_group_status_UN=4; date_podach-current_timestamp
             */
            vagonGroup = new NeVagonGroup();
            vagonGroup.setSenderUn(4397050017L);
            vagonGroup.setStUn(3848500007L);
            vagonGroup.setVagGroupStatusUn(4L);
            vagonGroup.setDatePodach(new java.sql.Date(new Date().getTime()));
        }
        return vagonGroup;
    }

    private NeVagonLists createNeVagonLists(NeVagonLists neVagonLists, VagonItem vagonItem, Long invoiceUn,
                    Long vagonGroupUn, String owner) {
        if (neVagonLists == null) {
            neVagonLists = new NeVagonLists();
        }
        neVagonLists.setVagGroupUn(vagonGroupUn);
        neVagonLists.setInvcUn(invoiceUn);
        neVagonLists.setVagNo(vagonItem.getNumber());
        neVagonLists.setOwnerRailways(owner);
        return neVagonLists;
    }

    private NeSmgsCargo createNeSmgsCargo(NeSmgsCargo neSmgsCargo, FormData formData, Long invoiceUn) {
        if (neSmgsCargo == null) {
            neSmgsCargo = new NeSmgsCargo();
        }
        neSmgsCargo.setGngCode(formData.getGngCode());
        neSmgsCargo.setInvUn(invoiceUn);
        // neSmgsCargo.setSenderCountry(senderCountry);
        return neSmgsCargo;
    }

    private String getCountryName(String code) {
        List<Country> countrylist = entityManager
                        .createQuery("select a from Country a where a.countryNo = ?1 and a.couEnd > CURRENT_TIMESTAMP",
                                        Country.class)
                        .setParameter(1, code).getResultList();
        if (countrylist.size() > 0) {
            return countrylist.get(0).getCountryName();
        } else {
            return null;
        }
    }

    private String getCountryNameByCode(String code) {
        if (code == null)
            return null;
        String sql = "select country_name from nsi.country where COU_END > current_timestamp and country_no = ?1";
        Query q = entityManager.createNativeQuery(sql);
        q.setParameter(1, code);
        String country = null;
        try {
            country = (String) q.getSingleResult();
        } catch (NoResultException e) {
        }

        return country;
    }

    private NeKatoType getKatoType(String katoType) {
        if (katoType != null) {
            List<NeKatoType> list = entityManager.createQuery(
                            "select a from NeKatoType a where a.katoCode = ?1 AND a.katoEnd > CURRENT_TIMESTAMP",
                            NeKatoType.class).setParameter(1, katoType).getResultList();
            if (list.size() > 0) {
                return list.get(0);
            }
        }
        return null;
    }

    private Long getManagUnByInvoiceUn(Long invoiceUn) {
        Long answer = null;
        java.math.BigInteger s = null;
        String sql = "select MANAG_UN from nsi.MANAGEMENT where MANAG_NO in (select cast(OWNER_RAILWAYS as SMALLINT) from KTZ.NE_VAGON_LISTS where INVC_UN in(?1)) and MANAG_END>CURRENT_TIMESTAMP";
        Query q = entityManager.createNativeQuery(sql);
        q.setParameter(1, invoiceUn);
        try {
            s = (java.math.BigInteger) q.getSingleResult();
            answer = s.longValue();
        } catch (NoResultException e) {
        }
        return answer;
    }

    private NePersonCategoryType getPersonCategoryType(Long categoryType) {
        List<NePersonCategoryType> list = entityManager.createQuery(
                        "select a from NePersonCategoryType a where a.categoryTypeUn = ?1 and a.categoryEnd > CURRENT_TIMESTAMP",
                        NePersonCategoryType.class).setParameter(1, categoryType).getResultList();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private GngModel getGngModel(Long invoiceUn) {
        List<GngModel> gngModelList = null;
        Query query = entityManager.createNativeQuery(
                "select a.SMGS_CARGO_UN as id, a.INV_UN as invoiceUn,a.GNG_CODE as code, b.CARGO_SHORTNAME1 as shortName1 from KTZ.NE_SMGS_CARGO a "
                        + "left join NSI.CARGO_GNG b on a.GNG_CODE = b.CARGO_GROUP "
                        + "where a.INV_UN = ?1 and b.C_GN_END > current_timestamp "
                        + "and b.CARGO_SHORTNAME1 is not null "
                        + "fetch first 1 rows only",
                GngModel.class);
        query.setParameter(1, invoiceUn);
        gngModelList = query.getResultList();
        if (gngModelList != null && gngModelList.size() > 0) {
            if (gngModelList.get(0) == null)
                return gngModelList.get(1);
            return gngModelList.get(0);

        }
        return null;
    }

    @Override
    @Transactional
    public void saveDocInfo(String invoiceId, String filename, Date date, String uuid) {
        NeSmgsAdditionDocuments document = new NeSmgsAdditionDocuments();
        document.setInvUn(Long.parseLong(invoiceId));
        document.setDocDate(date);
        document.setDocName(filename);
        document.setFileUuid(uuid);
        entityManager.persist(document);
    }

    @Override
    public boolean checkExpeditorCode(Long code) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM nsi.forwarder WHERE forwarder_exp_no=?1");
        query.setParameter(1, code);
        return Long.parseLong("" + query.getResultList().get(0)) > 0;
    }

}
