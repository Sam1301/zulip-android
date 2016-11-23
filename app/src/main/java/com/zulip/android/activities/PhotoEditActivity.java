package com.zulip.android.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zulip.android.R;
import com.zulip.android.util.DrawCustomView;
import com.zulip.android.util.PhotoHelper;

import static com.zulip.android.R.id.black_marker;

public class PhotoEditActivity extends AppCompatActivity {

    private String mPhotoPath;
    private ImageView mImageView;
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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // TODO: move var declarations to top
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);
        mIsCropped = intent.getExtras().getBoolean("myBoolean");

        setContentView(R.layout.activity_photo_edit);
        ImageView sendPhoto = (ImageView) findViewById(R.id.send_photo);

        final Intent sendIntent = new Intent(this, ZulipActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass edited photo file path
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout_picture);
                frameLayout.setVisibility(View.INVISIBLE);
                Bitmap bitmap = screenShot(frameLayout);
                PhotoHelper.saveBitmapAsFile(mPhotoPath, bitmap);
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

        ImageView undoBtn = (ImageView) findViewById(R.id.undo_btn);
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawCustomView.onClickUndo();
            }
        });

        // set a border and color for black marker
        ImageView black_marker = (ImageView) findViewById(R.id.black_marker);
        GradientDrawable blackCircle = (GradientDrawable) black_marker.getDrawable();
        blackCircle.setColor(ContextCompat.getColor(this, R.color.black_marker_tool));
        blackCircle.setStroke(3, Color.GRAY);
    }

    public void handleMarkerColorChange(View view) {
        int colorId = R.color.black_marker_tool;
        switch (view.getId()) {
            case R.id.red_marker:
                colorId = R.color.red_marker_tool;
                break;
            case R.id.yellow_marker:
                colorId = R.color.yellow_marker_tool;
                break;
            case R.id.green_marker:
                colorId = R.color.green_marker_tool;
                break;
            case R.id.white_marker:
                colorId = R.color.white_marker_tool;
                break;
            case R.id.blue_marker:
                colorId = R.color.blue_marker_tool;
                break;
            case black_marker:
                colorId = R.color.black_marker_tool;
                break;
            default:
                Log.e("Marker Tool", "Invalid color");
                break;
        }

        mDrawCustomView.setBrushColor(ContextCompat.getColor(this, colorId));
        ImageView markerIcon = (ImageView) findViewById(R.id.marker_btn);
        GradientDrawable markerBackground = (GradientDrawable) markerIcon.getBackground();
        markerBackground.setColor(ContextCompat.getColor(this, colorId));
        if (colorId == R.color.black_marker_tool) {
            markerBackground.setStroke(3, Color.GRAY);
        } else  {
            markerBackground.setStroke(0, Color.GRAY);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View decorView = getWindow().getDecorView();
        // make application's content appear behind the status bar
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

        int[] imageDimensions = PhotoHelper.getBitmapPositionInsideImageView(mImageView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                imageDimensions[2],
                imageDimensions[3]
        );
        params.setMargins(imageDimensions[0], imageDimensions[1], 0, 0);
        mDrawCustomView.setLayoutParams(params);
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
