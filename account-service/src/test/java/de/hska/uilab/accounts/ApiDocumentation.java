package de.hska.uilab.accounts;
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

import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.Service;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mavogel on 11/1/16.
 */
public class ApiDocumentation extends AbstractTestBase {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private RestDocumentationResultHandler documentationHandler;

    @Before
    public void setUp() {
        createServices();
        this.documentationHandler = document("{method-name}",
                preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).uris()
                        .withHost("82.165.207.147")
                        .withPort(8081)) // for the api-gateway =)
                .alwaysDo(this.documentationHandler)
                .build();
    }

    /////////////
    // GET
    /////////////
    @Test
    public void listAllAccounts() throws Exception {
        Account sampleTenantAccount1 = createSampleTenantAccount("prospect1@test.org");
        sampleTenantAccount1.setCompany("tenant1Company");
        accountRepository.save(sampleTenantAccount1);
        createSampleTenantAccount("prospect2@test.org");
        // TODO create user via rest interface
        createSampleUserAccount(sampleTenantAccount1.getId(), "John", "Doe", "user@test.org");

        this.mockMvc.perform(get("/account").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The account ID"),
                                fieldWithPath("[].email").description("the email address of the account"),
                                fieldWithPath("[].username").description("the username of the account"),
                                fieldWithPath("[].password").description("the password of the user"),
                                fieldWithPath("[].tenantStatus").description("the status of the tenant PROSPECT or CUSTOMER, which is empty in ADMIN accounts"),
                                fieldWithPath("[].tenantId").description("the corresponding tenantId, which is only set in USER accounts"),
                                fieldWithPath("[].accountType").description("the type of the account: ADMIN, TENANT or USER"),
                                fieldWithPath("[].firstname").description("the firstname of the account owner"),
                                fieldWithPath("[].lastname").description("the lastname of the account owner"),
                                fieldWithPath("[].company").description("the company of the account owner"),
                                fieldWithPath("[].services.[]").description("the services/modules this account can use")
                        )
                ));
    }

    @Test
    public void getAccountById() throws Exception {
        Account sampleTenantAccount = createSampleTenantAccount("prospect1@test.org");

        this.mockMvc.perform(get("/account/" + sampleTenantAccount.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("id").description("The account ID"),
                                fieldWithPath("email").description("the email address of the account"),
                                fieldWithPath("username").description("the username of the account"),
                                fieldWithPath("password").description("the password of the user"),
                                fieldWithPath("tenantStatus").description("the status of the tenant PROSPECT or CUSTOMER, which is empty in ADMIN accounts"),
                                fieldWithPath("tenantId").description("the corresponding tenantId, which is only set in USER accounts"),
                                fieldWithPath("accountType").description("the type of the account: ADMIN, TENANT or USER"),
                                fieldWithPath("firstname").description("the firstname of the account owner"),
                                fieldWithPath("lastname").description("the lastname of the account owner"),
                                fieldWithPath("company").description("the company of the account owner"),
                                fieldWithPath("services.[]").description("the services/modules this account can use")
                        )
                ));
    }

    @Test
    public void getAccountByIdNotFound() throws Exception {
        this.mockMvc.perform(get("/account/1000").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /////////////
    // POST
    /////////////
    @Test
    public void createTenantAccount() throws Exception {
        Map<String, String> newProspectAccount = new HashMap<>();
        newProspectAccount.put("email", "test@mail.org");

        // == go / verify ==
        this.mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProspectAccount)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("email").description("The email-address of the tenant")
                        )
                ));
    }

    @Test
    public void failDueToExistingTenantAccount() throws Exception {
        // == prepare ==
        accountRepository.save(Account.asProspect("test@mail.org"));

        Map<String, String> newProspectAccount = new HashMap<>();
        newProspectAccount.put("email", "test@mail.org");

        // == go / verify ==
        MvcResult mvcResult = this.mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProspectAccount)))
                .andExpect(status().isConflict())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("email").description("The email-address of the tenant")
                        )
                )).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("exists already"));
    }

    @Test
    public void createUserAccount() throws Exception {
        Service customer = serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        Service sales = serviceRepository.save(new Service(Service.ServiceName.PRODUCT));
        Account tenantAccount = accountRepository.save(Account.asProspect("prospectAcc@mail.org"));
        tenantAccount.addServices(customer, sales);
        accountRepository.save(tenantAccount);

        Map<String, String> newUserAccount = new HashMap<>();
        newUserAccount.put("firstname", "John");
        newUserAccount.put("lastname", "Doe");
        newUserAccount.put("email", "newuser@mail.org");

        this.mockMvc.perform(post("/account/" + tenantAccount.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newUserAccount)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("firstname").description("The first name of the user"),
                                fieldWithPath("lastname").description("The last name of the user"),
                                fieldWithPath("email").description("The email-address of the user")
                        )
                ));
    }

    /////////////
    // PATCH
    /////////////
    @Test
    public void updateTenantAccount() throws Exception {
        // == prepare ==
        Account baseAccount = createSampleTenantAccount("testuser2@mail.org");

        Map<String, String> updatedAccount = new HashMap<>();
        updatedAccount.put("id", String.valueOf(baseAccount.getId()));
        updatedAccount.put("username", "newUsername");
        updatedAccount.put("firstname", "John");
        updatedAccount.put("lastname", "Doe");
        updatedAccount.put("email", "test123@mail.org");
        updatedAccount.put("company", "VR Stuff");

        this.mockMvc.perform(patch("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updatedAccount)))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("id").description("The id of the user"),
                                fieldWithPath("username").description("The name of the user"),
                                fieldWithPath("firstname").description("The first name of the user"),
                                fieldWithPath("lastname").description("The last name of the user"),
                                fieldWithPath("email").description("The email-address of the user"),
                                fieldWithPath("company").description("The company name of the user")
                        )
                ));
    }

    @Test
    public void upgradeProspectToCustomer() throws Exception {
        // == prepare ==
        Account baseAccount = createSampleTenantAccount("testuser2@mail.org");

        this.mockMvc.perform(patch("/account/" + baseAccount.getId() + "/upgrade")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("id").description("The account ID"),
                                fieldWithPath("email").description("the email address of the account"),
                                fieldWithPath("username").description("the username of the account"),
                                fieldWithPath("password").description("the password of the user"),
                                fieldWithPath("tenantStatus").description("the status of the tenant PROSPECT or CUSTOMER, which is empty in ADMIN accounts"),
                                fieldWithPath("tenantId").description("the corresponding tenantId, which is only set in USER accounts"),
                                fieldWithPath("accountType").description("the type of the account: ADMIN, TENANT or USER"),
                                fieldWithPath("firstname").description("the firstname of the account owner"),
                                fieldWithPath("lastname").description("the lastname of the account owner"),
                                fieldWithPath("company").description("the company of the account owner"),
                                fieldWithPath("services.[]").description("the services/modules this account can use")
                        )
                ));

    }

    @Test
    public void addService() throws Exception {
        // == prepare ==
        createService(Service.ServiceName.SALES);
        createService(Service.ServiceName.PRODUCT);
        createService(Service.ServiceName.CUSTOMER);
        Account accountToAddServices = createSampleTenantAccount("testuser2@mail.org");

        List<Map<String, String>> servicesToAdd = new ArrayList<>();
        Map<String, String> salesService = new HashMap<>();
        salesService.put("name", "SALES");
        salesService.put("name", "PRODUCT");
        salesService.put("name", "CUSTOMER");
        servicesToAdd.add(salesService);

        this.mockMvc.perform(patch("/account/" + accountToAddServices.getId() + "/addservice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(servicesToAdd)))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("[].name").description("The name of the service to add")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The account ID"),
                                fieldWithPath("email").description("the email address of the account"),
                                fieldWithPath("username").description("the username of the account"),
                                fieldWithPath("password").description("the password of the user"),
                                fieldWithPath("tenantStatus").description("the status of the tenant PROSPECT or CUSTOMER, which is empty in ADMIN accounts"),
                                fieldWithPath("tenantId").description("the corresponding tenantId, which is only set in USER accounts"),
                                fieldWithPath("accountType").description("the type of the account: ADMIN, TENANT or USER"),
                                fieldWithPath("firstname").description("the firstname of the account owner"),
                                fieldWithPath("lastname").description("the lastname of the account owner"),
                                fieldWithPath("company").description("the company of the account owner"),
                                fieldWithPath("services.[]").description("the services/modules this account can use")
                        )
                ));
    }

    @Test
    public void removeService() throws Exception {
        // == prepare ==
        Account accountToAddServices = createSampleTenantAccount("testuser2@mail.org");

        List<Map<String, String>> servicesToAdd = new ArrayList<>();
        Map<String, String> salesService = new HashMap<>();
        salesService.put("name", "CUSTOMER");
        servicesToAdd.add(salesService);

        this.mockMvc.perform(patch("/account/" + accountToAddServices.getId() + "/rmservice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(servicesToAdd)))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("[].name").description("The name of the service to remove")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The account ID"),
                                fieldWithPath("email").description("the email address of the account"),
                                fieldWithPath("username").description("the username of the account"),
                                fieldWithPath("password").description("the password of the user"),
                                fieldWithPath("tenantStatus").description("the status of the tenant PROSPECT or CUSTOMER, which is empty in ADMIN accounts"),
                                fieldWithPath("tenantId").description("the corresponding tenantId, which is only set in USER accounts"),
                                fieldWithPath("accountType").description("the type of the account: ADMIN, TENANT or USER"),
                                fieldWithPath("firstname").description("the firstname of the account owner"),
                                fieldWithPath("lastname").description("the lastname of the account owner"),
                                fieldWithPath("company").description("the company of the account owner"),
                                fieldWithPath("services.[]").description("the services/modules this account can use")
                        )
                ));
    }

    /////////////
    // DELETE
    /////////////
    @Test
    public void deleteAccount() throws Exception {
        // == prepare ==
        Account accountToDelete = createSampleTenantAccount("testuser2@mail.org");

        // == go / verify ==
        this.mockMvc.perform(delete("/account/" + accountToDelete.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNonExistingAccount() throws Exception {
        // == prepare ==
        Long nonExistingAccountId = 100L;

        // == go / verify ==
        this.mockMvc.perform(delete("/" + nonExistingAccountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}