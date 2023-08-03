package com.example.miantraders;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;

public class activity_upload extends AppCompatActivity {

    ImageView uploadImage;
    Button saveButton;
    EditText productNameET, productCodeET, productPriceET, productPercentageET;

    String categoryTemp;
    String imageURL;
    Uri uri;

//    AutoCompleteTextView auto_complete_txt;
    Spinner spinner;

    ArrayAdapter<String> adapterCategory;

    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadImage = findViewById(R.id.uploadImage);
        productNameET = findViewById(R.id.uploadName);
        productCodeET = findViewById(R.id.uploadCode);
        productPriceET = findViewById(R.id.uploadPrice);
        productPercentageET = findViewById(R.id.uploadPercentage);
        saveButton = findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else{
                            Toast.makeText(activity_upload.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        ////////////Spinner Code Below
        ArrayList<String> spinnerCategory = CategoryManager.getCategoryList(this);
        spinner = findViewById(R.id.spinner);
        adapterCategory = new ArrayAdapter<String>(this,R.layout.list_category,spinnerCategory);
        spinner.setAdapter(adapterCategory);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryTemp = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(activity_upload.this, "Category: "+categoryTemp, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryTemp = null;
//                Toast.makeText(activity_upload.this, "Category: "+categoryTemp, Toast.LENGTH_SHORT).show();
            }
        });
        ////////////////////////


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Category");

        View view = getLayoutInflater().inflate(R.layout.add_category_popup,null);
        EditText newCategoryET = view.findViewById(R.id.newCategoryET);
        Button saveCategoryBTN = view.findViewById(R.id.saveCategoryBTN);

        saveCategoryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = newCategoryET.getText().toString();
                CategoryManager.addCategory(activity_upload.this, temp);
                Toast.makeText(activity_upload.this, temp, Toast.LENGTH_SHORT).show();
                adapterCategory.notifyDataSetChanged();
                newCategoryET.setText("");
            }
        });

        builder.setView(view);
        dialog = builder.create();

    }


    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_upload.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Use Glide to load the selected image and compress it
        Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Compress the bitmap and upload it to Firebase Storage
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Adjust the compression quality as needed
                        byte[] data = baos.toByteArray();

                        // Upload the compressed image
                        UploadTask uploadTask = storageReference.putBytes(data);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isComplete()) ;
                                Uri urlImage = uriTask.getResult();
                                imageURL = urlImage.toString();
                                uploadData();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }


    public void uploadData(){
        String name = productNameET.getText().toString();
        String code = productCodeET.getText().toString();
        String price = productPriceET.getText().toString();
        String percentage = productPercentageET.getText().toString();
        String category = categoryTemp;

        DataClass dataClass;
        if(percentage.isEmpty()){
            dataClass = new DataClass(name,code,price,"0",category,imageURL);
        }else{
            dataClass = new DataClass(name,code,price,percentage,category,imageURL);
        }
        //DataClass dataClass = new DataClass(name,code,price,percentage,category,imageURL);

        //Updating Data
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("Android Tutorials").child(currentDate)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(activity_upload.this, "Saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity_upload.this, activity_upload.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity_upload.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_upload_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_category:
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}