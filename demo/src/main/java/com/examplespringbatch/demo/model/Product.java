package com.examplespringbatch.demo.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {

    //we define the XmlElement name so be sure that mapping works also changing field's name
    @XmlElement(name = "productId")
    private String productId;

    @XmlElement(name = "productName")
    private String productName;

    @XmlElement(name = "productDesc")
    private String productDesc;

    @XmlElement(name = "unit")
    private Integer unit;

    @XmlElement(name = "price")
    private BigDecimal price;


}
