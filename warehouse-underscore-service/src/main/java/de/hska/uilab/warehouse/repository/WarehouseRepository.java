package de.hska.uilab.warehouse.repository;

import org.springframework.data.repository.CrudRepository;

import de.hska.uilab.warehouse.data.Warehouse;

public interface WarehouseRepository extends CrudRepository<Warehouse, Long>{
	Warehouse findByName(String name);
}
