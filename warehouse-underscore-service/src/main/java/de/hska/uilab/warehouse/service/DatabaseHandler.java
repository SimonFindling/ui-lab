package de.hska.uilab.warehouse.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hska.uilab.warehouse.data.Warehouse;
import de.hska.uilab.warehouse.repository.WarehousePlaceProductRepository;
import de.hska.uilab.warehouse.repository.WarehousePlaceRepository;
import de.hska.uilab.warehouse.repository.WarehouseRepository;

@Service
public class DatabaseHandler {
	@Autowired
	WarehouseRepository warehouseRepository;
	
	@Autowired
	WarehousePlaceRepository warehousePlaceRepository;
	
	@Autowired
	WarehousePlaceProductRepository warehousePlaceProductRepository;

	public List<Warehouse> getAllWarehouses() {
		return (List<Warehouse>) warehouseRepository.findAll();
	}
}
