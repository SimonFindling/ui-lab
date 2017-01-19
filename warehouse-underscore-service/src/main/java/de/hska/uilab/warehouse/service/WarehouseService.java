package de.hska.uilab.warehouse.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hska.uilab.warehouse.client.ProductClient;
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
@RequestMapping("/warehouse")
public class WarehouseService {

    @Autowired
    private ProductClient productClient;

    private final static Logger LOGGER = Logger.getLogger(WarehouseService.class.getName());

    @Autowired
    private DatabaseHandler dbh;


    /////////////
    // GET
    /////////////
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<String> showMessage() {
        LOGGER.log(Level.INFO, "Helloooooo");
        return new ResponseEntity<>("Helloooooo, I am WarehouseService!\n", HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Warehouse>> getWarehouses() {
        LOGGER.log(Level.INFO, "get all warehouses");

//        List<Product> objects = this.productClient.getAllProducts().getBody();
//        LOGGER.log(Level.INFO, objects + "");
        return new ResponseEntity<List<Warehouse>>(dbh.getAllWarehouses(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{warehouseId}", method = RequestMethod.GET)
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable long warehouseId) {
        LOGGER.log(Level.INFO, "get warehouse by id " + warehouseId);
        Warehouse wdb = dbh.getWarehouseById(warehouseId);
        if (wdb == null) {
            return new ResponseEntity<Warehouse>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Warehouse>(wdb, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/product/{productId}/count", method = RequestMethod.GET)
    public ResponseEntity<Integer> getAmountOfAProductInAllWarehouses(
            @PathVariable Integer productId) {
        LOGGER.log(Level.INFO, "get warehouseplaceProduct count by warehouseplaceProductId " + productId);
        return new ResponseEntity<Integer>(dbh.getWarehousePlaceProductForProductId(productId).size(), HttpStatus.OK);
    }

    // get all products with the matching whpProductId
    @RequestMapping(value = "/place/{productId}/all", method = RequestMethod.GET)
    public ResponseEntity<List<WarehousePlaceProduct>> getWarehousePlaceProductsForProductId(
            @PathVariable Integer productId) {
        LOGGER.log(Level.INFO, "get warehouseplaceProduct by warehouseplaceProductId " + productId);
        return new ResponseEntity<List<WarehousePlaceProduct>>(dbh.getWarehousePlaceProductForProductId(productId), HttpStatus.OK);
    }

    // TODO
    // get all products with the matching whpProductId and resolved product data
    @RequestMapping(value = "/place/{productId}/full", method = RequestMethod.GET)
    public ResponseEntity<List<ResolvedWarehousePlaceProduct>> getResolvedWarehousePlaceProductForProductId(
            @PathVariable Integer productId) {
        LOGGER.log(Level.INFO, "get warehouseplaceProduct by warehouseplaceProductId " + productId);
        List<WarehousePlaceProduct> dbwpp = dbh.getWarehousePlaceProductForProductId(productId);
        List<ResolvedWarehousePlaceProduct> ret = new ArrayList<ResolvedWarehousePlaceProduct>();
        for (WarehousePlaceProduct e : dbwpp) {
            ResolvedWarehousePlaceProduct resolved = new ResolvedWarehousePlaceProduct();

            ResponseEntity<Product> responseEntity = this.productClient.getSingleProduct(e.getProductid());
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

    @RequestMapping(value = "/place", method = RequestMethod.GET)
    public ResponseEntity<List<WarehousePlace>> getWarehousePlaces() {
        LOGGER.log(Level.INFO, "get all warehouseplaces");
        return new ResponseEntity<List<WarehousePlace>>(dbh.getAllWarehousePlaces(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{warehouseId}/place", method = RequestMethod.GET)
    public ResponseEntity<List<WarehousePlace>> getWarehousePlacesForWarehouseId(@PathVariable long warehouseId) {
        LOGGER.log(Level.INFO, "get all warehouseplaces for warehouse with id " + warehouseId);
        return new ResponseEntity<List<WarehousePlace>>(dbh.getAllWarehousePlacesForWarehouseId(warehouseId),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/place/{warehousePlaceId}/product", method = RequestMethod.GET)
    public ResponseEntity<List<WarehousePlaceProduct>> getAllProductsOfAPlace(
            @PathVariable long warehousePlaceId) {
        LOGGER.log(Level.INFO, "get warehouseplaceProduct by warehousePlaceId " + warehousePlaceId);
        return new ResponseEntity<List<WarehousePlaceProduct>>(dbh.getWarehousePlaceProductForWarehousePlaceId(warehousePlaceId), HttpStatus.OK);
    }

    /////////////
    // POST
    /////////////
    @RequestMapping(value = "", method = RequestMethod.POST)
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

    @RequestMapping(value = "/place", method = RequestMethod.POST)
    public ResponseEntity<Long> createPlaceInWarehouse(@RequestBody WarehousePlace warehousePlace) {
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

    @RequestMapping(value = "/product", method = RequestMethod.POST)
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

    /////////////
    // PATCH
    /////////////
    @RequestMapping(value = "/{warehouseId}", method = RequestMethod.PATCH)
    public ResponseEntity<Warehouse> updateWarehouse(@RequestBody Warehouse warehouse, @PathVariable long warehouseId) {
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

    @RequestMapping(value = "/place/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<WarehousePlace> updateWarehousePlace(@RequestBody WarehousePlace warehousePlace, @PathVariable long id) {
        LOGGER.log(Level.INFO, "Modify warehouseplace " + id);
        WarehousePlace updatedWarehousePlace = dbh.modifyWarehousePlace(id, warehousePlace);
        if (updatedWarehousePlace != null) {
            LOGGER.log(Level.INFO, "Modified warehouseplace.");
            return new ResponseEntity<>(updatedWarehousePlace, HttpStatus.OK);
        } else {
            LOGGER.log(Level.INFO, "Couldn't modify warehouseplace.");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/product/{warehousePlaceProductId}", method = RequestMethod.PATCH)
    public ResponseEntity<WarehousePlaceProduct> updateWarehousePlaceProduct(
            @PathVariable long warehousePlaceProductId,
            @RequestBody WarehousePlaceProduct warehousePlaceProduct) {
        LOGGER.log(Level.INFO, "Modify warehouseplaceproduct " + warehousePlaceProductId);
        WarehousePlaceProduct updatedWarehousePlaceProduct = dbh.modifyWarehousePlaceProduct(warehousePlaceProductId, warehousePlaceProduct);
        if (updatedWarehousePlaceProduct != null) {
            LOGGER.log(Level.INFO, "Modified warehouseplace product.");
            return new ResponseEntity<>(updatedWarehousePlaceProduct, HttpStatus.OK);
        } else {
            LOGGER.log(Level.INFO, "Couldn't modify warehouseplace product.");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /////////////
    // DELETE
    /////////////
    // TODO

}
