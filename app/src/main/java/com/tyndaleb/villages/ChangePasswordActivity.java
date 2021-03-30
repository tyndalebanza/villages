package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText inputPassword ;
    private EditText inputNewPassword_01 ;
    private EditText inputNewPassword_02 ;
    private Button btnChange ;
    private TextView btnLinkForgot ;
    private SessionManager session;
    private SQLiteHandler db;
    private TextView tv;
    private ImageView btnClose ;
    private  DelayedProgressDialog progressDialog ;
    private String user_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        inputPassword = (EditText) findViewById(R.id.input_password);
        inputNewPassword_01 = (EditText) findViewById(R.id.input_old_password_01);
        inputNewPassword_02 = (EditText) findViewById(R.id.input_old_password_02);

        btnChange = (Button) findViewById(R.id.reset);
        btnLinkForgot = (TextView) findViewById(R.id.login_forgot);
        btnClose = (ImageView) findViewById(R.id.previous);

       // inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
       // inputNewPassword_01.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
       // inputNewPassword_02.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        progressDialog = new DelayedProgressDialog();

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("uid");

        //Log.d("user_count",user_id);

        // Session manager
        session = new SessionManager(getApplicationContext());

        btnChange.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputNewPassword_01.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputNewPassword_02.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                String password = inputPassword.getText().toString().trim();
                String newpassword01 = inputNewPassword_01.getText().toString().trim();
                String newpassword02 = inputNewPassword_02.getText().toString().trim();

                // Check for empty data in the form
                if (!newpassword01.isEmpty() && !password.isEmpty() && !newpassword02.isEmpty()) {

                    if(newpassword01.equals(newpassword02)){
                        resetPassword(user_id,password,newpassword01);
                        //resetPassword(final String uid, final String password , final String newpassword)
                    }else {
                        // login user
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.MyAlertDialogStyle);
                        builder.setTitle("Login Error Message");
                        builder.setMessage("The new passwords are not the same , please re-enter");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.show();
                    }
                } else {
                    // Prompt user to enter credentials
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Login Error Message");
                    builder.setMessage("Please enter the missing Password");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.show();


                }
            }

        });

        btnLinkForgot.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ForgotEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();



            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                finish();

            }
        });

    }

    private void resetPassword(final String uid, final String password , final String newpassword) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //pDialog.setMessage("Logging in ...");
        //showDialog();
        progressDialog.show(getSupportFragmentManager(), "tag");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CHANGE_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // //Log.d(TAG, "Login Response: " + response.toString());
                // hideDialog();
                progressDialog.cancel();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        // String errorMsg = jObj.getString("error_msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                        builder.setTitle("Password Changed Successfully");
                        //builder.setMessage(errorMsg);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do something like...
                                ((Activity) ChangePasswordActivity.this).finish();;
                            }
                        });
                        //builder.setNegativeButton("Cancel", null);
                        builder.show();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                        builder.setTitle("Login Error Message");
                        builder.setMessage(errorMsg);
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();
                        progressDialog.cancel();

                    }
                } catch (JSONException e) {
                    // JSON error

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                    builder.setTitle("Login Error Message");
                    builder.setMessage( e.getMessage());
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                    progressDialog.cancel();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                builder.setTitle("Login Error Message");
                builder.setMessage(error.getMessage());
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();
                progressDialog.cancel();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("password", password);
                params.put("newpassword", newpassword);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}