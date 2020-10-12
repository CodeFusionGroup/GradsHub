package com.codefusiongroup.gradshub.friends;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.databinding.FragmentFriendsItemListBinding;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";

    private List<User> mFriendsList = new ArrayList<>();

    private FriendsListRecyclerViewAdapter mAdapter;
    private FriendsFragment.OnFriendsListFragmentInteractionListener mListener;

    private MainActivity mainActivity;
    private FriendsViewModel friendsViewModel;
    private FragmentFriendsItemListBinding binding;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FriendsFragment.OnFriendsListFragmentInteractionListener) {
            mListener = (FriendsFragment.OnFriendsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFriendsListFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivity = (MainActivity) requireActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate view and obtain an instance of the binding class
        binding = FragmentFriendsItemListBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        RecyclerView mRecyclerView = binding.list;
        Context context = binding.getRoot().getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new FriendsListRecyclerViewAdapter(mFriendsList, mListener);
        mRecyclerView.setAdapter(mAdapter);

        binding.refresh.setOnRefreshListener(() -> {
            friendsViewModel.getUserFriends(mainActivity.user.getUserID());
            binding.refresh.setRefreshing(true);
        });

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the FriendsViewModel component
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        friendsViewModel.getUserFriends( mainActivity.user.getUserID() );

        binding.setFriendsViewModel(friendsViewModel);

        // observe changes to live data objects in FriendsViewModel
        observeViewModel(friendsViewModel);

    }

    private void observeViewModel(FriendsViewModel viewModel) {

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
            } else {
                if (binding.refresh.isRefreshing()) {
                    binding.refresh.setRefreshing(false);
                }
                binding.progressCircular.setVisibility(View.GONE);
            }
        });


        viewModel.getUserFriendsResponse().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource != null) {

                switch (listResource.status) {

                    case API_DATA_SUCCESS:
                        if (listResource.data != null) {
                            mFriendsList.clear();
                            mFriendsList.addAll(listResource.data);
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Log.d(TAG, "listResource.data  is null");
                        }
                        break;

                    case ERROR:
                    case API_NON_DATA_SUCCESS:
                        GradsHubApplication.showToast(listResource.message);
                        break;
                }
            }
            else {
                Log.d(TAG, "listResource is null");
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.available_groups_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

        MenuItem mSearchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchItem.getActionView();
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
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public interface OnFriendsListFragmentInteractionListener {
        void onFriendsListFragmentInteraction(User user);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}