package com.codefusiongroup.gradshub.events;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.codefusiongroup.gradshub.R;

import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.Schedule;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
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


public class ScheduleListFragment extends Fragment implements ScheduleContract.IScheduleView{


    private static String TAG = "ScheduleListFragment";

    private View mView;
    private ScheduleListRecyclerViewAdapter mAdapter;

    // listener that keeps track of which event has the user clicked on
    private OnScheduleListFragmentInteractionListener mListener;

    // listener that keeps track of which event is favoured by the user
    private ScheduleListRecyclerViewAdapter.OnScheduleItemFavouredListener onScheduleItemFavouredListener;

    // listener that keeps track of which event is unfavoured by the user
    private ScheduleListRecyclerViewAdapter.OnScheduleItemUnFavouredListener onScheduleItemUnFavouredListener;

    private static List<String> userPreviouslyFavouredEvents = new ArrayList<>();
    private static List<String> userCurrentlyFavouredEvents = new ArrayList<>();
    private static List<String> userUnFavouredEvents = new ArrayList<>();
    private List<Schedule> eventsSchedule = new ArrayList<>();

    private User mUser;
    private SchedulePresenter mPresenter;


    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);

        if (context instanceof OnScheduleListFragmentInteractionListener) {
            mListener = (OnScheduleListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnScheduleListFragmentInteractionListener");
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate executed");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        MainActivity mainActivity = (MainActivity) requireActivity();
        mUser = mainActivity.user;

        mPresenter = new SchedulePresenter();

        if ( mainActivity.getScheduleList() != null ) {
            eventsSchedule = mainActivity.getScheduleList();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView executed");
        mView = inflater.inflate(R.layout.fragment_schedule_item_list, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated executed");
        mPresenter.subscribe(this);

        getEventsStars();
        getUserFavouredEvents(mUser);

        onScheduleItemFavouredListener = (item) -> userCurrentlyFavouredEvents.add( item.getId() );

        onScheduleItemUnFavouredListener = (item) -> userUnFavouredEvents.add( item.getId() );


        // called after setting listeners like above
        //mPresenter.onViewCreated(mUser);

        // listen for when user presses back button from the ScheduleListFragment and use that as an
        // indication that they are finished interacting with the events schedule and if they favoured or unfavoured
        // any events we update the user's favourite events accordingly.
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener( (v, keyCode, event) -> {

            if ( keyCode == KeyEvent.KEYCODE_BACK ) {

                if ( userCurrentlyFavouredEvents.size() > 0 ) {
                    mPresenter.registerUserFavouredEvents(mUser, userCurrentlyFavouredEvents);
                    userCurrentlyFavouredEvents.clear();
                    mAdapter.notifyDataSetChanged();
                }

                if ( userUnFavouredEvents.size() > 0 ) {
                    mPresenter.unRegisterUserFavouredEvents(mUser, userUnFavouredEvents);
                    userUnFavouredEvents.clear();
                    mAdapter.notifyDataSetChanged();
                }

            }

            return false;
        });

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

        // for now we will use the same resource file since menu is the same
        menuInflater.inflate(R.menu.available_groups_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }

        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {

            default:
                // registers a click for any item that's selected on the app bar if no case matches
                return super.onOptionsItemSelected(item);
        }

    }


    // following methods called in ScheduleListRecyclerViewAdapter
    //====================================================================================

    public static List<String> getUserCurrentlyFavouredEvents() {

        if ( userCurrentlyFavouredEvents != null ) {
            return userCurrentlyFavouredEvents;
        } else {
            return null;
        }

    }


    public static List<String> getUserPreviouslyFavouredEvents() {

        if ( userPreviouslyFavouredEvents != null ) {
            return userPreviouslyFavouredEvents;
        } else {
            return null;
        }

    }


    public static List<String> getUserCurrentlyUnFavouredEvents() {

        if ( userUnFavouredEvents != null ) {
            return userUnFavouredEvents;
        } else {
            return null;
        }

    }
    //============================================================================================


    public static void removeEventFromFavouredList(String eventID) { userCurrentlyFavouredEvents.remove(eventID); }


    public static void removeEventFromPreviouslyFavouredList(String eventID) { userPreviouslyFavouredEvents.remove(eventID); }


    public void getEventsStars() {

        Log.i(TAG, "getEventsStars executed");
        EventsAPI eventsAPI = ApiProvider.getEventsApiService();
        eventsAPI.getEventsStars().enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "getEventsStars, response.isSuccessful() = true");

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

                        Log.i(TAG, "getEventsStars, eventsStats size --> "+eventsStatistics.size());
                        for ( Map.Entry<String, String> entry: eventsStatistics.entrySet() ) {
                            String eventID = entry.getKey();

                            for (Schedule event: eventsSchedule) {
                                // skip events that have already been assigned their star counts
                                if ( event.getStarCount() == 0 && event.getId().equals(eventID) ) {
                                    event.setStarCount( Integer.parseInt( entry.getValue() ) );
                                    // break out of inner loop to prevent further checking of other events since we have
                                    // already found an event with this ID.
                                    break;
                                }

                            }

                        }

                    }

                }

                else {
                    Log.i("ScheduleModel", "response.isSuccessful = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i(TAG, "getEventsStars, onFailure executed");
                GradsHubApplication.showToast("failed to update events stars.");
                t.printStackTrace();
            }

        });

    }


    public void getUserFavouredEvents(User user) {

        Log.i(TAG, "getUserFavouredEvents executed");
        EventsAPI eventsAPI = ApiProvider.getEventsApiService();
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user.getUserID());
        eventsAPI.getUserFavouredEvents(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "getUserFavouredEvents,  response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        List<String> userFavouredEvents = new ArrayList<>();
                        JsonArray favouredEventsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: favouredEventsJA) {
                            JsonObject favouredEventJO = jsonElement.getAsJsonObject();
                            userFavouredEvents.add(favouredEventJO.get("EVENT_ID").getAsString());
                        }

                        userPreviouslyFavouredEvents.clear();
                        userPreviouslyFavouredEvents.addAll(userFavouredEvents);
                        Log.i(TAG, "getUserFavouredEvents,  userPreviouslyFavouredEvents size --> "+userPreviouslyFavouredEvents.size());


                        for (Schedule event: eventsSchedule) {
                            String event_id = event.getId();
                            // search for the event id in the userFavouriteEvents list
                            for (String eventID: userFavouredEvents) {

                                if ( eventID.equals(event_id) ) {
                                    event.setFavouredByUser(true);
                                    // remove the processed event id from the userFavouredEvents list to reduce the size
                                    // NOTE: this event id is removed after userPreviouslyFavouredEvents has been assigned
                                    userFavouredEvents.remove(eventID);
                                    // break out of inner loop to prevent further checking of other events since we have
                                    // already found an event with this ID.
                                    break;
                                }

                            }
                        }


                        if (mView instanceof RelativeLayout) {

                            Context context = mView.getContext();
                            RecyclerView recyclerView = mView.findViewById(R.id.scheduleList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            mAdapter = new ScheduleListRecyclerViewAdapter(eventsSchedule, mListener, onScheduleItemFavouredListener, onScheduleItemUnFavouredListener);
                            recyclerView.setAdapter(mAdapter);

                        }

                    }
                    else {
                        // user has not favoured any events yet
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        GradsHubApplication.showToast( apiDefault.getStatusCode() );
                    }
                }
                else {
                    Log.i("ScheduleModel", "response.isSuccessful = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GradsHubApplication.showToast("failed to update your favourite events.");
                t.printStackTrace();
            }

        });

    }


    //==============================================================================================
    private void loadSchedule() {

        if (mView instanceof RelativeLayout) {

            Context context = mView.getContext();
            RecyclerView recyclerView = mView.findViewById(R.id.scheduleList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new ScheduleListRecyclerViewAdapter(eventsSchedule, mListener, onScheduleItemFavouredListener, onScheduleItemUnFavouredListener);
            recyclerView.setAdapter(mAdapter);

        }

    }


    @Override
    public void updateEventsSchedule(List<String> userFavouriteEvents, Map<String, String> eventsStars) {
        //TODO: fix changes not showing immediately when user has favoured event

        // set star counts for each event if there are favoured events in the DB
        if (eventsStars != null) {

            for ( Map.Entry<String, String> entry: eventsStars.entrySet() ) {
                String eventID = entry.getKey();

                for (Schedule event: eventsSchedule) {
                    // skip events that have already been assigned their star counts
                    if ( event.getStarCount() == 0 && event.getId().equals(eventID) ) {
                        event.setStarCount( Integer.parseInt( entry.getValue() ) );
                        // break out of inner loop to prevent further checking of other events since we have
                        // already found an event with this ID.
                        break;
                    }

                }

            }
        }


        // set events that have been favoured by the user if there are any
        if (userFavouriteEvents != null) {

            userPreviouslyFavouredEvents.addAll(userFavouriteEvents);

            for (Schedule event: eventsSchedule) {
                String event_id = event.getId();
                // search for the event id in the userFavouriteEvents list
                for (String eventID: userFavouriteEvents) {

                    if ( eventID.equals(event_id) ) {
                        event.setFavouredByUser(true);
                        // remove the processed event id from the userFavouredEvents list to reduce the size
                        // NOTE: this event id is removed after userPreviouslyFavouredEvents has been assigned
                        userFavouriteEvents.remove(eventID);
                        // break out of inner loop to prevent further checking of other events since we have
                        // already found an event with this ID.
                        break;
                    }

                }
            }

            if (mView instanceof RelativeLayout) {

                Context context = mView.getContext();
                RecyclerView recyclerView = mView.findViewById(R.id.scheduleList);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                if (mAdapter==null) {

                    mAdapter = new ScheduleListRecyclerViewAdapter(eventsSchedule, mListener, onScheduleItemFavouredListener, onScheduleItemUnFavouredListener);
                    Log.i(TAG, "updateEventsSchedule: (inside IF) mAdapter is null, creating adapter --> "+mAdapter);
                }
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                Log.i(TAG, "updateEventsSchedule: (inside) mAdapter not null --> "+mAdapter);
                Log.i(TAG, "updateEventsSchedule: userPreviouslyFavouredEvents(inside) --> "+userPreviouslyFavouredEvents);

            }

            //mAdapter.notifyDataSetChanged();
        }

        //Log.i(TAG, "updateEventsSchedule: mAdapter (outside) --> "+mAdapter);
        //Log.i(TAG, "updateEventsSchedule: userPreviouslyFavouredEvents(outside) --> "+userPreviouslyFavouredEvents);

    }


    @Override
    public void showUserFavouredEventsStatus(String message) {
        GradsHubApplication.showToast(message);
    }
    //==============================================================================================


    @Override
    public void showServerErrorResponse(String message) {
        GradsHubApplication.showToast(message);
    }


    public interface OnScheduleListFragmentInteractionListener {
        void onScheduleListFragmentInteraction(Schedule item);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
