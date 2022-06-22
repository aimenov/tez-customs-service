package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "NE_SMGS_DECLARANT_INFO", schema = "KTZ")
@NamedQuery(name = "getSmgsDeclarantInfoByInvoiceId",
                query = "SELECT n FROM NeSmgsDeclarantInfo n WHERE n.invUn = :invcUn")
public class NeSmgsDeclarantInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_DECLARANT_INFO_UN")
    private Long smgsDeclarantInfoUn;

    @Column(name = "INV_UN")
    private long invUn;

    @Column(name = "DECLARANT_NAME")
    private String declarantName; // Наименование/ФИО

    @Column(name = "DECLARANT_SHORT_NAME")
    private String declarantShortName; // Краткое наименование

    @Column(name = "DECLARANT_COUNTRY")
    private String declarantCountry; // Страна

    @Column(name = "DECLARANT_INDEX")
    private String declarantIndex; // Почтовый индекс

    @Column(name = "DECLARANT_CITY")
    private String declarantCity; // Населеннывй пункт

    @Column(name = "DECLARANT_REGION")
    private String declarantRegion; // Область (регион, штат и т.п.)

    @Column(name = "DECLARANT_ADDRESS")
    private String declarantAddress; // улица, номер дома, номер офиса

    @Column(name = "DECLARANT_KZ_BIN")
    private String declarantKZBin; // БИН для РК

    @Column(name = "DECLARANT_KZ_IIN")
    private String declarantKZIin; // ИИН для РК

    @Column(name = "DECLARANT_KZ_PERSONS_CATEGORY")
    private String declarantKZPersonsCategory; // Категория лица для РК

    @Column(name = "DECLARANT_KZ_KATO")
    private String declarantKZKATO; // КАТО для РК

    @Column(name = "DECLARANT_KZ_ITN")
    private String declarantKZITN; // ИТН резерва для РК

    @Column(name = "DECLARANT_RU_OGRN")
    private String declarantRUOGRN; // ОГРН/ОГРНИП для РФ

    @Column(name = "DECLARANT_RU_KPP")
    private String declarantRUKPP; // КПП для РФ

    @Column(name = "DECLARANT_RU_INN")
    private String declarantRUINN; // ИНН для РФ

    @Column(name = "DECLARANT_BY_UNP")
    private String declarantBYUNP; // УНП для Беларуси

    @Column(name = "DECLARANT_BY_IN")
    private String declarantBYIN; // Идентификационный номер физ.лица для Беларуси

    @Column(name = "DECLARANT_AM_UNN")
    private String declarantAMUNN; // УНН для Армении

    @Column(name = "DECLARANT_AM_NZOU")
    private String declarantAMNZOU; // НЗОУ для Армении

    @Column(name = "DECLARANT_KG_INN")
    private String declarantKGINN; // ИНН для Кыргызстана

    @Column(name = "DECLARANT_KG_OKPO")
    private String declarantKGOKPO; // ОКПО для Кыргызстана

    private static final long serialVersionUID = 1L;

    public NeSmgsDeclarantInfo() {
        super();
    }

}
