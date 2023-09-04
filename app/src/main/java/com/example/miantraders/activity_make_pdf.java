package com.example.miantraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class activity_make_pdf extends AppCompatActivity {
    Spinner pdfSpinner;
    Button pdfBTN;
    String spinnerCat;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_pdf);
        pdfSpinner = findViewById(R.id.pdfSpinner);
        pdfBTN = findViewById(R.id.pdfBTN);

        databaseReference = FirebaseDatabase.getInstance().getReference("Category Manager");
        databaseReference.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> categoryList = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    categoryList.add(category);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity_make_pdf.this, android.R.layout.simple_spinner_item, categoryList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pdfSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity_make_pdf.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> spinnerCategory = CategoryManager.getCategoryList(this);
        boolean hasAllCategory = false;
        for (String category : spinnerCategory) {
            if (category.equalsIgnoreCase("All")) {
                hasAllCategory = true;
                break;
            }
        }
        if (!hasAllCategory) {
            spinnerCategory.add(0, "All");
        }

        pdfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCat = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(activity_make_pdf.this, spinnerCat, Toast.LENGTH_SHORT).show();
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
                fetchDataFromFirebase();
            }
        });

    }

    private void make_pdf_method(List<DataClass> dataList) {
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
        myPaint.setTextSize(6f);
        myPaint.setFakeBoldText(false);

        // Define the maximum width for the address text
        float maxWidth = myPageInfo1.getPageWidth() - 40; // Adjust the margin as needed
        // Split the address into lines to fit within the maxWidth
        String address = "Mian Traders, Zafar Bypass, Chanpeer Road, Narowal, delete this lenth text as you want";
        String[] addressLines = splitTextToFitWidth(address, myPaint, maxWidth);
        // Draw each line of the address
        float yPos = 50; // Adjust the Y position as needed
        for (String line : addressLines) {
            canvas.drawText(line, 20, yPos, myPaint);
            yPos += myPaint.getFontSpacing(); // Increment the Y position for the next line
        }

        //get data from firebase and show in pdf in tabular form
        // Iterate through dataList and add data to PDF
        float yPosition = 80; // Adjust the Y position as needed
        float column1X = 7; // X position for the first column
        float column2X = 40; // X position for the second column
        float column3X = 160; // X position for the third column
        float column4X = 190; // X position for the fourth column

        myPaint.setFakeBoldText(true);
        canvas.drawText("Code", column1X, yPosition, myPaint);
        canvas.drawText("Product Name", column2X, yPosition, myPaint);
        canvas.drawText("Price", column3X, yPosition, myPaint);
        canvas.drawText("Per", column4X, yPosition, myPaint);
        myPaint.setFakeBoldText(false);
        yPosition += myPaint.getFontSpacing();

        for (DataClass data : dataList) {

            canvas.drawText(data.getProductCode(), column1X, yPosition, myPaint);

            // Define the maximum width for the address text
            float max_width = myPageInfo1.getPageWidth() - 110; // Adjust the margin as needed
            // Split the address into lines to fit within the maxWidth
            String product_name = data.getProductName();
            String[] product_lines = splitTextToFitWidth(product_name, myPaint, max_width);
            // Draw each line of the address
            //float yPos = 50; // Adjust the Y position as needed
            float delPos = 0;
            for (String line : product_lines) {
                canvas.drawText(line, 40, yPosition, myPaint);
                yPosition += myPaint.getFontSpacing(); // Increment the Y position for the next line
                delPos += myPaint.getFontSpacing();
            }
            yPosition -= delPos;

            //canvas.drawText(data.getProductName(), column2X, yPosition, myPaint);
            canvas.drawText(data.getProductPrice(), column3X, yPosition, myPaint);
            canvas.drawText(data.getProductPercentage(), column4X, yPosition, myPaint);

            yPosition += delPos;
            yPosition += myPaint.getFontSpacing(); // Increment Y position for the next row
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

    private void fetchDataFromFirebase() {
        DatabaseReference dataReference = FirebaseDatabase.getInstance().getReference("Android Tutorials"); // Update the reference to your data
        dataReference.orderByChild("productCategory").equalTo(spinnerCat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Process the data retrieved from Firebase
                List<DataClass> dataList = new ArrayList<>();
                for (DataSnapshot itemSnapShot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapShot.getValue(DataClass.class);
                    dataList.add(dataClass);
                }

                // Generate the PDF with the retrieved data
                make_pdf_method(dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity_make_pdf.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}