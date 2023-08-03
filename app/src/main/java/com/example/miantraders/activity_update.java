package com.example.miantraders;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class activity_update extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton;
    EditText updateName, updateCode, updatePrice, updatePerc;

    String name, code, price, perc, categ, imageUrl, key, oldImageUrl;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    //AutoCompleteTextView update_auto_complete_txt;
    Spinner spinner;
    ArrayAdapter<String> adapterCategory;
    String categoryTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateImage = findViewById(R.id.updateImage);
        updateButton = findViewById(R.id.updateButton);
        updateName = findViewById(R.id.updateName);
        updateCode = findViewById(R.id.updateCode);
        updatePrice = findViewById(R.id.updatePrice);
        updatePerc = findViewById(R.id.updatePercentage);
        //update_auto_complete_txt = findViewById(R.id.auto_complete_txt);
        spinner = findViewById(R.id.spinner);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        }else{
                            Toast.makeText(activity_update.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        ///////Spinner code Below
        ArrayList<String> category = CategoryManager.getCategoryList(this);
        spinner = findViewById(R.id.spinner);
        adapterCategory = new ArrayAdapter<String>(this,R.layout.list_category,category);
        spinner.setAdapter(adapterCategory);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryTemp = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(activity_update.this, "Category: "+categoryTemp, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryTemp = null;
//                Toast.makeText(activity_update.this, "Category: "+categoryTemp, Toast.LENGTH_SHORT).show();
            }
        });
        ///////////////////////////

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Glide.with(activity_update.this).load(bundle.getString("Image")).into(updateImage);
            updateName.setText(bundle.getString("Name"));
            updateCode.setText(bundle.getString("Code"));
            updatePrice.setText(bundle.getString("Price"));
            updatePerc.setText(bundle.getString("Percentage"));

            String defaultCategory = bundle.getString("Category");
            int defaultCategoryPosition = category.indexOf(defaultCategory);
            if (defaultCategoryPosition != -1) {
                spinner.setSelection(defaultCategoryPosition);
                categoryTemp = defaultCategory;
            }

            key = bundle.getString("Key");
            oldImageUrl = bundle.getString("Image");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    saveData();
                } else {
                    updateDataWithoutImage();
                }
                makeNotification();
            }
        });

    }

    private void makeNotification() {
        String chanelID = "CHANNEL_ID_NOTIFICATION";
        String itemNumber = code;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), chanelID);
        notificationBuilder.setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Item # "+itemNumber+" has been updated")
                .setContentText("Kindly refresh the app before check item. Thanks")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelID);
            if(notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID, "Some Description", importance);
                notificationChannel.setLightColor(R.color.lavender);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_update.this);
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
                                imageUrl = urlImage.toString();
                                updateDate();
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

    public void updateDataWithoutImage() {
        name = updateName.getText().toString().trim();
        code = updateCode.getText().toString().trim();
        price = updatePrice.getText().toString().trim();
        perc = updatePerc.getText().toString().trim();
        String category = categoryTemp;

        DataClass dataClass = new DataClass(name, code, price, perc, category, oldImageUrl);
        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(activity_update.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity_update.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateDate(){
        name = updateName.getText().toString().trim();
        code = updateCode.getText().toString().trim();
        price = updatePrice.getText().toString().trim();
        perc = updatePerc.getText().toString().trim();
        String category = categoryTemp;

        DataClass dataClass = new DataClass(name, code, price, perc, category, imageUrl);
        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(activity_update.this, "Updated", Toast.LENGTH_SHORT).show();
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                    reference.delete();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity_update.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}