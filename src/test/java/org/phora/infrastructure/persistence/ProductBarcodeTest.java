package org.phora.infrastructure.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.phora.domain.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductBarcodeTest {

    private ProductRepositoryImpl repo;
    private final List<Integer> createdIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        BsConfig.getConnection(); // fuerza la creación de tablas (static block + initDB)
        repo = new ProductRepositoryImpl();
    }

    @AfterEach
    void cleanup() {
        for (int id : createdIds) {
            repo.findById(id).ifPresent(repo::delete);
        }
        createdIds.clear();
    }

    @Test
    void altaSinCodigos() {
        Product p = repoAdd("TEST sin códigos", List.of());

        assertTrue(p.getBarcodes().isEmpty());
        Product found = repo.findById(p.getId()).orElseThrow();
        assertTrue(found.getBarcodes().isEmpty(), "El producto debe persistirse sin códigos");
    }

    @Test
    void altaConVariosCodigos() {
        Product p = repoAdd("TEST con códigos", List.of("7790-001", "7790-002"));

        Product found = repo.findById(p.getId()).orElseThrow();
        assertEquals(List.of("7790-001", "7790-002"), found.getBarcodes());
    }

    @Test
    void modificarAgregaUnCodigo() {
        Product p = repoAdd("TEST agrega código", List.of("ABC-1"));
        p.setBarcodes(List.of("ABC-1", "ABC-2"));

        repo.update(p);

        Product found = repo.findById(p.getId()).orElseThrow();
        assertTrue(found.getBarcodes().containsAll(List.of("ABC-1", "ABC-2")),
                "La modificación debe agregar el código nuevo manteniendo el existente");
    }

    @Test
    void modificarEliminaUnCodigoMantieneElResto() {
        Product p = repoAdd("TEST elimina uno", List.of("ABC-1", "ABC-2", "ABC-3"));
        p.setBarcodes(List.of("ABC-1", "ABC-3"));

        repo.update(p);

        Product found = repo.findById(p.getId()).orElseThrow();
        assertEquals(List.of("ABC-1", "ABC-3"), found.getBarcodes(),
                "Se elimina solo un código y se conservan los demás");
    }

    @Test
    void codigoDuplicadoEntreProductosEsRechazado() {
        repoAdd("TEST duplicado 1", List.of("DUP-CODE"));

        assertThrows(IllegalArgumentException.class,
                () -> repoAdd("TEST duplicado 2", List.of("DUP-CODE")),
                "Un código ya registrado en otro producto debe rechazarse");
    }

    @Test
    void bajaEnCascadaBorraLosCodigos() throws Exception {
        Product p = repoAdd("TEST cascada", List.of("CSC-1", "CSC-2"));
        int id = p.getId();

        repo.delete(p);
        createdIds.remove(Integer.valueOf(id));

        try (Connection conn = BsConfig.getConnection();
             PreparedStatement st = conn.prepareStatement(
                     "SELECT COUNT(*) FROM product_barcodes WHERE product_id = ?")) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1),
                        "El borrado del producto debe eliminar sus códigos en cascada");
            }
        }
    }

    private Product repoAdd(String name, List<String> codes) {
        Product p = new Product.Builder()
                .name(name)
                .price(10.0)
                .stock(1)
                .barcodes(codes)
                .build();
        repo.add(p);
        createdIds.add(p.getId());
        return p;
    }
}