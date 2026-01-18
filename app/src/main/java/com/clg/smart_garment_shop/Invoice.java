package com.clg.smart_garment_shop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Invoice extends AppCompatActivity {

    TextView tvShopName, tvOwnerName, tvBillNo, tvDate;
    TextView tvCustomerName, tvCustomerMobile;
    TextView tvSubtotal, tvDiscount, tvFinalTotal, tvPaymentMode;
    LinearLayout itemsContainer, mainLayout;

    Button btnPrint, btnPdf, btnShare;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    String billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bindViews();

        billId = getIntent().getStringExtra("billId");

        if (billId == null) {
            Toast.makeText(this, "Bill not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadHeaderData();
        loadBillData();

        btnShare.setOnClickListener(v -> shareOnWhatsApp());
        btnPdf.setOnClickListener(v -> createPdf());
        btnPrint.setOnClickListener(v -> printInvoice());
    }

    private void bindViews() {
        tvShopName = findViewById(R.id.tvShopName);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvBillNo = findViewById(R.id.tvBillNo);
        tvDate = findViewById(R.id.tvDate);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerMobile = findViewById(R.id.tvCustomerMobile);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        tvPaymentMode = findViewById(R.id.tvPaymentMode);

        itemsContainer = findViewById(R.id.itemsContainer);
        mainLayout = findViewById(R.id.mainInvoiceLayout);

        btnPrint = findViewById(R.id.btnPrint);
        btnPdf = findViewById(R.id.btnPdf);
        btnShare = findViewById(R.id.btnShare);
    }

    private void loadHeaderData() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    tvShopName.setText(doc.getString("shopName"));
                    tvOwnerName.setText(doc.getString("ownerName"));
                });
    }

    private void loadBillData() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("bills")
                .document(billId)
                .get()
                .addOnSuccessListener(doc -> {

                    tvBillNo.setText("Bill No: " + doc.getString("billNo"));

                    if (doc.getDate("timestamp") != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        tvDate.setText("Date: " + sdf.format(doc.getDate("timestamp")));
                    }

                    tvCustomerName.setText("Customer: " + doc.getString("customerName"));
                    tvCustomerMobile.setText("Mobile: " + doc.getString("mobile"));

                    tvSubtotal.setText("Subtotal: ₹" + doc.getDouble("subtotal"));
                    tvDiscount.setText("Discount: " + doc.getString("discount"));
                    tvFinalTotal.setText("Final Total: ₹" + doc.getDouble("finalTotal"));
                    tvPaymentMode.setText("Payment: " + doc.getString("paymentMode"));

                    List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                    showItems(items);
                });
    }

    private void showItems(List<Map<String, Object>> items) {
        itemsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map<String, Object> item : items) {
            View row = inflater.inflate(R.layout.invoice_item_row, itemsContainer, false);

            ((TextView) row.findViewById(R.id.tvItemName)).setText((String) item.get("name"));
            ((TextView) row.findViewById(R.id.tvQty)).setText(String.valueOf(item.get("qty")));
            ((TextView) row.findViewById(R.id.tvPrice)).setText("₹" + item.get("price"));
            ((TextView) row.findViewById(R.id.tvTotal)).setText("₹" + item.get("total"));

            itemsContainer.addView(row);
        }
    }

    // ================= WHATSAPP SHARE =================
    private void shareOnWhatsApp() {
        StringBuilder builder = new StringBuilder();

        builder.append("🧾 ").append(tvShopName.getText()).append("\n");
        builder.append(tvOwnerName.getText()).append("\n\n");

        builder.append("📄 ").append(tvBillNo.getText()).append("\n");
        builder.append("📅 ").append(tvDate.getText()).append("\n\n");

        builder.append(tvCustomerName.getText()).append("\n");
        builder.append(tvCustomerMobile.getText()).append("\n\n");

        builder.append("🛍 Items:\n");

        for (int i = 0; i < itemsContainer.getChildCount(); i++) {
            View row = itemsContainer.getChildAt(i);

            TextView name = row.findViewById(R.id.tvItemName);
            TextView qty = row.findViewById(R.id.tvQty);
            TextView total = row.findViewById(R.id.tvTotal);

            builder.append("• ")
                    .append(name.getText())
                    .append(" x")
                    .append(qty.getText())
                    .append(" = ")
                    .append(total.getText())
                    .append("\n");
        }

        builder.append("\n");
        builder.append(tvSubtotal.getText()).append("\n");
        builder.append(tvDiscount.getText()).append("\n");
        builder.append(tvFinalTotal.getText()).append("\n");
        builder.append(tvPaymentMode.getText()).append("\n\n");
        builder.append("🤝 Thank you for shopping with us ❤️");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        intent.setPackage("com.whatsapp");

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    // ================= CREATE PDF =================
    private void createPdf() {
        try {
            Bitmap bitmap = getBitmapFromView(mainLayout);

            PdfDocument document = new PdfDocument();

            int pageWidth = 595;
            int pageHeight = 842;

            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int margin = 20;
            canvas.drawBitmap(bitmap, margin, margin, null);

            document.finishPage(page);

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    "Invoice_" + System.currentTimeMillis() + ".pdf");

            document.writeTo(new FileOutputStream(file));
            document.close();

            Toast.makeText(this, "PDF Saved: " + file.getName(), Toast.LENGTH_LONG).show();

            sharePdf(file);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF error", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePdf(File file) {
        Uri uri = FileProvider.getUriForFile(this,
                getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Invoice PDF"));
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // ================= PRINT =================
    private void printInvoice() {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("Invoice",
                new ViewPrintAdapter(this, mainLayout),
                new PrintAttributes.Builder().build());
    }
}
