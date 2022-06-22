package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "NE_SMGS_RECIEVER_INFO", schema = "KTZ")
@NamedQuery(name = "getSmgsRecieverInfoByInvoiceId",
                query = "SELECT n FROM NeSmgsRecieverInfo n WHERE n.invUn = :invcUn")
public class NeSmgsRecieverInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_RECIEVER_INFO_UN")
    private Long smgsRecieverInfoUn;

    @Column(name = "INV_UN")
    private long invUn;

    @Column(name = "RECIEVER_CODE")
    private String recieverCode;

    @Column(name = "RECIEVER_TYPE")
    private String recieverType;

    @Column(name = "RECIEVER_NAME")
    private String recieverName;

    @Column(name = "RECIEVER_FULL_NAME")
    private String recieverFullName;

    @Column(name = "RECIEVER_STREET")
    private String recieverStreet;

    @Column(name = "RECIEVER_BUILDING")
    private String recieverBuilding;

    @Column(name = "RECIEVER_SITY")
    private String recieverSity;

    @Column(name = "RECIEVER_REGION", length = 200)
    private String recieverRegion;

    @Column(name = "RECIEVER_POST_INDEX")
    private String recieverPostIndex;

    @Column(name = "RECIEVER_COUNTRY_CODE")
    private String recieverCountryCode;

    @Column(name = "RECIEVER_SIGNATURE")
    private String recieverSignature;

    @Column(name = "RECIEVER_TELEPHONE_NUM")
    private String recieverTelephoneNum;

    @Column(name = "RECIEVER_TELEFAX_NUM")
    private String recieverTelefaxNum;

    @Column(name = "RECIEVER_EMAIL")
    private String recieverEmail;

    @Column(name = "RECIEVER_RAILWAY_NUM")
    private String railwayNum;

    @Column(name = "RECIEVER_OKPO")
    private String recieverOkpo;

    @Column(name = "RECIEVER_BIN", length = 50)
    private String recieverBin;

    @Column(name = "RECIEVER_IIN", length = 50)
    private String recieverIin;

    @Column(name = "CATEGORY_TYPE")
    private Long categoryType;

    @Column(name = "KATO_TYPE")
    private Long katoType;

    @Column(name = "ITN", length = 25)
    private String itn;

    @Column(name = "KPP", length = 25)
    private String kpp;

    private static final long serialVersionUID = 1L;

    public NeSmgsRecieverInfo() {
        super();
    }

}
