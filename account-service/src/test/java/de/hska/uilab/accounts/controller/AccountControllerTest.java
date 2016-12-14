package de.hska.uilab.accounts.controller;

import de.hska.uilab.accounts.AbstractTestBase;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.AccountType;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.model.TenantStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
public class AccountControllerTest extends AbstractTestBase {

    @Before
    public void setUp() {
        createServices();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void shouldCreateACustomerService() throws Exception {
        Service testService = serviceRepository.findOne(Service.ServiceName.CUSTOMER);
        assertEquals(Service.ServiceName.CUSTOMER, testService.getName());
    }

    @Test
    public void shouldCreateATenantAccount() throws Exception {
        Map<String, String> newProspectAccount = new HashMap<>();
        newProspectAccount.put("email", "test@mail.org");

        this.mockMvc.perform(post("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProspectAccount)))
                .andExpect(status().isCreated());

        Account createdAcc = this.accountRepository.findOne(1L);
        assertEquals(Service.getProspectStandardServices().size(), createdAcc.getServices().size());
    }


    @Test
    public void shouldFailBecauseTenantExistAlready() throws Exception {
        Map<String, String> newProspectAccount = new HashMap<>();
        newProspectAccount.put("email", "test@mail.org");

        this.mockMvc.perform(post("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProspectAccount)))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProspectAccount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailBecauseThisEmailExistAlready() throws Exception {
        Service customer = serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        Service sales = serviceRepository.save(new Service(Service.ServiceName.PRODUCT));
        Account tenantAccount = accountRepository.save(Account.asProspect("newuser@mail.org"));
        tenantAccount.addServices(customer, sales);
        accountRepository.save(tenantAccount);

        Map<String, String> newUserAccount = new HashMap<>();
        newUserAccount.put("firstname", "John");
        newUserAccount.put("lastname", "Doe");
        newUserAccount.put("email", "newuser@mail.org");

        this.mockMvc.perform(post("/accounts/" + tenantAccount.getId())
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newUserAccount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFindAProspectAccount() throws Exception {
        // == prepare ==
        Account createdAccount = createSampleTenantAccount("testuser2@mail.org");

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
                .andExpect(jsonPath("$.services[2].name").value(Service.ServiceName.VENDOR.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllAccounts() throws Exception {
        // == prepare ==
        Account account3 = createSampleTenantAccount("testuser3@mail.org");
        createSampleTenantAccount("testuser4@mail.org");
        createSampleTenantAccount("testuser5@mail.org");
        Account account6 = createSampleTenantAccount("testuser6@mail.org");

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
                .andExpect(jsonPath("$[0].services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$[0].services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$[0].services[2].name").value(Service.ServiceName.VENDOR.name()))
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
                .andExpect(jsonPath("$[1].services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$[1].services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$[1].services[2].name").value(Service.ServiceName.VENDOR.name()))
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
                .andExpect(jsonPath("$[2].services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$[2].services[1].name").value(Service.ServiceName.CUSTOMER.name()))// 4
                .andExpect(jsonPath("$[2].services[2].name").value(Service.ServiceName.VENDOR.name())).andExpect(jsonPath("$[3].id").value(account6.getId()))
                // 4
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
                .andExpect(jsonPath("$[3].services[2].name").value(Service.ServiceName.VENDOR.name()))
                //
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateAccountsMailAndCompany() throws Exception {
        // == prepare ==
        Account baseAccount = createSampleTenantAccount("testuser2@mail.org");

        Map<String, String> updatedAccount = new HashMap<>();
        updatedAccount.put("id", String.valueOf(baseAccount.getId()));
        updatedAccount.put("username", "testuser2");
        updatedAccount.put("email", "test123@mail.org");
        updatedAccount.put("company", "VR Stuff");

        this.mockMvc.perform(patch("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updatedAccount)))
                .andExpect(status().isOk());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + baseAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("test123@mail.org"))
                .andExpect(jsonPath("$.username").value("testuser2"))
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.tenantId").isEmpty())
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").value("VR Stuff"))
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$.services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$.services[2].name").value(Service.ServiceName.VENDOR.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpgradeProspectToCustomer() throws Exception {
        // == prepare ==
        Account baseAccount = createSampleTenantAccount("testuser2@mail.org");

        this.mockMvc.perform(patch("/accounts/" + baseAccount.getId() + "/upgrade")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + baseAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("testuser2@mail.org"))
                .andExpect(jsonPath("$.username").isEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.CUSTOMER.name()))
                .andExpect(jsonPath("$.tenantId").isEmpty())
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$.services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$.services[2].name").value(Service.ServiceName.VENDOR.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpgradeProspectWithUsersToACustomer() throws Exception {
        // == prepare ==
        Account tenantAccount = createSampleTenantAccount("tenant@mail.org");
        Account doe1 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe1", "doe1@mail.org");
        Account doe2 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe2", "doe2@mail.org");
        Account doe3 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe3", "doe3@mail.org");

        Account tenantAccount2 = createSampleTenantAccount("tenant2@mail.org");
        Account doe4 = createSampleUserAccount(tenantAccount2.getId(), "John", "Doe4", "doe4@mail.org");

        this.mockMvc.perform(patch("/accounts/" + tenantAccount.getId() + "/upgrade")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + tenantAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.CUSTOMER.name()))
                .andExpect(jsonPath("$.tenantId").isEmpty())
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/accounts/" + doe1.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.CUSTOMER.name()))
                .andExpect(jsonPath("$.tenantId").value(tenantAccount.getId()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.lastname").value(doe1.getLastname()))
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + doe2.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.CUSTOMER.name()))
                .andExpect(jsonPath("$.tenantId").value(tenantAccount.getId()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.lastname").value(doe2.getLastname()))
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + doe3.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.CUSTOMER.name()))
                .andExpect(jsonPath("$.tenantId").value(tenantAccount.getId()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.lastname").value(doe3.getLastname()))
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + tenantAccount2.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.tenantId").isEmpty())
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/accounts/" + doe4.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.tenantId").value(tenantAccount2.getId()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.lastname").value(doe4.getLastname()))
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(status().isOk());

    }

    @Test
    public void shouldAddSalesServiceToCustomer() throws Exception {
        // == prepare ==
        Account tenantAccount = createSampleTenantAccount("tenant@mail.org");
        Account doe1 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe1", "doe1@mail.org");
        Account doe2 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe2", "doe2@mail.org");

        List<Map<String, String>> servicesToAdd = new ArrayList<>();
        Map<String, String> salesService = new HashMap<>();
        salesService.put("name", "SALES");
        servicesToAdd.add(salesService);

        this.mockMvc.perform(patch("/accounts/" + tenantAccount.getId() + "/addservice")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(servicesToAdd)))
                .andExpect(status().isOk());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + tenantAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("tenant@mail.org"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$.services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$.services[2].name").value(Service.ServiceName.SALES.name()))
                .andExpect(jsonPath("$.services[3].name").value(Service.ServiceName.VENDOR.name()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + doe1.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("doe1@mail.org"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$.services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$.services[2].name").value(Service.ServiceName.SALES.name()))
                .andExpect(jsonPath("$.services[3].name").value(Service.ServiceName.VENDOR.name()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + doe2.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("doe2@mail.org"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.PRODUCT.name()))
                .andExpect(jsonPath("$.services[1].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$.services[2].name").value(Service.ServiceName.SALES.name()))
                .andExpect(jsonPath("$.services[3].name").value(Service.ServiceName.VENDOR.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAddSalesServiceToCustomerAndItsUsers() throws Exception {
        // == prepare ==
        Account accountToAddServices = createSampleTenantAccount("tenant@mail.org");

        List<Map<String, String>> servicesToAdd = new ArrayList<>();
        Map<String, String> salesService = new HashMap<>();
        salesService.put("name", "SALES");
        servicesToAdd.add(salesService);

        this.mockMvc.perform(patch("/accounts/" + accountToAddServices.getId() + "/addservice")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(servicesToAdd)))
                .andExpect(status().isOk());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + accountToAddServices.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("tenant@mail.org"))
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
                .andExpect(jsonPath("$.services[2].name").value(Service.ServiceName.SALES.name()))
                .andExpect(jsonPath("$.services[3].name").value(Service.ServiceName.VENDOR.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRemoveVendorAndProductServiceToCustomer() throws Exception {
        // == prepare ==
        Account tenantAccount = createSampleTenantAccount("tenant@mail.org");
        Account doe1 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe1", "doe1@mail.org");
        Account doe2 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe2", "doe2@mail.org");

        List<Map<String, String>> servicesToRemove = new ArrayList<>();
        Map<String, String> salesService = new HashMap<>();
        salesService.put("name", "VENDOR");
        Map<String, String> productService = new HashMap<>();
        productService.put("name", "PRODUCT");

        servicesToRemove.add(salesService);
        servicesToRemove.add(productService);

        this.mockMvc.perform(patch("/accounts/" + tenantAccount.getId() + "/rmservice")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(servicesToRemove)))
                .andExpect(status().isOk());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + tenantAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("tenant@mail.org"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.accountType").value(AccountType.TENANT.name()))
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + doe1.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("doe1@mail.org"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/" + doe2.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("doe2@mail.org"))
                .andExpect(jsonPath("$.tenantStatus").value(TenantStatus.PROSPECT.name()))
                .andExpect(jsonPath("$.accountType").value(AccountType.USER.name()))
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRemoveSalesAndProductServiceToCustomerAndItsUsers() throws Exception {
        // == prepare ==
        Account accountToRemoveServices = createSampleTenantAccount("testuser2@mail.org");

        List<Map<String, String>> servicesToRemove = new ArrayList<>();
        Map<String, String> salesService = new HashMap<>();
        salesService.put("name", "VENDOR");
        Map<String, String> productService = new HashMap<>();
        productService.put("name", "PRODUCT");

        servicesToRemove.add(salesService);
        servicesToRemove.add(productService);

        this.mockMvc.perform(patch("/accounts/" + accountToRemoveServices.getId() + "/rmservice")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(servicesToRemove)))
                .andExpect(status().isOk());

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/" + accountToRemoveServices.getId()).accept(MediaType.APPLICATION_JSON)
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
    public void shouldDeleteAccount() throws Exception {
        // == prepare ==
        Account accountToDelete = createSampleTenantAccount("testuser2@mail.org");

        // == go / verify ==
        this.mockMvc.perform(delete("/accounts/" + accountToDelete.getId())
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertNull(accountRepository.findOne(accountToDelete.getId()));
        this.mockMvc.perform(get("/accounts/" + accountToDelete.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldDeleteTenantAndItsUserAccounts() throws Exception {
        // == prepare ==
        Account tenantAccount = createSampleTenantAccount("tenant@mail.org");
        Account doe1 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe1", "doe1@mail.org");
        Account doe2 = createSampleUserAccount(tenantAccount.getId(), "John", "Doe2", "doe2@mail.org");

        // == go / verify ==
        this.mockMvc.perform(delete("/accounts/" + tenantAccount.getId())
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertNull(accountRepository.findOne(tenantAccount.getId()));
        assertNull(accountRepository.findOne(doe1.getId()));
        assertNull(accountRepository.findOne(doe2.getId()));
        this.mockMvc.perform(get("/accounts/" + tenantAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(get("/accounts/" + doe1.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(get("/accounts/" + doe2.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(status().isNotFound());
    }
}