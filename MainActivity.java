package com.mydocreader.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecentFilesAdapter adapter;
    private List<String> recentFiles = new ArrayList<>();
    private TextView emptyText;
    private static final String PREFS_NAME = "DocReaderPrefs";
    private static final String RECENT_KEY = "recent_files";

    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<Intent> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);
        FloatingActionButton fab = findViewById(R.id.fab);

        adapter = new RecentFilesAdapter(recentFiles, this::openFile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        String path = FileUtils.getPath(this, uri);
                        if (path != null) {
                            addToRecent(path);
                            openFile(path);
                        } else {
                            openFileByUri(uri);
                        }
                    }
                }
            }
        );

        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> loadRecentFiles()
        );

        fab.setOnClickListener(v -> checkPermissionAndPick());
        checkPermissionAndPick();
        loadRecentFiles();
    }

    private void checkPermissionAndPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                permissionLauncher.launch(intent);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
        openFilePicker();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {
            "application/pdf",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "image/*"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "File Chuniye"));
    }

    private void openFile(String path) {
        String lower = path.toLowerCase();
        Intent intent;
        if (lower.endsWith(".pdf")) {
            intent = new Intent(this, PdfViewerActivity.class);
        } else if (lower.endsWith(".xls") || lower.endsWith(".xlsx")) {
            intent = new Intent(this, ExcelViewerActivity.class);
        } else if (lower.endsWith(".doc") || lower.endsWith(".docx")) {
            intent = new Intent(this, WordViewerActivity.class);
        } else if (lower.endsWith(".txt")) {
            intent = new Intent(this, TextViewerActivity.class);
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".bmp")) {
            intent = new Intent(this, ImageViewerActivity.class);
        } else {
            Toast.makeText(this, getString(R.string.file_not_supported), Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("file_path", path);
        startActivity(intent);
    }

    private void openFileByUri(Uri uri) {
        String mime = getContentResolver().getType(uri);
        Intent intent = null;
        if (mime != null) {
            if (mime.equals("application/pdf")) intent = new Intent(this, PdfViewerActivity.class);
            else if (mime.contains("excel") || mime.contains("spreadsheet")) intent = new Intent(this, ExcelViewerActivity.class);
            else if (mime.contains("word") || mime.contains("wordprocessing")) intent = new Intent(this, WordViewerActivity.class);
            else if (mime.equals("text/plain")) intent = new Intent(this, TextViewerActivity.class);
            else if (mime.startsWith("image/")) intent = new Intent(this, ImageViewerActivity.class);
        }
        if (intent != null) {
            intent.putExtra("file_uri", uri.toString());
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.file_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    private void addToRecent(String path) {
        recentFiles.remove(path);
        recentFiles.add(0, path);
        if (recentFiles.size() > 20) recentFiles.remove(recentFiles.size() - 1);
        saveRecentFiles();
    }

    private void saveRecentFiles() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        JSONArray arr = new JSONArray();
        for (String f : recentFiles) arr.put(f);
        prefs.edit().putString(RECENT_KEY, arr.toString()).apply();
    }

    private void loadRecentFiles() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(RECENT_KEY, "[]");
        recentFiles.clear();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) recentFiles.add(arr.getString(i));
        } catch (JSONException e) { e.printStackTrace(); }
        adapter.notifyDataSetChanged();
        emptyText.setVisibility(recentFiles.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentFiles();
    }
}
