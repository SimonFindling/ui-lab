package de.hska.uilab.warehouse.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.hska.uilab.warehouse.data.WarehousePlaceProduct;

public interface WarehousePlaceProductRepository extends CrudRepository<WarehousePlaceProduct, Long>{
	List<WarehousePlaceProduct> findByProductid(Integer productid);
	List<WarehousePlaceProduct> findByWarehouseplaceid(Long warehouseplaceid);
}
