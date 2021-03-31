package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    private ImageView imageView ;
    private int PICK_IMAGE_REQUEST = 1;
    private Button btnSave ;
    private ImageView btnPrevious ;
    private Bitmap bitmap_one;
    private Bitmap adjustedBitmap ;
    private RequestQueue requestQueue;
    //private ProgressDialog pDialog;
    private Bitmap orientedBitmap ;
    private String db_username ;
    private File imgfile ;
    private SQLiteHandler db;
    private SessionManager session;
    private TransferObserver observer ;
    private String userid;
    private  DelayedProgressDialog progressDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        imageView = (ImageView) findViewById(R.id.profile_photo);
        btnSave = (Button)  findViewById(R.id.btnSave);
        btnPrevious = (ImageView) findViewById(R.id.btnBack);
        progressDialog = new DelayedProgressDialog();

        imageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                ImagePicker.Companion.with(UpdateProfileActivity.this)
                        // Crop Image(User can choose Aspect Ratio)
                        .cropSquare()
                        .compress(512)
                        // User can only select image from Gallery
                        // Image resolution will be less than 1080 x 1920
                        .start();

            }

        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                progressDialog.show(UpdateProfileActivity.this.getSupportFragmentManager(), "tag");
                if (bitmap_one != null) {

                    saveImage();
                } else {
                    progressDialog.cancel();
                }
            }

        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

            finish();

            }

        });

        // SqLite database handler
        db = new SQLiteHandler(UpdateProfileActivity.this.getApplicationContext());

        // session manager
        session = new SessionManager(UpdateProfileActivity.this.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        userid = user.get("uid");
        db_username = user.get("fullname");
        // db_username = db_username.substring(1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK ) {

            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap_one = MediaStore.Images.Media.getBitmap(UpdateProfileActivity.this.getApplicationContext().getContentResolver(), filePath);
                //Log.d("RESULT_OK","RESULT_OK");

                //adjustedBitmap = modifyOrientation(bitmap_one,filePath.getPath() );
                // adjustedBitmap = getResizedBitmap(bitmap_one, 200);
                // Bitmap bitmap_two = modifyOrientation(bitmap_one,filePath.getPath() );
                orientedBitmap = ExifUtil.rotateBitmap(filePath.getPath(), bitmap_one);

                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(orientedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }



        } else if (requestCode == ImagePicker.RESULT_ERROR) {
            //fetchImage();

        }
    }

    public void saveImage(){
        // progressDialog.show(UpdateProfileActivity.this.getSupportFragmentManager(), "tag");

        File dir = UpdateProfileActivity.this.getExternalFilesDir(null);

        OutputStream fOut = null;

        imgfile = new File(dir.getAbsolutePath(), db_username + "_" + System.currentTimeMillis() + ".png");

        // //Log.d("FOUT",dir.getAbsolutePath());
        try {
            fOut = new FileOutputStream(imgfile);


            orientedBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            // saving the Bitmap to a file compressed as a JPEG with 85% compression rate


        } catch (FileNotFoundException e) {

        }

        try {

            fOut.close(); // do not forget to close the stream

        } catch (IOException e) {

        }

        final String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                UpdateProfileActivity.this.getApplicationContext(),
                "ap-southeast-2:2afc3602-07ad-4a46-8a78-434e5371c65a", // Identity pool ID
                Regions.AP_SOUTHEAST_2 // Region
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder()
                .context(UpdateProfileActivity.this.getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(s3)
                .build();

        // TransferUtility transferUtility = new TransferUtility(s3, getActivity().getApplicationContext());

        observer = transferUtility.upload(
                "streamboximages" ,     /* The bucket to upload to */
                "profileimages/" + date +"/"+ imgfile.getName(),    /* The key for the uploaded object */
                imgfile        /* The file where the data to upload exists */
        );

        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                // do something

                if (state.COMPLETED.equals(observer.getState())) {


                    String S3_image_file = "https://dq8rhf3zp6dxc.cloudfront.net/profileimages/" + date + "/" + imgfile.getName();
                    uploadVideo( userid ,S3_image_file );


                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent,
                                          long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                //Display percentage transfered to user
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
            }


        });

    }



    private void uploadVideo( final String userid , final String image_url  ) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";



        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_UPLOAD_PROFILE_PHOTO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // //Log.d(TAG, "Register Response: " + response.toString());
                progressDialog.cancel();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {



                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
                        builder.setTitle("Error Message");
                        builder.setMessage("Network Error Message. Restart the Application");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();
                        progressDialog.cancel();

                    }
                } catch (JSONException e) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
                builder.setTitle("Error Message");
                builder.setMessage("Network Error Message. Restart the Application");
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();

                progressDialog.cancel();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userid);
                params.put("image_url", image_url);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



}