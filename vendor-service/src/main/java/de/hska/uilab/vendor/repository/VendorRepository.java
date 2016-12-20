package de.hska.uilab.vendor.repository;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.hska.uilab.vendor.data.Vendor;

public interface VendorRepository extends CrudRepository<Vendor, Long>{
	List<Vendor> findByTenantId(int tenantId);
}
