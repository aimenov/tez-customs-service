package kz.ne.railways.tezcustoms.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.ne.railways.tezcustoms.service.model.preliminary_information.CommodityPart;
import kz.ne.railways.tezcustoms.service.model.preliminary_information.Declarant;
import kz.ne.railways.tezcustoms.service.model.preliminary_information.DestinationInformation;
import kz.ne.railways.tezcustoms.service.model.preliminary_information.Expeditor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class FormData {

    private String invoiceId;

    // кто какой юзер

    @Schema(description = "Пользователь изменивший запись")
    private Long userUn;

    @Schema(description = "Пользователь изменивший запись для историчности")
    private String user;

    //

    @Schema(description = "Номер накладной")
    @NotBlank
    private String invoiceNumber;

    @Schema(description = "Индекс поезда")
    private String trainIndex;

    @Schema(description = "Принадлежность вагона ")
    private String transportOwner;

    @Schema(description = "Код груза ГНГ")
    @NotBlank
    private String gngCode;


    /* Контейнерная отправка */

    @Schema(description = "Контейнерная отправка", allowableValues = {"0", "1"})
    private String conteinerRef; // is container

    @Schema(description = "Номер контейнера")
    private String ContainerNumber;

    // private Integer containerFilled; //признак заполненности контейнера

    @Schema(description = "Принадлежность контейнера")
    private Long vagonAccessory; // если отправка контейнерная, иначе принадлежность
                                 // вагона(KTZ.NE_VAGON_LISTS.OWNER_RAILWAYS)

    @Schema(description = "Типоразмер контейнера")
    private String containerMark;


    private DicDao containerCountry;
    private DicDao containerCode;

    //////////////////

    private int docType;
    private String invDateTime;
    private String gngName;


    /** NE_INVOICE_PREV_INFO */

    @Schema(description = "Назначение пред.информ-ии")
    @NotBlank
    private String transitDirectionCode;

    @Schema(description = "Особенности")
    private Integer featureType;

    @Schema(description = "Дата прибытие")
    private Timestamp arrivalDate;

    @Schema(description = "Время прибытия")
    private Timestamp arrivalTime;

    @Schema(description = "Станция прибытия")
    @NotBlank
    private String arriveStation;

    /* Таможенный орган */
    @Schema(description = "Таможенный орган спр.")
    private Long customOrgUn;

    @Schema(description = "Таможенный орган код")
    @NotBlank
    private String customCode;

    @Schema(description = "Таможенный орган наим")
    private String customName;

    /////////////////////////////

    @Schema(description = "код УИМП")
    private String responseMessage;

    @Schema(description = "Станция отправления")
    @NotBlank
    private String startStation;

    private String startStationName;
    private String senderSolrUUID;
    private String recieverSolrUUID;
    private String declarantSolrUUID;

    @Schema(description = "Страна отправления")
    @NotBlank
    private String senderCountry; // NeSmgsSenderInfo.SenderCountryCode

    @Schema(description = "Наименование /ФИО")
    @NotBlank
    private String senderName; // NeSmgsSenderInfo.SenderName

    @Schema(description = "Краткое наименование")
    @NotBlank
    private String senderShortName;

    @Schema(description = "Страна")
    @NotBlank
    private String senderCountryName; // NeSmgsSenderInfo.SenderCountryCode

    @Schema(description = "нет в интерфейсе")
    private String senderIndex = "000000"; // NeSmgsSenderInfo.setSenderPostIndex

    @Schema(description = "Сведения о товарной партии")
    private CommodityPart commodityPart;

    private Declarant declarant;
    private Expeditor expeditor;

    @Schema(description = "Сведения о месте назначения")
    private DestinationInformation destinationInformation;

    @Schema(description = "Описание товара")
    private InvoiceData invoiceData;

    @Schema(description = "Населенный пункт")
    @NotBlank
    private String senderPoint; // NeSmgsSenderInfo.setSenderSity
    @Schema(description = "Область (регион, штат и т.п.)")
    @NotBlank
    private String senderOblast;
    @Schema(description = "Улица, номер дома, номер офиса")
    @NotBlank
    private String senderStreetNh;// NeSmgsSenderInfo.SenderStreet
    @Schema(description = "не нужное")
    private String senderBIN;
    @Schema(description = "не нужное")
    private String senderIIN;
    @Schema(description = "не нужное")
    private String senderKATO;
    @Schema(description = "не нужное")
    private String senderKATOName;
    @Schema(description = "не нужное")
    private Long senderKatFace;// Categoriya lica
    @Schema(description = "не нужное")
    private String senderKatFaceCode;
    @Schema(description = "не нужное")
    private String senderKatFaceName;
    @Schema(description = "не нужное")
    private String senderITNreserv; // ИТН Резерв
    @Schema(description = "не нужное")
    private String senderKpp; // КПП


    @Schema(description = "Станция назначения код")
    private String destStation; // Станция назначения NeInvoice
    @Schema(description = "имя станция назначения")
    private String destStationName;
    @Schema(description = "Наименование")
    @NotBlank
    private String recieverName; // NeSmgsRecieverInfo.RecieverName
    @Schema(description = "Краткое наименование")
    @NotBlank
    private String recieverShortNam;
    @Schema(description = "Код страны")
    @NotBlank
    private String recieverCountry; // NeSmgsRecieverInfo.RecieverCountryCode
    @Schema(description = "страна")
    @NotBlank
    private String recieverCountryName; // NeSmgsRecieverInfo.RecieverCountryCode
    private String recieverIndex = "000000"; // NeSmgsRecieverInfo.RecieverPostIndex
    @Schema(description = "Населенный пункт")
    @NotBlank
    private String recieverPoint; // NeSmgsRecieverInfo.RecieverSity
    @Schema(description = "Область (регион, штат и т.п.)")
    @NotBlank
    private String recieverOblast;
    @Schema(description = "Улица, номер дома, номер офиса")
    @NotBlank
    private String recieverStreetNh;// NeSmgsRecieverInfo.RecieverStreet
    @Schema(description = "не нужное")
    private String recieverBIN;
    @Schema(description = "не нужное")
    private String recieverIIN;
    @Schema(description = "не нужное")
    private Long recieverKatFace;
    @Schema(description = "не нужное")
    private String recieverKatFaceCode;
    @Schema(description = "не нужное")
    private String recieverKatFaceName;
    @Schema(description = "не нужное")
    private String recieverKATO;
    @Schema(description = "не нужное")
    private String recieverKATOName;
    @Schema(description = "не нужное")
    private String recieverITNreserv;
    @Schema(description = "не нужное")
    private String recieverKPP;

    /** NE_SMGS_DESTINATION_INFO */ // сведения о месте назначения
    @Schema(description = "Место назначения")
    private String destPlace;
    @Schema(description = "код Станции ")
    private String destPlaceStation;
    @Schema(description = "станция")
    private String destPlaceStationName;
    @Schema(description = "код страны")
    private String destPlaceCountryCode;
    @Schema(description = "страна")
    private String destPlaceCountryName;
    private String destPlaceIndex = "000000"; // индекс
    @Schema(description = "Населенный пункт")
    private String destPlacePoint;
    @Schema(description = "Область (регион, штат и т.п.)")
    private String destPlaceOblast;
    @Schema(description = "Улица, номер дома, номер офиса")
    private String destPlaceStreet;
    @Schema(description = "Таможенный орган спр")
    private Long destPlaceCustomOrgUn; // Таможенный орган спр.
    @Schema(description = "Таможенный орган код")
    private String destPlaceCustomCode;// Таможенный орган код
    @Schema(description = "Таможенный орган наим")
    private String destPlaceCustomName;// Таможенный орган наим


    private String startStaCountry;
    private String destStationCountry;


    private Long vesselUn;// судно для актау паром и курык паром эксп
    private DicDao vessel;

    private List<ContainerData> containers = new ArrayList<ContainerData>();
    private String createDate;// дата создания

    private List<VagonItem> vagonList;

    private ContainerDatas containerDatas;

    public void addContainer(ContainerData container) {
        containers.add(container);
    }
}
