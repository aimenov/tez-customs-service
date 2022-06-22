package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "NE_CONTAINER_LISTS", schema = "KTZ")
public class NeContainerLists implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTAINER_LISTS_UN")
    private Long containerListsUn;

    @Column(name = "CONTAINER_NO")
    private String containerNo;

    @Column(name = "CONTAINER_MARK")
    private String containerMark;

    @Column(name = "CONTAINER_TYPE_UN")
    private Long containerTypeUn;

    @Column(name = "NPP")
    private Integer npp;

    @Column(name = "INVOICE_UN")
    private Long invoiceUn;

    @Column(name = "VAGON_LIST_UN")
    private Long vagonListUn;

    @Column(name = "VAG_GROUP_UN")
    private Long vagGroupUn;

    @Column(name = "MASS_FACT")
    private BigDecimal massFact;

    @Column(name = "MASS_TARA")
    private BigDecimal massTara;

    @Column(name = "GP")
    private BigDecimal gp;

    @Column(name = "MANAG_UN")
    private Long managUn;

    @Column(name = "PREV_ETSNG_CODE")
    private String prevEtsngCode;

    @Column(name = "C_OWNER_CODE")
    private Integer cOwnerCode;

    @Column(name = "C_OWNER_TYPE")
    private Short cOwnerType;

    @Column(name = "C_OWNER_NAME")
    private String cOwnerName;

    @Column(name = "LOCK_ARM_TYPE_UN")
    private Long lockArmTypeUn;

    @Column(name = "LOCK_ARM_COUNT")
    private Integer lockArmCount;

    @Column(name = "LOCK_ARM_MARKS")
    private String lockArmMarks;

    @Column(name = "LOCK_OWN")
    private Integer lockOwn;

    @Column(name = "SPEC_CONTAINER")
    private short specContainer;

    @Column(name = "FILLED_CONTAINER")
    private Integer filledContainer;

    @Column(name = "CON_UN")
    private String conUn;

}
