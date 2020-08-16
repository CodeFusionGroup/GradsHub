package com.codefusiongroup.gradshub.main.createpost;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.codefusiongroup.gradshub.model.Post;
import com.codefusiongroup.gradshub.model.ResearchGroup;
import com.codefusiongroup.gradshub.model.User;
import com.codefusiongroup.gradshub.network.MultipartRequest;
import com.codefusiongroup.gradshub.network.NetworkRequestQueue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class CreatePostFragment extends Fragment {

    private ProgressBar progressBar;
    private EditText postSubjectET;
    private TextInputEditText postDescriptionET;
    private TextView fileAttachmentTV;
    private View view;
    private Button pdfBtn;

    private String postSubject, postDescription;
    private static final int PICK_FILE_RESULT_CODE = 1;
    // Used to upload pdf file
//    private String displayName;
    private Uri uri;


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
        fileAttachmentTV = view.findViewById(R.id.fileAttachmentTV);
        postSubjectET = view.findViewById(R.id.postSubjectET);
        postDescriptionET = view.findViewById(R.id.postDescriptionET);
        Button postBtn = view.findViewById(R.id.postBtn);
        pdfBtn = view.findViewById(R.id.pdfBtn);
        pdfBtn.setVisibility(View.GONE);

        // POST BUTTON
        postBtn.setOnClickListener(v -> {

            postSubject = postSubjectET.getText().toString().trim();
            postDescription = postDescriptionET.getText().toString().trim();

            if ( isValidInput() ) {

                MainActivity mainActivity = (MainActivity) requireActivity();
                String postDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                ResearchGroup researchGroup = MyGroupsProfileFragment.getGroup();

//                createGroupPost( new Post(postDate, postSubject, postDescription), mainActivity.user, researchGroup );
                progressBar.setVisibility(View.VISIBLE);

                // TODO: Implement better logic for attachment is URL/Document
                // Post attachment is a pdf document
                if(uri != null) {

                    // Group post information for uploading a pdf
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("group_id", researchGroup.getGroupID());
//                    params.put("user_id", mainActivity.user.getUserID());
//                    params.put("post_title", postSubject);
//                    params.put("post_date", postDate);
//
//                    uploadPDF(displayName,uri,params);

                    // Upload file to firebase storage
                    uploadFile();


                } else {
                    // Post attachment is a URL
                    createGroupPost( new Post(postDate, postSubject, postDescription), mainActivity.user, researchGroup );
                }
            }

        });

        // PDF BUTTON
        pdfBtn.setOnClickListener(view1 -> {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(uri,"application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent appChooser = Intent.createChooser(target,"Open File");
            try{
                startActivity(appChooser);
            }catch(ActivityNotFoundException error){
                System.err.println(error);
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

        if (postDescription.isEmpty()) {
            postDescriptionET.setError("Not a valid post description!");
            postDescriptionET.requestFocus();
            return false;
        }

        return true;
    }


    private void createGroupPost(Post post, User user, ResearchGroup researchGroup) {

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
                        serverCreateGroupPostResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        //error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverCreateGroupPostResponse(JSONObject response) {

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Attachment icon
            case R.id.action_attach_file:
                selectPdfFile();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void selectPdfFile() {

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/pdf");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Select PDF");
        startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == PICK_FILE_RESULT_CODE && resultCode == RESULT_OK && data != null ) {

            if( data.getData() != null ) {

                uri = data.getData();
                String uriString = uri.toString();
                File file = new File(uriString);
                String path = file.getAbsolutePath();
                String displayName = null;

                // Get the name of the file
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            Log.d("File name>>>>  ",displayName);

                            // Display pdf button
                            pdfBtn.setVisibility(View.VISIBLE);
                            pdfBtn.setText(displayName);

                            // Disable url(description) editText
                            postDescriptionET.setInputType(0);
                        }

                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                } else if (uriString.startsWith("file://")) {
                    displayName = file.getName();
                    Log.d("File name>>>>  ",displayName);

                    // Display pdf button
                    pdfBtn.setVisibility(View.VISIBLE);
                    pdfBtn.setText(displayName);

                    // Disable url(description) editText
                    postDescriptionET.setInputType(0);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFile(){

        // Get firebase storage
        FirebaseStorage storage  = FirebaseStorage.getInstance();

        // Create a storage reference
        StorageReference storageRef = storage.getReference();

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/pdf")
                .build();

        // Upload file and metadata
        UploadTask uploadTask = storageRef.child("docs/"+uri.getLastPathSegment()).putFile(uri, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // TODO: Handle unsuccessful uploads
                Toast.makeText(requireActivity(), "Failed to upload file", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // TODO: Handle successful uploads on complete

                Toast.makeText(requireActivity(), "File uploaded", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void uploadPDF(final String pdfName, Uri pdfFile,Map<String, String> passedParams) {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/uploadfile.php";

        InputStream iStream = null;

        System.err.println("PDF file data:"+pdfFile);

        try {

            iStream = requireActivity().getContentResolver().openInputStream(pdfFile);
            assert iStream != null;
            final byte[] inputData = getBytes(iStream);

            MultipartRequest multipartRequest = new MultipartRequest( Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {

                            serverUploadPDFResponse(new String(response.data));

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    error.printStackTrace();
                }
            } )

            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = passedParams;
                    // params.put("tags", "ccccc");  add string parameters
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("file", new DataPart(pdfName ,inputData));
                    return params;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Access the Global(App) RequestQueue
            NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(multipartRequest);

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void serverUploadPDFResponse(String output) {
        System.err.println(output);
        try {
            JSONObject jsonObject = new JSONObject(output);
            jsonObject.toString().replace("\\\\","");
            String success = jsonObject.getString("success");
            String message = jsonObject.getString("message");

            switch (success) {
                case "-1": // Toast message File uploaded too big
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "1": // Toast message: Successfully uploaded file
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                    navController.navigate(R.id.action_createPostFragment_to_myGroupProfileFragment);
                    break;
            }


//            if (jsonObject.getString("status").equals("true")) {
//                Log.d("come::: >>>  ","yessssss");
//                ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
//                JSONArray dataArray = jsonObject.getJSONArray("data");
//
//                for (int i = 0; i < dataArray.length(); i++) {
//                    JSONObject dataobj = dataArray.getJSONObject(i);
////                    url = dataobj.optString("pathToFile");
////                    tv.setText(url);
//                }
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


}
