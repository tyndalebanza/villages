package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BioActivity extends AppCompatActivity {

    private  String write_up ;
    private TextView input_text ;
    private  DelayedProgressDialog progressDialog ;
    private String url_bio ;
    private RequestQueue requestQueue;
    private SQLiteHandler db;
    private SessionManager session;
    private String userid;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);

        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);
        Button save_text = (Button) findViewById(R.id.btnSave);
        input_text = (TextView)findViewById(R.id.input_text);
        progressDialog = new DelayedProgressDialog();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SqLite database handler
        db = new SQLiteHandler(BioActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(BioActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        userid = user.get("uid");

        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish();

            }
        });

        save_text.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form



                if(input_text.getText().toString().length() == 0 ){


                } else {

                    updateText(userid, input_text.getText().toString());

                }
            }

        });

        url_bio = AppConfig.URL_QUERY_BIO + "?user_id=" + userid  ;
        requestQueue = Volley.newRequestQueue(BioActivity.this);
        fetchBio();
    }

    public void fetchBio( ) {
        //pDialog.setMessage("Please wait ...");
        //showDialog();
        // progressDialog.show(getActivity().getSupportFragmentManager(), "tag");

        progressDialog.show(BioActivity.this.getSupportFragmentManager(), "tag");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url_bio,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.d("Response: ", response.toString());
                        progressDialog.cancel();
                        try {
                            JSONArray categoryArray = response.getJSONArray("image");

                            for (int i = 0; i < categoryArray.length(); i++) {
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);

                                String var_bio = categoryItem.getString("bio");
                                if(var_bio.equals("empty")){

                                }else {
                                    input_text.setText(var_bio) ;
                                }

                               // username.setText(categoryItem.getString("fullname"));
                                //fullname.setText(categoryItem.getString("fullname"));

                            }


                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BioActivity.this);
                            builder.setTitle("Error Message");
                            builder.setMessage("Network Error Message. Restart the Application");
                            builder.setPositiveButton("OK", null);
                            //builder.setNegativeButton("Cancel", null);
                            builder.create().show();
                            progressDialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(BioActivity.this);
                        builder.setTitle("Network Error Message");
                        builder.setMessage("Network Error Message. Restart the Application");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();
                        progressDialog.cancel();


                    }
                });

// Access the RequestQueue through your singleton class.
        requestQueue.add(jsObjRequest);
    }

    public  void updateText( final String user_id , final String edited_text   )  {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Saving ....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_BIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // //Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        finish();
                        // btnFollow.setText("Follow ");
                        // follow_count = 0 ;

                    } else {
                        //progressDialog.cancel();
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(BioActivity.this);
                        builder.setTitle("Network Error Message");
                        builder.setMessage("Network Error , please try again");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(BioActivity.this);
                    builder.setTitle("Network Error Message");
                    builder.setMessage("Network Error , please try again");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                    hideDialog();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BioActivity.this);
                builder.setTitle("Network Error Message");
                builder.setMessage("Network Error , please try again");
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
                params.put("write_up", edited_text );
                params.put("user_id", user_id);

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