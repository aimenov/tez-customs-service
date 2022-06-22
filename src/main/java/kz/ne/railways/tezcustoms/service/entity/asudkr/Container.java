package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "CONTAINER", schema = "NSI")
public class Container implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2394083416378552861L;

    @Id
    @Column(name = "CON_UN")
    private Long conUn;

    @Column(name = "CON_ID")
    private Integer conId;

    @Column(name = "CONGROUP_UN")
    private Long conGroupUn;

    @Column(name = "CON_CODE")
    public String conCode;

    @Column(name = "CON_SIZE")
    private String conSize;

    @Column(name = "CON_CHARACTER")
    private String conCharacter;

    @Column(name = "CON_LENGTH")
    private Integer conLength;

    @Column(name = "CON_HEIGHT")
    private Integer conHeight;

    @Column(name = "CON_WEIGHT")
    private Double conWeight;

    @Column(name = "CON_MAX_WEIGHT")
    private Double conMaxWeight;

    @Column(name = "CON_BGN")
    private Timestamp conBgn;

    @Column(name = "CON_END")
    private Timestamp conEnd;

}
