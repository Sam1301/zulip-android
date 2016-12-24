package com.zulip.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zulip.android.R;

import java.io.File;

public class PhotoSendActivity extends AppCompatActivity {

    private String mPhotoPath;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_send);

        // run activity in full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get the file path sent from ZulipActivity
        final Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);

        mImageView = (ImageView) findViewById(R.id.photoImageView);

        // intent to go back to ZulipActivity and upload photo
        final Intent sendIntent = new Intent(this, ZulipActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // use glide to take care of high performance bitmap decoding
        Glide.with(this).load(mPhotoPath).crossFade().into(mImageView);
    }
}
