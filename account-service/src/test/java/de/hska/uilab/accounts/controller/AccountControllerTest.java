package de.hska.uilab.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.AccountType;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.model.TenantStatus;
import de.hska.uilab.accounts.repository.AccountRepository;
import de.hska.uilab.accounts.repository.ServiceRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */

/**
 * Created by mavogel on 11/23/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // clear the db before each test
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void shouldCreateAProspectAccount() throws Exception {
        Account createdAccount = accountRepository.save(Account.asProspect("test@mail.org"));
        Account testAcc = accountRepository.findOne(createdAccount.getId());
        assertEquals("test@mail.org", testAcc.getEmail());
        assertEquals(AccountType.TENANT, testAcc.getAccountType());
        assertEquals(null, testAcc.getTenantId());
        assertEquals(TenantStatus.PROSPECT, testAcc.getTenantStatus());
        assertTrue(testAcc.getUsername().isEmpty());
        assertTrue(testAcc.getFirstname().isEmpty());
        assertTrue(testAcc.getLastname().isEmpty());
        assertFalse(testAcc.getPassword().isEmpty());
    }


    @Test
    public void shouldCreateACustomerService() throws Exception {
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        Service testService = serviceRepository.findOne(Service.ServiceName.CUSTOMER);
        assertEquals(Service.ServiceName.CUSTOMER, testService.getName());
    }

    @Test
    public void shouldFindOneProspectAccount() throws Exception {
        // == prepare ==
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        Account createdAccount = accountRepository.save(Account.asProspect("testuser2@mail.org"));

        Service customerService = serviceRepository.findOne(Service.ServiceName.CUSTOMER);
        Account testuser2 = accountRepository.findOne(createdAccount.getId());
        testuser2.addService(customerService);
        accountRepository.save(testuser2);

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + createdAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("testuser2@mail.org"))
                .andExpect(jsonPath("$.username").isEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.tenantId").isEmpty())
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFindOneProspectAccountWithMultipleServices() throws Exception {
        // == prepare ==
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        serviceRepository.save(new Service(Service.ServiceName.PRODUCT));
        Account createdAccount = accountRepository.save(Account.asProspect("testuser2@mail.org"));

        Account testuser2 = accountRepository.findOne(createdAccount.getId());
        testuser2.addService(serviceRepository.findOne(Service.ServiceName.CUSTOMER));
        testuser2.addService(serviceRepository.findOne(Service.ServiceName.PRODUCT));
        accountRepository.save(testuser2);

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + createdAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("testuser2@mail.org"))
                .andExpect(jsonPath("$.username").isEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.tenantId").isEmpty())
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$.services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateANewProspectAccount() throws Exception {
        Map<String, String> newProspectAccount = new HashMap<>();
        newProspectAccount.put("email", "test@mail.org");

        this.mockMvc.perform(post("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProspectAccount)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldCreateAndFindOneUserAccount() throws Exception {
        // == prepare ==
        // 1: creates services and tenant account
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        serviceRepository.save(new Service(Service.ServiceName.PRODUCT));
        Account tenantAccount = accountRepository.save(Account.asProspect("prospectAcc@mail.org"));
        tenantAccount = accountRepository.findOne(tenantAccount.getId());
        tenantAccount.addService(serviceRepository.findOne(Service.ServiceName.CUSTOMER));
        tenantAccount.addService(serviceRepository.findOne(Service.ServiceName.PRODUCT));
        accountRepository.save(tenantAccount);

        // 2: build and save user acc
        Map<String, String> newUserAccount = new HashMap<>();
        newUserAccount.put("firstname", "John");
        newUserAccount.put("lastname", "Doe");
        newUserAccount.put("email", "newuser@mail.org");

        this.mockMvc.perform(post("/accounts/" + tenantAccount.getId())
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newUserAccount)))
                .andExpect(status().isCreated());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/2").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("newuser@mail.org"))
                .andExpect(jsonPath("$.username").isEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.tenantStatus").isEmpty())
                .andExpect(jsonPath("$.tenantId").value(tenantAccount.getId()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$.services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllAccounts() throws Exception {
        // == prepare ==
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        serviceRepository.save(new Service(Service.ServiceName.SALES));
        serviceRepository.save(new Service(Service.ServiceName.PRODUCT));
        Account account3 = accountRepository.save(Account.asProspect("testuser3@mail.org"));
        accountRepository.save(Account.asProspect("testuser4@mail.org"));
        accountRepository.save(Account.asProspect("testuser5@mail.org"));
        Account account6 = accountRepository.save(Account.asProspect("testuser6@mail.org"));

        Account testuser3 = accountRepository.findOne(account3.getId());
        testuser3.addService(serviceRepository.findOne(Service.ServiceName.CUSTOMER));
        accountRepository.save(testuser3);

        Account testuser6 = accountRepository.findOne(account6.getId());
        testuser6.addService(serviceRepository.findOne(Service.ServiceName.CUSTOMER));
        testuser6.addService(serviceRepository.findOne(Service.ServiceName.SALES));
        testuser6.addService(serviceRepository.findOne(Service.ServiceName.PRODUCT));
        accountRepository.save(testuser6);

        // == go / verify ==
        this.mockMvc.perform(get("/accounts").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                // 1
                .andExpect(jsonPath("$[0].id").value(account3.getId()))
                .andExpect(jsonPath("$[0].email").value("testuser3@mail.org"))
                .andExpect(jsonPath("$[0].username").isEmpty())
                .andExpect(jsonPath("$[0].password").isNotEmpty())
                .andExpect(jsonPath("$[0].tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$[0].tenantId").isEmpty())
                .andExpect(jsonPath("$[0].accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$[0].firstname").isEmpty())
                .andExpect(jsonPath("$[0].lastname").isEmpty())
                .andExpect(jsonPath("$[0].company").isEmpty())
                .andExpect(jsonPath("$[0].services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                // 2
                .andExpect(jsonPath("$[1].email").value("testuser4@mail.org"))
                .andExpect(jsonPath("$[1].username").isEmpty())
                .andExpect(jsonPath("$[1].password").isNotEmpty())
                .andExpect(jsonPath("$[1].tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$[1].tenantId").isEmpty())
                .andExpect(jsonPath("$[1].accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$[1].firstname").isEmpty())
                .andExpect(jsonPath("$[1].lastname").isEmpty())
                .andExpect(jsonPath("$[1].company").isEmpty())
                .andExpect(jsonPath("$[1].services").isEmpty())
                // 3
                .andExpect(jsonPath("$[2].email").value("testuser5@mail.org"))
                .andExpect(jsonPath("$[2].username").isEmpty())
                .andExpect(jsonPath("$[2].password").isNotEmpty())
                .andExpect(jsonPath("$[2].tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$[2].tenantId").isEmpty())
                .andExpect(jsonPath("$[2].accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$[2].firstname").isEmpty())
                .andExpect(jsonPath("$[2].lastname").isEmpty())
                .andExpect(jsonPath("$[2].company").isEmpty())
                .andExpect(jsonPath("$[2].services").isEmpty())
                // 4
                .andExpect(jsonPath("$[3].id").value(account6.getId()))
                .andExpect(jsonPath("$[3].email").value("testuser6@mail.org"))
                .andExpect(jsonPath("$[3].username").isEmpty())
                .andExpect(jsonPath("$[3].password").isNotEmpty())
                .andExpect(jsonPath("$[3].tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$[3].tenantId").isEmpty())
                .andExpect(jsonPath("$[3].accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$[3].firstname").isEmpty())
                .andExpect(jsonPath("$[3].lastname").isEmpty())
                .andExpect(jsonPath("$[3].company").isEmpty())
                .andExpect(jsonPath("$[3].services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$[3].services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$[3].services[2].name").value(Service.ServiceName.SALES.name()))
                //
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateAccountsMailAndCompany() throws Exception {
        Account baseAccount = accountRepository.save(Account.asProspect("testuser2@mail.org"));

        Map<String, String> updatedAccount = new HashMap<>();
        updatedAccount.put("id", String.valueOf(baseAccount.getId()));
        updatedAccount.put("username", "testuser2");
        updatedAccount.put("email", "test123@mail.org");
        updatedAccount.put("company", "VR Stuff");

        this.mockMvc.perform(put("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updatedAccount)))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + baseAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("test123@mail.org"))
                .andExpect(jsonPath("$.username").isEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.tenantId").isEmpty())
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").value("VR Stuff"))
                .andExpect(jsonPath("$.services").isEmpty())
                .andExpect(status().isOk());
    }
//
//    @Test
//    public void shouldUpgradeToCustomer() throws Exception {
//        accountRepository.save(Account.asProspect("testuser2@mail.org"));
//
//        this.mockMvc.perform(put("/accounts/upgrade/testuser2")
//                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        this.mockMvc.perform(get("/accounts/testuser2").accept(MediaType.APPLICATION_JSON)
//                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
//                .andExpect(jsonPath("$.email").value("testuser2@mail.org"))
//                .andExpect(jsonPath("$.username").value("testuser2"))
//                .andExpect(jsonPath("$.password").isNotEmpty())
//                .andExpect(jsonPath("$.status").value(TenantStatus.CUSTOMER.name()))
//                .andExpect(jsonPath("$.firstname").isEmpty())
//                .andExpect(jsonPath("$.lastname").isEmpty())
//                .andExpect(jsonPath("$.company").isEmpty())
//                .andExpect(jsonPath("$.services").isEmpty())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Ignore // TODO how to add lists with JSON
//    public void shouldAddSalesAndProductServiceToCustomer() throws Exception {
//        accountRepository.save(Account.asProspect("testuser2@mail.org"));
//
//        Map<String, String> salesService = new HashMap<>();
//        salesService.put("name", "SALES");
//
//        this.mockMvc.perform(put("/accounts/addservice/testuser2")
//                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(this.objectMapper.writeValueAsString(salesService)))
//                .andExpect(status().isOk());
//
//        this.mockMvc.perform(get("/accounts/testuser2").accept(MediaType.APPLICATION_JSON)
//                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
//                .andExpect(jsonPath("$.email").value("testuser2@mail.org"))
//                .andExpect(jsonPath("$.username").value("testuser2"))
//                .andExpect(jsonPath("$.password").isNotEmpty())
//                .andExpect(jsonPath("$.status").value(TenantStatus.CUSTOMER.name()))
//                .andExpect(jsonPath("$.firstname").isEmpty())
//                .andExpect(jsonPath("$.lastname").isEmpty())
//                .andExpect(jsonPath("$.company").isEmpty())
//                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.SALES.name()))
//                .andExpect(status().isOk());
//    }
//
//
//    @Test
//    public void shouldDeleteAccount() throws Exception {
//        accountRepository.save(Account.asProspect("testuser2@mail.org"));
//
//        this.mockMvc.perform(delete("/accounts/testuser2")
//                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//    }

}