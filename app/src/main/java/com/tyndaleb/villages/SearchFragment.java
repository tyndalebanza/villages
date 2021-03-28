package com.tyndaleb.villages;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private androidx.recyclerview.widget.LinearLayoutManager layoutManager;
    private mainAdapter adapter;
    private SQLiteHandler db;
    private SessionManager session;
    private String userid;
    private String url_fetch_trending ;
    private RequestQueue requestQueue;
    private RequestQueue queue;
    private TextView banner_message ;
    private String url_dashboard_message ;
    private String village_name ;
    private TextView banner ;
    private SearchView searchView;
    private int requestCount = 1;

    private List<village_dash> myVillages;

    private  DelayedProgressDialog progressDialog ;



    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.search_recyclerView);
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

        searchView=(SearchView) rootView.findViewById(R.id.searchView);
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setQueryHint("Search Village");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {


                if (( query.length() == 1)|| ( query.length() == 0)){
                    // Dialog Box to inform that there has to be at least  2  letters
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Error Message");
                    builder.setMessage("The search term must be at least 2 characters");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();

                } else {

                   Intent intent = new Intent(getActivity(),
                            VillageSearchActivity.class);
                    intent.putExtra("extra_user_search", query);
                    startActivity(intent);


                }

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


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
        url_fetch_trending = AppConfig.URL_TRENDING_VILLAGE  ;

        requestQueue = Volley.newRequestQueue(getActivity());

        fetchTrending();

        return   rootView;

    }

    public void fetchTrending() {
        progressDialog.show(getActivity().getSupportFragmentManager(), "tag");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url_fetch_trending,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.d("Response: ", response.toString());
                        progressDialog.cancel();
                        try {
                            JSONArray categoryArray = response.getJSONArray("result");

                            for (int i = 0; i < categoryArray.length(); i++) {
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
