package com.foodorder.product.controller;

import com.foodorder.product.dto.ProductResponse;
import com.foodorder.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product Controller
 * REST API endpoints for product queries
 */
@RestController
@RequestMapping("/api/products")
@Validated
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /products
     * Retrieve all products with stock information
     * @return List of ProductResponse with 200 OK
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("API call: GET /products");
        List<ProductResponse> products = productService.getAllProducts();
        log.info("Returning {} products", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * GET /products/{id}
     * Retrieve product by ID with stock information
     * @param id product id
     * @return ProductResponse with 200 OK, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("API call: GET /products/{}", id);

        if (id == null || id <= 0) {
            log.warn("Invalid product id: {}", id);
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }

        ProductResponse product = productService.getProductById(id);
        log.info("Returning product with id: {}", id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Health check endpoint
     * @return status message
     */
    @GetMapping("/health/check")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check called");
        return new ResponseEntity<>("Product Service is running", HttpStatus.OK);
    }
}
