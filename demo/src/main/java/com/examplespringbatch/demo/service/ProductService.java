package com.examplespringbatch.demo.service;

import com.examplespringbatch.demo.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class ProductService {


    public ArrayList<Product> getProducts() {
        RestTemplate restTemplate = new RestTemplate();
        Product[] linkedList = restTemplate.getForObject("http://localhost:8080/products", Product[].class);
        ArrayList<Product> arrayListCopy = new ArrayList<>();
        arrayListCopy.addAll(CollectionUtils.arrayToList(linkedList));
        return arrayListCopy;
    }

}
