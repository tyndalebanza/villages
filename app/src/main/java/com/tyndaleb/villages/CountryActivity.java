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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CountryActivity extends AppCompatActivity {

    private EditText country;
    private String province_name;
    private String village_name ;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SessionManager session;
    private String user_id;

    private  DelayedProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        country =(EditText)findViewById(R.id.country);
        ImageView btnNext = (ImageView) findViewById(R.id.next);
        ImageView btnCancel = (ImageView) findViewById(R.id.close);
        TextView village_banner = (TextView) findViewById(R.id.villagename);

        village_name = getIntent().getStringExtra("EXTRA_VILLAGE_NAME");
        province_name = getIntent().getStringExtra("EXTRA_PROVINCE_NAME");
        village_banner.setText(village_name);
        progressDialog = new DelayedProgressDialog();

        // SqLite database handler
        db = new SQLiteHandler(CountryActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(CountryActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("uid");
        ////Log.d("USERID",userid);

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish() ;

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form



                if(country.getText().toString().length() < 2 ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CountryActivity.this);
                    builder.setTitle("Error Message");
                    builder.setMessage("State/Province is missing");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();

                } else {
                    createVillage(user_id,village_name, country.getText().toString(),province_name );
                }
            }

        });

    }

    private void createVillage(final String user_id,  final String village_name , final String country_name , final String province  ) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        progressDialog.show(CountryActivity.this.getSupportFragmentManager(), "tag");
        // pDialog.setMessage("Creating ....");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_NEW_VILLAGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Log.d("TAG", "Register Response: " + response.toString());
                // hideDialog();
                progressDialog.cancel();
                try {
                    JSONObject jObj = new JSONObject(response);

                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        // String uid = jObj.getString("uid");

                        JSONObject show = jObj.getJSONObject("result");
                        //      String name = user.getString("name");
                        int var_village_id = show.getInt("village_id");
                        // Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                CountryActivity.this,
                                VillageHomeActivity.class);
                        intent.putExtra("EXTRA_VILLAGE_NAME", village_name);
                        intent.putExtra("EXTRA_COUNTRY_NAME", country_name);
                        intent.putExtra("EXTRA_PROVINCE_NAME", province);
                        intent.putExtra("EXTRA_VILLAGE_ID", var_village_id);
                        startActivity(intent);
                        finish();

                } catch (JSONException e) {

                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(CountryActivity.this);
                    builder.setTitle("Error Message");
                    builder.setMessage("Network Error . Check your Coverage");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                    progressDialog.cancel();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CountryActivity.this);
                builder.setTitle("Error Message");
                builder.setMessage(error.getMessage());
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();
                progressDialog.cancel();

                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("village_name", village_name);
                params.put("country", country_name);
                params.put("state", province);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}