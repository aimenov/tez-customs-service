package kz.ne.railways.tezcustoms.service.model.preliminary_information;

import io.swagger.v3.oas.annotations.media.Schema;

public class DestinationInformation {
    private Location location;
    private String index = "000000";

    @Schema(description = "Место назначения")
    private String place;

    private Address address;

    @Schema(description = "Таможенный орган спр")
    private Long customOrgUn;

    @Schema(description = "Таможенный орган код")
    private String customCode;

    @Schema(description = "Таможенный орган наим")
    private String customName;
}
