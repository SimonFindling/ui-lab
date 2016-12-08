package de.hska.uilab.customer.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {

	private String street;
	private String number; // string because of numbers like 4a
	private String postal;
	private String city;
	private String country;

	public Address(
			@JsonProperty("street") String street,
			@JsonProperty("number") String number,
			@JsonProperty("postal") String postal,
			@JsonProperty("city") String city,
			@JsonProperty("country") String country
		) {
		//super();

		this.street = street;
		this.number = number;
		this.postal = postal;
		this.city = city;
		this.country = country;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPostal() {
		return this.postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
