package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Data
@Table(name = "NE_SMGS_TNVED_DOCUMENTS", schema = "KTZ")
public class NeSmgsTnVedDocuments implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGS_TNVED_DOCUMENTS_UN")
    private Long smgsTnVedDocumentsUn;

    @Column(name = "SMGS_TNVED_UN")
    private long smgsTnVedUn;

    @Column(name = "DOCUMENT_CODE")
    private String documentCode;

    @Column(name = "DOCUMENT_NAME")
    private String documentName;

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @Column(name = "DOCUMENT_DATE")
    private Date documentDate;

    @Column(name = "DOCUMENT_DATE_TO")
    private Date documentDateTo;

    @Column(name = "COPY_COUNT")
    private int copyCount;

    @Column(name = "LIST_COUNT")
    private int listCount;

}
