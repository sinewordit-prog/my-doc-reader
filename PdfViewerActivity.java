package com.mydocreader.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("PDF Viewer");
        }

        pdfView = findViewById(R.id.pdfView);

        String filePath = getIntent().getStringExtra("file_path");
        String fileUri = getIntent().getStringExtra("file_uri");

        if (filePath != null) {
            File file = new File(filePath);
            getSupportActionBar().setTitle(file.getName());
            pdfView.fromFile(file)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .onError(t -> Toast.makeText(this, getString(R.string.error_opening), Toast.LENGTH_SHORT).show())
                .load();
        } else if (fileUri != null) {
            Uri uri = Uri.parse(fileUri);
            pdfView.fromUri(uri)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .onError(t -> Toast.makeText(this, getString(R.string.error_opening), Toast.LENGTH_SHORT).show())
                .load();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
