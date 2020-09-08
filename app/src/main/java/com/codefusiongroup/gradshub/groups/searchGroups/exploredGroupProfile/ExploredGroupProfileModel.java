package com.codefusiongroup.gradshub.groups.searchGroups.exploredGroupProfile;

import android.util.Log;

import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.groups.GroupsAPI;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class ExploredGroupProfileModel implements ExploredGroupProfileContract.IAvailableGroupProfileModel {


    private final String  SUCCESS_CODE = "1";
    private final String  SERVER_FAILURE_CODE = "-100";
    private final String  SERVER_FAILURE_MSG = "Connection failed, please try again later.";

    private final GroupsAPI groupsAPI = ApiProvider.getGroupsApiService();
    private Map<String, String> params = new HashMap<>();

    private final ExploredGroupProfileContract.IAvailableGroupProfilePresenter mPresenter;
    private static ExploredGroupProfileModel mModel = null;


    private ExploredGroupProfileModel(ExploredGroupProfileContract.IAvailableGroupProfilePresenter presenter) {
        mPresenter = presenter;
    }


    public static ExploredGroupProfileModel newInstance(ExploredGroupProfilePresenter presenter) {
        if (mModel == null) {
            mModel = new ExploredGroupProfileModel(presenter);
        }
        return mModel;
    }


    @Override
    public void requestToJoinGroup(String userID, ResearchGroup group, String groupInviteCode) {

        params.put("user_id",userID);
        params.put("group_id", group.getGroupID());
        params.put("group_visibility", group.getGroupVisibility());
        params.put("group_code", groupInviteCode);

        groupsAPI.requestToJoinGroup(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {

                    JsonObject jsonObject = response.body();

                    if (jsonObject.get("success").getAsString().equals(SUCCESS_CODE)) {
                        mPresenter.setJoinGroupResponseCode(SUCCESS_CODE);
                    }
                    else {
                        mPresenter.setJoinGroupResponseCode( jsonObject.get("success").getAsString() );
                    }

                    mPresenter.setJoinGroupResponseMsg(jsonObject.get("message").getAsString());
                    mPresenter.onRequestToJoinGroupFinished();
                }
                else {
                    Log.i("AvailGroupProfileModel", "response.isSuccessful = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mPresenter.setJoinGroupResponseCode(SERVER_FAILURE_CODE);
                mPresenter.setJoinGroupResponseMsg(SERVER_FAILURE_MSG);
                mPresenter.onRequestToJoinGroupFinished();
                t.printStackTrace();
            }

        });

    }

}
