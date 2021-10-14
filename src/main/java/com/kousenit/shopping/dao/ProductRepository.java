package com.kousenit.shopping.dao;

import com.kousenit.shopping.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> findAllByPriceGreaterThanEqual(double amount);
    List<Product> findTop10ByNameContainsOrderByPrice(String regex);
}
