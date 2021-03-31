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

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Button btnLogin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView btnRegister = (TextView) findViewById(R.id.btnRegister);
        TextView btnForgot = (TextView) findViewById(R.id.btnForgot);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnlogin);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserCount();

        String user_count = user.get("user_count");

        // Session manager
        session = new SessionManager(getApplicationContext());


        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);

                startActivity(intent);
                finish();


            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,
                        ForgotEmailActivity.class);

                startActivity(intent);
                finish();


            }
        });

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Login Error Message");
                    builder.setMessage("Please enter the Email address  and Password");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();


                }
            }

        });


        if (user_count != null && Integer.parseInt(user_count) == 1) {
            Intent intent = new Intent(LoginActivity.this,
                    FrontPageActivity.class);
            startActivity(intent);
            finish();

        } else {

            logoutUser();
        }

    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
               // Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        //  session.setLogin(true);

                        // Now store the user in SQLite


                        JSONObject user = jObj.getJSONObject("user");
                        //      String name = user.getString("name");
                        String uid = user.getString("uid");
                        String email = user.getString("email");
                        String username = user.getString("username");
                        String fullname = user.getString("fullname");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(email,uid,username,fullname,created_at);
                        session.setLogin(true);

                        // Launch main activity


                        Intent intent = new Intent(LoginActivity.this,
                                FrontPageActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Login Error Message");
                        builder.setMessage(errorMsg);
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();


                    }
                } catch (JSONException e) {
                    // JSON error

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Login Error Message");
                    builder.setMessage("Network Error Message. Restart the Application");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();


                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Login Error Message");
                builder.setMessage(error.getMessage());
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

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