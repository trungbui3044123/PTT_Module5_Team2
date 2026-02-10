package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.ProductRequest;
import com.module5.team2.dto.response.ProductResponse;
import com.module5.team2.entity.ProductEntity;
import com.module5.team2.entity.ProductImageEntity;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.ProductStatus;
import com.module5.team2.exception.ResourceNotFoundException;
import com.module5.team2.repository.ProductRepository;
import com.module5.team2.service.CloudinaryService;
import com.module5.team2.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class ProductServiceImpl implements ProductService
{
    private final ProductRepository productRepository;

    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest request, MultipartFile[] files, UserEntity supplier) throws IOException {
        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .status(ProductStatus.ACTIVE)
                .supplier(supplier)
                .build();
        // Xử lý ảnh
        List<ProductImageEntity> images = new ArrayList<>();
        // TODO: Gioi han file upload size
        if (files != null && files.length > 0) {
            for(MultipartFile file : files) {
                Map result = cloudinaryService.upload(file);
                ProductImageEntity img = new ProductImageEntity();
                img.setImageUrl(result.get("url").toString());
                img.setPublicId(result.get("public_id").toString());
                img.setProduct(product);
                images.add(img);
            }
        }

        product.setImages(images);

        ProductEntity savedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .category(product.getCategory())
                .price(savedProduct.getPrice())
                .quantity(savedProduct.getQuantity())
                .description(savedProduct.getDescription())
                .status(savedProduct.getStatus().name())
                .imageUrls(savedProduct.getImages().stream().map(ProductImageEntity::getImageUrl).toList())
                .build();
    }

    @Override
    public Page<ProductResponse> getMyProducts(UserEntity supplier,
                                               String keyword,
                                               Pageable pageable) {
        Page<ProductEntity> page;

        if (keyword != null && !keyword.trim().isEmpty()) {
            page = productRepository.findBySupplierIdAndNameContainingIgnoreCase(
                    supplier.getId(),
                    keyword,
                    pageable
            );
        } else {
            page = productRepository.findBySupplierId(
                    supplier.getId(),
                    pageable
            );
        }

        return page.map(product -> ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .status(product.getStatus().name())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .imageUrls(
                        product.getImages()
                                .stream()
                                .map(ProductImageEntity::getImageUrl)
                                .toList()
                )
                .build());
    }

    @Override
    public ProductResponse getProductDetail(Integer id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với ID: " + id));

        // Mapping thủ công Entity -> Response
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .category(product.getCategory())
                .status(product.getStatus().name())
                .imageUrls(product.getImages().stream().map(ProductImageEntity::getImageUrl).toList())
                .build();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductRequest request, MultipartFile[] files, ProductStatus status) throws IOException {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm để cập nhật"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());

        if (status != null) {
            product.setStatus(status);
        }

        if (files != null && files.length > 0) {
            // Xóa danh sách ảnh cũ trong Entity (orphanRemoval sẽ tự xóa trong DB)
            product.getImages().clear();

            for (MultipartFile file : files) {
                Map result = cloudinaryService.upload(file);
                ProductImageEntity img = new ProductImageEntity();
                img.setImageUrl(result.get("url").toString());
                img.setPublicId(result.get("public_id").toString());
                img.setProduct(product);
                product.getImages().add(img);
            }
        }

        ProductEntity saved = productRepository.save(product);

        return ProductResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .price(saved.getPrice())
                .quantity(saved.getQuantity())
                .description(saved.getDescription())
                .status(saved.getStatus().name())
                .category(saved.getCategory())
                .imageUrls(saved.getImages().stream().map(ProductImageEntity::getImageUrl).toList())
                .build();
    }
}

