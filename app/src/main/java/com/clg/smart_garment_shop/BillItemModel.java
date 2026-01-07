package com.clg.smart_garment_shop;

public class BillItemModel {

    public String name;
    public String size;
    public double price;
    public int qty;
    public double total;

    public BillItemModel() {}

    public BillItemModel(String name, String size, double price) {
        this.name = name;
        this.size = size;
        this.price = price;
        this.qty = 1;
        this.total = price;
    }
}
