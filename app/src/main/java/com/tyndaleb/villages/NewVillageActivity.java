package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewVillageActivity extends AppCompatActivity {

    private EditText village_name;
    private ProgressDialog pDialog;

    private  DelayedProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_village);

        village_name =(EditText)findViewById(R.id.villagename);
        ImageView btnNext = (ImageView) findViewById(R.id.next);
        ImageView btnCancel = (ImageView) findViewById(R.id.close);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish() ;

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form



                if(village_name.getText().toString().length() < 2 ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewVillageActivity.this);
                    builder.setTitle("Error Message");
                    builder.setMessage("Village Name is missing");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK",null);
                    builder.create().show();

                } else {
                    queryDuplicate(village_name.getText().toString());
                }
            }

        });


    }

    private void queryDuplicate(final String villagename ) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Please Wait ....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_QUERY_DUPLICATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONObject v_count = jObj.getJSONObject("dupcount");
                    int village_count = v_count.getInt("count");
                    if ( village_count == 0 ){

                         Intent intent = new Intent(NewVillageActivity.this,
                                ProvinceActivity.class);
                         intent.putExtra("EXTRA_VILLAGE_NAME", village_name.getText().toString());
                         startActivity(intent);
                         finish() ;

                    }else {
                        Intent intent = new Intent(NewVillageActivity.this,
                                DuplicateVillageActivity.class);
                        intent.putExtra("EXTRA_VILLAGE_NAME", village_name.getText().toString());
                        startActivity(intent);
                        finish() ;
                        hideDialog();
                    }



                } catch (JSONException e) {

                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(NewVillageActivity.this);
                    builder.setTitle("Error Message");
                    builder.setMessage("Network Error . Check your Coverage");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                    hideDialog();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NewVillageActivity.this);
                builder.setTitle("Error Message");
                builder.setMessage(error.getMessage());
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("village", villagename);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}