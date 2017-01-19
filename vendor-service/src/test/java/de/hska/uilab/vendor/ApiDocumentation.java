package de.hska.uilab.vendor;/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Manuel Vogel
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
import de.hska.uilab.vendor.data.Address;
import de.hska.uilab.vendor.data.Vendor;
import de.hska.uilab.vendor.repository.AddressRepository;
import de.hska.uilab.vendor.repository.VendorRepository;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mavogel on 1/18/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // clear the db before each test
public class ApiDocumentation {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private RestDocumentationResultHandler documentationHandler;

    @Before
    public void setUp() {
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
    public void getVendorsByTenantId() throws Exception {
        // == prepare ==
        createVendor("Oculus", 2244L);
        createVendor("Cherry", 434222L);
        createVendor("Dell", 34266L);
        createVendor("Lenovo", 2244L);
        createVendor("HP", 2244L);

        // == go/verify ==
        this.mockMvc.perform(get("/vendor/" + 2244).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].vendorId").description("The vendor ID"),
                                fieldWithPath("[].name").description("the name of the vendor"),
                                fieldWithPath("[].email").description("the email-address of the vendor"),
                                fieldWithPath("[].address.id").description("the id of the address"),
                                fieldWithPath("[].address.street").description("the street where the vendor is located"),
                                fieldWithPath("[].address.number").description("the housenumber"),
                                fieldWithPath("[].address.postal").description("the postal code"),
                                fieldWithPath("[].address.city").description("the city"),
                                fieldWithPath("[].address.country").description("the country"),
                                fieldWithPath("[].tenantId").description("the id of the tenant the vendor sell products to")
                        )
                ));
    }

    @Test
    public void getVendorByTenantIdAndVendorId() throws Exception {
        // == prepare ==
        createVendor("Oculus", 2244L);
        createVendor("Cherry", 434222L);
        createVendor("Dell", 34266L);
        createVendor("Lenovo", 2244L);
        Vendor hp = createVendor("HP", 2244L);

        // == go/verify ==
        this.mockMvc.perform(get("/vendor/" + 2244 + "/" + hp.getVendorId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("vendorId").description("The vendor ID"),
                                fieldWithPath("name").description("the name of the vendor"),
                                fieldWithPath("email").description("the email-address of the vendor"),
                                fieldWithPath("address.id").description("the id of the address"),
                                fieldWithPath("address.street").description("the street where the vendor is located"),
                                fieldWithPath("address.number").description("the housenumber"),
                                fieldWithPath("address.postal").description("the postal code"),
                                fieldWithPath("address.city").description("the city"),
                                fieldWithPath("address.country").description("the country"),
                                fieldWithPath("tenantId").description("the id of the tenant the vendor sell products to")
                        )
                ));
    }

    /////////////
    // POST
    /////////////
    @Test
    public void createNewVendorForTenant() throws Exception {
        Map<String, Object> newVendor = new HashMap<>();
        newVendor.put("name", "Cherry");
        newVendor.put("email", "mail@cherry.com");
        newVendor.put("address", createAddress());
        newVendor.put("tenantId", "5");

        // == go / verify ==
        this.mockMvc.perform(post("/vendor/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newVendor)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("name").description("the name of the vendor"),
                                fieldWithPath("email").description("the email-address of the vendor"),
                                fieldWithPath("address").description("the address of the vendor"),
                                fieldWithPath("tenantId").description("the tenantId of the vendor")
                        )
                ));
    }

    /////////////
    // PATCH
    /////////////
    @Test
    public void modifyVendorForTenant() throws Exception {
        long tenantId = 5L;
        Vendor vendor = createVendor("Cherry", tenantId);

        Map<String, Object> newVendor = new HashMap<>();
        newVendor.put("name", "Cherry Inc.");
        newVendor.put("email", "mail@aws.cherry.com");
        newVendor.put("address", createAddress());
        newVendor.put("tenantId", tenantId);

        // == go / verify ==
        this.mockMvc.perform(patch("/vendor/" + tenantId + "/" + vendor.getVendorId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newVendor)))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("name").description("the name of the vendor"),
                                fieldWithPath("email").description("the email-address of the vendor"),
                                fieldWithPath("address").description("the address of the vendor"),
                                fieldWithPath("tenantId").description("the tenantId of the vendor")
                        )
                ));

        Vendor modifiedVendor = this.vendorRepository.findOne(vendor.getVendorId());
        assertEquals("Cherry Inc.", modifiedVendor.getName());
        assertEquals("mail@aws.cherry.com", modifiedVendor.getEmail());
    }

    /////////////
    // DELETE
    /////////////
    @Test
    public void deleteVendorForTenant() throws Exception {
        long tenantId = 5L;
        Vendor vendor = createVendor("Cherry", tenantId);

        // == go / verify ==
        this.mockMvc.perform(delete("/vendor/" + tenantId + "/" + vendor.getVendorId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertNull(this.vendorRepository.findOne(vendor.getVendorId()));
    }

    // HELPERS
    private Vendor createVendor(final String name, final Long tenantId) {
        return this.vendorRepository.save(new Vendor(name, "mail@"+ name.toLowerCase() + ".com",
                createAddress(), tenantId));
    }

    private Address createAddress() {
        return new Address("street", String.valueOf(new Random().nextInt(40)), String.valueOf(new Random().nextInt(99999)), "city", "country");
    }

}