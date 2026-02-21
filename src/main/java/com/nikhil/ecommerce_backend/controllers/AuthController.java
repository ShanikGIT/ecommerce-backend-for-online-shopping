package com.nikhil.ecommerce_backend.controllers;

import com.nikhil.ecommerce_backend.dto.customer.CustomerRegisterRequest;
import com.nikhil.ecommerce_backend.dto.general.*;
import com.nikhil.ecommerce_backend.dto.seller.SellerRegisterRequest;
import com.nikhil.ecommerce_backend.services.auth.AuthService;
import com.nikhil.ecommerce_backend.services.auth.PasswordResetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "Authentication APIs for users")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final MessageSource messageSource;

    private ApiResponse buildResponse(String message, HttpStatus status) {
        return new ApiResponse(
                true,
                message,
                status.value(),
                LocalDateTime.now()
        );
    }

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request,
                                                        Locale locale) {
        authService.registerCustomer(request,locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage("customer.registration.success", null, locale),
                HttpStatus.OK
        ));
    }

    @PutMapping("/activate")
    public ResponseEntity<ApiResponse> activation(@RequestParam("token") String token,
                                                Locale locale) {
        authService.activateAccount(token,locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage("account.activation.success", null, locale),
                HttpStatus.OK
        ));
    }

    @PostMapping("/resend-activation")
    public ResponseEntity<ApiResponse> resendActivation(@RequestParam("email") String email,
                                                        Locale locale) {
        authService.resendActivationLink(email,locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage("activation.link.resent", null, locale),
                HttpStatus.OK
        ));
    }

    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponse> registerSeller(@Valid @RequestBody SellerRegisterRequest request,
                                                      Locale locale) {
        authService.registerSeller(request,locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage("seller.registration.success", null, locale),
                HttpStatus.OK
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(
            @Valid @RequestBody LoginRequest request,
            Locale locale) {

        LoginResponse response = authService.userLogin(request, locale);

        String successMessage = messageSource.getMessage("login.success", null, locale);
        response.setMessage(successMessage);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> userLogout(@RequestHeader("Authorization") String authHeader,
                                                  Locale locale) {
        String token = authHeader.replace("Bearer ", "");
        String key = authService.userLogout(token,locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage(key, null, locale),
                HttpStatus.OK
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenByRefreshToken> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                      Locale locale) {
        passwordResetService.createPasswordResetToken(request.getEmail(),locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage("password.reset.link.sent", null, locale),
                HttpStatus.OK
        ));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam("token") String token,
                                                     @Valid @RequestBody ResetPasswordRequest request,
                                                     Locale locale) {
        passwordResetService.resetPassword(token, request,locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage("password.reset.success", null, locale),
                HttpStatus.OK
        ));
    }
}
