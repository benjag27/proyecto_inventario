package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.List;

public class FindByName {

    private final ProductRepository productRepository;

    public FindByName(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Returns all products whose name contains the given text (case-insensitive).
     * For example, "papa" matches "Papa peruana", "Papa chips", "Papas fritas".
     */
    public List<Product> execute(String name) {
        if (name == null || name.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.findByName(name.trim());
    }
}