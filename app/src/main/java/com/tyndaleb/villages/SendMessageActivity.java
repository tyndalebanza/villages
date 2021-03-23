package com.tyndaleb.villages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SendMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Button btnNext = (Button) findViewById(R.id.btnOK);

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                Intent newintent = new Intent(SendMessageActivity.this,
                        LoginActivity.class);

                startActivity(newintent);
                finish();
            }

        });
    }
}