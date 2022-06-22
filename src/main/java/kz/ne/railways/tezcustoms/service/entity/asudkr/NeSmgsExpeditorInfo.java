package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "NE_SMGS_EXPEDITOR_INFO", schema = "KTZ")
@NamedQuery(name = "getSmgsExpeditorInfoByInvoiceId",
                query = "SELECT n FROM NeSmgsExpeditorInfo n WHERE n.invUn = :invcUn")
public class NeSmgsExpeditorInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_EXPEDITOR_INFO_UN")
    private Long smgsExpeditorInfoUn;

    @Column(name = "INV_UN")
    private long invUn;

    @Column(name = "EXPEDITOR_NAME")
    private String expeditorName; // Наименование/ФИО

    @Column(name = "EXPEDITOR_SHORT_NAME")
    private String expeditorShortName; // Краткое наименование

    @Column(name = "EXPEDITOR_COUNTRY")
    private String expeditorCountry; // Страна

    @Column(name = "EXPEDITOR_INDEX")
    private String expeditorIndex; // Почтовый индекс

    @Column(name = "EXPEDITOR_CITY")
    private String expeditorCity; // Населеннывй пункт

    @Column(name = "EXPEDITOR_REGION")
    private String expeditorRegion; // Область (регион, штат и т.п.)

    @Column(name = "EXPEDITOR_ADDRESS")
    private String expeditorAddress; // улица, номер дома, номер офиса

    @Column(name = "EXPEDITOR_KZ_BIN")
    private String expeditorKZBin; // БИН для РК

    @Column(name = "EXPEDITOR_KZ_IIN")
    private String expeditorKZIin; // ИИН для РК

    @Column(name = "EXPEDITOR_KZ_PERSONS_CATEGORY")
    private String expeditorKZPersonsCategory; // Категория лица для РК

    @Column(name = "EXPEDITOR_KZ_KATO")
    private String expeditorKZKATO; // КАТО для РК

    @Column(name = "EXPEDITOR_KZ_ITN")
    private String expeditorKZITN; // ИТН резерва для РК

    @Column(name = "EXPEDITOR_RU_OGRN")
    private String expeditorRUOGRN; // ОГРН/ОГРНИП для РФ

    @Column(name = "EXPEDITOR_RU_KPP")
    private String expeditorRUKPP; // КПП для РФ

    @Column(name = "EXPEDITOR_RU_INN")
    private String expeditorRUINN; // ИНН для РФ

    @Column(name = "EXPEDITOR_BY_UNP")
    private String expeditorBYUNP; // УНП для Беларуси

    @Column(name = "EXPEDITOR_BY_IN")
    private String expeditorBYIN; // Идентификационный номер физ.лица для Беларуси

    @Column(name = "EXPEDITOR_AM_UNN")
    private String expeditorAMUNN; // УНН для Армении

    @Column(name = "EXPEDITOR_AM_NZOU")
    private String expeditorAMNZOU; // НЗОУ для Армении

    @Column(name = "EXPEDITOR_KG_INN")
    private String expeditorKGINN; // ИНН для Кыргызстана

    @Column(name = "EXPEDITOR_KG_OKPO")
    private String expeditorKGOKPO; // ОКПО для Кыргызстана

    private static final long serialVersionUID = 1L;

    public NeSmgsExpeditorInfo() {
        super();
    }

}
