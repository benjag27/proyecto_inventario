package org.phora.domain.model;

public class Product {

  private int id;
  private String name;
  private int stock;

  // El constructor lo hacemos privado para obligar a usar el Builder
  private Product(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.stock = builder.stock;git
  }

  // Getters
  public int getId() { return id; }
  public String getName() { return name; }
  public int getStock() { return stock; }

  // Setters (En DDD a veces se quitan para hacer el modelo inmutable, pero los dejamos si los necesitas)
  public void setId(int id) { this.id = id; }
  public void setName(String name) { this.name = name; }
  public void setStock(int stock) { this.stock = stock; }

  // --- AQUÍ EMPIEZA EL PATRÓN BUILDER ---
  public static class Builder {
    private int id;
    private String name;
    private int stock;

    public Builder id(int id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder stock(int stock) {
      this.stock = stock;
      return this;
    }

    public Product build() {
      return new Product(this);
    }
  }
}