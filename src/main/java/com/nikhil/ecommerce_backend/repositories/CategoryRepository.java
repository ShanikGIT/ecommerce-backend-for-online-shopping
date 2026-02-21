package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.Category;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCaseAndParentCategoryIsNull(String name);

    boolean existsByNameIgnoreCaseAndParentCategory_Id(String name, Long parentId);
    List<Category> findByParentCategory_Id(Long parentId);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c1 FROM Category c1 " +
            "WHERE c1.id NOT IN " +
            "(SELECT DISTINCT c2.parentCategory.id FROM Category c2 WHERE c2.parentCategory IS NOT NULL)")
    Page<Category> findLeafCategories(Pageable pageable);

    @Query("SELECT COUNT(c) = 0 FROM Category c WHERE c.parentCategory.id = :categoryId")
    boolean isLeafCategory(@Param("categoryId") Long categoryId);


    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id IN :parentIds")
    List<Category> findByParentCategoryIdIn(List<Long> parentIds);


}
