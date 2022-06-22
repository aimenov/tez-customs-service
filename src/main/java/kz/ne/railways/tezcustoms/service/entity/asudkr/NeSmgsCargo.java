package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "NE_SMGS_CARGO", schema = "KTZ")
@NamedQuery(name = "getSmgsCargoByInvoiceId", query = "SELECT n FROM NeSmgsCargo n WHERE n.invUn = :invcUn")
public class NeSmgsCargo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_CARGO_UN")
    private Long smgsCargoUn;

    @Column(name = "INV_UN")
    private long invUn;

    @Column(name = "PLACE_NUM")
    private Double placeNum;

    @Column(name = "PACKING_CODE")
    private String packingCode;

    @Column(name = "SENDER_COUNTRY")
    private String senderCountry;

    @Column(name = "DEST_COUNTRY")
    private String destCountry;

    @Column(name = "ETSNG_CODE")
    private String etsngCode;

    @Column(name = "GNG_CODE")
    private String gngCode;

    private String conductor;

    @Column(name = "MASS_SPOS_CODE")
    private String massSposCode;

    @Column(name = "MASS_SPOS_NAME")
    private String massSposName;

    @Column(name = "MASS_BRUTTO")
    private BigDecimal massBrutto;


    @Column(name = "FIXED_BY_SECTION")
    private String fixedBySection;

    @Column(name = "SENDER_CARGO_STATMENT")
    private String senderCargoStatment;

    @Column(name = "FIXED_BY_DOC")
    private Integer fixedByDoc;

    @Column(name = "FIXED_BY_CHAPTER")
    private String fixedByChapter;

    @Column(name = "FIXED_BY_PARAGRAPH")
    private String fixedByParagraph;


    private static final long serialVersionUID = 1L;

    public NeSmgsCargo() {
        super();
    }

}
