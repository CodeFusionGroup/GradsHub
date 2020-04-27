package com.example.gradshub.main.mygroups;

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

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradshub.R;
import com.example.gradshub.main.MainActivity;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyGroupsListFragment extends Fragment {

    private static List<ResearchGroup> items = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private MyGroupsListRecyclerViewAdapter adapter;
    private MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener mListener;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_groups_item_list, container, false);

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.myGroupsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyGroupsListRecyclerViewAdapter(items, mListener);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress_circular);
        MainActivity mainActivity = (MainActivity) requireActivity();
        getUserGroups(mainActivity.user);
        progressBar.setVisibility(View.VISIBLE);
    }


    private void getUserGroups(User user) {
        ContentValues params = new ContentValues();
        params.put("USER_EMAIL", user.getEmail());

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/listgroup.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverGetUserGroupsResponse(output);
            }

        };
        asyncHttpPost.execute();
    }


    private void serverGetUserGroupsResponse(String output) {

        try {

            if(output.equals("")) {
                if (view instanceof RelativeLayout) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }
            else {

                JSONArray ja = new JSONArray(output);
                JSONObject jsonObject = (JSONObject)ja.get(0);

                // if the user has not joined any groups, a status code and its message is returned
                if(jsonObject.has("success")) {
                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                    }
                    JSONObject joMessage = (JSONObject)ja.get(1);
                    // Toast message: you have not joined any groups.
                    Toast.makeText(requireActivity(), joMessage.getString("message"), Toast.LENGTH_SHORT).show();
                }

                // if the user has joined groups, get the list of research groups this user belongs to.
                else {
                    items.clear();// clear the items so that we don't have duplicate entries in our recycle view
                    for(int i = 0 ; i < ja.length(); i++) {
                        JSONObject jo = (JSONObject)ja.get(i);
                        String groupID = jo.getString("GROUP_ID");
                        String groupName = jo.getString("GROUP_NAME");
                        String groupAdmin = jo.getString("GROUP_ADMIN");
                        String groupVisibility = jo.getString("GROUP_VISIBILITY");
                        String groupInviteCode = jo.getString("GROUP_CODE");

                        ResearchGroup researchGroup = new ResearchGroup();
                        researchGroup.setGroupID(groupID);
                        researchGroup.setGroupName(groupName);
                        researchGroup.setGroupAdmin(groupAdmin);
                        researchGroup.setGroupVisibility(groupVisibility);
                        researchGroup.setGroupInviteCode(groupInviteCode);

                        items.add(researchGroup);
                    }

                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                        Context context = view.getContext();
                        recyclerView = view.findViewById(R.id.myGroupsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter = new MyGroupsListRecyclerViewAdapter(items, mListener);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.my_groups_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener) {
            mListener = (MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyGroupsListFragmentInteractionListener");
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
    public interface OnMyGroupsListFragmentInteractionListener {
        void onMyGroupsListFragmentInteraction(ResearchGroup item);
    }

}
