package kz.ne.railways.tezcustoms.service.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Detail {
    private Timestamp date;
    private String source;
    private DetailStatus detailStatus;
}
