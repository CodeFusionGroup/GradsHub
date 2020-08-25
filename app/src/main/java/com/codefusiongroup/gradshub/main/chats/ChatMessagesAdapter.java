package com.codefusiongroup.gradshub.main.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.model.ChatMessage;

import java.util.ArrayList;


public class ChatMessagesAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ChatMessage> chatMessagesList;

    public class ViewHolder {

        TextView messageTV;
        TextView messageTimeTV;

    }

    public ChatMessagesAdapter(Context context, ArrayList<ChatMessage> chatMessagesList){
        inflater = LayoutInflater.from(context);
        this.chatMessagesList = chatMessagesList;
    }

    @Override
    public int getCount() {
        return chatMessagesList.size();
    }


    @Override
    public ChatMessage getItem(int position) {
        return chatMessagesList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_chat_message_item, null);

            holder.messageTV = convertView.findViewById(R.id.messageTV);
            holder.messageTimeTV = convertView.findViewById(R.id.messageTimeTV);

            convertView.setTag(holder);

        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.messageTV.setText(chatMessagesList.get(position).getMessageBody());
        holder.messageTimeTV.setText(chatMessagesList.get(position).getMessageTime());

        return convertView;

    }

}
