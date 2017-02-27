package com.zulip.android.networking;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.zulip.android.ZulipApp;
import com.zulip.android.activities.ZulipActivity;
import com.zulip.android.networking.util.Upload;
import com.zulip.android.util.ZLog;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import retrofit2.Call;
import retrofit2.Response;

public class UploadService extends IntentService {

    public UploadService() {
        super("Upload Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;


    @Override
    protected void onHandleIntent(Intent intent) {
        String filePath = intent.getStringExtra("file_path");
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        File file = new File(filePath);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle("Upload")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initDownload(file);

    }
    int timeCount = 1;
    private void initDownload(File file){
        final long startTime = System.currentTimeMillis();
        timeCount = 1;
        final ProgressListener progressListener = new ProgressListener() {
            @Override public void update(long bytesRead, long contentLength, boolean done) {
                // update ui after every 3 second
                if (System.currentTimeMillis() - startTime > 1000 * timeCount) {
                    Upload upload = new Upload();
                    double per = ((double) bytesRead) / contentLength;
                    upload.setProgress((int) (per * 100));
                    upload.setCurrentFileSize((int) (bytesRead / (Math.pow(1024, 2))));
                    upload.setTotalFileSize((int) (contentLength / (Math.pow(1024, 2))));
                    sendNotification(upload);
                    timeCount++;
                }
            }
        };


        Call<ResponseBody> request = ZulipApp.get().getUploadService(progressListener).upload(prepareFilePart(file));
        try {
            uploadFile(request.execute());
        } catch (IOException e) {
            ZLog.logException(e);
        }
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(File file) {
        // create UploadProgressRequest instance from file
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("file", file.getName(), request);
    }

    private void uploadFile(Response response) throws IOException {

        if (response.isSuccessful()) {
            onUploadComplete();
        }
    }

    private void sendNotification(Upload upload){

//        sendIntent(upload);
        notificationBuilder.setProgress(100,upload.getProgress(),false);
        // TODO: change
        notificationBuilder.setContentText(String.format("Uploaded (%d/%d) MB",upload.getCurrentFileSize(),upload.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendIntent(Upload upload){

        Intent intent = new Intent(ZulipActivity.PROGRESS_UPDATE);
        intent.putExtra("upload", upload);
        LocalBroadcastManager.getInstance(UploadService.this).sendBroadcast(intent);
    }

    private void onUploadComplete(){

        Upload upload = new Upload();
        upload.setProgress(100);
        sendIntent(upload);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("File Uploaded");
        notificationManager.notify(0, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

    public static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override public long contentLength() {
            return responseBody.contentLength();
        }

        @Override public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    public interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}
