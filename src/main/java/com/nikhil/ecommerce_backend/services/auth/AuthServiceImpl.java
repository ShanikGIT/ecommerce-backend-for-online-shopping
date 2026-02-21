package com.nikhil.ecommerce_backend.services.auth;

import com.nikhil.ecommerce_backend.constants.AddressLabel;
import com.nikhil.ecommerce_backend.constants.RoleAuthority;
import com.nikhil.ecommerce_backend.dto.general.AccessTokenByRefreshToken;
import com.nikhil.ecommerce_backend.dto.customer.CustomerRegisterRequest;
import com.nikhil.ecommerce_backend.dto.general.LoginRequest;
import com.nikhil.ecommerce_backend.dto.general.LoginResponse;
import com.nikhil.ecommerce_backend.dto.seller.SellerRegisterRequest;
import com.nikhil.ecommerce_backend.dto.seller.UpdateAddressRequest;
import com.nikhil.ecommerce_backend.entities.*;
import com.nikhil.ecommerce_backend.exceptions.*;
import com.nikhil.ecommerce_backend.repositories.*;
import com.nikhil.ecommerce_backend.security.CustomUserDetailsService;
import com.nikhil.ecommerce_backend.security.JwtUtil;
import com.nikhil.ecommerce_backend.services.common.EmailService;
import com.nikhil.ecommerce_backend.services.common.TokenBlacklistCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;
    private final ActivationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistCache  tokenBlacklistCache;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void registerCustomer(CustomerRegisterRequest request, Locale locale) {
        if (userRepository.findByEmailAndIsDeletedFalse(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("email.already.exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("password.mismatch");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setActive(false);
        customer.setLocked(false);
        customer.setDeleted(false);
        customer.setCreatedBy(request.getEmail());
        customer.setUpdatedBy(request.getEmail());
        customer.setContact(request.getContactNumber());

        Role customerRole = (Role) roleRepository.findByAuthority(RoleAuthority.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("role.customer.not.found"));

        customer.getRoles().add(customerRole);
        customerRepository.save(customer);

        ActivationToken token = new ActivationToken(customer);
        tokenRepository.save(token);

        emailService.sendActivationEmail(customer.getEmail(), token.getToken(), locale);
    }

    @Override
    public void registerSeller(SellerRegisterRequest request, Locale locale) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("email.already.exists");
        }
        if (sellerRepository.existsByGst(request.getGst())) {
            throw new ResourceAlreadyExistsException("gst.already.registered");
        }
        if (sellerRepository.existsByCompanyNameIgnoreCase(request.getCompanyName())) {
            throw new ResourceAlreadyExistsException("company.name.in.use");
        }
        if(sellerRepository.existsByCompanyContact(request.getCompanyContact())) {
            throw new ResourceAlreadyExistsException("company.contact.in.use");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("password.mismatch");
        }
        if (!request.getAddress().getLabel().equals(AddressLabel.HOME)
                && !request.getAddress().getLabel().equals(AddressLabel.OFFICE)) {
            throw new IllegalArgumentException("address.label.not.found");
        }

        Seller seller = new Seller();
        seller.setFirstName(request.getFirstName());
        seller.setLastName(request.getLastName());
        seller.setEmail(request.getEmail());
        seller.setPassword(passwordEncoder.encode(request.getPassword()));
        seller.setActive(false);
        seller.setLocked(false);
        seller.setDeleted(false);
        seller.setGst(request.getGst());
        seller.setCompanyName(request.getCompanyName());
        seller.setCompanyContact(request.getCompanyContact());
        seller.setCreatedBy(request.getEmail());
        seller.setUpdatedBy(request.getEmail());
        UpdateAddressRequest addressDto = request.getAddress();
        Address address = new Address();
        address.setAddressLine(addressDto.getAddressLine());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setZipCode(addressDto.getZipCode());
        address.setLabel(addressDto.getLabel());
        address.setCreatedBy(request.getEmail());
        address.setUpdatedBy(request.getEmail());
        address.setSeller(seller);
        seller.setAddress(address);
        Role sellerRole = (Role) roleRepository.findByAuthority(RoleAuthority.SELLER)
                .orElseThrow(() -> new ResourceNotFoundException("role.seller.not.found"));
        seller.getRoles().add(sellerRole);

        sellerRepository.save(seller);

        emailService.sendSellerPendingApprovalEmail(seller.getEmail(), locale);
    }

    @Override
    @Transactional
    public void activateAccount(String token, Locale locale) {
        ActivationToken activationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenException("token.invalid"));

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(activationToken);
            resendActivationLink(activationToken.getUser().getEmail(), locale);
            throw new TokenException("activation.token.expired");
        }
        User user = activationToken.getUser();
        user.setActive(true);
        userRepository.save(user);
        emailService.notifyActivation(user.getEmail(), locale);
        tokenRepository.delete(activationToken);
    }

    @Override
    @Transactional
    public void resendActivationLink(String email, Locale locale) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("email.not.found"));

        if (user.isActive()) {
            throw new ResourceAlreadyExistsException("account.already.activated");
        }

        tokenRepository.deleteByUser(user);

        ActivationToken newToken = new ActivationToken(user);
        tokenRepository.save(newToken);

        emailService.sendActivationEmail(user.getEmail(), newToken.getToken(), locale);
    }

    @Override
    public LoginResponse userLogin(LoginRequest request, Locale locale) {
        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        if (user.isDeleted())
        {
            throw new ResourceNotFoundException("user.not.found");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), authentication.getAuthorities());
        RefreshToken refreshToken = refreshTokenService.createAndStoreRefreshToken(user.getId());

        return LoginResponse.builder()
                .message("login.success")
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(r -> r.getAuthority().name()).toList())
                .build();
    }

    @Override
    public String userLogout(String token, Locale locale)
    {
        if (tokenBlacklistCache.isBlacklisted(token)) {
            throw new TokenException("token.already.logged.out");
        }

        if (!jwtUtil.validateToken(token)) {
            throw new TokenException("token.invalid");
        }

        LocalDateTime expiry = jwtUtil.extractExpiration(token);

        tokenBlacklistCache.cacheBlacklistedToken(token, expiry);

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        refreshTokenService.deleteByUserId(user.getId());

        return "logout.success";
    }


    @Override
    public AccessTokenByRefreshToken refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenString)
                .orElseThrow(() -> new TokenException("token.invalid"));

        if (!refreshTokenService.verifyExpiration(refreshToken)) {
            throw new TokenException("refresh.token.expired");
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(),userDetails.getAuthorities());

        return AccessTokenByRefreshToken.builder()
                .newAccessToken(newAccessToken)
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .refreshToken(refreshTokenString)
                .build();
    }
}