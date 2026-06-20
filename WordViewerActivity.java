package com.mydocreader.app;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class WordViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Word Viewer");
        }

        TextView textView = findViewById(R.id.wordTextView);

        String filePath = getIntent().getStringExtra("file_path");
        String fileUri = getIntent().getStringExtra("file_uri");

        try {
            InputStream is;
            String name = "";
            boolean isDocx = false;

            if (filePath != null) {
                File file = new File(filePath);
                name = file.getName();
                isDocx = name.endsWith(".docx");
                is = new FileInputStream(file);
            } else if (fileUri != null) {
                Uri uri = Uri.parse(fileUri);
                is = getContentResolver().openInputStream(uri);
                String mime = getContentResolver().getType(uri);
                isDocx = mime != null && mime.contains("wordprocessingml");
            } else return;

            if (getSupportActionBar() != null && !name.isEmpty())
                getSupportActionBar().setTitle(name);

            String content;
            if (isDocx) {
                XWPFDocument doc = new XWPFDocument(is);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                content = extractor.getText();
                extractor.close();
            } else {
                HWPFDocument doc = new HWPFDocument(is);
                WordExtractor extractor = new WordExtractor(doc);
                content = extractor.getText();
                extractor.close();
            }
            textView.setText(content);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_opening) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
