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

        // Heading 1
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(14f);
        myPaint.setFakeBoldText(true); // To make the text bold
        canvas.drawText("Mian Traders", myPageInfo1.getPageWidth() / 2, 30, myPaint);

        // Heading 2 (Address)
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8f);
        myPaint.setFakeBoldText(false);

        // Define the maximum width for the address text
        float maxWidth = myPageInfo1.getPageWidth() - 40; // Adjust the margin as needed

        // Split the address into lines to fit within the maxWidth
        String address = "Shop Number, Street Number, City Name, Country Name";
        String[] addressLines = splitTextToFitWidth(address, myPaint, maxWidth);

        // Draw each line of the address
        float yPos = 50; // Adjust the Y position as needed
        for (String line : addressLines) {
            canvas.drawText(line, 20, yPos, myPaint);
            yPos += myPaint.getFontSpacing(); // Increment the Y position for the next line
        }

        // Heading 3 (Category Name)
        myPaint.setTextSize(12f);
        String cattext = "Category: "+spinnerCat;
        canvas.drawText(cattext, 20, 80, myPaint); // You might want to adjust the position based on your layout

        //
        // Draw Table Header (Column Names)
        myPaint.setTextSize(8f);
        String[] columnNames = {"Sr.", "Image", "Name", "Price", "Code", "Per"};
        float[] columnWidths = {15, 40, 40, 25, 25, 15}; // Adjust column widths as needed

        float xPos = 10;
        yPos = 110; // Adjust Y position as needed
        for (int i = 0; i < columnNames.length; i++) {
            canvas.drawText(columnNames[i], xPos, yPos, myPaint);
            xPos += columnWidths[i];
        }

        // Draw Table Rows (Simulated data)
        List<String[]> productList = getFirebaseData(); // Replace with your data retrieval logic
        yPos += myPaint.getFontSpacing() + 5; // Move to the next row

        for (int i = 0; i < productList.size(); i++) {
            String[] rowData = productList.get(i);
            xPos = 10;

            for (int j = 0; j < rowData.length; j++) {
                canvas.drawText(rowData[j], xPos, yPos, myPaint);
                xPos += columnWidths[j];
            }

            yPos += myPaint.getFontSpacing() + 5; // Move to the next row
        }

        myPdfDocument.finishPage(myPage1);

        File file = new File(getExternalFilesDir(null), "TestPDF.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            myPdfDocument.writeTo(fos);
            fos.close();
            Toast.makeText(this, "TestPDF created successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF.", Toast.LENGTH_SHORT).show();
        } finally {
            myPdfDocument.close();
        }
    }

    private List<String[]> getFirebaseData() {
        // Replace this with your actual data retrieval logic
        // Each element in the list represents a row with data for each column
        List<String[]> dataList = new ArrayList<>();

        dataList.add(new String[]{"1", "Image 1", "Product A", "$10", "Code A", "5%"});
        dataList.add(new String[]{"2", "Image 2", "Product B", "$15", "Code B", "10%"});
        // Add more rows as needed

        return dataList;
    }

    private String[] splitTextToFitWidth(String text, Paint paint, float maxWidth) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        int end;

        while (start < text.length()) {
            end = paint.breakText(text, start, text.length(), true, maxWidth, null);
            lines.add(text.substring(start, start + end));
            start += end;
        }

        return lines.toArray(new String[0]);
    }


}