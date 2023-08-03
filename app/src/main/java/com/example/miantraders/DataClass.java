package com.example.miantraders;

public class DataClass {
    private String productName;
    private String productCode;
    private String productPrice;
    private String productPercentage;
    private String productCategory;
    private String productImage;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductPercentage() {
        return productPercentage;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductImage() {
        return productImage;
    }

    public DataClass(String productName, String productCode, String productPrice, String productPercentage, String productCategory, String productImage) {
        this.productName = productName;
        this.productCode = productCode;
        this.productPrice = productPrice;
        this.productPercentage = productPercentage;
        this.productCategory = productCategory;
        this.productImage = productImage;
    }

    public DataClass(){

    }
}
