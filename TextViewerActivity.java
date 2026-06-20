package com.mydocreader.app;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class TextViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Text Viewer");
        }

        TextView textView = findViewById(R.id.textContent);
        String filePath = getIntent().getStringExtra("file_path");
        String fileUri = getIntent().getStringExtra("file_uri");

        try {
            InputStream is;
            if (filePath != null) {
                File file = new File(filePath);
                getSupportActionBar().setTitle(file.getName());
                is = new FileInputStream(file);
            } else if (fileUri != null) {
                Uri uri = Uri.parse(fileUri);
                is = getContentResolver().openInputStream(uri);
            } else return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            textView.setText(sb.toString());
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_opening), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
