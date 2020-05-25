package com.example.gradshub.main.mygroups;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gradshub.R;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.network.AsyncHTTpPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyGroupsProfileFragment extends Fragment {

    private static ResearchGroup researchGroup;

    private static List<Post> items = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private PostsListRecyclerViewAdapter adapter;
    private MyGroupsProfileFragment.OnPostsListFragmentInteractionListener mListener;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            researchGroup = bundle.getParcelable("group_item");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_groups_information, container, false);

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.postsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new PostsListRecyclerViewAdapter(items, mListener);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        TextView groupNameTV = view.findViewById(R.id.groupNameTV);

        groupNameTV.setText(researchGroup.getGroupName());

        getGroupPosts(researchGroup);
        //progressBar.setVisibility(View.VISIBLE);


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                navController.navigate(R.id.action_myGroupProfileFragment_to_createPostFragment);
            }
        });

    }


    private void getGroupPosts(ResearchGroup researchGroup) {
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

            if(output.equals("")) {
                if (view instanceof RelativeLayout) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }

            else if(success.equals("0")) {
                //progressBar.setVisibility(View.GONE);
                String message = jo.getString("message");
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
            }

            else if(success.equals("1")) {
                //progressBar.setVisibility(View.GONE);
                //switch (success) {

                    //case "1":

                        items.clear();// clear the items so that we don't have duplicate entries in our recycle view
                        JSONArray ja = jo.getJSONArray("message");

                        for(int i = 0 ; i < ja.length(); i++) {
                            JSONObject j = (JSONObject)ja.get(i);
                            String postSubject = j.getString("POST_TITLE");
                            String postDate = j.getString("POST_DATE");
                            String postDescription = j.getString("POST_URL");
                            String firstName = j.getString("USER_FNAME");
                            String lastName = j.getString("USER_LNAME");
                            String postCreator = firstName + " "+ lastName;
                            //int postLikesCount = Integer.parseInt(j.getString("POST_LIKES_NO"));
                            //int postCommentsCount = Integer.parseInt(j.getString("POST_COMMENTS_NO"));

                            Post post = new Post();
                            //post.setPostID(postID);
                            post.setPostDate(postDate);
                            post.setPostCreator(postCreator);
                            post.setPostSubject(postSubject);
                            post.setPostDescription(postDescription);
                            post.setPostLikesCount("0");
                            post.setPostCommentsCount("0");

                            items.add(post);

                        }

                        if (view instanceof RelativeLayout) {
                            progressBar.setVisibility(View.GONE);
                            Context context = view.getContext();
                            recyclerView = view.findViewById(R.id.postsList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            adapter = new PostsListRecyclerViewAdapter(items, mListener);
                            recyclerView.setAdapter(adapter);
                        }

                        //break;

               // }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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



    public static ResearchGroup getGroup() {
        return researchGroup;
    }

}
