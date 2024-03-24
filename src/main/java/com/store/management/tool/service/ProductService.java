package com.store.management.tool.service;

import com.store.management.tool.domain.Product;
import com.store.management.tool.exception.ResourceNotFoundException;
import com.store.management.tool.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Product add(Product product) {
        Product newProduct = productRepository.save(product);
        log.info("Successfully added new product {}.", product.getId());
        return newProduct;
    }

    public List<Product> getAll() {
        log.info("Retrieving all products.");
        return productRepository.findAll();
    }

    public Product getById(Long id) throws ResourceNotFoundException {
        log.info("Retrieving product by id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id %d not found", id)));
    }

    public Product update(Long id, Product updatedProduct) throws ResourceNotFoundException {
        log.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id %d not found", id)));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setQuantity(updatedProduct.getQuantity());

        return productRepository.save(product);
    }

    public void delete(Long id) throws ResourceNotFoundException {
        log.info("Deleting product with id: {}", id);
        Product productToDelete = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id %d not found", id)));

        productRepository.delete(productToDelete);
        log.info("User {} deleted successfully", id);
    }
}
