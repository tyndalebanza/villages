package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotEmailActivity extends AppCompatActivity {

    private String email ;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_email);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        EditText inputEmail = (EditText) findViewById(R.id.email);
        Button btnPassword = (Button) findViewById(R.id.btnSend);

        btnPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                email = inputEmail.getText().toString().trim();


                // Check for empty data in the form
                if (!email.isEmpty() ) {
                    // login user
                    RequestPassword(email);
                } else {
                    // Prompt user to enter credentials
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotEmailActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Reset Password Error Message");
                    builder.setMessage("Please enter the Email address");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.show();



                }
            }

        });
    }

    private void RequestPassword(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_password";

        pDialog.setMessage("Retrieving Password ...");
        // showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REQUEST_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
              //  Log.d(TAG, "Login Response: " + response.toString());
                // hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                  //  Log.d("TAG2","Check Error") ;
                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session

                        // Launch main activity

                        Intent intent = new Intent(ForgotEmailActivity.this,
                                SendMessageActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotEmailActivity.this, R.style.MyAlertDialogStyle);
                        builder.setTitle("Reset Password Error Message");
                        builder.setMessage(errorMsg);
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.show();



                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                    // String errorMsg = jObj.getString("error_msg");
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotEmailActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Reset Password Error Message");
                    builder.setMessage("Network Error Message. Restart the Application");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.show();


                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // Log.e(TAG, " Error: " + error.getMessage());
                // String errorMsg = jObj.getString("error_msg");
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotEmailActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("Reset Password Error Message");
                builder.setMessage("Network Error Message. Restart the Application");
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.show();


                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);


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