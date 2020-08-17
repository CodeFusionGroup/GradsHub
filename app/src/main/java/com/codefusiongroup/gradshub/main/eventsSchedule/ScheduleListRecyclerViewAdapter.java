package com.codefusiongroup.gradshub.main.eventsSchedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.eventsSchedule.ScheduleListFragment.OnScheduleListFragmentInteractionListener;
import com.codefusiongroup.gradshub.model.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ScheduleListRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Schedule> mValuesFull;
    private final List<Schedule> mValues;
    private HashMap<String, Boolean> userPreviouslyVotedEvents = new HashMap<>();
    private HashMap<String, Boolean> userCurrentlyVotedEvents = new HashMap<>();

    private final ScheduleListFragment.OnScheduleListFragmentInteractionListener mListener;


    private OnScheduleItemVotedListener onScheduleItemVotedListener;
    interface OnScheduleItemVotedListener {
        void onScheduleItemVoted(Schedule item, Boolean value);
    }

    public ScheduleListRecyclerViewAdapter(List<Schedule> items, OnScheduleListFragmentInteractionListener listener,
                                           OnScheduleItemVotedListener onScheduleItemVotedListener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
        this.onScheduleItemVotedListener = onScheduleItemVotedListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_schedule_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);

        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mDeadlineView.setText(mValues.get(position).getDeadLine());
        holder.mTimeZoneView.setText(mValues.get(position).getTimezone());
        holder.mDateView.setText(mValues.get(position).getDate());
        holder.mPlaceView.setText(mValues.get(position).getPlace());

        holder.mVotesCountView.setText( String.valueOf( mValues.get(position).getVotesCount() ) );

        holder.mUpVoteBtnView.setOnClickListener(v -> {
            holder.mItem = mValues.get(position);

            userPreviouslyVotedEvents = ScheduleListFragment.getUserPreviouslyVotedEvents();
            userCurrentlyVotedEvents = ScheduleListFragment.getUserCurrentlyVotedEvents();

            if ( userPreviouslyVotedEvents != null && userPreviouslyVotedEvents.containsKey(holder.mItem.getId()) ) {
                Boolean value = userPreviouslyVotedEvents.get(holder.mItem.getId());
                if ( value != null && value) {
                    Toast.makeText(v.getContext(), "already up voted event.", Toast.LENGTH_SHORT).show();
                }
            }
            else if ( userCurrentlyVotedEvents != null && userCurrentlyVotedEvents.containsKey(holder.mItem.getId()) ) {
                Boolean value = userCurrentlyVotedEvents.get(holder.mItem.getId());
                if ( value != null && value) {
                    Toast.makeText(v.getContext(), "already up voted event.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                onScheduleItemVotedListener.onScheduleItemVoted(holder.mItem, true);
                // get the current number of votes for this event and add 1
                holder.mItem.setVotesCount( mValues.get(position).getVotesCount() + 1 );
                holder.mVotesCountView.setText( String.valueOf( mValues.get(position).getVotesCount() ) );
                Toast.makeText(v.getContext(), "up voted event.", Toast.LENGTH_SHORT).show();
            }

        });


        holder.mDownVoteBtnView.setOnClickListener(v -> {
            holder.mItem = mValues.get(position);

            userPreviouslyVotedEvents = ScheduleListFragment.getUserPreviouslyVotedEvents();
            userCurrentlyVotedEvents = ScheduleListFragment.getUserCurrentlyVotedEvents();

            if ( userPreviouslyVotedEvents != null && userPreviouslyVotedEvents.containsKey(holder.mItem.getId()) ) {
                Boolean value = userPreviouslyVotedEvents.get(holder.mItem.getId());
                if ( value != null && !value) {
                    Toast.makeText(v.getContext(), "already down voted event.", Toast.LENGTH_SHORT).show();
                }
            }
            else if ( userCurrentlyVotedEvents != null && userCurrentlyVotedEvents.containsKey(holder.mItem.getId()) ) {
                Boolean value = userCurrentlyVotedEvents.get(holder.mItem.getId());
                if ( value != null && !value) {
                    Toast.makeText(v.getContext(), "already down voted event.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                onScheduleItemVotedListener.onScheduleItemVoted(holder.mItem, false);
                // get the current number of votes for this event and subtract 1
                holder.mItem.setVotesCount( mValues.get(position).getVotesCount() - 1 );
                holder.mVotesCountView.setText( String.valueOf( mValues.get(position).getVotesCount() ) );
                Toast.makeText(v.getContext(), "down voted event.", Toast.LENGTH_SHORT).show();
            }

        });


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onScheduleListFragmentInteraction(holder.mItem);
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
        public final TextView mTitleView;
        public final TextView mDeadlineView;
        public final TextView mTimeZoneView;
        public final TextView mDateView;
        public final TextView mPlaceView;
        public final ImageButton mUpVoteBtnView;
        public final ImageButton mDownVoteBtnView;
        public final TextView mVotesCountView;
        public Schedule mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.titleTV);
            mDeadlineView = view.findViewById(R.id.deadlineTV);
            mTimeZoneView = view.findViewById(R.id.timezoneTV);
            mDateView = view.findViewById(R.id.dateTV);
            mPlaceView = view.findViewById(R.id.placeTV);
            mUpVoteBtnView = view.findViewById(R.id.upVoteBtn);
            mDownVoteBtnView = view.findViewById(R.id.downVoteBtn);
            mVotesCountView = view.findViewById(R.id.votesCountTV);
        }

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Schedule> filteredResults;

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


    private List<Schedule> getFilteredResults(String constraint) {
        List<Schedule> results = new ArrayList<>();
        for (Schedule item: mValuesFull) {
            if( item.getTitle().toLowerCase().contains(constraint) ) {
                results.add(item);
            }
        }
        return results;
    }

}
