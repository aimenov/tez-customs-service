package kz.ne.railways.tezcustoms.service.model.preliminary_information;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public abstract class Base {
    @Schema(description = "Наименование")
    @NotBlank
    private String name;

    @Schema(description = "Краткое наименование")
    @NotBlank
    private String shortName;

    @Schema(description = "Код страны")
    @NotBlank
    private String сountryCode;

    @Schema(description = "Страна")
    private String countryName;

    @Schema(description = "Почтовый индекс")
    private String index = "000000";

    @NotBlank
    private Address address;
}
