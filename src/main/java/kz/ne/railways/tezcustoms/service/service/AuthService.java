package kz.ne.railways.tezcustoms.service.service;

import kz.ne.railways.tezcustoms.service.payload.request.LoginRequest;
import kz.ne.railways.tezcustoms.service.payload.request.SignupRequest;
import kz.ne.railways.tezcustoms.service.payload.response.BinResponse;
import kz.ne.railways.tezcustoms.service.payload.response.JwtResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

public interface AuthService {

    void createUser(SignupRequest signUpRequest, MultipartFile file);

    JwtResponse authenticateUser (LoginRequest loginRequest);

    RedirectView activateEmail (String key);

    BinResponse getCompanyByBin(String bin) throws IOException;

}
