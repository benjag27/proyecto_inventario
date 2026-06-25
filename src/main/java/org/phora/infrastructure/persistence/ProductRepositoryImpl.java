package org.phora.infrastructure.persistence;

import org.phora.domain.model.Product;
import org.phora.domain.repository.ProductRepository;

import java.sql.*;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {

   //conectar a la base de datos
    private Connection getConnection() throws SQLException {
        return BsConfig.getConnection();
    }

    @Override
    public void add(Product p) {
        String sql = "INSERT INTO productos (name, stock) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setInt(2, p.getStock());
            stmt.executeUpdate();

            System.out.println("✅ Producto '" + p.getName() + "' agregado con éxito.");

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar el producto: " + e.getMessage());
        }
    }

    @Override
    public void update(Product p) {
        String sql = "UPDATE productos SET name = ?, stock = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setInt(2, p.getStock());
            stmt.setInt(3, p.getId());
            stmt.executeUpdate();

            System.out.println("✅ Producto con ID " + p.getId() + " actualizado con éxito.");

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar el producto: " + e.getMessage());
        }
    }

    @Override
    public void delete(Product p) {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, p.getId());
            stmt.executeUpdate();

            System.out.println("✅ Producto con ID " + p.getId() + " eliminado de la base de datos.");

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar el producto: " + e.getMessage());
        }
    }

    @Override
    public Optional<Product> findById(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Usamos el Builder que creamos para reconstruir el objeto Product
                    Product product = new Product.Builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .stock(rs.getInt("stock"))
                            .build();
                    return Optional.of(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar el producto por ID: " + e.getMessage());
        }
        return Optional.empty();
    }
}