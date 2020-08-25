package com.codefusiongroup.gradshub.main.chats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.codefusiongroup.gradshub.main.postcomments.Comment;
import com.codefusiongroup.gradshub.model.ChatMessage;
import com.codefusiongroup.gradshub.model.ResearchGroup;
import com.codefusiongroup.gradshub.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChatMessagesFragment extends Fragment {


    private ProgressBar progressBar;
    private View view;
    private TextInputEditText messageET;

    private ChatMessagesAdapter mAdapter;
    private static ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();
    private String message;
    private String contactName = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            contactName = bundle.getString("contact_name");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_messages, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        messageET = view.findViewById(R.id.typeMessageET);
        ImageButton submitMessageBtn = view.findViewById(R.id.submitMessageBtn);
        ListView chatsListView = view.findViewById(R.id.chatsListView);

        requireActivity().setTitle(contactName);

        chatMessagesList.clear();
        chatMessagesList.add( new ChatMessage("Bob", "15:12", "what's the latest on the article?") );
        chatMessagesList.add( new ChatMessage("Alice", "15:30", "only the first part on motion sensing is drafted. testing looooooooooooooooong text") );
        chatMessagesList.add( new ChatMessage("Bob", "16:00", "Alright cool.") );
        chatMessagesList.add( new ChatMessage("Alice", "16:15", "Will update you when it's released") );

        mAdapter = new ChatMessagesAdapter(requireContext(), chatMessagesList);
        chatsListView.setAdapter(mAdapter);


        submitMessageBtn.setOnClickListener(v -> {

            message = messageET.getText().toString().trim();

            if ( isValidInput() ) {

                MainActivity mainActivity = (MainActivity) requireActivity();
                User user = mainActivity.user;
                String messageCreator = user.getFirstName() + " " + user.getLastName();

                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                String messageTime = dateTime.substring(dateTime.indexOf(" "));

                chatMessagesList.add( new ChatMessage(messageCreator, messageTime, "its an interesting read.") );
                mAdapter.notifyDataSetChanged();
                mAdapter = new ChatMessagesAdapter(requireContext(), chatMessagesList);
                chatsListView.setAdapter(mAdapter);

                messageET.setText("");

            }

        });


    }


    private boolean isValidInput() {

        if (message.isEmpty()) {
            messageET.setError("Not a valid message!");
            messageET.requestFocus();
            return false;
        }

        int maxCharLength = 160;
        if (message.length() > maxCharLength) {
            messageET.setError("Exceeded the maximum number of characters allowed!");
            messageET.requestFocus();
            return false;
        }

        return true;
    }


}