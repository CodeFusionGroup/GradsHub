package com.example.gradshub.main.postcomments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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

import com.example.gradshub.R;
import com.example.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class GroupPostCommentsFragment extends Fragment {


    private static ArrayList<Comment> commentsList = new ArrayList<>();
    private TextInputEditText commentET;
    private String comment;
    private Post post;
    private CommentsAdapter mAdapter;
    private View view;

    private ProgressBar progressBar;


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

        //postDateTV.setText(post.getPostDate());
        //postCreatorTV.setText(post.getPostCreator());
        //postSubjectTV.setText(post.getPostSubject());
        //postDescriptionTV.setText(post.getPostDescription());

        getGroupPostComments(post);
        progressBar.setVisibility(View.VISIBLE);

        ImageButton submitCommentBtn = view.findViewById(R.id.submitCommentBtn);
        submitCommentBtn.setOnClickListener(v -> {

            comment = commentET.getText().toString().trim();
            if ( isValidInput() ) {

                ResearchGroup researchGroup = MyGroupsProfileFragment.getGroup();
                User user = MyGroupsProfileFragment.getUser();
                //String fullName = user.getFirstName() + " " + user.getLastName();

                String commentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                Comment userComment = new Comment();
                //userComment.setCommentCreator(fullName);
                userComment.setComment(comment);
                userComment.setCommentDate(commentDate);

                //insertGroupPostComment(user, researchGroup, post, userComment);

                commentsList.add(userComment);
                mAdapter = new CommentsAdapter(requireContext(), commentsList);
                mAdapter.notifyDataSetChanged();

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

        ContentValues params = new ContentValues();
        //params.put("POST_ID", post.getPostID());

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/retrievecommentsGP.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverGetGroupPostCommentsResponse(output);
            }

        };
        asyncHttpPost.execute();
    }


    private void serverGetGroupPostCommentsResponse(String output) {

        try {

            commentsList.clear();

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");

            if (output.equals("")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }

            else if(success.equals("0")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
            }

            else if(success.equals("1")) {

                JSONArray ja = jo.getJSONArray("message");

                for(int i = 0 ; i < ja.length(); i++) {
                    JSONObject jsonObject = (JSONObject)ja.get(i);
                    String firstName = jsonObject.getString("USER_FNAME");
                    String lastName = jsonObject.getString("USER_LNAME");
                    String fullName = firstName + " " + lastName;
                    String comment = jsonObject.getString("POST_COMMENT");
                    String commentDate = jsonObject.getString("POST_COMMENT_DATE");
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

        ContentValues params = new ContentValues();
        //params.put("USER_ID", user.getUserID());
        params.put("GROUP_ID", researchGroup.getGroupID());
        params.put("POST_ID", post.getPostID());
        params.put("POST_COMMENT_DATE", comment.getCommentDate());
        params.put("POST_COMMENT", comment.getComment());


        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/insertcommentGP.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverInsertPostCommentResponse(output);
            }

        };
        asyncHttpPost.execute();
    }


    private void serverInsertPostCommentResponse(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");

            if (output.equals("")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }

            else if(success.equals("1")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
            }

        }

        catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
