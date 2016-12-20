package de.hska.uilab.vendor.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "address")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "street")
	private String street;
	@Column(name = "number")
	private String number;
	@Column(name = "postal")
	private String postal;
	@Column(name = "city")
	private String city;
	@Column(name = "country")
	private String country;
	
	public Address(){}
	public Address(String street, String number, String postal, String city, String country){
		this.street = street;
		this.number = number;
		this.postal = postal;
		this.city = city;
		this.country = country;
	}
	
	public Integer getId() {
		return id;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPostal() {
		return postal;
	}
	public void setPostal(String postal) {
		this.postal = postal;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
}