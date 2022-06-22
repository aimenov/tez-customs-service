package kz.ne.railways.tezcustoms.service.payload.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class InvoiceRequest {

    private String expCode;

    @NotBlank
    private String invoiceNum;

    @NotNull
    @Range(min = 2012, message = "Invoice year must be in range [2012,present]")
    private int year;

    @NotNull
    @Range(min = 1, max = 12, message = "Month must be in range [1,12]!")
    private int month;
}
