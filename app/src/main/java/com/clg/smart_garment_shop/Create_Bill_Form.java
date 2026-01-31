package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class Create_Bill_Form extends AppCompatActivity
        implements BillItemAdapter.OnBillChangeListener {

    EditText etCustomerName, etCustomerMobile, etDiscount;
    AutoCompleteTextView etSearchItem;
    TextView tvSubtotal, tvGrandTotal, tvShopName, tvOwnerName, tvBillNo, tvDate;
    Spinner spPaymentMode;
    RecyclerView rvBillItems;
    Button btnGenerateInvoice;
    ImageButton btnBack;

    List<BillItemModel> billItems = new ArrayList<>();
    BillItemAdapter billAdapter;

    double subtotal = 0.0;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    List<String> productNames = new ArrayList<>();
    ArrayAdapter<String> productAdapter;

    int billCounter = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill_form);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bindViews();
        setupRecyclerView();
        setupSpinner();

        AutoFixUserData.fixIfMissing(this);
        loadHeaderData();
        loadProductNames();
        loadBillNumber();
        setTodayDate();
        setupDiscountListener();
        setupDropdownAutoAdd();
        toggleRecyclerVisibility();

        btnGenerateInvoice.setOnClickListener(v -> generateBill());
        btnBack.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        tvShopName = findViewById(R.id.tvShopName);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvBillNo = findViewById(R.id.tvBillNo);
        tvDate = findViewById(R.id.tvDate);
        btnBack = findViewById(R.id.btnBack);

        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerMobile = findViewById(R.id.etCustomerMobile);
        etSearchItem = findViewById(R.id.etSearchItem);
        etDiscount = findViewById(R.id.etDiscount);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        spPaymentMode = findViewById(R.id.spPaymentMode);
        rvBillItems = findViewById(R.id.rvBillItems);
        btnGenerateInvoice = findViewById(R.id.btnGenerateInvoice);
    }

    private void setupRecyclerView() {
        rvBillItems.setLayoutManager(new LinearLayoutManager(this));
        billAdapter = new BillItemAdapter(billItems, this);
        rvBillItems.setAdapter(billAdapter);
    }

    private void setupSpinner() {
        String[] payments = {"Cash", "UPI", "Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                payments
        );
        spPaymentMode.setAdapter(adapter);
    }

    // DROPDOWN AUTO ADD
    private void setupDropdownAutoAdd() {
        etSearchItem.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProduct = parent.getItemAtPosition(position).toString();
            addItemFromFirestore(selectedProduct);
            etSearchItem.setText("");
        });
    }

    //  HEADER DATA
    private void loadHeaderData() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    String shop = doc.getString("shopName");
                    String owner = doc.getString("ownerName");

                    tvShopName.setText(shop != null ? shop : "My Shop");
                    tvOwnerName.setText(owner != null ? owner : "Owner");
                });
    }

    private void loadBillNumber() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("bills")
                .get()
                .addOnSuccessListener(qs -> {
                    billCounter = 1000 + qs.size() + 1;
                    tvBillNo.setText("Bill No: BILL-" + billCounter);
                });
    }

    private void setTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String today = sdf.format(new Date());
        tvDate.setText("Date: " + today);
    }

    //  SEARCH DROPDOWN
    private void loadProductNames() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("products")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    productNames.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("productName");
                        if (name != null) productNames.add(name);
                    }

                    productAdapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            productNames
                    );
                    etSearchItem.setAdapter(productAdapter);
                });
    }

    // ================= ADD ITEM =================
    private void addItemFromFirestore(String name) {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("products")
                .whereEqualTo("productName", name)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                    String productId = doc.getId();
                    String itemName = doc.getString("productName");
                    String category = doc.getString("category");
                    String subCategory = doc.getString("subCategory"); // NEW
                    double price = doc.getDouble("price");
                    Long stockQty = doc.getLong("quantity");

                    if (stockQty == null || stockQty <= 0) {
                        Toast.makeText(this, "Out of stock", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int stockLimit = stockQty.intValue();

                    for (BillItemModel item : billItems) {
                        if (item.getProductId().equals(productId)) {
                            if (item.getQuantity() < stockLimit) {
                                item.setQuantity(item.getQuantity() + 1);
                            } else {
                                Toast.makeText(this, "Stock limit reached", Toast.LENGTH_SHORT).show();
                            }
                            billAdapter.notifyDataSetChanged();
                            updateTotals();
                            toggleRecyclerVisibility();
                            return;
                        }
                    }

                    BillItemModel newItem = new BillItemModel(
                            productId,
                            itemName,
                            category,
                            subCategory,
                            price,
                            1,
                            stockLimit
                    );

                    billItems.add(newItem);
                    billAdapter.notifyDataSetChanged();
                    updateTotals();
                    toggleRecyclerVisibility();
                });
    }

    //  TOTALS
    private void updateTotals() {
        subtotal = 0.0;
        for (BillItemModel item : billItems) {
            subtotal += item.getTotal();
        }

        double finalTotal = applyDiscount(subtotal);

        tvSubtotal.setText("Subtotal: ₹" + String.format(Locale.US, "%.2f", subtotal));
        tvGrandTotal.setText("Final Total: ₹" + String.format(Locale.US, "%.2f", finalTotal));
    }

    private double applyDiscount(double amount) {
        String dis = etDiscount.getText().toString().trim();
        double discount = 0;

        try {
            if (dis.endsWith("%")) {
                double percent = Double.parseDouble(dis.replace("%", ""));
                discount = amount * percent / 100;
            } else if (!dis.isEmpty()) {
                discount = Double.parseDouble(dis);
            }
        } catch (Exception ignored) {}

        return amount - discount;
    }

    private void setupDiscountListener() {
        etDiscount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotals();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onBillChanged() {
        updateTotals();
        toggleRecyclerVisibility();
    }

    //  Recycler Auto Show/Hide
    private void toggleRecyclerVisibility() {
        if (billItems.isEmpty()) {
            rvBillItems.setVisibility(View.GONE);
        } else {
            rvBillItems.setVisibility(View.VISIBLE);
        }
    }

    //  SAVE BILL
    private void generateBill() {

        if (billItems.isEmpty()) {
            Toast.makeText(this, "No items added", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        double finalTotal = applyDiscount(subtotal);

        List<Map<String, Object>> itemList = new ArrayList<>();

        for (BillItemModel i : billItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", i.getProductId());
            map.put("name", i.getName());
            map.put("category", i.getCategory());
            map.put("subCategory", i.getSubCategory());
            map.put("qty", i.getQuantity());
            map.put("price", i.getPrice());
            map.put("total", i.getTotal());
            itemList.add(map);

            firestore.collection("users")
                    .document(userId)
                    .collection("products")
                    .document(i.getProductId())
                    .update("quantity", FieldValue.increment(-i.getQuantity()));
        }

        Map<String, Object> bill = new HashMap<>();
        bill.put("billNo", "BILL-" + billCounter);
        bill.put("customerName", etCustomerName.getText().toString());
        bill.put("mobile", etCustomerMobile.getText().toString());
        bill.put("subtotal", subtotal);

        bill.put("finalTotal", finalTotal);            // history + invoice
        bill.put("totalAmount", finalTotal);          // dashboard

        bill.put("discount", etDiscount.getText().toString());
        bill.put("paymentMode", spPaymentMode.getSelectedItem().toString());

        bill.put("timestamp", new Date());            // HISTORY (keep old working)
        bill.put("createdAt", System.currentTimeMillis()); // DASHBOARD (today sales)

        bill.put("items", itemList);



        firestore.collection("users")
                .document(userId)
                .collection("bills")
                .add(bill)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Bill Created: BILL-" + billCounter, Toast.LENGTH_LONG).show();

                    String billId = doc.getId(); // IMPORTANT

                    Intent intent = new Intent(Create_Bill_Form.this, Invoice.class);
                    intent.putExtra("billId", billId);
                    startActivity(intent);
                    finish();
                });

    }
}
