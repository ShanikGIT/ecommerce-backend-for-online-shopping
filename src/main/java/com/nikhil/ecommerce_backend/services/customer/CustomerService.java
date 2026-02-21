package com.nikhil.ecommerce_backend.services.customer;

import com.nikhil.ecommerce_backend.dto.customer.*;
import com.nikhil.ecommerce_backend.dto.general.UpdatePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

public interface CustomerService {
    CustomerProfileResponse getCustomerProfile(String email);

    List<AddressResponse> getCustomerAddresses(String email);

    String updateCustomerProfile(String email, UpdateCustomerProfileRequest request, MultipartFile imageFile);

    String updateCustomerPassword(String email, UpdatePasswordRequest request, Locale locale);

    String addCustomerAddress(String email, AddAddressRequest request, Locale locale);

    String deleteCustomerAddress(String email, Long addressId);

    String updateCustomerAddress(String email, Long addressId, UpdateAddressRequest request);

    List<CategoryView> getCategories(Long categoryId);

    CustomerProductView getProductForCustomer(Long productId);

    Page<CustomerProductList> getAllProductsByCategory(Long categoryId, String query, int max, int offset, String sort, String order);

    Page<CustomerProductList> getSimilarProducts(Long productId, int max, int offset, String sort, String order);

    CategoryFilter getFilteringDetailsForCategory(Long categoryId);

}