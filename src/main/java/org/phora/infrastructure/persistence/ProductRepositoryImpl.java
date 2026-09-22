package org.phora.infrastructure.persistence;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;
import org.phora.domain.service.LoginService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;


public class ProductRepositoryImpl implements ProductRepository {
    private static final Logger logger = Logger.getLogger(LoginService.class.getName());
    private static final String DUPLICATE_BARCODE_MSG =
            "El código de barras ya está registrado en otro producto";

    private Connection getConnection() throws SQLException {
        return BsConfig.getConnection();
    }

    @Override
    public void add(Product p) {
        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, p.getName());
                stmt.setDouble(2, p.getPrice());
                stmt.setInt(3, p.getStock());
                stmt.executeUpdate();
            }
            p.setId(selectLastInsertRowId(conn));
            insertBarcodes(conn, p);
            conn.commit();
            logger.info("Product '" + p.getName() + "' added successfully.");
        } catch (SQLException e) {
            if (isDuplicateBarcode(e)) throw new IllegalArgumentException(DUPLICATE_BARCODE_MSG);
            logger.info("Error adding product: " + e.getMessage());
        }
    }

    private int selectLastInsertRowId(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("No se pudo obtener el id generado para el producto");
        }
    }

    @Override
    public void update(Product p) {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, p.getName());
                stmt.setDouble(2, p.getPrice());
                stmt.setInt(3, p.getStock());
                stmt.setInt(4, p.getId());
                stmt.executeUpdate();
                syncBarcodes(conn, p);
            }
            conn.commit();
            logger.info("Product with ID " + p.getId() + " updated successfully.");
        } catch (SQLException e) {
            if (isDuplicateBarcode(e)) throw new IllegalArgumentException(DUPLICATE_BARCODE_MSG);
            logger.info("Error updating product: " + e.getMessage());
        }
    }

    @Override
    public void delete(Product p) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getId());
            stmt.executeUpdate();
            logger.info("Product with ID " + p.getId() + " deleted successfully.");
        } catch (SQLException e) {
            logger.info("Error deleting product: " + e.getMessage());
        }
    }

    @Override
    public Optional<Product> findById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product p = buildProduct(rs);
                    p.setBarcodes(findBarcodes(p.getId()));
                    return Optional.of(p);
                }
            }
        } catch (SQLException e) {
            logger.info("Error finding product by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY id";
        List<Product> products = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = buildProduct(rs);
                p.setBarcodes(findBarcodes(p.getId()));
                products.add(p);
            }
        } catch (SQLException e) {
            logger.info("Error listing products: " + e.getMessage());
        }
        return products;
    }

    @Override
    public List<Product> findByName(String name) {
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        List<Product> products = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product p = buildProduct(rs);
                    p.setBarcodes(findBarcodes(p.getId()));
                    products.add(p);
                }
            }
        } catch (SQLException e) {
            logger.info("Error finding products by name: " + e.getMessage());
        }
        return products;
    }

    private void insertBarcodes(Connection conn, Product p) throws SQLException {
        String sql = "INSERT INTO product_barcodes (product_id, barcode) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String code : p.getBarcodes()) {
                stmt.setInt(1, p.getId());
                stmt.setString(2, code);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Sincroniza la lista de códigos del producto: elimina los que ya no están
     * en la lista final e inserta los nuevos. Permite agregar un código o
     * eliminar uno solo sin tocar el resto.
     */
    private void syncBarcodes(Connection conn, Product p) throws SQLException {
        Set<String> desired = new HashSet<>(p.getBarcodes());

        String selectSql = "SELECT barcode FROM product_barcodes WHERE product_id = ?";
        List<String> existing = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, p.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) existing.add(rs.getString("barcode"));
            }
        }

        String insertSql = "INSERT INTO product_barcodes (product_id, barcode) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (String code : desired) {
                if (!existing.contains(code)) {
                    stmt.setInt(1, p.getId());
                    stmt.setString(2, code);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }

        String deleteSql = "DELETE FROM product_barcodes WHERE product_id = ? AND barcode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            for (String code : existing) {
                if (!desired.contains(code)) {
                    stmt.setInt(1, p.getId());
                    stmt.setString(2, code);
                    stmt.executeUpdate();
                }
            }
        }
    }

    private List<String> findBarcodes(int productId) {
        String sql = "SELECT barcode FROM product_barcodes WHERE product_id = ? ORDER BY id";
        List<String> codes = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) codes.add(rs.getString("barcode"));
            }
        } catch (SQLException e) {
            logger.info("Error loading barcodes for product " + productId + ": " + e.getMessage());
        }
        return codes;
    }

    private boolean isDuplicateBarcode(SQLException e) {
        return e.getMessage() != null && e.getMessage().contains("product_barcodes.barcode");
    }

    private Product buildProduct(ResultSet rs) throws SQLException {
        return new Product.Builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .price(rs.getDouble("price"))
                .stock(rs.getInt("stock"))
                .build();
    }
}