package com.swt.simpleservice.controller;

import com.swt.simpleservice.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ProductController {


    @GetMapping("/products")
    public List<Product> getProducts() {

        return Stream.of(
                new Product("1", "Apple iPhone", "Apple cell", 10, new BigDecimal("300.00")),
                new Product("2", "Dell computer", "Personal PC Dell", 20, new BigDecimal("2000.00")))
                .collect(Collectors.toList());
    }

}
