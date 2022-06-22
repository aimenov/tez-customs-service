package kz.ne.railways.tezcustoms.service.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataCaneVagInfo {
    private String VagNo;
    private String VagNoDC;
    private Integer propertyDC;
    private Integer OwnerCodeDC;
    private Integer depoDC;
    private String OwnerNameDC;
    private Integer typeVagDC;
    private Integer taraDC;
    private Integer railwayNoDC;
    private Integer gpDC;
    private Integer axisCount;
    private BigDecimal kodArMg;
    private String YellowMileage;
    private String RedMileage;
}
