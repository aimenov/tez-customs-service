package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(schema = "NSI", name = "NE_CUSTOMS_ORGS")
public class NeCustomsOrgs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMS_ORG_UN")
    private Long customsOrgUn;
    @Column(name = "CUSTOM_CODE", length = 10)
    private String customCode;
    @Column(name = "CUSTOM_NAME", length = 1024)
    private String customName;
    @Column(name = "CUSTOM_ORG_BGN")
    private Timestamp customOrgBgn;
    @Column(name = "CUSTOM_ORG_END")
    private Timestamp customOrgEnd;
    @Column(name = "COUNTRY_NO")
    private String countryNo;

}
