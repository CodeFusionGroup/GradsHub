package com.codefusiongroup.gradshub.events;


import android.util.Log;

import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


//public class ScheduleModel implements ScheduleContract.IScheduleModel {
//
//
//    private final String  SUCCESS_CODE = "1";
//    private final String  SERVER_FAILURE_CODE = "-100";
//    private final String  SERVER_FAILURE_MSG = "Connection failed, please try again later.";
//
//    private final EventsAPI eventsAPI = ApiProvider.getEventsApiService();
//    private HashMap<String, String> params = new HashMap<>();
//
//    private final ScheduleContract.ISchedulePresenter mPresenter;
//    private static ScheduleModel scheduleModel = null;
//
//
//    private ScheduleModel(ScheduleContract.ISchedulePresenter presenter) {
//        mPresenter = presenter;
//    }
//
//
//    public static ScheduleModel newInstance(SchedulePresenter presenter) {
//        if (scheduleModel == null) {
//            scheduleModel = new ScheduleModel(presenter);
//        }
//        return scheduleModel;
//    }
//
//
//    @Override
//    public void getUserFavouredEvents(User user) {
//
//        params.put("user_id", user.getUserID());
//        eventsAPI.getUserFavouredEvents(params).enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                if ( response.isSuccessful() ) {
//
//                    JsonObject jsonObject = response.body();
//
//                    if ( jsonObject.get("success").getAsString().equals(SUCCESS_CODE) ) {
//
//                        List<String> userFavouredEvents = new ArrayList<>();
//                        JsonArray favouredEventsJA = jsonObject.getAsJsonArray("message");
//
//                        for (JsonElement jsonElement: favouredEventsJA) {
//                            JsonObject favouredEventJO = jsonElement.getAsJsonObject();
//                            userFavouredEvents.add(favouredEventJO.get("EVENT_ID").getAsString());
//                        }
//
//                        mPresenter.setUserFavouredEventsResponseCode(SUCCESS_CODE);
//                        mPresenter.setUserFavouredEvents( userFavouredEvents );
//
//                    }
//                    else {
//                        // user has not favoured any events yet
//                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
//                        mPresenter.setUserFavouredEventsResponseMsg( apiDefault.getMessage() );
//                        mPresenter.setUserFavouredEventsResponseCode( apiDefault.getStatusCode() );
//                    }
//
//                    mPresenter.setUserFavouredEventsRequestFinished(true);
//                    mPresenter.scheduleDataRequestFinished();
//                }
//                else {
//                    Log.i("ScheduleModel", "response.isSuccessful = false");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                mPresenter.setUserFavouredEventsResponseCode(SERVER_FAILURE_CODE);
//                mPresenter.setUserFavouredEventsResponseMsg(SERVER_FAILURE_MSG);
//                mPresenter.setUserFavouredEventsRequestFinished(true);
//                mPresenter.scheduleDataRequestFinished();
//                t.printStackTrace();
//            }
//
//        });
//
//    }
//
//
//    @Override
//    public void getEventsStars() {
//
//        eventsAPI.getEventsStars().enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                if ( response.isSuccessful() ) {
//
//                    JsonObject jsonObject = response.body();
//
//                    if ( jsonObject.get("success").getAsString().equals(SUCCESS_CODE) ) {
//
//                        Map<String, String> eventsStatistics = new HashMap<>();
//                        JsonArray favouredEventsJA = jsonObject.getAsJsonArray("message");
//
//                        for (JsonElement jsonElement: favouredEventsJA) {
//                            JsonObject favouredEventJO = jsonElement.getAsJsonObject();
//                            String eventID = favouredEventJO.get("EVENT_ID").getAsString();
//                            String eventStars = favouredEventJO.get("NO_STARS").getAsString();
//                            eventsStatistics.put(eventID, eventStars);
//                        }
//
//                        mPresenter.setEventsStarsResponseCode(SUCCESS_CODE);
//                        mPresenter.setEventsStars( eventsStatistics );
//                        mPresenter.setEventsStarsRequestFinished(true);
//                        mPresenter.scheduleDataRequestFinished();
//
//                    }
//
//                }
//
//                else {
//                    Log.i("ScheduleModel", "response.isSuccessful = false");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                mPresenter.setEventsStarsResponseCode(SERVER_FAILURE_CODE);
//                mPresenter.setEventsStarsRequestFinished(true);
//                mPresenter.scheduleDataRequestFinished();
//                t.printStackTrace();
//            }
//
//        });
//
//    }
//
//
//    @Override
//    public void registerFavouredEvents(User user, List<String> favouredEvents) {
//
//        StringBuilder eventsIDs = new StringBuilder();
//
//        for(int i = 0; i < favouredEvents.size(); i++) {
//            eventsIDs.append(favouredEvents.get(i));
//
//            if (i != favouredEvents.size()-1) {
//                eventsIDs.append(",");
//            }
//        }
//
//        params.put("user_id", user.getUserID());
//        params.put("event_ids", eventsIDs.toString());
//        eventsAPI.registerFavouredEvents(params).enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                if ( response.isSuccessful() ) {
//
//                    JsonObject jsonObject = response.body();
//
//                    if ( jsonObject.get("success").getAsString().equals(SUCCESS_CODE) ) {
//                        mPresenter.setUserFavouredEventsResponseCode(SUCCESS_CODE);
//                        mPresenter.setUserFavouredEventsResponseMsg(jsonObject.get("message").getAsString());
//                    }
//
//                    mPresenter.onRequestUpdateUserFavouredEventsFinished();
//                }
//                else {
//                    Log.i("ScheduleModel", "response.isSuccessful = false");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                mPresenter.setUserFavouredEventsResponseCode(SERVER_FAILURE_CODE);
//                mPresenter.setUserFavouredEventsResponseMsg(SERVER_FAILURE_MSG);
//                mPresenter.onRequestUpdateUserFavouredEventsFinished();
//                t.printStackTrace();
//            }
//
//        });
//
//    }
//
//
//
//    @Override
//    public void unRegisterFavouredEvents(User user, List<String> unFavouredEvents) {
//
//        StringBuilder eventsIDs = new StringBuilder();
//
//        for(int i = 0; i < unFavouredEvents.size(); i++) {
//            eventsIDs.append(unFavouredEvents.get(i));
//
//            if (i != unFavouredEvents.size()-1) {
//                eventsIDs.append(",");
//            }
//        }
//
//        params.put("user_id", user.getUserID());
//        params.put("event_ids", eventsIDs.toString());
//        eventsAPI.unRegisterFavouredEvents(params).enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                if ( response.isSuccessful() ) {
//
//                    JsonObject jsonObject = response.body();
//
//                    if ( jsonObject.get("success").getAsString().equals(SUCCESS_CODE) ) {
//                        mPresenter.setUserFavouredEventsResponseCode(SUCCESS_CODE);
//                        mPresenter.setUserFavouredEventsResponseMsg(jsonObject.get("message").getAsString());
//                    }
//
//                    mPresenter.onRequestUpdateUserFavouredEventsFinished();
//                }
//                else {
//                    Log.i("ScheduleModel", "response.isSuccessful = false");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                mPresenter.setUserFavouredEventsResponseCode(SERVER_FAILURE_CODE);
//                mPresenter.setUserFavouredEventsResponseMsg(SERVER_FAILURE_MSG);
//                mPresenter.onRequestUpdateUserFavouredEventsFinished();
//                t.printStackTrace();
//            }
//
//        });
//
//    }
//
//}
