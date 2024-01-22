package com.m3sysoft.productservice.controller.service;

import com.m3sysoft.productservice.dto.ProductRequest;
import com.m3sysoft.productservice.dto.ProductResponse;
import com.m3sysoft.productservice.model.Product;
import com.m3sysoft.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);

        log.info("product {} is saved", product.getId());

    }

    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        List<ProductResponse> list;

        list = products.stream()
                .map(this::mapToProductResponse)
                .toList();

        return list;


    }

    private ProductResponse mapToProductResponse(Product product) {

        ProductResponse productResponse;
        productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();

        return productResponse;
    }
}
