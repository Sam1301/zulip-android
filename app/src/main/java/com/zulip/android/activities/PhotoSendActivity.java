package com.zulip.android.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zulip.android.R;
import com.zulip.android.util.PhotoHelper;

import java.io.File;

public class PhotoSendActivity extends AppCompatActivity {

    private ImageView mImageView;
    private String mPhotoPath;

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
        setContentView(R.layout.activity_photo_send);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

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
//                NavUtils.navigateUpFromSameTask(PhotoSendActivity.this);

            }
        });

        ImageView editPhotoBtn = (ImageView) findViewById(R.id.edit_photo);
        editPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoSendActivity.this, PhotoEditActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mPhotoPath);
                startActivity(intent);
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

        PhotoHelper.setPic(mImageView, mPhotoPath);
    }

}
