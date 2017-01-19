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
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findOne(id);
        if (null == product) {
            return new ResponseEntity("No Product found for ID " + id, HttpStatus.NOT_FOUND);
        }
        productRepository.delete(product);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id) {
        Product product = productRepository.findOne(id);
        product = productRepository.save(product);
        if (null == product) {
            return new ResponseEntity("No Product found for ID " + id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public ResponseEntity<String> info() {
		return new ResponseEntity<String>("Hello! This is product-microservice", HttpStatus.OK);
	}
}
