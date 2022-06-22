package kz.ne.railways.tezcustoms.service.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpeditorValidation {

    private boolean isExist;

    public ExpeditorValidation(boolean isExist){
        this.isExist = isExist;
    }
}
