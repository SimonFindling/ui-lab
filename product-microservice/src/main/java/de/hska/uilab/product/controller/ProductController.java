package de.hska.uilab.product.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.hska.uilab.product.schema.Product;

@RestController
public class ProductController {

	@RequestMapping(value="/products", method=RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> products = new ArrayList<>();
		products.add(new Product(1, 1, "name", "image", "info", 2000));
		products.add(new Product(2, 1, "name", "image", "info", 5000));
		
		return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
	}
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseEntity<String> info() {
		return new ResponseEntity<String>("Hello! This is product-microservice", HttpStatus.OK);
	}
}
