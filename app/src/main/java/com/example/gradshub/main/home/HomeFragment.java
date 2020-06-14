package com.example.gradshub.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradshub.R;
import com.example.gradshub.adapters.AdapterPosts;
import com.example.gradshub.model.ModelPost;

import java.util.ArrayList;
import java.util.List;


//TODO: implement functionality to display feeds on the home page in the form of card views.
public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //recycler view  and its properties
        recyclerView = view.findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        //init post list
        //postList = new ArrayList<>();
        postList = new ArrayList<>();

        loadPosts();


        return view;
    }
    /*This is where we need to load post from database using the ModelPost class*/
    private void loadPosts() {
        //path of all the posts
        //get data from database and add it to postlist array
        //postList.add();


    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.action_filter:
//                //nothing implemented yet
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
