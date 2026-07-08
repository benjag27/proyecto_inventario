package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.Optional;

public class UpdateProduct {

    private final ProductRepository productRepository;

    public UpdateProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public boolean execute(String name, double price, int stock, int id) {
        Optional<Product> p = productRepository.findById(id);
        if (p.isEmpty()) return false;

        Product product = p.get();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);

        productRepository.update(product);
        return true;
    }
}