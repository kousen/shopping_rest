package com.kousenit.shopping.services;

import com.kousenit.shopping.dao.ProductRepository;
import com.kousenit.shopping.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service  // transaction boundaries and business logic
@Transactional
public class ProductService {
    private final ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public void initializeDatabase() {
        if (repository.count() == 0) {
            repository.saveAll(Arrays.asList(
                    new Product("baseball", 5.0),
                    new Product("football", 12.0),
                    new Product("basketball", 10.0)
            ));
        }
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public List<Product> findAllByMinimumPrice(Double minPrice) {
        return repository.findAllByPriceGreaterThanEqual(minPrice);
    }

    public Optional<Product> findById(Integer id) {
        return repository.findById(id);
    }

    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    public void deleteProduct(Integer id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }
}
