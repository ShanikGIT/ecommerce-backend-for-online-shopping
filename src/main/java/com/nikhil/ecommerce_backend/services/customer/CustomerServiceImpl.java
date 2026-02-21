package com.nikhil.ecommerce_backend.services.customer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.ecommerce_backend.dto.customer.*;
import com.nikhil.ecommerce_backend.dto.general.UpdatePasswordRequest;
import com.nikhil.ecommerce_backend.entities.*;
import com.nikhil.ecommerce_backend.exceptions.PasswordMismatchException;
import com.nikhil.ecommerce_backend.exceptions.ResourceNotFoundException;
import com.nikhil.ecommerce_backend.repositories.CategoryRepository;
import com.nikhil.ecommerce_backend.repositories.CustomerRepository;
import com.nikhil.ecommerce_backend.repositories.ProductRepository;
import com.nikhil.ecommerce_backend.services.ProductServiceCommon;
import com.nikhil.ecommerce_backend.services.common.EmailService;
import com.nikhil.ecommerce_backend.services.common.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductServiceCommon productServiceCommon;
    private final ObjectMapper objectMapper;


    private Customer findCustomerByEmail(String email)
    {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("customer.profile.not.found"));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getCustomerProfile(String email)
    {
        Customer customer = findCustomerByEmail(email);
        String imageUrl = null;

        Optional<String> imageNameOpt = fileStorageService.findUserImageName(customer.getId());

        if (imageNameOpt.isPresent()) {
            imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/users/")
                    .path(imageNameOpt.get())
                    .toUriString();
        }
        return CustomerProfileResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .isActive(customer.isActive())
                .contact(customer.getContact())
                .imageUrl(imageUrl)
                .build();
    }

    @Override
    public List<AddressResponse> getCustomerAddresses(String email) {
        Customer customer = findCustomerByEmail(email);
        return customer.getAddresses().stream()
                .map(this::getAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updateCustomerProfile(String email, UpdateCustomerProfileRequest request, MultipartFile imageFile) {
        Customer customer = findCustomerByEmail(email);

        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getContact() != null) {
            customer.setContact(request.getContact());
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            fileStorageService.storeUserImage(imageFile, customer.getId());
        }
        customerRepository.save(customer);

        return "customer.profile.update.success";
    }

    @Override
    @Transactional
    public String updateCustomerPassword(String email, UpdatePasswordRequest request, Locale locale) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("password.mismatch");
        }

        String oldPassword = customerRepository.getPassword(email);

        if (passwordEncoder.matches(request.getPassword(), oldPassword)) {
            throw new IllegalArgumentException("last.password");
        }
        Customer customer = findCustomerByEmail(email);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customerRepository.save(customer);
        emailService.sendPasswordChangedEmail(customer.getEmail(), locale);
        return "customer.password.update.success";
    }

    @Override
    @Transactional
    public String addCustomerAddress(String email, AddAddressRequest request,Locale locale) {
        Customer customer = findCustomerByEmail(email);
        Address newAddress = new Address();
        newAddress.setAddressLine(request.getAddressLine());
        newAddress.setCity(request.getCity());
        newAddress.setState(request.getState());
        newAddress.setCountry(request.getCountry());
        newAddress.setZipCode(request.getZipCode());
        newAddress.setLabel(request.getLabel());
        newAddress.setCustomer(customer);
        customer.getAddresses().add(newAddress);
        customerRepository.save(customer);
        customer.getAddresses().get(customer.getAddresses().size() - 1);
        return "customer.address.add.success";
    }

    @Override
    @Transactional
    public String deleteCustomerAddress(String email, Long addressId) {
        Customer customer = findCustomerByEmail(email);

        boolean deleted = false;

        for(Address address : customer.getAddresses()) {
            if(address.getId().equals(addressId)) {
                address.setRemoved(true);
                deleted = true;
            }
        }
        if (!deleted) {
            throw new ResourceNotFoundException("customer.address.not.found");
        }
        customerRepository.save(customer);
        return "customer.address.delete.success";
    }

    @Override
    @Transactional
    public String updateCustomerAddress(String email, Long addressId, UpdateAddressRequest request) {
        Customer customer = findCustomerByEmail(email);
        Address addressToUpdate = customer.getAddresses().stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("customer.address.not.found"));

        if (request.getAddressLine() != null) {
            addressToUpdate.setAddressLine(request.getAddressLine());
        }
        if (request.getCity() != null) {
            addressToUpdate.setCity(request.getCity());
        }
        if (request.getState() != null) {
            addressToUpdate.setState(request.getState());
        }
        if (request.getCountry() != null) {
            addressToUpdate.setCountry(request.getCountry());
        }
        if (request.getZipCode() != null) {
            addressToUpdate.setZipCode(request.getZipCode());
        }
        if (request.getLabel() != null) {
            addressToUpdate.setLabel(request.getLabel());
        }
        customerRepository.save(customer);
        return "customer.address.update.success";
    }


    private AddressResponse getAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .label(address.getLabel().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryView> getCategories(Long categoryId) {
        List<Category> categories;

        if (categoryId!=null)
        {
            if (!categoryRepository.existsById(categoryId))
            {
                throw new ResourceNotFoundException("category.not.found");
            }
            categories = categoryRepository.findByParentCategory_Id(categoryId);
        }

        else
        {
            categories = categoryRepository.findRootCategories();
        }

        return categories.stream()
                .map(c -> new CategoryView(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProductView getProductForCustomer(Long productId) {
        Product product = productRepository.findActiveProductForCustomer(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

        if (product.getVariations() == null || product.getVariations().isEmpty()) {
            throw new ResourceNotFoundException("product.has.no.variations");
        }

        return productServiceCommon.toCustomerDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerProductList> getAllProductsByCategory(Long categoryId, String query,
                                                              int max, int offset, String sort, String order) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("category.not.found");
        }

        List<Long> categoryIds = productServiceCommon.getCategoryAndDescendants(categoryId, categoryRepository);
        Pageable pageable = productServiceCommon.buildPageable(max, offset, sort, order);

        Page<Product> productPage = productRepository.findActiveProductsByCategories(categoryIds, query, pageable);
        return productPage.map(productServiceCommon::toCustomerListDto);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<CustomerProductList> getSimilarProducts(Long productId, int max, int offset, String sort, String order)
    {

        Product referenceProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));


        if (!referenceProduct.getIsActive() || referenceProduct.getIsDeleted()) {
            throw new ResourceNotFoundException("product.not.found");
        }

        Long categoryId = referenceProduct.getCategory().getId();


        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(offset, max, sortable);


        Page<Product> similarProductsPage = productRepository.findSimilarProducts(categoryId, productId, pageable);


        return similarProductsPage.map(this::buildProductListDto);
    }

    private CustomerProductList buildProductListDto(Product product) {
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
                            .primaryImage(productServiceCommon.buildImageUrl(variation.getPrimaryImageName()))
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

    @Override
    @Transactional(readOnly = true)
    public CategoryFilter getFilteringDetailsForCategory(Long categoryId) {
        Category requestedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));

        List<Long> categoryIds = getCategoryAndAllDescendantIds(requestedCategory);

        List<String> brands = productRepository.findDistinctBrandsByCategoryIdIn(categoryIds);

        PriceRange priceRange = productRepository.findPriceRangeByCategoryIdIn(categoryIds);

        CategoryFilter.PriceRange priceRangeDto = CategoryFilter.PriceRange.builder()
                .minPrice(priceRange.getMinPrice())
                .maxPrice(priceRange.getMaxPrice())
                .build();
        return CategoryFilter.builder()
                .brands(brands)
                .priceRange(priceRangeDto)
                .build();
    }

    private List<Long> getCategoryAndAllDescendantIds(Category category) {
        List<Long> ids = new ArrayList<>();
        collectCategoryIds(category, ids);
        return ids;
    }

    private void collectCategoryIds(Category category, List<Long> ids) {
        ids.add(category.getId());
        List<Category> children = categoryRepository.findByParentCategory_Id(category.getId());
        for (Category child : children) {
            collectCategoryIds(child, ids);
        }
    }
}