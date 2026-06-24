package org.phora.infrastructure;

import org.phora.application.AddProduct;
import org.phora.domain.repository.ProductRepository;
import org.phora.infrastructure.persistence.ProductRepositoryImpl;

public class AppContext {

    private final ProductRepository productRepository;
    private final AddProduct addProductUseCase;

    public AppContext() {

        // 1. Instanciamos la persistencia (infraestructura)
        this.productRepository = new ProductRepositoryImpl();

        // 2. Le inyectamos el repositorio al caso de uso (aplicación)
        this.addProductUseCase = new AddProduct(this.productRepository);
    }

    // Proveemos el caso de uso a la capa de presentación
    public AddProduct getAddProductUseCase() {
        return this.addProductUseCase;
    }
}