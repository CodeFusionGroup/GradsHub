package com.codefusiongroup.gradshub.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.models.Schedule;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.events.ScheduleListFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private TextView mEmailTV;
    private TextView mFullNameTV;
    private TextView mContactNoTV;
    private TextView mAcademicStatusTV;

    private User mUser;
    //private UserPreferences mUserPreferences;

    private boolean profileUpdatedSuccessfully = false;

    private ProfileFragment.OnProfileUpdateSuccessfulListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity mainActivity = (MainActivity) requireActivity();
        mUser = mainActivity.user;

        //TODO: user user details from preferences

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            profileUpdatedSuccessfully = bundle.getBoolean("profile_updated");
            Log.d(TAG, "profileUpdatedSuccessfully --> "+profileUpdatedSuccessfully);
        }

    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mProgressBar = view.findViewById(R.id.progress_circular);
        mImageView = view.findViewById(R.id.img_container);
        mFullNameTV = view.findViewById(R.id.fullNameTV);
        mContactNoTV = view.findViewById(R.id.contactNoTV);
        mEmailTV = view.findViewById(R.id.emailTV);
        mAcademicStatusTV = view.findViewById(R.id.academicStatusTV);
        Button editProfileBnt = view.findViewById(R.id.editProfileBtn);

        if ( profileUpdatedSuccessfully ) {
            getUpdatedUserProfile( mUser.getUserID() );
        }

        if ( !mUser.getProfilePicture().equals("no profilePicture set") ) {
            Uri uri = Uri.parse( mUser.getProfilePicture() );
            Glide.with( requireActivity() ).load(uri).into(mImageView);
        }
        else {
            Glide.with( requireActivity() ).load(R.drawable.ic_account_circle).into(mImageView);
        }


        mFullNameTV.setText( mUser.getFullName() );
        mContactNoTV.setText( mUser.getPhoneNumber() );
        mEmailTV.setText( mUser.getEmail() );
        String academics = "Academic Status: " + mUser.getAcademicStatus();
        mAcademicStatusTV.setText(academics);

        editProfileBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to EditProfile screen
                NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
            }
        });

    }


    private void getUpdatedUserProfile(String userID) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        ProfileAPI profileAPI = ApiProvider.getProfileApiService();
        profileAPI.getUserUpdatedProfile(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mProgressBar.setVisibility(View.GONE);

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "getUpdatedUserProfile() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        JsonObject userJO = jsonObject.getAsJsonObject("user");
                        mUser = new Gson().fromJson(userJO, User.class);

                        // save details to preferences
                        UserPreferences.getInstance().saveUserState( mUser, requireActivity() );
                        String profileUri = mUser.getProfilePicture();

                        // here we using user object to set profile so must check for 'null' in profile
                        // field.
                        if (profileUri != null) {
                            Uri uri = Uri.parse(profileUri);
                            Glide.with( requireActivity() ).load(uri).into(mImageView);
                        }

                        mFullNameTV.setText( mUser.getFullName() );
                        mContactNoTV.setText( mUser.getPhoneNumber() );
                        mEmailTV.setText( mUser.getEmail() );
                        mAcademicStatusTV.setText( mUser.getAcademicStatus() );

                        mListener.onProfileUpdateSuccessfulListener(true);
                    }
                    else {
                        // DB validation failed
                        GradsHubApplication.showToast( jsonObject.get("message").getAsString() );
                    }

                }

                else {
                    // internal server failure or something. Response is received but not necessarily a successful one.
                    GradsHubApplication.showToast( "Failed to load updated profile, please try again later." );
                    Log.d(TAG, "getUpdatedUserProfile() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast("Failed to get profile information, please try again later.");
                Log.d(TAG, "getUpdatedUserProfile() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);

        if (context instanceof ProfileFragment.OnProfileUpdateSuccessfulListener) {
            mListener = (ProfileFragment.OnProfileUpdateSuccessfulListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProfileUpdateSuccessfulListener");
        }

    }


    public interface OnProfileUpdateSuccessfulListener {
        void onProfileUpdateSuccessfulListener(boolean value);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
