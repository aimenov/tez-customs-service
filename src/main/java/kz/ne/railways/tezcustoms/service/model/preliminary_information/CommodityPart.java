package kz.ne.railways.tezcustoms.service.model.preliminary_information;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class CommodityPart {

    @Schema(description = "Отправление")
    @NotBlank
    private Location departure;

    @Schema(description = "Назначение")
    @NotBlank
    private Location destination;

    @Schema(description = "Отправитель")
    @NotBlank
    private Sender sender;

    @Schema(description = "Получатель")
    @NotBlank
    private Receiver receiver;
}
