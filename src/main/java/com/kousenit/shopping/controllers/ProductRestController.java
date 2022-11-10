package com.kousenit.shopping.controllers;

import com.kousenit.shopping.entities.Product;
import com.kousenit.shopping.entities.ProductNotFoundException;
import com.kousenit.shopping.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProductRestController(ProductService service) {
        this.service = service;
    }

    @GetMapping  // localhost:8080/rest?minimumPrice=5.0
    public List<Product> getAllProducts(
            @RequestParam(required = false) Double minimumPrice) {
        if (minimumPrice != null) {
            return service.findAllByMinimumPrice(minimumPrice);
        }
        return service.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        // Simplest option:
        return ResponseEntity.of(productService.findProductById(id));
        
        // Best option if you need to customize the return value (see the ControllerAdvice class):
//        return productService.findProductById(id).orElseThrow(
//                () -> new ProductNotFoundException(id + ""));

        // Works, but overly verbose
//        Optional<Product> optionalProduct = productService.findProductById(id);
//        if (optionalProduct.isPresent()) {
//            return ResponseEntity.ok(optionalProduct.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//
        // Functional version that replaces "if" with "map" and "orElseGet"
//        return optionalProduct.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
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
