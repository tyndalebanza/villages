package com.tyndaleb.villages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class EditListActivity extends AppCompatActivity implements  MultiViewTypeAdapter.EditItemClickedListener{

    private String village_id ;
    private  String category ;

    private RequestQueue requestQueue;
    private RequestQueue queue;
    private  DelayedProgressDialog progressDialog ;

    //The request counter to send ?page=1, ?page=2  requests
    private int requestCount = 1;

    private ArrayList<village_item> myVillage;

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private androidx.recyclerview.widget.LinearLayoutManager layoutManager;
    private MultiViewTypeAdapter adapter ;
    private  String get_village_thread ;

    private SQLiteHandler db;
    private SessionManager session;

    private String user_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        ImageView backBtn = (ImageView) findViewById(R.id.btnBack);
        //ImageView edit_image = (ImageView)findViewById(R.id.edit_image);
        ImageView edit_text = (ImageView)findViewById(R.id.edit_text);
        TextView banner = (TextView) findViewById(R.id.category_name);

        village_id = getIntent().getStringExtra("EXTRA_VILLAGE_ID");
        category = getIntent().getStringExtra("EXTRA_CATEGORY");
        banner.setText(category);

        // SqLite database handler
        db = new SQLiteHandler(EditListActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(EditListActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("uid");



        backBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                finish() ;

            }
        });

        edit_text.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                Intent intent = new Intent(EditListActivity.this,
                        UnityEditActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", village_id);
                intent.putExtra("EXTRA_CATEGORY", category);
                startActivity(intent);
                // finish() ;

            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(EditListActivity.this, LinearLayoutManager.VERTICAL));

        progressDialog = new DelayedProgressDialog();
        // progressDialog.show(getActivity().getSupportFragmentManager(), "tag");
        layoutManager = new LinearLayoutManager(EditListActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        myVillage = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(EditListActivity.this);
        //Initializing our superheroes list

        adapter = new MultiViewTypeAdapter(myVillage, EditListActivity.this, this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);


        get_village_thread = AppConfig.URL_FETCH_VILLAGE_THREAD + "?village_id=" + village_id + "&category=" + category + "&user_id=" + user_id + "&page=";

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
        progressDialog.show(EditListActivity.this.getSupportFragmentManager(), "tag");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (get_village_thread + String.valueOf(requestCount),null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.cancel();
                        // Log.d("Response  Number: ", response.toString());
                        try {
                            JSONArray categoryArray = response.getJSONArray("result");

                            if(categoryArray.length() ==0){

                            }

                            for (int i = 0; i < categoryArray.length(); i++) {
                                //draft_card draftCard = new draft_card();
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);
                                int var_village_thread_id = categoryItem.getInt("village_thread_id");
                                int var_village_id = categoryItem.getInt("village_id");
                                int var_user_id = categoryItem.getInt("user_id");
                                int v_post_type = categoryItem.getInt("post_type");
                                String v_image = categoryItem.getString("image");
                                String v_write_up = categoryItem.getString("write_up");
                                String v_category = categoryItem.getString("category");
                                String v_username = categoryItem.getString("username");
                                String v_caption = categoryItem.getString("caption");
                                String v_profile_image = categoryItem.getString("profile_image");
                                int v_comments = categoryItem.getInt("comments");
                                int v_likes = categoryItem.getInt("likes");
                                int v_like_id = categoryItem.getInt("like_id");


                                village_item village_item = new village_item(var_village_thread_id,var_village_id,var_user_id,v_post_type,v_image,v_write_up,v_category,v_username,v_profile_image,v_caption,v_like_id,v_comments,v_likes);

                                myVillage.add(village_item);
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
                        progressDialog.cancel();

                    }
                });

        //adapter = new DraftAdapter(listDraftCards, this);


// Access the RequestQueue through your singleton class.
        return jsObjRequest;

    }

    @Override
    public void onRestart()
    {  // After a pause OR at startup
        super.onRestart();
        requestCount = 1;
        myVillage.removeAll(myVillage);
        // Log.d("THX",get_village_thread);
        getData();
    }

    public void onEditClicked(int village_thread_id, String write_up){

        Intent intent = new Intent(EditListActivity.this,
                TextEditActivity.class);
        intent.putExtra("EXTRA_VILLAGE_THREAD_ID", String.valueOf(village_thread_id));
        intent.putExtra("EXTRA_WRITE_UP", write_up);

        startActivity(intent);
    }

}