package com.zulip.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zulip.android.R;
import com.zulip.android.util.DrawCustomView;
import com.zulip.android.util.PhotoHelper;

public class PhotoEditActivity extends AppCompatActivity {

    private String mPhotoPath;
    private ImageView mImageView;
    private DrawCustomView mDrawCustomView;
    private SimpleTarget mGlideTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        // set a border and color for black marker color
        ImageView black_marker = (ImageView) findViewById(R.id.black_marker);
        GradientDrawable blackCircle = (GradientDrawable) black_marker.getDrawable();
        blackCircle.setColor(ContextCompat.getColor(this, R.color.black_marker_tool));
        blackCircle.setStroke(3, Color.GRAY);

        // change background of marker tool to default color red on activity start up
        int colorId = R.color.red_marker_tool;
        ImageView markerIcon = (ImageView) findViewById(R.id.marker_btn);
        GradientDrawable markerBackground = (GradientDrawable) markerIcon.getBackground();
        markerBackground.setColor(ContextCompat.getColor(this, colorId));

        // run activity in full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get file path of image from PhotoSendActivity
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);

        mImageView = (ImageView) findViewById(R.id.photoImageView);
        mDrawCustomView = (DrawCustomView) findViewById(R.id.draw_custom_view);

        // glide target called when image is loaded
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        mGlideTarget = new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                // set bitmap on imageView
                mImageView.setImageBitmap(bitmap);

                // bound the canvas for drawing to the actual dimensions of imageView
                int[] imageDimensions = PhotoHelper.getBitmapPositionInsideImageView(mImageView);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        imageDimensions[2],
                        imageDimensions[3]
                );
                params.setMargins(imageDimensions[0], imageDimensions[1], 0, 0);
                mDrawCustomView.setLayoutParams(params);
            }
        };

        // use glide to take care of high performance bitmap decoding
        Glide
                .with(this)
                .load(mPhotoPath)
                .asBitmap()
                .into(mGlideTarget);
    }

    /**
     * This function is called when any of the marker colors are chosen.
     * Its sets the color for marker tool and changes its the background.
     *
     * @param view color ImageView
     */
    public void handleMarkerColorChange(View view) {
        int colorId = R.color.red_marker_tool;
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
            case R.id.black_marker:
                colorId = R.color.black_marker_tool;
                break;
            default:
                Log.e("Marker Tool", "Invalid color");
                break;
        }

        // change marker tool color
        mDrawCustomView.setBrushColor(ContextCompat.getColor(this, colorId));

        // change background of marker tool
        ImageView markerIcon = (ImageView) findViewById(R.id.marker_btn);
        GradientDrawable markerBackground = (GradientDrawable) markerIcon.getBackground();
        markerBackground.setColor(ContextCompat.getColor(this, colorId));
        // if black color is selected, add a border to the background
        if (colorId == R.color.black_marker_tool) {
            markerBackground.setStroke(3, Color.GRAY);
        } else {
            markerBackground.setStroke(0, Color.GRAY);
        }
    }
}