package com.examplespringbatch.demo.reader;

import com.examplespringbatch.demo.model.Product;
import com.examplespringbatch.demo.service.ProductService;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Data
@Component
public class ProductServiceAdapter implements InitializingBean {


    private ProductService service;

    private ArrayList<Product> products = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        products = service.getProducts();
    }

    @Autowired
    public void setService(ProductService service) {
        this.service = service;
    }

    //The reader read 1 element each time, until the list is null. Without this method we have an infinite loop
    public Product nextProduct() {
        if (products.size() > 0) {
            return products.remove(0);
        }
        return null;
    }

}
