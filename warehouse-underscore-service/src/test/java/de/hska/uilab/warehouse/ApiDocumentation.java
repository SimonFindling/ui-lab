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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace2);

        // 2
        Warehouse newWarehouse2 = this.createWarehouse("VR Goggles Warehouse 2");
        WarehousePlace warehousePlace21 = this.createWarehousePlace("3C-10", newWarehouse2);
        WarehousePlace warehousePlace22 = this.createWarehousePlace("4B-45", newWarehouse2);
        WarehousePlace warehousePlace23 = this.createWarehousePlace("4B-45", newWarehouse2);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace21);
        this.createWarehousePlaceProduct(123, 5, WarehousePlaceProduct.Unit.BOX, warehousePlace22);
        WarehousePlaceProduct warehousePlaceProduct = this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace23);

        MvcResult mvcResult = this.mockMvc.perform(get("/warehouse/product/" + warehousePlaceProduct.getProductid() + "/count").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(5, Integer.valueOf(mvcResult.getResponse().getContentAsString()).intValue());
    }

    @Test
    public void getWarehousePlaceProductsForProductId() throws Exception {
        // 1
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");
        WarehousePlace warehousePlace = this.createWarehousePlace("3C-10", newWarehouse);
        WarehousePlace warehousePlace2 = this.createWarehousePlace("4B-45", newWarehouse);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace2);

        // 2
        Warehouse newWarehouse2 = this.createWarehouse("VR Goggles Warehouse 2");
        WarehousePlace warehousePlace21 = this.createWarehousePlace("3C-10", newWarehouse2);
        WarehousePlace warehousePlace22 = this.createWarehousePlace("4B-45", newWarehouse2);
        WarehousePlace warehousePlace23 = this.createWarehousePlace("4B-45", newWarehouse2);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.PIECE, warehousePlace21);
        this.createWarehousePlaceProduct(123, 5, WarehousePlaceProduct.Unit.PIECE, warehousePlace22);
        WarehousePlaceProduct warehousePlaceProduct = this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.PIECE, warehousePlace23);

        this.mockMvc.perform(get("/warehouse/place/" + warehousePlaceProduct.getProductid() + "/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The primary key ID"),
                                fieldWithPath("[].warehouseplaceid").description("The ID of the warehouse place where the product is located"),
                                fieldWithPath("[].productid").description("The id of the product"),
                                fieldWithPath("[].quantity").description("The quantity of the product at the given place"),
                                fieldWithPath("[].unit").description("The unit of the product")
                        )
                ));
    }

    @Test
    public void getWarehousePlaces() throws Exception {
        // 1
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");
        WarehousePlace warehousePlace = this.createWarehousePlace("3C-10", newWarehouse);
        WarehousePlace warehousePlace2 = this.createWarehousePlace("4B-45", newWarehouse);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace2);

        // 2
        Warehouse newWarehouse2 = this.createWarehouse("VR Goggles Warehouse 2");
        WarehousePlace warehousePlace21 = this.createWarehousePlace("3C-10", newWarehouse2);
        WarehousePlace warehousePlace22 = this.createWarehousePlace("4B-45", newWarehouse2);
        WarehousePlace warehousePlace23 = this.createWarehousePlace("4B-45", newWarehouse2);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.PIECE, warehousePlace21);
        this.createWarehousePlaceProduct(123, 5, WarehousePlaceProduct.Unit.PIECE, warehousePlace22);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.PIECE, warehousePlace23);

        this.mockMvc.perform(get("/warehouse/place").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The primary key ID"),
                                fieldWithPath("[].name").description("The name of the place in the warehouse"),
                                fieldWithPath("[].description").description("The description of the place in the warehouse"),
                                fieldWithPath("[].warehouse.id").description("The id of the warehouse the place is associated to"),
                                fieldWithPath("[].warehouse.name").description("The name of the warehouse the place is associated to"),
                                fieldWithPath("[].warehouse.description").description("The description of the warehouse the place is associated to"),
                                fieldWithPath("[].warehousePlaceProduct.[].id").description("The primary key ID"),
                                fieldWithPath("[].warehousePlaceProduct.[].warehouseplaceid").description("The ID of the warehouse place where the product is located"),
                                fieldWithPath("[].warehousePlaceProduct.[].productid").description("The id of the product"),
                                fieldWithPath("[].warehousePlaceProduct.[].quantity").description("The quantity of the product at the given place"),
                                fieldWithPath("[].warehousePlaceProduct.[].unit").description("The unit of the product")
                        )
                ));
    }

    @Test
    public void getWarehousePlacesForWarehouseId() throws Exception {
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");
        WarehousePlace warehousePlace = this.createWarehousePlace("3C-10", newWarehouse);
        WarehousePlace warehousePlace2 = this.createWarehousePlace("4B-45", newWarehouse);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace2);

        this.mockMvc.perform(get("/warehouse/"+ newWarehouse.getId() + "/place").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The primary key ID"),
                                fieldWithPath("[].name").description("The name of the place in the warehouse"),
                                fieldWithPath("[].description").description("The description of the place in the warehouse"),
                                fieldWithPath("[].warehouse.id").description("The id of the warehouse the place is associated to"),
                                fieldWithPath("[].warehouse.name").description("The name of the warehouse the place is associated to"),
                                fieldWithPath("[].warehouse.description").description("The description of the warehouse the place is associated to"),
                                fieldWithPath("[].warehousePlaceProduct.[].id").description("The primary key ID"),
                                fieldWithPath("[].warehousePlaceProduct.[].warehouseplaceid").description("The ID of the warehouse place where the product is located"),
                                fieldWithPath("[].warehousePlaceProduct.[].productid").description("The id of the product"),
                                fieldWithPath("[].warehousePlaceProduct.[].quantity").description("The quantity of the product at the given place"),
                                fieldWithPath("[].warehousePlaceProduct.[].unit").description("The unit of the product")
                        )
                ));
    }

    @Test
    public void getAllProductsOfAPlace() throws Exception {
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");
        WarehousePlace warehousePlace = this.createWarehousePlace("3C-10", newWarehouse);
        WarehousePlace warehousePlace2 = this.createWarehousePlace("4B-45", newWarehouse);
        this.createWarehousePlaceProduct(123, 3, WarehousePlaceProduct.Unit.BOX, warehousePlace);
        this.createWarehousePlaceProduct(1231244, 2, WarehousePlaceProduct.Unit.BOX, warehousePlace2);
        this.createWarehousePlaceProduct(12356, 4, WarehousePlaceProduct.Unit.PIECE, warehousePlace2);

        this.mockMvc.perform(get("/warehouse/place/"+ warehousePlace2.getId() + "/product").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("The primary key ID"),
                                fieldWithPath("[].warehouseplaceid").description("The ID of the warehouse place where the product is located"),
                                fieldWithPath("[].productid").description("The id of the product"),
                                fieldWithPath("[].quantity").description("The quantity of the product at the given place"),
                                fieldWithPath("[].unit").description("The unit of the product")
                        )
                ));
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

    @Test
    public void createPlaceInWarehouse() throws Exception {
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");

        Map<String, Object> newWarehousePlace = new HashMap<>();
        newWarehousePlace.put("name", "3C-10");
        newWarehousePlace.put("description", "3C-10 - Description");
        newWarehousePlace.put("warehouse", newWarehouse);

        // == go / verify ==
        MvcResult mvcResult = this.mockMvc.perform(post("/warehouse/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newWarehousePlace)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("name").description("the name of the warehouse"),
                                fieldWithPath("description").description("the description of the warehouse"),
                                fieldWithPath("warehouse").description("the id of the warehouse the place is located")
                        )
                )).andReturn();

        assertEquals(1L, Long.valueOf(mvcResult.getResponse().getContentAsString()).longValue());
    }

    @Test
    public void createWarehousePlaceProduct() throws Exception {
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");
        WarehousePlace warehousePlace = this.createWarehousePlace("3C-10", newWarehouse);

        Map<String, Object> newWarehousePlaceProduct = new HashMap<>();
        newWarehousePlaceProduct.put("warehouseplaceid", warehousePlace.getId());
        newWarehousePlaceProduct.put("productid", "123");
        newWarehousePlaceProduct.put("quantity", "5");
        newWarehousePlaceProduct.put("unit", WarehousePlaceProduct.Unit.PIECE);

        // == go / verify ==
        MvcResult mvcResult = this.mockMvc.perform(post("/warehouse/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(newWarehousePlaceProduct)))
                .andExpect(status().isCreated())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("warehouseplaceid").description("the id of the warehouse the place is located"),
                                fieldWithPath("productid").description("the id of the product"),
                                fieldWithPath("quantity").description("the quantity of the product located at this place"),
                                fieldWithPath("unit").description("the unit of the product")
                        )
                )).andReturn();

        assertEquals(1L, Long.valueOf(mvcResult.getResponse().getContentAsString()).longValue());
    }

    /////////////
    // PATCH
    /////////////
    @Test
    public void modifyWarehouse() throws Exception {
        Warehouse newWarehouse = this.createWarehouse("VR Goggles Warehouse");

        Map<String, String> modifiedWarehouse = new HashMap<>();
        modifiedWarehouse.put("name", "VR Goggles Warehouse Extension");
        modifiedWarehouse.put("description", "The warehouse extension in San Jose (Downtown)");

        // == go / verify ==
        MvcResult mvcResult = this.mockMvc.perform(patch("/warehouse/" + newWarehouse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(modifiedWarehouse)))
                .andExpect(status().isOk())
                .andDo(this.documentationHandler.document(
                        requestFields(
                                fieldWithPath("name").description("the name of the warehouse"),
                                fieldWithPath("description").description("the description of the warehouse")
                        )
                )).andReturn();
        Warehouse storedModifiedWarehouse = this.warehouseRepository.findOne(newWarehouse.getId());
        assertEquals("VR Goggles Warehouse Extension", storedModifiedWarehouse.getName());
        assertEquals("The warehouse extension in San Jose (Downtown)", storedModifiedWarehouse.getDescription());
    }

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

    private WarehousePlaceProduct createWarehousePlaceProduct(int productId, int quantity, final WarehousePlaceProduct.Unit unit, final WarehousePlace warehousePlace) {
        return this.warehousePlaceProductRepository.save(new WarehousePlaceProduct(warehousePlace.getId(), productId, quantity, unit));
    }
}