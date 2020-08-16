package com.codefusiongroup.gradshub.main.postcomments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codefusiongroup.gradshub.R;

import java.util.ArrayList;


public class CommentsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Comment> commentsList;

    public class ViewHolder {

        TextView commentDateTV;
        TextView commentCreatorTV;
        TextView commentTV;

    }

    public CommentsAdapter(Context context, ArrayList<Comment> commentsList){
        inflater = LayoutInflater.from(context);
        this.commentsList = commentsList;
    }

    @Override
    public int getCount() {
        return commentsList.size();
    }


    @Override
    public Comment getItem(int position) {
        return commentsList.get(position);
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
            convertView = inflater.inflate(R.layout.fragment_my_groups_comment_item, null);

            holder.commentDateTV = convertView.findViewById(R.id.commentDateTV);
            holder.commentCreatorTV = convertView.findViewById(R.id.commentCreatorTV);
            holder.commentTV = convertView.findViewById(R.id.commentTV);

            convertView.setTag(holder);

        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.commentDateTV.setText(commentsList.get(position).getCommentDate());
        holder.commentCreatorTV.setText(commentsList.get(position).getCommentCreator());
        holder.commentTV.setText(commentsList.get(position).getComment());

        return convertView;

    }

}
