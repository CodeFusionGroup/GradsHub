package com.codefusiongroup.gradshub.events;

import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.utils.api.Resource;

import java.util.List;
import java.util.Map;

public interface IEventsRepository {

    void registerFavouredEvents(String userID, List<String> favouredEvents);

    void unRegisterFavouredEvents(String userID, List<String> unfavouredEvents);

    void getPreviouslyFavouredEvents(String userID);

    void getEventsStars();

    MutableLiveData<Resource<String>> getRegisteredEventsResponse();

    MutableLiveData<Resource<String>> getUnregisteredEventsResponse ();

    MutableLiveData<Resource<List<String>>> getPreviouslyFavouredEventsResponse ();

    MutableLiveData<Resource<Map<String, String>>> getEventsStarsResponse ();

}
