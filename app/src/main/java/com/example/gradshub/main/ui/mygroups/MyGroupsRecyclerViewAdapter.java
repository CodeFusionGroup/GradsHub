package com.example.gradshub.main.ui.mygroups;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.gradshub.R;
import com.example.gradshub.main.ui.mygroups.MyGroupsFragment.OnListFragmentInteractionListener;
import com.example.gradshub.model.ResearchGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link ResearchGroup} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyGroupsRecyclerViewAdapter extends RecyclerView.Adapter<MyGroupsRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<ResearchGroup> mValuesFull;
    private final List<ResearchGroup> mValues;
    private final OnListFragmentInteractionListener mListener;


    public MyGroupsRecyclerViewAdapter(List<ResearchGroup> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mGroupListNoView.setText(String.valueOf(mValues.indexOf(mValues.get(position))+1));
        holder.mGroupNameView.setText(mValues.get(position).getGroupName());
        holder.mGroupVisibilityView.setText(mValues.get(position).getGroupVisibility());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    // returns how many items are in the adapter
    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView; // this is the view (card view) containing a research group item.
        public final TextView mGroupListNoView;
        public final TextView mGroupNameView;
        public final TextView mGroupVisibilityView;
        public ResearchGroup mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mGroupListNoView = view.findViewById(R.id.groupListNoTV);
            mGroupNameView = view.findViewById(R.id.groupNameTV);
            mGroupVisibilityView = view.findViewById(R.id.groupVisibilityTV);
        }

    }

    // this part of the code is what allows us to filter groups displayed in the my groups page according to group name
    // or group visibility.
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ResearchGroup> filteredResults;

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


    private List<ResearchGroup> getFilteredResults(String constraint) {
        List<ResearchGroup> results = new ArrayList<>();
        for (ResearchGroup item: mValuesFull) {
            if(item.getGroupName().toLowerCase().contains(constraint) || item.getGroupVisibility().toLowerCase().contains(constraint)){
                results.add(item);
            }
        }
        return results;
    }

}
