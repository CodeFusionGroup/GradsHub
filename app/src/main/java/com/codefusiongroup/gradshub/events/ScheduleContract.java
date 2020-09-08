package com.codefusiongroup.gradshub.events;

import com.codefusiongroup.gradshub.common.models.User;

import java.util.List;
import java.util.Map;


public interface ScheduleContract {


    interface IScheduleView {

        // informs the view to update the schedule in terms of showing the number of stars for each event
        // and highlighting those events the user has previously favoured if there are any.
        void updateEventsSchedule(List<String> userFavouredEvents, Map<String, String> eventsStars);

        // informs the view to display the status of the update after the user has favoured or unfavoured
        // some events as received from the endpoint
        void showUserFavouredEventsStatus(String message);

        // informs the view to display an error in case there is a failure to contact the server
        void showServerErrorResponse(String message);

    }

    interface ISchedulePresenter {

        // invoked by the view on the presenter as soon as the view for the fragment is initialised
        // which then gets the user's favoured events if there are any and gets the stars for each
        // favoured event
        void onViewCreated(User user);


        void setUserFavouredEventsRequestFinished(boolean value);


        void setEventsStarsRequestFinished(boolean value);


        // invoked by the model on the presenter as soon as the concurrent requests for user's favourite
        // events and events stars has completed
        void scheduleDataRequestFinished();


        // invoked by the view on the presenter to register the events the user has favoured
        void registerUserFavouredEvents(User user, List<String> favouredEvents);


        // invoked by the view on the presenter to unregister the events the user has unfavoured
        void unRegisterUserFavouredEvents(User user, List<String> unFavouredEvents);


        // invoked by the model on the presenter when the request to update the user's favourite
        // events has finished
        void onRequestUpdateUserFavouredEventsFinished();


        // invoked by the model on the presenter when the request for the user's favourite events has
        // been successful and there exists events the user has favoured
        void setUserFavouredEvents(List<String> favouredEvents);


        // invoked by the model on the presenter when the request for events stars data has
        // been successful and there exists favoured events
        void setEventsStars(Map<String, String> eventsStars);


        // invoked by the model on the presenter after the network request has finished to set
        // parameters for message and response code from the network request
        // (similarly for the methods that follow below this one)
        void setUserFavouredEventsResponseCode(String responseCode);


        void setUserFavouredEventsResponseMsg(String responseMsg);


        void setEventsStarsResponseCode(String responseCode);


    }

    interface IScheduleModel {

        // invoked by the presenter on the model when the view has alerted the presenter after its view has
        // been initialise (similarly for the method getEventsStars() )
        void getUserFavouredEvents(User user);


        void getEventsStars(); // don't delete

        // invoked by the presenter on the model to update the user's favoured events in case they have
        // favoured or unfavoured any events
        void updateUserFavouredEvents(User user, List<String> favouredEvents);

    }


}
