package com.example.gradshub.main.mygroups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradshub.R;
import com.example.gradshub.main.mygroups.MyGroupsProfileFragment.OnPostsListFragmentInteractionListener;
import com.example.gradshub.model.Post;

import java.util.ArrayList;
import java.util.List;


public class GroupPostsListRecyclerViewAdapter extends RecyclerView.Adapter<GroupPostsListRecyclerViewAdapter.ViewHolder> {


    private List<Post> mValuesFull;
    private final List<Post> mValues;
    private ArrayList<String> userAlreadyLikedPosts = new ArrayList<>();
    private ArrayList<String> userCurrentlyLikedPosts = new ArrayList<>();
    private int likesCounter = 0;

    private final MyGroupsProfileFragment.OnPostsListFragmentInteractionListener mListener;

    private OnPostItemLikedListener onPostItemLikedListener;
    interface OnPostItemLikedListener {
        void onPostItemLiked(Post item);
    }

    private OnPostItemCommentListener onPostItemCommentListener;
    interface OnPostItemCommentListener {
        void onPostItemComment(Post item);
    }


    public GroupPostsListRecyclerViewAdapter(List<Post> items, OnPostsListFragmentInteractionListener listener,
                                             OnPostItemLikedListener onPostItemLikedListener,
                                             OnPostItemCommentListener onPostItemCommentListener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
        this.onPostItemLikedListener = onPostItemLikedListener;
        this.onPostItemCommentListener = onPostItemCommentListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_groups_post_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        holder.mPostDateView.setText( mValues.get(position).getPostDate() );
        holder.mPostCreatorView.setText( mValues.get(position).getPostCreator() );
        holder.mPostSubjectView.setText( mValues.get(position).getPostSubject() );
        holder.mPostDescriptionView.setText( mValues.get(position).getPostDescription() );
        holder.mPostNoOfLikesView.setText( String.valueOf( mValues.get(position).getPostLikesCount() ) );
        holder.mPostNoOfCommentsView.setText( String.valueOf( mValues.get(position).getPostCommentsCount() ) );

        holder.mPostLikeBtn.setOnClickListener(v -> {
            holder.mItem = mValues.get(position);

            userAlreadyLikedPosts = MyGroupsProfileFragment.getPreviouslyLikedPosts();
            userCurrentlyLikedPosts = MyGroupsProfileFragment.getCurrentlyLikedPosts();

            if ( userAlreadyLikedPosts != null && userAlreadyLikedPosts.contains(holder.mItem.getPostID()) ||
                    userCurrentlyLikedPosts != null && userCurrentlyLikedPosts.contains(holder.mItem.getPostID()) ) {
                Toast.makeText(v.getContext(), "already liked post.", Toast.LENGTH_SHORT).show();
            }

            else {

                onPostItemLikedListener.onPostItemLiked(holder.mItem);
                likesCounter++;
                holder.mItem.setPostLikesCount(likesCounter);
                holder.mPostNoOfLikesView.setText( String.valueOf( mValues.get(position).getPostLikesCount() ) );
            }

            // reset this value so that the next post item if liked, doesn't use this value to increment on it
            likesCounter = 0;

        });


        holder.mCommentBtn.setOnClickListener(v -> onPostItemCommentListener.onPostItemComment(holder.mItem));


        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onPostsListFragmentInteraction(holder.mItem);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mPostDateView;
        public final TextView mPostCreatorView;
        public final TextView mPostSubjectView;
        public final TextView mPostDescriptionView;
        public final TextView mPostNoOfLikesView;
        public final TextView mPostNoOfCommentsView;
        public final ImageButton mPostLikeBtn;
        public final ImageButton mPostCommentBtn;
        public final Button mCommentBtn;
        public Post mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPostDateView = view.findViewById(R.id.postDateTV);
            mPostCreatorView = view.findViewById(R.id.postCreatorNameTV);
            mPostSubjectView = view.findViewById(R.id.postSubjectTV);
            mPostDescriptionView = view.findViewById(R.id.postDescriptionTV);
            mPostNoOfLikesView = view.findViewById(R.id.postLikesCountTV);
            mPostNoOfCommentsView = view.findViewById(R.id.postCommentsCountTV);
            mPostLikeBtn = view.findViewById(R.id.postLikeBtn);
            mPostCommentBtn = view.findViewById(R.id.postCommentBtn);
            mCommentBtn = view.findViewById(R.id.commentBtn);
        }

    }

}
