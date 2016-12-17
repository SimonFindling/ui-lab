package de.hska.uilab.vendor.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.hska.uilab.vendor.data.Vendor;

@RestController
public class VendorService {
	

	private final static Logger LOGGER = Logger.getLogger(VendorService.class.getName());

	@Autowired
	private DatabaseHandler dbh;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<String> showMessage() {
		LOGGER.log(Level.INFO, "Helloooooo");
		return new ResponseEntity<>("Helloooooo, I am VendorService!\n", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/vendors/{tenantId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Vendor>> getVendorsByTenantId(@PathVariable int tenantId) {
		LOGGER.log(Level.INFO, "Get vendors by tenantId " + tenantId);
		return new ResponseEntity<List<Vendor>>(dbh.getVendorsByTenantId(tenantId), HttpStatus.OK);
	}

	@RequestMapping(value = "/vendors/{tenantId}/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Vendor> getVendorByTenantIdAndId(@PathVariable int tenantId, @PathVariable int id) {
		LOGGER.log(Level.INFO, "Get vendor by tenantId " + tenantId + " and id " + id);
		return new ResponseEntity<Vendor>(dbh.getVendorByTenantIdAndId(tenantId, id), HttpStatus.OK);
	}

	@RequestMapping(value = "/vendors/{tenantId}", method = RequestMethod.POST)
	public ResponseEntity createVendorForTenant(@RequestBody Vendor vendor, @PathVariable int tenantId) {
		LOGGER.log(Level.INFO, "Create vendor " + vendor.getName() + " for tenant " + tenantId);
		if (dbh.createVendorForTenant(vendor, tenantId)) {
			LOGGER.log(Level.INFO, "Created vendor.");
			return new ResponseEntity(HttpStatus.CREATED);
		} else {
			LOGGER.log(Level.INFO, "Couldn't create vendor.");
			return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@RequestMapping(value = "/vendors/{tenantId}/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Vendor> modifyVendorForTenant(@RequestBody Vendor vendor, @PathVariable int tenantId,
			@PathVariable int id) {
		LOGGER.log(Level.INFO, "Modify vendor " + id + " for tenant " + tenantId);
		Vendor updatedVendor = dbh.modifyVendorForTenant(vendor, tenantId, id);
		if (updatedVendor != null) {
			LOGGER.log(Level.INFO, "Modified vendor.");
			return new ResponseEntity<>(updatedVendor, HttpStatus.OK);
		} else {
			LOGGER.log(Level.INFO, "Couldn't modify vendor.");
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@RequestMapping(value = "/vendors/{tenantId}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity deleteVendorForTenant(@PathVariable int tenantId, @PathVariable int id) {
		LOGGER.log(Level.INFO, "Delete vendor " + id + " for tenant " + tenantId);
		if (dbh.deleteVendorForTenant(tenantId, id)) {
			LOGGER.log(Level.INFO, "Deleted vendor.");
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} else {
			LOGGER.log(Level.INFO, "Couldn't delete vendor.");
			return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
		}
	}
}
