package com.clg.smart_garment_shop;

import com.google.firebase.Timestamp;

public class HistoryBillModel {

    public String billId;
    public String customerName;
    public Double finalTotal;
    public String paymentMode;

    // For History Date
    public Timestamp timestamp;

    // For Dashboard (not used here but exists in Firestore)
    public Long createdAt;

    public HistoryBillModel() {}
}
