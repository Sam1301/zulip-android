package com.zulip.android.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zulip.android.R;
import com.zulip.android.util.DrawCustomView;
import com.zulip.android.util.PhotoHelper;

public class PhotoEditActivity extends AppCompatActivity {

    private String mPhotoPath;
    private ImageView mImageView;
    private boolean isMarkingFinished;
    private DrawCustomView mDrawCustomView;
    private boolean mIsCropped;

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

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_photo_edit);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        // TODO: move var declarations to top
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);
        mIsCropped = intent.getExtras().getBoolean("myBoolean");

        ImageView sendPhoto = (ImageView) findViewById(R.id.send_photo);

        final Intent sendIntent = new Intent(this, ZulipActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass edited photo file path
                PhotoHelper.saveBitmapAsFile(mPhotoPath, mImageView);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                startActivity(sendIntent);
            }
        });

        mImageView = (ImageView) findViewById(R.id.photoImageView);

        ImageView backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoEditActivity.super.onBackPressed();
            }
        });



        mDrawCustomView = (DrawCustomView)findViewById(R.id.draw_custom_view);
        ImageView markerBtn = (ImageView) findViewById(R.id.marker_btn);
        markerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMarkingFinished) {
                    int[] imageDimensions = PhotoHelper.getBitmapPositionInsideImageView(mImageView);
//                    mDrawCustomView.setWidthHeightBitmap(imageDimensions[2], imageDimensions[3]);

//                    mDrawCustomView.getLayoutParams().width = imageDimensions[2];
//                    mDrawCustomView.getLayoutParams().height = imageDimensions[3];
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            imageDimensions[2],
                            imageDimensions[3]
                    );
                    params.setMargins(imageDimensions[0], imageDimensions[1], 0, 0);
                    mDrawCustomView.setLayoutParams(params);
//                    mDrawCustomView.requestLayout();

                    mDrawCustomView.setVisibility(View.VISIBLE);
                    isMarkingFinished = true;
                } else {
                    mDrawCustomView.invalidate();
                    Bitmap drawingBitmap = mDrawCustomView.getCanvasBitmap();
                    Bitmap imageViewBitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();

                    overlay(imageViewBitmap, drawingBitmap);

                    mDrawCustomView.setVisibility(View.GONE);
                    isMarkingFinished = false;
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
        if (!mIsCropped) {
            PhotoHelper.setPicWithRotation(mImageView, mPhotoPath);
        } else {
            PhotoHelper.setPicWithoutRotation(mImageView, mPhotoPath);
        }
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap overlayBitmap = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth(), bmp1.getHeight(), false);
        Canvas canvas = new Canvas(bmp1);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(overlayBitmap, 0, 0, paint);
        return bmp1;
    }
}
