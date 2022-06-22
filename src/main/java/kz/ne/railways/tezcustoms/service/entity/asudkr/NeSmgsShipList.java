package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "NE_SMGS_SHIP_LIST", schema = "KTZ")
public class NeSmgsShipList implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NE_SMGS_SHIP_LIST_UN")
    private Long neSmgsShipListUn;

    @Column(name = "INV_UN")
    private long invUn;

    @Column(name = "NE_VESSEL_UN")
    private Long neVesselUn;



    private static final long serialVersionUID = 1L;

    public NeSmgsShipList() {
        super();
    }

}
