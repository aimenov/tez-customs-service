package kz.ne.railways.tezcustoms.service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.ne.railways.tezcustoms.service.constants.errors.Errors;
import kz.ne.railways.tezcustoms.service.entity.Company;
import kz.ne.railways.tezcustoms.service.entity.Role;
import kz.ne.railways.tezcustoms.service.entity.User;
import kz.ne.railways.tezcustoms.service.exception.FLCException;
import kz.ne.railways.tezcustoms.service.model.ERole;
import kz.ne.railways.tezcustoms.service.payload.request.LoginRequest;
import kz.ne.railways.tezcustoms.service.payload.request.SignupRequest;
import kz.ne.railways.tezcustoms.service.payload.response.BinResponse;
import kz.ne.railways.tezcustoms.service.payload.response.JwtResponse;
import kz.ne.railways.tezcustoms.service.repository.CompanyRepository;
import kz.ne.railways.tezcustoms.service.repository.RoleRepository;
import kz.ne.railways.tezcustoms.service.repository.UserRepository;
import kz.ne.railways.tezcustoms.service.security.jwt.JwtUtils;
import kz.ne.railways.tezcustoms.service.security.service.impl.UserDetailsImpl;
import kz.ne.railways.tezcustoms.service.service.MailService;
import kz.ne.railways.tezcustoms.service.service.AuthService;
import kz.ne.railways.tezcustoms.service.service.SftpService;
import kz.ne.railways.tezcustoms.service.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SftpService fileserver;
    @Value("${server.redirectUrl}")
    private String url;

    @Override
    public void createUser (SignupRequest signUpRequest, MultipartFile file) {
        // TODO check iin after eds validation added
//        if (userRepository.existsByIin(signUpRequest.getIinBin())) {
//            throw new FLCException(Errors.IIN_TAKEN);
//        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new FLCException(Errors.EMAIL_IN_USE);
        }
        Set<ERole> strRoles = signUpRequest.getRoles();
        if (strRoles == null || strRoles.isEmpty()) {
            throw new FLCException(Errors.ROLE_CANNOT_BE_EMPTY);
        }

        Company company = findUserCompany(signUpRequest);
        String filepath = fileserver.sendRegistrationDoc(file, signUpRequest.getIinBin());
        newUser(signUpRequest, company, filepath);
    }

    Company findUserCompany(SignupRequest user){
        Company company = companyRepository.findOneByIdentifier(user.getIinBin());
        if (company == null) {
            company = new Company(user.getIinBin(), user.getAddress(),
                    user.getCompanyName(), user.getCompanyDirector(),
                    user.isCompany(), user.getKato());
            if (user.isCompany()) {
                company.setBin(user.getIinBin());
            } else {
                company.setIin(user.getIinBin());
            }
            companyRepository.save(company);
        }
        return  company;
    }

    private void newUser(SignupRequest signUpRequest, Company company, String filepath) {
        User user = new User(
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getPhone()
        );

        Set<Role> roles = roleRepository.findByNameIn(signUpRequest.getRoles());
        user.setCompany(company);
        setActivationKey(user);
        user.setRoles(roles);
        user.setRegisterFilePath(filepath);
        userRepository.save(user);
        mailService.sendActivationEmail(user);
    }

    @Override
    public JwtResponse authenticateUser (LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userRepository.existsByEmailAndEmailActivatedTrue(userDetails.getEmail())) {
            throw new FLCException(Errors.EMAIL_NOT_ACTIVATED);
        }
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(),
                userDetails.getCompanyName(), roles);
    }

    @Override
    public RedirectView activateEmail (String key) {
        RedirectView redirectView = new RedirectView();
        User user = userRepository.findOneByActivationKey(key);
        if (user == null) {
            redirectView.setUrl(url + "3");
            return redirectView;
            //new FLCException(Errors.EMAIL_IS_ACTIVATED, LocaleUtils.getDefaultBundle("activation.key.time2")));
        }
        if (user.getActivationKeyDate().before(Timestamp.from(Instant.now()))) {
            setActivationKey(user);
            userRepository.save(user);
            mailService.sendActivationEmail(user);
            redirectView.setUrl(url + "2");
            return redirectView;
            //return new MessageResponse(Errors.ACTIVATION_CODE_IS_EXPIRED, LocaleUtils.getDefaultBundle("activation.key.time0"));
        }
        user.setEmailActivated(true);
        user.setActivationKey(null);
        user.setActivationKeyDate(null);
        userRepository.save(user);
        redirectView.setUrl(url + "1");
        return redirectView;
        //return new MessageResponse(LocaleUtils.getDefaultBundle("activation.key.time1"));
    }

    @Override
    public BinResponse getCompanyByBin(String bin) throws IOException {
        String formatUrl = String.format("https://stat.gov.kz/api/juridical/counter/api/?bin=%s&lang=ru", bin);
        URL url = new URL(formatUrl);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            log.info("Bin response: \n");
            Map<String, Object> map = objectMapper.readValue(url, new TypeReference<HashMap<String, Object>>() {});
            log.info("map: " + map);
            if (map.get("obj") == null) {
                throw new FLCException(Errors.COMPANY_NOT_FOUND_BY_BIN);
            }
            BinResponse binResponse = new BinResponse((HashMap<String, String>) map.get("obj"));
            log.info(String.valueOf(binResponse));
            return binResponse;
        } catch (IOException e) {
            throw new FLCException(Errors.SERVICE_IS_UNAVAILABLE, e.getMessage());
        } catch (Exception e) {
            throw new FLCException(e.getMessage());
        }
    }

    private void setActivationKey (User user) {
        user.setActivationKey(RandomUtil.generateActivationKey());
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1);
        user.setActivationKeyDate(Timestamp.valueOf(tomorrow));
    }

}
