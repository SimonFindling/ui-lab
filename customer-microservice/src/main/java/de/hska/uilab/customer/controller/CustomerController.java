package de.hska.uilab.customer.controller;

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

import de.hska.uilab.customer.schema.Customer;
import de.hska.uilab.customer.schema.CustomerMock;

@RestController
@SuppressWarnings("rawtypes")
public class CustomerController {

	private CustomerMock mock = new CustomerMock();

	@RequestMapping(value = "/{tenantId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Customer>> getAllCustomers(@PathVariable int tenantId) {
		return new ResponseEntity<List<Customer>>(this.mock.getCustomersByTenantId(tenantId), HttpStatus.OK);
	}

	/**
	 * Returns a customer for the given customer id. Returns null if none is
	 * found.
	 *
	 * @param customerId
	 *            id for customer
	 * @return
	 */
	@RequestMapping(value = "/{tenantId}/{customerId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Customer> getSingleCustomer(@PathVariable int tenantId, @PathVariable int customerId) {
		Customer customerById = this.mock.getCustomerById(tenantId, customerId);
		if (customerById != null) {
			return new ResponseEntity<Customer>(customerById, HttpStatus.OK);
		}
		return new ResponseEntity<Customer>(customerById, HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/{tenantId}", method = RequestMethod.POST)
	public ResponseEntity createCustomer(@PathVariable int tenantId, @RequestBody Map<String, Object> body) {
		ObjectMapper mapper = new ObjectMapper();
		Customer retrievedCustomer = mapper.convertValue(body, Customer.class);
		if (this.mock.createCustomer(tenantId, retrievedCustomer)) {
			return new ResponseEntity(HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
	}

	@RequestMapping(value = "/{tenantId}/{customerId}", method = RequestMethod.PUT)
	public ResponseEntity changeCustomer(@PathVariable int tenantId, @PathVariable int customerId,
			@RequestBody Map<String, Object> body) {
		ObjectMapper mapper = new ObjectMapper();
		Customer retrievedCustomer = mapper.convertValue(body, Customer.class);
		if (this.mock.changeCustomer(tenantId, customerId, retrievedCustomer)) {
			return new ResponseEntity(HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
	}

	@RequestMapping(value = "/{tenantId}/{customerId}", method = RequestMethod.DELETE)
	public ResponseEntity deleteCustomer(@PathVariable int tenantId, @PathVariable int customerId) {
		if (this.mock.deleteCustomerById(tenantId, customerId)) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<String> info() {
		return new ResponseEntity<String>("Customer Microservice", HttpStatus.OK);
	}
}
