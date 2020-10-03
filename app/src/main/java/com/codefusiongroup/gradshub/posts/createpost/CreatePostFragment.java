package com.codefusiongroup.gradshub.posts.createpost;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContextWrapper;
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

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupProfile.MyGroupsProfileFragment;
import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class CreatePostFragment extends Fragment {

    private static final String TAG = "CreatePostFragment";

    private TextView pdfBtn;
    private ProgressBar progressBar;
    private EditText postSubjectET, postDescriptionET;

    private Uri uri;// Used to upload pdf file
    private String postSubject, postDescription, displayName;
    private static final int SELECT_FILE_REQUEST_CODE = 1;

    private String mFileName;
    private String postDate;
    private String uploadedFile;
    private ResearchGroup researchGroup;
    private MainActivity mainActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        postSubjectET = view.findViewById(R.id.postSubjectET);
        postDescriptionET = view.findViewById(R.id.postDescriptionET);
        Button postBtn = view.findViewById(R.id.postBtn);
        pdfBtn = view.findViewById(R.id.pdfBtn);


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


        pdfBtn.setOnClickListener(view1 -> {

            File path = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

            if ( path != null && path.exists() ) {
                File targetFile = new File(path, mFileName);
                Uri contentUri = FileProvider.getUriForFile( requireActivity(),
                        BuildConfig.APPLICATION_ID + ".fileprovider", targetFile );

                String mime = requireActivity().getContentResolver().getType(contentUri);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(contentUri, mime);
                target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent appChooser = Intent.createChooser(target,"Open PDF");

                try {
                    startActivity(appChooser);

                } catch(ActivityNotFoundException error) {
                    GradsHubApplication.showToast("Please install a pdf reader.");
                }

            }
            else {
                GradsHubApplication.showToast("Could not open pdf file.");
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
        startActivityForResult(chooseFile, SELECT_FILE_REQUEST_CODE);
    }


    // this method is called upon selecting a pdf file from the device file system which then gets the pdf file name
    // and displays the pdf button showing the pdf file name.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == SELECT_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null ) {

            if( data.getData() != null ) {

                uri = data.getData();
                String fileExtension = getFileExtension(uri);

                if (fileExtension.equals("pdf") ) {
                    // convert to string to determine the uri scheme (file:// or content://) to get file name
                    // for now we'll only deal with content:// scheme
                    String uriString = uri.toString();
                    Log.i(TAG, "uriString: " + uriString);

                    if ( uriString.startsWith("content://") ) {

                        Cursor cursor = null;

                        try {
                            // NOTE: following projection array doesn't necessarily mean the selected files are only from the Download directory
                            String[] projection = {MediaStore.Downloads.TITLE, MediaStore.Downloads.SIZE, MediaStore.Downloads.DISPLAY_NAME};
                            cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);

                            if (cursor != null) {

                                int nameColumnExternal = cursor.getColumnIndex(MediaStore.Downloads.TITLE);
                                int nameColumnRecent = cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME);
                                int sizeColumn = cursor.getColumnIndex(MediaStore.Downloads.SIZE);

                                cursor.moveToFirst();

                                String temp = cursor.getString(nameColumnRecent);
                                if (temp != null) {
                                    displayName = temp;
                                }
                                else {
                                    displayName = cursor.getString(nameColumnExternal);
                                }

                                int fileSize = cursor.getInt(sizeColumn);

                                // firebase max file size upload is 7MB, free tier storage limit 1GiB
                                if (fileSize < 2 * 1024 * 1024) { // 2MB limit

                                    ContextWrapper cw = new ContextWrapper(GradsHubApplication.getContext());
                                    File outDir = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                                    mFileName = displayName + "." + getFileExtension(uri);
                                    File outFile = new File(outDir, mFileName);

                                    try {
                                        InputStream in = requireActivity().getContentResolver().openInputStream(uri);

                                        if (in != null) {

                                            FileOutputStream fos = new FileOutputStream(outFile);

                                            byte[] buffer = new byte[1024];
                                            int read;

                                            while ((read = in.read(buffer)) != -1) {
                                                fos.write(buffer, 0, read);
                                            }

                                            in.close();
                                            fos.flush();
                                            fos.close();

                                            File pdfFile = hasExternalStoragePdfFile(mFileName);
                                            if (pdfFile != null) {
                                                // disable and hide edit text for post description
                                                postDescriptionET.setInputType(0);
                                                postDescriptionET.setVisibility(View.GONE);

                                                // show button with pdf file name
                                                pdfBtn.setVisibility(View.VISIBLE);
                                                pdfBtn.setText(displayName);
                                            } else {
                                                GradsHubApplication.showToast("Could not retrieve selected pdf file.");
                                            }

                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    GradsHubApplication.showToast("Selected file is too large.");
                                    return;
                                }

                            }

                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }

                    }
                    else if( uriString.startsWith("file://") ) {
                        // NOTE: implementation omitted since can't test this scheme, just display toast
                        GradsHubApplication.showToast("Could not retrieve selected pdf file");
                    }
                }
                else {
                    GradsHubApplication.showToast("File must be a pdf type.");
                }

            }
            else {
                return;
            }

        }
        else {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    private File hasExternalStoragePdfFile(String pdfFileName) {

        ContextWrapper cw = new ContextWrapper( GradsHubApplication.getContext() );
        File pdfFilePath = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        if (pdfFilePath != null) {
            File pdfFile = new File(pdfFilePath, pdfFileName);
            Log.i(TAG, " hasExternalStorageFile() --> pdf file name: " + pdfFile.getName() + " exists in external app storage.");
            return pdfFile;
        }

        return null;
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
        uploadTask.addOnFailureListener(new OnFailureListener() {
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
