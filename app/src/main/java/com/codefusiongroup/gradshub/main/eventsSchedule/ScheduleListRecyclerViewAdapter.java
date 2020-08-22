package com.codefusiongroup.gradshub.main.eventsSchedule;

import android.graphics.Color;
import android.util.Log;
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
import com.codefusiongroup.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.codefusiongroup.gradshub.model.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ScheduleListRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Schedule> mValuesFull;
    private final List<Schedule> mValues;
    private HashMap<String, Boolean> userPreviouslyVotedEvents = new HashMap<>();
    private HashMap<String, Boolean> userCurrentlyVotedEvents = new HashMap<>();

    private ArrayList<String> userPreviouslyFavouredEvents = new ArrayList<>();
    private ArrayList<String> userCurrentlyFavouredEvents = new ArrayList<>();

    private final ScheduleListFragment.OnScheduleListFragmentInteractionListener mListener;

    private OnScheduleItemVotedListener onScheduleItemVotedListener;
    interface OnScheduleItemVotedListener {
        void onScheduleItemVoted(Schedule item, Boolean value);
    }


    private OnScheduleItemFavouredListener onScheduleItemFavouredListener;
    interface OnScheduleItemFavouredListener {
        void onScheduleItemFavoured(Schedule item);
    }


    public ScheduleListRecyclerViewAdapter(List<Schedule> items, OnScheduleListFragmentInteractionListener listener,
                                           OnScheduleItemVotedListener onScheduleItemVotedListener,
                                           OnScheduleItemFavouredListener onScheduleItemFavouredListener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
        this.onScheduleItemVotedListener = onScheduleItemVotedListener;
        this.onScheduleItemFavouredListener = onScheduleItemFavouredListener;
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

        userPreviouslyVotedEvents = ScheduleListFragment.getUserPreviouslyVotedEvents();
        userPreviouslyFavouredEvents = ScheduleListFragment.getUserPreviouslyFavouredEvents();


        // TODO: fix logic for highlighting user's already favoured events
        if ( userPreviouslyFavouredEvents != null ) {

            for (String eventId: userPreviouslyFavouredEvents) {

                if ( eventId.equals(holder.mItem.getId()) ) {
                    holder.mFavouriteBtnView.setImageResource(R.drawable.ic_fav_filled);
                    holder.mFavouriteBtnView.setColorFilter(Color.rgb(255,223,0));
                    break;
                }

            }

        }


        holder.mUpVoteBtnView.setOnClickListener(v -> {

            userCurrentlyVotedEvents = ScheduleListFragment.getUserCurrentlyVotedEvents();
            holder.mItem = mValues.get(position);

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

            userCurrentlyVotedEvents = ScheduleListFragment.getUserCurrentlyVotedEvents();
            holder.mItem = mValues.get(position);

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


        holder.mFavouriteBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userCurrentlyFavouredEvents = ScheduleListFragment.getUserCurrentlyFavouredEvents();

                if ( userPreviouslyFavouredEvents != null && userPreviouslyFavouredEvents.contains(holder.mItem.getId()) ||
                        userCurrentlyFavouredEvents != null && userCurrentlyFavouredEvents.contains(holder.mItem.getId()) ) {
                    Toast.makeText(v.getContext(), "already favoured event.", Toast.LENGTH_SHORT).show();
                }

                else {

                    onScheduleItemFavouredListener.onScheduleItemFavoured(holder.mItem);
                    Toast.makeText(v.getContext(), "favoured event", Toast.LENGTH_SHORT).show();
                    holder.mFavouriteBtnView.setImageResource(R.drawable.ic_fav_filled);
                    holder.mFavouriteBtnView.setColorFilter(Color.rgb(255,223,0));

                }

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
        public final ImageButton mFavouriteBtnView;
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
            mFavouriteBtnView = view.findViewById(R.id.favouriteBtn);
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
