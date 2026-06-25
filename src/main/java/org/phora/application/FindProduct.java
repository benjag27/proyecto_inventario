package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.Optional;

public class FindProduct {


    private  ProductRepository productRepository;

    public FindProduct(ProductRepository productRepository) {

        this.productRepository = productRepository;

    }

    public void execute(Integer id) {

        Optional<Product> p  = this.productRepository.findById(id);
        System.out.println(p.get().getName());


    }

}