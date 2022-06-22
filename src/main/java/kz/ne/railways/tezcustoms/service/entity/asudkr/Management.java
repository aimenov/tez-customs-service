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
@Table(name = "MANAGEMENT", schema = "NSI")
public class Management implements Serializable {
    @Id
    @Column(name = "MANAG_UN")
    private long managUn;

    @Column(name = "MANAG_ID")
    private int managId;

    @Column(name = "MANAG_NO")
    private short managNo;

    @Column(name = "COU_UN")
    private long couUn;

    @Column(name = "MANAG_NAME")
    private String managName;

    @Column(name = "M_NAME_RUS")
    private String mNameRus;

    @Column(name = "M_NAME_LAT")
    private String mNameLat;

    @Column(name = "MANAG_BGN")
    private Timestamp managBgn;

    @Column(name = "MANAG_END")
    private Timestamp managEnd;

    @Column(name = "SHORT_NAME")
    private String shortName;

    private static final long serialVersionUID = 1L;

    public Management() {
        super();
    }

}
