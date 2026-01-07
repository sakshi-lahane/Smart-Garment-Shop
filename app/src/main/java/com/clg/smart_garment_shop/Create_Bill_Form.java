package com.clg.smart_garment_shop;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class Create_Bill_Form extends AppCompatActivity {

    EditText etCustomerName, etCustomerMobile, etSearchItem, etDiscount;
    TextView tvSubtotal, tvGST, tvGrandTotal;
    Spinner spPaymentMode;
    RecyclerView rvBillItems;
    Button btnAddItem, btnGenerateInvoice;

    List<BillItemModel> billItems = new ArrayList<>();
    BillItemAdapter billAdapter;

    double subtotal = 0.0;
    double gst = 0.0;

    FirebaseFirestore firestore;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill_form);

        try {
            bindViews();
            setupRecyclerView();
            setupDatabase();
            setupSpinner();
            setupButtons();
        } catch (Exception e) {
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ---------------- BIND VIEWS ----------------
    private void bindViews() {
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerMobile = findViewById(R.id.etCustomerMobile);
        etSearchItem = findViewById(R.id.etSearchItem);
        etDiscount = findViewById(R.id.etDiscount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvGST = findViewById(R.id.tvGST);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        spPaymentMode = findViewById(R.id.spPaymentMode);
        rvBillItems = findViewById(R.id.rvBillItems);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnGenerateInvoice = findViewById(R.id.btnGenerateInvoice);
    }

    // ---------------- RECYCLER VIEW ----------------
    private void setupRecyclerView() {
        rvBillItems.setLayoutManager(new LinearLayoutManager(this));
        billAdapter = new BillItemAdapter(billItems);
        rvBillItems.setAdapter(billAdapter);
    }

    // ---------------- DATABASE ----------------
    private void setupDatabase() {
        firestore = FirebaseFirestore.getInstance();
        StockDBHelper helper = new StockDBHelper(this);
        db = helper.getWritableDatabase();
    }

    // ---------------- PAYMENT SPINNER ----------------
    private void setupSpinner() {
        String[] payments = {"Cash", "UPI", "Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                payments
        );
        spPaymentMode.setAdapter(adapter);
    }

    // ---------------- BUTTONS ----------------
    private void setupButtons() {

        btnAddItem.setOnClickListener(v -> {
            String itemName = etSearchItem.getText().toString().trim();

            if (itemName.isEmpty()) {
                Toast.makeText(this, "Enter item name", Toast.LENGTH_SHORT).show();
                return;
            }

            addItemFromStock(itemName);
            etSearchItem.setText("");
        });

        btnGenerateInvoice.setOnClickListener(v -> generateBill());
    }

    // ---------------- ADD ITEM ----------------
    private void addItemFromStock(String name) {

        if (db == null) {
            Toast.makeText(this, "Database not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT name, size, price, qty FROM stock WHERE name = ?",
                    new String[]{name}
            );

            if (!c.moveToFirst()) {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                return;
            }

            int stockQty = c.getInt(c.getColumnIndexOrThrow("qty"));
            double price = c.getDouble(c.getColumnIndexOrThrow("price"));
            String size = c.getString(c.getColumnIndexOrThrow("size"));

            if (stockQty <= 0) {
                Toast.makeText(this, "Out of stock", Toast.LENGTH_SHORT).show();
                return;
            }

            for (BillItemModel item : billItems) {
                if (item.name.equalsIgnoreCase(name)) {
                    item.qty++;
                    item.total = item.qty * item.price;
                    subtotal += item.price;
                    updateTotals();
                    billAdapter.notifyDataSetChanged();
                    return;
                }
            }

            BillItemModel item = new BillItemModel(name, size, price);
            billItems.add(item);
            subtotal += price;

            updateTotals();
            billAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(this, "Add item error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (c != null) c.close();
        }
    }

    // ---------------- TOTALS ----------------
    private void updateTotals() {
        gst = subtotal * 0.05;
        tvSubtotal.setText("₹" + String.format(Locale.US, "%.2f", subtotal));
        tvGST.setText("₹" + String.format(Locale.US, "%.2f", gst));
        tvGrandTotal.setText("₹" + String.format(Locale.US, "%.2f", subtotal + gst));
    }

    // ---------------- GENERATE BILL ----------------
    private void generateBill() {

        if (billItems.isEmpty()) {
            Toast.makeText(this, "No items added", Toast.LENGTH_SHORT).show();
            return;
        }

        double discount = 0.0;
        try {
            String dis = etDiscount.getText().toString().trim();
            if (dis.endsWith("%")) {
                discount = subtotal * Double.parseDouble(dis.replace("%", "")) / 100;
            } else if (!dis.isEmpty()) {
                discount = Double.parseDouble(dis);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid discount", Toast.LENGTH_SHORT).show();
            return;
        }

        double grandTotal = subtotal + gst - discount;

        List<Map<String, Object>> itemList = new ArrayList<>();

        for (BillItemModel i : billItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", i.name);
            map.put("qty", i.qty);
            map.put("price", i.price);
            map.put("total", i.total);
            itemList.add(map);

            db.execSQL(
                    "UPDATE stock SET qty = qty - ? WHERE name = ?",
                    new Object[]{i.qty, i.name}
            );
        }

        Map<String, Object> bill = new HashMap<>();
        bill.put("customerName", etCustomerName.getText().toString());
        bill.put("mobile", etCustomerMobile.getText().toString());
        bill.put("items", itemList);
        bill.put("subtotal", subtotal);
        bill.put("gst", gst);
        bill.put("discount", discount);
        bill.put("grandTotal", grandTotal);
        bill.put("paymentMode", spPaymentMode.getSelectedItem().toString());
        bill.put("timestamp", new Date());

        firestore.collection("bills")
                .add(bill)
                .addOnSuccessListener(doc ->
                        Toast.makeText(this, "Bill saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
