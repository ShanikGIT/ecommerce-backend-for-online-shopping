package com.nikhil.ecommerce_backend.services.seller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.ecommerce_backend.dto.seller.ProductResponse;
import com.nikhil.ecommerce_backend.dto.seller.*;
import com.nikhil.ecommerce_backend.dto.general.UpdatePasswordRequest;
import com.nikhil.ecommerce_backend.entities.*;
import com.nikhil.ecommerce_backend.exceptions.PasswordMismatchException;
import com.nikhil.ecommerce_backend.exceptions.ResourceNotFoundException;
import com.nikhil.ecommerce_backend.repositories.*;
import com.nikhil.ecommerce_backend.repositories.specifications.ProductSpecification;
import com.nikhil.ecommerce_backend.services.ProductServiceCommon;
import com.nikhil.ecommerce_backend.services.common.EmailService;
import com.nikhil.ecommerce_backend.services.common.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final FileStorageService fileStorageService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    private final ProductVariationRepository  productVariationRepository;
    private final ObjectMapper objectMapper;
    private final ProductSpecification  productSpecification;
    private final ProductServiceCommon productServiceCommon;

    private Seller findSellerByEmail(String email) {
        if (sellerRepository.existsByEmail(email)) {
            return sellerRepository.findByEmail(email);
        } else {
            throw new ResourceNotFoundException("seller.profile.not.found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResponse getSellerProfile(String sellerEmail, Locale locale) {
        Seller seller = findSellerByEmail(sellerEmail);
        Address address = seller.getAddress();
        String imageUrl = null;
        Optional<String> imageNameOpt = fileStorageService.findUserImageName(seller.getId());
        if (imageNameOpt.isPresent()) {
            imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/users/")
                    .path(imageNameOpt.get())
                    .toUriString();
        }
        return SellerProfileResponse.builder()
                .id(seller.getId())
                .firstName(seller.getFirstName())
                .lastName(seller.getLastName())
                .isActive(seller.isActive())
                .companyName(seller.getCompanyName())
                .companyContact(seller.getCompanyContact())
                .imageUrl(imageUrl)
                .gst(seller.getGst())
                .addressLine(address != null ? address.getAddressLine() : null)
                .city(address != null ? address.getCity() : null)
                .state(address != null ? address.getState() : null)
                .country(address != null ? address.getCountry() : null)
                .zipCode(address != null ? address.getZipCode() : null)
                .build();
    }

    @Override
    @Transactional
    public String updateSellerProfile(String sellerEmail, UpdateSellerProfileRequest request, MultipartFile imageFile) {
        Seller seller = findSellerByEmail(sellerEmail);
        if (request.getFirstName() != null) {
            seller.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            seller.setLastName(request.getLastName());
        }
        if (request.getCompanyName() != null) {
            seller.setCompanyName(request.getCompanyName());
        }
        if (request.getCompanyContact() != null) {
            seller.setCompanyContact(request.getCompanyContact());
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            fileStorageService.storeUserImage(imageFile, seller.getId());
        }
        sellerRepository.save(seller);
        return "seller.profile.update.success";
    }

    @Override
    @Transactional
    public String updateSellerPassword(String sellerEmail, UpdatePasswordRequest request, Locale locale) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException(
                    messageSource.getMessage("password.mismatch", null, locale));
        }
        String oldPassword = sellerRepository.getPassword(sellerEmail);

        if (passwordEncoder.matches(request.getPassword(), oldPassword)) {
            throw new IllegalArgumentException("last.password");
        }
        Seller seller = findSellerByEmail(sellerEmail);
        seller.setPassword(passwordEncoder.encode(request.getPassword()));
        sellerRepository.save(seller);

        emailService.sendPasswordChangedEmail(seller.getEmail(), locale);

        return "seller.password.update.success";
    }

    @Override
    @Transactional
    public String updateSellerAddress(String sellerEmail, UpdateAddressRequest request, Locale locale) {

        Seller seller = findSellerByEmail(sellerEmail);

        Address address = seller.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setSeller(seller);
        seller.setAddress(address);

        if (request.getLabel()!=null) {
            address.setLabel(request.getLabel());
        }
        if(request.getAddressLine()!=null) {
            address.setAddressLine(request.getAddressLine());
        }
        if(request.getCity()!=null) {
            address.setCity(request.getCity());
        }
        if(request.getState()!=null) {
            address.setState(request.getState());
        }
        if(request.getCountry()!=null) {
            address.setCountry(request.getCountry());
        }
        if (request.getZipCode()!=null) {
            address.setZipCode(request.getZipCode());
        }
        sellerRepository.save(seller);

        return "seller.address.update.success";
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeafCategoryResponse> getAllLeafCategories(int max, int offset, String sort, String order) {

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        List<Category> leafCategories = categoryRepository.findLeafCategories(pageable).getContent();

        return new PageImpl<>(
                leafCategories.stream()
                        .map(this::mapCategoryToDto)
                        .toList(),
                pageable,
                leafCategories.size()
        );
    }

    private LeafCategoryResponse mapCategoryToDto(Category category) {

        Map<String, List<String>> metadataMap = category.getCategoryMetadataFieldVales()
                .stream()
                .collect(Collectors.toMap(
                        fv -> fv.getCategoryMetaDataField().getName(),
                        CategoryMetadataFieldValues::getValue
                ));

        List<CategoryFieldResponse> metadataFields = metadataMap.entrySet().stream()
                .map(entry -> new CategoryFieldResponse(entry.getKey(), entry.getValue()))
                .toList();

        return new LeafCategoryResponse(
                category.getId(),
                category.getName(),
                getParentChain(category),
                metadataFields
        );
    }

    private List<String> getParentChain(Category category)
    {
        LinkedList<String> chain = new LinkedList<>();
        Category current = category;
        while (current != null) {
            chain.addFirst(current.getName());
            current = current.getParentCategory();
        }
        return chain;
    }

    @Override
    public String addProduct(String email, AddProductRequest request) {

        Seller seller = findSellerByEmail(email);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));

        if (!categoryRepository.isLeafCategory((category.getId())))
        {
            throw new IllegalArgumentException("category.not.leaf");
        }

        Product product = new Product();

        product.setName(request.getName());

        product.setBrand(request.getBrand());

        if(request.getDescription()!=null) {
            product.setDescription(request.getDescription());
        }
        else{
            product.setDescription(null);
        }

        product.setIsCancellable(Boolean.TRUE.equals(request.isCancellable()));
        product.setIsReturnable(Boolean.TRUE.equals(request.isReturnable()));

        product.setSeller(seller);
        product.setCategory(category);
        product.setIsActive(false);

        Product savedProduct = productRepository.save(product);

        emailService.sendProductActivationEmailToAdmin(savedProduct);

        return "product.added.success";
    }

    @Override
    @Transactional
    public String addProductVariation(String sellerEmail, AddVariationRequest request, MultipartFile primaryImage, List<MultipartFile> secondaryImages) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("error.forbidden.message");
        }

        if (!product.getIsActive() || product.getIsDeleted()) {
            throw new IllegalArgumentException("product.not.active");
        }

        if (request.getMetadata() == null || request.getMetadata().isEmpty()) {
            throw new IllegalArgumentException("metadata.required");
        }

        productVariationRepository.findFirstByProductId(product.getId()).ifPresent(firstVariation -> {
            Set<String> existingKeys = firstVariation.getMetadata().properties().stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            if (!existingKeys.equals(request.getMetadata().keySet())) {
                throw new IllegalArgumentException("metadata.structure.mismatch");
            }
        });

        Long categoryId = product.getCategory().getId();
        for (Map.Entry<String, String> entry : request.getMetadata().entrySet()) {
            if (categoryMetadataFieldValuesRepository.isValueValidForCategory(categoryId, entry.getKey(), entry.getValue()) == 0) {
                throw new IllegalArgumentException("metadata.value.invalid");
            }
        }

        String primaryImageName = fileStorageService.storeProductImage(primaryImage, "products");

        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setQuantityAvailable(request.getQuantityAvailable());
        variation.setPrice(request.getPrice());
        variation.setMetadata(objectMapper.valueToTree(request.getMetadata()));
        variation.setPrimaryImageName(primaryImageName);
        variation.setActive(true);
        ProductVariation savedVariation = productVariationRepository.save(variation);

        if (secondaryImages != null && !secondaryImages.isEmpty()) {
            Set<ProductImage> imageEntities = secondaryImages.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(imageFile -> {
                        String imageName = fileStorageService.storeProductImage(imageFile, "products");
                        return new ProductImage(savedVariation, imageName);
                    })
                    .collect(Collectors.toSet());

            if (!imageEntities.isEmpty()) {
                savedVariation.getSecondaryImages().clear();
                savedVariation.getSecondaryImages().addAll(imageEntities);
            }
        }
        return "product.variation.add.success";
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductForSeller(Long productId, String sellerEmail) {
        Product product = productRepository.findByIdAndFetchCategory(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

        if (product.getIsDeleted()) {
            throw new ResourceNotFoundException("product.not.found");
        }

        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("error.forbidden.message");
        }

        return productServiceCommon.toSellerDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariationResponse getProductVariationForSeller(Long variationId, String sellerEmail) {

        ProductVariation variation = productVariationRepository.findByIdAndFetchProductAndSeller(variationId)
                .orElseThrow(() -> new ResourceNotFoundException("product.variation.not.found"));

        Product parentProduct = variation.getProduct();
        if (parentProduct.getIsDeleted()) {
            throw new ResourceNotFoundException("product.variation.not.found");
        }

        if (!parentProduct.getSeller().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("error.forbidden.message");
        }

        ProductVariationResponse.ParentProduct parentProductDto = ProductVariationResponse.ParentProduct.builder()
                .id(parentProduct.getId())
                .name(parentProduct.getName())
                .brand(parentProduct.getBrand())
                .build();

        Map<String, Object> metadataMap = objectMapper.convertValue(variation.getMetadata(), new TypeReference<>() {});
        Optional<String> imageNameOpt = fileStorageService.findProductImage(variation.getPrimaryImageName());
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/products/")
                .path(imageNameOpt.get())
                .toUriString();

        return ProductVariationResponse.builder()
                .id(variation.getId())
                .quantityAvailable(variation.getQuantityAvailable())
                .price(variation.getPrice())
                .isActive(variation.isActive())
                .primaryImageUrl(imageUrl)
                .metadata(metadataMap)
                .product(parentProductDto)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsForSeller(String sellerEmail, String query,
                                                         int max, int offset, String sort, String order) {
        Pageable pageable = productServiceCommon.buildPageable(max, offset, sort, order);

        Specification<Product> spec = Specification.allOf(
                productSpecification.hasSellerEmail(sellerEmail),
                productSpecification.isDeleted(false),
                productSpecification.matchesQuery(query));

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(productServiceCommon::toSellerDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariationResponse> getAllProductVariations(Long productId, String sellerEmail, int max, int offset, String sort, String order, String query) {

        Product parentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

        if (parentProduct.getIsDeleted()) {
            throw new ResourceNotFoundException("product.not.found");
        }

        if (!parentProduct.getSeller().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("error.forbidden.message");
        }
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(offset, max, sortable);

        Page<ProductVariation> variationPage = productVariationRepository.findByProductId(productId, pageable);

        List<ProductVariationResponse> responseDtos = new ArrayList<>();
        ProductVariationResponse.ParentProduct parentDto = ProductVariationResponse.ParentProduct.builder()
                .id(parentProduct.getId())
                .name(parentProduct.getName())
                .brand(parentProduct.getBrand())
                .build();

        for (ProductVariation variation : variationPage.getContent()) {
            Map<String, Object> metadataMap = objectMapper.convertValue(variation.getMetadata(), new TypeReference<>() {});

            Optional<String> imageNameOpt = fileStorageService.findProductImage(variation.getPrimaryImageName());
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/products/")
                    .path(imageNameOpt.get())
                    .toUriString();

            ProductVariationResponse variationDto = ProductVariationResponse.builder()
                    .id(variation.getId())
                    .quantityAvailable(variation.getQuantityAvailable())
                    .price(variation.getPrice())
                    .isActive(variation.isActive())
                    .primaryImageUrl(imageUrl)
                    .metadata(metadataMap)
                    .product(parentDto)
                    .build();
            responseDtos.add(variationDto);
        }
        return new PageImpl<>(responseDtos, pageable, variationPage.getTotalElements());
    }
    @Override
    @Transactional
    public String deleteProduct(Long productId, String sellerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("error.forbidden.message");
        }

        if (product.getIsDeleted()) {
            return "product.already.deleted";
        }

        for(ProductVariation variation : product.getVariations()) {
            if(variation.getProduct().getId().equals(productId))
            {
                variation.setRemoved(true);
            }
        }

        product.setIsDeleted(true);
        product.setIsActive(false);
        productRepository.save(product);
        return "product.delete.success";
    }


    @Override
    @Transactional
    public String updateProduct(Long productId, UpdateProductRequest requestDto, String sellerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

        if(product.getIsDeleted())
        {
            throw new ResourceNotFoundException("product.not.found");
        }

        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("error.forbidden.message");
        }
        String newName = requestDto.getName();
        if (newName != null && !newName.isBlank() && !newName.equals(product.getName())) {
            boolean nameExists = productRepository.existsByNameAndBrandAndCategoryAndSellerAndIdNot(
                    newName, product.getBrand(), product.getCategory(), product.getSeller(), productId
            );
            if (nameExists) {
                throw new IllegalArgumentException("product.name.already.exists");
            }
            product.setName(newName);
        }
        if (requestDto.getDescription() != null)
        {
            product.setDescription(requestDto.getDescription());
        }
        if (requestDto.getIsCancellable() != null)
        {
            product.setIsCancellable(requestDto.getIsCancellable());
        }

        if (requestDto.getIsReturnable() != null)
        {
            product.setIsReturnable(requestDto.getIsReturnable());
        }
        productRepository.save(product);
        return "product.update.success";
    }

    @Override
    @Transactional
    public String updateProductVariation(Long variationId, UpdateVariationRequest request,
                                         MultipartFile primaryImage, List<MultipartFile> secondaryImages,
                                         String sellerEmail) {

        ProductVariation variation = productVariationRepository.findById(variationId)
                .orElseThrow(() -> new ResourceNotFoundException("product.variation.not.found"));

        Product product = variation.getProduct();

        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("error.forbidden.message");
        }
        if (product.getIsDeleted() || !product.getIsActive()) {
            throw new IllegalArgumentException("product.not.active");
        }

        if (request.getQuantityAvailable() != null) {
            variation.setQuantityAvailable(request.getQuantityAvailable());
        }
        if (request.getPrice() != null) {
            variation.setPrice(request.getPrice());
        }
        if (request.getIsActive() != null) {
            variation.setActive(request.getIsActive());
        }

        if (request.getMetadata() != null) {
            Long categoryId = product.getCategory().getId();
            for (Map.Entry<String, String> entry : request.getMetadata().entrySet()) {
                if (categoryMetadataFieldValuesRepository.isValueValidForCategory(categoryId, entry.getKey(), entry.getValue()) == 0) {
                    throw new IllegalArgumentException("metadata.value.invalid");
                }
            }
            variation.setMetadata(objectMapper.valueToTree(request.getMetadata()));
        }

        if (primaryImage != null && !primaryImage.isEmpty()) {
            String newPrimaryImageName = fileStorageService.storeProductImage(primaryImage, "products");
            variation.setPrimaryImageName(newPrimaryImageName);
        }

        if (secondaryImages != null && !secondaryImages.isEmpty()) {

            variation.getSecondaryImages().clear();

            Set<ProductImage> newImageEntities = secondaryImages.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(imageFile -> {
                        String imageName = fileStorageService.storeProductImage(imageFile, "products");
                        return new ProductImage(variation, imageName);
                    })
                    .collect(Collectors.toSet());
            variation.getSecondaryImages().addAll(newImageEntities);
        }
        productVariationRepository.save(variation);
        return "product.variation.update.success";
    }
}