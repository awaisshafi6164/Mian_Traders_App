package com.example.miantraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class activity_detail extends AppCompatActivity {

    ImageView detailImage;
    TextView detailName, detailCode, detailPrice, detailPerc, detailCateg;

    Button deleteButton, editButton;
    String key = "";
    String imageUrl = "";

    AlertDialog dialog2, dialog3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailCode = findViewById(R.id.detailCode);
        detailPrice = findViewById(R.id.detailPrice);
        detailPerc = findViewById(R.id.detailPerc);
        detailCateg = findViewById(R.id.detailCatg);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            detailName.setText(bundle.getString("Name"));
            detailCode.setText(bundle.getString("Code"));
            detailPrice.setText(bundle.getString("Price"));
            detailPerc.setText(bundle.getString("Percentage"));
            detailCateg.setText(bundle.getString("Category"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
        }

        String cat = detailCateg.getText().toString();
        ArrayList<String> categoryList = CategoryManager.getCategoryList(this);

//        int position = categoryList.indexOf(cat);
//        if (position != -1) {
//            String toastMessage = "Position of " + cat + " is " + position;
//            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Position: "+position+" Category not found", Toast.LENGTH_SHORT).show();
//        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog3.show();
            }
        });

        //////////////Passsword Checker for Delete
        AlertDialog.Builder passbuilderDelete = new AlertDialog.Builder(this);
        passbuilderDelete.setTitle("Enter Password");

        View viewDelete = getLayoutInflater().inflate(R.layout.admin_password_popup, null);
        EditText adminPasswordET = viewDelete.findViewById(R.id.adminPasswordET);
        Button checkPasswordBTN = viewDelete.findViewById(R.id.checkPasswordBTN);

        checkPasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = adminPasswordET.getText().toString();
                String pass = "razzaq123";
                if (temp.equals(pass)) {
                    dialog2.dismiss();
                    Toast.makeText(activity_detail.this, "Deleting...", Toast.LENGTH_SHORT).show();

                    ////////////////////// Delete Code
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child(key).removeValue();
                            Toast.makeText(activity_detail.this, "Deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity_detail.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    /////////////////////////////////

                } else {
                    Toast.makeText(activity_detail.this, "Incorrect Password! Try Again", Toast.LENGTH_SHORT).show();
                }
                adminPasswordET.setText("");
            }
        });

        passbuilderDelete.setView(viewDelete);
        dialog2 = passbuilderDelete.create();
        /////////////////////////////////////


        //////////////Passsword Checker for Edit
        AlertDialog.Builder passbuilderEdit = new AlertDialog.Builder(this);
        passbuilderEdit.setTitle("Enter Password");

        View viewEdit = getLayoutInflater().inflate(R.layout.admin_password_popup, null);
        EditText adminPasswordETEdit = viewEdit.findViewById(R.id.adminPasswordET);
        Button checkPasswordBTNEdit = viewEdit.findViewById(R.id.checkPasswordBTN);

        checkPasswordBTNEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = adminPasswordETEdit.getText().toString();
                String pass = "razzaq123";
                if (temp.equals(pass)) {
                    Toast.makeText(activity_detail.this, "Editing...", Toast.LENGTH_SHORT).show();
                    dialog2.dismiss();
                    ////////////////////// Edit Code
                    Intent intent = new Intent(activity_detail.this, activity_update.class)
                            .putExtra("Name", detailName.getText().toString())
                            .putExtra("Code", detailCode.getText().toString())
                            .putExtra("Price", detailPrice.getText().toString())
                            .putExtra("Percentage", detailPerc.getText().toString())
                            .putExtra("Category", detailCateg.getText().toString())
                            .putExtra("Image", imageUrl)
                            .putExtra("Key", key);
                    startActivity(intent);
                    finish();
                    /////////////////////////////////

                } else {
                    Toast.makeText(activity_detail.this, "Incorrect Password! Try Again", Toast.LENGTH_SHORT).show();
                }
                adminPasswordETEdit.setText("");
            }
        });

        passbuilderEdit.setView(viewEdit);
        dialog3 = passbuilderEdit.create();
        /////////////////////////////////////

    }
}