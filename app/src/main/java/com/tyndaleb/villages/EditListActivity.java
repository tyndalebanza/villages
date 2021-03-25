package com.tyndaleb.villages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EditListActivity extends AppCompatActivity {

    private String village_id ;
    private  String category ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        ImageView backBtn = (ImageView) findViewById(R.id.btnBack);
        ImageView edit_image = (ImageView)findViewById(R.id.edit_image);
        ImageView edit_text = (ImageView)findViewById(R.id.edit_text);
        TextView banner = (TextView) findViewById(R.id.category_name);

        village_id = getIntent().getStringExtra("EXTRA_VILLAGE_ID");
        category = getIntent().getStringExtra("EXTRA_CATEGORY");
        banner.setText(category);


        backBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish() ;

            }
        });

        edit_text.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(EditListActivity.this,
                        EditLineActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", village_id);
                intent.putExtra("EXTRA_CATEGORY", category);
                startActivity(intent);
                // finish() ;

            }
        });


    }
}