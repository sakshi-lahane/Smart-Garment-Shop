package com.clg.smart_garment_shop;

public class BillItemModel {

    private String productId;
    private String name;
    private String category;
    private String subCategory;   // NEW
    private double price;
    private int quantity;
    private int stockLimit;

    public BillItemModel(String productId,
                         String name,
                         String category,
                         String subCategory,
                         double price,
                         int quantity,
                         int stockLimit) {

        this.productId = productId;
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;   // NEW
        this.price = price;
        this.quantity = quantity;
        this.stockLimit = stockLimit;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {      // NEW
        return subCategory;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStockLimit() {
        return stockLimit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return price * quantity;
    }
}
