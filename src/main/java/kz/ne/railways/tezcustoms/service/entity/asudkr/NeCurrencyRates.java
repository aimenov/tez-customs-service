package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "NE_CURRENCY_RATES", schema = "NSI")
public class NeCurrencyRates {

    @Id
    @Column(name = "CURRENCY_RATE_UN")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long currencyRateUn;

    @Column(name = "CURRENCU_CODE_UN")
    private Long currencuCodeUn;

    @Column(name = "RATE")
    private Double rate;

    @Column(name = "RATE_BGN")
    private Timestamp rateBgn;

    @Column(name = "RATE_END")
    private Timestamp rateEnd;

}
