package com.zulip.android.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
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
    private boolean isCropped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_photo_send);

        // TODO: move var declarations to top
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);

        // remove "file:" from file path
        mPhotoPath = mPhotoPath.replace("file:", "");
        mImageView = (ImageView) findViewById(R.id.photoImageView);

        ImageView sendPhoto = (ImageView) findViewById(R.id.send_photo);

        // intent to go back to ZulipActivity
        final Intent sendIntent = new Intent(this, ZulipActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                // TODO: declare public tag for intent

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
                    // TODO: see if Zlog is to be used here?
                    Log.e("Photo upload", "Could delete photo");
                }

                // TODO: go back to camera activity

                startActivity(sendIntent);
            }
        });

        ImageView editPhotoBtn = (ImageView) findViewById(R.id.edit_photo);
        editPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCropped) {
                    PhotoHelper.saveBitmapAsFile(mPhotoPath, mImageView);
                }

                Intent intent = new Intent(PhotoSendActivity.this, PhotoEditActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                intent.putExtra("myBoolean", isCropped);
                startActivity(intent);
            }
        });

        mCropImageView = (CropImageView) findViewById(R.id.crop_image_view);

        ImageView cropBtn = (ImageView) findViewById(R.id.crop_btn);
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCropFinished) {
                    Bitmap bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                    mCropImageView.setImageBitmap(bitmap);
                    mCropImageView.setVisibility(View.VISIBLE);
                    isCropFinished = true;
                    isCropped = true;
                } else {
                    Bitmap croppedImage = mCropImageView.getCroppedImage();
                    mCropImageView.setVisibility(View.GONE);
                    mImageView.setImageBitmap(croppedImage);
                    isCropFinished = false;
                }
            }
        });

    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View decorView = getWindow().getDecorView();
        // make application's content appear behind the status bar
//        decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // Hide the status bar on Android 4.1 and Higher
        int uiOptionsStatusBar = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptionsStatusBar);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (!isCropped) {
            PhotoHelper.setPicWithRotation(mImageView, mPhotoPath);
        } else {
            PhotoHelper.setPicWithoutRotation(mImageView, mPhotoPath);
        }
    }

}
