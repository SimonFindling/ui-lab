package de.hska.uilab.warehouse.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.hska.uilab.warehouse.data.Product;
import de.hska.uilab.warehouse.data.Warehouse;

@RestController
public class WarehouseService {
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Autowired
	RestTemplate restTemplate;

	private final static Logger LOGGER = Logger.getLogger(WarehouseService.class.getName());

	@Autowired
	private DatabaseHandler dbh;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<String> showMessage() {
		LOGGER.log(Level.INFO, "Helloooooo");
		return new ResponseEntity<>("Helloooooo, I am WarehouseService!\n", HttpStatus.OK);
	}
	
	@RequestMapping(value = "warehouse", method = RequestMethod.GET)
	public ResponseEntity<List<Warehouse>> getWarehouses(){
		LOGGER.log(Level.INFO, "get all warehouses");
		
		ResponseEntity<Product[]> responseEntity = this.restTemplate.getForEntity("http://172.19.0.9:7654/products", Product[].class);
		Product[] objects = responseEntity.getBody();
		LOGGER.log(Level.INFO, objects+"");
		return new ResponseEntity<List<Warehouse>>(dbh.getAllWarehouses(), HttpStatus.OK);
	}
}
