package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

public class AddProduct {

    private final ProductRepository productRepository;

    public AddProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(String name, double price, int stock) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        Product product = new Product.Builder()
                .name(name.trim())
                .price(price)
                .stock(stock)
                .build();
        productRepository.add(product);
    }
}