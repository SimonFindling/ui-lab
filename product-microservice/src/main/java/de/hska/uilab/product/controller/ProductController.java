package de.hska.uilab.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

	@RequestMapping(value="/products", method=RequestMethod.GET)
	public ResponseEntity<String> getAllProducts() {
		
		String res = "all products";
		
		return new ResponseEntity<String>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseEntity<String> info() {
		return new ResponseEntity<String>("Hello! This is product-microservice", HttpStatus.OK);
	}
}
