package com.kousenit.shopping.controllers;

import com.kousenit.shopping.dao.ProductRepository;
import com.kousenit.shopping.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String showProducts(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("products", repository.findAll());
        return "products";
    }

    @PostMapping
    public String addProduct(@Valid Product product, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Errors: " + errors);
            return "forward:products";
        }

        log.info("Saving product: " + product);
        repository.save(product);
        return "redirect:/products";
    }
}
