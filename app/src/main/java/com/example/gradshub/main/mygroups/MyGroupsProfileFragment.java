package com.example.gradshub.main.mygroups;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gradshub.R;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.NetworkRequestQueue;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyGroupsProfileFragment extends Fragment {

    private ProgressBar progressBar;
    private View view;

    private RecyclerView recyclerView;
    private GroupPostsListRecyclerViewAdapter adapter;

    private static ResearchGroup researchGroup;
    private static User user;

    private static ArrayList<String> userAlreadyLikedPosts = new ArrayList<>(); // values are post IDs
    private static ArrayList<String> userCurrentlyLikedPosts = new ArrayList<>(); // values are post IDs
    private List<Post> groupPosts = new ArrayList<>();

    // listener that keeps track of which post is liked in the particular group
    private GroupPostsListRecyclerViewAdapter.OnPostItemLikedListener onPostItemLikedListener;
    // listener that keeps track of which post the user wishes to comment on
    private GroupPostsListRecyclerViewAdapter.OnPostItemCommentListener onPostItemCommentListener;
    // listener that keeps track of which post has the user clicked on
    private MyGroupsProfileFragment.OnPostsListFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            researchGroup = bundle.getParcelable("group_item");
            user = bundle.getParcelable("user");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_groups_information, container, false);

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.postsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new GroupPostsListRecyclerViewAdapter(groupPosts, mListener, onPostItemLikedListener, onPostItemCommentListener);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        TextView groupNameTV = view.findViewById(R.id.groupNameTV);
        groupNameTV.setText(researchGroup.getGroupName());

        if ( userCurrentlyLikedPosts.size() > 0 ) {
            insertGroupLikedPosts();
            userCurrentlyLikedPosts.clear();
        }

        getUserLikedPosts();

        getGroupPosts();
        progressBar.setVisibility(View.VISIBLE);

        onPostItemLikedListener = item -> userCurrentlyLikedPosts.add(item.getPostID());

        onPostItemCommentListener = item -> {

            Bundle bundle = new Bundle();
            bundle.putParcelable("post_item", item);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
            navController.navigate(R.id.action_myGroupProfileFragment_to_groupPostCommentsFragment, bundle);

        };

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
            navController.navigate(R.id.action_myGroupProfileFragment_to_createPostFragment);
        });

    }


    private void getGroupPosts() {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/retrieveAll.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("group_id", researchGroup.getGroupID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetGroupPostsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetGroupPostsResponse(JSONObject response) {

        try {

            groupPosts.clear(); // clear to avoid duplicates
            String statusCode = response.getString("success");

            // this group has no posts yet.
            if (statusCode.equals("0")) {
                if (view instanceof RelativeLayout) {
                    progressBar.setVisibility(View.GONE);
                }

                Toast.makeText(requireActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            }

            // this group has posts, get the posts
            else {

                JSONArray groupPostsJA = response.getJSONArray("message");

                for(int i = 0 ; i < groupPostsJA.length(); i++) {

                    JSONObject groupPostJO = (JSONObject)groupPostsJA.get(i);

                    String firstName = groupPostJO.getString("USER_FNAME");
                    String lastName = groupPostJO.getString("USER_LNAME");
                    String postCreator = firstName + " "+ lastName;
                    String postSubject = groupPostJO.getString("POST_TITLE");
                    String postDate = groupPostJO.getString("POST_DATE");
                    String postDescription = groupPostJO.getString("POST_ATTACHMENT_URL");
                    String postID = groupPostJO.getString("GROUP_POST_ID");
                    int noLikes = Integer.parseInt(groupPostJO.getString("NO_OF_LIKES"));
                    int noComments = Integer.parseInt(groupPostJO.getString("NO_OF_COMMENTS"));

                    Post post = new Post();
                    post.setPostID(postID);
                    post.setPostDate(postDate);
                    post.setPostCreator(postCreator);
                    post.setPostSubject(postSubject);
                    post.setPostDescription(postDescription);
                    post.setPostLikesCount(noLikes);
                    post.setPostCommentsCount(noComments);

                    groupPosts.add(post);

                }

                if (view instanceof RelativeLayout) {
                    progressBar.setVisibility(View.GONE);
                    Context context = view.getContext();
                    recyclerView = view.findViewById(R.id.postsList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    adapter = new GroupPostsListRecyclerViewAdapter(groupPosts, mListener, onPostItemLikedListener, onPostItemCommentListener);
                    recyclerView.setAdapter(adapter);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getUserLikedPosts() {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/retrievelikes.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("group_id", researchGroup.getGroupID());
        params.put("user_id", user.getUserID());

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
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
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

            userAlreadyLikedPosts.clear(); // clear to avoid duplicates
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


    private void insertGroupLikedPosts() {

        StringBuilder likedPostsIDs = new StringBuilder();

        for(int i = 0; i < userCurrentlyLikedPosts.size(); i++) {

            likedPostsIDs.append(userCurrentlyLikedPosts.get(i));

            if (i != userCurrentlyLikedPosts.size()-1) {
                likedPostsIDs.append(",");
            }
        }

        String url = "https://gradshub.herokuapp.com/api/GroupPost/insertlikes.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());
        params.put("group_id", researchGroup.getGroupID());
        params.put("post_id", likedPostsIDs.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //serverInsertGroupLikedPostsResponse(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverInsertGroupLikedPostsResponse(String output) { }


    public static ResearchGroup getGroup() { return researchGroup; }


    public static User getUser() { return user; }


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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.my_group_info_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {

            case R.id.action_share:

                // share invite code if the user is the one who created the group (group admin)
                if ( researchGroup.getGroupAdmin().equals(user.getEmail()) &&
                        researchGroup.getGroupVisibility().toLowerCase().equals("private") ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View alertDialogView = inflater.inflate(R.layout.dialog_private_group_display_code, null);

                    TextView groupNameTV = alertDialogView.findViewById(R.id.groupNameTV);
                    TextView inviteCodeTV = alertDialogView.findViewById(R.id.inviteCodeTV);

                    groupNameTV.setText(researchGroup.getGroupName());
                    String text = "invite code: " + researchGroup.getGroupInviteCode();
                    inviteCodeTV.setText(text);

                    builder.setView(alertDialogView)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    shareGroupInviteCode();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }

                else {
                    Toast.makeText(requireActivity(), "sorry, only admins can share invite code for private groups!", Toast.LENGTH_LONG).show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void shareGroupInviteCode() {

        Intent shareCodeIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareCodeIntent.setType("text/plain");
        String groupInviteCode = researchGroup.getGroupInviteCode();
        shareCodeIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invite Code");
        shareCodeIntent.putExtra(android.content.Intent.EXTRA_TEXT, groupInviteCode);
        startActivity(Intent.createChooser(shareCodeIntent, "share invite code via"));

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MyGroupsProfileFragment.OnPostsListFragmentInteractionListener) {
            mListener = (MyGroupsProfileFragment.OnPostsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostsListFragmentInteractionListener");
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
    public interface OnPostsListFragmentInteractionListener {
        void onPostsListFragmentInteraction(Post item);
    }


}
