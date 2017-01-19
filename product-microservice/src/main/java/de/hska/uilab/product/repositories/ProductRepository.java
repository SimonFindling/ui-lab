package de.hska.uilab.product.repositories;

import de.hska.uilab.product.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {


}