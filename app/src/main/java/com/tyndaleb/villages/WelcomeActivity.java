package com.tyndaleb.villages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnStart = (Button) findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Launch login activity
                Intent intent = new Intent(
                        WelcomeActivity.this,
                        FrontPageActivity.class);
                startActivity(intent);
                finish();

            }

        });
    }
}