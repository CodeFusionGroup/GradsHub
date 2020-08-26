package com.codefusiongroup.gradshub.main.chats;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.main.availablegroups.AvailableGroupsListFragment;
import com.codefusiongroup.gradshub.main.availablegroups.AvailableGroupsListRecyclerViewAdapter;
import com.codefusiongroup.gradshub.model.Chat;
import com.codefusiongroup.gradshub.model.ChatMessage;
import com.codefusiongroup.gradshub.model.ResearchGroup;
import com.codefusiongroup.gradshub.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;


public class ChatsListFragment extends Fragment {

    private ProgressBar progressBar;
    private View view;
    private static User user;

    private RecyclerView recyclerView;
    private ChatsListRecyclerViewAdapter adapter;
    private ChatsListFragment.OnChatsListFragmentInteractionListener mListener;
    private ArrayList<Chat> chatsList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MainActivity mainActivity = (MainActivity) requireActivity();
        user = mainActivity.user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats_list, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        chatsList.clear(); // every time we navigate back clear list to having duplicates when we re-populate the list

        chatsList.add( new Chat("Kamo Kamo", "Hi, read on open AI", "12:45") );
        chatsList.add( new Chat("John John", "How many hidden nodes for the NN", "15:30") );
        chatsList.add( new Chat("Tshepo Tshepo", "Do it for distributed systems", "08:15") );
        chatsList.add( new Chat("Joe Doe", "read my last published article of ML", "20:00") );

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.chatsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new ChatsListRecyclerViewAdapter(chatsList, mListener);
            recyclerView.setAdapter(adapter);
        }

    }


    public static String getUserID() {
        return user.getUserID();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChatsListFragment.OnChatsListFragmentInteractionListener) {
            mListener = (ChatsListFragment.OnChatsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatsListFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnChatsListFragmentInteractionListener {
        void onChatsListFragmentInteraction(Chat item);
    }


}