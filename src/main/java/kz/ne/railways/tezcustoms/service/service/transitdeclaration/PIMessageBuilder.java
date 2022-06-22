package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.entity.asudkr.*;
import kz.ne.railways.tezcustoms.service.service.bean.PrevInfoBeanDAOLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.customs.commonaggregatetypes._5_10.*;
import ru.customs.cuesadcommonaggregatetypescust._5_11.ContainerNumberType;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class PIMessageBuilder implements PIMessageBuilderLocal {

    public static final String DOCUMENT_LANGUAGE = "RU";
    public static final String DOCUMENT_NAME = "Железнодорожная накладная";
    public static final String DOCUMENT_CODE = "02013";
    public static final long KTZH_LEL_PERS_UN = 4227200013L;
    public static final long KTZH_LEL_PERS_UN_FERRYMID = 772956L;
    public static final String CUSTOMS_COUNTRY_CODE = "398";

    @Autowired
    private PrevInfoBeanDAOLocal dao;

    private NeInvoice invoice;
    private NeInvoiceInfo invoiceInfo;
    private NeInvoicePrevInfo invoicePrevInfo;
    private List<NeVagonLists> vagonLists;
    private List<NeContainerLists> containerList;
    private List<NeSmgsTnVed> tnVedList;
    private NeSmgsSenderInfo senderInfo;
    private NeSmgsRecieverInfo recieverInfo;

    private NeSmgsCargo neSmgsCargo = null;
    private NeVagonGroup vagonGroup = null;
    private NeSmgsDestinationPlaceInfo neSmgsDestinationPlaceInfo = null;
    private NeSmgsDeclarantInfo declarantInfo;
    private NeSmgsExpeditorInfo expeditorInfo;

    private Long invoiceUn;
    private PIRWInformationCUType informationCUType;

    private Map<Long, NeUnitType> unitTypeMap;
    private Map<String, String> containers;

    @Value("${services.external.astana1.person.name}")
    private String customsPersName;
    @Value("${services.external.astana1.person.sname}")
    private String customsPersSName;
    @Value("${services.external.astana1.person.addr}")
    private String customsPersAddress;
    @Value("${services.external.astana1.person.city}")
    private String customsPersCity;
    @Value("${services.external.astana1.person.BIN}")
    private String customsPersBIN;
    @Value("${services.external.astana1.person.RNN}")
    private String customsPersRNN;

    public void setUp() {
        initUnitType();
        initContainers();
    }

    public Long getInvoiceUn() {
        return invoiceUn;
    }

    public void setInvoiceUn(Long invoiceUn) {
        this.invoiceUn = invoiceUn;
    }

    public NeInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(NeInvoice invoice) {
        this.invoice = invoice;
    }

    public NeInvoiceInfo getInvoiceInfo() {
        return invoiceInfo;
    }

    public void setInvoiceInfo(NeInvoiceInfo invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }

    public List<NeVagonLists> getVagonLists() {
        return vagonLists;
    }

    public void setVagonLists(List<NeVagonLists> vagonLists) {
        this.vagonLists = vagonLists;
    }

    public List<NeSmgsTnVed> getTnVedList() {
        return tnVedList;
    }

    public void setTnVedList(List<NeSmgsTnVed> tnVedList) {
        this.tnVedList = tnVedList;
    }

    public PIRWInformationCUType getInformationCUType() {
        return informationCUType;
    }

    public void setInformationCUType(PIRWInformationCUType informationCUType) {
        this.informationCUType = informationCUType;
    }

    public PrevInfoBeanDAOLocal getDao() {
        return dao;
    }

    public void setDao(PrevInfoBeanDAOLocal dao) {
        this.dao = dao;
    }

    public NeInvoicePrevInfo getInvoicePrevInfo() {
        return invoicePrevInfo;
    }

    public void setInvoicePrevInfo(NeInvoicePrevInfo invoicePrevInfo) {
        this.invoicePrevInfo = invoicePrevInfo;
    }

    public NeSmgsSenderInfo getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(NeSmgsSenderInfo senderInfo) {
        this.senderInfo = senderInfo;
    }

    public NeSmgsRecieverInfo getRecieverInfo() {
        return recieverInfo;
    }

    public void setRecieverInfo(NeSmgsRecieverInfo recieverInfo) {
        this.recieverInfo = recieverInfo;
    }

    public NeSmgsCargo getNeSmgsCargo() {
        return neSmgsCargo;
    }

    public void setNeSmgsCargo(NeSmgsCargo neSmgsCargo) {
        this.neSmgsCargo = neSmgsCargo;
    }

    public NeVagonGroup getVagonGroup() {
        return vagonGroup;
    }

    public void setVagonGroup(NeVagonGroup vagonGroup) {
        this.vagonGroup = vagonGroup;
    }

    public NeSmgsDestinationPlaceInfo getNeSmgsDestinationPlaceInfo() {
        return neSmgsDestinationPlaceInfo;
    }

    public void setNeSmgsDestinationPlaceInfo(NeSmgsDestinationPlaceInfo neSmgsDestinationPlaceInfo) {
        this.neSmgsDestinationPlaceInfo = neSmgsDestinationPlaceInfo;
    }

    public Map<Long, NeUnitType> getUnitTypeMap() {
        return unitTypeMap;
    }

    public void setUnitTypeMap(Map<Long, NeUnitType> unitTypeMap) {
        this.unitTypeMap = unitTypeMap;
    }

    public PIRWInformationCUType build(Long invoiceUn) {
        setInvoiceUn(invoiceUn);
        readData(invoiceUn);
        informationCUType = buildInformationCUType();
        buildGoodsList();
        informationCUType.setTrainInfo(buildTraininfo());
        return informationCUType;
    }

    private void readData(Long id) {
        invoice = dao.getInvoice(id);
        invoicePrevInfo = dao.getInvoicePrevInfo(id);
        senderInfo = dao.getSenderInfo(id);
        recieverInfo = dao.getRecieverInfo(id);
        neSmgsDestinationPlaceInfo = dao.getNeSmgsDestinationPlaceInfo(id);
        tnVedList = dao.getGridDatas(id);
        vagonGroup = dao.getVagonGroup(id);
        vagonLists = dao.getVagonList(id);
        neSmgsCargo = dao.getNeSmgsCargo(id);
        declarantInfo = dao.getDeclarantInfo(id);
        expeditorInfo = dao.getExpeditorInfo(id);
        containerList = dao.getContinerList(id);
        setUp();
    }

    private PIRWInformationCUType buildInformationCUType() {
        informationCUType = new PIRWInformationCUType();
        informationCUType.setCheckPointInfo(null);
        informationCUType.setPIPurpose(new BigDecimal("1"));
        if (invoicePrevInfo != null) {
            if (invoicePrevInfo.getArriveTime() != null) {
                informationCUType.setDateExpectedArrival(convertDate(invoicePrevInfo.getArriveTime()));
                informationCUType.setTimeExpectedArrival(convertTime(invoicePrevInfo.getArriveTime()));
            }

            // TODO:aha Disable PIPurpose 04.07.2016
            // if (invoicePrevInfo.getPrevInfoType() != null) {
            // informationCUType.setPIPurpose(new BigDecimal(invoicePrevInfo
            // .getPrevInfoType()));
            // }
        }
        informationCUType.setRefDocumentID(null);
        informationCUType.setDocumentID(UUID.randomUUID().toString());
        informationCUType.setDocumentModeID((Double.valueOf(Math.random())).toString());
        informationCUType.setLanguageCode(DOCUMENT_LANGUAGE);
        informationCUType.setTrainInfo(buildTraininfo());
        informationCUType.setUINP(UUID.randomUUID().toString());
        return informationCUType;
    }

    private TrainType buildTraininfo() {
        TrainType result = null;
        if (invoice.getTrainIndex() != null) {
            result = new TrainType();
            result.setPPVNumber(null);
            result.setTrainIndex(invoice.getTrainIndex());
            result.setTrainNumber(null);
            for (NeVagonLists vagon : vagonLists) {
                CarriageType item = new CarriageType();
                item.setAddInformaition(null);
                item.setCarriageNumber(vagon.getVagNo());
                item.setCarriageNumeric(null);
                item.setCountryRegistrationCode(null);
                item.setDepartureStation(null);
                item.setDestinationStation(null);
                // item.setEmptyIndicator(null);
                item.setGoodsWeightQuantity(null);
                result.getCarriageInfo().add(item);
            }
        }

        return result;
    }

    private void buildGoodsList() {
        if (informationCUType != null) {
            informationCUType.getPICUGoodsShipment().add(buildGoodItem());
        }
    }

    private String replaceSpacSymbol(String string) {
        return string.replaceAll("&", "and").replace("«", "\"").replace("»", "\"");
    }

    private PICUGoodsShipmentType buildGoodItem() {

        PICUGoodsShipmentType result = new PICUGoodsShipmentType();
        CUOrganizationType consignee = new CUOrganizationType();
        CUOrganizationType consignor = new CUOrganizationType();
        CUOrganizationType declarant = new CUOrganizationType();
        PICUConsignmentType consignmentType = new PICUConsignmentType();
        PICUPresentedDocType presentedDocType = new PICUPresentedDocType();
        FreightOperationsType freightOperationsType = new FreightOperationsType();

        AddressType addressType;
        ContactType contactType;
        RWStationType stationType;
        CUCustomsType customsOffice;
        RKOrganizationFeaturesType rkFeaturesType;
        RFOrganizationFeaturesType rfFeaturesType;
        RAOrganizationFeaturesType raFeaturesType;
        RBOrganizationFeaturesType rbFeaturesType;
        KGOrganizationFeaturesType kgFeaturesType;
        ITNKZType itnkz;
        Country country;
        Sta sta1;
        Sta sta2;
        Management management1;
        Management management2;
        NeCustomsOrgs customsOrgs;
        String dispatchCountryNo;
        DestinationPlaceType destinationPlaceType;

        // Документ

        presentedDocType.setPrDocumentDate(convertDate(invoice.getInvDate()));
        presentedDocType.setPrDocumentNumber(invoice.getInvcNum() != null ? invoice.getInvcNum() : "БН");

        // presentedDocType.setPrDocumentName(DOCUMENT_NAME);
        int limit = 250;
        String string = "";
        if (DOCUMENT_NAME != null)
            string = DOCUMENT_NAME.length() > limit ? DOCUMENT_NAME.substring(0, limit) : DOCUMENT_NAME;
        presentedDocType.setPrDocumentName(string);
        presentedDocType.setPresentedDocumentModeCode(DOCUMENT_CODE);

        if (senderInfo != null && recieverInfo != null) {

            // Заполняем отправителя
            consignor.setOrganizationName(replaceSpacSymbol(senderInfo.getSenderFullName()));
            consignor.setShortName(replaceSpacSymbol(senderInfo.getSenderName()));
            consignor.setOrganizationLanguage("RU");

            // Адрес
            addressType = new AddressType();

            country = dao.getCountry(senderInfo.getSenderCountryCode());
            if (country != null) {
                addressType.setCountryCode(country.getCountryId());
                addressType.setCounryName(country.getCountryName());
                addressType.setRegion(senderInfo.getSenderRegion());
                addressType.setCity(senderInfo.getSenderSity());
                addressType.setStreetHouse(senderInfo.getSenderStreet());
                // addressType.setPostalCode(senderInfo.getSenderPostIndex());
                addressType.setPostalCode("000000");
            }

            consignor.setAddress(addressType);

            // Контактные данные
            contactType = new ContactType();
            contactType.getPhone().add(senderInfo.getSenderTelephoneNum());
            contactType.setFax(senderInfo.getSenderTelefaxNum());
            contactType.getEMail().add(senderInfo.getSenderEmail());

            consignor.setContact(contactType);

            // Получатель
            consignee.setOrganizationName(replaceSpacSymbol(recieverInfo.getRecieverFullName()));
            consignee.setShortName(replaceSpacSymbol(recieverInfo.getRecieverName()));
            consignee.setOrganizationLanguage("RU");

            // Адрес
            addressType = new AddressType();
            country = dao.getCountry(recieverInfo.getRecieverCountryCode());
            if (country != null) {
                addressType.setCountryCode(country.getCountryId());
                addressType.setCounryName(country.getCountryName());
                addressType.setRegion(recieverInfo.getRecieverRegion());
                addressType.setCity(recieverInfo.getRecieverSity());
                addressType.setStreetHouse(recieverInfo.getRecieverStreet());
                // addressType.setPostalCode(recieverInfo.getRecieverPostIndex());
                addressType.setPostalCode("000000");
            }
            consignee.setAddress(addressType);

            // Контактные данные
            contactType = new ContactType();
            contactType.getPhone().add(recieverInfo.getRecieverTelephoneNum());
            contactType.setFax(recieverInfo.getRecieverTelefaxNum());
            contactType.getEMail().add(recieverInfo.getRecieverEmail());
            consignee.setContact(contactType);

            // Станции
            consignmentType = new PICUConsignmentType();

            // Откуда
            stationType = new RWStationType();

            sta1 = dao.getStation(invoice.getReciveStationCode());
            management1 = dao.getManagement(sta1.getManagUn());

            stationType.setStationCode(sta1.getStaNo());
            stationType.setStationName(sta1.getStaName());
            stationType.setRailwayCode(management1.getMNameLat());

            consignmentType.setDepartureStation(stationType);

            country = dao.getCountry(management1.getCouUn());
            dispatchCountryNo = country.getCountryNo();

            Country countryStart =
                            (invoicePrevInfo != null ? dao.getCountry(invoicePrevInfo.getStartStaCouNo()) : null);
            consignmentType.setDispatchCountryCode(
                            countryStart != null ? countryStart.getCountryId() : country.getCountryId());
            consignmentType.setDispatchCountryName(
                            countryStart != null ? countryStart.getCountryFullName() : country.getCountryFullName());

            // Сведения об организации.
            if ("KZ".equals(country.getCountryId())) {
                rkFeaturesType = new RKOrganizationFeaturesType();
                rkFeaturesType.setBIN(senderInfo.getSenderBin());
                rkFeaturesType.setIIN(senderInfo.getSenderIin());
                itnkz = new ITNKZType();
                itnkz.setCategoryCode(getCategoryCode(senderInfo.getCategoryType()));
                itnkz.setITNReserv(senderInfo.getItn());
                itnkz.setKATOCode(senderInfo.getKatoType() != null ? senderInfo.getKatoType().toString() : null);
                itnkz.setRNN(null);
                rkFeaturesType.setITN(itnkz);
                consignor.setRKOrganizationFeatures(rkFeaturesType);
            } else if ("RU".equals(country.getCountryId())) {
                rfFeaturesType = new RFOrganizationFeaturesType();
                rfFeaturesType.setOGRN(senderInfo.getSenderBin());
                rfFeaturesType.setINN(senderInfo.getSenderIin());
                rfFeaturesType.setKPP(senderInfo.getKpp());
                consignor.setRFOrganizationFeatures(rfFeaturesType);
            } else if ("AM".equals(country.getCountryId())) {
                raFeaturesType = new RAOrganizationFeaturesType();
                raFeaturesType.setSocialServiceNumber(senderInfo.getSenderBin());
                raFeaturesType.setUNN(senderInfo.getSenderIin());
                consignor.setRAOrganizationFeatures(raFeaturesType);
            } else if ("BY".equals(country.getCountryId())) {
                rbFeaturesType = new RBOrganizationFeaturesType();
                rbFeaturesType.setRBIdentificationNumber(senderInfo.getSenderIin());
                rbFeaturesType.setUNP(senderInfo.getSenderBin());
                consignor.setRBOrganizationFeatures(rbFeaturesType);
            } else if ("KG".equals(country.getCountryId())) {
                kgFeaturesType = new KGOrganizationFeaturesType();
                kgFeaturesType.setKGINN(senderInfo.getSenderIin());
                kgFeaturesType.setKGOKPO(senderInfo.getSenderBin());
                consignor.setKGOrganizationFeatures(kgFeaturesType);
            }
            // Куда
            stationType = new RWStationType();
            sta2 = dao.getStation(invoice.getDestStationCode());
            management2 = dao.getManagement(sta2.getManagUn());

            stationType.setStationCode(sta2.getStaNo());
            stationType.setStationName(sta2.getStaName());
            stationType.setRailwayCode(management2.getMNameLat());

            destinationPlaceType = new DestinationPlaceType();
            AddressType address = new AddressType();
            address.setCity(neSmgsDestinationPlaceInfo.getDestPlaceCity());
            // address.setCounryName();
            Country countryCode = dao.getCountryBycode(neSmgsDestinationPlaceInfo.getDestPlaceCountryCode());
            address.setCountryCode(countryCode.getCountryId());
            // address.setPostalCode(neSmgsDestinationPlaceInfo.getDestPlaceIndex());
            address.setPostalCode("000000");
            address.setRegion(neSmgsDestinationPlaceInfo.getDestPlaceRegion());
            address.setStreetHouse(neSmgsDestinationPlaceInfo.getDestPlaceStreet());
            // address.setTerritoryCode();
            destinationPlaceType.setAddress(address);
            destinationPlaceType.setDestinationPlaceInfo(neSmgsDestinationPlaceInfo.getDestPlace());

            // Станция назначения
            if (BigDecimal.ONE.equals(informationCUType.getPIPurpose())) {
                consignmentType.setDestinationPlace(destinationPlaceType);// todo Заполнить! THIS PLACE !!!
            } else {
                consignmentType.setDestinationStation(stationType);
            }
            country = dao.getCountry(management2.getCouUn());

            Country countryDest =
                            (invoicePrevInfo != null ? dao.getCountry(invoicePrevInfo.getDestStationCouNo()) : null);
            consignmentType.setDestinationCountryCode(
                            countryDest != null ? countryDest.getCountryId() : country.getCountryId());
            consignmentType.setDestinationCountryName(
                            countryDest != null ? countryDest.getCountryFullName() : country.getCountryFullName());

            consignmentType.setContainerIndicator(invoice.getIsContainer() == 1 ? true : false);

            // Добавляем вагоны и контейнеры!
            consignmentType.setPICUTransportMeans(buildsetPICUTransportMeans());

            // Сведения об организации.
            if ("KZ".equals(country.getCountryId())) {
                rkFeaturesType = new RKOrganizationFeaturesType();
                rkFeaturesType = new RKOrganizationFeaturesType();
                rkFeaturesType.setBIN(StringUtils.isEmpty(recieverInfo.getRecieverBin()) ? null
                                : recieverInfo.getRecieverBin());
                rkFeaturesType.setIIN(StringUtils.isEmpty(recieverInfo.getRecieverIin()) ? null
                                : recieverInfo.getRecieverIin());
                itnkz = new ITNKZType();
                itnkz.setCategoryCode(getCategoryCodeReciever(recieverInfo.getCategoryType()));
                itnkz.setITNReserv(recieverInfo.getItn());
                itnkz.setKATOCode(recieverInfo.getKatoType() != null ? recieverInfo.getKatoType().toString() : null);
                itnkz.setRNN(null);
                rkFeaturesType.setITN(itnkz);
                consignee.setRKOrganizationFeatures(rkFeaturesType);
            } else if ("RU".equals(country.getCountryId())) {
                rfFeaturesType = new RFOrganizationFeaturesType();
                rfFeaturesType.setOGRN(recieverInfo.getRecieverBin());
                rfFeaturesType.setINN(recieverInfo.getRecieverIin());
                rfFeaturesType.setKPP(recieverInfo.getKpp());
                consignee.setRFOrganizationFeatures(rfFeaturesType);
            } else if ("AM".equals(country.getCountryId())) {
                raFeaturesType = new RAOrganizationFeaturesType();
                raFeaturesType.setSocialServiceNumber(recieverInfo.getRecieverBin());
                raFeaturesType.setUNN(recieverInfo.getRecieverIin());
                consignee.setRAOrganizationFeatures(raFeaturesType);
            } else if ("BY".equals(country.getCountryId())) {
                rbFeaturesType = new RBOrganizationFeaturesType();
                rbFeaturesType.setRBIdentificationNumber(recieverInfo.getRecieverIin());
                rbFeaturesType.setUNP(recieverInfo.getRecieverBin());
                consignee.setRBOrganizationFeatures(rbFeaturesType);
            } else if ("KG".equals(country.getCountryId())) {
                kgFeaturesType = new KGOrganizationFeaturesType();
                kgFeaturesType.setKGINN(recieverInfo.getRecieverIin());
                kgFeaturesType.setKGOKPO(recieverInfo.getRecieverBin());
                consignee.setKGOrganizationFeatures(kgFeaturesType);
            }

            // Сведения о проведении грузовых операций

            // Таможня
            customsOffice = new CUCustomsType();
            RWStationType customStation = null;
            if (invoicePrevInfo != null) {
                customsOrgs = dao.getCustomsOrgs(invoicePrevInfo.getCustomOrgUn());
                customsOffice.setCode(customsOrgs.getCustomCode());
                customsOffice.setOfficeName(customsOrgs.getCustomName());
                customsOffice.setCustomsCountryCode(CUSTOMS_COUNTRY_CODE);
                Sta arriveSta = dao.getStation(invoicePrevInfo.getArriveStaNo());
                if (arriveSta != null) {
                    Management managementArriveSta = dao.getManagement(arriveSta.getManagUn());
                    customStation = new RWStationType();
                    customStation.setStationCode(arriveSta.getStaNo());
                    customStation.setStationName(arriveSta.getStaName());
                    customStation.setRailwayCode(
                                    managementArriveSta != null ? managementArriveSta.getMNameLat() : null);
                }
            }

            freightOperationsType.setOperationRWStation(customStation);
            freightOperationsType.setOperationCustomsOffice(customsOffice);
            freightOperationsType.getOperationDescription().add("Перегруз"); // todo Добавить правильное описание!

        }

        // Декларант/ Экседитор
        if (expeditorInfo != null)
            declarant = buildExpeditor();
        else
            declarant = buildDeclarant();


        // Добавляем ранее заполненные данные
        consignmentType.setContainerIndicator(invoice.getIsContainer() == 1 ? true : false);

        result.getFreightOperations().add(freightOperationsType);
        result.setTransportDocument(presentedDocType);
        result.setPICUConsignment(consignmentType);
        result.setConsignee(consignee);
        result.setConsignor(consignor);
        result.setDeclarant(declarant);
        result.setCarrier(getKTZH());

        long i = 1L;
        BigDecimal totalValue = new BigDecimal(0);
        String currencyCode = null;
        if (tnVedList != null && !tnVedList.isEmpty()) {
            Map<String, PIRWGoodsType> goodsMap = new HashMap<String, PIRWGoodsType>();
            List<NeSmgsTnVed> tnVedSort = new ArrayList<NeSmgsTnVed>();
            for (NeSmgsTnVed tnVedRow : tnVedList) {
                /*
                 * PIRWGoodsType goods = buildGoods(tnVedRow); goods.setGoodsNumeric(BigInteger.valueOf(i++));
                 * result.getPIGoods().add(goods); if (currencyCode == null && goods != null &&
                 * goods.getCurrencyCode() != null) { currencyCode = goods.getCurrencyCode(); } totalValue =
                 * totalValue.add(goods.getInvoiceValue());
                 */

                // **************Формирование кодов ТНВЭД*****************

                PIRWGoodsType goods = buildGoods(tnVedRow);
                tnVedSort.add(tnVedRow);
                goodsMap.put(tnVedRow.getTnVedCode(), goods);
            }

            // TODO: нужна сортировка!
            for (NeSmgsTnVed tnVedRow : tnVedSort) {
                // for(String key: goodsMap.keySet()) {
                // System.out.println("TNVED key: " + tnVedRow.getTnVedCode());
                PIRWGoodsType goods = goodsMap.get(tnVedRow.getTnVedCode());
                goods.setGoodsNumeric(BigInteger.valueOf(i++));
                result.getPIGoods().add(goods);
                totalValue = totalValue.add(goods.getInvoiceValue());
            }
        }
        result.setTotalInvoiceValue(totalValue);
        result.setCurrencyCode(currencyCode);

        return result;
    }

    private PIRWGoodsType addGoods(PIRWGoodsType result, TnVedRow tnVedRow) {
        result.setGrossWeightQuantity(result.getGrossWeightQuantity().add(tnVedRow.getBrutto()));
        result.setInvoiceValue(result.getInvoiceValue().add(tnVedRow.getPriceByTotal()));
        result.setNetWeightQuantity(result.getNetWeightQuantity().add(tnVedRow.getNetto()));
        if (tnVedRow.getUnitTypeUn() != null) {
            SupplementaryQuantityType supplementaryQuantityType = new SupplementaryQuantityType();
            NeUnitType unitType = unitTypeMap.get(tnVedRow.getUnitTypeUn());
            supplementaryQuantityType.setGoodsQuantity(tnVedRow.getCountByUnit());
            supplementaryQuantityType.setMeasureUnitQualifierCode(unitType.getUnitCode());
            supplementaryQuantityType.setMeasureUnitQualifierName(unitType.getUnitName());
            result.getSupplementaryQuantity().add(supplementaryQuantityType);
        }

        PIGoodsPackagingType packagingType = result.getPIGoodsPackaging();
        packagingType.setPakageQuantity(packagingType.getPakageQuantity()
                        .add(tnVedRow.getPackingCount() != null ? tnVedRow.getPackingCount() : BigDecimal.ZERO));
        packagingType.setPakagePartQuantity(packagingType.getPakagePartQuantity().add(
                        tnVedRow.getPakagePartQuantity() != null ? tnVedRow.getPakagePartQuantity() : BigDecimal.ZERO));
        PIGoodsPackingInformationType goodsPackingInformationType = new PIGoodsPackingInformationType();
        goodsPackingInformationType.setPackingCode(tnVedRow.getPackingCode());
        goodsPackingInformationType.setPakingQuantity(
                        tnVedRow.getPackingCount() != null ? BigInteger.valueOf(tnVedRow.getPackingCount().longValue())
                                        : null);
        goodsPackingInformationType.setPackageMark(tnVedRow.getPlaceCargoMark());

        packagingType.getPackingInformation().add(goodsPackingInformationType);

        addContainer(result, tnVedRow);

        List<NeSmgsTnVedDocuments> docs = dao.getSmgsTnVedDocuments(tnVedRow.getId());
        if (docs != null) {
            for (NeSmgsTnVedDocuments doc : docs) {
                result.getPresentedDocument().add(buildPICUPresentedDoc(doc));
            }
        }

        result.setPIGoodsPackaging(packagingType);

        return result;
    }

    private void addContainer(PIRWGoodsType result, TnVedRow tnVedRow) {
        String[] containersTnVed = (tnVedRow.getContainer() != null ? tnVedRow.getContainer().split(",") : null);
        if (!containerList.isEmpty() && !containers.isEmpty() && invoice.getIsContainer() == 1) {// По новой логике
            for (String key : containers.keySet()) {
                ContainerNumberType container = new ContainerNumberType();
                container.setContainerIdentificaror(key);
                container.setFullIndicator(containers.get(key));
                if (!haveContainer(result.getContainerNumber(), container)) { // Проверяем был ли добавлен уже такой
                                                                              // контейнер
                    result.getContainerNumber().add(container);
                }
            }
        } else if (containersTnVed != null) { // По старой логике
            for (String item : containersTnVed) {
                String itemTrim = item.trim();
                if (!StringUtils.isEmpty(itemTrim) && !containsContiner(result.getContainerNumber(), itemTrim)) {
                    ContainerNumberType container = new ContainerNumberType();
                    container.setContainerIdentificaror(itemTrim);
                    if (invoice.getIsContainer() == 1) {
                        container.setFullIndicator(containers.get(itemTrim));
                    }
                    if (!haveContainer(result.getContainerNumber(), container)) { // Проверяем был ли добавлен уже такой
                                                                                  // контейнер
                        result.getContainerNumber().add(container);
                    }
                }
            }
        }
    }

    private boolean haveContainer(List<ContainerNumberType> containerNumber, ContainerNumberType container) {
        if (!containerNumber.isEmpty() && container != null) {
            for (ContainerNumberType item : containerNumber) {
                if (item.getContainerIdentificaror() != null && container.getContainerIdentificaror() != null
                                && item.getContainerIdentificaror().equals(container.getContainerIdentificaror())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsContiner(List<ContainerNumberType> containerNumber, String itemTrim) {
        boolean result = false;
        if (!StringUtils.isEmpty(itemTrim)) {
            for (ContainerNumberType item : containerNumber) {
                if (itemTrim.equals(item.getContainerIdentificaror())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private CUOrganizationType buildDeclarant() {
        if (declarantInfo != null) {
            CUOrganizationType result = new CUOrganizationType();
            RKOrganizationFeaturesType rkFeaturesType;
            RFOrganizationFeaturesType rfFeaturesType;
            RAOrganizationFeaturesType raFeaturesType;
            RBOrganizationFeaturesType rbFeaturesType;
            KGOrganizationFeaturesType kgFeaturesType;
            ITNKZType itnkz;

            result.setOrganizationLanguage("RU");
            result.setOrganizationName(replaceSpacSymbol(declarantInfo.getDeclarantName()));
            result.setShortName(replaceSpacSymbol(declarantInfo.getDeclarantShortName()));

            AddressType addressType = new AddressType();

            Country country = dao.getCountry(declarantInfo.getDeclarantCountry());
            if (country != null) {
                addressType.setCounryName(country.getCountryFullName());
                addressType.setCountryCode(country.getCountryId());
            }

            // addressType.setPostalCode(declarantInfo.getDeclarantIndex());
            addressType.setPostalCode("000000");
            addressType.setCity(declarantInfo.getDeclarantCity());
            addressType.setRegion(declarantInfo.getDeclarantRegion());
            addressType.setStreetHouse(declarantInfo.getDeclarantAddress());

            result.setAddress(addressType);

            if ("KZ".equals(country.getCountryId())) {
                rkFeaturesType = new RKOrganizationFeaturesType();
                rkFeaturesType = new RKOrganizationFeaturesType();
                rkFeaturesType.setBIN(declarantInfo.getDeclarantKZBin());
                rkFeaturesType.setIIN(declarantInfo.getDeclarantKZIin());
                itnkz = new ITNKZType();
                itnkz.setCategoryCode(declarantInfo.getDeclarantKZPersonsCategory());
                itnkz.setITNReserv(declarantInfo.getDeclarantKZITN());
                itnkz.setKATOCode(declarantInfo.getDeclarantKZKATO());
                itnkz.setRNN(null);
                rkFeaturesType.setITN(itnkz);
                result.setRKOrganizationFeatures(rkFeaturesType);
            } else if ("RU".equals(country.getCountryId())) {
                rfFeaturesType = new RFOrganizationFeaturesType();
                rfFeaturesType.setOGRN(declarantInfo.getDeclarantRUOGRN());
                rfFeaturesType.setINN(declarantInfo.getDeclarantRUINN());
                rfFeaturesType.setKPP(declarantInfo.getDeclarantRUKPP());
                result.setRFOrganizationFeatures(rfFeaturesType);
            } else if ("AM".equals(country.getCountryId())) {
                raFeaturesType = new RAOrganizationFeaturesType();
                raFeaturesType.setSocialServiceNumber(declarantInfo.getDeclarantAMNZOU());
                raFeaturesType.setUNN(declarantInfo.getDeclarantAMUNN());
                result.setRAOrganizationFeatures(raFeaturesType);
            } else if ("BY".equals(country.getCountryId())) {
                rbFeaturesType = new RBOrganizationFeaturesType();
                rbFeaturesType.setRBIdentificationNumber(declarantInfo.getDeclarantBYIN());
                rbFeaturesType.setUNP(declarantInfo.getDeclarantBYUNP());
                result.setRBOrganizationFeatures(rbFeaturesType);
            } else if ("KG".equals(country.getCountryId())) {
                kgFeaturesType = new KGOrganizationFeaturesType();
                kgFeaturesType.setKGINN(declarantInfo.getDeclarantKGINN());
                kgFeaturesType.setKGOKPO(declarantInfo.getDeclarantKGOKPO());
                result.setKGOrganizationFeatures(kgFeaturesType);
            }
            return result;
        }
        return null;
    }

    private CUOrganizationType buildExpeditor() {
        if (expeditorInfo != null) {
            CUOrganizationType result = new CUOrganizationType();
            RKOrganizationFeaturesType rkFeaturesType;
            RFOrganizationFeaturesType rfFeaturesType;
            RAOrganizationFeaturesType raFeaturesType;
            RBOrganizationFeaturesType rbFeaturesType;
            KGOrganizationFeaturesType kgFeaturesType;
            ITNKZType itnkz;

            result.setOrganizationLanguage("RU");
            result.setOrganizationName(replaceSpacSymbol(expeditorInfo.getExpeditorName()));
            result.setShortName(replaceSpacSymbol(expeditorInfo.getExpeditorShortName()));

            AddressType addressType = new AddressType();

            Country country = dao.getCountry(expeditorInfo.getExpeditorCountry());
            if (country != null) {
                addressType.setCounryName(country.getCountryFullName());
                addressType.setCountryCode(country.getCountryId());
            }

            // addressType.setPostalCode(expeditorInfo.getExpeditorIndex());
            addressType.setPostalCode("000000");
            addressType.setCity(expeditorInfo.getExpeditorCity());
            addressType.setRegion(expeditorInfo.getExpeditorRegion());
            addressType.setStreetHouse(expeditorInfo.getExpeditorAddress());

            result.setAddress(addressType);

            if ("KZ".equals(country.getCountryId())) {
                rkFeaturesType = new RKOrganizationFeaturesType();
                rkFeaturesType = new RKOrganizationFeaturesType();
                rkFeaturesType.setBIN(expeditorInfo.getExpeditorKZBin());
                rkFeaturesType.setIIN(expeditorInfo.getExpeditorKZIin());
                itnkz = new ITNKZType();
                itnkz.setCategoryCode(expeditorInfo.getExpeditorKZPersonsCategory());
                itnkz.setITNReserv(expeditorInfo.getExpeditorKZITN());
                itnkz.setKATOCode(expeditorInfo.getExpeditorKZKATO());
                itnkz.setRNN(null);
                rkFeaturesType.setITN(itnkz);
                result.setRKOrganizationFeatures(rkFeaturesType);
            } else if ("RU".equals(country.getCountryId())) {
                rfFeaturesType = new RFOrganizationFeaturesType();
                rfFeaturesType.setOGRN(expeditorInfo.getExpeditorRUOGRN());
                rfFeaturesType.setINN(expeditorInfo.getExpeditorRUINN());
                rfFeaturesType.setKPP(expeditorInfo.getExpeditorRUKPP());
                result.setRFOrganizationFeatures(rfFeaturesType);
            } else if ("AM".equals(country.getCountryId())) {
                raFeaturesType = new RAOrganizationFeaturesType();
                raFeaturesType.setSocialServiceNumber(expeditorInfo.getExpeditorAMNZOU());
                raFeaturesType.setUNN(expeditorInfo.getExpeditorAMUNN());
                result.setRAOrganizationFeatures(raFeaturesType);
            } else if ("BY".equals(country.getCountryId())) {
                rbFeaturesType = new RBOrganizationFeaturesType();
                rbFeaturesType.setRBIdentificationNumber(expeditorInfo.getExpeditorBYIN());
                rbFeaturesType.setUNP(expeditorInfo.getExpeditorBYUNP());
                result.setRBOrganizationFeatures(rbFeaturesType);
            } else if ("KG".equals(country.getCountryId())) {
                kgFeaturesType = new KGOrganizationFeaturesType();
                kgFeaturesType.setKGINN(expeditorInfo.getExpeditorKGINN());
                kgFeaturesType.setKGOKPO(expeditorInfo.getExpeditorKGOKPO());
                result.setKGOrganizationFeatures(kgFeaturesType);
            }
            return result;
        }
        return null;
    }

    private PICUTransportMeansType buildsetPICUTransportMeans() {
        PICUTransportMeansType result = new PICUTransportMeansType();
        // Признак контейнерной перевозки
        if (invoice.getIsContainer() == 1) {
            for (String item : containers.keySet()) {
                if (!StringUtils.isEmpty(item)) {
                    result.getTransportIdentifier().add(item);
                }
            }
        } else {
            for (NeVagonLists vagon : vagonLists) {
                result.getTransportIdentifier().add(vagon.getVagNo());
            }
        }
        return result;
    }

    private PIRWGoodsType buildGoods(NeSmgsTnVed tnVedRow) {
        PIRWGoodsType result = new PIRWGoodsType();
        // result.setCurrencyCode(tnVedRow.getCurrencyCode());
        result.setGoodsNumeric(null);
        result.setGoodsTNVEDCode(tnVedRow.getTnVedCode());
        result.setGrossWeightQuantity(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getBruttoWeight())));
        result.setInvoiceValue(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getPriceByFull())));
        result.setNetWeightQuantity(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getNettoWeight())));
        // String description = (tnVedRow.getDescription() != null ? tnVedRow.getDescription() : "");
        // if (org.apache.commons.lang3.StringUtils.isNotBlank(tnVedRow.getDescriptionAdditionaly())) {
        // description += ", " + tnVedRow.getDescriptionAdditionaly();
        // }
        String description = "";
        if (tnVedRow.getTnVedName() != null) {
            description += "(" + tnVedRow.getTnVedName() + ")";
        }
        result.getGoodsDescription().add(description);

        // if (tnVedRow.getUnitTypeUn() != null) {
        SupplementaryQuantityType supplementaryQuantityType = new SupplementaryQuantityType();
        // NeUnitType unitType = unitTypeMap.get(tnVedRow.getUnitTypeUn());
        supplementaryQuantityType.setGoodsQuantity(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getCountByUnit())));
        supplementaryQuantityType.setMeasureUnitQualifierName(tnVedRow.getUnitName());
        result.getSupplementaryQuantity().add(supplementaryQuantityType);
        // }

        // PIGoodsPackagingType packagingType = new PIGoodsPackagingType();
        // packagingType.setPackageCode(tnVedRow.getPackingCode());
        // packagingType.setPakageQuantity(tnVedRow.getPackingCount());
        // packagingType.setPakagePartQuantity(tnVedRow.getPakagePartQuantity());

        // Если вид упаковки: навалом(VS), насыпью(VO, VR, VY), наливом(VL, VQ), неупакован(NE, NF, NG) или
        // нет сведений (NA), то передавать PakageTypeCode=0.
        // String pkgCode = tnVedRow.getPackingCode();
        // if (pkgCode != null && (pkgCode.equals("VS") || pkgCode.equals("VO") || pkgCode.equals("VR")
        // || pkgCode.equals("VY") || pkgCode.equals("VL") || pkgCode.equals("VQ") || pkgCode.equals("NE")
        // || pkgCode.equals("NF") || pkgCode.equals("NG") || pkgCode.equals("NA")))
        // packagingType.setPakageTypeCode("0");
        // else
        // packagingType.setPakageTypeCode((pkgCode != null ? "1" : "2")); // Если есть код упаковки, то
        // ставим "С
        // упаковкой", иначе "Без упаковки в
        // оборудованных емкостях транспортного
        // средства"


        // PIGoodsPackingInformationType goodsPackingInformationType = new PIGoodsPackingInformationType();
        // goodsPackingInformationType.setPackingCode(tnVedRow.getPackingCode());
        // goodsPackingInformationType.setPakingQuantity(
        // tnVedRow.getPackingCount() != null ? BigInteger.valueOf(tnVedRow.getPackingCount().longValue())
        // : null);
        // goodsPackingInformationType.setPackageMark(tnVedRow.getPlaceCargoMark());
        //
        // packagingType.getPackingInformation().add(goodsPackingInformationType);

        // addContainer(result, tnVedRow);

        // List<NeSmgsTnVedDocuments> docs = dao.getSmgsTnVedDocuments(tnVedRow.getId());
        // if (docs != null) {
        // for (NeSmgsTnVedDocuments doc : docs) {
        // result.getPresentedDocument().add(buildPICUPresentedDoc(doc));
        // }
        // }
        //
        // result.setPIGoodsPackaging(packagingType);

        return result;
    }

    private PICUPresentedDocType buildPICUPresentedDoc(NeSmgsTnVedDocuments doc) {
        if (doc != null) {
            PICUPresentedDocType result = new PICUPresentedDocType();
            result.setPresentedDocumentModeCode(doc.getDocumentCode());
            // result.setPrDocumentName(doc.getDocumentName());
            // int limit = 250;
            int limit = 108;
            String string = "";
            if (doc.getDocumentName() != null)
                string = doc.getDocumentName().length() > limit ? doc.getDocumentName().substring(0, limit)
                                : doc.getDocumentName();
            result.setPrDocumentName(string);
            result.setPrDocumentName(string);
            result.setPrDocumentNumber(doc.getDocumentNumber() != null ? doc.getDocumentNumber() : "БН");
            result.setPrDocumentDate(convertDate(doc.getDocumentDate()));
            return result;
        }
        return null;
    }

    private String getCategoryCode(Long categoryType) {
        NePersonCategoryType personCategoryType = dao.getPersonCategoryType(senderInfo.getCategoryType());
        return personCategoryType != null ? personCategoryType.getCategoryCode() : null;
    }

    private String getCategoryCodeReciever(Long categoryType) {
        NePersonCategoryType personCategoryType = dao.getPersonCategoryType(recieverInfo.getCategoryType());
        return personCategoryType != null ? personCategoryType.getCategoryCode() : null;
    }

    private CUOrganizationType getKTZH() {
        CUOrganizationType result = new CUOrganizationType();
        AddressType addressType = new AddressType();
        ITNKZType itnkz = new ITNKZType();
        RKOrganizationFeaturesType featuresType = new RKOrganizationFeaturesType();

        // LegalPerson ktzh = dao.getLegalPerson(KTZH_LEL_PERS_UN);

        result.setOrganizationName(replaceSpacSymbol(customsPersName));
        result.setShortName(replaceSpacSymbol(customsPersSName));
        addressType.setStreetHouse(customsPersAddress);
        addressType.setCity(customsPersCity);
        featuresType.setBIN(customsPersBIN);
        itnkz.setRNN(customsPersRNN);


        result.setOrganizationLanguage("RU");
        // result.setOrganizationName(ktzh.getLPersName());
        // result.setShortName(ktzh.getLPersSname());

        addressType.setRegion("АСТАНА");
        // addressType.setStreetHouse(ktzh.getLPersAddr());
        // addressType.setCity(ktzh.getLPersCity());
        addressType.setCountryCode("KZ");
        addressType.setCounryName("КАЗАХСТАН");

        result.setAddress(addressType);

        // featuresType.setBIN(ktzh.getLPersBin());
        // itnkz.setRNN(ktzh.getLPersRnn());
        featuresType.setITN(itnkz);
        result.setRKOrganizationFeatures(featuresType);

        return result;
    }

    private void initUnitType() {
        unitTypeMap = new HashMap<Long, NeUnitType>();
        List<NeUnitType> unitTypeList = dao.getUnitType();
        for (NeUnitType item : unitTypeList) {
            unitTypeMap.put(item.getUnitType(), item);
        }

    }

    private void initContainers() {
        containers = new HashMap<String, String>();
        Map<String, Set<String>> containerMap = new HashMap<String, Set<String>>();
        if (invoice.getIsContainer() == 1 && !containerList.isEmpty()) { // Если у нас контейнерная отправка и есть
                                                                         // данные в NE_CONTAINERS_LIST
            for (NeSmgsTnVed tnVedRow : tnVedList) {
                Set<String> containerTnVed = containerMap.get(tnVedRow.getTnVedCode());
                if (containerTnVed == null) {
                    containerTnVed = new HashSet<String>();
                }
                for (NeContainerLists item : containerList) {
                    String itemTrim = ((item.getContainerMark() != null ? item.getContainerMark() : "")
                                    + (item.getContainerNo() != null ? item.getContainerNo() : "")).trim();
                    if (!StringUtils.isEmpty(itemTrim)) {
                        containerTnVed.add(itemTrim);
                    }
                }
                containerMap.put(tnVedRow.getTnVedCode(), containerTnVed);
            }
        } else {
            for (NeSmgsTnVed tnVedRow : tnVedList) {
                Set<String> containerTnVed = containerMap.get(tnVedRow.getTnVedCode());
                if (tnVedRow.getContainer() != null) {
                    if (containerTnVed == null) {
                        containerTnVed = new HashSet<String>();
                    }
                    String[] cnt = tnVedRow.getContainer().split(",");
                    for (String item : cnt) {
                        String itemTrim = item.trim();
                        if (!StringUtils.isEmpty(itemTrim)) {
                            containerTnVed.add(itemTrim);
                        }
                    }
                    containerMap.put(tnVedRow.getTnVedCode(), containerTnVed);
                }
            }
        }
        for (String tnVed : containerMap.keySet()) {
            Set<String> containerTnVed = containerMap.get(tnVed);
            for (String item : containerTnVed) {
                String fill = containers.get(item);
                if (fill != null) {
                    containers.put(item, "2");
                } else {
                    containers.put(item, "1");
                }
            }
        }
    }

    public static XMLGregorianCalendar convertDate(Date date) {
        XMLGregorianCalendar xgcal = null;
        if (date != null) {
            try {
                xgcal = DatatypeFactory.newInstance()
                                .newXMLGregorianCalendar((new SimpleDateFormat("yyyy-MM-dd").format(date)));
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return xgcal;
    }

    public static XMLGregorianCalendar convertTime(Date date) {
        XMLGregorianCalendar xgcal = null;
        if (date != null) {
            try {
                xgcal = DatatypeFactory.newInstance()
                                .newXMLGregorianCalendar((new SimpleDateFormat("hh:mm:ss").format(date)));
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return xgcal;
    }

}
