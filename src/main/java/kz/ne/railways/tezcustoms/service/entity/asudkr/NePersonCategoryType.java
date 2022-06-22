package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(schema = "NSI", name = "NE_PERSON_CATEGORY_TYPE")
public class NePersonCategoryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_TYPE_UN")
    private Long categoryTypeUn;

    @Column(name = "CATEGORY_CODE", length = 25)
    private String categoryCode;

    @Column(name = "CATEGORY_NAME", length = 512)
    private String categoryName;

    @Column(name = "CATEGORY_BGN")
    private Timestamp categoryBgn;

    @Column(name = "CATEGORY_END")
    private Timestamp categoryEnd;

}
