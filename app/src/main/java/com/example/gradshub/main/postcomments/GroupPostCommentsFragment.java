package com.example.gradshub.main.postcomments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gradshub.R;
import com.example.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.NetworkRequestQueue;
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


    private ProgressBar progressBar;
    private View view;
    private TextInputEditText commentET;

    private Post post;
    private CommentsAdapter mAdapter;
    private static ArrayList<Comment> commentsList = new ArrayList<>();
    private String comment;


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

        progressBar = view.findViewById(R.id.progress_circular);
        commentET = view.findViewById(R.id.typeCommentET);

        TextView postDateTV = view.findViewById(R.id.postDateTV);
        TextView postCreatorTV = view.findViewById(R.id.postCreatorNameTV);
        TextView postSubjectTV = view.findViewById(R.id.postSubjectTV);
        TextView postDescriptionTV = view.findViewById(R.id.postDescriptionTV);

        postDateTV.setText(post.getPostDate());
        postCreatorTV.setText(post.getPostCreator());
        postSubjectTV.setText(post.getPostSubject());
        postDescriptionTV.setText(post.getPostDescription());

        getGroupPostComments(post);
        progressBar.setVisibility(View.VISIBLE);

        ImageButton submitCommentBtn = view.findViewById(R.id.submitCommentBtn);
        submitCommentBtn.setOnClickListener(v -> {

            comment = commentET.getText().toString().trim();
            if ( isValidInput() ) {

                ResearchGroup researchGroup = MyGroupsProfileFragment.getGroup();
                User user = MyGroupsProfileFragment.getUser();
                String fullName = user.getFirstName() + " " + user.getLastName();

                String commentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                Comment userComment = new Comment();
                userComment.setCommentCreator(fullName);
                userComment.setComment(comment);
                userComment.setCommentDate(commentDate);

                insertGroupPostComment(user, researchGroup, post, userComment);

                commentET.setText("");
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


    private void serverGetGroupPostCommentsResponse(JSONObject response) {

        try {

            commentsList.clear(); // clear to avoid duplicates
            String statusCode = response.getString("success");

            // no comments for post
            if(statusCode.equals("0")) {
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

                progressBar.setVisibility(View.GONE);
                ListView commentsListView = view.findViewById(R.id.commentsListView);
                mAdapter = new CommentsAdapter(requireContext(), commentsList);
                commentsListView.setAdapter(mAdapter);

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
        params.put("group_id", researchGroup.getGroupID());
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
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
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
                // call getGroupPostComments() method to update adapter to show latest comments immediately
                getGroupPostComments(post);
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
