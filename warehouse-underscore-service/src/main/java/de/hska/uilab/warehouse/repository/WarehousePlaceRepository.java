package de.hska.uilab.warehouse.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.hska.uilab.warehouse.data.Warehouse;
import de.hska.uilab.warehouse.data.WarehousePlace;

public interface WarehousePlaceRepository extends CrudRepository<WarehousePlace, Long> {
	List<WarehousePlaceRepository> findByWarehouse(Warehouse warehouse);
}
