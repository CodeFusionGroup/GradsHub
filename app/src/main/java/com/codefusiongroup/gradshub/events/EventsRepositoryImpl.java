package com.codefusiongroup.gradshub.events;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
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


public class EventsRepositoryImpl implements IEventsRepository {

    private static final String TAG = "EventsRepositoryImpl";

    private static EventsRepositoryImpl instance;
    private final EventsAPI eventsAPI = ApiProvider.getEventsApiService();

    private MutableLiveData<Resource<String>> favouredEventsResponse;
    private MutableLiveData<Resource<String>> unfavouredEventsResponse;
    private MutableLiveData<Resource< Map<String, String> >> eventsStarsResponse;
    private MutableLiveData<Resource<List<String>>> previouslyFavouredEventsResponse;


    private EventsRepositoryImpl() { }

    public static EventsRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new EventsRepositoryImpl();
        }
        return instance;
    }

    @Override
    public MutableLiveData<Resource<String>> getRegisteredEventsResponse() {
        if (favouredEventsResponse == null) {
            favouredEventsResponse = new MutableLiveData<>();
        }
        return favouredEventsResponse;
    }

    @Override
    public MutableLiveData<Resource<String>> getUnregisteredEventsResponse() {
        if (unfavouredEventsResponse == null) {
            unfavouredEventsResponse = new MutableLiveData<>();
        }
        return unfavouredEventsResponse;
    }

    @Override
    public MutableLiveData<Resource<List<String>>> getPreviouslyFavouredEventsResponse () {
        if (previouslyFavouredEventsResponse == null) {
            previouslyFavouredEventsResponse = new MutableLiveData<>();
        }
        return previouslyFavouredEventsResponse;
    }

    @Override
    public MutableLiveData<Resource<Map<String, String>>> getEventsStarsResponse () {
        if (eventsStarsResponse == null) {
            eventsStarsResponse = new MutableLiveData<>();
        }
        return eventsStarsResponse;
    }


    @Override
    public void registerFavouredEvents(String userID, List<String> favouredEvents) {

        StringBuilder eventsIDs = new StringBuilder();
        for(int i = 0; i < favouredEvents.size(); i++) {
            eventsIDs.append(favouredEvents.get(i));
            if (i != favouredEvents.size()-1) {
                eventsIDs.append(",");
            }
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("event_ids", eventsIDs.toString());

        Log.d(TAG, "registerFavouredEvents() --> user id: "+userID);
        Log.d(TAG, "registerFavouredEvents() --> favoured events: "+eventsIDs.toString());

        eventsAPI.registerFavouredEvents(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "registerFavouredEvents() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
                    favouredEventsResponse.setValue( Resource.apiNonDataRequestSuccess(jsonObject.get("message").getAsString()) );
                }
                else {
                    favouredEventsResponse.setValue( Resource.error("Failed to update favoured events.") );
                    Log.d(TAG, "registerFavouredEvents() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                favouredEventsResponse.setValue( Resource.error("Failed to update favoured events.") );
                Log.d(TAG, "registerFavouredEvents() --> onFailure executed, error: ", t);
            }

        });

    }


    @Override
    public void unRegisterFavouredEvents(String userID, List<String> unfavouredEvents) {

        StringBuilder eventsIDs = new StringBuilder();
        for(int i = 0; i < unfavouredEvents.size(); i++) {
            eventsIDs.append(unfavouredEvents.get(i));
            if (i != unfavouredEvents.size()-1) {
                eventsIDs.append(",");
            }
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("event_ids", eventsIDs.toString());

        Log.d(TAG, "unRegisterFavouredEvents() --> user id: "+userID);
        Log.d(TAG, "unRegisterFavouredEvents() --> unfavoured events: "+eventsIDs.toString());

        eventsAPI.unRegisterFavouredEvents(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "unRegisterFavouredEvents() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
                    unfavouredEventsResponse.setValue( Resource.apiNonDataRequestSuccess(jsonObject.get("message").getAsString()) );
                }
                else {
                    unfavouredEventsResponse.setValue( Resource.error("Failed to update favoured events") );
                    Log.d(TAG, "unRegisterFavouredEvents() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                unfavouredEventsResponse.setValue( Resource.error("Failed to update favoured events") );
                Log.d(TAG, "unRegisterFavouredEvents() --> onFailure executed, error: ", t);
            }

        });


    }


    @Override
    public void getPreviouslyFavouredEvents(String userID) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        eventsAPI.getUserFavouredEvents(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "getPreviouslyFavouredEvents() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();

                    // there exists user favoured events
                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
                        List<String> favouredEvents = new ArrayList<>();
                        JsonArray favouredEventsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: favouredEventsJA) {
                            JsonObject favouredEventJO = jsonElement.getAsJsonObject();
                            String eventId = favouredEventJO.get("EVENT_ID").getAsString();
                            favouredEvents.add(eventId);
                        }
                        previouslyFavouredEventsResponse.setValue( Resource.apiDataRequestSuccess(favouredEvents, null) );
                    }
                    else {
                        // we don't consider this result
                        Log.d(TAG, "getPreviouslyFavouredEvents() --> no favoured events");
                    }

                }
                else {
                    previouslyFavouredEventsResponse.setValue( Resource.error("Failed to update favoured events, please swipe to refresh page") );
                    Log.d(TAG, "getPreviouslyFavouredEvents() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                previouslyFavouredEventsResponse.setValue( Resource.error("Failed to update favoured events, please swipe to refresh page") );
                Log.d(TAG, "getPreviouslyFavouredEvents() --> onFailure executed, error: ", t);
            }

        });

    }


    @Override
    public void getEventsStars() {

        eventsAPI.getEventsStars().enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "getEventsStars() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
                        Map<String, String> eventsStatistics = new HashMap<>();
                        JsonArray favouredEventsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: favouredEventsJA) {
                            JsonObject favouredEventJO = jsonElement.getAsJsonObject();
                            String eventID = favouredEventJO.get("EVENT_ID").getAsString();
                            String eventStars = favouredEventJO.get("NO_STARS").getAsString();
                            eventsStatistics.put(eventID, eventStars);
                        }
                        eventsStarsResponse.setValue( Resource.apiDataRequestSuccess(eventsStatistics, null) );
                    }

                    else {
                        // we don't consider this case
                        Log.d(TAG, "getEventsStars() --> no events stats available");
                    }
                }
                else {
                    eventsStarsResponse.setValue( Resource.error("Failed to update events stats, please swipe to refresh page") );
                    Log.d(TAG, "getEventsStars() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                eventsStarsResponse.setValue( Resource.error("Failed to update events stats, please swipe to refresh page") );
                Log.d(TAG, "getEventsStars() --> onFailure executed, error: ", t);
            }

        });

    }


}
