package com.codefusiongroup.gradshub.messaging.openChats;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.BaseView;

import java.util.ArrayList;
import java.util.List;


public class OpenChatsFragment extends Fragment implements BaseView<OpenChatsPresenter>, OpenChatsContract.IOpenChatsView {


    private static final String TAG = "OpenChatsFragment";

    private View mView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OpenChatsRecyclerViewAdapter mAdapter;
    private ArrayList<Chat> mOpenChatsList = new ArrayList<>();
    private OpenChatsFragment.OnOpenChatsFragmentInteractionListener mListener;

    private OpenChatsPresenter mPresenter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OpenChatsFragment.OnOpenChatsFragmentInteractionListener) {
            mListener = (OpenChatsFragment.OnOpenChatsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOpenChatsFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPresenter( new OpenChatsPresenter() );
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_open_chats_list, container, false);

        if (mView instanceof RelativeLayout) {
            Context context = mView.getContext();
            RecyclerView mRecyclerView = mView.findViewById(R.id.chatsList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new OpenChatsRecyclerViewAdapter(mOpenChatsList, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        mPresenter.attachView(this);
        Log.i(TAG, "open chats presenter has subscribed to view");

        MainActivity mainActivity = (MainActivity) requireActivity();
        mPresenter.initialiseUserOpenChats(mainActivity.user.getUserID());

        // if network request to fetch user's open chats fails, the user can refresh the page to
        // initiate another request
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.initialiseUserOpenChats(mainActivity.user.getUserID());
            mSwipeRefreshLayout.setRefreshing(true);
        });

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
                mAdapter.getFilter().filter(newText);
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
    public void setPresenter(OpenChatsPresenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void showUserOpenChatsResponseMsg(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void updateUserOpenChats(List<Chat> openChats) {

        hideProgressBar();
        if ( mSwipeRefreshLayout.isRefreshing() ) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        // mAdapter must point to the same object mOpenChatsList otherwise recycler view won't update
        mOpenChatsList.clear();
        mOpenChatsList.addAll(openChats);
        mAdapter.notifyDataSetChanged();

    }


    public interface OnOpenChatsFragmentInteractionListener {
        void onOpenChatsFragmentInteraction(Chat item);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mPresenter.detachView(this);
        Log.i(TAG, "open chats presenter has unsubscribed from view");
    }

}