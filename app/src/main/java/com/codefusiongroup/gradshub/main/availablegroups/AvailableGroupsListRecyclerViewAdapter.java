package com.codefusiongroup.gradshub.main.availablegroups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.availablegroups.AvailableGroupsListFragment.OnAvailableGroupsListFragmentInteractionListener;
import com.codefusiongroup.gradshub.model.ResearchGroup;

import java.util.ArrayList;
import java.util.List;


public class AvailableGroupsListRecyclerViewAdapter extends RecyclerView.Adapter<AvailableGroupsListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<ResearchGroup> mValuesFull;
    private final List<ResearchGroup> mValues;
    private final OnAvailableGroupsListFragmentInteractionListener mListener;


    public AvailableGroupsListRecyclerViewAdapter(List<ResearchGroup> items, OnAvailableGroupsListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_available_groups_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mGroupListNoView.setText(String.valueOf(mValues.indexOf(mValues.get(position))+1));
        holder.mGroupNameView.setText(mValues.get(position).getGroupName());
        holder.mGroupVisibilityView.setText(mValues.get(position).getGroupVisibility());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onAvailableGroupsListFragmentInteraction(holder.mItem);
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
