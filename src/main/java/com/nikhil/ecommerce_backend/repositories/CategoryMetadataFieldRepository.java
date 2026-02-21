package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, Long>
{
    boolean existsByNameIgnoreCase(String name);
    Page<CategoryMetadataField> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
