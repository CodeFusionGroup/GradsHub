package com.codefusiongroup.gradshub.profile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "EditProfileFragment";

    private static final int SELECT_PICTURE_CODE = 1;

    private Button mSaveBtn;
    private Spinner mSpinner;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private ArrayAdapter<CharSequence> mAcademicStatusAdapter;
    private EditText mFirstNameET, mLastNameET, mEmailET, mPhoneNoET, mPasswordET, mConfirmPasswordET;

    private Uri mSelectedImageUri;
    private Uri mFirebaseImageUri;
    private String mUploadedImageName;
    private StorageReference mStorageRef;

    private User mUser;
    private String mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, mConfirmPassword;

    private boolean mImageUrlRetrievalFailed = false;
    private boolean mImageUploadFailed = false;
    private boolean mUserProfileUpdateFailed = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mUser = mainActivity.user;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // order of calling these methods is important initViewComponents() then initialiseSpinner()
        initViewComponents(view);
        initialiseSpinner(mSpinner);

        mFirstNameET.setText( mUser.getFirstName() );
        mLastNameET.setText( mUser.getLastName() );
        mEmailET.setText( mUser.getEmail() );
        mPhoneNoET.setText( mUser.getPhoneNumber() );

        String academicStatus = mUser.getAcademicStatus();
        if ( !academicStatus.equals("no academicStatus set") ) {
            int spinnerPos = mAcademicStatusAdapter.getPosition(academicStatus);
            mSpinner.setSelection(spinnerPos);
        }

        if ( !mUser.getProfilePicture().equals("no profilePicture set") ) {
            Uri uri = Uri.parse( mUser.getProfilePicture() );
            Glide.with( requireActivity() ).load(uri).into(mImageView);
        }
        else {
            Glide.with( requireActivity() ).load(R.drawable.ic_account_circle).into(mImageView);
        }


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask the user to give the app permission to access media storage
                if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectImage();
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
        });


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFirstName = mFirstNameET.getText().toString().trim();
                mLastName = mLastNameET.getText().toString().trim();
                mEmail = mEmailET.getText().toString().trim();
                mPhoneNo = mPhoneNoET.getText().toString().trim();
                mAcademicStatus = mSpinner.getSelectedItem().toString();
                mPassword = mPasswordET.getText().toString().trim();
                mConfirmPassword = mConfirmPasswordET.getText().toString().trim();

                //============ SCENARIOS TO RETRY PROFILE UPDATE IN CASE OF FAILURE ===============
                if (mImageUploadFailed) {
                    Log.d(TAG, "executed scenario --> mImageUploadFailed");
                    if ( mSelectedImageUri != null && validateInput() ) {
                        updateProfileWithImage();
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    else {
                        GradsHubApplication.showToast("Please select an image to upload.");
                    }
                }

                // means the file upload was successful (file exists in firebase storage) but failed
                // to get the URL for the file
                else if (mImageUrlRetrievalFailed) {
                    Log.d(TAG, "executed scenario --> mImageUrlRetrievalFailed");
                    getFirebaseImageURL(mStorageRef);
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                // failing point occurred on the DB server
                else if (mUserProfileUpdateFailed) {
                    Log.d(TAG, "executed scenario --> mUserProfileUpdateFailed");
                    // user included a profile picture for this profile update. We check the mFirebaseImageUri
                    // to know what information to include in the profile update of the user.
                    if (mFirebaseImageUri != null) {
                        Log.d(TAG, "profile updated with profile picture");
                        // for this case we don't call updateProfileWithImage() since that will go through the process
                        // of uploading the image to firebase again
                        updateUserProfile( mUser.getUserID(), mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, mFirebaseImageUri.toString() );
                    }
                    // user did not include a profile picture for this profile update
                    else {
                        Log.d(TAG, "profile updated without profile picture");
                        updateUserProfile( mUser.getUserID(), mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, null );
                    }
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                //==================================================================================

                // on first attempt to update profile
                else {
                    Log.d(TAG, "first attempt executed");
                    if ( validateInput() ) {

                        if (mSelectedImageUri != null) {
                            Log.d(TAG, "first attempt --> profile updated with profile picture");
                            updateProfileWithImage();
                        }
                        // user did not include a profile picture for this profile update
                        else {
                            Log.d(TAG, "first attempt --> profile updated without profile picture");
                            updateUserProfile( mUser.getUserID(), mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, null );
                        }

                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                }

            }
        });

    }


    private void initViewComponents(View view) {

        mSpinner = view.findViewById(R.id.spinner);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mImageView = view.findViewById(R.id.img_container);
        mFirstNameET = view.findViewById(R.id.firstNameET);
        mLastNameET = view.findViewById(R.id.lastNameET);
        mEmailET = view.findViewById(R.id.emailET);
        mPhoneNoET = view.findViewById(R.id.phoneNumberET);
        mPasswordET = view.findViewById(R.id.passwordET);
        mConfirmPasswordET = view.findViewById(R.id.confirmPasswordET);
        mSaveBtn = view.findViewById(R.id.updateBtn);

    }


    // this part of the code allows us to create a spinner with a drop down list of items we want to display to the user to select.
    @VisibleForTesting
    private void initialiseSpinner(Spinner spinner) {

        // style and populate spinner.
        mAcademicStatusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.ACADEMIC_STATUS, android.R.layout.simple_spinner_item);
        // spinner will have drop down layout style.
        mAcademicStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching adapter (academicStatusAdapter) to spinner.
        spinner.setAdapter(mAcademicStatusAdapter);

        spinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TextView tv = (TextView) view;

        if (position == 0) {
            // the first item (which by default is the displayed item) is set to gray to indicate its not part of the selection.
            tv.setTextColor(Color.rgb(112,128,144));
        }
        else {
            // the font colour for valid items once selected, is set to black when displayed after being selected.
            tv.setTextColor(Color.BLACK);
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // no implemetation
    }


    private boolean validateInput() {

        String academicStatusPlaceHolder = "Select your academic status here";//used as hint

        if ( !mEmail.isEmpty() ) {
            if ( !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches() ){
                mEmailET.setError("Please check that your email address is entered correctly.");
                mEmailET.requestFocus();
                return false;
            }
        }

        if ( mAcademicStatus.equals(academicStatusPlaceHolder) ) {

            TextView errorText = (TextView) mSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED); // just to highlight that this is an error message, we display it in red.
            errorText.setText(R.string.status_error); // changes the selected item text to this text.
            mSpinner.requestFocus();
            return false;

        }

        if ( !mPassword.isEmpty() ) {

            if ( mConfirmPassword.isEmpty() ) {
                mConfirmPasswordET.setError("Please confirm your password.");
                mConfirmPasswordET.requestFocus();
                return false;
            }

            else if ( !mPassword.equals(mConfirmPassword) ) {
                mConfirmPasswordET.setError("Password does not match above entered password!");
                mConfirmPasswordET.requestFocus();
                return false;
            }
        }

        return true;
    }


    private void selectImage() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult( Intent.createChooser(galleryIntent, "Select Picture"), SELECT_PICTURE_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == SELECT_PICTURE_CODE && resultCode == RESULT_OK && data != null ) {

            if (data.getData() != null) {

                mSelectedImageUri = data.getData();

                String[] projection = { MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE };

                try ( Cursor cursor = requireActivity().getContentResolver().query(mSelectedImageUri, projection, null, null, null) ) {

                    if ( cursor != null) {

                        int nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                        int sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);

                        while ( cursor.moveToNext() ) {

                            String imageName = cursor.getString(nameColumn);
                            int imageSize = cursor.getInt(sizeColumn);
                            Log.d(TAG, "onActivityResult() --> image name: "+imageName+", image disk size: "+imageSize/1024.0 +" KB");

                            if ( mSelectedImageUri.getPath() != null ) {
                                Glide.with( requireActivity() ).load(mSelectedImageUri).into(mImageView);
                            }
                            else {
                                GradsHubApplication.showToast("An error occurred while trying to retrieve the selected image.");
                                Log.d(TAG, " onActivityResult() --> selected image uri path is null");
                            }

                        }

                        cursor.close();
                    }
                    else {
                        Log.d( TAG, " onActivityResult() --> cursor is null");
                    }

                }
            }

            else {
                GradsHubApplication.showToast("You have not selected an image.");
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private String getFileExtension() {

        ContentResolver cr = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mSelectedImageUri));

    }


    private void updateProfileWithImage() {

        mStorageRef  = FirebaseStorage.getInstance().getReference();

        // image file name is user's id so that whenever the user uploads a new profile image the existing image is replaced
        // however the new profile image may have a different file extension than the already existing one
        // in which case it might not be replaced but nonetheless we limit too many pictures for the same user
        // existing in the storage.
        mUploadedImageName = mUser.getUserID() + "." + getFileExtension();
        UploadTask uploadTask = mStorageRef.child( "images/" + mUploadedImageName ).putFile(mSelectedImageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "updateProfileWithImage() --> onSuccess executed.");
                mImageUploadFailed = false;
                getFirebaseImageURL(mStorageRef);
            }
        })

        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "updateProfileWithImage() --> onFailure executed.");
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast("Something went wrong while trying to update profile, please try again.");
                mImageUploadFailed = true;
            }

        });

    }


    private void getFirebaseImageURL(StorageReference storageRef) {

        storageRef.child("images/" + mUploadedImageName ).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "getFirebaseImageURL() --> onSuccess executed");
                        mFirebaseImageUri = uri;
                        mImageUrlRetrievalFailed = false;
                        updateUserProfile( mUser.getUserID(), mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, uri.toString() );
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "getFirebaseImageURL() --> onFailure executed");
                        mProgressBar.setVisibility(View.GONE);
                        GradsHubApplication.showToast("Something went wrong while trying to update profile, please try again.");
                        mImageUrlRetrievalFailed = true;
                    }
                });

    }


    private void updateUserProfile(String userID, String firstName, String lastName, String email, String phoneNo, String academicStatus, String password, String firebaseImageUrl) {

        if ( password.equals("") ) {
            password = null;
        }
        HashMap<String, String> params = new HashMap<>();

        // among these fields password and firebaseImageUrl can be null
        params.put("user_id", userID);
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", email);
        params.put("phone_no", phoneNo);
        params.put("acad_status", academicStatus);
        params.put("password", password);
        params.put("profile_picture", firebaseImageUrl);

        ProfileAPI profileAPI = ApiProvider.getProfileApiService();
        profileAPI.updateUserProfile(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mProgressBar.setVisibility(View.GONE);

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "updateUserProfile() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        GradsHubApplication.showToast( "successfully updated profile." );
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("profile_updated", true);
                        // navigate to Profile screen
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                        navController.navigate(R.id.action_editProfileFragment_to_profileFragment, bundle);
                    }
                    else {
                        // DB validation failed
                        GradsHubApplication.showToast( jsonObject.get("message").getAsString() );
                        mUserProfileUpdateFailed = true;
                    }

                }

                else {
                    // internal server failure or something. Response is received but not necessarily a successful one.
                    GradsHubApplication.showToast( "Failed to update profile information, please try again later." );
                    mUserProfileUpdateFailed = true;
                    Log.d(TAG, "updateUserProfile() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast("Failed to update profile information, please try again later.");
                mUserProfileUpdateFailed = true;
                Log.d(TAG, "updateUserProfile() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }

}





