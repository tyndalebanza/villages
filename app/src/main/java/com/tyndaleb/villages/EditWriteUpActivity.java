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

public class EditWriteUpActivity extends AppCompatActivity {

    private String village_thread_id ;
    private  String write_up ;
    private TextView input_text ;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_write_up);
        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        Button save_text = (Button) findViewById(R.id.btnSave);
        input_text = (TextView)findViewById(R.id.input_text);
        village_thread_id = getIntent().getStringExtra("EXTRA_VILLAGE_THREAD_ID");
        write_up = getIntent().getStringExtra("EXTRA_WRITE_UP");

        input_text.setText(write_up);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnClose.setOnClickListener(new View.OnClickListener() {

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

                    updateText(village_thread_id, input_text.getText().toString());

                }
            }

        });
    }

    public  void updateText( final String village_thread_id , final String edited_text   )  {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Saving ....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_EDIT_TEXT_ENTRY, new Response.Listener<String>() {

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditWriteUpActivity.this);
                        builder.setTitle("Network Error Message");
                        builder.setMessage("Network Error , please try again");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditWriteUpActivity.this);
                    builder.setTitle("Network Error Message");
                    builder.setMessage("Network Error , please try again");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditWriteUpActivity.this);
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
                params.put("village_thread_id", village_thread_id);

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