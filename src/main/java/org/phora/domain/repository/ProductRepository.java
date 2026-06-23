package org.phora.domain.repository;

import java.util.Optional;

import org.phora.domain.model.Product;

public interface ProductRepository {

  public void add(Product p);

  public void update(Product p);

  public void delete(Product p);

  public Optional<Product> findById(int id);
}