package kz.ne.railways.tezcustoms.service.model.preliminary_information;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Address {

    @Schema(description = "Населенный пункт")
    @NotBlank
    private String city;

    @Schema(description = "Область (регион, штат и т.п.)")
    @NotBlank
    private String region;

    @Schema(description = "Улица, номер дома, номер офиса")
    @NotBlank
    private String address;
}
