package com.example.gradshub.main.ui.exploregroups;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gradshub.R;


//TODO: implement functionality to display research groups the user can join and allows the user to join groups if the group
// is public otherwise request inviteCode from group admin if the group is private.
public class ExploreGroupsFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_groups, container, false);
    }

}
