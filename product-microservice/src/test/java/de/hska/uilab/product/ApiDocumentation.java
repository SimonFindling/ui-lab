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

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;

import de.hska.uilab.product.model.Product;
import de.hska.uilab.product.repositories.ProductRepository;

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
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProductRepository productRepository;

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
    public void getAllProducts() throws Exception {
        // == prepare ==
    	productRepository.save(new Product("Keyboard Cherry", 22, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1534867563456567"));
    	productRepository.save(new Product("Keyboard Blue", 32.99, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1235683558"));
    	productRepository.save(new Product("Keyboard Orange", 366.12, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "23645324523532"));

        // == go/verify ==
        this.mockMvc.perform(get("/product").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The product ID"),
                                fieldWithPath("[].name").description("the id of the vendor of the product"),
                                fieldWithPath("[].price").description("the price of the product"),
                                fieldWithPath("[].image").description("the image of the product (BASE64)"),
                                fieldWithPath("[].description").description("the description of the product"),
                                fieldWithPath("[].ean").description("the ean of the product")
                        )
                ));
    }

    @Test
    public void getSingleProduct() throws Exception {
        // == prepare ==
    	productRepository.save(new Product("Keyboard Cherry", 22, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1534867563456567"));
    	productRepository.save(new Product("Keyboard Blue", 32.99, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1235683558"));
    	productRepository.save(new Product("Keyboard Orange", 366.12, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "23645324523532"));

        // == go/verify ==
        this.mockMvc.perform(get("/product/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("id").description("The product ID"),
                                fieldWithPath("name").description("the id of the vendor of the product"),
                                fieldWithPath("price").description("the price of the product"),
                                fieldWithPath("image").description("the image of the product (BASE64)"),
                                fieldWithPath("description").description("the description of the product"),
                                fieldWithPath("ean").description("the ean of the product")
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
        newProduct.put("price", "15");
        newProduct.put("name", "Keyboard Cherry");
        newProduct.put("image", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()));
        newProduct.put("description", "super information");
        newProduct.put("ean", "2324-sd-sas");

        // == go / verify ==
        this.mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("id").description("The product ID"),
                                fieldWithPath("name").description("the id of the vendor of the product"),
                                fieldWithPath("price").description("the price of the product"),
                                fieldWithPath("image").description("the image of the product (BASE64)"),
                                fieldWithPath("description").description("the description of the product"),
                                fieldWithPath("ean").description("the ean of the product")
                        )
                ));
    }

    /////////////
    // PATCH
    /////////////
    @Test
    public void changeProduct() throws Exception {
    	productRepository.save(new Product("Keyboard Cherry", 22, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1534867563456567"));
    	productRepository.save(new Product("Keyboard Blue", 32.99, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1235683558"));
    	productRepository.save(new Product("Keyboard Orange", 366.12, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "23645324523532"));

        Map<String, String> changedProduct = new HashMap<>();
        changedProduct.put("id", "1");
        changedProduct.put("name", "Keyboard Cherry - New New!!");
        changedProduct.put("price", "15666");
        changedProduct.put("image", Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()));
        changedProduct.put("description", "super information pt2");
        changedProduct.put("ean", "2324-sd-sas");

        // == go ==
        this.mockMvc.perform(patch("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(changedProduct)))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("id").description("The product ID"),
                                fieldWithPath("name").description("the id of the vendor of the product"),
                                fieldWithPath("price").description("the price of the product"),
                                fieldWithPath("image").description("the image of the product (BASE64)"),
                                fieldWithPath("description").description("the description of the product"),
                                fieldWithPath("ean").description("the ean of the product")
                        )
                ));

        // == verify ==
        Product changedAndPersistedProduct = productRepository.findOne(1L);
        assertEquals("Keyboard Cherry - New New!!", changedAndPersistedProduct.getName());
        assertEquals("super information pt2", changedAndPersistedProduct.getDescription());
        assertEquals(15666, changedAndPersistedProduct.getPrice(), 0.0001);
    }


    /////////////
    // DELETE
    /////////////
    @Test
    public void deleteProduct() throws Exception {
        // == prepare ==
    	productRepository.save(new Product("Keyboard Cherry", 22, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1534867563456567"));
    	productRepository.save(new Product("Keyboard Blue", 32.99, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "1235683558"));
    	productRepository.save(new Product("Keyboard Orange", 366.12, Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes()), "info", "23645324523532"));

        // == go ==
        this.mockMvc.perform(delete("/product/2"))
                .andExpect(status().isNoContent());

        // == verify ==
        assertEquals(2, Iterators.size(productRepository.findAll().iterator()));
    }

}