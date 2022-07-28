package com.kousenit.shopping.controllers;

import com.kousenit.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Transactional
@Profile("test")
class ProductRestControllerTest {
    @Autowired
    private WebTestClient client;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Integer> getIds() {
        return jdbcTemplate.query("select id from products",
                (rs, rowNum) -> rs.getInt("id"));
    }

    @Test
    void getAll() {
        client.get()
                .uri("/rest")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(4)
                .consumeWith(System.out::println);
    }

    @Test
    void getSingleProductThatExists() {
        List<Integer> ids = getIds();
        client.get()
                .uri("/rest/{id}", ids.get(0))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(System.out::println);
    }

    @Test
    void getSingleProductThatDoesNotExist() {
        List<Integer> ids = getIds();
        assertFalse(ids.contains(999));

        client.get()
                .uri("/rest/{id}", 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getWithTRT(@Autowired TestRestTemplate template) {
        Product product = template.getForObject("/rest/1", Product.class);
        Product correct = new Product(1, "baseball", 9.99);
        assertEquals(correct, product);
    }

    @Test
    void insertProduct() {
        Product product = new Product("baseball bat", 20.97);

        client.post()
                .uri("/rest")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("baseball bat")
                .jsonPath("$.price").isEqualTo(20.97)
                .consumeWith(System.out::println);
    }

    @Test
    void postWithTRT(@Autowired TestRestTemplate template) {
        Product product = new Product();
        product.setName("baseball bat");
        product.setPrice(20.97);

        ResponseEntity<Product> response = template.postForEntity("/rest", product, Product.class);
        Product savedProduct = response.getBody();
        assert savedProduct != null;
        assertAll(
                () -> assertEquals(product.getName(), savedProduct.getName()),
                () -> assertEquals(product.getPrice(), savedProduct.getPrice(), 0.01),
                () -> assertNotNull(savedProduct.getId()));
    }

    @Test
    void updateProduct(@Autowired TestRestTemplate template) {
        List<Integer> ids = getIds();
        Product product = template.getForObject("/rest/{id}", Product.class, ids.get(0));
        product.setPrice(product.getPrice() * 1.10);

        client.put()
                .uri("/products/{id}", product.getId())
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectBody(Product.class)
                .consumeWith(System.out::println);
    }


    @Test
    void getProductsWithMinimumPrice() {
        client.get()
                .uri(uriBuilder -> uriBuilder.path("/rest")
                        .queryParam("minimumPrice", 12.0)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(3);
    }

    @Test
    void deleteSingleProduct() {
        List<Integer> ids = getIds();
        if (ids.size() == 0) {
            System.out.println("No ids found");
            return;
        }

        client.get()
                .uri("/rest/{id}", ids.get(0))
                .exchange()
                .expectStatus().isOk();

        client.delete()
                .uri("/rest/{id}", ids.get(0))
                .exchange()
                .expectStatus().isNoContent();

        client.get()
                .uri("/rest/{id}", ids.get(0))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteAllProducts() {
        client.delete()
                .uri("/rest")
                .exchange()
                .expectStatus().isNoContent();

        client.get()
                .uri("/rest")
                .exchange()
                .expectBodyList(Product.class)
                .hasSize(0);
    }
}