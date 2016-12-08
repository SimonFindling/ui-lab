package de.hska.uilab.customer.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {

	private int id;
	private int tenantId;
	private String firstName;
	private String lastName;
	private String email;
	private String description;
	private Address address;

	public Customer(
			@JsonProperty("id") int id,
			@JsonProperty("tenantId") int tenantId,
			@JsonProperty("firstName") String firstName,
			@JsonProperty("lastName") String lastName,
			@JsonProperty("email") String email,
			@JsonProperty("description") String description,
			@JsonProperty("address") Address address) {
		//super();

		this.id = id;
		this.tenantId = tenantId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.description = description;
		this.address = address;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * Calculated from fixed sales orders.
	 * [read-only]
	 */
	public int getRevenue() {
		return 0; // TODO
	}

}
