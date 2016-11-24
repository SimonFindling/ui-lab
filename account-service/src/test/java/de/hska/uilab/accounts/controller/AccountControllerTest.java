package de.hska.uilab.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.model.Status;
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

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void shouldCreateAnAccount() throws Exception {
        accountRepository.save(Account.asProspect("test@mail.org"));
        Account testAcc = accountRepository.findOne("test");
        assertEquals("test@mail.org", testAcc.getEmail());
        assertEquals("test", testAcc.getUsername());
    }

    @Test
    public void shouldCreateService() throws Exception {
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        Service testService = serviceRepository.findOne(Service.ServiceName.CUSTOMER);
        assertEquals(Service.ServiceName.CUSTOMER, testService.getName());
    }

    @Test
    public void shouldFindOneAccount() throws Exception {
        // == prepare ==
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        accountRepository.save(Account.asProspect("testuser2@mail.org"));

        Service customerService = serviceRepository.findOne(Service.ServiceName.CUSTOMER);
        Account testuser2 = accountRepository.findOne("testuser2");
        testuser2.addService(customerService);
        accountRepository.save(testuser2);

        // == go / verify ==
        this.mockMvc.perform(get("/accounts/testuser2").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("testuser2@mail.org"))
                .andExpect(jsonPath("$.username").value("testuser2"))
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.status").value(Status.PROSPECT.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(jsonPath("$.services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllAccounts() throws Exception {
        // == prepare ==
        serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        serviceRepository.save(new Service(Service.ServiceName.SALES));
        serviceRepository.save(new Service(Service.ServiceName.PRODUCT));
        accountRepository.save(Account.asProspect("testuser3@mail.org"));
        accountRepository.save(Account.asProspect("testuser4@mail.org"));
        accountRepository.save(Account.asProspect("testuser5@mail.org"));
        accountRepository.save(Account.asProspect("testuser6@mail.org"));

        Account testuser3 = accountRepository.findOne("testuser3");
        testuser3.addService(serviceRepository.findOne(Service.ServiceName.CUSTOMER));
        accountRepository.save(testuser3);

        Account testuser6 = accountRepository.findOne("testuser6");
        testuser6.addService(serviceRepository.findOne(Service.ServiceName.CUSTOMER));
        testuser6.addService(serviceRepository.findOne(Service.ServiceName.SALES));
        testuser6.addService(serviceRepository.findOne(Service.ServiceName.PRODUCT));
        accountRepository.save(testuser6);

        // == go / verify ==
        this.mockMvc.perform(get("/accounts").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                // 1
                .andExpect(jsonPath("$[0].email").value("testuser3@mail.org"))
                .andExpect(jsonPath("$[0].username").value("testuser3"))
                .andExpect(jsonPath("$[0].password").isNotEmpty())
                .andExpect(jsonPath("$[0].status").value(Status.PROSPECT.name()))
                .andExpect(jsonPath("$[0].firstname").isEmpty())
                .andExpect(jsonPath("$[0].lastname").isEmpty())
                .andExpect(jsonPath("$[0].company").isEmpty())
                .andExpect(jsonPath("$[0].services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                // 2
                .andExpect(jsonPath("$[1].email").value("testuser4@mail.org"))
                .andExpect(jsonPath("$[1].username").value("testuser4"))
                .andExpect(jsonPath("$[1].password").isNotEmpty())
                .andExpect(jsonPath("$[1].status").value(Status.PROSPECT.name()))
                .andExpect(jsonPath("$[1].firstname").isEmpty())
                .andExpect(jsonPath("$[1].lastname").isEmpty())
                .andExpect(jsonPath("$[1].company").isEmpty())
                .andExpect(jsonPath("$[1].services").isEmpty())
                // 3
                .andExpect(jsonPath("$[2].email").value("testuser5@mail.org"))
                .andExpect(jsonPath("$[2].username").value("testuser5"))
                .andExpect(jsonPath("$[2].password").isNotEmpty())
                .andExpect(jsonPath("$[2].status").value(Status.PROSPECT.name()))
                .andExpect(jsonPath("$[2].firstname").isEmpty())
                .andExpect(jsonPath("$[2].lastname").isEmpty())
                .andExpect(jsonPath("$[2].company").isEmpty())
                .andExpect(jsonPath("$[2].services").isEmpty())
                // 4
                .andExpect(jsonPath("$[3].email").value("testuser6@mail.org"))
                .andExpect(jsonPath("$[3].username").value("testuser6"))
                .andExpect(jsonPath("$[3].password").isNotEmpty())
                .andExpect(jsonPath("$[3].status").value(Status.PROSPECT.name()))
                .andExpect(jsonPath("$[3].firstname").isEmpty())
                .andExpect(jsonPath("$[3].lastname").isEmpty())
                .andExpect(jsonPath("$[3].company").isEmpty())
                .andExpect(jsonPath("$[3].services[0].name").value(Service.ServiceName.CUSTOMER.name()))
                .andExpect(jsonPath("$[3].services[1].name").value(Service.ServiceName.SALES.name()))
                .andExpect(jsonPath("$[3].services[2].name").value(Service.ServiceName.PRODUCT.name()))
                //
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateANewAccount() throws Exception {
        Map<String, String> newAccount = new HashMap<>();
        newAccount.put("email", "test@mail.org");

        this.mockMvc.perform(post("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newAccount)))
//                .andExpect(content().) TODO
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldUpdateAccountsMailAndCompany() throws Exception {
        accountRepository.save(Account.asProspect("testuser2@mail.org"));

        Map<String, String> updatedAccount = new HashMap<>();
        updatedAccount.put("username", "testuser2");
        updatedAccount.put("email", "test123@mail.org");
        updatedAccount.put("company", "VR Stuff");

        this.mockMvc.perform(put("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updatedAccount)))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/testuser2").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("test123@mail.org"))
                .andExpect(jsonPath("$.username").value("testuser2"))
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.status").value(Status.PROSPECT.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").value("VR Stuff"))
                .andExpect(jsonPath("$.services").isEmpty())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpgradeToCustomer() throws Exception {
        accountRepository.save(Account.asProspect("testuser2@mail.org"));

        this.mockMvc.perform(put("/accounts/upgrade/testuser2")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/accounts/testuser2").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(jsonPath("$.email").value("testuser2@mail.org"))
                .andExpect(jsonPath("$.username").value("testuser2"))
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.status").value(Status.CUSTOMER.name()))
                .andExpect(jsonPath("$.firstname").isEmpty())
                .andExpect(jsonPath("$.lastname").isEmpty())
                .andExpect(jsonPath("$.company").isEmpty())
                .andExpect(jsonPath("$.services").isEmpty())
                .andExpect(status().isOk());
    }

}