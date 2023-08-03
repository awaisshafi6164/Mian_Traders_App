package com.example.miantraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    SearchView searchView;
    Spinner spinner;
    ArrayAdapter<String> adapterCategory;
    String spinnerCat;
    MyAdapter adapter;

    AlertDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission((MainActivity.this),
                android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();


        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new MyAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot itemSnapShot: snapshot.getChildren()){
                    DataClass dataClass = itemSnapShot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapShot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchList(s);
                return true;
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
                spinnerCat = adapterView.getItemAtPosition(i).toString();
                ArrayList<DataClass> spinnerList = new ArrayList<>();
                for(DataClass dataClass: dataList){
                    if(dataClass.getProductCategory().toLowerCase().contains(spinnerCat.toLowerCase())){
                        spinnerList.add(dataClass);
                    }
                }
                adapter.searchDataList(spinnerList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerCat = null;
            }
        });
        ////////////////////////

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.show();
//                Intent intent = new Intent(MainActivity.this,activity_upload.class);
//                startActivity(intent);
            }
        });

        //////////////Passsword Checker
        AlertDialog.Builder passbuilder = new AlertDialog.Builder(this);
        passbuilder.setTitle("Admin Password Please"); // Set the title first

        View view2 = getLayoutInflater().inflate(R.layout.admin_password_popup, null);
        EditText adminPasswordET = view2.findViewById(R.id.adminPasswordET);
        Button checkPasswordBTN = view2.findViewById(R.id.checkPasswordBTN);

        checkPasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = adminPasswordET.getText().toString();
                String pass = "razzaq123";
                if (temp.equals(pass)) {
                    dialog2.dismiss();
                    Toast.makeText(MainActivity.this, "Correct Password", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, activity_upload.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect Password! Try Again", Toast.LENGTH_SHORT).show();
                }
                adminPasswordET.setText("");
            }
        });

        passbuilder.setView(view2);
        dialog2 = passbuilder.create();
        /////////////////////////////////////

    }

    public void searchList(String text){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for(DataClass dataClass: dataList){
            if(dataClass.getProductName().toLowerCase().contains(text.toLowerCase()) && dataClass.getProductCategory().toLowerCase().contains(spinnerCat.toLowerCase()) ){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_main_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.notification:
//                Toast.makeText(this, "Notification Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.refresh:
                finish();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.make_pdf:
//                Toast.makeText(this, "Make PDF Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.share_app:
//                Toast.makeText(this, "Share App Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.about_us:
//                Toast.makeText(this, "About Us Selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}