package com.nikhil.ecommerce_backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.ecommerce_backend.dto.customer.*;
import com.nikhil.ecommerce_backend.dto.general.ApiResponse;
import com.nikhil.ecommerce_backend.dto.general.UpdatePasswordRequest;
import com.nikhil.ecommerce_backend.services.customer.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
@Tag(name = "CustomerController", description = "APIs for Customers")
public class CustomerController {

    private final CustomerService customerService;
    private final MessageSource messageSource;


    private ApiResponse buildResponse(String message, Locale locale, HttpStatus status) {
        return new ApiResponse(
                true,
                messageSource.getMessage(message, null, locale),
                status.value(),
                LocalDateTime.now()
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> viewMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(customerService.getCustomerProfile(userDetails.getUsername()));
    }

    @PatchMapping(value = "/profile/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart(value = "data") @Valid String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Locale locale) {
        UpdateCustomerProfileRequest request = null;
        try {
            request = new ObjectMapper().readValue(requestJson, UpdateCustomerProfileRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String messageKey = customerService.updateCustomerProfile(userDetails.getUsername(), request, imageFile);
        return ResponseEntity.ok(buildResponse(messageKey, locale, HttpStatus.OK));
    }

    @PatchMapping("/profile/password/update")
    public ResponseEntity<ApiResponse> updateMyPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequest request,
            Locale locale) {
        String message = customerService.updateCustomerPassword(userDetails.getUsername(), request, locale);
        return ResponseEntity.ok(buildResponse(message, locale, HttpStatus.OK));
    }

    @GetMapping("/addresses/view")
    public ResponseEntity<List<AddressResponse>> viewMyAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(customerService.getCustomerAddresses(userDetails.getUsername()));
    }

    @PostMapping("/addresses/add")
    public ResponseEntity<ApiResponse> addMyAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddAddressRequest request, Locale locale) {
        String message = customerService.addCustomerAddress(userDetails.getUsername(), request, locale);
        return ResponseEntity.ok(buildResponse(message, locale, HttpStatus.OK));
    }

    @PatchMapping("/addresses/update")
    public ResponseEntity<ApiResponse> updateMyAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("addressId") Long addressId,
            @Valid @RequestBody UpdateAddressRequest request,Locale locale) {
        String message = customerService.updateCustomerAddress(userDetails.getUsername(), addressId, request);
        return ResponseEntity.ok(buildResponse(message, locale, HttpStatus.OK));
    }

    @DeleteMapping("/addresses/delete")
    public ResponseEntity<ApiResponse> deleteMyAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("addressId") Long addressId,
            Locale locale) {
        String message = customerService.deleteCustomerAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.ok(buildResponse(message, locale, HttpStatus.OK));
    }

    @GetMapping("/categories/view")
    public ResponseEntity<List<CategoryView>> listCategories(
            @RequestParam(required = false) Long categoryId) {

        List<CategoryView> categories = customerService.getCategories(categoryId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/filter")
    public ResponseEntity<CategoryFilter> getCategoryFilters(@RequestParam("categoryId") Long categoryId) {
        CategoryFilter filterDto = customerService.getFilteringDetailsForCategory(categoryId);
        return ResponseEntity.ok(filterDto);
    }

    @GetMapping("/products/view-product")
    public ResponseEntity<CustomerProductView> viewProduct(@RequestParam("id") Long id) {
        CustomerProductView product = customerService.getProductForCustomer(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/products/view-all")
    public ResponseEntity<Page<CustomerProductList>> viewAllProducts(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order) {

        Page<CustomerProductList> products = customerService.getAllProductsByCategory(
                categoryId, query, max, offset, sort, order);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/similar")
    public ResponseEntity<Page<CustomerProductList>> viewSimilarProducts(
            @RequestParam("productId") Long productId,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order) {

        Page<CustomerProductList> similarProducts = customerService.getSimilarProducts(
                productId, max, offset, sort, order);

        return ResponseEntity.ok(similarProducts);
    }

}
