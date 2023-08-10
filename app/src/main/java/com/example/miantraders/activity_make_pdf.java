package com.example.miantraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class activity_make_pdf extends AppCompatActivity {
    Spinner pdfSpinner;
    ArrayAdapter<String> adapterCategory;
    String spinnerCat;
    MyAdapter adapter;
    List<DataClass> dataList;
    
    Button pdfBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_pdf);


        dataList = new ArrayList<>();
        adapter = new MyAdapter(activity_make_pdf.this, dataList);

        ArrayList<String> spinnerCategory = CategoryManager.getCategoryList(this);

// Check if "All" category is present in the spinnerCategory list
        boolean hasAllCategory = false;
        for (String category : spinnerCategory) {
            if (category.equalsIgnoreCase("All")) {
                hasAllCategory = true;
                break;
            }
        }

// If "All" category is not present, add it at position 0
        if (!hasAllCategory) {
            spinnerCategory.add(0, "All");
        }

        pdfSpinner = findViewById(R.id.pdfSpinner);
        adapterCategory = new ArrayAdapter<String>(this, R.layout.list_category, spinnerCategory);
        pdfSpinner.setAdapter(adapterCategory);

        pdfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCat = adapterView.getItemAtPosition(i).toString();

                if (spinnerCat.equalsIgnoreCase("All")) {
                    Toast.makeText(activity_make_pdf.this, "All Cat", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity_make_pdf.this, "Other Cat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerCat = null;
            }
        });

        pdfBTN = findViewById(R.id.pdfBTN);
        pdfBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_pdf_method();
            }
        });
        
    }

    private void make_pdf_method() {
        PdfDocument myPdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(210, 297, 1).create();
        PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
        Canvas canvas = myPage1.getCanvas();
        canvas.drawText("Mian Trader's", 40, 50, myPaint);
        myPdfDocument.finishPage(myPage1);

        File file = new File(getExternalFilesDir(null), "FirstPDF.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            myPdfDocument.writeTo(fos);
            fos.close();
            Toast.makeText(this, "PDF created successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF.", Toast.LENGTH_SHORT).show();
        } finally {
            myPdfDocument.close();
        }
    }
}