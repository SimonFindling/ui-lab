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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.repository.AccountRepository;
import de.hska.uilab.accounts.repository.ServiceRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mavogel on 11/1/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // clear the db before each test
public class ApiDocumentation {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private MockMvc mockMvc;

    private RestDocumentationResultHandler documentationHandler;

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
        this.documentationHandler = document("{method-name}",
                preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.documentationHandler)
                .build();
    }

    /////////////
    // GET
    /////////////
    @Test
    public void listAllAccounts() throws Exception {
        Service product = createService(Service.ServiceName.PRODUCT);
        Service sales = createService(Service.ServiceName.SALES);
        Service customer = createService(Service.ServiceName.CUSTOMER);

        Account sampleTenantAccount1 = createSampleTenantAccount("prospect1@test.org", product, sales, customer);
        createSampleTenantAccount("prospect2@test.org", product, customer);
        createSampleUserAccount(sampleTenantAccount1.getId(), "John", "Doe", "user@test.org", product, sales, customer);

        this.mockMvc.perform(get("/accounts").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
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
        Service product = createService(Service.ServiceName.PRODUCT);
        Service sales = createService(Service.ServiceName.SALES);
        Service customer = createService(Service.ServiceName.CUSTOMER);
        Account sampleTenantAccount = createSampleTenantAccount("prospect1@test.org", product, sales, customer);

        this.mockMvc.perform(get("/accounts/" + sampleTenantAccount.getId()).accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
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
        this.mockMvc.perform(get("/accounts/1000").accept(MediaType.APPLICATION_JSON)
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42"))
                .andExpect(status().isNotFound());
    }

    /////////////
    // POST
    /////////////
    @Test
    public void createTenantAccount() throws Exception {
        Map<String, String> newProspectAccount = new HashMap<>();
        newProspectAccount.put("email", "test@mail.org");

        this.mockMvc.perform(post("/accounts")
                .header("Authorization: Bearer", "0b79bab50daca910b000d4f1a2b675d604257e42")
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
    public void createUserAccount() throws Exception {
        Service customer = serviceRepository.save(new Service(Service.ServiceName.CUSTOMER));
        Service sales = serviceRepository.save(new Service(Service.ServiceName.PRODUCT));
        Account tenantAccount = accountRepository.save(Account.asProspect("prospectAcc@mail.org"));
        tenantAccount.addServices(customer, sales);
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
    // PUT
    /////////////

    /////////////
    // PATCH
    /////////////

    /////////////
    // DELETE
    /////////////

    private Service createService(final Service.ServiceName name) {
        return this.serviceRepository.save(new Service(name));

    }

    /**
     * HELPERS
     */
    private Account createSampleTenantAccount(final String email, final Service... services) {
        Account createdAcc = this.accountRepository.save(Account.asProspect(email));
        if (services != null && services.length > 0) {
            createdAcc.addServices(services);
            createdAcc = this.accountRepository.save(createdAcc);
        }

        return createdAcc;

    }

    private Account createSampleUserAccount(final Long tenantId, final String firstName, final String lastName,
                                            final String email, final Service... services) {
        Account createdAcc = this.accountRepository.save(Account.asUser(tenantId, firstName, lastName, email));

        if (services != null && services.length > 0) {
            createdAcc.addServices(services);
            createdAcc = this.accountRepository.save(createdAcc);
        }

        return createdAcc;
    }


//    @Test
//    public void addCategory() throws Exception {
//        Map<String, String> newCategory = new HashMap<>();
//        newCategory.put("id", "1");
//        newCategory.put("name", "TestCategory");
//
//        ConstrainedFields fields = new ConstrainedFields(Category.class);
//
//        this.mockMvc.perform(post("/categories")
//                .header("Authorization: Basic", "0b79bab50daca910b000d4f1a2b675d604257e42")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(this.objectMapper.writeValueAsString(newCategory)))
//                .andExpect(status().isCreated())
//                .andDo(this.documentationHandler.document(
//                        requestFields(
//                                fields.withPath("id").description("The category ID"),
//                                fields.withPath("name").description("The name of the category")
//                        )
//                ));
//    }
//
//    @Test
//    public void deleteCategory() throws Exception {
//        Category originalCategory = createSampleCategory(1L, "TestCategory");
//
//        this.mockMvc.perform(delete("/categories/" + originalCategory.getId())
//                .header("Authorization: Basic", "0b79bab50daca910b000d4f1a2b675d604257e42")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//    }
//
//    /**
//     * HELPERS
//     */
//    private static class ConstrainedFields {
//
//        private final ConstraintDescriptions constraintDescriptions;
//
//        ConstrainedFields(Class<?> input) {
//            this.constraintDescriptions = new ConstraintDescriptions(input);
//        }
//
//        private FieldDescriptor withPath(String path) {
//            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
//                    .collectionToDelimitedString(this.constraintDescriptions
//                            .descriptionsForProperty(path), ". ")));
//        }
//    }
//
//    private Category createSampleCategory(final Long id, final String name) {
//        return categoryRepository.save(new Category(id, name));
//    }
}
