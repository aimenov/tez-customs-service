package kz.ne.railways.tezcustoms.service.payload.request;

import javax.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    @NotBlank
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
