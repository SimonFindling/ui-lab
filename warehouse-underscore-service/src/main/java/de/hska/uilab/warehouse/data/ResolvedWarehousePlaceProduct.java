package de.hska.uilab.warehouse.data;

public class ResolvedWarehousePlaceProduct {
	private Long id;
	private Long warehouseplaceid;
	private int quantity;
	private Unit unit;
	
	private Integer productid;
	private Integer vendorId;
	private String productName;
	private String productImage;
	private String productInformation;
	private int price;
	
	public Long getWarehouseplace() {
		return warehouseplaceid;
	}

	public void setWarehouseplaceid(Long warehouseplaceid) {
		this.warehouseplaceid = warehouseplaceid;
	}

	public Integer getProductid() {
		return productid;
	}

	public void setProductid(Integer productid) {
		this.productid = productid;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	public void setId(Long id){
		this.id = id;
	}

	public enum Unit {
		PIECE, BOX
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductInformation() {
		return productInformation;
	}
	public void setProductInformation(String productInformation) {
		this.productInformation = productInformation;
	}
	public Long getId() {
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
