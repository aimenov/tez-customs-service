package kz.ne.railways.tezcustoms.service.model;

import com.sun.xml.bind.v2.TODO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Contract {

    private String user;
    private Date creationDate;
    private String invoiceId;
    private String invoiceNum;

    private String departStation;
    private String destStation;
    private String arrivalStation;

    private String codeUINP;
    private String declarant;
    private Status status;
    private Detail detail;

    public Contract() {}
}
