package de.hska.uilab.customer.schema;

import java.util.ArrayList;
import java.util.List;

public class CustomerMock {

	private List<Customer> all = new ArrayList<Customer>();
	private static int currentId = 0;

	public CustomerMock() {
		this.addNewCustomers(new Customer[] {
				new Customer(1, 1, "Rudolf", "Reindeer", "first customer", "rudolf@reindeer.com",
						new Address("Northstreet", "1a", "00001", "Stable", "Northpole")),
				new Customer(2, 1, "Hannes", "Müller", "second customer", "hannes@mueller.de",
						new Address("Hauptstraße", "3", "76131", "Karlsruhe", "Germany")),
				new Customer(3, 2, "Charles", "Dickens", "thirdcustomer", "info@dickens.net",
						new Address("Doughty Street", "48", "14048", "London", "Great Britain"))
		});
	}

	private boolean addNewCustomer(Customer customer) {
		customer.setId(++CustomerMock.currentId);
		this.all.add(customer);
		return true;
	}

	private boolean addNewCustomers(Customer[] customers) {
		for (Customer customer : customers) {
			if (!this.addNewCustomer(customer)) {
				return false;
			}
		}
		return true;
	}

	public List<Customer> getAllCustomers() {
		return this.all;
	}

	public List<Customer> getCustomersByTenantId(int id) {
		List<Customer> customers = new ArrayList<>();
		for (Customer customer : this.all) {
			if (customer.getTenantId() == id) {
				customers.add(customer);
			}
		}
		return customers;
	}

	public Customer getCustomerById(int tenantId, int id) {
		for (Customer customer : this.getCustomersByTenantId(tenantId)) {
			if (customer.getId() == id) {
				return customer;
			}
		}
		return null;
	}

	public boolean createCustomer(int tenantId, Customer customer) {
		customer.setTenantId(tenantId);
		return this.addNewCustomer(customer);
	}

	public boolean changeCustomer(int tenantId, int customerId, Customer customer) {
		Customer customerById = this.getCustomerById(tenantId, customerId);
		if (customerById != null) {
			this.all.remove(customerById);
			customer.setId(customerId);
			customer.setTenantId(tenantId);
			this.all.add(customer);
			return true;
		}
		return false;
	}

	public boolean deleteCustomerById(int tenantId, int customerId) {
		Customer customerById = this.getCustomerById(tenantId, customerId);
		if (customerById != null) {
			return this.all.remove(customerById);
		}
		return false;
	}

}
