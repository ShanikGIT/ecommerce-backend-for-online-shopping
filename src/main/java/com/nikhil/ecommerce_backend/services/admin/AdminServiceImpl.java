package com.nikhil.ecommerce_backend.services.admin;
import com.nikhil.ecommerce_backend.dto.admin.*;
import com.nikhil.ecommerce_backend.entities.*;
import com.nikhil.ecommerce_backend.exceptions.ResourceAlreadyExistsException;
import com.nikhil.ecommerce_backend.exceptions.ResourceNotFoundException;
import com.nikhil.ecommerce_backend.repositories.*;
import com.nikhil.ecommerce_backend.repositories.specifications.ProductSpecification;
import com.nikhil.ecommerce_backend.services.ProductServiceCommon;
import com.nikhil.ecommerce_backend.services.common.EmailService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSpecification productSpecification;
    private final ProductServiceCommon productServiceCommon;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseForAdmin> getAllCustomers(int page, int size, String sort, String email) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        Page<Customer> customerPage;

        if (email != null && !email.isEmpty()) {
            customerPage = customerRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        return customerPage.map(this::mapToCustomerResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerResponseForAdmin> getAllSellers(int page, int size, String sort, String email) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        Page<Seller> sellerPage;

        if (email != null && !email.isEmpty()) {
            sellerPage = sellerRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            sellerPage = sellerRepository.findAll(pageable);
        }
        return sellerPage.map(this::mapToSellerResponse);
    }

    private CustomerResponseForAdmin mapToCustomerResponse(Customer customer) {

        if (customer == null) {
            throw new ResourceNotFoundException("customer.user.not.found");
        }

        return CustomerResponseForAdmin.builder()
                .id(customer.getId())
                .fullName(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .isActive(customer.isActive())
                .build();
    }

    private SellerResponseForAdmin mapToSellerResponse(Seller seller) {
        if (seller == null) {
            throw new ResourceNotFoundException("customer.user.not.found");
        }
        return SellerResponseForAdmin.builder()
                .id(seller.getId())
                .fullName(seller.getFirstName() + " " + seller.getLastName())
                .email(seller.getEmail())
                .isActive(seller.isActive())
                .companyName(seller.getCompanyName())
                .companyContact(seller.getCompanyContact())
                .build();
    }

    @Override
    public String activateCustomer(Long customerId, Locale locale) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer.user.not.found"));

        if (customer.isActive()) {
            return "admin.customer.already.activated";
        }

        customer.setActive(true);
        customerRepository.save(customer);

        emailService.sendAccountActivatedByAdminEmail(customer.getEmail(), locale);

        return "admin.customer.activate.success";
    }

    @Override
    public String deactivateCustomer(Long customerId, Locale locale) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer.user.not.found"));

        if (!customer.isActive()) {
            return "admin.customer.already.deactivated";
        }

        customer.setActive(false);
        customerRepository.save(customer);

        emailService.sendAccountDeactivatedByAdminEmail(customer.getEmail(), locale);

        return "admin.customer.deactivate.success";
    }

    @Override
    public String activateSeller(Long sellerId, Locale locale) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("seller.user.not.found"));

        if (seller.isActive()) {
            return "admin.seller.already.activated";
        }

        seller.setActive(true);
        sellerRepository.save(seller);

        emailService.sendAccountActivatedByAdminEmail(seller.getEmail(), locale);

        return "admin.seller.activate.success";
    }

    @Override
    public String deactivateSeller(Long sellerId, Locale locale) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("seller.user.not.found"));

        if (!seller.isActive()) {
            return "admin.seller.already.deactivated";
        }

        seller.setActive(false);
        sellerRepository.save(seller);

        emailService.sendAccountDeactivatedByAdminEmail(seller.getEmail(), locale);

        return "admin.seller.deactivate.success";
    }

    @Override
    public String addMetadataField(String fieldName, Locale locale) {

        if (fieldName == null || fieldName.trim().equals("\"\""))
        {
            throw new IllegalArgumentException("fieldName.blank");
        }

        if (categoryMetadataFieldRepository.existsByNameIgnoreCase(fieldName)) {
            throw new ResourceAlreadyExistsException("metadata.field.name.exists");
        }

        CategoryMetadataField newField = new CategoryMetadataField();

        newField.setName(fieldName);

        CategoryMetadataField savedField = categoryMetadataFieldRepository.save(newField);

        return messageSource.getMessage("category.metadata.field.added", new Object[]{savedField.getId()}, locale);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MetadataFieldResponse> getAllMetadataFields(int max, int offset, String sort, String order, String query) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sorting = Sort.by(direction, sort);

        Pageable pageable = PageRequest.of(offset, max, sorting);

        Page<CategoryMetadataField> metadataFieldPage;

        if (query != null) {
            metadataFieldPage = categoryMetadataFieldRepository.findByNameContainingIgnoreCase(query, pageable);
        } else {
            metadataFieldPage = categoryMetadataFieldRepository.findAll(pageable);
        }

        return metadataFieldPage.map(cmf ->
                MetadataFieldResponse.builder()
                        .id(cmf.getId())
                        .name(cmf.getName())
                        .build()
        );
    }

    @Override
    @Transactional
    public String addCategory(String name, Long parentId, Locale locale) {

        Category parent = null;

        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("category.parent.not.found"));
        }

        Category currentAncestor = parent;

        while (currentAncestor != null) {
            if (currentAncestor.getName().equalsIgnoreCase(name)) {
                throw new ResourceAlreadyExistsException("category.name.exists.in.hierarchy");
            }
            currentAncestor = currentAncestor.getParentCategory();
        }

        boolean ifExists;

        if (parent == null) {
            ifExists = categoryRepository.existsByNameIgnoreCaseAndParentCategoryIsNull(name);
        } else {
            ifExists = categoryRepository.existsByNameIgnoreCaseAndParentCategory_Id(name, parentId);
        }

        if (ifExists) {
            throw new ResourceAlreadyExistsException("category.name.exists.in.location");
        }

        Category newCategory = new Category();

        newCategory.setName(name);

        newCategory.setParentCategory(parent);

        Category savedCategory = categoryRepository.save(newCategory);
        return messageSource.getMessage(
                "category.add.success",
                new Object[]{savedCategory.getId()},
                locale
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));

        List<Category> children = categoryRepository.findByParentCategory_Id(id);

        return mapToCategoryResponse(category, children);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(int max, int offset, String sort, String order, String query) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        Page<Category> categoriesOnPage = (query != null && !query.isBlank())
                ? categoryRepository.findByNameContainingIgnoreCase(query, pageable)
                : categoryRepository.findAll(pageable);

        if (categoriesOnPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> categoryIdsOnPage = categoriesOnPage.stream().map(Category::getId).toList();
        List<Category> allChildren = categoryRepository.findByParentCategoryIdIn(categoryIdsOnPage);

        Map<Long, List<Category>> childrenByParentId = allChildren.stream()
                .collect(Collectors.groupingBy(c -> c.getParentCategory().getId()));

        return categoriesOnPage.map(category -> {
            List<Category> children = childrenByParentId.getOrDefault(category.getId(), Collections.emptyList());
            return mapToCategoryResponse(category, children);
        });
    }

    private CategoryResponse mapToCategoryResponse(Category category, List<Category> children) {
        List<CategorySummary> parents = new ArrayList<>();
        Category currentParent = category.getParentCategory();
        while (currentParent != null) {
            parents.add(new CategorySummary(currentParent.getId(), currentParent.getName()));
            currentParent = currentParent.getParentCategory();
        }
        Collections.reverse(parents);

        List<CategorySummary> childrenDtos = children.stream()
                .map(child -> new CategorySummary(child.getId(), child.getName()))
                .toList();

        Map<String, List<String>> metadata = category.getCategoryMetadataFieldVales()
                .stream()
                .collect(Collectors.toMap(
                        cmfv -> cmfv.getCategoryMetaDataField().getName(),
                        CategoryMetadataFieldValues::getValue
                ));

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setParents(parents);
        response.setImmediateChildren(childrenDtos);
        response.setMetadata(metadata);

        return response;
    }

    @Override
    public String updateCategory(Long categoryId, String name, Locale locale) {
        Category category = null;

        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));
        }

        Category currentAncestor = category;
        while (currentAncestor != null) {
            if (currentAncestor.getName().equalsIgnoreCase(name)) {
                throw new ResourceAlreadyExistsException("category.name.exists.in.hierarchy");
            }
            currentAncestor = currentAncestor.getParentCategory();
        }

        boolean ifExists = false;
        if (category != null) {
            if (category.getParentCategory() != null) {
                ifExists = categoryRepository.existsByNameIgnoreCaseAndParentCategory_Id(
                        name, category.getParentCategory().getId()
                );
            } else {
                ifExists = categoryRepository.existsByNameIgnoreCaseAndParentCategory_Id(name, null);
            }
        }

        if (ifExists) {
            throw new ResourceAlreadyExistsException("category.name.exists.in.location");
        }
        if (hasChildWithSameName(category.getId(), name)) {
            throw new ResourceAlreadyExistsException("category.name.exists.in.hierarchy");

        }

        category.setName(name);
        categoryRepository.save(category);

        return messageSource.getMessage(
                "category.update.success",
                new Object[]{category.getId()},
                locale
        );
    }

    private boolean hasChildWithSameName(Long categoryId, String name) {
        List<Category> children = categoryRepository.findByParentCategory_Id(categoryId);

        for (Category child : children) {
            if (child.getName().equalsIgnoreCase(name)) {
                return true;
            }
            if (hasChildWithSameName(child.getId(), name)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public String addCategoryMetadata(List<CategoryMetadataRequest> requests, Locale locale) {
        for (CategoryMetadataRequest req : requests) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));

            for (MetadataFieldRequest metadata : req.getMetadata()) {
                CategoryMetadataField field = categoryMetadataFieldRepository.findById(metadata.getMetadataFieldId())
                        .orElseThrow(() -> new ResourceNotFoundException("metadata.field.not.found"));

                List<String> cleanedValues = Optional.ofNullable(metadata.getValues())
                        .orElseThrow(() -> new IllegalArgumentException("metadata.values.required"))
                        .stream()
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .toList();

                if (cleanedValues.isEmpty()) {
                    throw new IllegalArgumentException("metadata.values.required");
                }


                Set<String> uniqueValues = new HashSet<>(metadata.getValues());
                if (uniqueValues.size() != metadata.getValues().size()) {
                    throw new IllegalArgumentException("metadata.values.duplicate");
                }

                Optional<CategoryMetadataFieldValues> existing =
                        categoryMetadataFieldValuesRepository.findByCategoryAndCategoryMetaDataField(category, field)
                                .stream().findFirst();

                if (existing.isPresent()) {
                    throw new IllegalArgumentException("metadata.already.exists.for.category.and.field");
                }

                CategoryMetadataFieldValues cmfv = new CategoryMetadataFieldValues();
                cmfv.setCategory(category);
                cmfv.setCategoryMetaDataField(field);
                cmfv.setValue(new ArrayList<>(uniqueValues));
                categoryMetadataFieldValuesRepository.save(cmfv);
            }
        }
        return messageSource.getMessage("category.metadata.add.success", null, locale);
    }

    @Transactional
    @Override
    public String updateCategoryMetadata(List<CategoryMetadataRequest> requests, Locale locale) {
        for (CategoryMetadataRequest req : requests) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));

            for (MetadataFieldRequest metadata : req.getMetadata()) {
                CategoryMetadataField field = categoryMetadataFieldRepository.findById(metadata.getMetadataFieldId())
                        .orElseThrow(() -> new ResourceNotFoundException("metadata.field.not.found"));

                List<CategoryMetadataFieldValues> existingValues =
                        categoryMetadataFieldValuesRepository.findByCategoryAndCategoryMetaDataField(category, field);
                if (metadata.getValues() == null || metadata.getValues().isEmpty()) {
                    throw new IllegalArgumentException("metadata.values.required");
                }
                boolean hasInvalid = metadata.getValues().stream()
                        .anyMatch(v -> v == null || v.trim().isEmpty());
                if (hasInvalid) {
                    throw new IllegalArgumentException("metadata.values.required");
                }

                List<String> cleanedValues = metadata.getValues().stream()
                        .map(String::trim)
                        .toList();

                CategoryMetadataFieldValues cmfv;
                if (existingValues.isEmpty()) {
                    cmfv = new CategoryMetadataFieldValues();
                    cmfv.setCategory(category);
                    cmfv.setCategoryMetaDataField(field);
                } else {
                    cmfv = existingValues.get(0);
                }

                Set<String> mergedValues = new HashSet<>();
                if (cmfv.getValue() != null) {
                    mergedValues.addAll(cmfv.getValue());
                }
                mergedValues.addAll(cleanedValues);

                cmfv.setValue(new ArrayList<>(mergedValues));
                categoryMetadataFieldValuesRepository.save(cmfv);
            }
        }

        return messageSource.getMessage("category.metadata.update.success", null, locale);
    }



    @Override
    @Transactional(readOnly = true)
    public AdminProductView getProductForAdmin(Long productId) {
        Product product = productRepository.findByIdForAdmin(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));
        return productServiceCommon.toAdminDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminProductView> getAllProducts(Long sellerId, Long categoryId, Boolean isActive,
                                                 Boolean isDeleted, int max, int offset, String sort, String order) {
        Specification<Product> spec = Specification.where(null);
        if (sellerId != null) spec = spec.and(productSpecification.hasSellerId(sellerId));
        if (categoryId != null) spec = spec.and(productSpecification.hasCategoryId(categoryId));
        if (isActive != null) spec = spec.and(productSpecification.isActive(isActive));
        if (isDeleted != null) spec = spec.and(productSpecification.isDeleted(isDeleted));

        Pageable pageable = productServiceCommon.buildPageable(max, offset, sort, order);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(productServiceCommon::toAdminDto);
    }

    @Override
    @Transactional
    public String deactivateProduct(Long productId,Locale locale) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));


        if (!product.getIsActive()) {
            throw new IllegalArgumentException("product.already.inactive");
        }

        product.setIsActive(false);
        productRepository.save(product);


        emailService.sendProductDeactivationEmail(product, locale);

        return messageSource.getMessage("product.deactivate.success", null, locale);

    }

    @Override
    @Transactional
    public String activateProduct(Long productId,Locale locale) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));


        if (product.getIsActive()) {
            throw new IllegalArgumentException("product.already.active");
        }

        product.setIsActive(true);
        productRepository.save(product);


        emailService.sendProductActivationEmail(product, locale);

        return messageSource.getMessage("product.activate.success", null, locale);

    }

}
