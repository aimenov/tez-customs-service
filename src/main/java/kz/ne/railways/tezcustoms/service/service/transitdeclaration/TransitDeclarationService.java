package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.ktzh.epd.service.HandlerServiceLocator;
import kz.ne.ktzh.epd.service.InvoiceRequest;
import kz.ne.ktzh.epd.service.InvoiceResponse;
import kz.ne.ktzh.epd.service.Method;
import kz.ne.railways.tezcustoms.service.entity.asudkr.*;
import kz.ne.railways.tezcustoms.service.model.DataCaneVagInfo;
import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;
import kz.ne.railways.tezcustoms.service.service.ForDataService;
import kz.ne.railways.tezcustoms.service.service.bean.PrevInfoBeanDAOLocal;
import kz.ne.railways.tezcustoms.service.util.PredInfoSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.AxisFault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.customs.commonaggregatetypes._5_10.*;
import ru.customs.cuesadcommonaggregatetypescust._5_11.*;
import ru.customs.information.customsdocuments.esadout_cu._5_11.*;
import ru.customs.information.customsdocuments.esadout_cu._5_11.ObjectFactory;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.PICUGoodsShipmentType;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.PIRWGoodsType;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.PIRWInformationCUType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.ServiceException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class TransitDeclarationService implements TransitDeclarationServiceLocal {
    public static final String GNG_ERROR = "Код товара не найден в классификаторе или не действителен";
    public static final String GNG_FIND_START = "(индекс элемента:";
    public static final String GNG_FIND_END = ", всего элементов:";

    public static final String DOCUMENT_LANGUAGE = "RU";
    public static final String COUNTRY_CODE_2_ALPHA = "KZ";
    public static final String RAILWAY_TRANSPORT_CODE = "20";
    public static final String DOCUMENT_MODE_ID = "1006107E";

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

    private String uuid;

    public String getUiid() {
        return uuid;
    }

    public void setUiid(String ui) {
        this.uuid = ui;
    }

    @Autowired
    private PrevInfoBeanDAOLocal dao;

    @Autowired
    private ForDataService forDataService;

    private User user;

    @Autowired
    private PredInfoSender sender;

    @Autowired
    private PIMessageBuilderLocal builder;

    public NeInvoice invoice;
    private NeInvoiceInfo invoiceInfo;
    public NeInvoicePrevInfo invoicePrevInfo;
    public List<NeVagonLists> vagonLists;
    public List<NeContainerLists> containerList;
    private List<NeSmgsTnVed> tnVedList;
    private NeSmgsSenderInfo senderInfo;
    private NeSmgsRecieverInfo recieverInfo;
    private NeSmgsCargo neSmgsCargo = null;
    private NeVagonGroup vagonGroup = null;
    private NeSmgsDestinationPlaceInfo neSmgsDestinationPlaceInfo = null;
    private NeSmgsDeclarantInfo declarantInfo;
    private NeSmgsExpeditorInfo expeditorInfo;
    private NeCustomsOrgs customsOrgs;
    public Map<String, DataCaneVagInfo> mapVagons = null;
    private NeSmgsShipList ship;

    private Long invoiceUn;
    private String exportTD;

    public String getExportTD() {
        return exportTD;
    }

    public void setExportTD(String exportTD) {
        this.exportTD = exportTD;
    }

    private Map<Long, NeUnitType> unitTypeMap;
    private Map<String, String> containers;
    private Map<String, BigDecimal> rates;
    private Map<String, CurrencyCode> currencyCodes;
    private Map<String, String> replaceTnVedCode;

    private ESADoutCUType result;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PredInfoSender getSender() {
        return sender;
    }

    public void setSender(PredInfoSender sender) {
        this.sender = sender;
    }


    public SaveDeclarationResponseType setUp() {
        initUnitType();
        initContainers();
        initVagons();
        initCurrencyCodes(new Date());
        initRates(new Date());
        return initReplaceTnVedCode();
    }

    public String getXml(ESADoutCUType value) {
        String result = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ESADoutCUType.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            ObjectFactory ob = new ObjectFactory();
            StringWriter sw = new StringWriter();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new PINamespacePrefixMapper());
            marshaller.marshal(ob.createESADoutCU(value), sw);
            result = sw.toString().replaceAll("ns2", "ESADout_CU").replaceAll("ns3", "catESAD_cu");
            // .replaceAll("5.8.0", "5.10.0");
            // result = sw.toString();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        log.info(result);
        return result;
    }

    public ESADoutCUType build(long invoiceUn) {
        this.invoiceUn = invoiceUn;
        readData();
        result = createESADoutCUType();
        return result;
    }

    private ESADoutCUType createESADoutCUType() {
        ObjectFactory ob = new ObjectFactory();
        result = ob.createESADoutCUType();
        result.setDocumentModeID(getDocumentMode()); // Идентификатор вида документа. (Код документа в Альбоме форматов
                                                     // для РФ)
        result.setDocumentID(getDocumentID()); // Уникальный идентификатор документа
        result.setRefDocumentID(getRefDocumentID()); // Уникальный идентификатор исходного документа
        result.setCustomsProcedure(getCustomsProcedure()); // ИМ, ЭК, ТТ. Первый подраздел гр.1 ДТ/ТД
        result.setCustomsModeCode(getCustomsModeCode()); // Код таможенной процедуры в соответствии с классификатором
                                                         // видов таможенных процедур гр. 1. второй подраздел ДТ. Первый
                                                         // элемент первого подраздела гр. 37/«00» – для припасов
        result.setTransitFeature(getTransitFeature()); // Особенность помещения товаров под процедуру таможенного
                                                       // транзита. гр. 1. второй подраздел ТД. МПО - международные
                                                       // почтовые отправления, ФЛ - товары и (или) транспортные
                                                       // средства для личного пользования*
        result.setTransitDirectionCode(getTransitDirectionCode()); // ИМ, ЭК, ТР, ВТ, ТС. Гр. 1 ТД. Третий подраздел
        result.setDeclarationKind(getDeclrationKind()); // Код особенности декларирования товаров по классификатору
                                                        // особенностей таможенного декларирования товаров. Гр.7 ДТ
        result.setSubsoilSign(getSubsoilSign());// Признак недропользователя. Для РК
        result.setSealNumber(getSealNumber()); // Номер пломбиратора в ТД
        result.setSealQuantity(getSealQuantity()); // Количество наложенных пломб в ТД

        result.setLanguageCUESAD(DOCUMENT_LANGUAGE); // Код языка заполнения документа
        result.setRecipientCountryCode(COUNTRY_CODE_2_ALPHA); // Код страны, в сооветствии с классификатором стран мира,
                                                              // таможенного органа, которому представляется документ
        result.setMovementCode(getMovementCode()); // Код цели перемещения в ТД
        result.setExecutionPlace(getExecutionPlace()); // Место представления ТД
        result.setRailwayStationCode(getRailwayStationCode()); // Код ж/д станции. Гр.50 ТД
        result.setRegNumberDoc(null); // Исходящий номер регистрации документов в соответствии с системой (регламентом)
                                      // учета исходящих документов декларанта или таможенного представителя в
                                      // Республике Беларусь
        result.setExecutionDate(getExecutionDate()); // Дата составления (представления)
        result.setSecurityLabelCode(getSecurityLabelCode()); // Код защитной наклейки для РК
        result.setESADoutCUGoodsShipment(getGoodsShipment()); // Сведения о товарной партии
        result.setRBTechMarK(null); // Технические отметки для РБ
        result.setFilledPerson(getFilledPerson()); // Сведения о лице, заполнившем документ
        result.setCUESADCustomsRepresentative(getCustomsRepresentative()); // Таможенный представитель
        return result;
    }

    private void invoiceReq(long invoceId, String user) {
        try {
            HandlerServiceLocator locator = new HandlerServiceLocator();
            // locator.setHandlerPortEndpointAddress("http://10.60.0.101:9080/EpdService/HandlerService");
            locator.setHandlerPortEndpointAddress("http://epdservice.service.railway:9080/EpdService/HandlerService");
            long t = System.currentTimeMillis();
            InvoiceRequest request = new InvoiceRequest();
            request.setInvoiceId(invoceId);
            request.setMethod(Method.PI);
            request.setUser(user);
            InvoiceResponse response = locator.getHandlerPort().createInvoice(request);

            System.out.println(response.getNewId());
            System.out.println("time:" + (System.currentTimeMillis() - t) + " ms");

        } catch (AxisFault e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSecurityLabelCode() {
        // Код защитной наклейки для РК
        return "KT9DF4"; // от 25.11.2016
    }

    private String getRailwayStationCode() {
        // Код ж/д станции. Гр.50 ТД
        return invoicePrevInfo != null ? invoicePrevInfo.getArriveStaNo() : null;
    }

    private XMLGregorianCalendar getExecutionDate() {
        // Дата составления (представления)
        return convertDate(new Date()); // Нужно уточнить!
    }

    private String getExecutionPlace() {
        // TODO Auto-generated method stub
        // Место представления ТД

        return null; // Поак нет справочников!
    }

    private String getMovementCode() {
        // TODO Auto-generated method stub
        // Код цели перемещения в ТД
        return null;
    }

    private BigInteger getSealQuantity() {
        // TODO Auto-generated method stub
        // Количество наложенных пломб в ТД
        BigInteger result = BigInteger.ZERO;
        return result;
    }

    private String getSealNumber() {
        // TODO Auto-generated method stub
        // Номер пломбиратора в ТД
        return null;
    }

    private String getSubsoilSign() {
        // TODO Auto-generated method stub
        // Признак недропользователя. Для РК
        return null;
    }

    private String getDeclrationKind() {
        // TODO Auto-generated method stub
        // Код особенности декларирования товаров по классификатору особенностей таможенного декларирования
        // товаров. Гр.7 ДТ
        return null;
    }

    private String getCustomsModeCode() {
        // TODO Auto-generated method stub
        // Код таможенной процедуры в соответствии с классификатором видов таможенных процедур гр. 1. второй
        // подраздел ДТ. Первый элемент первого подраздела гр. 37/«00» – для припасов
        String result = null;
        return result;
    }

    private String getRefDocumentID() {
        // Уникальный идентификатор исходного документа
        String result = null;
        if (invoicePrevInfo != null)
            result = invoicePrevInfo.getResponseUUID();

        return result;
    }

    private String getDocumentID() {
        // TODO Auto-generated method stub
        // Уникальный идентификатор документа
        return UUID.randomUUID().toString();
    }

    private String getDocumentMode() {
        // Идентификатор вида документа. (Код документа в Альбоме форматов для РФ)
        return DOCUMENT_MODE_ID;
    }

    private CUESADCustomsRepresentativeType getCustomsRepresentative() {
        // TODO Auto-generated method stub
        // Таможенный представитель
        CUESADCustomsRepresentativeType result = null;
        return result;
    }

    private ESADFilledPersonType getFilledPerson() {
        // TODO Auto-generated method stub
        // Сведения о лице, заполнившем документ
        ESADFilledPersonType result = new ESADFilledPersonType();
        AuthoritesDocumentType document = new AuthoritesDocumentType();
        document.setPrDocumentNumber(null);
        document.setPrDocumentName(null);
        document.setPrDocumentDate(null);
        document.setComplationAuthorityDate(null);
        result.setAuthoritesDocument(document); // Сведения о документе, удостоверяющем полномочия, о доверенности на
                                                // совершение действий
        ContactType contact = new ContactType();
        contact.getPhone();
        contact.getEMail();
        result.setContact(contact); // Контактная информация
        IdentityCardType identityCard = new IdentityCardType();
        identityCard.setIdentityCardCode(null);
        identityCard.setIdentityCardDate(null);
        identityCard.setIdentityCardName(null);
        identityCard.setIdentityCardNumber(null);
        identityCard.setIdentityCardSeries(null);
        identityCard.setOrganizationName(null);
        result.setIdentityCard(identityCard); // Документ, удостоверяющий личность
        result.setPersonName(user != null ? user.getName() : null); // Имя
        result.setPersonMiddleName(null); // Отчество
        result.setPersonSurname(user != null ? user.getSurName() : null); // Фамилия
        result.setPersonPost(null); // Должность
        result.setQualificationCertificate(null); // Номер квалификационного аттестата специалиста по таможенному
                                                  // оформлению. Для РБ
        return result;
    }

    private ESADoutCUGoodsShipmentType getGoodsShipment() {
        // Сведения о товарной партии
        ESADoutCUGoodsShipmentType result = new ESADoutCUGoodsShipmentType();
        result.setOriginCountryName(getOriginCountryName()); // Страна происхождения товара. Краткое название страны в
                                                             // соответствии с классификатором стран мира / РАЗНЫЕ/
                                                             // НЕИЗВЕСТНА/ ЕВРОСОЮЗ
        result.setESADoutCUConsignor(getConsignor());// "Отправитель/Экспортер"
        result.setESADoutCUConsignee(getConsignee()); // "Получатель"
        result.setESADoutCUFinancialAdjustingResponsiblePerson(getFinancialAdjustingResponsiblePerson()); // Лицо
                                                                                                          // ответственное
                                                                                                          // за
                                                                                                          // финансовое
                                                                                                          // урегулирование
        if (expeditorInfo != null) // Если указан экспедитор
            result.setESADoutCUDeclarant(getExpeditor()); // Сведения о экспедиторе товаров
        else
            result.setESADoutCUDeclarant(getDeclarant()); // Сведения о декларанте товаров
        result.setESADoutCUCarrier(getCarrier());// Сведения о перевозчике гр. 50 ТД
        result.setESADoutCUConsigment(getConsigment()); // Сведения о перевозке товаров. Гр. 15, 15а, 17,17а, 18, 19,
                                                        // 21, 25, 26, 29 -ДТ/ Гр. 15, 17, 18, 19, 21, 25, 26, 29 , 53,
                                                        // 55- ТД
        result.setESADoutCUPayments(getPayments()); // Сведения об уплачиваемых платежах, платежных поручениях, отсрочке
                                                    // платежей. Гр. 48, В
        result.getGuarantee().add(getGuarantee());// Гарантия.Гр 52 в ДТ
        result.getTDGuarantee().add(getTDGuarantee());// Гарантия для ТД
        buildGoods(result.getESADoutCUGoods()); // Товарная часть Гр. 31-47
        buildGoodsLocation(result.getESADoutCUGoodsLocation());// Местонахождение товаров. Гр. 30 ДТ
        result.setSpecificationNumber(getSpecificationNumber(result)); // Общее количество представленных спецификаций
        result.setSpecificationListNumber(getSpecificationListNumber(result)); // Общее количество листов представленных
                                                                               // спецификаций
        result.setTotalGoodsNumber(getTotalGoodsNumber(result)); // Всего наименований товаров
        result.setTotalPackageNumber(getTotalPackageNumber(result)); // Общее количество грузовых мест
        result.setTotalSheetNumber(getTotalSheetNumber(result)); // Количество листов
        result.setTotalCustCost(null); // Сведения о стоимости/общая таможенная стоимость
        result.setCustCostCurrencyCode(null); // Буквенный код валюты таможенной стоимости в соответствии с
                                              // классификатором валют
        result.setESADoutCUMainContractTerms(getMainContractTerms(result)); // Условия сделки Гр. 11, 20, 22, 23, 24 ДТ

        return result;
    }

    private ESADoutCUMainContractTermsType getMainContractTerms(ESADoutCUGoodsShipmentType goodsShipemnt) {
        // TODO Auto-generated method stub
        ESADoutCUMainContractTermsType result = new ESADoutCUMainContractTermsType();
        String currencyCode = getCurrencyCode();
        result.setContractCurrencyCode(currencyCode); // Трехзначный буквенный в графе 22 код валюты цены договора/
                                                      // платежа (оценки). По классификатору валют
        result.setCurrencyQuantity(null); // Количество единиц валюты, для которой указан курс для РБ
        result.setContractCurrencyRate(getContractCurrencyRate(currencyCode)); // Курс валюты цены договора/ платежа
                                                                               // (оценки)
        result.setTotalInvoiceAmount(getTotalInvoiceAmount()); // Общая стоимость товаров. Гр 22 подраздел 2
        result.setTradeCountryCode(null); // Буквенный код торгующей страны в соответствии с классификатором стран мира.
                                          // Гр.11, 1-й подраздел ДТ
        result.setRBCountryCode(null); // Код административно-территориального деления страны в соответствии с
                                       // классификатором административно-территориального деления стран, применяемым в
                                       // Республике Беларусь. Гр.11, 2-й подраздел ДТ
        result.setDealFeatureCode(null); // Код особенности сделки (договора). В соответствии с классификатором
                                         // особенностей внешнеэкономической сделки, используемым в государствах -
                                         // членахТаможенного союзаЕвразийского экономического союза. Гр. 24 ДТ
        result.setDealNatureCode(null); // Код характера сделки с декларируемыми товарами в соответствии с
                                        // классификатором характера сделки, используемым в государствах -
                                        // членахТаможенного союзаЕвразийского экономического союза. Гр.24 ДТ
        result.setCUESADDeliveryTerms(null); // Условия поставки товаров. Гр. 20 ДТ

        return result;
    }

    private BigDecimal getContractCurrencyRate(String currencyCode) {
        // Курс валюты цены договора/ платежа (оценки)
        if ("KZT".equals(currencyCode)) {
            return BigDecimal.ONE;
        } else {
            if (rates != null)
                return rates.get(currencyCode) != null ? rates.get(currencyCode).setScale(2, BigDecimal.ROUND_HALF_UP)
                                : null;
            else
                return BigDecimal.ONE;
        }
    }

    private BigDecimal getTotalInvoiceAmount() {
        // Общая стоимость товаров. Гр 22 подраздел 2
        BigDecimal result = BigDecimal.ZERO;
        if (tnVedList != null)
            for (NeSmgsTnVed row : tnVedList) {
                if (row.getPriceByFull() != null) {
                    result = result.add(BigDecimal.valueOf(Double.parseDouble(row.getPriceByFull())));
                }
            }
        return result.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String getCurrencyCode() {
        // Трехзначный буквенный в графе 22 код валюты цены договора/ платежа (оценки). По классификатору
        // валют
        String result = "USD";
        // if (tnVedList != null)
        // for (NeSmgsTnVed row : tnVedList) {
        // if (row.getCurrencyCode() != null) {
        // result = row.getCurrencyCode();
        // break;
        // }
        // }
        return result;
    }

    private String getCustCostCurrencyCode(ESADoutCUGoodsShipmentType goodsShipment) {
        // Буквенный код валюты таможенной стоимости в соответствии с классификатором валют
        return "KZT";
    }

    private BigDecimal getTotalCustCost(ESADoutCUGoodsShipmentType goodsShipment) {
        // Сведения о стоимости/общая таможенная стоимость
        BigDecimal result = BigDecimal.ZERO;
        for (ESADoutCUGoodsType item : goodsShipment.getESADoutCUGoods()) {
            result = result.add(item.getCustomsCost());
        }
        return result.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigInteger getTotalSheetNumber(ESADoutCUGoodsShipmentType goodsShipment) {
        // Количество листов
        int countGoods = goodsShipment.getTotalGoodsNumber().intValue();
        return BigInteger.valueOf(calcSheetNumber(countGoods));
    }

    private int calcSheetNumber(int countGoods) {
        int countlist = (countGoods - 1) / 3 + (((countGoods - 1) % 3) > 0 ? 1 : 0);
        return countlist + 1;
    }

    private BigDecimal getTotalPackageNumber(ESADoutCUGoodsShipmentType goodsShipment) {
        // Общее количество грузовых мест
        BigDecimal result = BigDecimal.ZERO;
        // if (tnVedList != null && !tnVedList.isEmpty()) {
        // for (NeSmgsTnVed tnVedRow : tnVedList) {
        // result = result.add(tnVedRow.getPlaceCargoCount());
        // }
        // }
        // if (BigDecimal.ZERO.equals(result)) {
        // result = BigDecimal.ONE;
        // }
        return result.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    private BigInteger getTotalGoodsNumber(ESADoutCUGoodsShipmentType goodsShipment) {
        Integer count = goodsShipment.getESADoutCUGoods().size();
        return BigInteger.valueOf(count);
    }

    private BigDecimal getSpecificationListNumber(ESADoutCUGoodsShipmentType goodsShipment) {
        // TODO Auto-generated method stub
        // Общее количество листов представленных спецификаций
        return null;
    }

    private BigDecimal getSpecificationNumber(ESADoutCUGoodsShipmentType goodsShipment) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getOriginCountryName() {
        // TODO Auto-generated method stub
        // Страна происхождения товара. Краткое название страны в соответствии с классификатором стран мира
        // РАЗНЫЕ/ НЕИЗВЕСТНА/ ЕВРОСОЮЗ
        String result = null;
        String code = "";
        if (tnVedList != null)
            for (NeSmgsTnVed row : tnVedList) {
                String countryCode = row.getTnVedCountry();
                Country country = dao.getCountry(countryCode);
                if (country != null && result == null) {
                    result = country.getCountryFullName();
                    code = countryCode;
                } else if (countryCode != null && !code.equals(countryCode)) {
                    result = "РАЗНЫЕ";
                    code = "РАЗНЫЕ";
                }
            }
        if (result == null) {
            result = "НЕИЗВЕСТНА";
        }
        return result;
    }

    private ESADoutCUPaymentsType getPayments() {
        // TODO Auto-generated method stub
        // Сведения об уплачиваемых платежах, платежных поручениях, отсрочке платежей. Гр. 48, В
        ESADoutCUPaymentsType result = new ESADoutCUPaymentsType();
        return result;
    }

    private CUGuaranteeType getGuarantee() {
        CUGuaranteeType guaranteeType = new CUGuaranteeType();
        guaranteeType.setPaymentWayCode("00");
        return guaranteeType;
    }

    private TDGuaranteeType getTDGuarantee() {
        TDGuaranteeType guaranteeType = new TDGuaranteeType();
        guaranteeType.setMeasureCode("00");
        return guaranteeType;
    }

    private void buildGoods(List<ESADoutCUGoodsType> doutCUGoods) {
        // Товарная часть Гр. 31-47
        long i = 1L;
        if (tnVedList != null && !tnVedList.isEmpty()) {
            List<TnVedRow> tnVedSort = new ArrayList<TnVedRow>();
            Map<String, ESADoutCUGoodsType> goodsMap = new HashMap<String, ESADoutCUGoodsType>();
            for (NeSmgsTnVed tnVedRow : tnVedList) {
                ESADoutCUGoodsType goods = buildGoods(tnVedRow);
                // TDG-5514
                // goodsMap.put(tnVedRow.getTnVedCode() + tnVedRow.getPackingCode() + tnVedRow.getId(), goods);
                // --
                goods.setAdditionalSheetCount(getAdditionalSheetCount(i++)); // Порядковый номер листа (первый подраздел
                                                                             // гр.3)

                // Integer containerCount = goods.getESADContainer().getContainerNumber().size();
                // goods.getESADContainer()
                // .setContainerQuantity(containerCount > 0 ? BigInteger.valueOf(containerCount) : null);
                // goods.getESADContainer().setContainerKind(getContainerKind()); // Тип контейнера в соответствии с
                // классификатором видов груза, упаковки
                // и упаковочных материалов

                // doutCUGoods.add(goods);
            }
            i = 1L;
            // TODO: нужна сортировка!
            for (TnVedRow tnVedRow : tnVedSort) {
                // for(String key: goodsMap.keySet()) {
                // ESADoutCUGoodsType goods = goodsMap.get(key);
                ESADoutCUGoodsType goods =
                                goodsMap.get(tnVedRow.getTnVedCode() + tnVedRow.getPackingCode() + tnVedRow.getId());
                goods.setGoodsNumeric(BigInteger.valueOf(i++));
                doutCUGoods.add(goods);
            }
        }
    }

    private BigDecimal getAdditionalSheetCount(Long i) {
        // Порядковый номер листа (первый подраздел гр.3)
        BigDecimal result = BigDecimal.ZERO;
        if (i == 1L) {
            result = BigDecimal.ONE;
        } else {
            result = BigDecimal.valueOf(calcSheetNumber(i.intValue()));
        }
        return result.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    private String getContainerKind() {
        // TODO Auto-generated method stub
        // Тип контейнера в соответствии с классификатором видов груза, упаковки и упаковочных материалов
        return null;
    }

    private ESADoutCUGoodsType addGoods(ESADoutCUGoodsType goods, TnVedRow tnVedRow) {
        goods.setGrossWeightQuantity(goods.getGrossWeightQuantity()
                        .add(tnVedRow.getBrutto() != null ? tnVedRow.getBrutto() : BigDecimal.ZERO)
                        .setScale(3, BigDecimal.ROUND_HALF_UP));
        goods.setInvoicedCost(goods.getInvoicedCost()
                        .add(tnVedRow.getPriceByTotal() != null ? tnVedRow.getPriceByTotal() : BigDecimal.ZERO)
                        .setScale(2, BigDecimal.ROUND_HALF_UP));
        goods.setCustomsCost(null);
        goods.setNetWeightQuantity(goods.getNetWeightQuantity()
                        .add(tnVedRow.getNetto() != null ? tnVedRow.getNetto() : BigDecimal.ZERO)
                        .setScale(3, BigDecimal.ROUND_HALF_UP));
        if (tnVedRow.getUnitTypeUn() != null) {
            SupplementaryQuantityType supplementaryQuantityType = new SupplementaryQuantityType();
            NeUnitType unitType = unitTypeMap.get(tnVedRow.getUnitTypeUn());
            supplementaryQuantityType.setGoodsQuantity(tnVedRow.getCountByUnit().setScale(2, BigDecimal.ROUND_HALF_UP));
            supplementaryQuantityType.setMeasureUnitQualifierCode(unitType.getUnitCode());
            supplementaryQuantityType.setMeasureUnitQualifierName(unitType.getUnitName());
            // fix_xsd_5.11
            // goods.getSupplementaryGoodsQuantity().add(supplementaryQuantityType);
            goods.getSupplementaryGoodsQuantity1().add(supplementaryQuantityType);
        }
        ESADGoodsPackagingType packagingType = goods.getESADGoodsPackaging();
        packagingType.setPakageQuantity(packagingType.getPakageQuantity()
                        .add(tnVedRow.getPlaceCargoCount() != null ? tnVedRow.getPlaceCargoCount() : BigDecimal.ONE)
                        .setScale(2, BigDecimal.ROUND_HALF_UP));
        if (packagingType.getPakagePartQuantity() != null) {
            packagingType.setPakagePartQuantity(packagingType.getPakagePartQuantity()
                            .add(tnVedRow.getPakagePartQuantity() != null ? tnVedRow.getPakagePartQuantity()
                                            : BigDecimal.ONE)
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        // в ХМL для товаров с одинаковыми кода ТН ВЭД, объединять
        BigInteger quantity = BigInteger.ONE;
        int sizePackType = packagingType.getPackingInformation().size();
        for (int i = 0; i < sizePackType; i++) {
            PackingInformationType informationType =
                            (PackingInformationType) packagingType.getPackingInformation().get(i);
            quantity = tnVedRow.getPackingCount() != null ? BigInteger.valueOf(
                            tnVedRow.getPackingCount().longValue() + informationType.getPakingQuantity().longValue())
                            : BigInteger.ONE;
            informationType.setPakingQuantity(quantity);
        }
        /*
         * PIGoodsPackingInformationType goodsPackingInformationType = new PIGoodsPackingInformationType();
         * goodsPackingInformationType.setPackingCode(tnVedRow.getPackingCode());
         * goodsPackingInformationType.setPakingQuantity(quantity);
         * goodsPackingInformationType.setPackageMark(tnVedRow.getPlaceCargoMark());
         * packagingType.getPackingInformation().add(goodsPackingInformationType);
         */

        addContainer(goods, tnVedRow);
        List<NeSmgsTnVedDocuments> docs = dao.getSmgsTnVedDocuments(tnVedRow.getId());
        if (docs != null) {
            for (NeSmgsTnVedDocuments doc : docs) {
                goods.getESADoutCUPresentedDocument().add(buildPICUPresentedDoc(doc));
            }
        }
        goods.setESADGoodsPackaging(packagingType);
        return goods;
    }

    private ESADoutCUGoodsType buildGoods(NeSmgsTnVed tnVedRow) {

        ESADoutCUGoodsType result = new ESADoutCUGoodsType();
        result.setCurrencyCode(tnVedRow.getCurrencyCodeUn());

        result.setGoodsNumeric(null);
        result.setGoodsTNVEDCode(tnVedRow.getTnVedCode());
        result.setGrossWeightQuantity(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getBruttoWeight())));
        result.setInvoicedCost(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getPriceByFull())));
        result.setNetWeightQuantity(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getNettoWeight())));
        result.setCustomsCost(null);
        result.setGoodFeatures(null);
        String description = "";
        if (tnVedRow.getTnVedName() != null) {
            description += "(" + tnVedRow.getTnVedName() + ")";
        }
        result.getGoodsDescription().add(description);

        SupplementaryQuantityType supplementaryQuantityType = new SupplementaryQuantityType();
        supplementaryQuantityType.setGoodsQuantity(BigDecimal.valueOf(Double.parseDouble(tnVedRow.getCountByUnit())));
        supplementaryQuantityType.setMeasureUnitQualifierName(tnVedRow.getUnitName());
        result.getSupplementaryGoodsQuantity1().add(supplementaryQuantityType);

        // ESADGoodsPackagingType packagingType = new ESADGoodsPackagingType();
        // packagingType.setPackageCode(tnVedRow.getPackingCode());
        // if (tnVedRow.getPakagePartQuantity() != null
        // && tnVedRow.getPakagePartQuantity().toBigInteger() != BigInteger.ZERO) {
        // packagingType.setPakagePartQuantity(tnVedRow.getPakagePartQuantity() != null
        // ? tnVedRow.getPakagePartQuantity().setScale(2, BigDecimal.ROUND_HALF_UP)
        // : BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        // }
        // TAV возможно фикс по количеству грузомест
        // packagingType.setPakageQuantity(tnVedRow.getPackingCount() != null ?
        // tnVedRow.getPackingCount().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO.setScale(2,
        // BigDecimal.ROUND_HALF_UP));
        // packagingType.setPakageQuantity(tnVedRow.getPlaceCargoCount() != null
        // ? tnVedRow.getPlaceCargoCount().setScale(2, BigDecimal.ROUND_HALF_UP)
        // : BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        //
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
        // // упаковкой", иначе "Без упаковки в
        // // оборудованных емкостях транспортного
        // // средства"
        //
        // PackingInformationType goodsPackingInformationType = new PackingInformationType();
        // goodsPackingInformationType.setPackingCode(tnVedRow.getPackingCode());
        // goodsPackingInformationType.setPakingQuantity(
        // tnVedRow.getPackingCount() != null ? BigInteger.valueOf(tnVedRow.getPackingCount().longValue())
        // : null);
        //
        // packagingType.getPackingInformation().add(goodsPackingInformationType);
        //
        // result.setESADContainer(new ESADContainerType());
        // addContainer(result, tnVedRow);
        // List<NeSmgsTnVedDocuments> docs = dao.getSmgsTnVedDocuments(tnVedRow.getId());
        // if (docs != null) {
        // for (NeSmgsTnVedDocuments doc : docs) {
        // result.getESADoutCUPresentedDocument().add(buildPICUPresentedDoc(doc));
        // }
        // }
        // result.setESADGoodsPackaging(packagingType);
        Country country = dao.getCountry(tnVedRow.getTnVedCountry());
        if (country != null) {
            result.setOriginCountryCode(country.getCountryId());
            result.setOriginCountryName(country.getCountryName());
        } else {
            result.setOriginCountryCode("00");
            result.setOriginCountryName("НЕИЗВЕСТНА");
        }
        // result.setMilitaryProducts(tnVedRow.getTnVedIsArmy() != null && tnVedRow.getTnVedIsArmy() == 1 ?
        // true : false);
        result.setLanguageGoods(DOCUMENT_LANGUAGE);
        return result;
    }

    private String getTnVedCode(String tnVedCode) {
        String result = tnVedCode;
        if (replaceTnVedCode != null)
            while (replaceTnVedCode.get(result) != null) {
                result = replaceTnVedCode.get(result);
            }
        return result;
    }

    private BigDecimal conversion(BigDecimal priceByTotal, String currencyCode) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal rate = rates.get(currencyCode);
        if (priceByTotal != null && rate != null) {
            result = priceByTotal.multiply(rate);
        } else if ("KZT".equals(currencyCode)) {
            result = priceByTotal;
        }
        return result.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private void addContainer(ESADoutCUGoodsType goods, TnVedRow tnVedRow) {
        String[] containersTnVed = (tnVedRow.getContainer() != null ? tnVedRow.getContainer().split(",") : null);
        ESADContainerType goodsContainer = goods.getESADContainer();
        if (!containerList.isEmpty() && !containers.isEmpty() && invoice.getIsContainer() == 1) {// По новой логике
            for (String key : containers.keySet()) {
                ContainerNumberType container = new ContainerNumberType();
                container.setContainerIdentificaror(key);
                container.setFullIndicator(containers.get(key));
                System.out.println("++++++++++++++++++++setFullIndicator1:" + key);

                if (!haveContainer(goodsContainer.getContainerNumber(), container)) { // Проверяем был ли добавлен уже
                                                                                      // такой контейнер
                    goodsContainer.getContainerNumber().add(container);
                }
            }
        } else if (containersTnVed != null) { // По старой логике
            for (String item : containersTnVed) {
                String itemTrim = item.trim();
                if (!StringUtils.isEmpty(itemTrim)
                                && !containsContiner(goodsContainer.getContainerNumber(), itemTrim)) {
                    ContainerNumberType container = new ContainerNumberType();
                    container.setContainerIdentificaror(itemTrim);
                    System.out.println("++++++++++++++++++++setFullIndicator2:" + itemTrim);
                    if (invoice.getIsContainer() == 1) {
                        container.setFullIndicator(containers.get(itemTrim));
                    }
                    if (!haveContainer(goodsContainer.getContainerNumber(), container)) { // Проверяем был ли добавлен
                                                                                          // уже такой контейнер
                        goodsContainer.getContainerNumber().add(container);
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

    private CUPresentedDocumentsType buildPICUPresentedDoc(NeSmgsTnVedDocuments doc) {
        if (doc != null && doc.getDocumentCode() != null) {
            CUPresentedDocumentsType result = new CUPresentedDocumentsType();
            result.setPresentedDocumentModeCode(doc.getDocumentCode());
            // int limit = 250;
            int limit = 108;
            String string = "";
            if (doc.getDocumentName() != null)
                string = doc.getDocumentName().length() > limit ? doc.getDocumentName().substring(0, limit)
                                : doc.getDocumentName();
            result.setPrDocumentName(string);
            result.setPrDocumentName(string);
            result.setPrDocumentNumber(doc.getDocumentNumber() != null ? doc.getDocumentNumber() : "БН");

            if (doc.getDocumentDate() != null && doc.getDocumentDateTo() != null) {
                result.setDocumentBeginActionsDate(convertDate(doc.getDocumentDate()));
                result.setDocumentEndActionsDate(convertDate(doc.getDocumentDateTo()));
            } else if (doc.getDocumentDate() != null) {
                result.setPrDocumentDate(convertDate(doc.getDocumentDate()));
            }
            return result;
        }
        return null;
    }

    private ESADoutCUConsigmentType getConsigment() {
        // Сведения о перевозке товаров. Гр. 15, 15а, 17,17а, 18, 19, 21, 25, 26, 29 -ДТ/ Гр. 15, 17, 18,
        // 19, 21, 25, 26, 29 , 53, 55- ТД
        ESADoutCUConsigmentType result = new ESADoutCUConsigmentType();

        if (invoicePrevInfo == null)
            return result;

        result.setContainerIndicator(invoice.getIsContainer() == 1 ? true : false); // Признак контейнерных перевозок.
                                                                                    // Гр.19
        Country dispatchCountry = invoicePrevInfo != null ? dao.getCountry(invoicePrevInfo.getStartStaCouNo()) : null;
        result.setDispatchCountryCode(dispatchCountry != null ? dispatchCountry.getCountryId() : null); // Страна
                                                                                                        // отправления.
                                                                                                        // Буквенный код
                                                                                                        // страны в
                                                                                                        // соответствии
                                                                                                        // с
                                                                                                        // классификатором
                                                                                                        // стран мира.
                                                                                                        // Гр 15,
                                                                                                        // подраздел a
        result.setDispatchCountryName(dispatchCountry != null ? dispatchCountry.getCountryName() : null); // Страна
                                                                                                          // отправления.
                                                                                                          // Краткое
                                                                                                          // название
                                                                                                          // страны в
                                                                                                          // соответствии
                                                                                                          // с
                                                                                                          // классификатором
                                                                                                          // стран мира.
                                                                                                          // Гр.15
        result.setRBDispatchCountryCode(null); // Код административно-территориального деления страны отправления в
                                               // соответствии с классификатором административно-территориального
                                               // деления стран, применяемым в Республике Беларусь. Гр.15, подраздел b
        Country destinationCountry =
                        invoicePrevInfo != null ? dao.getCountry(invoicePrevInfo.getDestStationCouNo()) : null;
        result.setDestinationCountryCode(destinationCountry != null ? destinationCountry.getCountryId() : null); // Страна
                                                                                                                 // назначения.
                                                                                                                 // Буквенный
                                                                                                                 // код
                                                                                                                 // страны
                                                                                                                 // в
                                                                                                                 // соответствии
                                                                                                                 // с
                                                                                                                 // классификатором
                                                                                                                 // стран
                                                                                                                 // мира
                                                                                                                 // / 00
                                                                                                                 // (неизвестна).
                                                                                                                 // Гр
                                                                                                                 // 17,
                                                                                                                 // подраздел
                                                                                                                 // a
        result.setDestinationCountryName(destinationCountry != null ? destinationCountry.getCountryName() : null); // Страна
                                                                                                                   // назначения.
                                                                                                                   // Краткое
                                                                                                                   // название
                                                                                                                   // страны
                                                                                                                   // в
                                                                                                                   // соответствии
                                                                                                                   // с
                                                                                                                   // классификатором
                                                                                                                   // стран
                                                                                                                   // мира
                                                                                                                   // /
                                                                                                                   // НЕИЗВЕСТНА.
                                                                                                                   // Гр
                                                                                                                   // 17
        result.setRBDestinationCountryCode(null); // Код административно-территориального деления страны назначения в
                                                  // соответствии с классификатором административно-территориального
                                                  // деления стран, применяемым в Республике Беларусь. Гр.17, подраздел
                                                  // b
        result.setDateExpectedArrival(convertDate(invoicePrevInfo.getArriveTime())); // Дата ожидаемого прибытия товаров
                                                                                     // и транспортных средств в
                                                                                     // пограничный пункт пропуска. ТД
        result.setTimeExpectedArrival(convertTime(invoicePrevInfo.getArriveTime())); // Время ожидаемого прибытия
                                                                                     // товаров и транспортных средств в
                                                                                     // пограничный пункт пропуска. ТД

        result.setESADoutCUDepartureArrivalTransport(getDepartureArrivalTransport()); // Транспортные средства при
                                                                                      // прибытии/убытии. Гр. 18, 26

        result.setESADoutCUBorderTransport(getDepartureBorderTransportType()); // Транспортные средства на границе. Гр.
                                                                               // 21, 25 ДТ
        // old 5.10
        // result.setDeliveryCustomsOffice(getDeliveryCustomsOffice()); // Таможенный орган назначения при
        // транзите.гр.53 ТД
        // new 5.11
        buildTDDeliveryPlace(result);

        result.setPPBorderCustoms(getPPBorderCustoms()); // Таможенный орган (пограничный пункт пропуска), в который
                                                         // ожидается прибытие товаров и транспортных средств
        buildBorderCustomsOffice(result.getBorderCustomsOffice()); // Таможенный орган въезда/выезда. Гр.29
        buildESADoutCUReloadingInfo(result.getESADoutCUReloadingInfo()); // Информация о перегрузке товаров. Гр. 55 ТД
        return result;
    }

    private ESADoutCUDepartureArrivalTransportType getDepartureArrivalTransport() {
        // TODO Auto-generated method stub
        // Транспортные средства при прибытии/убытии. Гр. 18, 26
        ESADoutCUDepartureArrivalTransportType result = new ESADoutCUDepartureArrivalTransportType();
        result.setTransportModeCode(RAILWAY_TRANSPORT_CODE); // Транспортные средства при прибытии/убытии. Гр. 18, 26
        result.setTransportNationalityCode(getTransportNationalityCode()); // Буквенный код страны принадлежности
                                                                           // (регистрации) транспортных средств по
                                                                           // классификатору стран мира. 99-разные,
                                                                           // 00-неизвестна
        result.setTransportMeansQuantity(getTransportMeansQuantity(true)); // Количество транспортных средств
        result.setMethodTransport(null); // Способ транспортировки товаров при перемещении по линиям электропередачи или
                                         // трубопроводным транспортом: 1 - газопровод; 2 - нефтепровод; 3 -
                                         // нефтепродуктопровод; 4 - линии электропередачи
        result.setNameObject(null); // Наименование объекта, на котором установлены приборы учета товаров, перемещаемых
                                    // по линиям электропередачи или трубопроводным транспортом
        buildTransportMeans(result.getTransportMeans(), true); // Описание транспортного средства
        return result;
    }

    private ESADoutCUBorderTransportType getDepartureBorderTransportType() {
        // Транспортные средства на границе. Гр. 21, 25 ДТ
        ESADoutCUBorderTransportType result = new ESADoutCUBorderTransportType();
        result.setTransportModeCode(RAILWAY_TRANSPORT_CODE); // Транспортные средства при прибытии/убытии. Гр. 18, 26
        result.setTransportNationalityCode(getTransportNationalityCode()); // Буквенный код страны принадлежности
                                                                           // (регистрации) транспортных средств по
                                                                           // классификатору стран мира. 99-разные,
                                                                           // 00-неизвестна
        result.setTransportMeansQuantity(getTransportMeansQuantity(false)); // Количество транспортных средств
        result.setMethodTransport(null); // Способ транспортировки товаров при перемещении по линиям электропередачи или
                                         // трубопроводным транспортом: 1 - газопровод; 2 - нефтепровод; 3 -
                                         // нефтепродуктопровод; 4 - линии электропередачи
        result.setNameObject(null); // Наименование объекта, на котором установлены приборы учета товаров, перемещаемых
                                    // по линиям электропередачи или трубопроводным транспортом
        if (buildTransportMeans(result.getTransportMeans(), false)) // Описание транспортного средства
            return result;
        else
            return null;
    }

    private BigInteger getTransportMeansQuantity(boolean isArrival) {
        // Количество транспортных средств
        int count = 0;
        if (invoice.getIsContainer() != 1 && vagonLists != null && !vagonLists.isEmpty() && vagonLists.size() > 1
                        && mapVagons != null) {

            for (NeVagonLists vagon : vagonLists) {
                if (mapVagons.get(vagon.getVagNo()) != null) {
                    if (isArrival)
                        count++;
                } else {
                    if (!isArrival)
                        count++;
                }
            }
            return BigInteger.valueOf(count);
        }

        return BigInteger.valueOf(vagonLists.size());
    }

    private String getTransportNationalityCode() {
        // Буквенный код страны принадлежности (регистрации) транспортных средств по классификатору стран
        // мира. 99-разные, 00-неизвестна
        return null;
        /*
         * String result = null; for(NeVagonLists vagon: vagonLists) { String item =
         * getTransportMeansNationalityCode(vagon.getOwnerRailways()); if(result == null) { if (item !=
         * null) { result = item; } } else if(!result.equals(item)) { result = "99"; break; } } return
         * (result == null ? "00" : result);
         */ }

    /**
     * 18 и 21 графа
     * 
     * @param transportMeans
     * @param isArrival 18 - isArriavl = true; 21 - isArriavl = false
     */
    private boolean buildTransportMeans(List<TransportMeansBaseType> transportMeans, boolean isArrival) {
        // признак добавления блока
        boolean isResult = true;

        boolean isOtherOwner = false;
        boolean isKzOwner = false;
        boolean isParom = Arrays.asList("689202", "693807").contains(invoicePrevInfo.getArriveStaNo());
        boolean isChine = Arrays.asList("708507", "707608", "708403", "707701", "710102")
                        .contains(invoicePrevInfo.getArriveStaNo());
        // Признак контейнерной перевозки

        if (invoice.getIsContainer() == 1) {
            boolean has7Length = false;
            if (vagonLists != null && vagonLists.size() > 0) {
                for (NeVagonLists vagon : vagonLists) {
                    if (vagon.getVagNo().length() == 7) {
                        has7Length = true;
                        break;
                    }
                }
            }

            if (isArrival) {// 18
                // Описание транспортного средства
                if (has7Length) {
                    for (NeContainerLists container : containerList) {
                        TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                        transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                        transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                        transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
                        transportMeansBaseType.setTransportIdentifier(
                                        container.getContainerMark() + container.getContainerNo()); // Идентификатор.
                                                                                                    // Номер
                                                                                                    // транспортного
                                                                                                    // средства,
                                                                                                    // наименование
                                                                                                    // судна, номер
                                                                                                    // авиарейса, номер
                                                                                                    // поезда, номер
                                                                                                    // железнодорожного
                                                                                                    // вагона (платформ,
                                                                                                    // цистерн и т.п.)
                        // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(container.getManagUn()));
                        // // Буквенный код страны принадлежности транспортного средства по классификатору стран мира
                        transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
                                                                            // транспортного средства для РБ
                        transportMeans.add(transportMeansBaseType);
                    }
                } else {
                    for (NeVagonLists vagon : vagonLists) {
                        TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                        transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                        transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                        transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
                        transportMeansBaseType.setTransportIdentifier(vagon.getVagNo()); // Идентификатор. Номер
                                                                                         // транспортного средства,
                                                                                         // наименование судна, номер
                                                                                         // авиарейса, номер поезда,
                                                                                         // номер железнодорожного
                                                                                         // вагона (платформ, цистерн и
                                                                                         // т.п.)
                        // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
                        // // Буквенный код страны принадлежности транспортного средства по классификатору стран мира
                        transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
                                                                            // транспортного средства для РБ
                        transportMeans.add(transportMeansBaseType);
                    }
                }
            } else {
                // 21 графа для
                if (has7Length && vagonLists != null && vagonLists.size() > 0) {
                    NeVagonLists vagon = vagonLists.get(0);
                    TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                    transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                    transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                    transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
                    transportMeansBaseType.setTransportIdentifier(vagon.getVagNo()); // Идентификатор. Номер
                                                                                     // транспортного средства,
                                                                                     // наименование судна, номер
                                                                                     // авиарейса, номер поезда, номер
                                                                                     // железнодорожного вагона
                                                                                     // (платформ, цистерн и т.п.)
                    // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
                    // // Буквенный код страны принадлежности транспортного средства по классификатору стран мира
                    transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
                                                                        // транспортного средства для РБ
                    transportMeans.add(transportMeansBaseType);
                } else if (isParom) {
                    TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                    transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                    transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                    transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства

                    NeVessel ves = dao.getVessel(ship.getNeVesselUn());
                    if (ves != null) {
                        transportMeansBaseType.setTransportIdentifier(ves.getVesselName());
                        transportMeansBaseType.setTransportMeansNationalityCode("AZ"); // Буквенный код страны
                                                                                       // принадлежности транспортного
                                                                                       // средства по классификатору
                                                                                       // стран мира
                    }
                    // Идентификатор. Номер транспортного средства, наименование судна, номер авиарейса, номер поезда,
                    // номер железнодорожного вагона (платформ, цистерн и т.п.)

                    transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
                                                                        // транспортного средства для РБ
                    transportMeans.add(transportMeansBaseType);
                }
            }

            // if(isParom) {
            // if (isArrival) {//
            // for(NeVagonLists vagon: vagonLists) {
            // TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
            // transportMeansBaseType.setVIN(null); //Номер шасси (VIN)
            // transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
            // transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
            // transportMeansBaseType.setTransportIdentifier(vagon.getVagNo()); //Идентификатор. Номер
            // транспортного средства, наименование судна, номер авиарейса, номер поезда, номер железнодорожного
            // вагона (платформ, цистерн и т.п.)
            // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
            // // Буквенный код страны принадлежности транспортного средства по классификатору стран мира
            // transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
            // транспортного средства для РБ
            // transportMeans.add(transportMeansBaseType);
            // }
            // } else {
            //
            // }
            // } else {
            // if (isArrival) {//18
            // for (String item : containers.keySet()) {
            // if (!StringUtils.isEmpty(item)) {
            // TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
            // transportMeansBaseType.setVIN(null); //Номер шасси (VIN)
            // transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
            // transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
            // transportMeansBaseType.setTransportIdentifier(item); //Идентификатор. Номер транспортного
            // средства, наименование судна, номер авиарейса, номер поезда, номер железнодорожного вагона
            // (платформ, цистерн и т.п.)
            // for(NeVagonLists vagon: vagonLists)
            // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
            // // Буквенный код страны принадлежности транспортного средства по классификатору стран мира
            // transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
            // транспортного средства для РБ
            // transportMeans.add(transportMeansBaseType);
            // }
            // }
            // } else {//21
            //
            // }
            // }

        } else {// повагонка
            int size = vagonLists.size();

            if (!isArrival && isParom) {
                TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства

                NeVessel ves = dao.getVessel(ship.getNeVesselUn());
                if (ves != null) {
                    transportMeansBaseType.setTransportIdentifier(ves.getVesselName());// Идентификатор. Номер
                                                                                       // транспортного средства,
                                                                                       // наименование судна, номер
                                                                                       // авиарейса, номер поезда, номер
                                                                                       // железнодорожного вагона
                                                                                       // (платформ, цистерн и т.п.)
                    transportMeansBaseType.setTransportMeansNationalityCode("AZ"); // Буквенный код страны
                                                                                   // принадлежности транспортного
                                                                                   // средства по классификатору стран
                                                                                   // мира
                }

                transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации транспортного
                                                                    // средства для РБ
                transportMeans.add(transportMeansBaseType);
            } else {
                for (NeVagonLists vagon : vagonLists) {
                    TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                    if (size > 1) {
                        DataCaneVagInfo info = null;
                        if (mapVagons != null) {
                            info = mapVagons.get(vagon.getVagNo());
                        }
                        if (isArrival) { // 18 гр.
                            if (info == null) {
                                continue;
                            } else {
                                isKzOwner = true;
                            }
                        } else {
                            if (info != null) {
                                continue;
                            } else {
                                isOtherOwner = true;
                            }
                        }
                    }
                    transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                    transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                    transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
                    transportMeansBaseType.setTransportIdentifier(vagon.getVagNo()); // Идентификатор. Номер
                                                                                     // транспортного средства,
                                                                                     // наименование судна, номер
                                                                                     // авиарейса, номер поезда, номер
                                                                                     // железнодорожного вагона
                                                                                     // (платформ, цистерн и т.п.)
                    // if(!isOtherOwner && !isKzOwner){
                    // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
                    // // Буквенный код страны принадлежности транспортного средства по классификатору стран мира
                    // } else if (isKzOwner){
                    // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode("27"));
                    // } else if (isOtherOwner){
                    // if("156".equals(invoicePrevInfo.getStartStaCouNo())){//fix для китая
                    // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode("33"));
                    // } else {
                    // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
                    // }
                    // }
                    transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
                                                                        // транспортного средства для РБ
                    transportMeans.add(transportMeansBaseType);
                }
            }



            // Доп. логика 18 графы, запись если все для Китая //ESADout_CUDepartureArrivalTransport
            if (isArrival && transportMeans.size() == 0) {
                for (NeVagonLists vagon : vagonLists) {
                    TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                    transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                    transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                    transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
                    transportMeansBaseType.setTransportIdentifier(vagon.getVagNo()); // Идентификатор. Номер
                                                                                     // транспортного средства,
                                                                                     // наименование судна, номер
                                                                                     // авиарейса, номер поезда, номер
                                                                                     // железнодорожного вагона
                                                                                     // (платформ, цистерн и т.п.)
                    // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
                    transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
                                                                        // транспортного средства для РБ
                    transportMeans.add(transportMeansBaseType);
                }
            }

            // DKR-10222 ESADout_CUBorderTransport
            if (!isArrival && transportMeans.size() == 0) {// 21
                for (NeVagonLists vagon : vagonLists) {
                    TransportMeansBaseType transportMeansBaseType = new TransportMeansBaseType();
                    transportMeansBaseType.setVIN(null); // Номер шасси (VIN)
                    transportMeansBaseType.setTransportKindCode("20"); // Код типа транспортного средства
                    transportMeansBaseType.setTransportMarkCode(null); // Код марки транспортного средства
                    transportMeansBaseType.setTransportIdentifier(vagon.getVagNo()); // Идентификатор. Номер
                                                                                     // транспортного средства,
                                                                                     // наименование судна, номер
                                                                                     // авиарейса, номер поезда, номер
                                                                                     // железнодорожного вагона
                                                                                     // (платформ, цистерн и т.п.)
                    // transportMeansBaseType.setTransportMeansNationalityCode(getTransportMeansNationalityCode(vagon.getOwnerRailways()));
                    transportMeansBaseType.setTransportRegNumber(null); // Номер свидетельства о регистрации
                                                                        // транспортного средства для РБ
                    if (getExportTD() == null) // DKR-10564
                        transportMeans.add(transportMeansBaseType);
                }
            }
        }

        if (!isArrival) { // гр.21 DKR-10623
            // boolean isChine = "156".equals(invoicePrevInfo.getStartStaCouNo());

            for (NeVagonLists vagon : vagonLists) {
                DataCaneVagInfo info = null;
                if (mapVagons != null) {
                    info = mapVagons.get(vagon.getVagNo());
                }
                if (info != null) {
                    isKzOwner = true;
                } else {
                    isOtherOwner = true;
                }
            }
            if (invoice.getIsContainer() == 1) {
                // if (!isChine)
                // isResult = false;
            } else {
                if (isKzOwner && !isChine)
                    isResult = false;
            }

            if (isParom) {
                isResult = true;
            }

        }
        System.out.println("+++++isKzOwner:" + isKzOwner);
        System.out.println("+++++isResult:" + isResult);

        return isResult;
    }

    private String getTransportMeansNationalityCode(Long managUn) {
        String result = null;
        if (managUn == null) {
            return result;
        }
        Country country = dao.getCountryByManagUn(managUn);
        if (country != null) {
            result = country.getCountryId();
        }
        return result;
    }

    private String getTransportMeansNationalityCode(String ownerRailways) {
        // Буквенный код страны принадлежности транспортного средства по классификатору стран мира
        if (ownerRailways != null) {
            int managNo = Integer.valueOf(ownerRailways);
            Country country = dao.getCountryByManagNo(managNo);
            if (country != null) {
                return country.getCountryId();
            }
        }
        return null;
    }

    private CUCustomsType getDeliveryCustomsOffice() {
        // Таможенный орган назначения при транзите.гр.53 ТД
        CUCustomsType dest = null;
        if (neSmgsDestinationPlaceInfo != null) {
            NeCustomsOrgs destCustoms = dao.getCustomsOrgs(neSmgsDestinationPlaceInfo.getDestPlaceCustomOrgUn());
            if (destCustoms != null) {
                dest = new CUCustomsType();
                dest.setCode(destCustoms.getCustomCode());
                Country country = dao.getCountry(destCustoms.getCountryNo());
                dest.setCustomsCountryCode(country.getCountryNo());
                dest.setOfficeName(destCustoms.getCustomName());
            }
        }
        return dest;
    }

    private CUCustomsType getPPBorderCustoms() {
        // Таможенный орган (пограничный пункт пропуска), в который ожидается прибытие товаров и
        // транспортных средств
        CUCustomsType result = new CUCustomsType();
        if (customsOrgs != null) {
            result.setCode(customsOrgs.getCustomCode());
            Country country = dao.getCountry(customsOrgs.getCountryNo());
            result.setCustomsCountryCode(country.getCountryNo());
            result.setOfficeName(customsOrgs.getCustomName());
        }
        return result;
    }

    private void buildESADoutCUReloadingInfo(List<ESADReloadingInfoType> doutCUReloadingInfo) {
        // TODO Auto-generated method stub
        // Информация о перегрузке товаров. Гр. 55 ТД
    }

    private void buildBorderCustomsOffice(List<CUCustomsType> borderCustomsOffice) {
        // Таможенный орган въезда/выезда. Гр.29
        CUCustomsType cust1 = getPPBorderCustoms();
        CUCustomsType cust2 = getDeliveryCustomsOffice();
        if (cust1 != null) {
            borderCustomsOffice.add(cust1);
        }
        if (cust2 != null) {
            borderCustomsOffice.add(cust2);
        }
    }

    private void buildTDDeliveryPlace(ESADoutCUConsigmentType type) {
        // Место назначения при транзите. Гр.53
        TDDeliveryPlaceType placeType = new TDDeliveryPlaceType();
        CUCustomsType cust1 = getDeliveryCustomsOffice();
        if (cust1 != null) {
            placeType.setDeliveryCustomsOffice(cust1);
            type.setTDDeliveryPlace(placeType);
        }
    }

    private void buildGoodsLocation(List<ESADoutCUGoodsLocationType> doutCUGoodsLocation) {
        // TODO Auto-generated method stub
        // Местонахождение товаров. Гр. 30 ДТ
    }

    private String replaceSpacSymbol(String string) {
        if (string == null) {
            return null;
        }
        return string.replaceAll("&", "and").replace("«", "\"").replace("»", "\"");
    }

    private ESADoutCUCarrierType getCarrier() {
        // Сведения о перевозчике гр. 50 ТД
        ESADoutCUCarrierType result = new ESADoutCUCarrierType();
        AddressType addressType = new AddressType();
        RKOrganizationFeaturesType featuresType = new RKOrganizationFeaturesType();
        ITNKZType itnkz = new ITNKZType();
        // LegalPerson ktzh = dao.getLegalPerson(KTZH_LEL_PERS_UN);
        result.setOrganizationName(replaceSpacSymbol(customsPersName));
        result.setShortName(replaceSpacSymbol(customsPersSName));

        log.debug("org name: " + result.getOrganizationName());
        log.debug("short name: " + result.getShortName());
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
        itnkz.setKATOCode("11");
        itnkz.setCategoryCode("05");
        // itnkz.setRNN(ktzh.getLPersRnn());
        if (!StringUtils.isEmpty(itnkz.getCategoryCode()) || !StringUtils.isEmpty(itnkz.getITNReserv())
                        || !StringUtils.isEmpty(itnkz.getKATOCode()) || !StringUtils.isEmpty(itnkz.getRNN())) {
            featuresType.setITN(itnkz);
        }
        result.setRKOrganizationFeatures(featuresType);
        return result;
    }

    private ESADoutCUDeclarantType getDeclarant() {
        // Сведения о декларанте товаров
        ESADoutCUDeclarantType result = new ESADoutCUDeclarantType();
        if (declarantInfo != null) {
            Country country = dao.getCountry(declarantInfo.getDeclarantCountry());
            result.setOrganizationName(replaceSpacSymbol(declarantInfo.getDeclarantName())); // Наименование организации
                                                                                             // / ФИО физического лица
            result.setShortName(replaceSpacSymbol(declarantInfo.getDeclarantShortName())); // Краткое наименование
                                                                                           // организации
            result.setOrganizationLanguage(DOCUMENT_LANGUAGE); // Код языка для заполнения наименования
            if ("KZ".equals(country.getCountryId())) {
                RKOrganizationFeaturesType features = new RKOrganizationFeaturesType();
                features.setBIN(declarantInfo.getDeclarantKZBin()); // Бизнес-идентификационный номер (БИН)
                features.setIIN(!StringUtils.isEmpty(declarantInfo.getDeclarantKZIin())
                                ? declarantInfo.getDeclarantKZIin()
                                : null); // Индивидуальный идентификационный номер (ИИН)
                ITNKZType itn = new ITNKZType();
                itn.setCategoryCode(declarantInfo.getDeclarantKZPersonsCategory()); // Категория лица. Двухзначный
                                                                                    // цифровой код категории лица
                                                                                    // согласно учредительным документам
                                                                                    // или свидетельству о регистрации в
                                                                                    // качестве индивидуального
                                                                                    // предпринимателя
                itn.setITNReserv(!StringUtils.isEmpty(declarantInfo.getDeclarantKZITN())
                                ? declarantInfo.getDeclarantKZITN()
                                : null); // Резерв для ИТН
                itn.setRNN(null); // РНН. Регистрационный налоговый номер, присваиваемый налоговыми органами Республики
                                  // Казахстан
                itn.setKATOCode(declarantInfo.getDeclarantKZKATO()); // Код КАТО. Двухзначный код КАТО в соответствии с
                                                                     // классификатором кодов
                                                                     // административно-территориальных объектов
                if (!StringUtils.isEmpty(itn.getCategoryCode()) || !StringUtils.isEmpty(itn.getITNReserv())
                                || !StringUtils.isEmpty(itn.getKATOCode()) || !StringUtils.isEmpty(itn.getRNN())) {
                    features.setITN(itn); // Идентификационный таможенный номер (ИТН)
                }
                result.setRKOrganizationFeatures(features); // Сведения об организации. Особенности Республики Казахстан
            } else if ("RU".equals(country.getCountryId())) {
                RFOrganizationFeaturesType rfFeaturesType = new RFOrganizationFeaturesType();
                rfFeaturesType.setOGRN(declarantInfo.getDeclarantRUOGRN());
                rfFeaturesType.setINN(declarantInfo.getDeclarantRUINN());
                rfFeaturesType.setKPP(declarantInfo.getDeclarantRUKPP());
                result.setRFOrganizationFeatures(rfFeaturesType); // Сведения об организации. Особенности Российской
                                                                  // Федерации
            } else if ("AM".equals(country.getCountryId())) {
                RAOrganizationFeaturesType raFeaturesType = new RAOrganizationFeaturesType();
                raFeaturesType.setSocialServiceNumber(declarantInfo.getDeclarantAMNZOU());
                raFeaturesType.setUNN(declarantInfo.getDeclarantAMUNN());
                result.setRAOrganizationFeatures(raFeaturesType); // Сведения об организации. Особенности республики
                                                                  // Армения
            } else if ("BY".equals(country.getCountryId())) {
                RBOrganizationFeaturesType rbFeaturesType = new RBOrganizationFeaturesType();
                rbFeaturesType.setRBIdentificationNumber(declarantInfo.getDeclarantBYIN());
                rbFeaturesType.setUNP(declarantInfo.getDeclarantBYUNP());
                result.setRBOrganizationFeatures(rbFeaturesType); // Сведения об организации. Особенности Республики
                                                                  // Беларусь
            } else if ("KG".equals(country.getCountryId())) {
                KGOrganizationFeaturesType kgFeaturesType = new KGOrganizationFeaturesType();
                kgFeaturesType.setKGINN(declarantInfo.getDeclarantKGINN());
                kgFeaturesType.setKGOKPO(declarantInfo.getDeclarantKGOKPO());
                result.setKGOrganizationFeatures(kgFeaturesType); // Сведения об организации. Особенности Кыргызской
                                                                  // Республики
            }
            AddressType address = new AddressType();
            if (country != null) {
                address.setCountryCode(country.getCountryId()); // Буквенный код страны в соответствии с классификатором
                                                                // стран мира
                address.setCounryName(country.getCountryName()); // Краткое название страны в соответствии с
                                                                 // классификатором стран мира
                address.setRegion(declarantInfo.getDeclarantRegion()); // Область (регион, штат, провинция и т.п.)
                address.setCity(declarantInfo.getDeclarantCity()); // Населенный пункт
                address.setStreetHouse(declarantInfo.getDeclarantAddress()); // Улица, номер дома, номер офиса
                address.setPostalCode("000000"); // Почтовый индекс
                address.setTerritoryCode(null); // Код единицы административно-территориального деления
            }
            result.setAddress(address); // Адрес организации
            result.setIdentityCard(null); // Документ, удостоверяющий личность
            ContactType contact = new ContactType();
            // contact.getPhone().add(declarantInfo.getRecieverTelephoneNum());
            // contact.setFax(declarantInfo.getRecieverTelefaxNum());
            // contact.getEMail().add(declarantInfo.getRecieverEmail());
            result.setContact(contact); // Контактная информация
            result.setBranchDescription(null); // Сведения об обособленном подразделении
        }
        return result;
    }

    private ESADoutCUDeclarantType getExpeditor() {
        // Сведения о декларанте товаров
        ESADoutCUDeclarantType result = new ESADoutCUDeclarantType();
        if (expeditorInfo != null) {
            Country country = dao.getCountry(expeditorInfo.getExpeditorCountry());
            result.setOrganizationName(replaceSpacSymbol(expeditorInfo.getExpeditorName())); // Наименование организации
                                                                                             // / ФИО физического лица
            result.setShortName(replaceSpacSymbol(expeditorInfo.getExpeditorShortName())); // Краткое наименование
                                                                                           // организации
            result.setOrganizationLanguage(DOCUMENT_LANGUAGE); // Код языка для заполнения наименования
            if ("KZ".equals(country.getCountryId())) {
                RKOrganizationFeaturesType features = new RKOrganizationFeaturesType();
                features.setBIN(expeditorInfo.getExpeditorKZBin()); // Бизнес-идентификационный номер (БИН)
                features.setIIN(!StringUtils.isEmpty(expeditorInfo.getExpeditorKZIin())
                                ? expeditorInfo.getExpeditorKZIin()
                                : null); // Индивидуальный идентификационный номер (ИИН)
                ITNKZType itn = new ITNKZType();
                itn.setCategoryCode(expeditorInfo.getExpeditorKZPersonsCategory()); // Категория лица. Двухзначный
                                                                                    // цифровой код категории лица
                                                                                    // согласно учредительным документам
                                                                                    // или свидетельству о регистрации в
                                                                                    // качестве индивидуального
                                                                                    // предпринимателя
                itn.setITNReserv(!StringUtils.isEmpty(expeditorInfo.getExpeditorKZITN())
                                ? expeditorInfo.getExpeditorKZITN()
                                : null); // Резерв для ИТН
                itn.setRNN(null); // РНН. Регистрационный налоговый номер, присваиваемый налоговыми органами Республики
                                  // Казахстан
                itn.setKATOCode(expeditorInfo.getExpeditorKZKATO()); // Код КАТО. Двухзначный код КАТО в соответствии с
                                                                     // классификатором кодов
                                                                     // административно-территориальных объектов
                if (!StringUtils.isEmpty(itn.getCategoryCode()) || !StringUtils.isEmpty(itn.getITNReserv())
                                || !StringUtils.isEmpty(itn.getKATOCode()) || !StringUtils.isEmpty(itn.getRNN())) {
                    features.setITN(itn); // Идентификационный таможенный номер (ИТН)
                }
                result.setRKOrganizationFeatures(features); // Сведения об организации. Особенности Республики Казахстан
            } else if ("RU".equals(country.getCountryId())) {
                RFOrganizationFeaturesType rfFeaturesType = new RFOrganizationFeaturesType();
                rfFeaturesType.setOGRN(expeditorInfo.getExpeditorRUOGRN());
                rfFeaturesType.setINN(expeditorInfo.getExpeditorRUINN());
                rfFeaturesType.setKPP(expeditorInfo.getExpeditorRUKPP());
                result.setRFOrganizationFeatures(rfFeaturesType); // Сведения об организации. Особенности Российской
                                                                  // Федерации
            } else if ("AM".equals(country.getCountryId())) {
                RAOrganizationFeaturesType raFeaturesType = new RAOrganizationFeaturesType();
                raFeaturesType.setSocialServiceNumber(expeditorInfo.getExpeditorAMNZOU());
                raFeaturesType.setUNN(expeditorInfo.getExpeditorAMUNN());
                result.setRAOrganizationFeatures(raFeaturesType); // Сведения об организации. Особенности республики
                                                                  // Армения
            } else if ("BY".equals(country.getCountryId())) {
                RBOrganizationFeaturesType rbFeaturesType = new RBOrganizationFeaturesType();
                rbFeaturesType.setRBIdentificationNumber(expeditorInfo.getExpeditorBYIN());
                rbFeaturesType.setUNP(expeditorInfo.getExpeditorBYUNP());
                result.setRBOrganizationFeatures(rbFeaturesType); // Сведения об организации. Особенности Республики
                                                                  // Беларусь
            } else if ("KG".equals(country.getCountryId())) {
                KGOrganizationFeaturesType kgFeaturesType = new KGOrganizationFeaturesType();
                kgFeaturesType.setKGINN(expeditorInfo.getExpeditorKGINN());
                kgFeaturesType.setKGOKPO(expeditorInfo.getExpeditorKGOKPO());
                result.setKGOrganizationFeatures(kgFeaturesType); // Сведения об организации. Особенности Кыргызской
                                                                  // Республики
            }
            AddressType address = new AddressType();
            if (country != null) {
                address.setCountryCode(country.getCountryId()); // Буквенный код страны в соответствии с классификатором
                                                                // стран мира
                address.setCounryName(country.getCountryName()); // Краткое название страны в соответствии с
                                                                 // классификатором стран мира
                address.setRegion(expeditorInfo.getExpeditorRegion()); // Область (регион, штат, провинция и т.п.)
                address.setCity(expeditorInfo.getExpeditorCity()); // Населенный пункт
                address.setStreetHouse(expeditorInfo.getExpeditorAddress()); // Улица, номер дома, номер офиса
                address.setPostalCode("000000"); // Почтовый индекс
                address.setTerritoryCode(null); // Код единицы административно-территориального деления
            }
            result.setAddress(address); // Адрес организации
            result.setIdentityCard(null); // Документ, удостоверяющий личность
            ContactType contact = new ContactType();
            // contact.getPhone().add(expeditorInfo.getRecieverTelephoneNum());
            // contact.setFax(expeditorInfo.getRecieverTelefaxNum());
            // contact.getEMail().add(expeditorInfo.getRecieverEmail());
            result.setContact(contact); // Контактная информация
            result.setBranchDescription(null); // Сведения об обособленном подразделении
        }
        return result;
    }

    private ESADoutCUFinancialAdjustingResponsiblePersonType getFinancialAdjustingResponsiblePerson() {
        // TODO Auto-generated method stub
        // Лицо ответственное за финансовое урегулирование
        ESADoutCUFinancialAdjustingResponsiblePersonType result =
                        new ESADoutCUFinancialAdjustingResponsiblePersonType();
        return result;
    }

    private ESADoutCUConsigneeType getConsignee() {
        // "Получатель"
        ESADoutCUConsigneeType result = new ESADoutCUConsigneeType();
        if (recieverInfo != null) {
            Country country = dao.getCountry(recieverInfo.getRecieverCountryCode());
            result.setOrganizationName(recieverInfo.getRecieverFullName() != null
                            ? replaceSpacSymbol(recieverInfo.getRecieverFullName())
                            : null); // Наименование организации / ФИО физического лица
            result.setShortName(
                            recieverInfo.getRecieverName() != null ? replaceSpacSymbol(recieverInfo.getRecieverName())
                                            : null); // Краткое наименование организации
            result.setOrganizationLanguage(DOCUMENT_LANGUAGE); // Код языка для заполнения наименования
            if (country != null) {
                if ("KZ".equals(country.getCountryId())) {
                    RKOrganizationFeaturesType features = new RKOrganizationFeaturesType();
                    features.setBIN(recieverInfo.getRecieverBin()); // Бизнес-идентификационный номер (БИН)
                    features.setIIN(!StringUtils.isEmpty(recieverInfo.getRecieverIin()) ? recieverInfo.getRecieverIin()
                                    : null); // Индивидуальный идентификационный номер (ИИН)
                    ITNKZType itn = new ITNKZType();
                    itn.setCategoryCode(getCategoryCodeReciever(recieverInfo.getCategoryType())); // Категория лица.
                                                                                                  // Двухзначный
                                                                                                  // цифровой код
                                                                                                  // категории лица
                                                                                                  // согласно
                                                                                                  // учредительным
                                                                                                  // документам или
                                                                                                  // свидетельству о
                                                                                                  // регистрации в
                                                                                                  // качестве
                                                                                                  // индивидуального
                                                                                                  // предпринимателя
                    itn.setITNReserv(!StringUtils.isEmpty(recieverInfo.getItn()) ? recieverInfo.getItn() : null); // Резерв
                                                                                                                  // для
                                                                                                                  // ИТН
                    itn.setRNN(null); // РНН. Регистрационный налоговый номер, присваиваемый налоговыми органами
                                      // Республики Казахстан
                    itn.setKATOCode(recieverInfo.getKatoType() != null ? recieverInfo.getKatoType().toString() : null); // Код
                                                                                                                        // КАТО.
                                                                                                                        // Двухзначный
                                                                                                                        // код
                                                                                                                        // КАТО
                                                                                                                        // в
                                                                                                                        // соответствии
                                                                                                                        // с
                                                                                                                        // классификатором
                                                                                                                        // кодов
                                                                                                                        // административно-территориальных
                                                                                                                        // объектов
                    if (!StringUtils.isEmpty(itn.getCategoryCode()) || !StringUtils.isEmpty(itn.getITNReserv())
                                    || !StringUtils.isEmpty(itn.getKATOCode()) || !StringUtils.isEmpty(itn.getRNN())) {
                        features.setITN(itn); // Идентификационный таможенный номер (ИТН)
                    }
                    result.setRKOrganizationFeatures(features); // Сведения об организации. Особенности Республики
                                                                // Казахстан
                } else if ("RU".equals(country.getCountryId())) {
                    RFOrganizationFeaturesType rfFeaturesType = new RFOrganizationFeaturesType();
                    rfFeaturesType.setOGRN(recieverInfo.getRecieverBin());
                    rfFeaturesType.setINN(recieverInfo.getRecieverIin());
                    rfFeaturesType.setKPP(recieverInfo.getKpp());
                    result.setRFOrganizationFeatures(rfFeaturesType); // Сведения об организации. Особенности Российской
                                                                      // Федерации
                } else if ("AM".equals(country.getCountryId())) {
                    RAOrganizationFeaturesType raFeaturesType = new RAOrganizationFeaturesType();
                    raFeaturesType.setSocialServiceNumber(recieverInfo.getRecieverBin());
                    raFeaturesType.setUNN(recieverInfo.getRecieverIin());
                    result.setRAOrganizationFeatures(raFeaturesType); // Сведения об организации. Особенности республики
                                                                      // Армения
                } else if ("BY".equals(country.getCountryId())) {
                    RBOrganizationFeaturesType rbFeaturesType = new RBOrganizationFeaturesType();
                    rbFeaturesType.setRBIdentificationNumber(recieverInfo.getRecieverIin());
                    rbFeaturesType.setUNP(recieverInfo.getRecieverBin());
                    result.setRBOrganizationFeatures(rbFeaturesType); // Сведения об организации. Особенности Республики
                                                                      // Беларусь
                } else if ("KG".equals(country.getCountryId())) {
                    KGOrganizationFeaturesType kgFeaturesType = new KGOrganizationFeaturesType();
                    kgFeaturesType.setKGINN(recieverInfo.getRecieverIin());
                    kgFeaturesType.setKGOKPO(recieverInfo.getRecieverBin());
                    result.setKGOrganizationFeatures(kgFeaturesType); // Сведения об организации. Особенности Кыргызской
                                                                      // Республики
                }
            }
            AddressType address = new AddressType();
            if (country != null) {
                address.setCountryCode(country.getCountryId()); // Буквенный код страны в соответствии с классификатором
                                                                // стран мира
                address.setCounryName(country.getCountryName()); // Краткое название страны в соответствии с
                                                                 // классификатором стран мира
                address.setRegion(recieverInfo.getRecieverRegion()); // Область (регион, штат, провинция и т.п.)
                address.setCity(recieverInfo.getRecieverSity()); // Населенный пункт
                address.setStreetHouse(recieverInfo.getRecieverStreet()); // Улица, номер дома, номер офиса
                address.setPostalCode(!StringUtils.isEmpty(recieverInfo.getRecieverPostIndex())
                                ? recieverInfo.getRecieverPostIndex()
                                : "000000"); // Почтовый индекс
                address.setPostalCode("000000"); // Почтовый индекс
                address.setTerritoryCode(null); // Код единицы административно-территориального деления
            }
            result.setAddress(address); // Адрес организации
            result.setIdentityCard(null); // Документ, удостоверяющий личность
            ContactType contact = new ContactType();
            contact.getPhone().add(recieverInfo.getRecieverTelephoneNum());
            contact.setFax(recieverInfo.getRecieverTelefaxNum());
            contact.getEMail().add(recieverInfo.getRecieverEmail());
            result.setContact(contact); // Контактная информация
            result.setOfficesExchangeCode(null); // Код учреждения обмена подачи международных почтовых отправлений
            result.setContractorIndicator(null); // Особенность указанных сведений:1-КОНТРАГЕНТ
            result.setBranchDescription(null); // Сведения об обособленном подразделении
        }
        return result;
    }

    private ESADoutCUConsignorType getConsignor() {
        // "Отправитель/Экспортер"
        ESADoutCUConsignorType result = new ESADoutCUConsignorType();
        if (senderInfo != null) {
            Country country = dao.getCountry(senderInfo.getSenderCountryCode());
            result.setOrganizationName(replaceSpacSymbol(senderInfo.getSenderFullName())); // Наименование организации /
                                                                                           // ФИО физического лица
            result.setShortName(senderInfo.getSenderName() == null ? null
                            : senderInfo.getSenderName().replaceAll("&", "and")); // Краткое наименование организации
            result.setOrganizationLanguage(DOCUMENT_LANGUAGE); // Код языка для заполнения наименования
            if (country != null) {
                if ("KZ".equals(country.getCountryId())) {
                    RKOrganizationFeaturesType features = new RKOrganizationFeaturesType();
                    features.setBIN(senderInfo.getSenderBin()); // Бизнес-идентификационный номер (БИН)
                    features.setIIN(!StringUtils.isEmpty(senderInfo.getSenderIin()) ? senderInfo.getSenderIin() : null); // Индивидуальный
                                                                                                                         // идентификационный
                                                                                                                         // номер
                                                                                                                         // (ИИН)
                    ITNKZType itn = new ITNKZType();
                    itn.setCategoryCode(getCategoryCode(senderInfo.getCategoryType())); // Категория лица. Двухзначный
                                                                                        // цифровой код категории лица
                                                                                        // согласно учредительным
                                                                                        // документам или свидетельству
                                                                                        // о регистрации в качестве
                                                                                        // индивидуального
                                                                                        // предпринимателя
                    itn.setITNReserv(!StringUtils.isEmpty(senderInfo.getItn()) ? senderInfo.getItn() : null); // Резерв
                                                                                                              // для ИТН
                    itn.setRNN(null); // РНН. Регистрационный налоговый номер, присваиваемый налоговыми органами
                                      // Республики Казахстан
                    itn.setKATOCode(senderInfo.getKatoType() != null ? senderInfo.getKatoType().toString() : null); // Код
                                                                                                                    // КАТО.
                                                                                                                    // Двухзначный
                                                                                                                    // код
                                                                                                                    // КАТО
                                                                                                                    // в
                                                                                                                    // соответствии
                                                                                                                    // с
                                                                                                                    // классификатором
                                                                                                                    // кодов
                                                                                                                    // административно-территориальных
                                                                                                                    // объектов
                    if (!StringUtils.isEmpty(itn.getCategoryCode()) || !StringUtils.isEmpty(itn.getITNReserv())
                                    || !StringUtils.isEmpty(itn.getKATOCode()) || !StringUtils.isEmpty(itn.getRNN())) {
                        features.setITN(itn); // Идентификационный таможенный номер (ИТН)
                    }
                    result.setRKOrganizationFeatures(features); // Сведения об организации. Особенности Республики
                                                                // Казахстан
                } else if ("RU".equals(country.getCountryId())) {
                    RFOrganizationFeaturesType rfFeaturesType = new RFOrganizationFeaturesType();
                    rfFeaturesType.setOGRN(senderInfo.getSenderBin());
                    rfFeaturesType.setINN(senderInfo.getSenderIin());
                    rfFeaturesType.setKPP(senderInfo.getKpp());
                    result.setRFOrganizationFeatures(rfFeaturesType); // Сведения об организации. Особенности Российской
                                                                      // Федерации
                } else if ("AM".equals(country.getCountryId())) {
                    RAOrganizationFeaturesType raFeaturesType = new RAOrganizationFeaturesType();
                    raFeaturesType.setSocialServiceNumber(senderInfo.getSenderBin());
                    raFeaturesType.setUNN(senderInfo.getSenderIin());
                    result.setRAOrganizationFeatures(raFeaturesType); // Сведения об организации. Особенности республики
                                                                      // Армения
                } else if ("BY".equals(country.getCountryId())) {
                    RBOrganizationFeaturesType rbFeaturesType = new RBOrganizationFeaturesType();
                    rbFeaturesType.setRBIdentificationNumber(senderInfo.getSenderIin());
                    rbFeaturesType.setUNP(senderInfo.getSenderBin());
                    result.setRBOrganizationFeatures(rbFeaturesType); // Сведения об организации. Особенности Республики
                                                                      // Беларусь
                } else if ("KG".equals(country.getCountryId())) {
                    KGOrganizationFeaturesType kgFeaturesType = new KGOrganizationFeaturesType();
                    kgFeaturesType.setKGINN(senderInfo.getSenderIin());
                    kgFeaturesType.setKGOKPO(senderInfo.getSenderBin());
                    result.setKGOrganizationFeatures(kgFeaturesType); // Сведения об организации. Особенности Кыргызской
                                                                      // Республики
                }
            }
            AddressType address = new AddressType();
            if (country != null) {
                address.setCountryCode(country.getCountryId()); // Буквенный код страны в соответствии с классификатором
                                                                // стран мира
                address.setCounryName(country.getCountryName()); // Краткое название страны в соответствии с
                                                                 // классификатором стран мира
                address.setRegion(senderInfo.getSenderRegion()); // Область (регион, штат, провинция и т.п.)
                address.setCity(senderInfo.getSenderSity()); // Населенный пункт
                address.setStreetHouse(senderInfo.getSenderStreet()); // Улица, номер дома, номер офиса
                // address.setPostalCode(!StringUtils.isEmpty(senderInfo.getSenderPostIndex()) ?
                // senderInfo.getSenderPostIndex() : "000000"); // Почтовый индекс
                address.setPostalCode("000000"); // Почтовый индекс
                address.setTerritoryCode(null); // Код единицы административно-территориального деления
            }
            result.setAddress(address); // Адрес организации
            result.setIdentityCard(null); // Документ, удостоверяющий личность
            ContactType contact = new ContactType();
            contact.getPhone().add(senderInfo.getSenderTelephoneNum());
            contact.setFax(senderInfo.getSenderTelefaxNum());
            contact.getEMail().add(senderInfo.getSenderEmail());
            result.setContact(contact); // Контактная информация
            result.setOfficesExchangeCode(null); // Код учреждения обмена подачи международных почтовых отправлений
            result.setContractorIndicator("1"); // Особенность указанных сведений:1-КОНТРАГЕНТ
            result.setBranchDescription(null); // Сведения об обособленном подразделении
        }
        return result;
    }

    private String getCategoryCode(Long categoryType) {
        NePersonCategoryType personCategoryType = dao.getPersonCategoryType(senderInfo.getCategoryType());
        return personCategoryType != null ? personCategoryType.getCategoryCode() : null;
    }

    private String getCategoryCodeReciever(Long categoryType) {
        NePersonCategoryType personCategoryType = dao.getPersonCategoryType(recieverInfo.getCategoryType());
        return personCategoryType != null ? personCategoryType.getCategoryCode() : null;
    }

    private String getTransitFeature() {
        // Особенность помещения товаров под процедуру таможенного транзита. гр. 1. второй подраздел ТД. МПО
        // - международные почтовые отправления, ФЛ - товары и (или) транспортные средства для личного
        // пользования*
        String result = null;
        if (invoicePrevInfo != null && invoicePrevInfo.getPrevInfoFeatures() != null) {
            if (invoicePrevInfo.getPrevInfoFeatures() == 1) {
                result = "ФЛ";
            } else if (invoicePrevInfo.getPrevInfoFeatures() == 2) {
                result = "МПО";
            }
        }
        return result;
    }

    private String getTransitDirectionCode() {
        // TODO Сделать вычисление
        // Особенность помещения товаров под процедуру таможенного транзита. гр. 1. второй подраздел ТД. МПО
        // - международные почтовые отправления, ФЛ - товары и (или) транспортные средства для личного
        // пользования*
        TransitDirectionCode result = TransitDirectionCode.TR;
        if (invoicePrevInfo != null) {
            if ("1".equals(invoicePrevInfo.getPrevInfoType())) {
                result = TransitDirectionCode.TR;
            } else if ("2".equals(invoicePrevInfo.getPrevInfoType())) {
                result = TransitDirectionCode.IM;
            } else if ("3".equals(invoicePrevInfo.getPrevInfoType())) {
                result = TransitDirectionCode.EK;
            } else if ("4".equals(invoicePrevInfo.getPrevInfoType())) {
                result = TransitDirectionCode.VT;
            } else if ("5".equals(invoicePrevInfo.getPrevInfoType())) {
                result = TransitDirectionCode.TC;
            }
        }
        return result.code;
    }

    private String getCustomsProcedure() {
        // ИМ, ЭК, ТТ. Первый подраздел гр.1 ДТ/ТД
        CustomsProcedure result = CustomsProcedure.TT;
        /*
         * if ("1".equals(invoicePrevInfo.getPrevInfoType()) ) { result = CustomsProcedure.TT; } else if
         * ("2".equals(invoicePrevInfo.getPrevInfoType())) { result = CustomsProcedure.IM; } else { result =
         * CustomsProcedure.EK; }
         */
        return result.getCode();
    }

    public PrevInfoBeanDAOLocal getDao() {
        return dao;
    }

    public void setDao(PrevInfoBeanDAOLocal dao) {
        this.dao = dao;
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

    private void initVagons() {
        if (invoice.getIsContainer() != 1 && vagonLists != null && !vagonLists.isEmpty()) {
            List<String> vagons = new ArrayList<String>();
            for (NeVagonLists vagon : vagonLists) {
                vagons.add(vagon.getVagNo());
            }
            mapVagons = dao.getDatacaneVagInfo(vagons);
        }
    }

    private void initRates(Date date) {
        rates = new HashMap<String, BigDecimal>();
        List<NeCurrencyRates> list = dao.getRates(date);
        if (!list.isEmpty()) {
            for (NeCurrencyRates item : list) {
                Long curCodeUn = item.getCurrencuCodeUn();
                if (curCodeUn != null && item.getRate() != null) {
                    CurrencyCode currencyCode = dao.getCurrencyCode(curCodeUn);
                    String key = currencyCode.getCurCodeLet();
                    BigDecimal value = BigDecimal.valueOf(item.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    rates.put(key, value);
                }
            }
        }
    }

    private void initCurrencyCodes(Date date) {
        List<CurrencyCode> list = dao.getCurrencyCodeList(date);
        currencyCodes = new HashMap<String, CurrencyCode>();
        if (!list.isEmpty()) {
            for (CurrencyCode item : list) {
                String key = item.getCurCode();
                currencyCodes.put(key, item);
            }
        }
    }

    private String getCurrencyLet(String code) {
        String result = null;
        CurrencyCode item = currencyCodes.get(code);
        if (item != null && item.getCurCodeLet() != null) {
            result = item.getCurCodeLet();
        }
        return result;
    }

    /*
     * Send PI to service
     */
    /*
     * private SaveDeclarationResponseType initReplaceTnVedCode() { SaveDeclarationResponseType result =
     * null; replaceTnVedCode = new HashMap<String, String>(); if (sender != null) {
     * PIRWInformationCUType sendData = makeData(invoiceUn); this.uuid = sendData.getUINP();
     * 
     * int attempt = 0; // пытаемся обрезать код ТН ВЭД до 6 символов! boolean ex = true; while (ex) {
     * ex = false; result = sender.send(sendData); if (isGngError(result) && attempt < 3) { //3 попытки
     * attempt++; sendData = changeGng(result, sendData); ex = true; } else { // Ошибка ФЛК 301 со
     * стороны Т. if (attempt != 0) { String code = result.getCode(); String message = "КОД ОШИБКИ - " +
     * code; try { message += getErrValueMess(result, sendData); if (!code.equals("0"))
     * result.setValue(message + " " + result.getValue()); } catch(Exception exn) {
     * exn.printStackTrace(); } } } } } return result; }
     */

    public SaveDeclarationResponseType send(Long invoiceUn) {
        SaveDeclarationResponseType result = null;

        if (invoiceUn != null) {
            user = new User("Altair", "Aimenov");

            ESADoutCUType tdDoc = build(invoiceUn);
            if (tdDoc != null) {
                // System.out.println(getXml(tdDoc));
                result = sendTD(invoiceUn);
            }
            if (result != null && result.getValue() != null) {
                String str = result.getValue().replaceAll("(\\r|\\n)", "<br>").replaceAll("\"", "'");

                if (str != null && str.length() > 250)
                    str = str.substring(0, 250);

                result.setValue(str);
                forDataService.saveCustomsResponse(invoiceUn, result, uuid);

                String uinp = str.substring(0,21);
                result.setValue(uinp);
            }
        }
        return result;
    }

    /*
     * Send TD to service
     */
    public SaveDeclarationResponseType sendTD(long invoiceUn) {
        this.invoiceUn = invoiceUn;
        SaveDeclarationResponseType result = readData();

        if (sender != null) {
            // ESADoutCUType sendData = makeDataTD(invoiceUn);

            this.result.setRefDocumentID(UUID.randomUUID().toString());
            this.uuid = this.result.getRefDocumentID();

            boolean ex = true;
            while (ex) {
                ex = false;
                result = sender.sendTD(this.result);
            }
        }
        return result;
    }

    private SaveDeclarationResponseType initReplaceTnVedCode() {
        SaveDeclarationResponseType result = null;
        return result;
    }

    /**
     * Сформировать ПИ
     * 
     * @param invoiceid
     * @return
     */
    private PIRWInformationCUType makeData(Long invoiceid) {
        PIRWInformationCUType result = null;
        if (invoiceid != null) {
            builder.setDao(dao);
            result = builder.build(invoiceid);
        }
        return result;
    }

    /**
     * Сформировать ТД
     * 
     * @param invoiceid
     * @return
     */
    private ESADoutCUType makeDataTD(Long invoiceid) {
        ESADoutCUType result = null;
        TransitDeclarationService td = new TransitDeclarationService();
        if (invoiceid != null) {
            ESADoutCUType tdDoc = td.build(invoiceid);
            if (tdDoc != null) {
                result = tdDoc;
            }
        }
        return result;
    }

    private boolean isGngError(SaveDeclarationResponseType result) {
        if (result != null && result.getValue() != null) {
            return isGngError(result.getValue());
        }
        return false;
    }

    private boolean isGngError(String result) {
        if (result != null) {
            return result.indexOf(GNG_ERROR) >= 0;
        }
        return false;
    }

    private PIRWInformationCUType changeGng(SaveDeclarationResponseType result, PIRWInformationCUType sendData) {
        if (result != null && result.getValue() != null && sendData != null) {
            String[] splitValue = result.getValue().split("\\n");
            List<String> splitList = new ArrayList<String>();
            for (String item : splitValue) {
                if (isGngError(item)) {
                    splitList.add(item);
                }
            }
            if (!splitList.isEmpty()) {
                Set<Integer> goodsNumber = getGoodsNumber(splitList.get(0));
                System.out.println("Error index: " + goodsNumber);

                for (PICUGoodsShipmentType goodsShipment : sendData.getPICUGoodsShipment()) {
                    for (PIRWGoodsType goods : goodsShipment.getPIGoods()) {
                        System.out.println("current index: " + goods.getGoodsNumeric().intValue());
                        System.out.println("current value: " + goods.getGoodsTNVEDCode());

                        if (goods.getGoodsNumeric() != null
                                        && goodsNumber.contains(goods.getGoodsNumeric().intValue())) {
                            if (goods.getGoodsTNVEDCode() != null && goods.getGoodsTNVEDCode().length() > 6) {
                                // String code = goods.getGoodsTNVEDCode().substring(0,
                                // goods.getGoodsTNVEDCode().length() - 4);
                                String code = goods.getGoodsTNVEDCode().substring(0, 6); // Режим до 6 символов

                                log.info(goods.getGoodsNumeric().intValue() + ": " + goods.getGoodsTNVEDCode() + " --> "
                                                + code);

                                replaceTnVedCode.put(goods.getGoodsTNVEDCode(), code);
                                goods.setGoodsTNVEDCode(code);
                            }
                        }
                    }
                }
            }
        }
        return sendData;
    }

    private String getErrValueMess(SaveDeclarationResponseType result, PIRWInformationCUType sendData) {
        String message = "";
        if (result != null && result.getValue() != null && sendData != null) {
            String[] splitValue = result.getValue().split("\\n");
            List<String> splitList = new ArrayList<String>();
            for (String item : splitValue) {
                if (isGngError(item)) {
                    splitList.add(item);
                }
            }
            if (!splitList.isEmpty()) {
                Set<Integer> goodsNumber = getGoodsNumber(splitList.get(0));
                message += " Ошибочные индексы: " + goodsNumber + ".";
                for (PICUGoodsShipmentType goodsShipment : sendData.getPICUGoodsShipment()) {
                    for (PIRWGoodsType goods : goodsShipment.getPIGoods()) {
                        if (goods.getGoodsNumeric() != null
                                        && goodsNumber.contains(goods.getGoodsNumeric().intValue())) {
                            message += " ( Индекс: " + goods.getGoodsNumeric().toString() + ", Код ТНВЭД: "
                                            + goods.getGoodsTNVEDCode() + " ); ";
                        }
                    }
                }
            }
        }
        return message;
    }

    private Set<Integer> getGoodsNumber(String value) {
        Set<Integer> result = new HashSet<Integer>();
        if (value != null && value.indexOf(GNG_ERROR) >= 0) {
            if (value.indexOf(GNG_FIND_START) >= 0) {
                String str0 = value.substring(value.indexOf(GNG_ERROR) + GNG_ERROR.length());
                while (str0.indexOf(GNG_FIND_START) >= 0) {
                    int start = str0.indexOf(GNG_FIND_START) + GNG_FIND_START.length();
                    String str1 = str0.substring(start);
                    if (str1.indexOf(GNG_FIND_END) >= 0) {
                        start = str1.indexOf(GNG_FIND_END);
                        String str2 = str1.substring(0, start).trim();
                        result.add(Integer.valueOf(str2));
                    }
                    str0 = str1.substring(start, str1.length());
                }
            } else {
                result.add(1);
            }
        }
        return result;
    }



    private SaveDeclarationResponseType readData() {
        if (dao != null) {
            invoice = dao.getInvoice(invoiceUn);
            invoicePrevInfo = dao.getInvoicePrevInfo(invoiceUn);
            senderInfo = dao.getSenderInfo(invoiceUn);
            recieverInfo = dao.getRecieverInfo(invoiceUn);
            neSmgsDestinationPlaceInfo = dao.getNeSmgsDestinationPlaceInfo(invoiceUn);
            tnVedList = dao.getGridDatas(invoiceUn);
            vagonGroup = dao.getVagonGroup(invoiceUn);
            vagonLists = dao.getVagonList(invoiceUn);
            neSmgsCargo = dao.getNeSmgsCargo(invoiceUn);
            declarantInfo = dao.getDeclarantInfo(invoiceUn);
            expeditorInfo = dao.getExpeditorInfo(invoiceUn);
            containerList = dao.getContinerList(invoiceUn);
            customsOrgs = dao.getCustomsOrgs(invoicePrevInfo.getCustomOrgUn());
            ship = dao.getNeSmgsShipList(invoiceUn);
            return setUp();
        }
        return null;
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

    private enum CustomsProcedure { // ИМ, ЭК, ТТ. Первый подраздел гр.1 ДТ/ТД
        // TT("ТТ"),
        TT("TT"), IM("ИМ"), EK("ЭК");

        private String code;

        private CustomsProcedure(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum TransitDirectionCode { // Особенность помещения товаров под процедуру таможенного транзита. гр. 1.
                                       // второй подраздел ТД. МПО - международные почтовые отправления, ФЛ - товары и
                                       // (или) транспортные средства для личного пользования*
        TR(1, "ТР"), IM(2, "ИМ"), EK(3, "ЭК"), VT(4, "ВТ"), TC(5, "ТС");

        private final String code;
        private final Integer order;
        private static final Map<Integer, TransitDirectionCode> lookupByCode =
                        new HashMap<Integer, TransitDirectionCode>(5);

        static {
            for (TransitDirectionCode tranType : values()) {
                lookupByCode.put(tranType.getOrder(), tranType);
            }
        }

        private TransitDirectionCode(Integer order, String code) {
            this.code = code;
            this.order = order;
        }

        public String getCode() {
            return code;
        }

        public Integer getOrder() {
            return order;
        }

        public static TransitDirectionCode getByCode(Integer code) {
            return lookupByCode.get(code);
        }
    }

    public static class User {
        private String name;
        private String surName;

        public User(String name, String surName) {
            this.name = name;
            this.surName = surName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurName() {
            return surName;
        }

        public void setSurName(String surName) {
            this.surName = surName;
        }


    }

}
