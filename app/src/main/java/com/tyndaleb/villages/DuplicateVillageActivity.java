package com.tyndaleb.villages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DuplicateVillageActivity extends AppCompatActivity implements duplicateAdapter.EditListener{


    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private androidx.recyclerview.widget.LinearLayoutManager layoutManager;
    private duplicateAdapter adapter;
    private SQLiteHandler db;
    private SessionManager session;
    private String userid;
    private String get_duplicate_list_url ;
    private RequestQueue requestQueue;
    private RequestQueue queue;
    private TextView banner_message ;
    private String url_dashboard_message ;
    private String village_name ;
    private TextView banner ;

    private int requestCount = 1;

    private List<village_dash> myVillages;

    private  DelayedProgressDialog progressDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_village);

        ImageView btnClose = (ImageView) findViewById(R.id.close);
        Button btnNext = (Button) findViewById(R.id.btnContinue);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        village_name = getIntent().getStringExtra("EXTRA_VILLAGE_NAME");

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // progressDialog.show(getActivity().getSupportFragmentManager(), "tag");
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myVillages = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        //Initializing our superheroes list

        progressDialog = new DelayedProgressDialog();

        adapter = new duplicateAdapter(myVillages, this,this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

// SqLite database handler
        db = new SQLiteHandler(this.getApplicationContext());

        // session manager
        session = new SessionManager(this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        userid = user.get("uid");
        // db_username = user.get("fullname");
        ////Log.d("USERID",userid);


        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form

                finish() ;

            }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form

                Intent intent = new Intent(DuplicateVillageActivity.this,
                        ProvinceActivity.class);
                intent.putExtra("EXTRA_VILLAGE_NAME", village_name);
                startActivity(intent);
                finish() ;
            }

        });

        get_duplicate_list_url = AppConfig.URL_DUPLICATE_VILLAGE + "?village=" + village_name + "&page=";

        getData();
        //Calling method to get data
        recyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //Ifscrolled at last then
                if (isLastItemDisplaying(recyclerView)) {
                    //Calling the method getdata again
                    getData();
                }
            }
        });

    }

    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(requestCount));
        //Incrementing the request counter
        requestCount++;
    }




    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    //Overriden method to detect scrolling
    //@Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //Ifscrolled at last then
        if (isLastItemDisplaying(recyclerView)) {
            //Calling the method getdata again
            getData();
        }
    }

    private JsonObjectRequest getDataFromServer(int requestCount) {


        progressDialog.show(this.getSupportFragmentManager(), "tag");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (get_duplicate_list_url  + String.valueOf(requestCount),null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.cancel();
                        // Log.d("Response  Number: ", response.toString());
                        try {
                            // JSONArray categoryArray = response.getJSONArray();

                            for (int i = 0; i < response.length(); i++) {
                                //draft_card draftCard = new draft_card();
                                JSONObject categoryItem = (JSONObject) response.getJSONObject(String.valueOf(i));

                                int v_village_id = categoryItem.getInt("village_id");
                                String v_village_name = categoryItem.getString("village_name");
                                String v_country = categoryItem.getString("country");
                                String v_state = categoryItem.getString("state");
                                String v_image_url = categoryItem.getString("image");

                                village_dash village_dash = new village_dash(v_village_name , v_country ,v_state , v_village_id,v_image_url);

                                myVillages.add(village_dash);
                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.cancel();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("VOLLEY",error.getMessage());
                        progressDialog.cancel();


                    }
                });

        //adapter = new DraftAdapter(listDraftCards, this);


// Access the RequestQueue through your singleton class.
        return jsObjRequest;

    }
    public void onItemClicked(int village_id){

      /*  Intent intent = new Intent(
                getActivity(),
                VillageHomeActivity.class);
        intent.putExtra("EXTRA_VILLAGE_ID", village_id);
        startActivity(intent);
        finish() */

    }


}