package de.hska.uilab.vendor;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.hska.uilab.vendor.data.Address;
import de.hska.uilab.vendor.data.Vendor;
import de.hska.uilab.vendor.repository.VendorRepository;
import de.hska.uilab.vendor.service.DatabaseHandler;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VendorServiceTest {
	
	@Autowired
	private DatabaseHandler testee;
	
	@Autowired
	VendorRepository vendorRepository;
	
	@Test
	public void contextLoads() {
		
	}
	
	@Before
    public void setUp() {
	}
	
	private void writeVendorWithAddressToDB(int vendorId, String vendorName, long tenantId, String city){
		Vendor testV = new Vendor();
		testV.setName(vendorName);
		testV.setTenantId(tenantId);
		Address testA = new Address();
		testA.setCity(city);
		testV.setAddress(testA);
		vendorRepository.save(testV);
	}
	
	@Test
	public void getVendorsByTenantIdTest(){
		writeVendorWithAddressToDB(1, "name", 66, "city");
		writeVendorWithAddressToDB(2, "name2", 662, "city2");
		
		List<Vendor> retList = testee.getVendorsByTenantId(66);
		assertEquals(1, retList.size());
		assertEquals("name", retList.get(0).getName());
		assertEquals(new Long(66), retList.get(0).getTenantId());
		assertEquals("city", retList.get(0).getAddress().getCity());
		
		List<Vendor> retList2 = testee.getVendorsByTenantId(123456);
		assertEquals(0, retList2.size());
		
	}
	
	@Test
	public void getVendorByTenantIdAndIdTest(){
		writeVendorWithAddressToDB(1, "name", 66, "city");
		writeVendorWithAddressToDB(2, "name2", 662, "city2");
		
		Vendor ret= testee.getVendorByTenantIdAndId(66, 1);
		assertEquals("name", ret.getName());
		assertEquals(new Long(66), ret.getTenantId());
		assertEquals(new Long(1), ret.getVendorId());
		assertEquals("city", ret.getAddress().getCity());
		
		Vendor ret2 = testee.getVendorByTenantIdAndId(123456, 1);
		assertEquals(null, ret2);
	}
	
	@Test
	public void createVendorForTenantTest(){
		writeVendorWithAddressToDB(1, "name", 2, "city");
		writeVendorWithAddressToDB(1, "name3", 3, "city3");
		Vendor newV = new Vendor();
		newV.setName("name2");
		newV.setTenantId(2l);
		Address newA = new Address();
		newA.setCity("city2");
		newV.setAddress(newA);
		
		testee.createVendorForTenant(newV, 2);
		
		List<Vendor> retList = vendorRepository.findByTenantId(2l);
		assertEquals(2, retList.size());
		assertEquals(new Long(2), retList.get(0).getTenantId());
		assertEquals("name", retList.get(0).getName());
		assertEquals("city", retList.get(0).getAddress().getCity());
		assertEquals("name2", retList.get(1).getName());
		assertEquals("city2", retList.get(1).getAddress().getCity());
	}
	
	@Test
	public void modifyVendorForTenantTest(){
		writeVendorWithAddressToDB(1, "name", 2, "city");
		Vendor retV = testee.modifyVendorForTenant(new Vendor(), 2, 2);
		assertNull(retV);
		
		Vendor mV = new Vendor();
		mV.setName("modifiedName");
		Address a = new Address();
		a.setCity("modifiedCity");
		mV.setAddress(a);
		
		Vendor retV2 = testee.modifyVendorForTenant(mV, 2, 1);
		assertNotNull(retV2);
		assertEquals("modifiedName", retV2.getName());
		assertEquals("modifiedCity", retV2.getAddress().getCity());
	}

	@Test
	public void deleteVendorForTenantTest(){
		writeVendorWithAddressToDB(1, "name", 2, "city");
		//tenantid vendorid
		boolean retB1 = testee.deleteVendorForTenant(2, 3);
		assertFalse(retB1);
		assertEquals(1, ((List<Vendor>)vendorRepository.findAll()).size());
		boolean retB2 = testee.deleteVendorForTenant(2, 2);
		assertFalse(retB2);
		assertEquals(1, ((List<Vendor>)vendorRepository.findAll()).size());
		boolean retB3 = testee.deleteVendorForTenant(2, 1);
		assertTrue(retB3);
		assertEquals(0, ((List<Vendor>)vendorRepository.findAll()).size());
	}
}
