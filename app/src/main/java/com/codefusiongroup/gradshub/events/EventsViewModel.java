package com.codefusiongroup.gradshub.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;

import java.util.List;
import java.util.Map;


public class EventsViewModel extends ObservableViewModel {

    EventsRepositoryImpl repository;

    public EventsViewModel() {
        repository = EventsRepositoryImpl.getInstance();
    }


    private MutableLiveData<Resource<String>> favouredEventsResponse;
    private MutableLiveData<Resource<String>> unfavouredEventsResponse;
    private MutableLiveData<Resource<List<String>>> previouslyFavouredEventsResponse;
    private MutableLiveData<Resource<Map<String,String>>> eventsStarsResponse;


    public void getPreviouslyFavouredEvents(String userID) {
        repository.getPreviouslyFavouredEvents(userID);
    }

    public void getEventsStars() {
        repository.getEventsStars();
    }


    public void registerFavouredEvents(String userID, List<String> favouredEvents) {
        repository.registerFavouredEvents(userID, favouredEvents);
    }


    public void unRegisterFavouredEvents(String userID, List<String> unfavouredEvents) {
        repository.unRegisterFavouredEvents(userID, unfavouredEvents);
    }


    public LiveData<Resource<List<String>>> getPreviouslyFavouredEvents() {
        if (previouslyFavouredEventsResponse == null) {
            previouslyFavouredEventsResponse = repository.getPreviouslyFavouredEventsResponse();
        }
        return previouslyFavouredEventsResponse;
    }


    public LiveData<Resource<Map<String, String>>> getEventsStarsResponse() {
        if (eventsStarsResponse == null) {
            eventsStarsResponse = repository.getEventsStarsResponse();
        }
        return eventsStarsResponse;
    }


    public LiveData<Resource<String>> getRegisteredEventsResponse() {
        if (favouredEventsResponse == null) {
            favouredEventsResponse = repository.getRegisteredEventsResponse();
        }
        return favouredEventsResponse;
    }


    public LiveData<Resource<String>> getUnregisteredEventsResponse() {
        if (unfavouredEventsResponse == null) {
            unfavouredEventsResponse = repository.getUnregisteredEventsResponse();
        }
        return unfavouredEventsResponse;
    }


    public void onBackPressed() {
        previouslyFavouredEventsResponse.setValue(null);
        favouredEventsResponse.setValue(null);
        unfavouredEventsResponse.setValue(null);
        eventsStarsResponse.setValue(null);
    }

}
