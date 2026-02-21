package com.nikhil.ecommerce_backend.repositories.specifications;

import com.nikhil.ecommerce_backend.entities.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProductSpecification {

    public Specification<Product> hasSellerId(Long sellerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("seller").get("id"), sellerId);
    }

    public Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public Specification<Product> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public Specification<Product> isDeleted(Boolean isDeleted) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isDeleted"), isDeleted);
    }

    public Specification<Product> hasSellerEmail(String sellerEmail) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("seller").get("email"), sellerEmail);
    }

    public Specification<Product> matchesQuery(String queryText) {
        return (root, query, criteriaBuilder) -> {
            if (queryText == null || queryText.isBlank()) {
                return criteriaBuilder.conjunction(); // no filter
            }
            String likePattern = "%" + queryText.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), likePattern)
            );
        };
    }
}