package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "NE_INVOICE_PREV_INFO", schema = "KTZ")
public class NeInvoicePrevInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PREV_INFO_UN")
    private Long prevInfoUn;

    @Column(name = "INVOICE_UN")
    private Long invoiceUn;

    @Column(name = "PREV_INFO_TYPE", length = 1)
    private String prevInfoType; // AppoPrInf - js name

    @Column(name = "ARRIVE_DATETIME")
    private Timestamp arriveTime;

    @Column(name = "ARRIVE_STA_NO", length = 6)
    private String arriveStaNo;

    @Column(name = "CUSTOM_ORG_UN")
    private Long customOrgUn;

    @Column(name = "CUSTOM_CODE", length = 10)
    private String customCode;

    @Column(name = "CUSTOM_NAME", length = 250)
    private String customName;

    @Column(name = "USER_UN")
    private Long userUn;

    @Column(name = "CREATE_DATETIME")
    private Timestamp createDatetime;

    @Column(name = "RESPONSE_DATETIME")
    private Timestamp responseDatetime;

    @Column(name = "PREV_INFO_STATUS")
    private int prevInfoStatus = 0;

    @Column(name = "RESPONSE_TEXT")
    private String responseText;

    @Column(name = "RESPONSE_UUID")
    private String responseUUID;

    @Column(name = "START_STA_COU_NO")
    private String startStaCouNo;

    @Column(name = "DEST_STATION_COU_NO")
    private String destStationCouNo;

    @Column(name = "PREV_INFO_FEATURES")
    private Integer prevInfoFeatures;

    @Column(name = "TRANZIT_SEND_DATETIME")
    private Timestamp tranzitSendDatetime;

}
