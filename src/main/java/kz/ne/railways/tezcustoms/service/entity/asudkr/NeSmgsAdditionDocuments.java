package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "NE_SMGS_ADDITION_DOCUMENTS", schema = "KTZ")
public class NeSmgsAdditionDocuments implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_ADDITION_DOCS_UN")
    private Long smgsAdditionDocsUn;

    @Column(name = "INV_UN")
    private long invUn;

    @Column(name = "DOC_NUMBER")
    private String docNumber;

    @Column(name = "DOC_NAME")
    private String docName;

    @Column(name = "DOC_DATE")
    private Date docDate;

    @Column(name = "FILENAME")
    private String fileName;

    @Column(name = "FILE_UUID")
    private String fileUuid;

    private int status;

    private static final long serialVersionUID = 1L;

    public NeSmgsAdditionDocuments() {
        super();
    }

}
