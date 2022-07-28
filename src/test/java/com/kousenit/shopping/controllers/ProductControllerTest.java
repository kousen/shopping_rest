package com.kousenit.shopping.controllers;

import com.kousenit.shopping.entities.Product;
import com.kousenit.shopping.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService service;

    private final List<Product> products = Arrays.asList(
            new Product(1,"baseball", 9.99),
            new Product(2, "football", 14.95),
            new Product(3, "basketball", 11.99)
    );

    @BeforeEach
    void setUp() {
        Mockito.when(service.findAll())
                .thenReturn(products);
        Mockito.when(service.saveProduct(Mockito.any(Product.class)))
                .thenReturn(products.get(0),
                        products.get(1),
                        products.get(2));
        Mockito.when(service.findById(1))
                .thenReturn(Optional.of(products.get(0)));
    }

    @Test
    void getAllProducts() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", products));
        verify(service).findAll();
    }

    @Test
    void getProductById() throws Exception {
        mvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("product", is(products.get(0))));
        verify(service).findById(1);
    }

    @Test
    void getProductByIdDoesNotExist() throws Exception {
        mvc.perform(get("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string("Product not found with id 999"));
    }

    @Test
    void saveProduct() throws Exception {
        mvc.perform(post("/products")
                .content("name=golfball&price=5.0")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(view().name("redirect:/products"));
    }

    @Test
    void deleteAllProducts() throws Exception {
        mvc.perform(delete("/products"))
                .andExpect(status().isNoContent())
                .andExpect(view().name("redirect:/products"));
        verify(service).deleteAllInBatch();
    }

    @Test
    void deleteSingleProduct() throws Exception {
        mvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent())
                .andExpect(view().name("redirect:/products"));
        verify(service).deleteProduct(anyInt());
    }
}