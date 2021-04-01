package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BioLinkActivity extends AppCompatActivity {
    private CircleImageView imageView;
    private TextView username ;
    private TextView textBio ;
    private SQLiteHandler db;
    private SessionManager session;
    private String userid;
    private String db_username ;
    private String url_image ;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private String url_image_path ;
    private String user_id ;

    private  DelayedProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_link);

        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);
        imageView = (CircleImageView) findViewById(R.id.profile_photo);
        username = (TextView) findViewById(R.id.username);
        textBio = (TextView) findViewById(R.id.bio);
        user_id = getIntent().getStringExtra("EXTRA_USER_ID");

        progressDialog = new DelayedProgressDialog();

        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish();

            }
        });


        url_image = AppConfig.URL_QUERY_PROFILE + "?user_id=" + user_id  ;
        requestQueue = Volley.newRequestQueue(BioLinkActivity.this);
        fetchImage();
    }

    public void fetchImage( ) {
        //pDialog.setMessage("Please wait ...");
        //showDialog();
        // progressDialog.show(getActivity().getSupportFragmentManager(), "tag");

        progressDialog.show(BioLinkActivity.this.getSupportFragmentManager(), "tag");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url_image,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.d("Response: ", response.toString());
                        progressDialog.cancel();
                        try {
                            JSONArray categoryArray = response.getJSONArray("image");

                            for (int i = 0; i < categoryArray.length(); i++) {
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);
                                url_image_path = categoryItem.getString("profile_image");
                                textBio.setText(categoryItem.getString("bio"));
                                username.setText(categoryItem.getString("fullname"));
                                //fullname.setText(categoryItem.getString("fullname"));

                            }

                            loadImage(url_image_path, imageView);



                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BioLinkActivity.this);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(BioLinkActivity.this);
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



    private void loadImage( String url ,CircleImageView imageView ){

        imageLoader = CustomVolleyRequest.getInstance(BioLinkActivity.this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(imageView,
                0, R.drawable
                        .loading));
        imageView.setImageUrl(url, imageLoader);

    }

}