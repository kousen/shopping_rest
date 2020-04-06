package com.kousenit.shopping.controllers;

import com.kousenit.shopping.dao.ProductRepository;
import com.kousenit.shopping.entities.Product;
import com.kousenit.shopping.entities.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String showProducts(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("products", repository.findAll());
        return "products";
    }

    @GetMapping("{id}")
    public String showProduct(@PathVariable Integer id, Model model) {
        Optional<Product> optional = repository.findById(id);
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
        repository.save(product);
        return "redirect:/products";
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteProduct(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/products";
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteAll() {
        repository.deleteAll();
        return "redirect:/products";
    }
}
