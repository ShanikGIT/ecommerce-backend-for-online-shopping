package com.nikhil.ecommerce_backend.repositories;


import com.nikhil.ecommerce_backend.entities.Category;
import com.nikhil.ecommerce_backend.entities.CategoryMetadataField;
import com.nikhil.ecommerce_backend.entities.CategoryMetadataFieldValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues, Long>
{
    List<CategoryMetadataFieldValues> findByCategoryAndCategoryMetaDataField(Category category, CategoryMetadataField field);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM category_metadata_field_values cmfv " +
            "JOIN category_metadata_field cmf ON cmfv.category_metadata_field_id = cmf.id " +
            "WHERE cmfv.category_id = :categoryId " +
            "AND cmf.name = :fieldName " +
            "AND JSON_CONTAINS(cmfv.value, CAST(CONCAT('\"', :value, '\"') AS JSON)))",
            nativeQuery = true)
    Integer isValueValidForCategory(@Param("categoryId") Long categoryId,
                                    @Param("fieldName") String fieldName,
                                    @Param("value") String value);

    @Query("SELECT cmfv FROM CategoryMetadataFieldValues cmfv JOIN FETCH cmfv.categoryMetaDataField " +
            "WHERE cmfv.category.id IN :categoryIds")
    List<CategoryMetadataFieldValues> findAllByCategoryId(List<Long> categoryIds);
}

