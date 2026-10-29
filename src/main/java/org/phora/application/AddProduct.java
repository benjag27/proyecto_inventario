package org.phora.application;

import org.phora.domain.model.BarcodeRules;
import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.service.AuditLogService;

import java.util.List;

public class AddProduct {

    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;

    public AddProduct(ProductRepository productRepository, AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
    }


    public void execute(String name, double price, int stock, List<String> barcodes, String activeUser) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        List<String> normalized = BarcodeRules.normalizeAll(barcodes);

        Product product = new Product.Builder()
                .name(name.trim())
                .price(price)
                .stock(stock)
                .barcodes(normalized)
                .build();

        productRepository.add(product);

        String codes = normalized.isEmpty() ? "sin códigos" : "Códigos: " + String.join(", ", normalized);
        String desc = "Se registró el producto: " + product.getName() + " (Precio: $" + product.getPrice() + " | Stock Inicial: " + product.getStock() + " | " + codes + ")";
        auditLogService.logAction(activeUser, "CREATE", desc);
    }
}