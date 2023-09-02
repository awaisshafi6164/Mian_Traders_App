package com.example.miantraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class activity_all_category_manager extends AppCompatActivity {
    Button newCatBTN, updateCatBTN, deleteCatBTN, newCatETBTN, updateCatETBTN, deleteCatETBTN;
    ImageButton clearCatIMGBTN;
    Spinner spinnerCatManager;
    EditText newCatET, updateCatET;
    TextView updateCatTV, deleteCatTV, deleteInfoCatTV;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category_manager);

        newCatBTN = findViewById(R.id.newCatBTN);
        updateCatBTN = findViewById(R.id.updateCatBTN);
        deleteCatBTN = findViewById(R.id.deleteCatBTN);
        newCatETBTN = findViewById(R.id.newCatETBTN);
        updateCatETBTN = findViewById(R.id.updateCatETBTN);
        deleteCatETBTN = findViewById(R.id.deleteCatETBTN);

        clearCatIMGBTN = findViewById(R.id.clearCatIMGBTN);

        spinnerCatManager = findViewById(R.id.spinnerCatManager);

        newCatET = findViewById(R.id.newCatET);
        updateCatET = findViewById(R.id.updateCatET);

        updateCatTV = findViewById(R.id.updateCatTV);
        deleteCatTV = findViewById(R.id.deleteCatTV);
        deleteInfoCatTV = findViewById(R.id.deleteInfoCatTV);

        newCatBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCatET.setVisibility(View.VISIBLE);
                newCatETBTN.setVisibility(View.VISIBLE);

                updateCatTV.setVisibility(View.GONE);
                updateCatET.setVisibility(View.GONE);
                updateCatETBTN.setVisibility(View.GONE);

                deleteInfoCatTV.setVisibility(View.GONE);
                deleteCatTV.setVisibility(View.GONE);
                deleteCatETBTN.setVisibility(View.GONE);
            }
        });

        updateCatBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCatET.setVisibility(View.GONE);
                newCatETBTN.setVisibility(View.GONE);

                updateCatTV.setVisibility(View.VISIBLE);
                updateCatET.setVisibility(View.VISIBLE);
                updateCatETBTN.setVisibility(View.VISIBLE);

                deleteInfoCatTV.setVisibility(View.GONE);
                deleteCatTV.setVisibility(View.GONE);
                deleteCatETBTN.setVisibility(View.GONE);

                // Set the selected spinner text to updateCatTV
                String selectedCategory = spinnerCatManager.getSelectedItem().toString();
                updateCatTV.setText("Selected Category: " + selectedCategory);
                updateCatET.setText(selectedCategory);
            }
        });

        deleteCatBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCatET.setVisibility(View.GONE);
                newCatETBTN.setVisibility(View.GONE);

                updateCatTV.setVisibility(View.GONE);
                updateCatET.setVisibility(View.GONE);
                updateCatETBTN.setVisibility(View.GONE);

                deleteInfoCatTV.setVisibility(View.VISIBLE);
                deleteCatTV.setVisibility(View.VISIBLE);
                deleteCatETBTN.setVisibility(View.VISIBLE);

                // Set the selected spinner text to updateCatTV
                String selectedCategory = spinnerCatManager.getSelectedItem().toString();
                deleteCatTV.setText("Selected Category: " + selectedCategory);
            }
        });
        clearCatIMGBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCatET.setVisibility(View.GONE);
                newCatETBTN.setVisibility(View.GONE);

                updateCatTV.setVisibility(View.GONE);
                updateCatET.setVisibility(View.GONE);
                updateCatETBTN.setVisibility(View.GONE);

                deleteInfoCatTV.setVisibility(View.GONE);
                deleteCatTV.setVisibility(View.GONE);
                deleteCatETBTN.setVisibility(View.GONE);
            }
        });

        //Initialize the Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Category Manager");
        databaseReference.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> categoryList = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    categoryList.add(category);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity_all_category_manager.this, android.R.layout.simple_spinner_item, categoryList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCatManager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity_all_category_manager.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        newCatETBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCatETBTN_method();
            }
        });

        updateCatETBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCatETBTN_method();
            }
        });

        deleteCatETBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCatETBTN_method();
            }
        });
    }

    private void deleteCatETBTN_method() {
        String selectedCategory = spinnerCatManager.getSelectedItem().toString();

        if (!TextUtils.isEmpty(selectedCategory)) {
            DatabaseReference selectedCategoryRef = databaseReference.child("categories").child(selectedCategory);

            // Remove the selected category from Firebase
            selectedCategoryRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity_all_category_manager.this, "Category Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity_all_category_manager.this, "Failed to Delete Category", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(activity_all_category_manager.this, "Please select a category to delete", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateCatETBTN_method() {
        String updatedCategory = updateCatET.getText().toString().trim().toLowerCase();
        String selectedCategory = spinnerCatManager.getSelectedItem().toString();

        if (!TextUtils.isEmpty(updatedCategory)) {
            // Check if the selected category exists in the spinner
            if (categoryExists(selectedCategory)) {
                DatabaseReference selectedCategoryRef = databaseReference.child("categories").child(selectedCategory);

                // Update the key to the new category name
                selectedCategoryRef.getParent().child(updatedCategory).setValue(updatedCategory);

                // Remove the old key
                selectedCategoryRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity_all_category_manager.this, "Category Updated Successfully", Toast.LENGTH_SHORT).show();
                            // Clear the updateCatET EditText
                            updateCatET.setText("");
                        } else {
                            Toast.makeText(activity_all_category_manager.this, "Failed to Update Category", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(activity_all_category_manager.this, "Selected category does not exist", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity_all_category_manager.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }




    // Helper method to check if a category exists in the spinner
    private boolean categoryExists(String categoryName) {
        for (int i = 0; i < spinnerCatManager.getCount(); i++) {
            if (spinnerCatManager.getItemAtPosition(i).toString().equalsIgnoreCase(categoryName)) {
                return true;
            }
        }
        return false;
    }
    public void newCatETBTN_method(){
        String categoryText = newCatET.getText().toString().trim().toLowerCase();

        if (!TextUtils.isEmpty(categoryText)) {
            DatabaseReference newCategoryRef = databaseReference.child("categories").child(categoryText);
            newCategoryRef.setValue(categoryText);
            Toast.makeText(activity_all_category_manager.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            newCatET.setText("");
        } else {
            Toast.makeText(activity_all_category_manager.this, "Category cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}