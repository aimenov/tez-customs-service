package kz.ne.railways.tezcustoms.service.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String companyName;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String email, String companyName, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.companyName = companyName;
        this.roles = roles;
    }

}

