package com.zulip.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.zulip.android.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoSendActivity extends AppCompatActivity {

    private ImageView mImageView;
    private String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_send);

        Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(Intent.EXTRA_TEXT);
        mImageView = (ImageView) findViewById(R.id.photoImageView);
        Log.e("oooooooooooooooooo", "onCrete");
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (mImageView != null) {
            setPic();
        }
        Log.e("oooooooooooooooooo", "onWindow");
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = null;
        try {
            InputStream in = getContentResolver().openInputStream(
                    Uri.parse(mPhotoPath));
            bitmap = BitmapFactory.decodeStream(in/*, null, bmOptions*/);
        } catch (FileNotFoundException e) {
            // do something
            Log.e("ooooooooo", "error");
        }

        // TODO: rotate bitmap

        mImageView.setImageBitmap(bitmap);
    }
}
