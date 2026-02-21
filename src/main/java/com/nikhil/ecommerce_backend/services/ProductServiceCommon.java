package com.nikhil.ecommerce_backend.services;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.ecommerce_backend.dto.admin.AdminProductView;
import com.nikhil.ecommerce_backend.dto.customer.CustomerProductList;
import com.nikhil.ecommerce_backend.dto.customer.CustomerProductView;
import com.nikhil.ecommerce_backend.dto.seller.ProductResponse;
import com.nikhil.ecommerce_backend.entities.Category;
import com.nikhil.ecommerce_backend.entities.Product;
import com.nikhil.ecommerce_backend.repositories.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceCommon {

    private final ObjectMapper objectMapper;

    public ProductServiceCommon(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String buildImageUrl(String imageName) {
        if (imageName == null || imageName.isBlank()) return null;

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/products/")
                .path(imageName)
                .toUriString();
    }

    public Pageable buildPageable(int max, int offset, String sort, String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(offset, max, Sort.by(direction, sort));
    }

    public AdminProductView toAdminDto(Product product) {
        AdminProductView.Category categoryDto = AdminProductView.Category.builder()
                .id(product.getCategory().getId())
                .name(product.getCategory().getName())
                .build();

        List<AdminProductView.Variation> variationDtos = product.getVariations().stream()
                .map(variation -> AdminProductView.Variation.builder()
                        .id(variation.getId())
                        .price(variation.getPrice())
                        .quantityAvailable(variation.getQuantityAvailable())
                        .isActive(variation.isActive())
                        .primaryImageUrl(buildImageUrl(variation.getPrimaryImageName()))
                        .build())
                .collect(Collectors.toList());

        return AdminProductView.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .isActive(product.getIsActive())
                .isDeleted(product.getIsDeleted())
                .category(categoryDto)
                .variations(variationDtos)
                .sellerId(product.getSeller().getId())
                .build();
    }

    public CustomerProductView toCustomerDto(Product product) {
        CustomerProductView.Category categoryDto = CustomerProductView.Category.builder()
                .name(product.getCategory().getName())
                .build();

        List<CustomerProductView.Variation> variationDtos = product.getVariations().stream().map(variation -> {
            String primaryImageUrl = buildImageUrl(variation.getPrimaryImageName());

            List<CustomerProductView.Image> imageDtos = variation.getSecondaryImages().stream()
                    .map(image -> CustomerProductView.Image.builder()
                            .imageUrl(buildImageUrl(image.getImageName()))
                            .build())
                    .collect(Collectors.toList());

            Map<String, Object> metadataMap =
                    objectMapper.convertValue(variation.getMetadata(), new TypeReference<>() {});

            return CustomerProductView.Variation.builder()
                    .price(variation.getPrice())
                    .quantityAvailable(variation.getQuantityAvailable())
                    .metadata(metadataMap)
                    .primaryImage(primaryImageUrl)
                    .secondaryImages(imageDtos)
                    .build();
        }).collect(Collectors.toList());

        return CustomerProductView.builder()
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(categoryDto)
                .variations(variationDtos)
                .build();
    }

    public CustomerProductList toCustomerListDto(Product product) {
        CustomerProductList.Category categoryDto = CustomerProductList.Category.builder()
                .name(product.getCategory().getName())
                .build();

        List<CustomerProductView.Variation> variationDtos = product.getVariations().stream()
                .filter(v -> v.isActive())
                .map(variation -> {
                    Map<String, Object> metadataMap =
                            objectMapper.convertValue(variation.getMetadata(), new TypeReference<>() {});

                    return CustomerProductView.Variation.builder()
                            .price(variation.getPrice())
                            .quantityAvailable(variation.getQuantityAvailable())
                            .metadata(metadataMap)
                            .primaryImage(buildImageUrl(variation.getPrimaryImageName()))
                            .build();
                }).collect(Collectors.toList());

        return CustomerProductList.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .category(categoryDto)
                .variations(variationDtos)
                .build();
    }

    public ProductResponse toSellerDto(Product product) {
        ProductResponse.Category categoryDto = ProductResponse.Category.builder()
                .id(product.getCategory().getId())
                .name(product.getCategory().getName())
                .build();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .isActive(product.getIsActive())
                .description(product.getDescription())
                .isCancellable(product.getIsCancellable())
                .isReturnable(product.getIsReturnable())
                .category(categoryDto)
                .build();
    }

    public List<Long> getCategoryAndDescendants(Long categoryId, CategoryRepository categoryRepository) {
        List<Long> result = new ArrayList<>();
        collectIds(categoryId, categoryRepository, result);
        return result;
    }

    private void collectIds(Long categoryId, CategoryRepository categoryRepository, List<Long> result) {
        result.add(categoryId);
        List<Category> children = categoryRepository.findByParentCategory_Id(categoryId);
        for (Category child : children) {
            collectIds(child.getId(), categoryRepository, result);
        }
    }
}
