package com.kousenit.shopping.controllers;

import com.kousenit.shopping.entities.Product;
import com.kousenit.shopping.entities.ProductNotFoundException;
import com.kousenit.shopping.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final Logger log = LoggerFactory.getLogger(ProductController.class.getName());

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public String showProducts(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("products", service.findAll());
        return "products";
    }

    @GetMapping("{id}")
    public String showProduct(@PathVariable Integer id, Model model) {
        Optional<Product> optional = service.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("product", optional.get());
        } else {
            throw new ProductNotFoundException(id);
        }
        return "products";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addProduct(@Valid Product product, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Errors: " + errors);
            return "products";
        }

        log.info("Saving product: " + product);
        service.saveProduct(product);
        return "redirect:/products";
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteProduct(@PathVariable Integer id) {
        service.deleteProduct(id);
        return "redirect:/products";
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteAll() {
        service.deleteAllInBatch();
        return "redirect:/products";
    }
}
