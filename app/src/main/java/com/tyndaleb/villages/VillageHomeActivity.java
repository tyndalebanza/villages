package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class VillageHomeActivity extends AppCompatActivity {
    private TextView txt_village_name ;
    private  TextView txt_province_country_name ;
    private int village_id ;

    private SQLiteHandler db;
    private SessionManager session;

    private String user_id;
    private  String url_fetch_village ;
    private String village_name;

    //private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private File imgfile ;
    private RequestQueue requestQueue;

    private  DelayedProgressDialog progressDialog ;

    private NetworkImageView village_profile_photo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_home);
        txt_village_name = (TextView) findViewById(R.id.VillageName);
        txt_province_country_name = (TextView) findViewById(R.id.StateCountry);
        village_profile_photo = (NetworkImageView) findViewById(R.id.home_image);
        ImageView btnClose = (ImageView) findViewById(R.id.close);

        ImageView history_pen = (ImageView) findViewById(R.id.history_pen);
        ImageView chieftainship_pen = (ImageView) findViewById(R.id.chieftainship_pen);
        ImageView tribe_pen = (ImageView) findViewById(R.id.tribe_pen);
        ImageView language_pen = (ImageView) findViewById(R.id.language_pen);
        ImageView traditions_pen = (ImageView) findViewById(R.id.traditions_pen);
        ImageView cuisine_pen = (ImageView) findViewById(R.id.cuisine_pen);
        ImageView farming_pen = (ImageView) findViewById(R.id.farming_pen);
        ImageView architecture_pen = (ImageView) findViewById(R.id.architecture_pen);
        ImageView dressing_pen = (ImageView) findViewById(R.id.dressing_pen);
        ImageView outdoor_scenery_pen = (ImageView) findViewById(R.id.outdoor_scenery_pen);
        ImageView art_and_craft_pen = (ImageView) findViewById(R.id.art_and_craft_pen);
        ImageView wildlife_pen = (ImageView) findViewById(R.id.wildlife_pen);
        ImageView events_pen = (ImageView) findViewById(R.id.events_pen);


        village_id = getIntent().getIntExtra("EXTRA_VILLAGE_ID",0);

        progressDialog = new DelayedProgressDialog();

        db = new SQLiteHandler(VillageHomeActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(VillageHomeActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("uid");


        village_profile_photo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(VillageHomeActivity.this,
                        VillageProfilePhotoActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_VILLAGE_NAME", village_name);
                startActivity(intent);
            }


        });

        history_pen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageHomeActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "History");
                startActivity(intent);
                // finish() ;

            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form

                finish() ;

            }

        });

    }

    public void fetchVillage() {
        progressDialog.show(VillageHomeActivity.this.getSupportFragmentManager(), "tag");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url_fetch_village,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.d("Response: ", response.toString());
                        progressDialog.cancel();
                        try {
                            JSONArray categoryArray = response.getJSONArray("result");

                            for (int i = 0; i < categoryArray.length(); i++) {
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);


                                loadImage(categoryItem.getString("profile_image"), village_profile_photo);

                                txt_village_name.setText(categoryItem.getString("village_name"));
                                village_name = categoryItem.getString("village_name");
                                String string01 = categoryItem.getString("state")+",";
                                String string02 = categoryItem.getString("country") ;
                                txt_province_country_name.setText(string01 + string02);

                            }



                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(VillageHomeActivity.this);
                            builder.setTitle("Error Message");
                            builder.setMessage("Network Error Message. Restart the Application");
                            builder.setPositiveButton("OK", null);
                            //builder.setNegativeButton("Cancel", null);
                            builder.create().show();
                            progressDialog.cancel();
                            //progressDialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(VillageHomeActivity.this);
                        builder.setTitle("Network Error Message");
                        builder.setMessage("Network Error Message. Restart the Application");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();
                        progressDialog.cancel();
                        // progressDialog.cancel();


                    }
                });

// Access the RequestQueue through your singleton class.
        requestQueue.add(jsObjRequest);
    }

    private void loadImage(String url , NetworkImageView imageView ){

        imageLoader = CustomVolleyRequest.getInstance(VillageHomeActivity.this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(imageView,
                0, android.R.drawable
                        .ic_dialog_alert));
        imageView.setImageUrl(url, imageLoader);

    }

    @Override
    public void onResume(){
        super.onResume();
        //here...
        // SqLite database handler


        url_fetch_village = AppConfig.URL_VILLAGE_HOME + "?village_id=" + village_id  ;

        requestQueue = Volley.newRequestQueue(VillageHomeActivity.this);

        fetchVillage();

    }

}