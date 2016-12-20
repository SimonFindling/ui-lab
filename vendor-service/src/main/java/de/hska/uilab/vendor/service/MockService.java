package de.hska.uilab.vendor.service;

import java.util.ArrayList;
import java.util.List;

import de.hska.uilab.vendor.data.Address;
import de.hska.uilab.vendor.data.Vendor;

public class MockService {
	public List<Vendor> getVendorList(int i){
		List<Vendor> retList = new ArrayList<>();
		for(int j = 0; j < i; j++){
			retList.add(getVendor(j));
		}
		return retList;
	}
	
	public Vendor getVendor(int i){
		Vendor retVendor = new Vendor();
		retVendor.setAddress(getAddress(i));
		retVendor.setEmail("email " + i);
		retVendor.setVendorId(i);
		retVendor.setName("name " + i);
		return retVendor;
	}
	
	private Address getAddress(int i){
		Address retAddress = new Address();
		retAddress.setCity("city " + i);
		retAddress.setCountry("country " + i);
		retAddress.setNumber(""+i);
		retAddress.setPostal(""+i);
		retAddress.setStreet("street " + i);
		return retAddress;
	}

	public boolean createVendorForTenant(Vendor vendor, String tenantId) {
		return true;
	}

	public Vendor modifyVendorForTenant(int id, Vendor vendor, int tenantId) {
		return getVendor(id);
	}

	public boolean deleteVendorForTenant(int id, int tenantId) {
		return true;
	}
}
