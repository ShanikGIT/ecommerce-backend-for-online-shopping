package com.nikhil.ecommerce_backend.controllers;
import com.nikhil.ecommerce_backend.dto.seller.ProductVariationResponse;
import com.nikhil.ecommerce_backend.dto.seller.ProductResponse;
import com.nikhil.ecommerce_backend.dto.general.ApiResponse;
import com.nikhil.ecommerce_backend.dto.seller.*;
import com.nikhil.ecommerce_backend.dto.general.UpdatePasswordRequest;
import com.nikhil.ecommerce_backend.services.seller.SellerService;
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
@RequestMapping("/api/seller")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SELLER')")
@Tag(name = "SellerController", description = "APIs for Sellers")
public class SellerController {

    private final SellerService sellerService;
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
    public ResponseEntity<SellerProfileResponse> viewMyProfile(
            @AuthenticationPrincipal UserDetails userDetails, Locale locale) {
        return ResponseEntity.ok(sellerService.getSellerProfile(userDetails.getUsername(), locale));
    }

    @PatchMapping(value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") @Valid UpdateSellerProfileRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Locale locale) {
        String messageKey = sellerService.updateSellerProfile(userDetails.getUsername(), request, imageFile);
        return ResponseEntity.ok(buildResponse(messageKey, locale, HttpStatus.OK));
    }

    @PatchMapping("/profile/password")
    public ResponseEntity<ApiResponse> updateMyPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequest request,
            Locale locale) {

        return ResponseEntity.ok(buildResponse(sellerService.updateSellerPassword(userDetails.getUsername(),
                        request, locale),
                        locale,
                        HttpStatus.OK));
    }

    @PatchMapping("/profile/address")
    public ResponseEntity<ApiResponse> updateMyAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateAddressRequest request,
            Locale locale) {

        return ResponseEntity.ok(buildResponse(sellerService.updateSellerAddress(userDetails.getUsername(),
                        request, locale),
                        locale,
                        HttpStatus.OK));
    }

    @GetMapping("/categories")
    public ResponseEntity<Page<LeafCategoryResponse>> listAllCategories(
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order )
    {

        Page<LeafCategoryResponse> categories = sellerService.getAllLeafCategories(max, offset, sort, order);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/products/add")
    public ResponseEntity<ApiResponse> addProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddProductRequest request,
            Locale locale) {

        String messageKey = sellerService.addProduct(userDetails.getUsername(), request);

        return ResponseEntity.ok(buildResponse(messageKey, locale, HttpStatus.OK));

    }
    @PostMapping(value = "/products/add-variations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> addProductVariation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") AddVariationRequest request,
            @RequestPart("primaryImage") MultipartFile primaryImage,
            @RequestPart(value = "secondaryImages", required = false) List<MultipartFile> secondaryImages,
            Locale locale) {

        String messageKey = sellerService.addProductVariation(
                userDetails.getUsername(), request, primaryImage, secondaryImages);

        return ResponseEntity.ok(buildResponse(messageKey, locale, HttpStatus.OK));
    }

    @GetMapping("/products/view-product")
    public ResponseEntity<ProductResponse> viewProduct(
            @RequestParam("id") Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProductResponse productDto = sellerService.getProductForSeller(productId, userDetails.getUsername());
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/products/view-variation")
    public ResponseEntity<ProductVariationResponse> viewProductVariation(
            @RequestParam("id") Long variationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProductVariationResponse variationDto = sellerService.getProductVariationForSeller(variationId, userDetails.getUsername());
        return ResponseEntity.ok(variationDto);
    }

    @GetMapping("/products/view-products")
    public ResponseEntity<Page<ProductResponse>> viewAllProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order) {

        Page<ProductResponse> products = sellerService.getAllProductsForSeller(
                userDetails.getUsername(), query, max, offset, sort, order);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/view-variations")
    public ResponseEntity<Page<ProductVariationResponse>> viewAllProductVariations(
            @RequestParam("productId") Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "asc") String order)
    {

        Page<ProductVariationResponse> variations = sellerService.getAllProductVariations(
                productId, userDetails.getUsername(), max, offset, sort, order,query);

        return ResponseEntity.ok(variations);
    }

    @DeleteMapping("/products/delete-product")
    public ResponseEntity<ApiResponse> deleteProduct(
            @RequestParam("id") Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            Locale locale) {
        String messageKey = sellerService.deleteProduct(productId, userDetails.getUsername());
        return ResponseEntity.ok(buildResponse(messageKey, locale, HttpStatus.OK));
    }

    @PutMapping("/products/update-product")
    public ResponseEntity<ApiResponse> updateProduct(
            @RequestParam("id") Long productId,
            @Valid @RequestBody UpdateProductRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails,
            Locale locale) {
        String messageKey = sellerService.updateProduct(productId, requestDto, userDetails.getUsername());
        return ResponseEntity.ok(buildResponse(messageKey, locale, HttpStatus.OK));
    }

    @PutMapping(value = "/products/update-variation", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> updateProductVariation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("id") Long variationId,
            @Valid @RequestPart("data") UpdateVariationRequest request,
            @RequestPart(value = "primaryImage", required = false) MultipartFile primaryImage,
            @RequestPart(value = "secondaryImages", required = false) List<MultipartFile> secondaryImages,
            Locale locale)
    {

        String messageKey = sellerService.updateProductVariation(
                variationId, request, primaryImage, secondaryImages, userDetails.getUsername());

        return ResponseEntity.ok(buildResponse(messageKey, locale, HttpStatus.OK));
    }
}
