package org.phora.infrastructure;

import org.phora.application.*;
import org.phora.domain.repository.AuditLogRepository;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.repository.UserRepository;
import org.phora.domain.service.AuditLogService;
import org.phora.domain.service.LoginService;
import org.phora.infrastructure.persistence.AuditLogRepositoryImpl;
import org.phora.infrastructure.persistence.ProductRepositoryImpl;
import org.phora.infrastructure.persistence.UserRepositoryImpl;


public class AppContext {

    private final ProductRepository productRepository;
    private final AddProduct addProductUseCase;
    private final UpdateProduct updateProductUseCase;
    private final DeleteProduct deleteProductUseCase;
    private final FindProduct findProductUseCase;
    private final FindByName findByNameUseCase;
    private final ListProducts  listProductsUseCase;
    private final UserRepository userRepository;
    private final LoginService loginServiceUseCase;

    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;


    public AppContext() {
        // 1. Persistencia (infraestructura)
        this.productRepository = new ProductRepositoryImpl();
        this.auditLogRepository = new AuditLogRepositoryImpl();

        this.auditLogService = new AuditLogService(this.auditLogRepository);

        // 2. Casos de uso de productos (aplicación)
        this.addProductUseCase = new AddProduct(this.productRepository, this.auditLogService);
        this.updateProductUseCase = new UpdateProduct(this.productRepository,this.auditLogService);
        this.deleteProductUseCase = new DeleteProduct(this.productRepository,this.auditLogService);
        this.findProductUseCase = new FindProduct(this.productRepository);
        this.findByNameUseCase = new FindByName(this.productRepository);
        this.listProductsUseCase = new ListProducts(this.productRepository);



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

    public FindByName getFindByNameUseCase() {return  this.findByNameUseCase;}

    public LoginService getLoginServiceUseCase() {
        return this.loginServiceUseCase;
    }

    public ListProducts getListProductsUseCase()   { return listProductsUseCase; }
    public AuditLogService getAuditLogService() {
        return this.auditLogService;
    }

}