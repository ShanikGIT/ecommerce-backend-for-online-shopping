package com.nikhil.ecommerce_backend.services.auth;

import com.nikhil.ecommerce_backend.dto.general.AccessTokenByRefreshToken;
import com.nikhil.ecommerce_backend.dto.customer.CustomerRegisterRequest;
import com.nikhil.ecommerce_backend.dto.general.LoginRequest;
import com.nikhil.ecommerce_backend.dto.general.LoginResponse;
import com.nikhil.ecommerce_backend.dto.seller.SellerRegisterRequest;

import java.util.Locale;

public interface AuthService {

    void registerCustomer(CustomerRegisterRequest request, Locale locale);

    void registerSeller(SellerRegisterRequest request, Locale locale);

    void activateAccount(String token, Locale locale);

    void resendActivationLink(String email, Locale locale);

    LoginResponse userLogin(LoginRequest request, Locale locale);

    String userLogout(String token, Locale locale);

    AccessTokenByRefreshToken refreshToken(String refreshToken);
}
