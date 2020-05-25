package com.example.gradshub.main.home;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradshub.R;
import com.example.gradshub.main.MainActivity;
import com.example.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FeedListFragment extends Fragment {

//    RecyclerView recyclerView;
//    List<ModelPost> postList;
//    AdapterPosts adapterPosts;

//    private static List<Post> items = new ArrayList<>();
    private View view;
//    private RecyclerView recyclerView;
//    private FeedListRecyclerViewAdapter adapter;
//    private FeedListFragment.OnFeedListFragmentInteractionListener mListener;
//    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed_item_list, container, false);

//        if (view instanceof RelativeLayout) {
//            Context context = view.getContext();
//            recyclerView = view.findViewById(R.id.feedList);
//            recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            adapter = new FeedListRecyclerViewAdapter(items, mListener);
//            recyclerView.setAdapter(adapter);
//        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        progressBar = view.findViewById(R.id.progress_circular);
//        //MainActivity mainActivity = (MainActivity) requireActivity();
//        ResearchGroup researchGroup = MyGroupsProfileFragment.getGroup();
//        getGroupPosts(researchGroup);
//        //progressBar.setVisibility(View.VISIBLE);
    }



//    private void getGroupPosts(ResearchGroup researchGroup) {
//        ContentValues params = new ContentValues();
//        params.put("GROUP_ID", researchGroup.getGroupID());
//
//        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/retrievegrouppost.php", params) {
//            @SuppressLint("StaticFieldLeak")
//            @Override
//            protected void onPostExecute(String output) {
//                serverGetGroupPostsResponse(output);
//            }
//
//        };
//        asyncHttpPost.execute();
//    }
//
//
//    private void serverGetGroupPostsResponse(String output) {
//
//        try {
//
//            if(output.equals("")) {
//                if (view instanceof RelativeLayout) {
//                    progressBar.setVisibility(View.GONE);
//                }
//                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
//            }
//            else {
//
//                JSONArray ja = new JSONArray(output);
//                JSONObject jsonObject = (JSONObject)ja.get(0);
//
//                // if there are no posts for any of those private groups where this user is a member a status code is returned.
//                if(jsonObject.has("success")) {
//                    if (view instanceof RelativeLayout) {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                    JSONObject joMessage = (JSONObject)ja.get(1);
//                    // Toast message: no recent developments
//                    Toast.makeText(requireActivity(), joMessage.getString("message"), Toast.LENGTH_SHORT).show();
//                }
//
//                // get list of posts
//                else {
//                    items.clear();// clear the items so that we don't have duplicate entries in our recycle view
//                    for(int i = 0 ; i < ja.length(); i++) {
//                        JSONObject jo = (JSONObject)ja.get(i);
//                        String postID = jo.getString("POST_ID");
//                        String postCreator = jo.getString("POST_CREATOR");
//                        String postDate = jo.getString("POST_DATE");
//                        String postSubject = jo.getString("POST_TITLE");
//                        String postDescription = jo.getString("POST_URL");
//                        int postLikesCount = Integer.parseInt(jo.getString("POST_LIKES_NO"));
//                        int postCommentsCount = Integer.parseInt(jo.getString("POST_COMMENTS_NO"));
//
//                        Post post = new Post();
//                        post.setPostID(postID);
//                        post.setPostCreator(postCreator);
//                        post.setPostDate(postDate);
//                        post.setPostSubject(postSubject);
//                        post.setPostDescription(postDescription);
//                        post.setPostLikesCount(postLikesCount);
//                        post.setPostCommentsCount(postCommentsCount);
//
//                        items.add(post);
//                    }
//
//                    if (view instanceof RelativeLayout) {
//                        progressBar.setVisibility(View.GONE);
//                        Context context = view.getContext();
//                        recyclerView = view.findViewById(R.id.feedList);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                        adapter = new FeedListRecyclerViewAdapter(items, mListener);
//                        recyclerView.setAdapter(adapter);
//                    }
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        //recycler view  and its properties
//        recyclerView = view.findViewById(R.id.postsRecyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
////        layoutManager.setStackFromEnd(true);
////        layoutManager.setReverseLayout(true);
//        recyclerView.setLayoutManager(layoutManager);
//        //init post list
//        //postList = new ArrayList<>();
//        postList = new ArrayList<>();
//        loadPosts();
//        return view;
//    }


//    /*This is where we need to load post from database using the ModelPost class*/
//    private void loadPosts() {
//        //path of all the posts
//        //get data from database and add it to postlist array
//        //postList.add();
//
//
//    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_filter:
                //nothing implemented yet
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof FeedListFragment.OnFeedListFragmentInteractionListener) {
//            mListener = (FeedListFragment.OnFeedListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFeedListFragmentInteractionListener");
//        }
//    }
//
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     */
//    public interface OnFeedListFragmentInteractionListener {
//        void onFeedListFragmentInteraction(Post item);
//    }


}
