package de.hska.uilab.product.controller;

import de.hska.uilab.product.model.Product;
import de.hska.uilab.product.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product")
public class ProductsController {

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<String> info() {
        return new ResponseEntity<String>("Hello! This is product-microservice", HttpStatus.OK);
    }

    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Product> allProducts() {
        return productRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Product> getProduct(@PathVariable long id) {
        if (productRepository.findOne(id) != null) {
            return ResponseEntity.ok(productRepository.findOne(id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        productRepository.save(product);
        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
    }

    @PatchMapping(value = "")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Product foundProduct = productRepository.findOne(product.getId());
        if (null == product) {
            return new ResponseEntity("No Product found for ID " + product.getId(), HttpStatus.NOT_FOUND);
        }

        foundProduct.setName(product.getName());
        foundProduct.setDescription(product.getDescription());
        foundProduct.setImage(product.getImage());
        foundProduct.setPrice(product.getPrice());
        foundProduct.setEan(product.getEan());

        try {
            return new ResponseEntity<Product>(productRepository.save(foundProduct), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findOne(id);
        if (null == product) {
            return new ResponseEntity("No Product found for ID " + id, HttpStatus.NOT_FOUND);
        }
        productRepository.delete(product);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
