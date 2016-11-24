package com.zulip.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.zulip.android.R;
import com.zulip.android.util.PhotoHelper;

import java.io.File;

public class PhotoSendActivity extends AppCompatActivity {

    private ImageView mImageView;
    private String mPhotoPath;
    private CropImageView mCropImageView;
    private boolean isCropFinished;
    private boolean mIsCropped;
    private String mIsCroppedKey = "photo cropped";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // run activity in full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo_send);

        // get the file path sent from ZulipActivity
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);

        mImageView = (ImageView) findViewById(R.id.photoImageView);
        ImageView sendPhoto = (ImageView) findViewById(R.id.send_photo);

        // intent to go back to ZulipActivity and upload photo
        final Intent sendIntent = new Intent(this, ZulipActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsCropped) {
                    // if image was cropped, delete old file
                    // and store new bitmap on that location
                    Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    PhotoHelper.saveBitmapAsFile(mPhotoPath, bitmap);
                }

                // add the file path of cropped image
                sendIntent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                startActivity(sendIntent);
            }
        });

        ImageView deleteBtn = (ImageView) findViewById(R.id.delete_photo);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(mPhotoPath);
                boolean isFileDeleted = file.delete();
                if (!isFileDeleted) {
                    Log.e("Photo upload", "Could not delete photo");
                }
                // go back to ZulipActivity to start camera intent
                startActivity(sendIntent);
            }
        });

        ImageView editPhotoBtn = (ImageView) findViewById(R.id.edit_photo);
        editPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsCropped) {
                    // if image was cropped, delete old file
                    // and store new bitmap on that location
                    Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    PhotoHelper.saveBitmapAsFile(mPhotoPath, bitmap);
                }

                // start PhotoEditActivity, passing it the file path for cropped photo
                Intent intent = new Intent(PhotoSendActivity.this, PhotoEditActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                intent.putExtra(mIsCroppedKey, mIsCropped);
                startActivity(intent);
            }
        });

        mCropImageView = (CropImageView) findViewById(R.id.crop_image_view);

        final ImageView cropBtn = (ImageView) findViewById(R.id.crop_btn);
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCropFinished) {
                    // if image is to be cropped, make CropImageView visible
                    Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    mCropImageView.setImageBitmap(bitmap);
                    mCropImageView.setVisibility(View.VISIBLE);

                    // tint the crop button blue during cropping
                    cropBtn.setColorFilter(ContextCompat.getColor(PhotoSendActivity.this,
                            R.color.holo_blue_dark));
                    isCropFinished = true;
                    mIsCropped = true;
                } else {
                    // set cropped image as source of ImageView
                    Bitmap croppedImage = mCropImageView.getCroppedImage();
                    mCropImageView.setVisibility(View.GONE);
                    mImageView.setImageBitmap(croppedImage);

                    // tint the crop button white when cropping is finished
                    cropBtn.setColorFilter(Color.WHITE);
                    isCropFinished = false;
                }
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // rotate bitmap if it was sent from camera intent
        // otherwise, don't rotate
        if (!mIsCropped) {
            PhotoHelper.setPic(mImageView, mPhotoPath, true);
        } else {
            PhotoHelper.setPic(mImageView, mPhotoPath, false);
        }
    }
}
