package com.tyndaleb.villages;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private androidx.recyclerview.widget.LinearLayoutManager layoutManager;
    private mainAdapter adapter;
    private SQLiteHandler db;
    private SessionManager session;
    private String userid;
    private String get_home_page_list_url ;
    private RequestQueue requestQueue;
    private RequestQueue queue;
    private TextView banner_message ;
    private String url_dashboard_message ;
    private String village_name ;
    private TextView banner ;
    private SearchView searchView;
    private int requestCount = 1;
    private  static String search_term;

    private List<village_dash> myVillages;

    private  DelayedProgressDialog progressDialog ;
    private TextView villagedrum;
    private  ImageView imgHome ;



    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        imgHome = (ImageView) rootView.findViewById(R.id.home);
        villagedrum = (TextView) rootView.findViewById(R.id.banner01);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Montserrat-Bold.otf");
        villagedrum.setTypeface(custom_font);
        villagedrum.setTypeface(custom_font);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        // progressDialog.show(getActivity().getSupportFragmentManager(), "tag");
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        myVillages = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        //Initializing our superheroes list

        progressDialog = new DelayedProgressDialog();

        adapter = new mainAdapter(myVillages, getActivity());

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                // Log.d("THREAD",String.valueOf(adapter.getVillageId(position)));
                Intent intent = new Intent(
                        getActivity(),
                        VillageActivity.class);
                intent.putExtra("EXTRA_VILLAGE_ID", adapter.getVillageId(position));
                startActivity(intent);
            }
        });

// SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        userid = user.get("uid");
        // db_username = user.get("fullname");
        ////Log.d("USERID",userid);
        get_home_page_list_url = AppConfig.URL_VILLAGE_HOME_LIST + "?user_id=" + userid + "&page=";
       // Log.d("SEARCHTERM",get_search_list_url);
       // getData();
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


        return   rootView;

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
        progressDialog.show(getActivity().getSupportFragmentManager(), "tag");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (get_home_page_list_url + String.valueOf(requestCount),null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.cancel();
                        // Log.d("Response  Number: ", response.toString());
                        try {
                            JSONArray categoryArray = response.getJSONArray("result");

                            if(categoryArray.length() > 0){

                                imgHome.setVisibility(View.INVISIBLE);
                            }

                            for (int i = 0; i < categoryArray.length(); i++) {
                                //draft_card draftCard = new draft_card();
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);

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
                        progressDialog.cancel();


                    }
                });

        //adapter = new DraftAdapter(listDraftCards, this);


// Access the RequestQueue through your singleton class.
        return jsObjRequest;

    }

    @Override
    public void onResume(){
        super.onResume();
        //OnResume Fragment
        requestCount = 1;
        myVillages.removeAll(myVillages);
        adapter.notifyDataSetChanged();
        // Log.d("THX",get_village_thread);
        getData();
    }


}
