package com.tyndaleb.villages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;

public class VillageProfilePhotoActivity extends AppCompatActivity {

    private ImageView village_profile_photo ;
    private int GALLERY_IMAGE_REQ_CODE = 102 ;
    private Bitmap bitmap_one;
    private Bitmap orientedBitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_profile_photo);

        Button save_image = (Button) findViewById(R.id.btnSave);
        ImageView backBtn = (ImageView) findViewById(R.id.btnBack);
        village_profile_photo = (ImageView) findViewById(R.id.village_profile_photo);

        village_profile_photo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                ImagePicker.Companion.with(VillageProfilePhotoActivity.this)
                        // Crop Image(User can choose Aspect Ratio)
                        .crop(16f, 9f)
                        // User can only select image from Gallery
                         // Image resolution will be less than 1080 x 1920
                        .start();
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
                orientedBitmap = ExifUtil.rotateBitmap(filePath.getPath(), bitmap_one);

                //Setting the Bitmap to ImageView
                village_profile_photo.setImageBitmap(orientedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }



        } else if (requestCode == ImagePicker.RESULT_ERROR) {
            //fetchImage();

        }
    }
}