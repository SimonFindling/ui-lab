package de.hska.uilab.vendor.repository;

import org.springframework.data.repository.CrudRepository;

import de.hska.uilab.vendor.data.Address;
import de.hska.uilab.vendor.data.Vendor;

public interface AddressRepository extends CrudRepository<Address, Long>{

}
