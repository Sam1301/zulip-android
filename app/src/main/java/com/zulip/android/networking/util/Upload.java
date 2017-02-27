package com.zulip.android.networking.util;

import android.os.Parcel;
import android.os.Parcelable;

public class Upload implements Parcelable {

    public Upload(){
    }

    private int progress;
    private int currentFileSize;
    private int totalFileSize;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(int currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(int totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(progress);
        dest.writeInt(currentFileSize);
        dest.writeInt(totalFileSize);
    }

    private Upload(Parcel in) {

        progress = in.readInt();
        currentFileSize = in.readInt();
        totalFileSize = in.readInt();
    }

    public static final Parcelable.Creator<Upload> CREATOR = new Parcelable.Creator<Upload>() {
        public Upload createFromParcel(Parcel in) {
            return new Upload(in);
        }

        public Upload[] newArray(int size) {
            return new Upload[size];
        }
    };
}
