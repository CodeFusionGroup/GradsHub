package com.codefusiongroup.gradshub.groups.searchGroups.exploreGroupsList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codefusiongroup.gradshub.R;

import com.codefusiongroup.gradshub.common.models.ResearchGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ExploreGroupsRecyclerViewAdapter extends RecyclerView.Adapter<ExploreGroupsRecyclerViewAdapter.ViewHolder>
                implements Filterable {


    private final List<ResearchGroup> mValues;
    private List<ResearchGroup> mValuesFull;
    private final ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener mListener;


    public ExploreGroupsRecyclerViewAdapter(List<ResearchGroup> items, ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_explore_groups_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mGroup = mValues.get(position);
        holder.mGroupListNoView.setText(String.valueOf(mValues.indexOf(mValues.get(position))+1));
        holder.mGroupNameView.setText(mValues.get(position).getGroupName());
        holder.mGroupVisibilityView.setText(mValues.get(position).getGroupVisibility());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onExploreGroupsFragmentInteraction(holder.mGroup);
                Toast.makeText(v.getContext(), "selected " + holder.mGroup.getGroupName(), Toast.LENGTH_SHORT).show();
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
        public ResearchGroup mGroup;

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