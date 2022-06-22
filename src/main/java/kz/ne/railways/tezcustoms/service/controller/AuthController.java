package kz.ne.railways.tezcustoms.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.ne.railways.tezcustoms.service.exception.FLCException;
import kz.ne.railways.tezcustoms.service.payload.request.LoginRequest;
import kz.ne.railways.tezcustoms.service.payload.request.SignupRequest;
import kz.ne.railways.tezcustoms.service.payload.response.BinResponse;
import kz.ne.railways.tezcustoms.service.payload.response.ExpeditorValidation;
import kz.ne.railways.tezcustoms.service.payload.response.MessageResponse;
import kz.ne.railways.tezcustoms.service.service.EcpService;
import kz.ne.railways.tezcustoms.service.service.AuthService;
import kz.ne.railways.tezcustoms.service.service.ForDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder encoder;
    private final ForDataService forDataService;
    private final EcpService ecpService;
    private final AuthService authService;

    @Operation(summary = "Sign in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully signed in",
                            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content)})
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws FLCException {
        log.info("request password: " + loginRequest.getPassword());
        log.info("encoded password: " + encoder.encode(loginRequest.getPassword()));
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @Operation(summary = "Sign up")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully signed up",
                    content = {@Content(mediaType = "application/json")})})
    @PostMapping(value = "/signup")
    public ResponseEntity<MessageResponse> registerUser(@RequestPart MultipartFile file,
                                                        @RequestPart SignupRequest signupRequest) {
        authService.createUser(signupRequest, file);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/activate")
    public RedirectView activateEmail(@RequestParam(value = "key") String key) {
        return authService.activateEmail(key);
    }

    @GetMapping("/checkBin/{bin}")
    public ResponseEntity<BinResponse> checkBin(@PathVariable String bin) throws IOException {
        return ResponseEntity.ok(authService.getCompanyByBin(bin));
    }

    @GetMapping("/checkExpeditor/{code}")
    public ResponseEntity<?> checkExpeditor(@PathVariable String code) {
        return ResponseEntity.ok(new ExpeditorValidation(forDataService.checkExpeditorCode(Long.parseLong(code))));
    }

    @PostMapping("/checkEcp")
    public ResponseEntity<?> checkEcp(@RequestParam("ecp") String ecp, @RequestParam("bin") String bin) throws FLCException {
        try {
            boolean isValid = ecpService.isValidSigner(ecp, bin);
            if (isValid) {
                return ResponseEntity.ok(new MessageResponse("IIN/BIN verified"));
            } else {
                String errorCode = "NOT_VALID_USER";
                String message = "IIN/BIN is not match";
                throw new FLCException(errorCode, message);
            }
        } catch (Exception exception) {
            throw new FLCException(exception.getMessage());
        }
    }
}
