package de.hska.uilab.vendor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hska.uilab.vendor.data.Vendor;
import de.hska.uilab.vendor.repository.AddressRepository;
import de.hska.uilab.vendor.repository.VendorRepository;

@Service
public class DatabaseHandler {
	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	AddressRepository addressRepositoty;

	public List<Vendor> getVendorsByTenantId(long tenantId) {
		return vendorRepository.findByTenantId(tenantId);
	}

	/**
	 * returns vendor specified with given tenantid and vendorid. if no object
	 * is found, null will be returned.
	 *
	 * @param tenantId
	 * @param vendorId
	 * @return
	 */
	public Vendor getVendorByTenantIdAndId(long tenantId, long vendorId) {
		List<Vendor> vendors = vendorRepository.findByTenantId(tenantId);
		for (Vendor v : vendors) {
			if (v.getVendorId() == vendorId)
				return v;
		}
		return null;
	}

    public Vendor getVendorById(long vendorId) {
        Vendor vendor = vendorRepository.findOne(vendorId);
        if (vendor != null) {
            return vendor;
        }
        return null;
    }

	/**
	 * creates new vendor object in db. the given tenantid will be set.
	 *
	 * @param vendor
	 * @param tenantId
	 * @return
	 */
	public boolean createVendorForTenant(Vendor vendor, long tenantId) {
		Vendor newVendor = vendor.getNewVendorForTenant(tenantId);
		vendorRepository.save(newVendor);
		return true;
	}

	/**
	 * modify the vendor in db, defined by the id in the vendor-object. If there
	 * is no vendor in db with that db, null will be returned.
	 *
	 * @param vendor
	 * @param tenantId
	 * @return
	 */
	public Vendor modifyVendorForTenant(Vendor vendor, long tenantId, long vendorId) {
		Vendor v = getVendorByTenantIdAndId(tenantId, vendorId);
		if (v == null)
			return null;
		else {
			vendor.setTenantId(tenantId);
			vendor.setVendorId(vendorId);
			vendorRepository.save(vendor);
			return vendor;
		}
	}

	/**
	 * deletes vendor specified by tenantid and vendorid. returns false if
	 * object couldn't be deleted.
	 *
	 * @param tenantId
	 * @param vendorId
	 */
	public boolean deleteVendorForTenant(long tenantId, long vendorId) {
		Vendor v = getVendorByTenantIdAndId(tenantId, vendorId);
		if (v != null) {
			vendorRepository.delete(v);
			return true;
		} else
			return false;
	}
}
