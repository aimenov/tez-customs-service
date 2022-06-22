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
@Table(name = "NE_VESSEL", schema = "NSI")
public class NeVessel implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1721762568269063141L;

    @Id
    @Column(name = "NE_VESSEL_UN")
    private Long neVesselUn;


    @Column(name = "VESSEL_NAME")
    private String vesselName;


    @Column(name = "VESSEL_BGN")
    private Timestamp vesselBgn;

    @Column(name = "VESSEL_END")
    private Timestamp vesselEnd;

}
