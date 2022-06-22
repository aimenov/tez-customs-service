package kz.ne.railways.tezcustoms.service.exception;

import lombok.Data;

@Data
public class FLCException extends RuntimeException{

    private String errorMessage;
    private String errorCode;

    public FLCException(String errorCode) {
        this.errorCode = errorCode;
    }

    public FLCException(String errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}