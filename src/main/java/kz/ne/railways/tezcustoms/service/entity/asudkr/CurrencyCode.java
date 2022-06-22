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
@Table(name = "CURRENCY_CODE", schema = "NSI")
public class CurrencyCode implements Serializable {
    @Id
    @Column(name = "CUR_CODE_UN")
    private long curCodeUn;

    @Column(name = "CUR_CODE_ID")
    private int curCodeId;

    @Column(name = "CUR_CODE_NAME")
    private String curCodeName;

    @Column(name = "CUR_CODE")
    private String curCode;

    @Column(name = "CUR_CODE_LET")
    private String curCodeLet;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CUR_CODE_BGN")
    private Timestamp curCodeBgn;

    @Column(name = "CUR_CODE_END")
    private Timestamp curCodeEnd;

    private static final long serialVersionUID = 1L;

    public CurrencyCode() {
        super();
    }

}
