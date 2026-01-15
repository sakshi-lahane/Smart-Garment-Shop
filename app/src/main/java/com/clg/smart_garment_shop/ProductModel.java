package com.clg.smart_garment_shop;

public class ProductModel {

    private String productId;
    private String productName;
    private String category;
    private String subCategory;
    private String size;
    private String color;
    private double price;
    private int quantity;
    private String createdBy;
    private long createdAt;

    public ProductModel() {}

    public ProductModel(String productId, String productName, String category,
                        String subCategory, String size, String color,
                        double price, int quantity, String createdBy, long createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.subCategory = subCategory;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // 🔹 GETTERS
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getCreatedBy() { return createdBy; }
    public long getCreatedAt() { return createdAt; }

    // 🔹 SETTERS (Firestore needs these)
    public void setProductId(String productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setCategory(String category) { this.category = category; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
