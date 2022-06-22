package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class SearchPIDataModel implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    private Date createdDate;
    private String author;
    private String trainIndex;
    private String invoiceNum;
    private String codeGood;
    private String nameGood;
    private String startStation;
    private String destStation;
    private String sender;
    private String receiver;
    private String response;
    private Date responseTime;
    private String status;
    private String numVagonContainer;
    private String containerMark;
    private Date tranzitSendDatetime;
    private int docType;
    private int isContainer;
    private Date invDateTime;
    private String startStaName;
    private String destStaName;
    private String isPi;


    @Transient
    private String hasDoc;

    @Transient
    private boolean isView;

}
