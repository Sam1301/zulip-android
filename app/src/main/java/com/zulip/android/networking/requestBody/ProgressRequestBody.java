package com.zulip.android.networking.requestBody;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private UploadCallbacks mListener;
    int counter = 0;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage, String progress);
        void onError();
        void onFinish();
    }

    public ProgressRequestBody(final File file, final  UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("multipart/form-data");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        // workaround logging interceptor disturbing progress update
        counter++;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread only when httpLoggingInterceptor
                // is not calling writeTo()
                if (counter % 2 == 0) {
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                }

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            String progress = Math.round((mUploaded / Math.pow(1024, 2)) * 100) / 100.0 + " MB"
                    + " / " + Math.round((mTotal / Math.pow(1024, 2)) * 100) / 100.0 + " MB";
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal), progress);
        }
    }
}