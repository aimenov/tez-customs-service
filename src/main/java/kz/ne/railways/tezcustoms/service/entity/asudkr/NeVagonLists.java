package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(schema = "KTZ", name = "NE_VAGON_LISTS")
public class NeVagonLists implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VAG_LISTS_UN")
    private Long vagListsUn;

    @Column(name = "VAG_NO")
    private String vagNo;

    @Column(name = "INVC_UN")
    private Long invcUn;

    // @Column(name="INVC_NUM")
    // private Long invcNumPP;
    //
    // @Column(name="VPODACH_UN")
    // private Long vPodachUn;
    //
    // @Column(name="ROD_VAG_UCH")
    // private Integer rodVagUch;
    //
    // @Column(name="TYPE_VAG_UCH")
    // private Integer typeVagUch;
    //
    // @Column(name="GRP_VAG_CEN")
    // private Double grpVagCen;

    @Column(name = "VAG_GROUP_UN")
    private Long vagGroupUn;

    /*
     * @Column(name="VAG_SIGN") private Integer vagSign;
     * 
     * @Column(name="LOCK_ARM_COUNT") private Integer lockCount;
     * 
     * @Column(name="LOCK_ARM_TYPE_UN") private Long lockArmTypeUn;
     * 
     * @Column(name="LOCK_ARM_MARKS") private String lockArmMarks;
     */

    @Column(name = "OWNER_RAILWAYS")
    private String ownerRailways;

    /*
     * @Column(name="PROVIDED_BY") private String providedBy;
     * 
     * @Column(name="OWNER_NAME") private String ownerName;
     * 
     * @Column(name="AXIS") private Integer axis;
     * 
     * @Column(name="SPEC_NOTE_1") private Integer specNote1;
     * 
     * @Column(name="SPEC_NOTE_2") private Integer specNote2;
     * 
     * @Column(name="SPEC_NOTE_3") private Integer specNote3;
     * 
     * @Column(name="OVERSIZE_INDEX") private String oversizeIndex;
     * 
     * @Column(name="NOTE_V_LIST") private String noteVagonList;
     * 
     * @Column(name="MASS_TARA") private BigDecimal massTara;
     * 
     * @Column(name="LOCK_OWN") private Integer lockOwn;
     * 
     * @Column(name="V_OWNER_CODE") private Integer vOwnerCode;
     * 
     * @Column(name="V_OWNER_TYPE") private Short vOwnerType;
     */
    /*
     * public String getNoteVagonList() { return noteVagonList; }
     * 
     * public void setNoteVagonList(String noteVagonList) { this.noteVagonList = noteVagonList; }
     */
    public NeVagonLists() {
        super();
    }



    /*
     * public String getOversizeIndex() { return oversizeIndex; }
     * 
     * 
     * 
     * public void setOversizeIndex(String oversizeIndex) { this.oversizeIndex = oversizeIndex; }
     * 
     * 
     * 
     * public Integer getSpecNote1() { return specNote1; }
     * 
     * 
     * 
     * public void setSpecNote1(Integer specNote1) { this.specNote1 = specNote1; }
     * 
     * 
     * 
     * public Integer getSpecNote2() { return specNote2; }
     * 
     * 
     * 
     * public void setSpecNote2(Integer specNote2) { this.specNote2 = specNote2; }
     * 
     * 
     * 
     * public Integer getSpecNote3() { return specNote3; }
     * 
     * 
     * 
     * public void setSpecNote3(Integer specNote3) { this.specNote3 = specNote3; }
     */

    /*
     * public Long getVPodachUn() { return this.vPodachUn; }
     * 
     * public void setVPodachUn(Long vPodachUn) { this.vPodachUn = vPodachUn; }
     * 
     * public Integer getRodVagUch() { return this.rodVagUch; }
     * 
     * public void setRodVagUch(Integer rodVagUch) { this.rodVagUch = rodVagUch; }
     * 
     * public void setGrpVagCen(Double grpVagCen) { this.grpVagCen = grpVagCen; }
     * 
     * public Double getGrpVagCen() { return grpVagCen; }
     */

    /*
     * public void setVagSign(Integer vagSign) { this.vagSign = vagSign; }
     * 
     * public Integer getVagSign() { return vagSign; }
     * 
     * public void setTypeVagUch(Integer typeVagUch) { this.typeVagUch = typeVagUch; }
     * 
     * public Integer getTypeVagUch() { return typeVagUch; }
     * 
     * public Integer getLockCount() { return lockCount; }
     * 
     * public void setLockCount(Integer lockCount) { this.lockCount = lockCount; }
     * 
     * public Long getLockArmTypeUn() { return lockArmTypeUn; }
     * 
     * public void setLockArmTypeUn(Long lockArmTypeUn) { this.lockArmTypeUn = lockArmTypeUn; }
     * 
     * public String getLockArmMarks() { return lockArmMarks; }
     * 
     * public void setLockArmMarks(String lockArmMarks) { this.lockArmMarks = lockArmMarks; }
     */

    /*
     * public String getProvidedBy() { return providedBy; }
     * 
     * public void setProvidedBy(String providedBy) { this.providedBy = providedBy; }
     * 
     * public String getOwnerName() { return ownerName; }
     * 
     * public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
     * 
     * public Integer getAxis() { return axis; }
     * 
     * public void setAxis(Integer axis) { this.axis = axis; }
     * 
     * public BigDecimal getMassTara() { return massTara; }
     * 
     * public void setMassTara(BigDecimal massTara) { this.massTara = massTara; }
     * 
     * public void setLockOwn(Integer lockOwn) { this.lockOwn = lockOwn; }
     * 
     * 
     * 
     * public Integer getLockOwn() { return lockOwn; }
     * 
     * 
     * 
     * public void setInvcNumPP(Long invcNum) { this.invcNumPP = invcNum; }
     * 
     * public Long getInvcNumPP() { return invcNumPP; }
     * 
     * 
     * 
     * public Integer getVOwnerCode() { return vOwnerCode; }
     * 
     * 
     * 
     * public void setVOwnerCode(Integer ownerCode) { vOwnerCode = ownerCode; }
     * 
     * 
     * 
     * public Short getVOwnerType() { return vOwnerType; }
     * 
     * 
     * 
     * public void setVOwnerType(Short ownerType) { vOwnerType = ownerType; }
     */
}
