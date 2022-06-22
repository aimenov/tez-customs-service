package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "NE_SMGS_SENDER_INFO", schema = "KTZ")
@NamedQuery(name = "getSmgsSenderInfoByInvoiceId", query = "SELECT n FROM NeSmgsSenderInfo n WHERE n.invUn = :invcUn")
public class NeSmgsSenderInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_SENDER_INFO_UN")
    private Long smgsSenderInfoUn;

    @Column(name = "INV_UN")
    private long invUn;

    @Column(name = "SENDER_CODE")
    private String senderCode;

    @Column(name = "SENDER_TYPE")
    private String senderType;

    @Column(name = "SENDER_NAME")
    private String senderName;

    @Column(name = "SENDER_FULL_NAME")
    private String senderFullName;

    @Column(name = "SENDER_STREET")
    private String senderStreet;

    @Column(name = "SENDER_BUILDING")
    private String senderBuilding;

    @Column(name = "SENDER_SITY")
    private String senderSity;

    @Column(name = "SENDER_REGION", length = 200)
    private String senderRegion;

    @Column(name = "SENDER_POST_INDEX")
    private String senderPostIndex;

    @Column(name = "SENDER_COUNTRY_CODE")
    private String senderCountryCode;

    @Column(name = "SENDER_SIGNATURE")
    private String senderSignature;

    @Column(name = "SENDER_TELEPHONE_NUM")
    private String senderTelephoneNum;

    @Column(name = "SENDER_TELEFAX_NUM")
    private String senderTelefaxNum;

    @Column(name = "SENDER_EMAIL")
    private String senderEmail;

    @Column(name = "SENDER_OKPO")
    private String senderOkpo;

    @Column(name = "SENDER_BIN", length = 50)
    private String senderBin;

    @Column(name = "SENDER_IIN", length = 50)
    private String senderIin;

    @Column(name = "CATEGORY_TYPE")
    private Long categoryType;

    @Column(name = "KATO_TYPE")
    private Long katoType;

    @Column(name = "ITN", length = 25)
    private String itn;

    @Column(name = "KPP", length = 25)
    private String kpp;


    private static final long serialVersionUID = 1L;

    public NeSmgsSenderInfo() {
        super();
    }

}
