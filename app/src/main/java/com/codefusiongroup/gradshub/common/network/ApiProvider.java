package com.codefusiongroup.gradshub.common.network;

import com.codefusiongroup.gradshub.authentication.AuthenticationAPI;
import com.codefusiongroup.gradshub.events.EventsAPI;
import com.codefusiongroup.gradshub.friends.FriendsAPI;
import com.codefusiongroup.gradshub.groups.GroupsAPI;
import com.codefusiongroup.gradshub.messaging.MessagingAPI;
import com.codefusiongroup.gradshub.profile.ProfileAPI;


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

    public static FriendsAPI getFriendsApiService() {
        return RetrofitClient.getClient().create(FriendsAPI.class);
    }


    public static ProfileAPI getProfileApiService() {
        return RetrofitClient.getClient().create(ProfileAPI.class);
    }

}
