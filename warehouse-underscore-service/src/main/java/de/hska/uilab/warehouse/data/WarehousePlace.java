package de.hska.uilab.warehouse.data;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity(name = "warehouseplace")
public class WarehousePlace {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@OneToOne
	@JoinColumn(name = "warehouse_id")
	private Warehouse warehouse;
	
	@OneToMany
	@JoinColumn(name="warehouseplace_id", referencedColumnName="id")
	private List<WarehousePlaceProduct> warehousePlaceProduct;
	
	public WarehousePlace(String name, String description, Warehouse warehouse){
		this.name = name;
		this.description = description;
		this.warehouse = warehouse;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Long getId() {
		return id;
	}

	public List<WarehousePlaceProduct> getWarehousePlaceProduct() {
		return warehousePlaceProduct;
	}

	public void setWarehousePlaceProduct(List<WarehousePlaceProduct> warehousePlaceProduct) {
		this.warehousePlaceProduct = warehousePlaceProduct;
	}
}
