package kz.ne.railways.tezcustoms.service.entity.asudkr;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "NE_INVC_REF_PI", schema = "KTZ")
public class NeInvcRefPi implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REF_PI_UN")
    private Long id;

    @Column(name = "INVC_UN")
    private Long invcUn;

    @Column(name = "PREV_INFO_INVC_UN")
    private Long prevInvcUn;

    @Column(name = "ADDED_ON")
    private Timestamp addedOn;

}
