package kz.ne.railways.tezcustoms.service.entity.asudkr;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(schema = "KTZ", name = "NE_SMGS_TN_VED")
public class NeSmgsTnVed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_TN_VED_UN")
    private Long smgsTnVedUn;

    @Column(name = "INVOICE_UN")
    private Long invoiceUn;

    @Column(name = "TN_VED_CODE", length = 25)
    private String tnVedCode;

    @Column(name = "PRODUCT_DESCRIPTION", length = 500)
    private String productDescription;

    @Column(name = "PRODUCT_DESCRIPTION_ADD", length = 500)
    private String productDescriptionAdd;

    @Column(name = "COUNT_BY_UNIT")
    private String countByUnit;

    @Column(name = "UNIT_NAME")
    private String unitName;

    @Column(name = "PLACE_CARGO_COUNT")
    private String placeCargoCount;

    // TODO: REMOVE
    @Column(name = "PACKING_COUNT")
    private String packingCount;

    @Column(name = "PAKAGE_PART_QUANTITY")
    private String packagePartQuantity;

    @Column(name = "PACKING_NAME")
    private Long packingName;

    @Column(name = "PLACE_CARGO_MARK", length = 512)
    private String placeCargoMark;

    @Column(name = "NETTO_WEIGHT")
    private String nettoWeight;

    @Column(name = "BRUTTO_WEIGHT")
    private String bruttoWeight;

    @Column(name = "PRICE_BY_ONE")
    private String priceByOne;

    @Column(name = "PRICE_BY_FULL")
    private String priceByFull;

    @Column(name = "CURRENCY_CODE_UN")
    private String currencyCodeUn;

    @Column(name = "CONTAINER", length = 2048)
    private String container;

    @Column(name = "TN_VED_NAME", length = 500)
    private String tnVedName;

    @Column(name = "VAGON_ACCESSORY_COU_NO")
    private String vagonAccessoryCouNo;

    @Column(name = "TN_VED_COUNTRY")
    private String tnVedCountry;

    @Column(name = "TN_VED_IS_ARMY")
    private String tnVedIsArmy;

    @Column(name = "TN_VED_DESCRIPTION", length = 1000)
    private String tnVedDescription;

}
