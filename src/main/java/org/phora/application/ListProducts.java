package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.List;

public class ListProducts {

    private final ProductRepository productRepository;

    public ListProducts(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> execute() {
        return productRepository.findAll();
    }
}