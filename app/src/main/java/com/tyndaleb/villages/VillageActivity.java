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

public class VillageActivity extends AppCompatActivity {

    private TextView txt_village_name ;
    private  TextView txt_province_country_name ;
    private int village_id ;

    private SQLiteHandler db;
    private SessionManager session;

    private String user_id;
    private  String url_fetch_village ;
    private String village_name;
    private String url_fetch_led ;

    //private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private File imgfile ;
    private RequestQueue requestQueue;

    private  DelayedProgressDialog progressDialog ;

    private NetworkImageView village_profile_photo ;

    private ImageView history_led ;
    private ImageView chieftainship_led;
    private ImageView tribe_led ;
    private ImageView language_led ;
    private ImageView traditions_led;
    private ImageView cuisine_led ;
    private ImageView farming_led ;
    private ImageView architecture_led;
    private ImageView dressing_led ;
    private ImageView outdoor_scenery_led ;
    private ImageView art_and_craft_led ;
    private ImageView wildlife_led ;
    private ImageView events_led ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village);

        txt_village_name = (TextView) findViewById(R.id.VillageName);
        txt_province_country_name = (TextView) findViewById(R.id.StateCountry);
        village_profile_photo = (NetworkImageView) findViewById(R.id.home_image);
        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);

        TextView history = (TextView) findViewById(R.id.history);
        TextView chieftainship = (TextView) findViewById(R.id.chieftainship);
        TextView tribe = (TextView) findViewById(R.id.tribe);
        TextView language = (TextView) findViewById(R.id.language);
        TextView traditions = (TextView) findViewById(R.id.traditions);
        TextView cuisine = (TextView) findViewById(R.id.cuisine);
        TextView farming = (TextView) findViewById(R.id.farming);
        TextView architecture = (TextView) findViewById(R.id.architecture);
        TextView dressing = (TextView) findViewById(R.id.dressing);
        TextView outdoor_scenery = (TextView) findViewById(R.id.outdoor_scenery);
        TextView art_and_craft = (TextView) findViewById(R.id.art_and_craft);
        TextView wildlife = (TextView) findViewById(R.id.wildlife);
        TextView events = (TextView) findViewById(R.id.events);

        history_led = (ImageView) findViewById(R.id.history_led);
        chieftainship_led = (ImageView) findViewById(R.id.chieftainship_led);
        tribe_led = (ImageView) findViewById(R.id.tribe_led);
        language_led = (ImageView) findViewById(R.id.language_led);
        traditions_led = (ImageView) findViewById(R.id.traditions_led);
        cuisine_led = (ImageView) findViewById(R.id.cuisine_led);
        farming_led = (ImageView) findViewById(R.id.farming_led);
        architecture_led = (ImageView) findViewById(R.id.architecture_led);
        dressing_led = (ImageView) findViewById(R.id.dressing_led);
        outdoor_scenery_led = (ImageView) findViewById(R.id.outdoor_scenery_led);
        art_and_craft_led = (ImageView) findViewById(R.id.art_and_craft_led);
        wildlife_led = (ImageView) findViewById(R.id.wildlife_led);
        events_led = (ImageView) findViewById(R.id.events_led);


        village_id = getIntent().getIntExtra("EXTRA_VILLAGE_ID",0);

        progressDialog = new DelayedProgressDialog();

        db = new SQLiteHandler(VillageActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(VillageActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("uid");

        history.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "History");
                startActivity(intent);
                // finish() ;

            }
        });

        chieftainship.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Chieftainship");
                startActivity(intent);
                // finish() ;

            }
        });

        tribe.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Tribe");
                startActivity(intent);
                // finish() ;

            }
        });

        language.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Language");
                startActivity(intent);
                // finish() ;

            }
        });

        traditions.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Traditions");
                startActivity(intent);
                // finish() ;

            }
        });

        cuisine.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Cuisine");
                startActivity(intent);
                // finish() ;

            }
        });

        farming.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Farming");
                startActivity(intent);
                // finish() ;

            }
        });

        architecture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Architecture");
                startActivity(intent);
                // finish() ;

            }
        });

        dressing.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Dressing");
                startActivity(intent);
                // finish() ;

            }
        });

        outdoor_scenery.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Outdoor_Scenery");
                startActivity(intent);
                // finish() ;

            }
        });

        art_and_craft.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Art_and_Craft");
                startActivity(intent);
                // finish() ;

            }
        });

        wildlife.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Wildlife");
                startActivity(intent);
                // finish() ;

            }
        });

        events.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(VillageActivity.this,
                        EditListActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", String.valueOf(village_id));
                intent.putExtra("EXTRA_CATEGORY", "Events");
                startActivity(intent);
                // finish() ;

            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form

                finish() ;

            }

        });


    }

    public void fetchVillage() {
        progressDialog.show(VillageActivity.this.getSupportFragmentManager(), "tag");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url_fetch_village,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.d("Response: ", response.toString());
                        // progressDialog.cancel();
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

                                fetchViewLED();

                            }



                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(VillageActivity.this);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(VillageActivity.this);
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

        imageLoader = CustomVolleyRequest.getInstance(VillageActivity.this.getApplicationContext())
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

        url_fetch_led = AppConfig.URL_VILLAGE_LED + "?village_id=" + village_id  ;

        requestQueue = Volley.newRequestQueue(VillageActivity.this);

        fetchVillage();

    }

    public void fetchViewLED() {

        // progressDialog.show(VillageHomeActivity.this.getSupportFragmentManager(), "tag02");
        //pDialog.setMessage("Please wait ...");
        //showDialog();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url_fetch_led,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d("Response: ", response.toString());
                        progressDialog.cancel();
                        try {
                            JSONArray categoryArray = response.getJSONArray("result");

                            for (int i = 0; i < categoryArray.length(); i++) {
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);

                                if(categoryItem.getInt("history_led") > 0) {
                                    history_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("chieftainship_led") > 0) {
                                    chieftainship_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("tribe_led") > 0) {
                                    tribe_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("language_led") > 0) {
                                    language_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("traditions_led") > 0) {
                                    traditions_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("cuisine_led") > 0) {
                                    cuisine_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("farming_led") > 0) {
                                    farming_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("architecture_led") > 0) {
                                    architecture_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("dressing_led") > 0) {
                                    dressing_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("outdoor_scenery_led") > 0) {
                                    outdoor_scenery_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("art_and_craft_led") > 0) {
                                    art_and_craft_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("wildlife_led") > 0) {
                                    wildlife_led.setImageResource(R.drawable.green_led) ;
                                };
                                if(categoryItem.getInt("events_led") > 0) {
                                    events_led.setImageResource(R.drawable.green_led) ;
                                };


                            }



                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(VillageActivity.this);
                            builder.setTitle("Error Message");
                            builder.setMessage("Network Error Message. Restart the Application");
                            builder.setPositiveButton("OK", null);
                            //builder.setNegativeButton("Cancel", null);
                            builder.create().show();
                            progressDialog.cancel();
                            // Log.d("TRACE",e.toString());
                            //progressDialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(VillageActivity.this);
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



}