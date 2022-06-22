package kz.ne.railways.tezcustoms.service.payload.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EcpDataResponse {
    @NotBlank
    private Long documentNumber;

    private String documentType;

    @NotBlank
    private String rawData;
}
