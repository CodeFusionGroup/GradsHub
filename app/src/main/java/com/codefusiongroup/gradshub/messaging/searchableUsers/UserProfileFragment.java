package com.codefusiongroup.gradshub.messaging.searchableUsers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.User;


public class UserProfileFragment extends Fragment {


    private User mSelectedUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            mSelectedUser = bundle.getParcelable("selected_user");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        ImageButton messageBtn = view.findViewById(R.id.messageBtn);
        TextView userNameTV = view.findViewById(R.id.userProfileNameTV);

        userNameTV.setText(mSelectedUser.getFullName());

        messageBtn.setOnClickListener(v -> {

            // navigate to chat messages to start a conversation
            Bundle userBundle = new Bundle();
            userBundle.putParcelable("selected_user", mSelectedUser);
            Bundle bundle = new Bundle();
            bundle.putBundle("user_bundle", userBundle);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
            navController.navigate(R.id.action_userProfileFragment_to_chatMessagesFragment, bundle);

        });

    }


}