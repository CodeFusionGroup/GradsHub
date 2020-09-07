package com.codefusiongroup.gradshub.posts.postcomments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.posts.postcomments.GroupPostCommentsFragment.OnCommentsListFragmentInteractionListener;

import java.util.List;


public class GroupPostCommentsRecyclerViewAdapter extends RecyclerView.Adapter<GroupPostCommentsRecyclerViewAdapter.ViewHolder> {


    private final List<Comment> mValues;
    private final OnCommentsListFragmentInteractionListener mListener;


    public GroupPostCommentsRecyclerViewAdapter(List<Comment> items, OnCommentsListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_groups_comment_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mComment = mValues.get(position);

        holder.mCommentDateView.setText( mValues.get(position).getCommentDate() );
        holder.mCommentCreatorView.setText( mValues.get(position).getCommentCreator() );
        holder.mCommentView.setText( mValues.get(position).getComment() );


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onCommentsListFragmentInteraction(holder.mComment);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mCommentDateView;
        public final TextView mCommentCreatorView;
        public final TextView mCommentView;
        public Comment mComment;

        public ViewHolder(View view) {

            super(view);

            mView = view;
            mCommentDateView = view.findViewById(R.id.commentDateTV);
            mCommentCreatorView = view.findViewById(R.id.commentCreatorTV);
            mCommentView = view.findViewById(R.id.commentTV);

        }

    }

}
