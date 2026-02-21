package com.nikhil.ecommerce_backend.services.auth;

import com.nikhil.ecommerce_backend.dto.general.ResetPasswordRequest;
import com.nikhil.ecommerce_backend.entities.PasswordResetToken;
import com.nikhil.ecommerce_backend.entities.User;
import com.nikhil.ecommerce_backend.exceptions.TokenException;
import com.nikhil.ecommerce_backend.repositories.PasswordResetTokenRepository;
import com.nikhil.ecommerce_backend.repositories.UserRepository;
import com.nikhil.ecommerce_backend.services.common.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.token.password-reset-duration-minutes}")
    private long prtExp;

    @Transactional
    public void createPasswordResetToken(String email, Locale locale) {
        User user = userRepo.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("email.not.found"));

        tokenRepo.deleteByUser(user);

        PasswordResetToken token = new PasswordResetToken(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(prtExp));
        tokenRepo.save(token);

        emailService.sendPasswordResetEmail(user.getEmail(), token.getToken(), locale);
    }

    public void resetPassword(String token, ResetPasswordRequest request, Locale locale) throws TokenException {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("password.mismatch");
        }
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new TokenException("token.invalid"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(resetToken);
            throw new TokenException("token.expired");
        }
        String oldPassword = userRepo.getPassword(resetToken.getUser().getEmail());
        if (passwordEncoder.matches(request.getPassword(), oldPassword)) {
            throw new IllegalArgumentException("last.password");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordUpdateDate(new Date());
        user.setLocked(false);
        user.setInvalid_attempt_count(0);
        userRepo.save(user);

        tokenRepo.delete(resetToken);

        emailService.sendPasswordChangedEmail(user.getEmail(), locale);
    }
}
