package com.codefusiongroup.gradshub.main.chats;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.main.availablegroups.AvailableGroupsListRecyclerViewAdapter;
import com.codefusiongroup.gradshub.model.ChatMessage;
import com.codefusiongroup.gradshub.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ChatMessagesFragment extends Fragment {


    private ProgressBar progressBar;
    private View view;
    private EditText typeMessageET;

    private RecyclerView mMessageRecycler;
    private ChatMessagesAdapter mAdapter;
    private static ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();

    private String message;
    private String contactName = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            contactName = bundle.getString("contact_name");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_messages_list, container, false);

        // TODO: set action bar to contactName
        requireActivity().setTitle(contactName);

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            mMessageRecycler = view.findViewById(R.id.chatMessagesList);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new ChatMessagesAdapter(context, chatMessagesList);
            mMessageRecycler.setAdapter(mAdapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        typeMessageET = view.findViewById(R.id.typeMessageET);
        Button sendMessageBtn = view.findViewById(R.id.sendMessageBtn);

        chatMessagesList.clear();
        chatMessagesList.add( new ChatMessage("62", "15:12", "what's the latest on the article?") );
        chatMessagesList.add( new ChatMessage("20", "15:30", "only the first part on motion prediction modeling is drafted.") );
        chatMessagesList.add( new ChatMessage("62", "16:00", "Alright cool.") );
        chatMessagesList.add( new ChatMessage("20", "16:15", "Will update you when it's released") );


        sendMessageBtn.setOnClickListener(v -> {

            message = typeMessageET.getText().toString().trim();

            if ( isValidInput() ) {

                MainActivity mainActivity = (MainActivity) requireActivity();
                User user = mainActivity.user;
                String messageCreator = user.getUserID();

                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                String messageTime = dateTime.substring(dateTime.indexOf(" "));

                chatMessagesList.add( new ChatMessage(messageCreator, messageTime, message) );
                mAdapter.notifyDataSetChanged();

                typeMessageET.setText("");

            }

        });


    }


    private boolean isValidInput() {

        if (message.isEmpty()) {
            typeMessageET.setError("Not a valid message!");
            typeMessageET.requestFocus();
            return false;
        }

//        int maxCharLength = 160;
//        if (message.length() > maxCharLength) {
//            typeMessageET.setError("Exceeded the maximum number of characters allowed!");
//            typeMessageET.requestFocus();
//            return false;
//        }

        return true;
    }


}