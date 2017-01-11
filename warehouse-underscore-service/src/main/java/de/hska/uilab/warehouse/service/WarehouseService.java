package de.hska.uilab.warehouse.service;

import java.util.ArrayList;
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
import de.hska.uilab.warehouse.data.ResolvedWarehousePlaceProduct;
import de.hska.uilab.warehouse.data.Warehouse;
import de.hska.uilab.warehouse.data.WarehousePlace;
import de.hska.uilab.warehouse.data.WarehousePlaceProduct;

@RestController
public class WarehouseService {
	
	private final static String PRODUCT_SERVICE_URL = "http://product/";

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

		ResponseEntity<Product[]> responseEntity = this.restTemplate.getForEntity("http://172.19.0.6:7654/products",
				Product[].class);
		Product[] objects = responseEntity.getBody();
		LOGGER.log(Level.INFO, objects + "");
		return new ResponseEntity<List<Warehouse>>(dbh.getAllWarehouses(), HttpStatus.OK);
	}

	@RequestMapping(value = "/warehouses/{warehouseId}", method = RequestMethod.GET)
	public ResponseEntity<Warehouse> getWarehouseById(@PathVariable long warehouseId) {
		LOGGER.log(Level.INFO, "get warehouse by id " + warehouseId);
		Warehouse wdb = dbh.getWarehouseById(warehouseId);
		if (wdb == null) {
			return new ResponseEntity<Warehouse>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<Warehouse>(wdb, HttpStatus.OK);
		}
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

	@RequestMapping(value = "/warehouses/{warehouseId}", method = RequestMethod.PUT)
	public ResponseEntity<Warehouse> modifyWarehouse(@RequestBody Warehouse warehouse, @PathVariable long warehouseId) {
		LOGGER.log(Level.INFO, "Modify warehouse " + warehouseId);
		Warehouse updatedWarehouse = dbh.modifyWarehouse(warehouseId, warehouse);
		if (updatedWarehouse != null) {
			LOGGER.log(Level.INFO, "Modified warehouse.");
			return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
		} else {
			LOGGER.log(Level.INFO, "Couldn't modify warehouse.");
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@RequestMapping(value = "/warehouseplaces", method = RequestMethod.GET)
	public ResponseEntity<List<WarehousePlace>> getWarehousePlaces() {
		LOGGER.log(Level.INFO, "get all warehouseplacess");
		return new ResponseEntity<List<WarehousePlace>>(dbh.getAllWarehousePlaces(), HttpStatus.OK);
	}

	@RequestMapping(value = "/warehouseplaces/{warehousePlaceId}", method = RequestMethod.GET)
	public ResponseEntity<List<WarehousePlace>> getWarehousePlacesForWarehouseId(@PathVariable long warehousePlaceId) {
		LOGGER.log(Level.INFO, "get all warehouseplaces for warehouse with id " + warehousePlaceId);
		return new ResponseEntity<List<WarehousePlace>>(dbh.getAllWarehousePlacesForWarehouseId(warehousePlaceId),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/warehouseplaces", method = RequestMethod.POST)
	public ResponseEntity<Long> createWarehouse(@RequestBody WarehousePlace warehousePlace) {
		LOGGER.log(Level.INFO, "Create warehouseplace " + warehousePlace.getName() + " for warehouse "
				+ warehousePlace.getWarehouse().getId());
		long createdId = dbh.createWarehousePlace(warehousePlace);
		if (createdId != -1) {
			LOGGER.log(Level.INFO, "Created warehouseplace with id: " + createdId + ".");
			return new ResponseEntity<Long>(createdId, HttpStatus.CREATED);
		} else {
			LOGGER.log(Level.INFO, "Couldn't create warehouseplace.");
			return new ResponseEntity<Long>(-1l, HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@RequestMapping(value = "/warehouseplaces/{warehousePlaceId}", method = RequestMethod.PUT)
	public ResponseEntity<WarehousePlace> modifyWarehousePlace(@RequestBody WarehousePlace warehousePlace, @PathVariable long warehousePlaceId) {
		LOGGER.log(Level.INFO, "Modify warehouseplace " + warehousePlaceId);
		WarehousePlace updatedWarehousePlace = dbh.modifyWarehousePlace(warehousePlaceId, warehousePlace);
		if (updatedWarehousePlace != null) {
			LOGGER.log(Level.INFO, "Modified warehouseplace.");
			return new ResponseEntity<>(updatedWarehousePlace, HttpStatus.OK);
		} else {
			LOGGER.log(Level.INFO, "Couldn't modify warehouseplace.");
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	// get all products from within the warehousePlace with the matching placeId
	@RequestMapping(value = "/warehouseplace/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<WarehousePlaceProduct>> getWarehousePlaceProductForWarehousePlaceId(
			@PathVariable long warehousePlaceId) {
		LOGGER.log(Level.INFO, "get warehouseplaceProduct by warehousePlaceId " + warehousePlaceId);
		return new ResponseEntity<List<WarehousePlaceProduct>>(dbh.getWarehousePlaceProductForWarehousePlaceId(warehousePlaceId), HttpStatus.OK);
	}
	
	// count all products with $ID in all warehouses
	@RequestMapping(value = "/warehouseplaceproductcount/{id}", method = RequestMethod.GET)
	public ResponseEntity<Integer> getWarehousePlaceProductCount(
			@PathVariable Integer warehouseplaceProductId) {
		LOGGER.log(Level.INFO, "get warehouseplaceProduct count by warehouseplaceProductId " + warehouseplaceProductId);
		return new ResponseEntity<Integer>(dbh.getWarehousePlaceProductForProductId(warehouseplaceProductId).size(), HttpStatus.OK);
	}
	
	// get all products with the matching whpProductId
	@RequestMapping(value = "/warehouseplaceproducts/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<WarehousePlaceProduct>> getWarehousePlaceProductForProductId(
			@PathVariable Integer warehouseplaceProductId) {
		LOGGER.log(Level.INFO, "get warehouseplaceProduct by warehouseplaceProductId " + warehouseplaceProductId);
		return new ResponseEntity<List<WarehousePlaceProduct>>(dbh.getWarehousePlaceProductForProductId(warehouseplaceProductId), HttpStatus.OK);
	}
	
	// create new whpProduct
	@RequestMapping(value = "/warehouseplaceproducts", method = RequestMethod.POST)
	public ResponseEntity<Long> createWarehousePlaceProduct(
			@RequestBody WarehousePlaceProduct warehousePlaceProduct) {
		long createdId = dbh.createWarehousePlaceProduct(warehousePlaceProduct);
		if (createdId != -1) {
			LOGGER.log(Level.INFO, "Created warehousePlaceProduct with id: " + createdId + ".");
			return new ResponseEntity<Long>(createdId, HttpStatus.CREATED);
		} else {
			LOGGER.log(Level.INFO, "Couldn't create warehousePlaceProduct.");
			return new ResponseEntity<Long>(-1l, HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	// update existing whpProduct
	@RequestMapping(value = "/warehouseplaceproducts/{id}", method = RequestMethod.POST)
	public ResponseEntity<WarehousePlaceProduct> updateWarehousePlaceProduct(
			@PathVariable long warehousePlaceProductId,
			@RequestBody WarehousePlaceProduct warehousePlaceProduct) {
		LOGGER.log(Level.INFO, "Modify warehouseplaceproduct " + warehousePlaceProductId);
		WarehousePlaceProduct updatedWarehousePlaceProduct = dbh.modifyWarehousePlaceProduct(warehousePlaceProductId, warehousePlaceProduct);
		if (updatedWarehousePlaceProduct != null) {
			LOGGER.log(Level.INFO, "Modified warehouseplace.");
			return new ResponseEntity<>(updatedWarehousePlaceProduct, HttpStatus.OK);
		} else {
			LOGGER.log(Level.INFO, "Couldn't modify warehouseplace.");
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	// get all products with the matching whpProductId and resolved product data
	@RequestMapping(value = "/resolvedwarehouseplaceproducts/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<ResolvedWarehousePlaceProduct>> getResolvedWarehousePlaceProductForProductId(
		@PathVariable Integer warehouseplaceProductId) {
		LOGGER.log(Level.INFO, "get warehouseplaceProduct by warehouseplaceProductId " + warehouseplaceProductId);
		List<WarehousePlaceProduct> dbwpp = dbh.getWarehousePlaceProductForProductId(warehouseplaceProductId);
		List<ResolvedWarehousePlaceProduct> ret = new ArrayList<ResolvedWarehousePlaceProduct>();
		for (WarehousePlaceProduct e : dbwpp) {
			ResolvedWarehousePlaceProduct resolved = new ResolvedWarehousePlaceProduct();
			
			ResponseEntity<Product> responseEntity = this.restTemplate.getForEntity(PRODUCT_SERVICE_URL + "/" + e.getProductid(), Product.class);
			Product product = responseEntity.getBody();
			if (product != null) {
				resolved.setVendorId(product.getVendorId());
				resolved.setProductName(product.getProductName());
				resolved.setProductImage(product.getProductImage());
				resolved.setProductInformation(product.getProductInformation());
				resolved.setPrice(product.getPrice());
				
			} else {
				LOGGER.log(Level.INFO, "Couldn't resolve product data.");
				
				resolved.setVendorId(-1);
				resolved.setProductName("not found");
				resolved.setProductImage("not found");
				resolved.setProductInformation("not found");
				resolved.setPrice(-1);
			}
			ret.add(resolved);
		}
		
		return new ResponseEntity<List<ResolvedWarehousePlaceProduct>>(ret, HttpStatus.OK);
	}
}
