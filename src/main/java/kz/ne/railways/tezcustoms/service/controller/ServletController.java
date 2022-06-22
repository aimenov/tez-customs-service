package kz.ne.railways.tezcustoms.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.ne.railways.tezcustoms.service.entity.User;
import kz.ne.railways.tezcustoms.service.constants.errors.Errors;
import kz.ne.railways.tezcustoms.service.exception.FLCException;
import kz.ne.railways.tezcustoms.service.payload.request.EcpSignRequest;
import kz.ne.railways.tezcustoms.service.payload.response.MessageResponse;
import kz.ne.railways.tezcustoms.service.repository.UserRepository;
import kz.ne.railways.tezcustoms.service.service.EcpService;
import kz.ne.railways.tezcustoms.service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/servlet")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ServletController {

    /*
     * TODO make general encoding request.setCharacterEncoding("UTF-8");
     * response.setCharacterEncoding("UTF-8");
     */

    private final UserRepository userRepository;
    private final EcpService ecpService;

    @Operation(summary = "Sign ecp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document successfully signed",
                            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                            description = "NOT_VALID_SIGNER: IIN/BIN is not allowed to sign this document",
                            content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @PostMapping("sign-ecp-data")
    //@PreAuthorize("hasRole('CLIENT') or hasRole('OPERATOR') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> signEcpData(@Valid @RequestBody EcpSignRequest ecpSignRequest) throws FLCException {
        try {
            User user = userRepository.findByEmail(SecurityUtils.getCurrentUserLogin())
                            .orElseThrow(() -> new FLCException(Errors.USER_NOT_FOUND));
            boolean isValid = ecpService.isValidSigner(ecpSignRequest.getSignedData(), user);
            if (isValid) {
                return ResponseEntity.ok(new MessageResponse("Document successfully signed"));
            } else {
                String errorCode = "NOT_VALID_SIGNER";
                String message = "IIN/BIN is not allowed to sign this document";
                throw new FLCException(errorCode,message);
            }
        } catch (Exception exception) {
            throw new FLCException(exception.getMessage());
        }
    }
}
