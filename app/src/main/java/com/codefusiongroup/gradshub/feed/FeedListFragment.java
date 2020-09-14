package com.codefusiongroup.gradshub.feed;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.User;


public class FeedListFragment extends Fragment {

    private View mView;
    private User mUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments()!=null) {
            mUser = getArguments().getParcelable("user");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_feed_item_list, container, false);
        return mView;
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

        switch ( item.getItemId() ) {

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
