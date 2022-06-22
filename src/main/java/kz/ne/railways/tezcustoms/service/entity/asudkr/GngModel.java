package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class GngModel {
    @Id
    private int id;
    private String invoiceUn;
    private String code;
    private String shortName1;

}
