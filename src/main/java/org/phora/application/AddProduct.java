package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

public class AddProduct {

    // 1. Declaramos la INTERFAZ (No la implementación)
    private final ProductRepository productRepository;

    // 2. El constructor recibe CUALQUIER cosa que implemente esa interfaz
    public AddProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 3. El método para ejecutar el caso de uso
    public void execute(String name, int stock) {
        // Podrías meter validaciones de negocio aquí si quisieras:
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }

        // Creamos el objeto usando el Builder
        Product nuevoProducto = new Product.Builder()
                .name(name)
                .stock(stock)
                .build();

        // Guardamos usando el repositorio
        this.productRepository.add(nuevoProducto);
    }
}