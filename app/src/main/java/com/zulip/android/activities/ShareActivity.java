package com.zulip.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    public static final String SHARE_KEY= "implicit share intent";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent shareIntent = getIntent();
        if (shareIntent.getStringExtra(Intent.EXTRA_TEXT) != null) {
            // pass to zulip activity
            Intent intent = new Intent(this, ZulipActivity.class);
            // add data to this intent
            intent.putExtra(SHARE_KEY, shareIntent.getStringExtra(Intent.EXTRA_TEXT));
            startActivity(intent);
        }
    }
}
