package org.phora.infrastructure;

import org.phora.application.AddProduct;
import org.phora.application.LoginService;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.repository.UserRepository;
import org.phora.infrastructure.persistence.ProductRepositoryImpl;
import org.phora.infrastructure.persistence.UserRepositoryImpl;

public class AppContext {

    private final ProductRepository productRepository;
    private final AddProduct addProductUseCase;

    private final UserRepository userRepository;
    private final LoginService loginServiceUseCase;

    public AppContext() {

        // 1. Instanciamos la persistencia (infraestructura)
        this.productRepository = new ProductRepositoryImpl();

        // 2. Le inyectamos el repositorio al caso de uso (aplicación)
        this.addProductUseCase = new AddProduct(this.productRepository);

        this.userRepository = new UserRepositoryImpl();
        this.loginServiceUseCase = new LoginService(userRepository);

    }

    // Proveemos el caso de uso a la capa de presentación
    public AddProduct getAddProductUseCase() {
        return this.addProductUseCase;
    }

    public LoginService getLoginServiceUseCase() {
        return this.loginServiceUseCase;
    }
}