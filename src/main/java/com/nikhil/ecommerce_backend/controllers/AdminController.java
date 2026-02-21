package com.nikhil.ecommerce_backend.controllers;

import com.nikhil.ecommerce_backend.dto.admin.*;
import com.nikhil.ecommerce_backend.dto.general.ApiResponse;
import com.nikhil.ecommerce_backend.services.admin.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@Validated
@Tag(name = "AdminController", description = "Management APIs for admin")
public class AdminController {

    private final AdminService adminService;
    private final MessageSource messageSource;

    private ApiResponse buildResponse(String message, HttpStatus status) {
        return new ApiResponse(
                true,
                message,
                status.value(),
                LocalDateTime.now()
        );
    }

    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerResponseForAdmin>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String email) {

        Page<CustomerResponseForAdmin> customerPage = adminService.getAllCustomers(page, size, sort, email);
        return ResponseEntity.ok(customerPage);
    }

    @GetMapping("/sellers")
    public ResponseEntity<Page<SellerResponseForAdmin>> getAllSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String email) {

        Page<SellerResponseForAdmin> sellerPage = adminService.getAllSellers(page, size, sort, email);
        return ResponseEntity.ok(sellerPage);
    }

    @PutMapping("/customers/{id}/activate")
    public ResponseEntity<ApiResponse> activateCustomer(@PathVariable Long id, Locale locale) {
        String message = adminService.activateCustomer(id, locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage(message, null, locale),
                HttpStatus.OK
        ));
    }

    @PutMapping("/customers/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateCustomer(@PathVariable Long id, Locale locale) {
        String message = adminService.deactivateCustomer(id, locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage(message, null, locale),
                HttpStatus.OK
        ));
    }

    @PutMapping("/sellers/{id}/activate")
    public ResponseEntity<ApiResponse> activateSeller(@PathVariable Long id, Locale locale) {
        String message = adminService.activateSeller(id, locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage(message, null, locale),
                HttpStatus.OK
        ));
    }

    @PutMapping("/sellers/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateSeller(@PathVariable Long id, Locale locale) {
        String message = adminService.deactivateSeller(id, locale);
        return ResponseEntity.ok(buildResponse(
                messageSource.getMessage(message, null, locale),
                HttpStatus.OK
        ));
    }

    @PostMapping("/metadata-field")
    public ResponseEntity<ApiResponse> addMetadataField(@RequestParam("fieldName") String fieldName,
                                                        Locale locale) {
        String message = adminService.addMetadataField(fieldName, locale);
        return ResponseEntity.ok(buildResponse(
                message,
                HttpStatus.OK
        ));
    }

    @GetMapping("/metadata-fields")
    public ResponseEntity<Page<MetadataFieldResponse>> getAllMetadataFields(
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query) {

        Page<MetadataFieldResponse> responsePage = adminService.getAllMetadataFields(max, offset, sort, order, query);
        return ResponseEntity.ok(responsePage);
    }

    @PostMapping("/categories/add")
    public ResponseEntity<ApiResponse> addCategory(
            @RequestParam("name")
            @NotBlank(message = "Category name cannot be empty")
            @Size(min = 2, max = 50, message = "Category name must be between 3 and 50 characters")
            @Pattern(regexp = "^[A-Za-z ]+$", message = "Category name can only contain letters and spaces")
            String name,
            @RequestParam(required = false) Long parentId,
            Locale locale) {

        String message = adminService.addCategory(name, parentId, locale);
        return new ResponseEntity<>(
                buildResponse(message, HttpStatus.CREATED),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/category")
    public ResponseEntity<CategoryResponse> getCategoryById(@RequestParam("categoryId") Long id) {
        return ResponseEntity.ok(adminService.getCategoryById(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query) {

        return ResponseEntity.ok(adminService.getAllCategories(size, offset, sort, order, query));
    }

    @PutMapping("/categories/update")
    public ResponseEntity<ApiResponse> updateCategory(
            @RequestParam Long categoryId,
            @RequestParam("name")
            @NotBlank(message = "Category name cannot be empty")
            @Size(min = 2, max = 50, message = "Category name must be between 3 and 50 characters")
            @Pattern(regexp = "^[A-Za-z ]+$", message = "Category name can only contain letters and spaces")
            String name,
            Locale locale) {

        String message = adminService.updateCategory(categoryId, name, locale);
        return new ResponseEntity<>(
                buildResponse(message, HttpStatus.CREATED),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/categories/add-metadata")
    public ResponseEntity<ApiResponse> addCategoryMetadata(
            @RequestBody List<@Valid CategoryMetadataRequest> requests,
            Locale locale) {

        String message = adminService.addCategoryMetadata(requests, locale);
        return ResponseEntity.ok(buildResponse(
                message,
                HttpStatus.CREATED
        ));
    }

    @PutMapping("/categories/update-metadata")
    public ResponseEntity<ApiResponse> updateCategoryMetadata(
            @RequestBody List<@Valid CategoryMetadataRequest> requests,
            Locale locale) {

        String message = adminService.updateCategoryMetadata(requests, locale);
        return ResponseEntity.ok(buildResponse(
                message,
                HttpStatus.OK
        ));
    }

    @GetMapping("/products/view-product")
    public ResponseEntity<AdminProductView> viewProduct(@RequestParam("productId") Long productId) {
        return ResponseEntity.ok(adminService.getProductForAdmin(productId));
    }

    @GetMapping("/products/view-products")
    public ResponseEntity<Page<AdminProductView>> viewAllProducts(
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order) {

        return ResponseEntity.ok(adminService.getAllProducts(
                sellerId, categoryId, isActive, isDeleted, max, offset, sort, order
        ));
    }

    @PutMapping("products/deactivate")
    public ResponseEntity<ApiResponse> deactivateProduct(@RequestParam("productId") Long productId,
                                                         Locale locale) {
        String message = adminService.deactivateProduct(productId, locale);
        return ResponseEntity.ok(buildResponse(
                message,
                HttpStatus.OK
        ));
    }

    @PutMapping("products/activate")
    public ResponseEntity<ApiResponse> activateProduct(@RequestParam("productId") Long productId,
                                                       Locale locale) {
        String message = adminService.activateProduct(productId, locale);
        return ResponseEntity.ok(buildResponse(
                message,
                HttpStatus.OK
        ));
    }
}
