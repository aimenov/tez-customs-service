package kz.ne.railways.tezcustoms.service.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EcpSignRequest {

    private Long documentNumber;

    @NotBlank
    private String type;

    @NotBlank
    private String rawData;

    @NotBlank
    private String signedData;
}
