package com.kousenit.shopping.controllers;

import com.kousenit.shopping.dao.ProductRepository;
import com.kousenit.shopping.entities.Product;
import com.kousenit.shopping.entities.ProductNotFoundException;
import com.kousenit.shopping.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class ProductRestController {
    private final ProductService service;

    public ProductRestController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(required = false) Double minimumPrice) {
        if (minimumPrice != null) {
            return service.findAllByMinimumPrice(minimumPrice);
        }
        return service.findAll();
    }

    @GetMapping("{id}")
    public Product getProduct(@PathVariable("id") Integer id) {
        return service.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Product> insertProduct(@RequestBody Product product) {
        Product p = service.saveProduct(product);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(p.getId())
                .toUri();
        return ResponseEntity.created(location).body(p);
    }

    @PutMapping("{id}")
    public Product updateOrInsertProduct(@PathVariable Integer id,
                                 @RequestBody Product newProduct) {
        return service.findById(id).map(product -> {
                    product.setName(newProduct.getName());
                    product.setPrice(newProduct.getPrice());
                    return service.saveProduct(product);
                }).orElseGet(() -> {
                   newProduct.setId(id);
                   return service.saveProduct(newProduct);
                });
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        Optional<Product> existingProduct = service.findById(id);
        if (existingProduct.isPresent()) {
            service.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllProducts() {
        service.deleteAllInBatch();
    }
}
