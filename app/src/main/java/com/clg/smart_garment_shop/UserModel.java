package com.clg.smart_garment_shop;

public class UserModel {

    public String uid, shopName, ownerName, email, mobile;

    public UserModel() {}

    public UserModel(String uid, String shopName,
                     String ownerName, String email,
                     String mobile) {
        this.uid = uid;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.email = email;
        this.mobile = mobile;
    }
}
