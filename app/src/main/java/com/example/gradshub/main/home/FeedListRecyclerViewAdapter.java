package com.example.gradshub.main.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradshub.R;
import com.example.gradshub.model.Post;
//import com.example.gradshub.main.home.FeedListFragment.OnFeedListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;


//public class FeedListRecyclerViewAdapter extends RecyclerView.Adapter<FeedListRecyclerViewAdapter.ViewHolder> {
//
//    private List<Post> mValuesFull;
//    private final List<Post> mValues;
//    private final FeedListFragment.OnFeedListFragmentInteractionListener mListener;
//
//
//    public FeedListRecyclerViewAdapter(List<Post> items, OnFeedListFragmentInteractionListener listener) {
//        mValues = items;
//        mListener = listener;
//        mValuesFull = new ArrayList<>(mValues);
//    }
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.mItem = mValues.get(position);
//
//        holder.mPostDateView.setText(mValues.get(position).getPostDate());
//        holder.mPostCreatorView.setText(mValues.get(position).getPostCreator());
//        holder.mPostSubjectView.setText(mValues.get(position).getPostSubject());
//
//        String link = mValues.get(position).getPostDescription();
//
//        holder.mPostDescriptionView.setText(mValues.get(position).getPostDescription());
//        holder.mPostNoOfLikesView.setText(mValues.get(position).getPostLikesCount());
//        holder.mPostNoOfCommentsView.setText(mValues.get(position).getPostCommentsCount());
//
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    mListener.onFeedListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return mValues.size();
//    }
//
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        public final View mView;
//        public final TextView mPostDateView;
//        public final TextView mPostCreatorView;
//        public final TextView mPostSubjectView;
//        public final TextView mPostDescriptionView;
//        public final TextView mPostNoOfLikesView;
//        public final TextView mPostNoOfCommentsView;
//        public Post mItem;
//
//        public ViewHolder(View view) {
//            super(view);
//            mView = view;
//            mPostDateView = view.findViewById(R.id.postDateTV);
//            mPostCreatorView = view.findViewById(R.id.postCreatorNameTV);
//            mPostSubjectView = view.findViewById(R.id.postSubjectTV);
//            mPostDescriptionView = view.findViewById(R.id.postDescriptionTV);
//            mPostNoOfLikesView = view.findViewById(R.id.postLikesCountTV);
//            mPostNoOfCommentsView = view.findViewById(R.id.postCommentsCountTV);
//        }
//
//    }
//
//}
