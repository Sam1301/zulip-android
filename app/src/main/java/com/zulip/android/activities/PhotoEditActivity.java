package com.zulip.android.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.zulip.android.R;
import com.zulip.android.util.PhotoHelper;
import com.zulip.android.util.ZLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoEditActivity extends AppCompatActivity {

    private String mPhotoPath;
    private ImageView mImageView;
    private CropImageView mCropImageView;
    private boolean isCropFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // TODO: make content appear behind status bar
//        // make application's content appear behind the status bar
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_photo_edit);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        // TODO: move var declarations to top
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);

        ImageView sendPhoto = (ImageView) findViewById(R.id.send_photo);

        final Intent sendIntent = new Intent(this, ZulipActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass edited photo file path
                saveBitmapAsFile();
                sendIntent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                startActivity(sendIntent);
            }
        });

        mImageView = (ImageView) findViewById(R.id.photoImageView);
        mCropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        
        ImageView backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoEditActivity.super.onBackPressed();
            }
        });

        ImageView cropBtn = (ImageView) findViewById(R.id.crop_btn);
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCropFinished) {
                    Bitmap bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                    mCropImageView.setImageBitmap(bitmap);
                    mCropImageView.setVisibility(View.VISIBLE);
                    isCropFinished = true;
                } else {
                    Bitmap croppedImage = mCropImageView.getCroppedImage();
                    mCropImageView.setVisibility(View.GONE);
                    mImageView.setImageBitmap(croppedImage);
                    isCropFinished = false;
                }
            }
        });
    }

    private void saveBitmapAsFile() {
        // delete old bitmap
        File file = new File(mPhotoPath);
        file.delete();

        // store new bitmap at mPhotoPath
        FileOutputStream out = null;
        Bitmap bmp = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        try {
            out = new FileOutputStream(mPhotoPath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                ZLog.logException(e);
            }
        }
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

        PhotoHelper.setPic(mImageView, mPhotoPath);
    }
}
