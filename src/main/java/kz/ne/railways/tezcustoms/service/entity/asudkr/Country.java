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
@Table(name = "COUNTRY", schema = "NSI")
public class Country implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1721762568269063141L;

    @Id
    @Column(name = "COU_UN")
    private Long couUn;

    @Column(name = "COU_ID")
    private int couId;

    @Column(name = "COUNTRY_NO")
    private String countryNo;

    @Column(name = "REGION_UN")
    private int regionUn;

    @Column(name = "COUNTRY_ID")
    private String countryId;

    @Column(name = "COUNTRY_NAME")
    private String countryName;

    @Column(name = "COUNTRY_FULLNAME")
    private String countryFullName;

    @Column(name = "COUNTRY_ID3")
    private String countryId3;

    @Column(name = "COU_BGN")
    private Timestamp couBegin;

    @Column(name = "COU_END")
    private Timestamp couEnd;

}
