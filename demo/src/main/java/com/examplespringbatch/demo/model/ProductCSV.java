package com.examplespringbatch.demo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.StringJoiner;

@Data
public class ProductCSV {

    private String productId;

    private String productName;

    private String productDesc;

    private Integer unit;

    private BigDecimal price;

    @Override
    public String toString() {
        return new StringJoiner(", ", ProductCSV.class.getSimpleName() + "(", ")")
                .add("productId=" + productId)
                .add("productName=" + productName)
                .add("productDesc=" + productDesc)
                .add("unit=" + unit)
                .add("price=" + price)
                .toString();
    }
}
