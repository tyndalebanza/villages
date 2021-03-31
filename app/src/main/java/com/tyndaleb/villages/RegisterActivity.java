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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnLinkLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputName;
    private EditText inputUsername ;
    private EditText inputPassword;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Button btnLogin;
    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView btnLogin = (TextView) findViewById(R.id.btnLogin);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputFullName = (EditText) findViewById(R.id.fullname);
        //inputUsername = (EditText) findViewById(R.id.input_username);
        //inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //inputEmail.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS );
        //inputName = (EditText) findViewById(R.id.input_name);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterActivity.this,
                        LoginActivity.class);

                startActivity(intent);
                finish();


            }
        });

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        // if (session.isLoggedIn()) {
        logoutUser();
        //}

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String email = inputEmail.getText().toString().trim();
                // String name = inputName.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // String username = inputUsername.getText().toString().trim();

                String fullname =  inputFullName.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()  && !fullname.isEmpty() ) {

                    registerUser(email, password, fullname);


                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Registration Error Message");
                    builder.setMessage("Please enter e-mail address , username and password");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();

                }
            }
        });


    }

    private void registerUser(final String email,
                              final String password ,
                              final String fullname
    ) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
              //  Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        // String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        //      String name = user.getString("name");
                        String uid = user.getString("uid");
                        String email = user.getString("email");
                        String username = "null";
                        String fullname = user.getString("fullname");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(email,uid,username,fullname,created_at);
                        session.setLogin(true);

                        // Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle("Registration Error Message");
                        builder.setMessage(errorMsg);
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();

                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Registration Error Message");
                    builder.setMessage("Network Error . Check your Coverage");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Registration Error Message");
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
                params.put("email", email);
                params.put("password", password);
                params.put("fullname", fullname);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
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
