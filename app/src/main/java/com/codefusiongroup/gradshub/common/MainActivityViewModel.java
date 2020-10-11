package com.codefusiongroup.gradshub.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.events.EventsRepositoryImpl;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;

import java.util.List;


public class MainActivityViewModel extends ObservableViewModel {

    private EventsRepositoryImpl repository;
    private MutableLiveData<Resource<List<String>>> favouredEventsResponse;

    public MainActivityViewModel() {
        repository = EventsRepositoryImpl.getInstance();
    }

    public void fetchFavouriteEvents(String userID) {
        repository.getPreviouslyFavouredEvents(userID);
    }

    public LiveData<Resource<List<String>>> getFavouredEventsResponse() {
        if (favouredEventsResponse == null) {
            favouredEventsResponse = repository.getPreviouslyFavouredEventsResponse();
        }
        return favouredEventsResponse;
    }

}
