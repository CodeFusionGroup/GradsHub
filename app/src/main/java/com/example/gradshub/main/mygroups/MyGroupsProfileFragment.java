package com.example.gradshub.main.mygroups;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gradshub.R;
import com.example.gradshub.main.postcomments.Comment;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MyGroupsProfileFragment extends Fragment {


    private static ResearchGroup researchGroup;
    private static User user;

    private static ArrayList<String> userAlreadyLikedPosts = new ArrayList<>(); // values are post IDs
    private static ArrayList<String> userCurrentlyLikedPosts = new ArrayList<>(); // values are post IDs
    private static List<Post> items = new ArrayList<>();

    // listener that keeps track of which post is liked in the particular group
    private GroupPostsListRecyclerViewAdapter.OnPostItemLikedListener onPostItemLikedListener;
    // listener that keeps track of which post the user wishes to comment on
    private GroupPostsListRecyclerViewAdapter.OnPostItemCommentListener onPostItemCommentListener;
    // listener that keeps track of which post has the user clicked on
    private MyGroupsProfileFragment.OnPostsListFragmentInteractionListener mListener;

    private View view;
    private RecyclerView recyclerView;
    private GroupPostsListRecyclerViewAdapter adapter;

    private ProgressBar progressBar;


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
            adapter = new GroupPostsListRecyclerViewAdapter(items, mListener, onPostItemLikedListener, onPostItemCommentListener);
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

        onPostItemLikedListener = item -> {
            userCurrentlyLikedPosts.add(item.getPostID());
        };

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

        ContentValues params = new ContentValues();
        params.put("GROUP_ID", researchGroup.getGroupID());

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/retrievegrouppost.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverGetGroupPostsResponse(output);
            }

        };
        asyncHttpPost.execute();

    }


    private void serverGetGroupPostsResponse(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");

            if (output.equals("")) {
                if (view instanceof RelativeLayout) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }

            else {

                items.clear();

                if (success.equals("0")) {
                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                    }
                    // Toast msg: this group has no posts yet.
                    Toast.makeText(requireActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                }

                else {

                    JSONArray ja = jo.getJSONArray("message");

                    for(int i = 0 ; i < ja.length(); i++) {
                        JSONObject jasonObject = (JSONObject)ja.get(i);

                        String firstName = jasonObject.getString("USER_FNAME");
                        String lastName = jasonObject.getString("USER_LNAME");
                        String postCreator = firstName + " "+ lastName;
                        String postSubject = jasonObject.getString("POST_TITLE");
                        String postDate = jasonObject.getString("POST_DATE");
                        String postDescription = jasonObject.getString("POST_ATTACHMENT_URL");
                        String postID = jasonObject.getString("GROUP_POST_ID");
                        int noLikes = Integer.parseInt(jasonObject.getString("NO_OF_LIKES"));
                        int noComments = Integer.parseInt(jasonObject.getString("NO_OF_COMMENTS"));

                        Post post = new Post();
                        post.setPostID(postID);
                        post.setPostDate(postDate);
                        post.setPostCreator(postCreator);
                        post.setPostSubject(postSubject);
                        post.setPostDescription(postDescription);
                        post.setPostLikesCount(noLikes);
                        post.setPostCommentsCount(noComments);

                        items.add(post);

                    }

                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                        Context context = view.getContext();
                        recyclerView = view.findViewById(R.id.postsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter = new GroupPostsListRecyclerViewAdapter(items, mListener, onPostItemLikedListener, onPostItemCommentListener);
                        recyclerView.setAdapter(adapter);
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getUserLikedPosts() {

        ContentValues params = new ContentValues();
        params.put("GROUP_ID", researchGroup.getGroupID());
        params.put("USER_ID", user.getUserID());

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/retrievelikesGP.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverGetUserLikedPostsResponse(output);
            }

        };
        asyncHttpPost.execute();

    }


    private void serverGetUserLikedPostsResponse(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            userAlreadyLikedPosts.clear();

            if (output.equals("")) {
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }

            // user has previously liked posts in this group
            else if (success.equals("1")) {

                JSONArray ja = jo.getJSONArray("message");

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

        ContentValues params = new ContentValues();
        params.put("USER_ID", user.getUserID());
        params.put("GROUP_ID", researchGroup.getGroupID());
        params.put("POST_ID", likedPostsIDs.toString());

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/insertlikesGP.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverInsertGroupLikedPostsResponse(output);
            }

        };
        asyncHttpPost.execute();

    }


    private void serverInsertGroupLikedPostsResponse(String output) {

        if (output.equals("")) {
            Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
        }

    }


    public static ResearchGroup getGroup() {
        return researchGroup;
    }


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
        switch (item.getItemId()) {
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
