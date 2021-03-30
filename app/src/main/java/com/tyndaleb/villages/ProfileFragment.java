package com.tyndaleb.villages;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private SQLiteHandler db;
    private SessionManager session;
    private String userid;
    private String db_username ;
    private String url_image ;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private CircleImageView imageView;
    private ProgressDialog pDialog;
    private TextView username ;
    private TextView fullname ;
    private TextView followers ;
    private TextView following ;
    private String url_image_path ;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap_one;
    private Boolean change_image = false ;
    private File imgfile ;
    private Bitmap adjustedBitmap ;
    private TransferObserver observer ;
    private TransferObserver imgobserver ;
    private ImageView profileButton ;
    private ImageView logout ;
    private ImageView changeButton ;
    private  DelayedProgressDialog progressDialog ;
    private TextView termsView ;


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = (CircleImageView) rootView.findViewById(R.id.profile_photo);
        Button profileButton = (Button) rootView.findViewById(R.id.btnProfile);
        Button changeButton = (Button) rootView.findViewById(R.id.btnPassword);
        Button logout = (Button) rootView.findViewById(R.id.btnLogOut);
        username = (TextView) rootView.findViewById(R.id.username);

        profileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);

            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivityForResult(intent, 1);



            }
        });

        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                session.setLogin(false);
                db.deleteUsers();

                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();



            }


        });

        // SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        userid = user.get("uid");
        db_username = user.get("username");

        url_image = AppConfig.URL_QUERY_PROFILE + "?user_id=" + userid  ;
        requestQueue = Volley.newRequestQueue(getActivity());
        fetchImage();

        return   rootView;

    }


    public void fetchImage( ) {
        //pDialog.setMessage("Please wait ...");
        //showDialog();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url_image,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.d("Response: ", response.toString());
                        //  progressDialog.cancel();
                        try {
                            JSONArray categoryArray = response.getJSONArray("image");

                            for (int i = 0; i < categoryArray.length(); i++) {
                                JSONObject categoryItem = (JSONObject) categoryArray.get(i);
                                url_image_path = categoryItem.getString("profile_image");

                                username.setText(categoryItem.getString("fullname"));
                                //fullname.setText(categoryItem.getString("fullname"));

                            }

                            loadImage(url_image_path, imageView);



                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Error Message");
                            builder.setMessage("Network Error Message. Restart the Application");
                            builder.setPositiveButton("OK", null);
                            //builder.setNegativeButton("Cancel", null);
                            builder.create().show();
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
                        // progressDialog.cancel();


                    }
                });

// Access the RequestQueue through your singleton class.
        requestQueue.add(jsObjRequest);
    }



    private void loadImage( String url ,CircleImageView imageView ){

        imageLoader = CustomVolleyRequest.getInstance(getActivity().getApplicationContext())
                .getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(imageView,
                0, android.R.drawable
                        .ic_dialog_alert));
        imageView.setImageUrl(url, imageLoader);

    }

    @Override
    public void onResume(){
        super.onResume();
        //OnResume Fragment
        fetchImage();
    }

}
