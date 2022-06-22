package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "NE_INVOICE", schema = "KTZ")
public class NeInvoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVC_UN")
    private Long invcUn;

    @Column(name = "INVC_NUM")
    private String invcNum;

    @Column(name = "INVC_DT")
    private Timestamp invcDt;

    @Column(name = "INVC_SPEED_UN")
    private Long invcSpeedUn;

    @Column(name = "INVC_MASS_UN")
    private Long invcMassUn;

    @Column(name = "INVC_LOADING_BY_MEANS")
    private Long invcLoadingByMeansUn;

    @Column(name = "INVC_STATUS")
    private int invcStatus;

    @Column(name = "INV_DATE")
    private Date invDate;

    @Column(name = "MGS_ID")
    private String mgsId;

    @Column(name = "OTPR_NO")
    private Long otprNo;

    @Column(name = "SMGS_TYPE")
    private Integer smgsType;

    @Column(name = "DATE_PRIEM")
    private Timestamp datePriem;

    @Column(name = "LOADED_BY")
    private Integer loadedBy;

    @Column(name = "CARGO_WORTH")
    private BigDecimal cargoWorth;

    @Column(name = "CARGO_WORTH_CURR")
    private String cargoWorthCurr;

    @Column(name = "SHIPPING_STATUS")
    private String shippingStatus;

    @Column(name = "ADDITION_INFO")
    private String additionInfo;

    @Column(name = "ADDITION_INFO_ROAD")
    private String additionInfoRoad;

    @Column(name = "INFO_NOT_FOR_ROAD")
    private String infoNotForRoad;

    @Column(name = "SENDER_STATMENT")
    private String senderStatment;

    @Column(name = "TARIF_NOTES")
    private String tarifNotes;

    @Column(name = "CARRIER_NOTES")
    private String carrierNotes;

    @Column(name = "NOT_PAPER_SHIPPING")
    private Short notPaperShipping;

    @Column(name = "CONTRACT_NUM")
    private String contractNum;

    @Column(name = "RECIVE_STATION_CODE")
    private String reciveStationCode;

    @Column(name = "DEST_STATION_CODE")
    private String destStationCode;

    @Column(name = "FOREGN_BORDER_POINT")
    private String foregnBorderPoint;

    @Column(name = "BORDER_POINT_INBOUND")
    private String borderPointInbound;

    @Column(name = "BORDER_POINT_OUTBOUND")
    private String borderPointOutbound;

    @Column(name = "IS_PRIVATE_ROAD")
    private Integer isPrivateRoad;

    @Column(name = "INVOICE_DATETIME")
    private Timestamp invoiceDatetime;

    @Column(name = "MESSAGE_STATUS")
    private Integer messageStatus;

    @Column(name = "PARENT_INVC_UN")
    private Long parentInvcUn;

    @Column(name = "PERIOD_OF_DELIVERY")
    private BigDecimal periodOfDelivery;

    @Column(name = "DISTANCE")
    private BigDecimal distance;

    @Column(name = "INVOICE_RASKREDITATION_DT")
    private Timestamp invoiceRaskreditationDt;

    @Column(name = "UNLOAD_DT")
    private Timestamp invUnloadDt;

    @Column(name = "TRAIN_INDEX", length = 16)
    private String trainIndex;

    @Column(name = "IS_CONTAINER")
    private byte isContainer;

    @Column(name = "DOC_TYPE")
    private int docType;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "UINP_CODE", length = 20)
    private String uinpCode;

    @Column(name = "INVOICE_STATUS")
    private int invoiceStatus;

    private static final Long serialVersionUID = 1L;

    public NeInvoice() {
        super();
    }

}
