package kz.ne.railways.tezcustoms.service.model.preliminary_information;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class Location {

    @NotBlank
    private String countryCode;
    private String countryId;
    private String countryName;

    @NotBlank
    private String stationCode;
    private String stationId;
    private String stationName;
}
