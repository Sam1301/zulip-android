package com.zulip.android.networking.response.events;

import com.google.gson.annotations.SerializedName;
import com.zulip.android.ZulipApp;
import com.zulip.android.models.Stream;

import java.util.List;

/**
 * This class is used to deserialize the subscription type events {@link EventsBranch.BranchType#SUBSCRIPTIONS}.
 *
 * {@link SubscriptionWrapper#operation} signifies the operation of the event
 * namely : add {@link SubscriptionWrapper#OPERATION_ADD},
 * update {@link SubscriptionWrapper#OPERATION_UPDATE} and
 * remove {@link SubscriptionWrapper#OPERATION_REMOVE}.
 *
 * {@link SubscriptionWrapper#property} holds the property updated and {@link SubscriptionWrapper#value}
 * holds the updated value of this property.
 */

public class SubscriptionWrapper extends EventsBranch {

    public static final String OPERATION_ADD = "add";
    public static final String OPERATION_REMOVE = "remove";
    public static final String OPERATION_UPDATE = "update";

    @SerializedName("subscriptions")
    private List<Stream> streams;

    @SerializedName("op")
    private String operation;

    @SerializedName("name")
    private String streamName;

    @SerializedName("property")
    private String property;

    @SerializedName("email")
    private String email;

    @SerializedName("value")
    private Object value;

    public List<Stream> getStreams() {
        return this.streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * This function returns the updated stream object in case of an update subscription event.
     *
     * @param app {@link ZulipApp} instance
     * @return updated {@link Stream} object
     */
    public Stream getUpdatedStream(ZulipApp app) {
        if (this.operation.equalsIgnoreCase(SubscriptionWrapper.OPERATION_UPDATE)) {
            // TODO: account for other updates
            if (property.equalsIgnoreCase("color")) {
                // color of stream is changed
                Stream stream = Stream.getByName(app, streamName);
                stream.setFetchColor((String) this.value);
                return stream;
            } else if (property.equalsIgnoreCase("in_home_view")) {
                // stream is muted or unmuted
                Stream stream = Stream.getByName(app, streamName);
                stream.setInHomeView((boolean) this.value);
                return stream;
            }
        }

        return null;
    }
}
