package com.codefusiongroup.gradshub.feed;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupProfile.MyGroupsProfileFragment;
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

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;

    //private RecyclerView recyclerView;
    private FeedListRecyclerViewAdapter mAdapter;

    private static ArrayList<String> userAlreadyLikedPosts = new ArrayList<>(); // values are post IDs
    private static ArrayList<String> userCurrentlyLikedPosts = new ArrayList<>(); // values are post IDs
    private List<Post> groupPosts = new ArrayList<>();

    // listener that keeps track of which post is liked in the feed
    private FeedListRecyclerViewAdapter.OnPostItemLikedListener onPostItemLikedListener;

    // listener that keeps track of which post the user wishes to comment on
    private FeedListRecyclerViewAdapter.OnPostItemCommentListener onPostItemCommentListener;

    // listener that keeps track of which post PDF the user wants to download
    private FeedListRecyclerViewAdapter.OnPostPDFDownloadListener onPostPDFDownloadListener;

    private ArrayList<Post> mLatestPosts = new ArrayList<>();

    private View mView;
    private static User mUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments()!=null) {
            mUser = getArguments().getParcelable("user");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_feed_item_list, container, false);

        groupPosts.clear();
        Post post1 = new Post("2020-10-02", "Android 11", "developer.android.com");
        Post post2 = new Post("2020-09-25", "Android 10", "developer.android.com");
        Post post3 = new Post("2020-09-10", "Android Pie", "developer.android.com");
        Post post4 = new Post("2020-09-01", "Android Oreo", "developer.android.com");
        Post post5 = new Post("2020-08-21", "Android Nougat", "developer.android.com");

        groupPosts.add(post1);
        groupPosts.add(post2);
        groupPosts.add(post3);
        groupPosts.add(post4);
        groupPosts.add(post5);

        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        //getUserFeedLikedPosts();
        //fetchLatestPosts( mUser.getUserID() );

        // if network request to fetch latest posts for feed fails, the user can refresh the page to
        // initiate another request
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            //fetchLatestPosts( mUser.getUserID() );
            //mSwipeRefreshLayout.setRefreshing(true);
        });

        onPostItemLikedListener = item -> userCurrentlyLikedPosts.add( item.getPostID() );

        onPostItemCommentListener = item -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("post_item", item);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
            navController.navigate(R.id.action_feedListFragment_to_groupPostCommentsFragment, bundle);
        };

        onPostPDFDownloadListener = item -> downloadFile(requireActivity(), item.getPostFileName(), ".pdf", DIRECTORY_DOWNLOADS, item.getPostDescription());


        // listeners must be initialised before setting adapter
        if (mView instanceof RelativeLayout) {
            Context context = mView.getContext();
            RecyclerView recyclerView = mView.findViewById(R.id.feedList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new FeedListRecyclerViewAdapter(groupPosts, onPostItemLikedListener, onPostItemCommentListener, onPostPDFDownloadListener);
            recyclerView.setAdapter(mAdapter);
        }


    }


    private void fetchLatestPosts(String userID) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        FeedAPI feedAPI = ApiProvider.getFeedApiService();
        feedAPI.getLatestPosts(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( mSwipeRefreshLayout.isRefreshing() ) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                mProgressBar.setVisibility(View.GONE);

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "fetchLatestPosts() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        List<Post> latestPosts = new ArrayList<>();
                        JsonArray latestPostsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: latestPostsJA) {
                            JsonObject latestPostJO = jsonElement.getAsJsonObject();
                            Post post = new Gson().fromJson(latestPostJO, Post.class);
                            latestPosts.add(post);
                        }

                        // mAdapter must point to the same object mLatestPosts otherwise recycler view won't update
                        mLatestPosts.clear();
                        mLatestPosts.addAll(latestPosts);
                        mAdapter.notifyDataSetChanged();
                    }
                    // no latest posts
                    else {
                        GradsHubApplication.showToast("no latest posts exist yet.");
                    }
                }

                else {
                    GradsHubApplication.showToast("Failed to show feed, please swipe to refresh page or try again later.");
                    Log.d(TAG, "fetchLatestPosts() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast("Failed to show feed, please swipe to refresh page or try again later.");
                Log.d(TAG, "fetchUserOpenChats() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


    private void getUserFeedLikedPosts() {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/retrievelikes.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", mUser.getUserID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetUserLikedPostsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // NOTE: no toast message displayed here. Except for debugging purposes can include one.
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

        // Extend timeout
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void serverGetUserLikedPostsResponse(JSONObject response) {

        try {

            userAlreadyLikedPosts.clear(); // clear to avoid duplicates in recycler view
            String statusCode = response.getString("success");

            // user has previously liked posts in this group
            if (statusCode.equals("1")) {

                JSONArray ja = response.getJSONArray("message");
                for(int i = 0 ; i < ja.length(); i++) {
                    JSONObject jasonObject = (JSONObject)ja.get(i);
                    userAlreadyLikedPosts.add(jasonObject.getString("GROUP_POST_ID"));
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static User getUser() { return mUser; }


    public static ArrayList<String> getCurrentlyLikedPosts() {

        if (userCurrentlyLikedPosts.size() == 0) {
            return null;
        }

        return userCurrentlyLikedPosts;
    }


    public static ArrayList<String> getPreviouslyLikedPosts() {

        if (userAlreadyLikedPosts.size() == 0) {
            return null;
        }

        return userAlreadyLikedPosts;
    }


    public void downloadFile(Context ctx, String fileName, String fileExtension, String destDir, String url) {

        DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(ctx, destDir, fileName ); // we didn't add fileExtension here because the file gets downloaded with .pdf appended already

        assert dm != null;
        dm.enqueue(request);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
