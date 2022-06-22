package kz.ne.railways.tezcustoms.service.exception;

import kz.ne.railways.tezcustoms.service.constants.errors.Errors;
import kz.ne.railways.tezcustoms.service.payload.response.MessageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FLCExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FLCException.class)
    ResponseEntity<Object> handleException(FLCException exception) {
        MessageResponse response = new MessageResponse(exception.getErrorCode(), exception.getErrorMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable
            (HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        MessageResponse response = new MessageResponse(Errors.MALFORMED_JSON_REQUEST, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
            (final MethodArgumentNotValidException ex, final HttpHeaders headers,
             final HttpStatus status, final WebRequest request) {
        MessageResponse response = new MessageResponse(Errors.INVALID_PARAMETERS, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
