package com.codefusiongroup.gradshub.messaging.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.messaging.users.UsersListFragment.OnUsersListFragmentInteractionListener;
import com.codefusiongroup.gradshub.common.models.User;

import java.util.ArrayList;
import java.util.List;


public class UsersListRecyclerViewAdapter extends RecyclerView.Adapter<UsersListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<User> mValuesFull;
    private final List<User> mValues;
    private final OnUsersListFragmentInteractionListener mListener;


    public UsersListRecyclerViewAdapter(List<User> items, OnUsersListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_users_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mUser = mValues.get(position);

        holder.mUserListNoView.setText( String.valueOf( mValues.indexOf(holder.mUser) + 1 ) );
        holder.mUserNameView.setText(holder.mUser.getFullName());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onUsersListFragmentInteraction(holder.mUser);
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
            if( user.getFullName().toLowerCase().contains(constraint) ){
                results.add(user);
            }
        }

        return results;
    }

}
