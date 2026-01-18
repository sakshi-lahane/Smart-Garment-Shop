package com.clg.smart_garment_shop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;

public class ViewPrintAdapter extends PrintDocumentAdapter {

    private Context context;
    private View view;

    public ViewPrintAdapter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         android.os.CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, android.os.Bundle extras) {

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo info = new PrintDocumentInfo.Builder("invoice.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build();

        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(android.print.PageRange[] pages,
                        android.os.ParcelFileDescriptor destination,
                        android.os.CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                view.getWidth(), view.getHeight(), 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        document.finishPage(page);

        try {
            document.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        callback.onWriteFinished(new android.print.PageRange[]{android.print.PageRange.ALL_PAGES});
    }
}
