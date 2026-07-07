package org.phora.infrastructure.persistence;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {

    private Connection getConnection() throws SQLException {
        return BsConfig.getConnection();
    }

    @Override
    public void add(Product p) {
        String sql = "INSERT INTO products (name, stock) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getName());
            stmt.setInt(2, p.getStock());
            stmt.executeUpdate();
            System.out.println("Product '" + p.getName() + "' added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    @Override
    public void update(Product p) {
        String sql = "UPDATE products SET name = ?, stock = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getName());
            stmt.setInt(2, p.getStock());
            stmt.setInt(3, p.getId());
            stmt.executeUpdate();
            System.out.println("Product with ID " + p.getId() + " updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
    }

    @Override
    public void delete(Product p) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getId());
            stmt.executeUpdate();
            System.out.println("Product with ID " + p.getId() + " deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
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
                    Product product = new Product.Builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .stock(rs.getInt("stock"))
                            .build();
                    return Optional.of(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding product by ID: " + e.getMessage());
        }
        return Optional.empty();
    }
}