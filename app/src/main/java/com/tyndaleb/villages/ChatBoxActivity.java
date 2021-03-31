package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatBoxActivity extends AppCompatActivity {
    private List<user_chat> myChats;
    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private SQLiteHandler db;
    private SessionManager session;
    private String userid;
    private String get_chat_url ;
    private RequestQueue requestQueue;
    private RequestQueue queue;
    private int thread_id ;
    private String profile_image ;
    private EditText chat_text ;
    private String fullname;

    private  DelayedProgressDialog progressDialog ;

    //The request counter to send ?page=1, ?page=2  requests
    private int requestCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        thread_id = getIntent().getIntExtra("extra_thread_id",0);

        recyclerView = (RecyclerView) findViewById(R.id.chat_thread_recycler_view);
        ImageView btnBack = (ImageView) findViewById(R.id.previous);
        ImageView btnSubmit = (ImageView) findViewById(R.id.submit);
        chat_text = (EditText) findViewById(R.id.editText);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ChatBoxActivity.this, LinearLayoutManager.VERTICAL));

        progressDialog = new DelayedProgressDialog();
        // progressDialog.show(getActivity().getSupportFragmentManager(), "tag");
        layoutManager = new LinearLayoutManager(ChatBoxActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        myChats = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(ChatBoxActivity.this);
        //Initializing our superheroes list

        adapter = new chatAdapter(myChats, ChatBoxActivity.this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);


        // SqLite database handler
        db = new SQLiteHandler(ChatBoxActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(ChatBoxActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        userid = user.get("uid");
        fullname = user.get("fullname");
        ////Log.d("USERID",userid);

        get_chat_url = AppConfig.URL_QUERY_CHAT_LIST + "?thread_id=" + thread_id +  "&page="  ;
        // get_home_page_url = AppConfig.URL_FRONT_PAGE_THREAD_LIST + "?user_id=" + userid + "&page=";

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if(chat_text.getText().toString().length() > 0 ) {

                   // user_chat user_chat = new user_chat(Integer.parseInt(userid), fullname, profile_image, song_id, chat_text.getText().toString(), 0);

                   // myChats.add(user_chat);
                   // adapter.notifyDataSetChanged();
                    String str_text = chat_text.getText().toString() ;
                    chat_text.setText("");
                    updateChat( thread_id ,str_text , userid );
                }

            }

        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                //mTimer = null ;
                finish() ;

            }

        });

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
        progressDialog.show(ChatBoxActivity.this.getSupportFragmentManager(), "tag");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (get_chat_url + String.valueOf(requestCount),null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.cancel();
                        // Log.d("Response  Number: ", response.toString());
                        try {
                            JSONArray categoryArray = response.getJSONArray("result");

                            for (int i = 0; i < categoryArray.length(); i++) {
                                //draft_card draftCard = new draft_card();
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);

                                int v_user_id = categoryItem.getInt("user_id");
                                String v_fullname = categoryItem.getString("fullname");
                                String v_profile_image = categoryItem.getString("profile_image");
                                int v_thread_id = categoryItem.getInt("thread_id");
                                String v_chat_text = categoryItem.getString("chat_text");
                                int v_created_at = categoryItem.getInt("created_at");

                                user_chat user_chat = new user_chat(v_user_id,v_fullname,v_profile_image,v_thread_id,v_chat_text,v_created_at);

                                myChats.add(user_chat);
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
    private void updateChat( final int thread_id , final String chat_text , final String user_id ) {
        progressDialog.show(ChatBoxActivity.this.getSupportFragmentManager(), "tag");
        // Tag used to cancel the request
        String tag_string_req = "req_new_item";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERT_CHAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // itemView.vote_button.setEnabled(false);
                        requestCount = 1;
                        myChats.removeAll(myChats);
                        adapter.notifyDataSetChanged();
                        getData();

                        //Start New Activity
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatBoxActivity.this);
                    builder.setTitle("Network Error Message");
                    builder.setMessage("Network Error , please try again");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                    progressDialog.cancel();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG, "Login Error: " + error.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatBoxActivity.this);
                builder.setTitle("Network Error Message");
                builder.setMessage("Network Error , please try again");
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();
                progressDialog.cancel();
                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {


                String str_thread_id = String.valueOf(thread_id) ;
                // String str_user_id = String.valueOf(user_id) ;

                Map<String, String> params = new HashMap<String, String>();
                params.put("thread_id", str_thread_id);
                params.put("chat_text", chat_text);
                params.put("user_id", user_id);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}