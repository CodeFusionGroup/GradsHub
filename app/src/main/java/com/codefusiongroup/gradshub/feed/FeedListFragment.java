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

    private View mView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FeedListRecyclerViewAdapter mAdapter;

    // listener that keeps track of which post is liked in the feed
    private FeedListRecyclerViewAdapter.OnPostItemLikedListener onPostItemLikedListener;

    // listener that keeps track of which post the user wishes to comment on
    private FeedListRecyclerViewAdapter.OnPostItemCommentListener onPostItemCommentListener;

    // listener that keeps track of which post PDF the user wants to download
    private FeedListRecyclerViewAdapter.OnPostPDFDownloadListener onPostPDFDownloadListener;

    private static User mUser;
    private List<Post> mLatestPosts = new ArrayList<>();
    private static ArrayList<String> userAlreadyLikedPosts = new ArrayList<>(); // values are post IDs
    private static ArrayList<String> userCurrentlyLikedPosts = new ArrayList<>(); // values are post IDs


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
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        // progress bar and swipe refresh layout must be initialised before these network calls
        assert mUser != null;
        fetchFeedLatestPosts( mUser.getUserID() );
        getUserFeedLikedPosts();

        // if network request to fetch latest posts for feed fails, the user can refresh the page to
        // initiate another request
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getUserFeedLikedPosts();
            fetchFeedLatestPosts( mUser.getUserID() );
            mSwipeRefreshLayout.setRefreshing(true);
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
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            RecyclerView recyclerView = view.findViewById(R.id.feedList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new FeedListRecyclerViewAdapter(mLatestPosts, onPostItemLikedListener, onPostItemCommentListener, onPostPDFDownloadListener);
            recyclerView.setAdapter(mAdapter);
        }

    }


    private void fetchFeedLatestPosts(String userID) {

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
                    Log.d(TAG, "fetchFeedLatestPosts() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        List<Post> latestPosts = new ArrayList<>();
                        JsonArray latestPostsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: latestPostsJA) {
                            JsonObject latestPostJO = jsonElement.getAsJsonObject();

                            Post post = new Post();
                            String postDescription;

                            JsonElement postFileJE = latestPostJO.get("POST_FILE");
                            // check if post is for a pdf file
                            if( postFileJE != null && !postFileJE.isJsonNull() ) {
                                postDescription = latestPostJO.get("POST_FILE").getAsString();
                                String postFileName = latestPostJO.get("POST_FILE_NAME").getAsString();
                                post.setPostFileName(postFileName);

                            }
                            // post description is for a normal link
                            else {
                                postDescription = latestPostJO.get("POST_URL").getAsString();
                            }

                            JsonElement postLikesCountJE = latestPostJO.get("NO_OF_LIKES");
                            int noLikes = 0;
                            if (postLikesCountJE != null && !postLikesCountJE.isJsonNull()) {
                                noLikes = Integer.parseInt(latestPostJO.get("NO_OF_LIKES").getAsString());
                            }

                            JsonElement postCommentsCountJE = latestPostJO.get("NO_OF_COMMENTS");
                            int noComments = 0;
                            if (postCommentsCountJE != null && !postCommentsCountJE.isJsonNull()) {
                                noComments = Integer.parseInt(latestPostJO.get("NO_OF_COMMENTS").getAsString());
                            }

                            String firstName = latestPostJO.get("USER_FNAME").getAsString();
                            String lastName = latestPostJO.get("USER_LNAME").getAsString();
                            String postCreator = firstName + " "+ lastName;
                            String postSubject = latestPostJO.get("POST_TITLE").getAsString();
                            String postDate = latestPostJO.get("POST_DATE").getAsString();
                            String postID = latestPostJO.get("GROUP_POST_ID").getAsString();

                            //TODO: needs id from php file
                            //String groupID = latestPostJO.get("GROUP_ID").getAsString();

                            post.setPostID(postID);
                            post.setPostDate(postDate);
                            post.setPostCreator(postCreator);
                            post.setPostSubject(postSubject);
                            post.setPostDescription(postDescription);
                            post.setPostLikesCount(noLikes);
                            post.setPostCommentsCount(noComments);

                            //Post post = new Gson().fromJson(latestPostJO, Post.class);
                            latestPosts.add(post);
                        }
                        // mAdapter must point to the same object mLatestPosts otherwise recycler view won't update
                        mLatestPosts.clear();
                        mLatestPosts.addAll(latestPosts);
                        mAdapter.notifyDataSetChanged();
                    }

                    else {
                        GradsHubApplication.showToast("no latest posts exist yet.");
                    }
                }

                else {
                    GradsHubApplication.showToast("Failed to load feed, please swipe to refresh page or try again later.");
                    Log.d(TAG, "fetchFeedLatestPosts() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if ( mSwipeRefreshLayout.isRefreshing() ) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast("Failed to load feed, please swipe to refresh page or try again later.");
                Log.d(TAG, "fetchFeedLatestPosts() --> onFailure executed, error: ", t);
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

            Log.d(TAG, "userAlreadyLikedPosts size: "+userAlreadyLikedPosts.size());
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
