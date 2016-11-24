package com.zulip.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
    private String mIsCroppedKey = "photo cropped";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // run activity in full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get file path of image from PhotoSendActivity
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);
        mIsCropped = intent.getExtras().getBoolean(mIsCroppedKey);

        setContentView(R.layout.activity_photo_edit);

        ImageView sendPhoto = (ImageView) findViewById(R.id.send_photo);

        // intent to go back to ZulipActivity and upload photo
        final Intent sendIntent = new Intent(this, ZulipActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass edited photo file path to ZulipActivity
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout_picture);
                frameLayout.setVisibility(View.INVISIBLE);
                // take screenshot of cropped image
                Bitmap bitmap = screenShot(frameLayout);
                PhotoHelper.saveBitmapAsFile(mPhotoPath, bitmap);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                startActivity(sendIntent);
            }
        });

        mImageView = (ImageView) findViewById(R.id.photoImageView);

        // go back when back button is pressed
        ImageView backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoEditActivity.super.onBackPressed();
            }
        });

        mDrawCustomView = (DrawCustomView) findViewById(R.id.draw_custom_view);
        ImageView undoBtn = (ImageView) findViewById(R.id.undo_btn);
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawCustomView.onClickUndo();
            }
        });

        // set a border and color for black marker color
        ImageView black_marker = (ImageView) findViewById(R.id.black_marker);
        GradientDrawable blackCircle = (GradientDrawable) black_marker.getDrawable();
        blackCircle.setColor(ContextCompat.getColor(this, R.color.black_marker_tool));
        blackCircle.setStroke(3, Color.GRAY);
    }

    /**
     * This function is called when any of the marker colors are chosen.
     * Its sets the color for marker tool and changes its the background.
     *
     * @param view color ImageView
     */
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

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // rotate bitmap if image was sent from PhotoSendActivity without cropping
        // otherwise don't rotate
        if (!mIsCropped) {
            PhotoHelper.setPic(mImageView, mPhotoPath, true);
        } else {
            PhotoHelper.setPic(mImageView, mPhotoPath, false);
        }

        // bound the canvas for drawing to the actual dimensions of imageView
        int[] imageDimensions = PhotoHelper.getBitmapPositionInsideImageView(mImageView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                imageDimensions[2],
                imageDimensions[3]
        );
        params.setMargins(imageDimensions[0], imageDimensions[1], 0, 0);
        mDrawCustomView.setLayoutParams(params);
    }

    /**
     * Function that takes a screenshot of the view passed and returns a bitmap for it.
     *
     * @param view {@link View}
     * @return screenshot of the view passed
     */
    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
