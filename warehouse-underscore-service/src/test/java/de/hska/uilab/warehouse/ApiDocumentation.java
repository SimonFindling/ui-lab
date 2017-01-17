package de.hska.uilab.warehouse;/*
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
import de.hska.uilab.warehouse.client.ProductClient;
import de.hska.uilab.warehouse.data.Product;
import de.hska.uilab.warehouse.data.Warehouse;
import de.hska.uilab.warehouse.data.WarehousePlace;
import de.hska.uilab.warehouse.data.WarehousePlaceProduct;
import de.hska.uilab.warehouse.repository.WarehousePlaceProductRepository;
import de.hska.uilab.warehouse.repository.WarehousePlaceRepository;
import de.hska.uilab.warehouse.repository.WarehouseRepository;
import de.hska.uilab.warehouse.service.WarehouseService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mavogel on 1/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // clear the db before each test
public class ApiDocumentation {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehousePlaceRepository warehousePlaceRepository;

    @Autowired
    private WarehousePlaceProductRepository warehousePlaceProductRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private RestDocumentationResultHandler documentationHandler;

//    @Mock
//    private ProductClient productClient;

//    @InjectMocks
//    private WarehouseService warehouseService;

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
    public void getWarehouses() throws Exception {
        // == prepare ==
        this.createWarehouse("VR Goggles Warehouse");
        this.createWarehouse("Bike Warehouse");
        this.createWarehouse("Keyboard Warehouse");

        // == train ==
//        Mockito.when(productClient.getAllProducts())
//                .thenReturn(ResponseEntity.ok(Arrays.asList(new Product())));

        // == go/verify ==
        this.mockMvc.perform(get("/warehouse").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The warehouse ID"),
                                fieldWithPath("[].name").description("the name of the warehouse"),
                                fieldWithPath("[].description").description("the description of the warehouse")
                        )
                ));
    }

    @Test
    public void getWarehousesById() throws Exception {
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");

        this.mockMvc.perform(get("/warehouse/" + newWarehouse.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("id").description("The warehouse ID"),
                                fieldWithPath("name").description("the name of the warehouse"),
                                fieldWithPath("description").description("the description of the warehouse")
                        )
                ));
    }

    @Test
    public void getAmountOfAProductInAllWarehouses() throws Exception {
        // 1
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");
        WarehousePlace warehousePlace = this.createWarehousePlace("3C-10", newWarehouse);
        WarehousePlace warehousePlace2 = this.createWarehousePlace("4B-45", newWarehouse);
        this.createWarehousePlaceProduct(123, 3, warehousePlace);
        this.createWarehousePlaceProduct(123, 3, warehousePlace2);

        // 2
        Warehouse newWarehouse2 = this.createWarehouse("VR Goggles Warehouse 2");
        WarehousePlace warehousePlace21 = this.createWarehousePlace("3C-10", newWarehouse2);
        WarehousePlace warehousePlace22 = this.createWarehousePlace("4B-45", newWarehouse2);
        WarehousePlace warehousePlace23 = this.createWarehousePlace("4B-45", newWarehouse2);
        this.createWarehousePlaceProduct(123, 3, warehousePlace21);
        this.createWarehousePlaceProduct(123, 5, warehousePlace22);
        WarehousePlaceProduct warehousePlaceProduct = this.createWarehousePlaceProduct(123, 3, warehousePlace23);

        MvcResult mvcResult = this.mockMvc.perform(get("/warehouse/product/" + warehousePlaceProduct.getProductid() + "/count").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(5, Integer.valueOf(mvcResult.getResponse().getContentAsString()).intValue());
    }

    /////////////
    // POST
    /////////////
    @Test
    public void createNewWarehouse() throws Exception {
        Map<String, String> newWarehouse = new HashMap<>();
        newWarehouse.put("name", "VR Goggles Warehouse");
        newWarehouse.put("description", "The warehouse in San Jose");

        // == go / verify ==
        MvcResult mvcResult = this.mockMvc.perform(post("/warehouse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newWarehouse)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("name").description("the name of the warehouse"),
                                fieldWithPath("description").description("the description of the warehouse")
                        )
                )).andReturn();

        assertEquals(1L, Long.valueOf(mvcResult.getResponse().getContentAsString()).longValue());
    }


    /////////////
    // PATCH
    /////////////

    /////////////
    // DELETE
    /////////////


    // HELPERS
    private Warehouse createWarehouse(String name) {
        final Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        warehouse.setDescription(name + " - Description");
        return this.warehouseRepository.save(warehouse);
    }

    private WarehousePlace createWarehousePlace(String name, Warehouse warehouse) {
        return this.warehousePlaceRepository.save(new WarehousePlace(name, name + " - Desc", warehouse));
    }

    private WarehousePlaceProduct createWarehousePlaceProduct(int productId, int quantity, final WarehousePlace warehousePlace) {
        return this.warehousePlaceProductRepository.save(new WarehousePlaceProduct(warehousePlace.getId(), productId, quantity, WarehousePlaceProduct.Unit.BOX));
    }
}