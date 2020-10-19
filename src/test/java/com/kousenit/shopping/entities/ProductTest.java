package com.kousenit.shopping.entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProductTest {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();;
    private static final Validator validator = factory.getValidator();

    @Test
    void nameCanNotBeBlank() {
        Product product = new Product("", 10.0);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Product> violation : violations) {
            System.out.println(violation.getMessage());
        }
    }

    @Test
    void priceMustBeGEZero() {
        Product product = new Product("name", -1);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Product> violation : violations) {
            System.out.println(violation.getMessage());
        }
    }
}