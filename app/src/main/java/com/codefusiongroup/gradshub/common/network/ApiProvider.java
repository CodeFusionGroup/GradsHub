package com.codefusiongroup.gradshub.common.network;

import com.codefusiongroup.gradshub.authentication.AuthenticationAPI;
import com.codefusiongroup.gradshub.events.EventsAPI;
import com.codefusiongroup.gradshub.groups.GroupsAPI;
import com.codefusiongroup.gradshub.messaging.MessagingAPI;


// class provides the various APIs interfaces
public class ApiProvider {

    private ApiProvider() {
        // empty
    }

    public static AuthenticationAPI getAuthApiService() {
        return RetrofitClient.getClient().create(AuthenticationAPI.class);
    }

    public static EventsAPI getEventsApiService() {
        return RetrofitClient.getClient().create(EventsAPI.class);
    }

    public static GroupsAPI getGroupsApiService() {
        return RetrofitClient.getClient().create(GroupsAPI.class);
    }

    public static MessagingAPI getMessageApiService() {
        return RetrofitClient.getClient().create(MessagingAPI.class);
    }


}
