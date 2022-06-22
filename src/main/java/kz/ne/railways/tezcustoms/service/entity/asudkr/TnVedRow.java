package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
public class TnVedRow {
    @Id
    private Long id;
    private Long invoiceUn;
    private String tnVedCode;
    private String description;
    private String descriptionAdditionaly;
    private String tnVedName;
    private BigDecimal countByUnit;
    private Long unitTypeUn;
    private String unitTypeName;
    private BigDecimal placeCargoCount;
    private BigDecimal packingCount;
    private BigDecimal pakagePartQuantity;
    private String packingCode;
    private Long pakingTypeUn;
    private String packingTypeName;
    private String placeCargoMark;
    private BigDecimal netto;
    private BigDecimal brutto;
    private BigDecimal priceByOne;
    private BigDecimal priceByTotal;
    private Long currencyUn;
    private String currencyCode;
    private String currencyName;
    private String container;
    private String documents;
    private String vagonAccessoryCouNo;
    private String tnVedCountry;
    private Short tnVedIsArmy;

}
