package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.ProductVariation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
    Optional<ProductVariation> findFirstByProductId(Long productId);
    @Query("SELECT pv FROM ProductVariation pv JOIN FETCH pv.product p JOIN FETCH p.seller WHERE pv.id = :variationId")
    Optional<ProductVariation> findByIdAndFetchProductAndSeller(Long variationId);
    Page<ProductVariation> findByProductId(Long productId, Pageable pageable);
}
