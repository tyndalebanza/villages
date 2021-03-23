package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ProvinceActivity extends AppCompatActivity {

    private EditText province;
    private String country_name;
    private String village_name ;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SessionManager session;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);
        province =(EditText)findViewById(R.id.province);
        ImageView btnNext = (ImageView) findViewById(R.id.next);
        ImageView btnCancel = (ImageView) findViewById(R.id.close);
        TextView village_banner = (TextView) findViewById(R.id.VillageName);

        village_name = getIntent().getStringExtra("EXTRA_VILLAGE_NAME");

        village_banner.setText( village_name);

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish() ;

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form



                if(province.getText().toString().length() < 2 ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProvinceActivity.this);
                    builder.setTitle("Error Message");
                    builder.setMessage("State/Province is missing");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();

                } else {
                    Intent intent = new Intent(ProvinceActivity.this,
                            CountryActivity.class);
                    intent.putExtra("EXTRA_VILLAGE_NAME", village_name);
                    intent.putExtra("EXTRA_PROVINCE_NAME", province.getText().toString());
                    startActivity(intent);
                    finish() ;
                }
            }

        });



    }
}