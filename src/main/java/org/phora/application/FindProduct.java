package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.Optional;

public class FindProduct {

    private final ProductRepository productRepository;

    public FindProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Optional<Product> execute(Integer id) {
        return this.productRepository.findById(id);
    }
}