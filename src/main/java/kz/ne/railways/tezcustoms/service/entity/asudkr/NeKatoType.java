package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(schema = "NSI", name = "NE_KATO_TYPE")
public class NeKatoType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KATO_UN")
    private Long katoUn;

    @Column(name = "KATO_CODE", length = 25)
    private String katoCode;

    @Column(name = "KATO_NAME", length = 512)
    private String katoName;

    @Column(name = "KATO_BGN")
    private Timestamp katoBgn;

    @Column(name = "KATO_END")
    private Timestamp katoEnd;

}
