package com.codefusiongroup.gradshub.posts.createpost;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.BuildConfig;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupProfile.MyGroupsProfileFragment;
import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class CreatePostFragment extends Fragment {

    private ProgressBar progressBar;
    private View view;

    private EditText postSubjectET, postDescriptionET;
    private TextView pdfBtn;

    private String postSubject, postDescription, displayName;
    private static final int PICK_FILE_RESULT_CODE = 1;

    private Uri uri;// Used to upload pdf file

    private MainActivity mainActivity;
    private String postDate;
    private ResearchGroup researchGroup;

    private String uploadedFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_post, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        postSubjectET = view.findViewById(R.id.postSubjectET);
        postDescriptionET = view.findViewById(R.id.postDescriptionET);
        Button postBtn = view.findViewById(R.id.postBtn);
        pdfBtn = view.findViewById(R.id.pdfBtn);

        // POST BUTTON
        postBtn.setOnClickListener(v -> {

            postSubject = postSubjectET.getText().toString().trim();
            postDescription = postDescriptionET.getText().toString().trim();

            if ( isValidInput() ) {

                mainActivity = (MainActivity) requireActivity();
                postDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                researchGroup = MyGroupsProfileFragment.getGroup();

                progressBar.setVisibility(View.VISIBLE);

                // Post attachment is a pdf document
                if(uri != null) {
                    // Upload file to firebase storage
                    uploadFile();

                } else {
                    // Post attachment is a link
                    createPostForLink( new Post(postDate, postSubject, postDescription), mainActivity.user, researchGroup );
                }
            }
        });

        // PDF BUTTON
        pdfBtn.setOnClickListener(view1 -> {

            //TODO : open pdf and show contents of file (currently opens empty pdf file)
            File docPath = new File(requireActivity().getFilesDir(), "Download");
            File targetFile = new File(docPath, displayName);
            Uri contentUri = FileProvider.getUriForFile( requireActivity(),
                    BuildConfig.APPLICATION_ID + ".fileprovider", targetFile );

            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(contentUri, "application/pdf");
            target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent appChooser = Intent.createChooser(target,"Choose PDF Application");

            try {
                startActivity(appChooser);

            } catch(ActivityNotFoundException error) {
                // Instruct the user to install a PDF reader here, or something
                Toast.makeText(requireActivity(), "Please install a pdf reader", Toast.LENGTH_LONG).show();
            }
        });

    }


    private boolean isValidInput() {

        if (postSubject.isEmpty()) {
            postSubjectET.setError("Not a valid post subject!");
            postSubjectET.requestFocus();
            return false;
        }

        int maxCharLength = 30;
        if (postSubject.length() > maxCharLength) {
            postSubjectET.setError("Exceeded the maximum number of characters allowed!");
            postSubjectET.requestFocus();
            return false;
        }

        // only evaluated if post is not a file attachment
        if (uri == null && postDescription.isEmpty()) {
            postDescriptionET.setError("Not a valid post description!");
            postDescriptionET.requestFocus();
            return false;
        }

        return true;
    }


    private void createPostForLink(Post post, User user, ResearchGroup researchGroup) {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/create.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("group_id", researchGroup.getGroupID());
        params.put("user_id", user.getUserID());
        params.put("post_date", post.getPostDate());
        params.put("post_title", post.getPostSubject());
        params.put("post_url", post.getPostDescription());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverCreatePostForLinkResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);
    }


    private void serverCreatePostForLinkResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            // NOTE: there are currently no constraints on group properties like group names being the same or something
            // successfully created a post
            if (statusCode.equals("1")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                // navigate to the profile of this group to see the post that was created
                NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                navController.navigate(R.id.action_createPostFragment_to_myGroupProfileFragment);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.create_post_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Attachment icon
            case R.id.action_attach_file:

                // ask the user to give permission to the app to access media storage
                if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectPdfFile();
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    // this method is called to open the device file manager to pick the pdf file the user wants to upload
    private void selectPdfFile() {

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/pdf");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Select PDF");
        startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);

    }


    // this method is called upon selecting a pdf file from the device file system which then gets the pdf file name
    // and displays the pdf button showing the pdf file name.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == PICK_FILE_RESULT_CODE && resultCode == RESULT_OK && data != null ) {

            if( data.getData() != null ) {

                uri = data.getData(); // return uri of selected file
                String uriString = uri.toString();
                File file = new File(uriString);

                //TODO: fix displayName. Seems to be null when selecting file from internal storage but file appear
                // without its name displayed

                // Get the name of the file
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;

                    try {

                        cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {


                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                            // commented out Log messages cause they result in an error when displayName = null as a
                            // result of selecting a file from internal storage
                            //Log.d("File name>>>>  ",displayName);

                            // Disable url(description) editText
                            postDescriptionET.setInputType(0);
                            postDescriptionET.setVisibility(View.GONE);

                            // Display pdf button
                            pdfBtn.setVisibility(View.VISIBLE);
                            pdfBtn.setText(displayName);

                        }

                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                } else if (uriString.startsWith("file://")) {


                    displayName = file.getName();

                    //Log.d("File name>>>>  ",displayName);

                    // Disable url(description) editText
                    postDescriptionET.setInputType(0);
                    postDescriptionET.setVisibility(View.GONE);

                    // Display pdf button
                    pdfBtn.setVisibility(View.VISIBLE);
                    pdfBtn.setText(displayName);

                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    private String getFileExtension(Uri uri) {
        ContentResolver cr = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    // this method is called after onActivityForResult() finishes execution if the user has decided to post a
    // pdf file.
    private void uploadFile() {

        // Get firebase storage
        FirebaseStorage storage  = FirebaseStorage.getInstance();

        // Create a storage reference
        StorageReference storageRef = storage.getReference();

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/pdf")
                .build();

        uploadedFile = String.valueOf(System.currentTimeMillis());
        // Upload file and metadata (we make a folder "docs" in our root element)
        UploadTask uploadTask = storageRef.child( "docs/" + uploadedFile + "." + getFileExtension(uri) )
                .putFile(uri, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
            }

        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //System.out.println("Upload is paused");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // this shows while they haven't clicked POST button if upload to firebase fails. Must fix
                Toast.makeText(requireActivity(), "Failed to upload file, please try again.", Toast.LENGTH_SHORT).show();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
		        // get the url of the uploaded file.
                getFileURL(storageRef);
            }
        });

    }


    // this method is called once the file has been successfully uploaded to firebase storage to get the url that
    // points to the file on firebase storage
    private void getFileURL(StorageReference storageRef) {

        storageRef.child("docs/" + uploadedFile + ".pdf").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL , post data to server
                createPostForFile( new Post( postDate, postSubject, uri.toString(), displayName ), mainActivity.user, researchGroup );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // TODO: handle error (may have to retry operation)
                Toast.makeText(requireActivity(), "Failed to get file URL after firebase upload.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    // A group post with a file attachment
    private void createPostForFile(Post post, User user, ResearchGroup researchGroup) {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/create.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("group_id", researchGroup.getGroupID());
        params.put("user_id", user.getUserID());
        params.put("post_date", post.getPostDate());
        params.put("post_title", post.getPostSubject());
        params.put("post_file", post.getPostDescription());
        params.put("post_file_name", post.getPostFileName());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverCreatePostForFileResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance(requireActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void serverCreatePostForFileResponse(JSONObject response) {

        try {

            response.toString().replace("\\\\","");
            String success = response.getString("success");
            String message = response.getString("message");

            switch (success) {

                case "0": // Toast message: You didn't send the required values
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    break;

                case "1": // Toast message: New post created
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                    navController.navigate(R.id.action_createPostFragment_to_myGroupProfileFragment);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
