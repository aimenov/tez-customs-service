package kz.ne.railways.tezcustoms.service.payload.request;

import kz.ne.railways.tezcustoms.service.model.ERole;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 12)
    private String iinBin;

    @NotBlank
    @Size(max = 50)
    private String phone;

    @Size(max = 250)
    private String address;

    @Size(max = 120)
    private String companyName;

    @Size(max = 120)
    private String companyDirector;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 50)
    private String middleName;

    private boolean isCompany;

    @NotNull
    private Set<ERole> roles;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String kato;

    private String expeditorCode;


}
