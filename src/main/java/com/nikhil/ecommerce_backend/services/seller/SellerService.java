package com.nikhil.ecommerce_backend.services.seller;
import com.nikhil.ecommerce_backend.dto.seller.ProductResponse;
import com.nikhil.ecommerce_backend.dto.seller.*;
import com.nikhil.ecommerce_backend.dto.general.UpdatePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

public interface SellerService
{
    SellerProfileResponse getSellerProfile(String sellerEmail, Locale locale);

    String updateSellerProfile(String sellerEmail, UpdateSellerProfileRequest request, MultipartFile imageFile);

    String updateSellerPassword(String sellerEmail, UpdatePasswordRequest request, Locale locale);

    String updateSellerAddress(String sellerEmail, UpdateAddressRequest request, Locale locale);

    Page<LeafCategoryResponse> getAllLeafCategories(int max, int offset, String sort, String order);

    String addProduct(String sellerUsername,AddProductRequest request);

    String addProductVariation(String sellerUsername, AddVariationRequest request, MultipartFile primaryImage, List<MultipartFile> secondaryImages);

    ProductResponse getProductForSeller(Long productId, String sellerEmail);

    com.nikhil.ecommerce_backend.dto.seller.ProductVariationResponse getProductVariationForSeller(Long variationId, String sellerEmail);

    Page<ProductResponse> getAllProductsForSeller(String sellerEmail, String query, int max, int offset, String sort, String order);

    Page<com.nikhil.ecommerce_backend.dto.seller.ProductVariationResponse> getAllProductVariations(Long productId, String sellerEmail, int max, int offset, String sort, String order, String query);

    String deleteProduct(Long productId, String sellerEmail);

    String updateProduct(Long productId, UpdateProductRequest requestDto, String sellerEmail);


    String updateProductVariation(Long variationId, UpdateVariationRequest request, MultipartFile primaryImage, List<MultipartFile> secondaryImages, String username);
}