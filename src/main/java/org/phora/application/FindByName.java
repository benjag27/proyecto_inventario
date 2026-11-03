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
     * Devuelve los productos cuyo nombre contiene el texto (sin distinguir
     * mayúsculas). Por ejemplo, "papa" coincide con "Papa peruana".
     */
    public List<Product> execute(String name) {
        if (name == null || name.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.findByName(name.trim());
    }
}