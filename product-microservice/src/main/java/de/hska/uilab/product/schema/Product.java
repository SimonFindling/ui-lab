package de.hska.uilab.product.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
	private int id;
	private int vendorId;
	private String productName;
	private String productImage;
	private String productInformation;
	private int price;

	public Product(@JsonProperty("id") int id, @JsonProperty("vendorId") int vendorId, @JsonProperty("productName") String productName,
			@JsonProperty("productImage") String productImage, @JsonProperty("productInformation") String productInformation,
			@JsonProperty("price") int price) {
		super();
		this.id = id;
		this.vendorId = vendorId;
		this.productName = productName;
		this.productImage = productImage;
		this.productInformation = productInformation;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVendorId() {
		return vendorId;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public String getProductInformation() {
		return productInformation;
	}

	public void setProductInformation(String productInformation) {
		this.productInformation = productInformation;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}
