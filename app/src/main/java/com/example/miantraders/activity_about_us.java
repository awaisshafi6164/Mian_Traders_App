package com.example.miantraders;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nex3z.notificationbadge.NotificationBadge;

public class activity_about_us extends AppCompatActivity {

    NotificationBadge badge;
    Button incBTN;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        badge = findViewById(R.id.badge);
        incBTN = findViewById(R.id.incBTN);

        incBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badge.setNumber(counter++);
            }
        });

    }
}