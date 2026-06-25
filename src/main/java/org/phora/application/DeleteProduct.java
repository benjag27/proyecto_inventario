package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.Optional;

public class DeleteProduct {


    private  ProductRepository productRepository;

    public DeleteProduct(ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    public void execute(Integer id) {

        Optional<Product> p  = this.productRepository.findById(id);
        if(p.isPresent()) {
            this.productRepository.delete(p.get());
        }else{
            System.out.println("No se encontró el producto");
        }
    }
}