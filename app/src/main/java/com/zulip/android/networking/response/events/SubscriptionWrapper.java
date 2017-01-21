package com.zulip.android.networking.response.events;

import com.google.gson.annotations.SerializedName;
import com.zulip.android.models.Stream;

import java.util.List;

/**
 * TODO: add description
 */

public class SubscriptionWrapper extends EventsBranch {

    @SerializedName("subscriptions")
    private List<Stream> streams;

    public List<Stream> getStreams() {
        return this.streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }
}
