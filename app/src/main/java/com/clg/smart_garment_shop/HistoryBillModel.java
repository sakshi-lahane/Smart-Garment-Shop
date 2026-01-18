package com.clg.smart_garment_shop;

import com.google.firebase.Timestamp;

public class HistoryBillModel {

    public String billId;
    public String customerName;
    public Double finalTotal;
    public String paymentMode;
    public Timestamp timestamp;

    public HistoryBillModel() {}
}
