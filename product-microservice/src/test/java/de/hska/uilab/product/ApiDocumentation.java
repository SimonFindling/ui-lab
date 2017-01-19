package de.hska.uilab.product;/*
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
import de.hska.uilab.product.schema.Product;
import de.hska.uilab.product.schema.ProductsMock;
import org.junit.After;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mavogel on 1/18/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiDocumentation {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductsMock productsMock;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private RestDocumentationResultHandler documentationHandler;

    @Before
    public void setUp() {
        this.productsMock = new ProductsMock();
        this.documentationHandler = document("{method-name}",
                preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).uris()
                        .withHost("82.165.207.147")
                        .withPort(8081)) // for the api-gateway =)
                .alwaysDo(this.documentationHandler)
                .build();
    }

    @After
    public void cleanUp() throws Exception {
        this.productsMock.deleteAllProducts();
    }

    /////////////
    // GET
    /////////////
    @Test
    public void getAllProducts() throws Exception {
        // == prepare ==
        productsMock.createProduct(new Product(1, 22, "Keyboard Cherry", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(2, 32, "Keyboard Blue", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(3, 366, "Keyboard Orange", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));

        // == go/verify ==
        this.mockMvc.perform(get("/product").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The product ID"),
                                fieldWithPath("[].vendorId").description("the id of the vendor of the product"),
                                fieldWithPath("[].productName").description("the name of the product"),
                                fieldWithPath("[].productImage").description("the image of the product (BASE64)"),
                                fieldWithPath("[].productInformation").description("the information about the product"),
                                fieldWithPath("[].price").description("the price of the product")
                        )
                ));
    }

    @Test
    public void getSingleProduct() throws Exception {
        // == prepare ==
        productsMock.createProduct(new Product(1, 22, "Keyboard Cherry", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(2, 32, "Keyboard Blue", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(3, 366, "Keyboard Orange", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));

        // == go/verify ==
        this.mockMvc.perform(get("/product/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("id").description("The product ID"),
                                fieldWithPath("vendorId").description("the id of the vendor of the product"),
                                fieldWithPath("productName").description("the name of the product"),
                                fieldWithPath("productImage").description("the image of the product (BASE64)"),
                                fieldWithPath("productInformation").description("the information about the product"),
                                fieldWithPath("price").description("the price of the product")
                        )
                ));
    }

    /////////////
    // POST
    /////////////
    @Test
    public void createProduct() throws Exception {
        Map<String, String> newProduct = new HashMap<>();
        newProduct.put("id", "1");
        newProduct.put("vendorId", "12444");
        newProduct.put("productName", "Keyboard Cherry");
        newProduct.put("productImage", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()));
        newProduct.put("productInformation", "super information");
        newProduct.put("price", "15");

        // == go / verify ==
        this.mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("id").description("The product ID"),
                                fieldWithPath("vendorId").description("the id of the vendor of the product"),
                                fieldWithPath("productName").description("the name of the product"),
                                fieldWithPath("productImage").description("the image of the product (BASE64)"),
                                fieldWithPath("productInformation").description("the information about the product"),
                                fieldWithPath("price").description("the price of the product")
                        )
                ));
    }

    /////////////
    // PATCH
    /////////////
    @Test
    public void changeProduct() throws Exception {
        productsMock.createProduct(new Product(1, 22, "Keyboard Cherry", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(2, 32, "Keyboard Blue", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(3, 366, "Keyboard Orange", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));

        Map<String, String> changedProduct = new HashMap<>();
        changedProduct.put("id", "1");
        changedProduct.put("vendorId", "12444");
        changedProduct.put("productName", "Keyboard Cherry - New New!!");
        changedProduct.put("productImage", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()));
        changedProduct.put("productInformation", "super information pt2");
        changedProduct.put("price", "15666");

        // == go ==
        this.mockMvc.perform(patch("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(changedProduct)))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("id").description("The product ID"),
                                fieldWithPath("vendorId").description("the id of the vendor of the product"),
                                fieldWithPath("productName").description("the name of the product"),
                                fieldWithPath("productImage").description("the image of the product (BASE64)"),
                                fieldWithPath("productInformation").description("the information about the product"),
                                fieldWithPath("price").description("the price of the product")
                        )
                ));

        // == verify ==
        Product changedAndPersistedProduct = productsMock.getProductById(1);
        assertEquals("Keyboard Cherry - New New!!", changedAndPersistedProduct.getProductName());
        assertEquals("super information pt2", changedAndPersistedProduct.getProductInformation());
        assertEquals(15666, changedAndPersistedProduct.getPrice());
    }


    /////////////
    // DELETE
    /////////////
    @Test
    public void deleteProduct() throws Exception {
        // == prepare ==
        productsMock.createProduct(new Product(1, 22, "Keyboard Cherry", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(2, 32, "Keyboard Blue", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));
        productsMock.createProduct(new Product(3, 366, "Keyboard Orange", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", 15));

        // == go ==
        this.mockMvc.perform(delete("/product/2"))
                .andExpect(status().isNoContent());

        // == verify ==
        assertEquals(2, productsMock.getAllProducts().size());
    }

}