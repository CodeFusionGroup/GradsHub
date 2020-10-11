package com.codefusiongroup.gradshub.events;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.models.Schedule;
import com.codefusiongroup.gradshub.common.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ScheduleListFragment extends Fragment {

    private static String TAG = "ScheduleListFragment";

    private View mView;
    private ScheduleListRecyclerViewAdapter adapter;
    // listener that keeps track of which event has the user clicked on
    private OnScheduleListFragmentInteractionListener listener;

    ScheduleListRecyclerViewAdapter.OnScheduleItemFavouredListener onScheduleItemFavouredListener;
    ScheduleListRecyclerViewAdapter.OnScheduleItemUnFavouredListener onScheduleItemUnFavouredListener;

    private List<Schedule> eventsSchedule = new ArrayList<>();
    private static List<String> userUnFavouredEvents = new ArrayList<>();
    private static List<String> userCurrentlyFavouredEvents = new ArrayList<>();
    private static List<String> userPreviouslyFavouredEvents = new ArrayList<>();

    //private User user;
    private MainActivity mainActivity;
    private EventsViewModel eventsViewModel;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScheduleListFragmentInteractionListener) {
            listener = (OnScheduleListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnScheduleListFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mainActivity = (MainActivity) requireActivity();
        if ( mainActivity.getScheduleList() != null ) {
            eventsSchedule = mainActivity.getScheduleList();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_schedule_item_list, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // listener that keeps track of which event is favoured by the user
         onScheduleItemFavouredListener
                = (item) -> userCurrentlyFavouredEvents.add(item.getId());

        // listener that keeps track of which event is unfavoured by the user
         onScheduleItemUnFavouredListener
                = (item) -> userUnFavouredEvents.add(item.getId());

        // called after setting the listeners above
        if (mView instanceof RelativeLayout) {
            Context context = mView.getContext();
            RecyclerView recyclerView = mView.findViewById(R.id.scheduleList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new ScheduleListRecyclerViewAdapter(eventsSchedule, listener, onScheduleItemFavouredListener, onScheduleItemUnFavouredListener);
            recyclerView.setAdapter(adapter);
        }

        eventsViewModel = new ViewModelProvider( this ).get(EventsViewModel.class);
        observeViewModel();
        eventsViewModel.getEventsStars();
        Log.d(TAG, "mainActivity.user.getUserID() = "+mainActivity.user.getUserID() );
        eventsViewModel.getPreviouslyFavouredEvents( mainActivity.user.getUserID() );

        // listen for when user presses back button from the ScheduleListFragment and use that as an
        // indication that they are finished interacting with the events schedule and if they favoured or unfavoured
        // any events we update the user's favourite events accordingly.
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener( (v, keyCode, event) -> {

            if ( keyCode == KeyEvent.KEYCODE_BACK ) {

                if ( userCurrentlyFavouredEvents.size() > 0 ) {
                    eventsViewModel.registerFavouredEvents(mainActivity.user.getUserID(), userCurrentlyFavouredEvents);
                    userCurrentlyFavouredEvents.clear();
                    userPreviouslyFavouredEvents.clear();
                }

                if ( userUnFavouredEvents.size() > 0 ) {
                    eventsViewModel.unRegisterFavouredEvents(mainActivity.user.getUserID(), userUnFavouredEvents);
                    userUnFavouredEvents.clear();
                    userPreviouslyFavouredEvents.clear();
                }
            }

            return false;
        });

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        eventsViewModel = new ViewModelProvider( this ).get(EventsViewModel.class);
//        observeViewModel();
//        eventsViewModel.getEventsStars();
//        eventsViewModel.getPreviouslyFavouredEvents( mainActivity.user.getUserID() );

//        // Obtain the EventsViewModel component
//        eventsViewModel = new ViewModelProvider( this ).get(EventsViewModel.class);
//        eventsViewModel.fetchEventsStars();
//
//        if (favouredEventsNetworkFailed || mainActivity.areFavouredEventsUpdated()) {
//            Log.d(TAG, "mainActivity favoured events have not change, state change is still: "+mainActivity.areFavouredEventsUpdated());
//            Log.d(TAG, "MainActivity call to fetch user's favoured events was unsuccessful, initiating new request...");
//            eventsViewModel.fetchPreviouslyFavouredEvents(user.getUserID());
//        }
//
//        // observe changes to live data objects in EventsViewModel
//        observeViewModel();
    }


    private void observeViewModel() {

        // FETCHES THE USER'S FAVOURED EVENTS IF THERE ARE ANY
        eventsViewModel.getPreviouslyFavouredEvents().observe(getViewLifecycleOwner(), listResource -> {

            if (listResource != null) {

                if (listResource.data != null) {

                    userPreviouslyFavouredEvents.addAll(listResource.data);
                    // set events that have been favoured by the user
                    for (Schedule event: eventsSchedule) {
                        String event_id = event.getId();
                        // search for this event_id in the userPreviouslyFavouredEvents list
                        for (String eventID: userPreviouslyFavouredEvents) {
                            if ( eventID.equals(event_id) ) {
                                event.setFavouredByUser(true);
                                break;
                            }
                        }
                    }

//                    if (mView instanceof RelativeLayout) {
//                        Context context = mView.getContext();
//                        RecyclerView recyclerView = mView.findViewById(R.id.scheduleList);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                        adapter = new ScheduleListRecyclerViewAdapter(eventsSchedule, listener, onScheduleItemFavouredListener, onScheduleItemUnFavouredListener);
//                        recyclerView.setAdapter(adapter);
//                    }

                    //if (adapter != null ) {
                        adapter.notifyDataSetChanged();
                    //}
                }
                else {
                    Log.d(TAG, "listResource.data is null");
                }
            }
        });


        // SETS NUMBER OF STARS FOR EACH EVENT
        eventsViewModel.getEventsStarsResponse().observe(getViewLifecycleOwner(), mapResource -> {

            if (mapResource != null) {

                if (mapResource.data != null) {

                    for ( Map.Entry<String, String> entry: mapResource.data.entrySet() ) {
                        String eventID = entry.getKey();
                        for (Schedule event: eventsSchedule) {
                            // condition skips events that have already been assigned their star counts
                            if ( event.getStarCount() == 0 && event.getId().equals(eventID) ) {
                                event.setStarCount( Integer.parseInt( entry.getValue() ) );
                                // break out of inner loop to prevent further checking of other events
                                // since we have already found an event with this ID.
                                break;
                            }
                        }
                    }

                    if (adapter != null ) {
                        adapter.notifyDataSetChanged();
                    }
                }
                else {
                    Log.d(TAG, "mapResource.data is null");
                }
            }
        });


        //================= HANDLES THE STAR AND UN-STAR OF AN EVENT BY THE USER ===================
        eventsViewModel.getRegisteredEventsResponse().observe(getViewLifecycleOwner(), stringResource -> {
            if (stringResource != null) {
                // NOTE: below toast message code is commented out on purpose to prevent
                // recurrence of toast message when navigating back due to live data resuming observation
                //GradsHubApplication.showToast(stringResource.message);
                Log.d(TAG, "eventsViewModel.getRegisteredEventsResponse() --> "+ stringResource.message);
            }
        });


        eventsViewModel.getUnregisteredEventsResponse().observe(getViewLifecycleOwner(), stringResource -> {
            if (stringResource != null) {
                // NOTE: below toast message code is commented out on purpose to prevent
                // recurrence of toast message when navigating back due to live data resuming observation
                //GradsHubApplication.showToast(stringResource.message);
                Log.d(TAG, "eventsViewModel.getUnregisteredEventsResponse() --> "+stringResource.message);
            }
        });
        //==========================================================================================

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

        // we re-use the same layout since functionality is the same
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
                adapter.getFilter().filter(newText);
                return false;
            }

        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    //==================== METHODS CALLED IN ScheduleListRecyclerViewAdapter =======================
    public static List<String> getUserCurrentlyFavouredEvents() {
        if ( userCurrentlyFavouredEvents != null ) {
            return userCurrentlyFavouredEvents;
        }
        return null;
    }

    public static List<String> getUserPreviouslyFavouredEvents() {
        if ( userPreviouslyFavouredEvents != null ) {
            return userPreviouslyFavouredEvents;
        }
        return null;
    }

    //======= UPDATES THE FAVOURED AND UNFAVOURED EVENTS LISTS IN RESPONSE TO USER'S ACTIONS =======
    public static void removeEventFromFavouredList(String eventID) {
        userCurrentlyFavouredEvents.remove(eventID);
    }

    public static void removeEventFromPreviouslyFavouredList(String eventID) {
        userPreviouslyFavouredEvents.remove(eventID);
    }
    //==============================================================================================


    public interface OnScheduleListFragmentInteractionListener {
        void onScheduleListFragmentInteraction(Schedule item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
