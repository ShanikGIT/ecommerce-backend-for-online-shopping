package com.nikhil.ecommerce_backend.services.admin;

import com.nikhil.ecommerce_backend.dto.admin.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Locale;

public interface AdminService {
    Page<CustomerResponseForAdmin> getAllCustomers(int page, int size, String sort, String email);

    Page<SellerResponseForAdmin> getAllSellers(int page, int size, String sort, String email);

    String activateCustomer(Long customerId, Locale locale);

    String deactivateCustomer(Long customerId, Locale locale);

    String activateSeller(Long sellerId, Locale locale);

    String deactivateSeller(Long sellerId, Locale locale);

    String addMetadataField(String fieldName,Locale locale);

    Page<MetadataFieldResponse> getAllMetadataFields(int max, int offset, String sort, String order, String query);

    String addCategory(String name, Long parentId, Locale locale);

    String updateCategory(Long categoryId, String name,  Locale locale);

    CategoryResponse getCategoryById(Long id);

    Page<CategoryResponse> getAllCategories(int max, int offset, String sort, String order, String query);

    String addCategoryMetadata(List<CategoryMetadataRequest> requests, Locale locale);

    String updateCategoryMetadata(List<CategoryMetadataRequest> requests, Locale locale);

    AdminProductView getProductForAdmin(Long productId);

    Page<AdminProductView> getAllProducts(Long sellerId, Long categoryId, Boolean isActive, Boolean isDeleted,
                                          int max, int offset, String sort, String order);

    String deactivateProduct(Long productId, Locale locale);

    String activateProduct(Long productId, Locale locale);

}