package com.example.miantraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_notification_message extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView notificationListView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Notifications");

        // Initialize UI components
        notificationListView = findViewById(R.id.notificationListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Set the adapter to the ListView
        notificationListView.setAdapter(adapter);

        // Attach ValueEventListener to retrieve data from Firebase
        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the adapter before adding new data
                adapter.clear();

                // Iterate through the data and add it to the adapter
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String message = messageSnapshot.getValue(String.class);
                    adapter.add(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }
}