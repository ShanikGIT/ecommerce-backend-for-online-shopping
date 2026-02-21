package com.nikhil.ecommerce_backend.security;

import com.nikhil.ecommerce_backend.entities.User;
import com.nikhil.ecommerce_backend.repositories.UserRepository;
import com.nikhil.ecommerce_backend.services.common.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;

    @Value("${app.security.password-max-age-days}")
    private long passwordMaxAgeDays;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        Locale locale = Locale.getDefault();

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage("auth.invalid.email", null, locale))
                );

        if (user.isLocked()) {
            throw new LockedException(messageSource.getMessage("auth.account.locked", null, locale));
        }

        if (!user.isActive()) {
            throw new DisabledException("auth.account.not.active");
        }

        if (isPasswordExpired(user)) {
            throw new CredentialsExpiredException(
                    messageSource.getMessage("auth.password.expired", null, locale));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            int attempts = user.getInvalid_attempt_count() + 1;
            user.setInvalid_attempt_count(attempts);

            if (attempts >= 3) {
                user.setLocked(true);
                userRepository.save(user);
                emailService.sendAccountLockedEmail(user.getEmail(), locale);
                throw new LockedException(messageSource.getMessage("auth.locked.by.attempts", null, locale));
            }

            userRepository.save(user);
            throw new BadCredentialsException(
                    messageSource.getMessage("auth.invalid.password", null, locale));
        }

        user.setInvalid_attempt_count(0);
        userRepository.save(user);

        return new UsernamePasswordAuthenticationToken(
                email, password,
                userDetailsService.loadUserByUsername(email).getAuthorities()
        );
    }

    private boolean isPasswordExpired(User user) {
        if (user.getPasswordUpdateDate() == null) {
            return false;
        }
        long passwordUpdateTime = user.getPasswordUpdateDate().getTime();
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - passwordUpdateTime;

        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        return diffInDays > passwordMaxAgeDays;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
