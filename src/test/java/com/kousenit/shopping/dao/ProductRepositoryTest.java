package com.kousenit.shopping.dao;

import com.kousenit.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // In a test, @Transactional causes each tx to rollback at end of test
class ProductRepositoryTest {
    @Autowired
    private ProductRepository dao;

    @Test
    void autowiringWorked() {
        assertNotNull(dao);
    }

    @Test
    void findById() {
        Optional<Product> optionalProduct = dao.findById(1);
        assertTrue(optionalProduct.isPresent());
    }

    @Test
    void shouldBeFourProductsInSampleDB() {
        assertEquals(4, dao.count());
    }

    @Test
    void deleteAllProducts() {
        dao.deleteAll();
        assertEquals(0, dao.count());
    }

    @Test
    void insertProduct() {
        Product bat = new Product("cricket bat", 35.00);
        dao.save(bat);
        assertAll(
                () -> assertNotNull(bat.getId()),
                () -> assertEquals(5, dao.count())
        );
    }

    @Test
    void priceGE12() {
        List<Product> products = dao.findAllByPriceGreaterThanEqual(12.0);
        assertEquals(3, products.size());
        System.out.println(products);
    }
}