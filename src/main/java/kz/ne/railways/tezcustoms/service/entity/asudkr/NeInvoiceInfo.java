package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "NE_INVOICE_INFO")
@NamedQuery(name = "getNeInvoiceInfoByInvoiceId",
                query = "SELECT n FROM NeInvoiceInfo n WHERE n.invoiceUn = :invoiceUn")
public class NeInvoiceInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVOICE_INFO_UN")
    private Long invoiceInfoUn;

    @Column(name = "INVOICE_UN")
    private long invoiceUn;

    @Column(name = "TRAFFIC_BY_ROUTES")
    private String trafficByRoutes;

    @Column(name = "IS_DIVIDE")
    private Short isDivide;

    @Column(name = "DIFFUSE_STATION_ST_UN")
    private Long diffuseStationStUn;

    @Column(name = "REQ_TYPE_TRAF_UN")
    private Long reqTypeTrafUn;

    @Column(name = "SHIP_KIND_UN")
    private Long shipKindUn;

    @Column(name = "REQ_PLAN")
    private String reqPlan;

    @Column(name = "GU12_PLAN_NR")
    private String gu12PlanNr;

    @Column(name = "GU11_PLAN_NR")
    private String gu11PlanNr;

    @Column(name = "PROPERTY_UN")
    private Long propertyUn;

    private static final long serialVersionUID = 1L;

    public NeInvoiceInfo() {
        super();
    }

}
