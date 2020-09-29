package com.codefusiongroup.gradshub.profile;

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
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private TextView mUsernameTV;
    private TextView mContactNoTV;
    private TextView mEmailTV;
    private TextView mAcademicStatusTV;

    private User mUser;
    private UserPreferences mUserPreferences;

    private boolean profileUpdatedSuccessfully = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserPreferences = UserPreferences.getInstance();
        mUser = mUserPreferences.getUserDetails( requireActivity() );

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            profileUpdatedSuccessfully = bundle.getBoolean("profile_updated");
            Log.i(TAG, "profileUpdatedSuccessfully --> "+profileUpdatedSuccessfully);
        }

    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mProgressBar = view.findViewById(R.id.progress_circular);
        mImageView = view.findViewById(R.id.img_container);
        mUsernameTV = view.findViewById(R.id.usernameTV);
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

        mUsernameTV.setText( mUser.getUsername() );
        mContactNoTV.setText( mUser.getPhoneNumber() );
        mEmailTV.setText( mUser.getEmail() );
        mAcademicStatusTV.setText( mUser.getAcademicStatus() );

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
                    Log.i(TAG, "getUpdatedUserProfile() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        JsonObject userJO = jsonObject.getAsJsonObject("user");
                        mUser = new Gson().fromJson(userJO, User.class);

                        // save details to preferences
                        mUserPreferences.saveUserDetails( mUser, requireActivity() );


                        if ( !mUser.getProfilePicture().equals("no profilePicture set") ) {
                            Uri uri = Uri.parse( mUser.getProfilePicture() );
                            Glide.with( requireActivity() ).load(uri).into(mImageView);
                        }

                        mUsernameTV.setText( mUser.getUsername() );
                        mContactNoTV.setText( mUser.getPhoneNumber() );
                        mEmailTV.setText( mUser.getEmail() );
                        mAcademicStatusTV.setText( mUser.getAcademicStatus() );

                    }
                    else {
                        // DB validation failed
                        GradsHubApplication.showToast( jsonObject.get("message").getAsString() );
                    }

                }

                else {
                    // internal server failure or something. Response is received but not necessarily a successful one.
                    GradsHubApplication.showToast( "Failed to load updated profile, please try again later." );
                    Log.i(TAG, "getUpdatedUserProfile() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast("Failed to get profile information, please try again later.");
                Log.i(TAG, "getUpdatedUserProfile() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


}
