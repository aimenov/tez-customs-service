package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(schema = "NSI", name = "NE_UNIT_TYPE")
public class NeUnitType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UNIT_TYPE_UN")
    private Long unitType;

    @Column(name = "UNIT_CODE", length = 3)
    private String unitCode;

    @Column(name = "UNIT_NAME", length = 512)
    private String unitName;

    @Column(name = "UNIT_DESCRIPTION", length = 1024)
    private String unitDescription;

    @Column(name = "UNIT_BGN")
    private Timestamp unitBgn;

    @Column(name = "UNIT_END")
    private Timestamp unitEnd;

}
