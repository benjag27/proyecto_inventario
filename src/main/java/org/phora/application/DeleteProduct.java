package org.phora.application;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.service.AuditLogService;

import java.util.Optional;

public class DeleteProduct {

    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;


    public DeleteProduct(ProductRepository productRepository,AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Devuelve true si el producto existía y se eliminó, false si no
     * se encontró — para que la UI pueda mostrar el mensaje correcto.
     */
    public boolean execute(Integer id, String activeUser) {
        Optional<Product> p = this.productRepository.findById(id);


        if (p.isEmpty()) {
            return false;
        }
        Product product = p.get();

        this.productRepository.delete(p.get());
        String desc = "Se elimino el producto: " + product.getName() + " (Precio: $" + product.getPrice() + " | Stock Inicial: " + product.getStock() + ")";
        auditLogService.logAction(activeUser, "CREATE", desc);
        return true;
    }
}