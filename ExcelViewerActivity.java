package com.mydocreader.app;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelViewerActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Spinner sheetSpinner;
    private Workbook workbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Excel Viewer");
        }

        tableLayout = findViewById(R.id.tableLayout);
        sheetSpinner = findViewById(R.id.sheetSpinner);

        String filePath = getIntent().getStringExtra("file_path");
        String fileUri = getIntent().getStringExtra("file_uri");

        try {
            InputStream is;
            String name = "";
            if (filePath != null) {
                File file = new File(filePath);
                name = file.getName();
                is = new FileInputStream(file);
                workbook = name.endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is);
            } else if (fileUri != null) {
                Uri uri = Uri.parse(fileUri);
                is = getContentResolver().openInputStream(uri);
                String mime = getContentResolver().getType(uri);
                workbook = (mime != null && mime.contains("openxmlformats")) ?
                    new XSSFWorkbook(is) : new HSSFWorkbook(is);
            } else return;

            if (getSupportActionBar() != null && !name.isEmpty())
                getSupportActionBar().setTitle(name);

            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++)
                sheetNames.add(workbook.getSheetName(i));

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sheetNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sheetSpinner.setAdapter(spinnerAdapter);
            sheetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadSheet(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            loadSheet(0);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_opening) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadSheet(int index) {
        tableLayout.removeAllViews();
        Sheet sheet = workbook.getSheetAt(index);
        DataFormatter formatter = new DataFormatter();

        for (Row row : sheet) {
            TableRow tableRow = new TableRow(this);
            for (Cell cell : row) {
                TextView tv = new TextView(this);
                tv.setText(formatter.formatCellValue(cell));
                tv.setPadding(16, 8, 16, 8);
                tv.setTextSize(13);
                tv.setBackgroundResource(R.drawable.cell_border);
                tableRow.addView(tv);
            }
            tableLayout.addView(tableRow);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
