package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.service.AuditLogService;

public class AddProduct {

    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;

    public AddProduct(ProductRepository productRepository, AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
    }

    // 👈 Agregamos activeUser acá
    public void execute(String name, double price, int stock, String activeUser) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        Product product = new Product.Builder()
                .name(name.trim())
                .price(price)
                .stock(stock)
                .build();

        productRepository.add(product);

        String desc = "Se registró el producto: " + product.getName() + " (Precio: $" + product.getPrice() + " | Stock Inicial: " + product.getStock() + ")";
        auditLogService.logAction(activeUser, "CREATE", desc);
    }
}