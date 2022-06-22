package kz.ne.railways.tezcustoms.service.security.service;

import kz.ne.railways.tezcustoms.service.model.PasswordResetToken;

public interface PasswordResetTokenService {
    PasswordResetToken findByToken(String token);

    PasswordResetToken save(PasswordResetToken passwordResetToken);
}
