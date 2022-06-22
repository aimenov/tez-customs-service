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
@Table(name = "STA", schema = "NSI")
public class Sta implements Serializable {
    @Id
    @Column(name = "ST_UN")
    private Long stUn;

    @Column(name = "ST_ID")
    private int stId;

    @Column(name = "STA_NO")
    private String staNo;

    @Column(name = "STA_NAME")
    private String staName;

    @Column(name = "DIV_UN")
    private Long divUn;

    @Column(name = "ROAD_UN")
    private Long roadUn;

    @Column(name = "MANAG_UN")
    private Long managUn;

    @Column(name = "PR_UN")
    private Long prUn;

    @Column(name = "TEHPD_UN")
    private Long tehpdUn;

    private Short sign;

    private String paragraph;

    @Column(name = "STA_EMAIL")
    private String staEmail;

    @Column(name = "ST_BGN")
    private Timestamp stBgn;

    @Column(name = "ST_END")
    private Timestamp stEnd;

    private static final Long serialVersionUID = 1L;

    public Sta() {
        super();
    }

}
