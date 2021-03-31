package com.tyndaleb.villages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class VillageProfilePhotoActivity extends AppCompatActivity {

    private ImageView village_profile_photo ;
    private int GALLERY_IMAGE_REQ_CODE = 102 ;
    private Bitmap bitmap_one;
    private Bitmap orientedBitmap ;
    private String village_id ;
    private String village_name ;

    private ProgressDialog pDialog;
    private TransferObserver observer ;

    private File imgfile ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_profile_photo);

        Button save_image = (Button) findViewById(R.id.btnSave);
        ImageView backBtn = (ImageView) findViewById(R.id.btnBack);
        village_profile_photo = (ImageView) findViewById(R.id.village_profile_photo);

        village_id = getIntent().getStringExtra("EXTRA_VILLAGE_ID");
        village_name = getIntent().getStringExtra("EXTRA_VILLAGE_NAME");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        village_profile_photo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                ImagePicker.Companion.with(VillageProfilePhotoActivity.this)
                        // Crop Image(User can choose Aspect Ratio)
                        .crop(16f, 9f)
                        .compress(512)
                        // User can only select image from Gallery
                         // Image resolution will be less than 1080 x 1920
                        .start();
               }




        });

        save_image.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                pDialog.setMessage("Saving ....");
                showDialog();

                if (bitmap_one != null) {

                    saveImage();
                } else {
                    hideDialog();
                }
            }


        });

        backBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form

                finish() ;

            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK ) {

            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap_one = MediaStore.Images.Media.getBitmap(VillageProfilePhotoActivity.this.getApplicationContext().getContentResolver(), filePath);
                //Log.d("RESULT_OK","RESULT_OK");

                //adjustedBitmap = modifyOrientation(bitmap_one,filePath.getPath() );
                // adjustedBitmap = getResizedBitmap(bitmap_one, 200);
                // Bitmap bitmap_two = modifyOrientation(bitmap_one,filePath.getPath() );
                // orientedBitmap = ExifUtil.rotateBitmap(filePath.getPath(), bitmap_one);

                //Setting the Bitmap to ImageView
                village_profile_photo.setImageBitmap(bitmap_one);
            } catch (IOException e) {
                e.printStackTrace();
            }



        } else if (requestCode == ImagePicker.RESULT_ERROR) {
            //fetchImage();

        }
    }

    public void saveImage(){
        //pDialog.setMessage("Saving ....");
        //showDialog();

        File dir = VillageProfilePhotoActivity.this.getExternalFilesDir(null);

        OutputStream fOut = null;

        imgfile = new File(dir.getAbsolutePath(), village_name + "_" + System.currentTimeMillis() + ".png");

        // //Log.d("FOUT",dir.getAbsolutePath());
        try {
            fOut = new FileOutputStream(imgfile);


            bitmap_one.compress(Bitmap.CompressFormat.PNG, 50, fOut);
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
                VillageProfilePhotoActivity.this.getApplicationContext(),
                "ap-southeast-2:2afc3602-07ad-4a46-8a78-434e5371c65a", // Identity pool ID
                Regions.AP_SOUTHEAST_2 // Region
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder()
                .context(VillageProfilePhotoActivity.this.getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(s3)
                .build();

        // TransferUtility transferUtility = new TransferUtility(s3, getActivity().getApplicationContext());

        observer = transferUtility.upload(
                "streamboximages" ,     /* The bucket to upload to */
                "village/" + date +"/"+ imgfile.getName(),    /* The key for the uploaded object */
                imgfile        /* The file where the data to upload exists */
        );

        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                // do something

                if (state.COMPLETED.equals(observer.getState())) {


                    String S3_image_file = "https://dq8rhf3zp6dxc.cloudfront.net/village/" + date + "/" + imgfile.getName();
                    // uploadVideo( userid ,S3_image_file );
                    insertProfileImage(village_id, S3_image_file);


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

    private void insertProfileImage(final String village_id,final String image_url ) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";



        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_VILLAGE_PROFILE_IMAGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Log.d("TAG", "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");

                        AlertDialog.Builder builder = new AlertDialog.Builder(VillageProfilePhotoActivity.this);
                        builder.setTitle("Error Message");
                        builder.setMessage(errorMsg);
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();
                        hideDialog();

                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(VillageProfilePhotoActivity.this);
                    builder.setTitle("Error Message");
                    builder.setMessage("Network Error . Check your Coverage");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                    hideDialog();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(VillageProfilePhotoActivity.this);
                builder.setTitle("Show Creation Error Message");
                builder.setMessage(error.getMessage());
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                params.put("village_id", village_id);
                params.put("image", image_url);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}