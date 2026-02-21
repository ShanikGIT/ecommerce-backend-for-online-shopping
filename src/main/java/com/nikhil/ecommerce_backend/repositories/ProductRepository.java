package com.nikhil.ecommerce_backend.repositories;
import com.nikhil.ecommerce_backend.dto.customer.PriceRange;
import com.nikhil.ecommerce_backend.entities.Category;
import com.nikhil.ecommerce_backend.entities.Product;
import com.nikhil.ecommerce_backend.entities.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product>
{
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :productId")
    Optional<Product> findByIdAndFetchCategory(Long productId);

    boolean existsByNameAndBrandAndCategoryAndSellerAndIdNot(
            String name, String brand, Category category, Seller seller, Long productId
    );

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.variations v " +
            "LEFT JOIN FETCH v.secondaryImages " +
            "WHERE p.id = :productId AND p.isActive = true AND p.isDeleted = false")
    Optional<Product> findActiveProductForCustomer(Long productId);

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN FETCH p.category " +
            "JOIN p.variations v " +
            "WHERE p.category.id IN :categoryIds " +
            "AND p.isActive = true AND p.isDeleted = false " +
            "AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> findActiveProductsByCategories(List<Long> categoryIds, String query, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN FETCH p.category " +
            "JOIN p.variations v " +
            "WHERE p.category.id = :categoryId " +
            "AND p.id <> :excludeProductId " +
            "AND p.isActive = true AND p.isDeleted = false")
    Page<Product> findSimilarProducts(Long categoryId, Long excludeProductId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.variations v " +
            "WHERE p.id = :productId")
    Optional<Product> findByIdForAdmin(Long productId);

    @Query("SELECT DISTINCT p.brand FROM Product p " +
            "WHERE p.category.id IN :categoryIds AND p.isActive = true AND p.isDeleted = false " +
            "ORDER BY p.brand ASC")
    List<String> findDistinctBrandsByCategoryIdIn(List<Long> categoryIds);

    @Query("SELECT new com.nikhil.ecommerce_backend.dto.customer.PriceRange(MIN(pv.price), MAX(pv.price)) " +
            "FROM ProductVariation pv JOIN pv.product p " +
            "WHERE p.category.id IN :categoryIds AND p.isActive = true AND p.isDeleted = false")
    PriceRange findPriceRangeByCategoryIdIn(List<Long> categoryIds);

    @Query("SELECT p FROM Product p where p.isActive=false and p.isDeleted = false")
    List<Product> findInactiveProducts();
}
