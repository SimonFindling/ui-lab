package de.hska.uilab.product.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hska.uilab.product.schema.Product;
import de.hska.uilab.product.schema.ProductsMock;

@RestController
@SuppressWarnings("rawtypes")
public class ProductController {
	private ProductsMock pm = new ProductsMock();

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Product>> getAllProducts() {
		return new ResponseEntity<List<Product>>(pm.getAllProducts(), HttpStatus.OK);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity createProduct(@RequestBody Map<String, String> body) {
		ObjectMapper mapper = new ObjectMapper();
		Product retrievedProduct = mapper.convertValue(body, Product.class);
		if (pm.createProduct(retrievedProduct)) {
			return new ResponseEntity(HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Returns a product for the given product id. Returns null if non is found.
	 * @param productId id for product
	 * @return
	 */
	@RequestMapping(value = "/{productId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Product> getSingleProduct(@PathVariable int productId) {
		Product productById = pm.getProductById(productId);
		if (productById != null) {
			return new ResponseEntity<Product>(productById, HttpStatus.OK);
		}
		return new ResponseEntity<Product>(productById, HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public ResponseEntity changeProduct(@RequestBody Map<String, String> body) {
		ObjectMapper mapper = new ObjectMapper();
		Product retrievedProduct = mapper.convertValue(body, Product.class);
		if (pm.changeProduct(retrievedProduct)) {
			return new ResponseEntity(HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
	}
	
	@RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
	public ResponseEntity deleteProduct(@PathVariable int productId) {
		if (pm.deleteProductById(productId)) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<String> info() {
		return new ResponseEntity<String>("Hello! This is product-microservice", HttpStatus.OK);
	}
}
