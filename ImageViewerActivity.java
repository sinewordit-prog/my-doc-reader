package com.mydocreader.app;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Image Viewer");
        }

        ImageView imageView = findViewById(R.id.imageView);
        String filePath = getIntent().getStringExtra("file_path");
        String fileUri = getIntent().getStringExtra("file_uri");

        try {
            if (filePath != null) {
                File file = new File(filePath);
                getSupportActionBar().setTitle(file.getName());
                imageView.setImageURI(Uri.fromFile(file));
            } else if (fileUri != null) {
                imageView.setImageURI(Uri.parse(fileUri));
            }
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
