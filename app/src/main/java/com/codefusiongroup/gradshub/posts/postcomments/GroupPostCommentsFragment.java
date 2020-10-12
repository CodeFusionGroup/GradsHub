package com.codefusiongroup.gradshub.posts.postcomments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupProfile.MyGroupsProfileFragment;
import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class GroupPostCommentsFragment extends Fragment {

    private View view;
    private TextInputEditText commentET;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Post post;
    private String comment;
    private static ArrayList<Comment> commentsList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            post = bundle.getParcelable("post_item");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_post_comments, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        progressBar = view.findViewById(R.id.progress_circular);
        commentET = view.findViewById(R.id.typeCommentET);
        ImageButton submitCommentBtn = view.findViewById(R.id.submitCommentBtn);

        TextView postDateTV = view.findViewById(R.id.postDateTV);
        TextView postCreatorTV = view.findViewById(R.id.postCreatorNameTV);
        TextView postSubjectTV = view.findViewById(R.id.postSubjectTV);

        TextView postDescriptionTV = view.findViewById(R.id.postDescriptionTV);
        TextView pdfDownloadTV = view.findViewById(R.id.pdfDownloadTV);

        postDateTV.setText(post.getPostDate());
        postCreatorTV.setText(post.getPostCreator());
        postSubjectTV.setText(post.getPostSubject());


        if(post.getPostDescription().startsWith("https://firebasestorage")) {
            pdfDownloadTV.setVisibility(View.VISIBLE);
            pdfDownloadTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(requireActivity(), "Downloading file...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            postDescriptionTV.setVisibility(View.GONE);
            postDescriptionTV.setText(post.getPostDescription());
        }


        getGroupPostComments(post);
        progressBar.setVisibility(View.VISIBLE);


        submitCommentBtn.setOnClickListener(v -> {

            comment = commentET.getText().toString().trim();
            if ( isValidInput() ) {

                ResearchGroup researchGroup = MyGroupsProfileFragment.getGroup();
                //TODO: must account for group from feed
                MainActivity mainActivity = (MainActivity) requireActivity();
                User user = mainActivity.user;
                String fullName = user.getFullName();

                String commentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                Comment userComment = new Comment();
                userComment.setCommentCreator(fullName);
                userComment.setComment(comment);
                userComment.setCommentDate(commentDate);

                insertGroupPostComment(user, researchGroup, post, userComment);

                commentET.setText("");
            }

        });


        // in the event of failed network requests for getGroupPostComments(), refresh page to make another request
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                getGroupPostComments(post);
            }
        });


    }


    private boolean isValidInput() {

        if (comment.isEmpty()) {
            commentET.setError("Not a valid comment!");
            commentET.requestFocus();
            return false;
        }

        int maxCharLength = 50;
        if (comment.length() > maxCharLength) {
            commentET.setError("Exceeded the maximum number of characters allowed!");
            commentET.requestFocus();
            return false;
        }

        return true;
    }


    private void getGroupPostComments(Post post) {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/retrievecomments.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("post_id", post.getPostID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetGroupPostCommentsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server.
                        mSwipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "failed to load comments, swipe to refresh.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetGroupPostCommentsResponse(JSONObject response) {

        try {

            commentsList.clear(); // clear to avoid duplicates in recycler view
            String statusCode = response.getString("success");

            // no comments for post
            if(statusCode.equals("0")) {

                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if(isAdded()) // Ensures the fragment is added (testing)
                    Toast.makeText(requireActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
            }

            // post has comments
            else if(statusCode.equals("1")) {

                JSONArray commentsJA = response.getJSONArray("message");

                for(int i = 0 ; i < commentsJA.length(); i++) {

                    JSONObject commentJO = (JSONObject)commentsJA.get(i);
                    String firstName = commentJO.getString("USER_FNAME");
                    String lastName = commentJO.getString("USER_LNAME");
                    String fullName = firstName + " " + lastName;
                    String comment = commentJO.getString("POST_COMMENT");
                    String commentDate = commentJO.getString("POST_COMMENT_DATE");

                    commentsList.add( new Comment(fullName, comment, commentDate) );
                }

                mSwipeRefreshLayout.setRefreshing(false);

                if (view instanceof RelativeLayout) {
                    progressBar.setVisibility(View.GONE);
                    Context context = view.getContext();
                    RecyclerView recyclerView = view.findViewById(R.id.commentsList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    GroupPostCommentsRecyclerViewAdapter adapter = new GroupPostCommentsRecyclerViewAdapter(commentsList);
                    recyclerView.setAdapter(adapter);
                }

            }

        }

        catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void insertGroupPostComment(User user, ResearchGroup researchGroup, Post post, Comment comment) {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/insertcomment.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());
        //TODO: must account for commenting from feed
        //params.put("group_id", post.getPostGroupID()); // when you comment from feed
        params.put("group_id", researchGroup.getGroupID());// when you comment from a group
        params.put("post_id", post.getPostID());
        params.put("post_date", comment.getCommentDate());
        params.put("post_comment", comment.getComment());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverInsertPostCommentResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server.
                        Toast.makeText(requireActivity(), "failed to insert your comment, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);
    }


    private void serverInsertPostCommentResponse(JSONObject response) {

        try {

            String success = response.getString("success");

            // toast msg: inserted comment
            if(success.equals("1")) {
                Toast.makeText(requireActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                //post.setPostCommentsCount(post.getPostCommentsCount()+1);
                // call getGroupPostComments() method to update comments count for that post
                getGroupPostComments(post);//TODO: fix call not fetching comments count
            }

        }

        catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // ============= TESTING CODE =========================
    // setters and getters for testing purposes
    public void setPost(Post post) {
        this.post = post;
    }
    // ====================================================

}
