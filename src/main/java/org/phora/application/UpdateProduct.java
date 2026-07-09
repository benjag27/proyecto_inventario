package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.service.AuditLogService;

import java.util.Optional;

public class UpdateProduct {

    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;

    public UpdateProduct(ProductRepository productRepository, AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
    }

    public boolean execute(String name, double price, int stock, int id,String activeUser) {
        Optional<Product> p = productRepository.findById(id);
        if (p.isEmpty()) return false;

        Product product = p.get();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);

        productRepository.update(product);
        String desc = "Se registró el cambio en el  producto: " + product.getName() + " (Precio: $" + product.getPrice() + " | Stock Inicial: " + product.getStock() + ")";
        auditLogService.logAction(activeUser, "CREATE", desc);
        return true;
    }
}