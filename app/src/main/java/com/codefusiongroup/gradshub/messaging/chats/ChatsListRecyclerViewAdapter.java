package com.codefusiongroup.gradshub.messaging.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.messaging.chats.ChatsListFragment.OnChatsListFragmentInteractionListener;
import com.codefusiongroup.gradshub.common.models.Chat;

import java.util.ArrayList;
import java.util.List;


public class ChatsListRecyclerViewAdapter extends RecyclerView.Adapter<ChatsListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Chat> mValuesFull;
    private final List<Chat> mValues;
    private final OnChatsListFragmentInteractionListener mListener;


    public ChatsListRecyclerViewAdapter(List<Chat> items, OnChatsListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_chat_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        //holder.mUserImageView.setImageBitmap(R.drawable.mess);
        holder.mContactNameView.setText( mValues.get(position).getContactName() );
        holder.mLatestMessageView.setText( mValues.get(position).getLatestMessage() );
        holder.mLatestMessageTimeView.setText( mValues.get(position).getLatestMessageTime() );


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onChatsListFragmentInteraction(holder.mItem);
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
        public final ImageView mUserImageView;
        public final TextView mContactNameView;
        public final TextView mLatestMessageView;
        public final TextView mLatestMessageTimeView;
        public Chat mItem;

        public ViewHolder(View view) {

            super(view);

            mView = view;
            mUserImageView = view.findViewById(R.id.userImageView);
            mContactNameView = view.findViewById(R.id.contactNameTV);
            mLatestMessageView = view.findViewById(R.id.latestMessageTV);
            mLatestMessageTimeView = view.findViewById(R.id.latestMessageTimeTV);

        }

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Chat> filteredResults;

                if(constraint == null || constraint.length() == 0) {
                    filteredResults = new ArrayList<>(mValuesFull);
                }
                else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase().trim());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mValues.clear();
                mValues.addAll((List)results.values);
                notifyDataSetChanged();
            }
        };
    }


    private List<Chat> getFilteredResults(String constraint) {
        List<Chat> results = new ArrayList<>();
        for (Chat item: mValuesFull) {
            if( item.getContactName().toLowerCase().contains(constraint) ){
                results.add(item);
            }
        }
        return results;
    }

}
