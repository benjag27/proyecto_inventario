package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.Optional;

public class DeleteProduct {

    private final ProductRepository productRepository;

    public DeleteProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Devuelve true si el producto existía y se eliminó, false si no
     * se encontró — para que la UI pueda mostrar el mensaje correcto.
     */
    public boolean execute(Integer id) {
        Optional<Product> p = this.productRepository.findById(id);

        if (p.isEmpty()) {
            return false;
        }

        this.productRepository.delete(p.get());
        return true;
    }
}