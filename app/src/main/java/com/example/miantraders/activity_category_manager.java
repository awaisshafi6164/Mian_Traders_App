package com.example.miantraders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class activity_category_manager extends AppCompatActivity {
    Spinner spinner;
    ArrayAdapter<String> adapterCategory;
    String spinnerCat;
    int position;
    ArrayList<String> spinnerCategory;
    Button saveBTN;
    EditText editET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manager);

        ////////////Spinner Code Below
        spinnerCategory = CategoryManager.getCategoryList(this);
        spinner = findViewById(R.id.spinnerCatManager);
        adapterCategory = new ArrayAdapter<String>(this, R.layout.list_category, spinnerCategory);
        spinner.setAdapter(adapterCategory);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCat = adapterView.getItemAtPosition(i).toString();
                position = i;
                Toast.makeText(activity_category_manager.this, spinnerCat, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerCat = null;
            }
        });
        ////////////////////////

        Button deleteBTN = findViewById(R.id.deleteBTNCatManager);
        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItemFromSpinnerCategory(spinnerCat);
            }
        });

        saveBTN = findViewById(R.id.saveBTNCatManager);
        editET = findViewById(R.id.editCatET);
        Button editBTN = findViewById(R.id.editBTNCatManager);
        editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editET.setVisibility(View.VISIBLE);
                saveBTN.setVisibility(View.VISIBLE);
            }
        });
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItemFromSpinnerCategory();
            }
        });

    }

    private void editItemFromSpinnerCategory(){
        String newText = editET.getText().toString().trim();
        if (!newText.isEmpty()) {
            spinnerCategory.set(position, newText);
            CategoryManager.saveCategoryList(activity_category_manager.this, spinnerCategory);
            adapterCategory.notifyDataSetChanged();
            Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
            editET.setVisibility(View.GONE);
            saveBTN.setVisibility(View.GONE);
        }else{
            Toast.makeText(this, "Edit Field is Empty", Toast.LENGTH_SHORT).show();
        }
    }
    private void removeItemFromSpinnerCategory(String itemToRemove) {
        spinnerCategory.remove(spinnerCat);
        CategoryManager.saveCategoryList(activity_category_manager.this, spinnerCategory);
        adapterCategory.notifyDataSetChanged();
        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
    }

}