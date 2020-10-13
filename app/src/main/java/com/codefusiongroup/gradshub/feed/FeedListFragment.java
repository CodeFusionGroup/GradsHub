package com.codefusiongroup.gradshub.feed;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.databinding.FragmentFeedItemListBinding;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class FeedListFragment extends Fragment {

    private static final String TAG = "FeedListFragment";

    private User mUser;
    private FeedViewModel feedViewModel;
    private FragmentFeedItemListBinding binding;
    private FeedListRecyclerViewAdapter mAdapter;
    private List<Post> mLatestPosts = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments()!=null) {
            mUser = getArguments().getParcelable("user");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate view and obtain an instance of the binding class
        binding = FragmentFeedItemListBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // if network request to fetch latest posts for feed fails, the user can refresh the page to
        // initiate another request
        binding.refresh.setOnRefreshListener(() -> {
            if (mUser != null) {
                feedViewModel.getLatestPosts( mUser.getUserID() );
                binding.refresh.setRefreshing(true);
            }
        });


        // listener that keeps track of which post is liked in the feed
        FeedListRecyclerViewAdapter.OnPostItemLikedListener onPostItemLikedListener = post -> feedViewModel.insertFeedLikedPosts(mUser.getUserID(), post.getGroupID(), post.getPostID());

        // listener that keeps track of which post PDF the user wants to download
        FeedListRecyclerViewAdapter.OnPostPDFDownloadListener onPostPDFDownloadListener = item -> downloadFile(requireActivity(), item.getPostFileName(), DIRECTORY_DOWNLOADS, item.getPostDescription());

        // listener that keeps track of which post the user wishes to comment on
        FeedListRecyclerViewAdapter.OnPostItemCommentListener onPostItemCommentListener = item -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("post_item", item);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
            navController.navigate(R.id.action_feedListFragment_to_groupPostCommentsFragment, bundle);
        };

        // after setting listeners we set recycler view
        Context context = binding.getRoot().getContext();
        RecyclerView recyclerView = binding.feedList;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new FeedListRecyclerViewAdapter(mLatestPosts, onPostItemLikedListener, onPostItemCommentListener, onPostPDFDownloadListener);
        recyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the FeedViewModel component
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        if (mUser != null) {
            feedViewModel.getLatestPosts( mUser.getUserID() );
        }

        binding.setFeedViewModel(feedViewModel);

        // observe changes to live data objects in FeedViewModel
        observeViewModel(feedViewModel);

    }


    private void observeViewModel(FeedViewModel viewModel) {

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
            } else {
                binding.progressCircular.setVisibility(View.GONE);
            }
        });

        viewModel.getLatestPostsResponse().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource != null) {

                if ( binding.refresh.isRefreshing() ) {
                    binding.refresh.setRefreshing(false);
                }
                switch (listResource.status) {

                    case API_DATA_SUCCESS:
                        if (listResource.data != null) {
                            mLatestPosts.clear();
                            mLatestPosts.addAll(listResource.data);
                            mAdapter.notifyDataSetChanged();
                            viewModel.deregisterObserverObjects();
                        }
                        else {
                            Log.d(TAG, "listResource.data is null");
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


    public void downloadFile(Context ctx, String fileName, String destDir, String url) {
        DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(ctx, destDir, fileName );
        assert dm != null;
        dm.enqueue(request);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
