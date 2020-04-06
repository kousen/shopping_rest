package com.kousenit.shopping.controllers;

import com.kousenit.shopping.dao.ProductRepository;
import com.kousenit.shopping.entities.Product;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.springframework.web.servlet.function.ServerResponse.ok;

@Component
public class ProductHandler {
    private ProductRepository repository;

    public ProductHandler(ProductRepository repository) {
        this.repository = repository;
    }

    public ServerResponse getAllProducts(ServerRequest request) {
        return ok().body(repository.findAll());
    }

    public ServerResponse getPersonById(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));
        Optional<Product> optional = repository.findById(id);
        return optional.map(product ->
                ok().contentType(MediaType.APPLICATION_JSON).body(product))
                .orElseGet(() -> ServerResponse.notFound().build());
    }

    public ServerResponse createProduct(ServerRequest request) throws ServletException, IOException {
        Product product = repository.save(request.body(Product.class));
        URI uri = ServletUriComponentsBuilder.fromServletMapping(request.servletRequest())
                .path(product.getId().toString()).build()
                .toUri();
        return ServerResponse.created(uri).body(product);
    }
}
