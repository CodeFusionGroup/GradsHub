package com.codefusiongroup.gradshub.groups.creategroup;

import android.util.Log;

import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.groups.GroupsAPI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class CreateGroupModel implements CreateGroupContract.ICreateGroupModel {



    private final String  SUCCESS_CODE = "1";
    private final String  SERVER_FAILURE_CODE = "-100";
    private final String  SERVER_FAILURE_MSG = "Connection failed, please try again later.";

    private final GroupsAPI groupsAPI = ApiProvider.getGroupsApiService();
    private Map<String, String> params = new HashMap<>();

    private final CreateGroupContract.ICreateGroupPresenter mPresenter;
    private static CreateGroupModel createGroupModel = null;


    private CreateGroupModel(CreateGroupContract.ICreateGroupPresenter presenter) {
        mPresenter = presenter;
    }


    public static CreateGroupModel newInstance(CreateGroupPresenter presenter) {
        if (createGroupModel == null) {
            createGroupModel = new CreateGroupModel(presenter);
        }
        return createGroupModel;
    }



    @Override
    public void createUserGroup(ResearchGroup group) {

        params.put("email", group.getGroupAdmin());
        params.put("name", group.getGroupName());
        params.put("visibility", group.getGroupVisibility());
        params.put("code", group.getGroupInviteCode());

        groupsAPI.createGroup(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(SUCCESS_CODE) ) {
                        mPresenter.setCreateGroupResponseCode(SUCCESS_CODE);
                        mPresenter.setCreateGroupResponseMsg(jsonObject.get("message").getAsString());
                    }
                    else {
                        // group name already exists
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        mPresenter.setCreateGroupResponseCode( apiDefault.getStatusCode() );
                        mPresenter.setCreateGroupResponseMsg( apiDefault.getMessage() );
                    }

                    mPresenter.onRequestToCreateGroupFinished();
                }
                else {
                    Log.i("CreateGroupModel", "response.isSuccessful = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mPresenter.setCreateGroupResponseCode(SERVER_FAILURE_CODE);
                mPresenter.setCreateGroupResponseMsg(SERVER_FAILURE_MSG);
                mPresenter.onRequestToCreateGroupFinished();;
                t.printStackTrace();
            }

        });

    }


}
