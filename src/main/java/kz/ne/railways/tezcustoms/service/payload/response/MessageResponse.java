package kz.ne.railways.tezcustoms.service.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    private String errorCode;
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
