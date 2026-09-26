package org.phora.domain.model;

import java.util.List;

public class Product {

  private int id;
  private String name;
  private double price;
  private int stock;
  private List<String> barcodes;

  private Product(Builder builder) {
    this.id       = builder.id;
    this.name     = builder.name;
    this.price    = builder.price;
    this.stock    = builder.stock;
    this.barcodes = List.copyOf(builder.barcodes);
  }

  public int getId()        { return id; }
  public String getName()   { return name; }
  public double getPrice()  { return price; }
  public int getStock()     { return stock; }
  public List<String> getBarcodes() { return barcodes; }

  public void setId(int id)              { this.id = id; }
  public void setName(String name)       { this.name = name; }
  public void setPrice(double price)     { this.price = price; }
  public void setStock(int stock)        { this.stock = stock; }
  public void setBarcodes(List<String> barcodes) {
    this.barcodes = barcodes == null ? List.of() : List.copyOf(barcodes);
  }

  public static class Builder {
    private int id;
    private String name;
    private double price;
    private int stock;
    private List<String> barcodes = List.of();

    public Builder id(int id)                   { this.id = id;             return this; }
    public Builder name(String name)            { this.name = name;         return this; }
    public Builder price(double price)          { this.price = price;       return this; }
    public Builder stock(int stock)             { this.stock = stock;       return this; }
    public Builder barcodes(List<String> codes) { this.barcodes = codes == null ? List.of() : codes; return this; }

    public Product build() { return new Product(this); }
  }
}