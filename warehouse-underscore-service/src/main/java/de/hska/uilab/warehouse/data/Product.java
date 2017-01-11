package de.hska.uilab.warehouse.data;

public class Product {
	private Integer id;
	private Integer vendorId;
	private String productName;
	private String productImage;
	private String productInformation;
	private int price;
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductInformation() {
		return productInformation;
	}
	public void setProductInformation(String productInformation) {
		this.productInformation = productInformation;
	}
	public Integer getId() {
		return id;
	}
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	public String getProductImage() {
		return productImage;
	}
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getProductName() {
		return productName;
	}
}
