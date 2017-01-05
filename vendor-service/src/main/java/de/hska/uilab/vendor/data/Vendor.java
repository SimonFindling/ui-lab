package de.hska.uilab.vendor.data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "vendor")
public class Vendor {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "vendorid")
	private Long vendorId;
	@Column(name = "name")
	private String name;
	@Column(name = "email")
	private String email;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "addressid")
	private Address address;
	@Column(name = "tenantid")
	private Long tenantId;
	
	public Vendor(){}
	public Vendor(Long vendorId, String name, String email, Address address){
		this.vendorId = vendorId;
		this.name = name;
		this.email = email;
		this.address = address;
	}
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	public Vendor getNewVendorForTenant(Long tenantId){
		Vendor newVendor = new Vendor();
		newVendor.address = address;
		newVendor.email = email;
		newVendor.name = name;
		newVendor.tenantId = tenantId;
		return newVendor;
	}
}