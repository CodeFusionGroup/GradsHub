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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.login.LoginViewModel;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;
import com.codefusiongroup.gradshub.common.repositories.UserRepositoryImpl;
import com.codefusiongroup.gradshub.databinding.FragmentFeedItemListBinding;
import com.codefusiongroup.gradshub.databinding.FragmentLoginBinding;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.utils.forms.LoginForm;
import com.codefusiongroup.gradshub.utils.validations.FormValidator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class FeedListFragment extends Fragment {

    private static final String TAG = "FeedListFragment";

    private User mUser;
    private FeedViewModel feedViewModel;
    private FragmentFeedItemListBinding binding;
    private FeedListRecyclerViewAdapter mAdapter;

    private List<Post> mLatestPosts = new ArrayList<>();
//    private List<Post> currentlyLikedPosts = new ArrayList<>();
//    private boolean isInserted = false;


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

        //Log.d(TAG, "currentlyLikedPosts size on start up: "+currentlyLikedPosts.size());
        // listener that keeps track of which post is liked in the feed
        FeedListRecyclerViewAdapter.OnPostItemLikedListener onPostItemLikedListener = new FeedListRecyclerViewAdapter.OnPostItemLikedListener() {
            @Override
            public void onPostItemLiked(Post post) {
                //currentlyLikedPosts.add(post);
                FeedRepositoryImpl.getInstance().insertUserLikedPosts(mUser.getUserID(), post.getGroupID(), post.getPostID());
                //feedViewModel.getLatestPosts( mUser.getUserID() );
                //Post post = currentlyLikedPosts.get(0);
                //currentlyLikedPosts.clear();
                //Log.d(TAG, "currentlyLikedPosts after clear prior to insert call: "+currentlyLikedPosts.size());
                //feedViewModel.insertFeedLikedPosts(mUser.getUserID(), post.getGroupID(), post.getPostID());

            }
        };

//        if (currentlyLikedPosts.size() > 0) {
//            Post post = currentlyLikedPosts.get(0);
//            currentlyLikedPosts.clear();
//            Log.d(TAG, "currentlyLikedPosts after clear prior to insert call: "+currentlyLikedPosts.size());
//            feedViewModel.insertFeedLikedPosts(mUser.getUserID(), post.getGroupID(), post.getPostID());
//        }


        // listener that keeps track of which post PDF the user wants to download
        FeedListRecyclerViewAdapter.OnPostPDFDownloadListener onPostPDFDownloadListener = item -> downloadFile(requireActivity(), item.getPostFileName(), DIRECTORY_DOWNLOADS, item.getPostDescription());

        // listener that keeps track of which post the user wishes to comment on
        FeedListRecyclerViewAdapter.OnPostItemCommentListener onPostItemCommentListener = item -> {

//            Log.d(TAG, "currentlyLikedPosts size after clicking comment button: "+currentlyLikedPosts.size());
//            if (currentlyLikedPosts.size() > 0) {
//                Post post = currentlyLikedPosts.get(0);
//                currentlyLikedPosts.clear();
//                Log.d(TAG, "currentlyLikedPosts after clear prior to insert call(for comment btn clicked): "+currentlyLikedPosts.size());
//                feedViewModel.insertFeedLikedPosts();
//
//
//            }

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
                            //viewModel.deregisterObserverObjects();
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

//        viewModel.getInsertLikesResponse().observe(getViewLifecycleOwner(), new Observer<Resource<String>>() {
//            @Override
//            public void onChanged(Resource<String> stringResource) {
//                if (stringResource != null) {
//
//                    //currentlyLikedPosts.clear();
//                    //GradsHubApplication.showToast(stringResource.message);
//                    //currentlyLikedPosts.clear();
//                    Log.d(TAG, "api response: "+stringResource.message);
//                    //viewModel.deregisterObserverObjects();
//                }
//                else {
//                    Log.d(TAG, "getInsertLikesResponse() --> stringResource is null");
//                }
//            }
//        });

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
