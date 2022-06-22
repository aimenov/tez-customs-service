package kz.ne.railways.tezcustoms.service.security;

import kz.ne.railways.tezcustoms.service.constants.Constants;
import kz.ne.railways.tezcustoms.service.util.SecurityUtils;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String currentAuditor = Optional.of(SecurityUtils.getCurrentUserLogin()).orElse(Constants.SYSTEM);
        return Optional.of(currentAuditor);
    }
}