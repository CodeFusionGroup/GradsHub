package com.codefusiongroup.gradshub.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.User;

import java.util.ArrayList;
import java.util.List;


public class FriendsListRecyclerViewAdapter extends RecyclerView.Adapter<FriendsListRecyclerViewAdapter.ViewHolder>
                implements Filterable {


    private final List<User> mValues;
    private List<User> mValuesFull;
    private final FriendsFragment.OnFriendsListFragmentInteractionListener mListener;


    public FriendsListRecyclerViewAdapter(List<User> items, FriendsFragment.OnFriendsListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friends_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mUser = mValues.get(position);
        holder.mUserListNoView.setText( String.valueOf( mValues.indexOf( mValues.get(position) ) + 1 ) );
        holder.mUserNameView.setText( holder.mUser.getFullName() );

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onFriendsListFragmentInteraction(holder.mUser);
                Toast.makeText(v.getContext(), "selected " + holder.mUser.getFullName(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mUserListNoView;
        public final TextView mUserNameView;
        public User mUser;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserListNoView = view.findViewById(R.id.userListNoTV);
            mUserNameView = view.findViewById(R.id.userNameTV);
        }

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> filteredResults;

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


    private List<User> getFilteredResults(String constraint) {
        List<User> results = new ArrayList<>();

        for (User user: mValuesFull) {
            if( user.getFullName().toLowerCase().contains(constraint) ) {
                results.add(user);
            }
        }

        return results;
    }

}