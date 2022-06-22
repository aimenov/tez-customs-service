package kz.ne.railways.tezcustoms.service.model.preliminary_information;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Personal {

    @Schema(description = "БИН для РК")
    @NotBlank
    private String bin;

    @Schema(description = "ИИН для РК")
    @NotBlank
    private String iin;

    @Schema(description = "Категория лица для РК")
    @NotBlank
    private String personsCategory;

    @Schema(description = "КАТО для РК")
    @NotBlank
    private String kato;

    @Schema(description = "ИТН резерва для РК")
    @NotBlank
    private String itn;

    /*
     * they are null in tables so I commented if add them, make common for declarant and expeditor
     */

    // private String declarantRUOGRN; // ОГРН/ОГРНИП для РФ
    // private String declarantRUKPP; // КПП для РФ
    // private String declarantRUINN; // ИНН для РФ
    // private String declarantBYUNP; // УНП для Беларуси
    // private String declarantBYIN; // Идентификационный номер физ.лица для Беларуси
    // private String declarantAMUNN; // УНН для Армении
    // private String declarantAMNZOU; // НЗОУ для Армении
    // private String declarantKGINN; // ИНН для Кыргызстана
    // private String declarantKGOKPO; // ОКПО для Кыргызстана

    // private String expeditorRUOGRN; // ОГРН/ОГРНИП для РФ
    // private String expeditorRUKPP; // КПП для РФ
    // private String expeditorRUINN; // ИНН для РФ
    // private String expeditorBYUNP; // УНП для Беларуси
    // private String expeditorBYIN; // Идентификационный номер физ.лица для Беларуси
    // private String expeditorAMUNN; // УНН для Армении
    // private String expeditorAMNZOU; // НЗОУ для Армении
    // private String expeditorKGINN; // ИНН для Кыргызстана
    // private String expeditorKGOKPO; // ОКПО для Кыргызстана
}
