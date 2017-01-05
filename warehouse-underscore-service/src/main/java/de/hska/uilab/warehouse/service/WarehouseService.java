package de.hska.uilab.warehouse.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.hska.uilab.warehouse.data.Product;
import de.hska.uilab.warehouse.data.Warehouse;
import de.hska.uilab.warehouse.data.WarehousePlace;

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

	@RequestMapping(value = "/warehouses", method = RequestMethod.GET)
	public ResponseEntity<List<Warehouse>> getWarehouses() {
		LOGGER.log(Level.INFO, "get all warehouses");

		ResponseEntity<Product[]> responseEntity = this.restTemplate.getForEntity("http://172.19.0.8:7654/products",
				Product[].class);
		Product[] objects = responseEntity.getBody();
		LOGGER.log(Level.INFO, objects + "");
		return new ResponseEntity<List<Warehouse>>(dbh.getAllWarehouses(), HttpStatus.OK);
	}

	@RequestMapping(value = "/warehouses", method = RequestMethod.POST)
	public ResponseEntity<Long> createWarehouse(@RequestBody Warehouse warehouse) {
		LOGGER.log(Level.INFO, "Create warehouse " + warehouse.getName());
		long createdId = dbh.createWarehouse(warehouse);
		if (createdId != -1) {
			LOGGER.log(Level.INFO, "Created warehouse with id: " + createdId + ".");
			return new ResponseEntity<Long>(createdId, HttpStatus.CREATED);
		} else {
			LOGGER.log(Level.INFO, "Couldn't create warehouse.");
			return new ResponseEntity<Long>(-1l, HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@RequestMapping(value = "/warehouseplaces", method = RequestMethod.GET)
	public ResponseEntity<List<WarehousePlace>> getWarehousePlaces() {
		LOGGER.log(Level.INFO, "get all warehouseplacess");
		return new ResponseEntity<List<WarehousePlace>>(dbh.getAllWarehousePlaces(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/warehouseplaces/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<WarehousePlace>> getWarehousePlacesForWarehouseId(
			@PathVariable long warehouseId) {
		LOGGER.log(Level.INFO, "get all warehouseplaces for warehouse with id " + warehouseId);
		return new ResponseEntity<List<WarehousePlace>>(dbh.getAllWarehousePlacesForWarehouseId(warehouseId), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/warehouseplaces", method = RequestMethod.POST)
	public ResponseEntity<Long> createWarehouse(@RequestBody WarehousePlace warehousePlace) {
		LOGGER.log(Level.INFO, "Create warehouse " + warehousePlace.getName() + " for warehouse " + warehousePlace.getWarehouse().getId());
		long createdId = dbh.createWarehousePlace(warehousePlace);
		if (createdId != -1) {
			LOGGER.log(Level.INFO, "Created warehouseplace with id: " + createdId + ".");
			return new ResponseEntity<Long>(createdId, HttpStatus.CREATED);
		} else {
			LOGGER.log(Level.INFO, "Couldn't create warehouseplace.");
			return new ResponseEntity<Long>(-1l, HttpStatus.NOT_ACCEPTABLE);
		}
	}
}
