package com.codefusiongroup.gradshub.friends;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class FriendsRepositoryImpl implements IFriendsRepository {

    private static final String TAG = "FriendsRepositoryImpl";
    private final FriendsAPI friendsAPI = ApiProvider.getFriendsApiService();

    private static FriendsRepositoryImpl repository = null;

    // constructor private so that only one instance of FriendsRepositoryImpl object is created and no other
    // class can instantiate it directly
    private FriendsRepositoryImpl() { }

    // singleton pattern
    public static FriendsRepositoryImpl getInstance() {
        if (repository == null) {
            repository = new FriendsRepositoryImpl();
        }
        return repository;
    }


    private MutableLiveData<Resource<List<User>>> userFriendsResponse;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    @Override
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }


    @Override
    public MutableLiveData<Resource<List<User>>> getUserFriendsResponse() {
        if (userFriendsResponse == null) {
            userFriendsResponse = new MutableLiveData<>();
        }
        return userFriendsResponse;
    }


        @Override
    public void addToFriends(String userID, String addID) {

    }


    @Override
    public void removeFromFriends(String userID, String removeID) {

    }


    @Override
    public void getUserFriends(String userID) {

        Map<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        isLoading.setValue(true);
        friendsAPI.getUserFriends(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                isLoading.setValue(false);
                if ( response.isSuccessful() ) {
                    Log.d(TAG, "getUserFriends() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        JsonArray userFriendsJA = jsonObject.getAsJsonArray("message");
                        List<User> friendsList = new ArrayList<>();
                        for (JsonElement jsonElement: userFriendsJA) {

                            JsonObject userFriendJO = jsonElement.getAsJsonObject();
                            String userID = userFriendJO.get("USER_ID").getAsString();
                            String firstName = userFriendJO.get("USER_FNAME").getAsString();
                            String lastName = userFriendJO.get("USER_LNAME").getAsString();

                            //TODO: needs php file
                            //String academicStatus = userFriendJO.get("USER_ACAD_STATUS").getAsString();

                            User user = new User();
                            user.setUserID(userID);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            //user.setAcademicStatus(academicStatus);
                            user.setFriendStatus(true);
                            friendsList.add(user);
                        }

                        userFriendsResponse.setValue( Resource.apiDataRequestSuccess(friendsList, null));
                    }
                    else {
                        // no latest posts exists yet
                        userFriendsResponse.setValue( Resource.apiNonDataRequestSuccess(jsonObject.get("message").getAsString()) );
                    }

                }
                else {
                    userFriendsResponse.setValue( Resource.error("Could not load friends please refresh page.") );
                    Log.d(TAG, "getUserFriends() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isLoading.setValue(false);
                userFriendsResponse.setValue( Resource.error("Could not load friends please refresh page.") );
                Log.d(TAG, "getUserFriends() --> onFailure executed, error: ", t);
            }

        });

    }


    public void deregisterObserverObjects() {
        isLoading.setValue(null);
        userFriendsResponse.setValue(null);
    }

}
