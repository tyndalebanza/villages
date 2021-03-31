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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditLineActivity extends AppCompatActivity {

    private TextView input_text ;
    private String user_id ;
    private String village_id ;
    private  String category ;

    private ProgressDialog pDialog;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_line);

        Button save_text = (Button) findViewById(R.id.btnSave);
        input_text = (TextView) findViewById(R.id.input_text);
        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        village_id = getIntent().getStringExtra("EXTRA_VILLAGE_ID");
        category = getIntent().getStringExtra("EXTRA_CATEGORY");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(EditLineActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(EditLineActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("uid");


        save_text.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form


                if (input_text.getText().toString().length() == 0) {

                } else {
                    insertText(user_id, input_text.getText().toString(), "0", village_id, category, "empty", "empty");
                }
            }

        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish();

            }
        });

    }

        private void insertText(final String user_id, final String text_writeup, final String input_type,final String village_id,final String category,final String image_url ,String caption ) {
            // Tag used to cancel the request
            String tag_string_req = "req_register";

            pDialog.setMessage("Saving ....");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_VILLAGE_THREAD, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    // Log.d("TAG", "Register Response: " + response.toString());
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        if (!error) {

                            finish();
                        } else {

                            // Error occurred in registration. Get the error
                            // message
                            String errorMsg = jObj.getString("error_msg");

                            AlertDialog.Builder builder = new AlertDialog.Builder(EditLineActivity.this);
                            builder.setTitle("Error Message");
                            builder.setMessage(errorMsg);
                            builder.setPositiveButton("OK", null);
                            //builder.setNegativeButton("Cancel", null);
                            builder.create().show();

                        }
                    } catch (JSONException e) {

                        e.printStackTrace();

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditLineActivity.this);
                        builder.setTitle("SError Message");
                        builder.setMessage("Network Error . Check your Coverage");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditLineActivity.this);
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
                    params.put("user_id", user_id);
                    params.put("village_id", village_id);
                    params.put("post_type", input_type);
                    params.put("image", image_url);
                    params.put("write_up", text_writeup);
                    params.put("category", category);
                    params.put("caption", caption);


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
