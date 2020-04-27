package com.example.gradshub.main.mygroups;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gradshub.R;
import com.example.gradshub.model.ResearchGroup;


public class MyGroupsProfileFragment extends Fragment {

    private ResearchGroup researchGroup;


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
        return inflater.inflate(R.layout.fragment_my_groups_information, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextView groupNameTV = view.findViewById(R.id.groupNameTV);
        groupNameTV.setText(researchGroup.getGroupName());
    }

}
