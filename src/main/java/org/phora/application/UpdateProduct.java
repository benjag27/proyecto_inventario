package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.Optional;

public class UpdateProduct {

    private ProductRepository productRepository;

    public UpdateProduct(ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    public void execute(String name, int stock, int id) {

        Optional<Product> p = this.productRepository.findById(id);
        if (p.isPresent()) {
            p.get().setName(name);
            p.get().setStock(stock);
        } else {
            System.out.println("Product to update not founded");
        }
    }
}