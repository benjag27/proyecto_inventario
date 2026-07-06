package org.phora.infrastructure;

import org.phora.application.AddProduct;
import org.phora.application.DeleteProduct;
import org.phora.application.FindProduct;
import org.phora.application.UpdateProduct;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.repository.UserRepository;
import org.phora.domain.service.LoginService;
import org.phora.infrastructure.persistence.ProductRepositoryImpl;
import org.phora.infrastructure.persistence.UserRepositoryImpl;

public class AppContext {

    private final ProductRepository productRepository;
    private final AddProduct addProductUseCase;
    private final UpdateProduct updateProductUseCase;
    private final DeleteProduct deleteProductUseCase;
    private final FindProduct findProductUseCase;

    private final UserRepository userRepository;
    private final LoginService loginServiceUseCase;

    public AppContext() {
        // 1. Persistencia (infraestructura)
        this.productRepository = new ProductRepositoryImpl();

        // 2. Casos de uso de productos (aplicación)
        this.addProductUseCase = new AddProduct(this.productRepository);
        this.updateProductUseCase = new UpdateProduct(this.productRepository);
        this.deleteProductUseCase = new DeleteProduct(this.productRepository);
        this.findProductUseCase = new FindProduct(this.productRepository);

        // 3. Login
        this.userRepository = new UserRepositoryImpl();
        this.loginServiceUseCase = new LoginService(userRepository);
    }

    public AddProduct getAddProductUseCase() {
        return this.addProductUseCase;
    }

    public UpdateProduct getUpdateProductUseCase() {
        return this.updateProductUseCase;
    }

    public DeleteProduct getDeleteProductUseCase() {
        return this.deleteProductUseCase;
    }

    public FindProduct getFindProductUseCase() {
        return this.findProductUseCase;
    }

    public LoginService getLoginServiceUseCase() {
        return this.loginServiceUseCase;
    }
}