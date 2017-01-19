package de.hska.uilab.warehouse.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hska.uilab.warehouse.data.Warehouse;
import de.hska.uilab.warehouse.data.WarehousePlace;
import de.hska.uilab.warehouse.data.WarehousePlaceProduct;
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

	private final static Logger LOGGER = Logger.getLogger(DatabaseHandler.class.getName());

	public List<Warehouse> getAllWarehouses() {
		return (List<Warehouse>) warehouseRepository.findAll();
	}

	public long createWarehouse(Warehouse warehouse) {
		Warehouse dbw = warehouseRepository.findByName(warehouse.getName());
		if (dbw != null) {
			LOGGER.log(Level.INFO, "warehouse with name " + warehouse.getName() + " already exists");
			return -1;
		}
		warehouseRepository.save(warehouse);
		return warehouse.getId();
	}

	public List<WarehousePlace> getAllWarehousePlaces() {
		return (List<WarehousePlace>) warehousePlaceRepository.findAll();
	}

	public long createWarehousePlace(WarehousePlace warehousePlace) {
		if (warehousePlace.getWarehouse() == null)
			return -1;
		
		if(warehousePlace.getWarehouse().getId() == null)
			return -1;

		if(warehouseRepository.findOne(warehousePlace.getWarehouse().getId()) == null)
			return -1;
		
		List<WarehousePlace> dbWarehousePlace = warehousePlaceRepository.findByWarehouse(warehousePlace.getWarehouse());
		boolean alreadyInDB = false;
		for (WarehousePlace wp : dbWarehousePlace) {
			if (wp.getName().equals(warehousePlace.getName())) {
				alreadyInDB = true;
				break;
			}
		}
		if (alreadyInDB) {
			LOGGER.log(Level.INFO, "warehouseplace with name " + warehousePlace.getName() + " for warehouse "
					+ warehousePlace.getWarehouse().getId() + " already exsists in db.");
			return -1;
		}

		warehousePlaceRepository.save(warehousePlace);
		return warehousePlace.getId();
	}

	public List<WarehousePlace> getAllWarehousePlacesForWarehouseId(long warehouseId) {
		Warehouse dbWarehouse = warehouseRepository.findOne(warehouseId);
		return warehousePlaceRepository.findByWarehouse(dbWarehouse);
	}

	public Warehouse getWarehouseById(long warehouseId) {
		return warehouseRepository.findOne(warehouseId);
	}

	public Warehouse modifyWarehouse(long warehouseId, Warehouse warehouse) {
		LOGGER.log(Level.INFO, warehouseId+"");
		Warehouse w = warehouseRepository.findOne(warehouseId);
		LOGGER.log(Level.INFO, w+"");
		if (w == null)
			return null;
		else {
			w.setDescription(warehouse.getDescription());
			w.setName(warehouse.getName());
			warehouseRepository.save(w);
			return w;
		}
	}

	public WarehousePlace modifyWarehousePlace(long warehousePlaceId, WarehousePlace warehousePlace) {
		LOGGER.log(Level.INFO, warehousePlaceId+"");
		WarehousePlace w = warehousePlaceRepository.findOne(warehousePlaceId);
		LOGGER.log(Level.INFO, w+"");
		if (w == null)
			return null;
		else {
			w.setDescription(warehousePlace.getDescription());
			w.setName(warehousePlace.getName());
			warehousePlaceRepository.save(w);
			return w;
		}
	}

	public List<WarehousePlaceProduct> getWarehousePlaceProductForWarehousePlaceId(long warehousePlaceId) {
		List<WarehousePlaceProduct> dbWPP = warehousePlaceProductRepository.findByWarehouseplaceid(warehousePlaceId); 
		return dbWPP;
	}
	
	public List<WarehousePlaceProduct> getWarehousePlaceProductForProductId(Integer productId) {
		List<WarehousePlaceProduct> dbWPP = warehousePlaceProductRepository.findByProductid(productId); 
		return dbWPP;
	}
	
	public long createWarehousePlaceProduct(WarehousePlaceProduct warehousePlaceProduct) {
		warehousePlaceProductRepository.save(warehousePlaceProduct);
		return warehousePlaceProduct.getId();
	}
	
	public WarehousePlaceProduct modifyWarehousePlaceProduct(long warehousePlaceProductId, WarehousePlaceProduct warehousePlaceProduct) {
		LOGGER.log(Level.INFO, warehousePlaceProductId + "");
		WarehousePlaceProduct dbWhpProduct = warehousePlaceProductRepository.findById(warehousePlaceProductId);
		LOGGER.log(Level.INFO, dbWhpProduct +"");
		if (dbWhpProduct == null)
			return null;
		else {
			dbWhpProduct.setProductid(warehousePlaceProduct.getProductid());
			dbWhpProduct.setQuantity(warehousePlaceProduct.getQuantity());
			dbWhpProduct.setUnit(warehousePlaceProduct.getUnit());
			dbWhpProduct.setWarehouseplaceid(warehousePlaceProduct.getWarehouseplaceid());
			warehousePlaceProductRepository.save(dbWhpProduct);
			return dbWhpProduct;
		}
	}
}
