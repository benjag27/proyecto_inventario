package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.util.Optional;

public class UpdateProduct {

    private final ProductRepository productRepository;

    public UpdateProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Busca el producto por id, le aplica los nuevos valores y persiste
     * el cambio. Devuelve true si se encontró y actualizó, false si el
     * id no existía — así la UI sabe qué mensaje mostrar sin tener que
     * adivinar mirando un Optional vacío que no le llega.
     */
    public boolean execute(String name, int stock, int id) {
        Optional<Product> p = this.productRepository.findById(id);

        if (p.isEmpty()) {
            return false;
        }

        Product producto = p.get();
        producto.setName(name);
        producto.setStock(stock);

        this.productRepository.update(producto);
        return true;
    }
}