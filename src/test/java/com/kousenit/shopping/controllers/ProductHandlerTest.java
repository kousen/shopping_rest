package com.kousenit.shopping.controllers;

import com.kousenit.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"GrazieInspection", "SqlResolve", "SqlNoDataSourceInspection"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
class ProductHandlerTest {
    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Integer> getIds() {
        return jdbcTemplate.query("select id from products",
                (rs, rowNum) -> rs.getInt("id"));
    }

    // Odd contortions you need to go through to get a List<Product>
    private List<Product> getProducts() {
        String url = "http://localhost:" + randomServerPort + "/function";
        ResponseEntity<List<Product>> productsEntity =
                template.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {});
        return productsEntity.getBody();
    }

    @Test
    void getAll() {
        List<Product> products = getProducts();
        assertNotNull(products);
        assertEquals(4, products.size());
    }

    @Test
    void getSingleProductThatExists() {
        List<Integer> ids = getIds();
        ids.forEach(id -> {
                    Product product = template.getForObject("/function/{id}", Product.class, id);
                    assertAll(
                            () -> assertNotNull(product.getId()),
                            () -> assertTrue(product.getName().length() > 0),
                            () -> assertTrue(product.getPrice() >= 0.0)
                    );
                }
        );
    }

    @Test
    void getSingleProductThatDoesNotExist() {
        List<Integer> ids = getIds();
        assertFalse(ids.contains(999));

        ResponseEntity<Product> entity = template.getForEntity(
                "/function/{id}", Product.class, 999);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void insertProduct() {
        Product product = new Product();
        product.setName("baseball bat");
        product.setPrice(20.97);

        ResponseEntity<Product> response = template.postForEntity("/function", product, Product.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Product savedProduct = response.getBody();
        assert savedProduct != null;
        assertAll(
                () -> assertNotNull(savedProduct),
                () -> assertEquals(product.getName(), savedProduct.getName()),
                () -> assertEquals(product.getPrice(), savedProduct.getPrice(), 0.01),
                () -> assertNotNull(savedProduct.getId()));
    }
}