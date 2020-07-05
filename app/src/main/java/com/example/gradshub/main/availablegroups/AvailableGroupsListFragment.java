package com.example.gradshub.main.availablegroups;

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
import androidx.appcompat.widget.SearchView;
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


public class AvailableGroupsListFragment extends Fragment {

    private static List<ResearchGroup> items = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private AvailableGroupsListRecyclerViewAdapter adapter;
    private OnAvailableGroupsListFragmentInteractionListener mListener;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_available_groups_item_list, container, false);

        // ensures that the recycler view list of items are immediately visible without showing any delays if user were to
        // navigate back to this list. Otherwise the delay would be noticeable because every time the user navigates back
        // to this list, a call to the remote server would be issued causing the delay. When navigation back the view for this
        // fragment is recreated to display the items that are already on the adapter instead of fetching the list from the
        // server again.
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.availableGroupsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new AvailableGroupsListRecyclerViewAdapter(items, mListener);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress_circular);
        MainActivity mainActivity = (MainActivity) requireActivity();
        getGroupsToExplore(mainActivity.user);
        progressBar.setVisibility(View.VISIBLE);
    }


    public void getGroupsToExplore(User user) {

        ContentValues params = new ContentValues();
        params.put("USER_ID", user.getUserID());

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/availablegroups.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverGetGroupsToExploreResponse(output);
            }

        };
        asyncHttpPost.execute();

    }


    private void serverGetGroupsToExploreResponse(String output) {

        try {

            items.clear(); // clear the items so that we don't have duplicate entries in our recycle view
            if(output.equals("")) {
                if (view instanceof RelativeLayout) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                }

            }
            else {

                JSONObject jo = new JSONObject(output);
                String success = jo.getString("success");

                if(success.equals("0")) {
                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                        // Toast message: no available groups (current user belongs to all groups)
                        Toast.makeText(requireActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                }
                else if(success.equals("1")) {

                    JSONArray ja = jo.getJSONArray("message");

                    for(int i = 0 ; i < ja.length(); i++) {
                        JSONObject jsonObject = (JSONObject)ja.get(i);
                        String groupID = jsonObject.getString("GROUP_ID");
                        String groupName = jsonObject.getString("GROUP_NAME");
                        String groupVisibility = jsonObject.getString("GROUP_VISIBILITY");

                        ResearchGroup researchGroup = new ResearchGroup();
                        researchGroup.setGroupID(groupID);
                        researchGroup.setGroupName(groupName);
                        researchGroup.setGroupVisibility(groupVisibility);

                        items.add(researchGroup);
                    }

                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                        Context context = view.getContext();
                        recyclerView = view.findViewById(R.id.availableGroupsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter = new AvailableGroupsListRecyclerViewAdapter(items, mListener);
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
        menuInflater.inflate(R.menu.available_groups_menu, menu);
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
        if (context instanceof OnAvailableGroupsListFragmentInteractionListener) {
            mListener = (OnAvailableGroupsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAvailableGroupsListFragmentInteractionListener");
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
    public interface OnAvailableGroupsListFragmentInteractionListener {
        void onAvailableGroupsListFragmentInteraction(ResearchGroup item);
    }


    public static AvailableGroupsListFragment getInstance() {
        return new AvailableGroupsListFragment();
    }

}
