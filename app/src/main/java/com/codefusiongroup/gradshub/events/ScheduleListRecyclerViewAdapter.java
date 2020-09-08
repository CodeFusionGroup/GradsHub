package com.codefusiongroup.gradshub.events;

import android.graphics.Color;
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
import com.codefusiongroup.gradshub.events.ScheduleListFragment.OnScheduleListFragmentInteractionListener;
import com.codefusiongroup.gradshub.common.models.Schedule;

import java.util.ArrayList;
import java.util.List;


public class ScheduleListRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleListRecyclerViewAdapter.ViewHolder> implements Filterable {


    private List<Schedule> mValuesFull;
    private final List<Schedule> mValues;
    private List<String> userPreviouslyFavouredEvents = new ArrayList<>();
    private List<String> userCurrentlyFavouredEvents = new ArrayList<>();

    private final ScheduleListFragment.OnScheduleListFragmentInteractionListener mListener;

    private OnScheduleItemFavouredListener onScheduleItemFavouredListener;
    interface OnScheduleItemFavouredListener {
        void onScheduleItemFavoured(Schedule item);
    }

    private OnScheduleItemUnFavouredListener onScheduleItemUnFavouredListener;
    interface OnScheduleItemUnFavouredListener {
        void onScheduleItemUnFavoured(Schedule item);
    }


    public ScheduleListRecyclerViewAdapter(List<Schedule> items, OnScheduleListFragmentInteractionListener listener,
                                           OnScheduleItemFavouredListener onScheduleItemFavouredListener,
                                           OnScheduleItemUnFavouredListener onScheduleItemUnFavouredListener) {
        mValues = items;
        mListener = listener;
        mValuesFull = new ArrayList<>(mValues);
        this.onScheduleItemFavouredListener = onScheduleItemFavouredListener;
        this.onScheduleItemUnFavouredListener = onScheduleItemUnFavouredListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_schedule_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mEvent = mValues.get(position);

        holder.mTitleView.setText( holder.mEvent.getTitle() );
        holder.mDeadlineView.setText( holder.mEvent.getDeadLine() );
        holder.mTimeZoneView.setText( holder.mEvent.getTimezone() );
        holder.mDateView.setText( holder.mEvent.getDate() );
        holder.mPlaceView.setText( holder.mEvent.getPlace() );


        userPreviouslyFavouredEvents = ScheduleListFragment.getUserPreviouslyFavouredEvents();

        // TODO: must assign keys instead and use those to compare
        if ( userPreviouslyFavouredEvents != null ) {

            for (String eventId: userPreviouslyFavouredEvents) {

                if ( eventId.equals( holder.mEvent.getId() ) ) {
                    holder.mFavouriteBtnView.setImageResource(R.drawable.ic_fav_filled);
                    holder.mFavouriteBtnView.setColorFilter(Color.rgb(255,223,0));
                    break;
                }

            }

        }


        // TODO: requires php file to test
//        Map<String, Integer> eventsStars = ScheduleListFragment.getEventsStars();
//        if ( eventsStarCounts != null && eventsStarCounts.containsKey( holder.mItem.getId() ) ) {
//            holder.mStarCountView.setText( String.valueOf( mValues.get(position).getStarCount() ) );
//        }


        holder.mFavouriteBtnView.setOnClickListener(v -> {

            userCurrentlyFavouredEvents = ScheduleListFragment.getUserCurrentlyFavouredEvents();

            // if they unfavoured an event that was previously favoured and exists in the DB.
            if ( userPreviouslyFavouredEvents != null && userPreviouslyFavouredEvents.contains( holder.mEvent.getId() ) ) {

                // remove event from the previously favoured list
                ScheduleListFragment.removeEventFromPreviouslyFavouredList( holder.mEvent.getId() );

                // add the event id to the unfavoured list
                onScheduleItemUnFavouredListener.onScheduleItemUnFavoured(holder.mEvent);

                holder.mFavouriteBtnView.setImageResource(R.drawable.ic_fav_stroke);
                holder.mFavouriteBtnView.setColorFilter(Color.rgb(128,128,128));
                Toast.makeText(v.getContext(), "unfavoured event.", Toast.LENGTH_SHORT).show();
            }

            // if they unfavoured an event that they recently favoured but this event does not exist in the DB
            else if ( userCurrentlyFavouredEvents != null && userCurrentlyFavouredEvents.contains( holder.mEvent.getId() ) ) {

                // remove the event id from the currently favoured list
                ScheduleListFragment.removeEventFromFavouredList( holder.mEvent.getId() );

                holder.mFavouriteBtnView.setImageResource(R.drawable.ic_fav_stroke);
                holder.mFavouriteBtnView.setColorFilter(Color.rgb(128,128,128));
                Toast.makeText(v.getContext(), "unfavoured event.", Toast.LENGTH_SHORT).show();
            }

            else {

                onScheduleItemFavouredListener.onScheduleItemFavoured(holder.mEvent);
                holder.mFavouriteBtnView.setImageResource(R.drawable.ic_fav_filled);
                holder.mFavouriteBtnView.setColorFilter(Color.rgb(255,223,0));
                Toast.makeText(v.getContext(), "favoured event.", Toast.LENGTH_SHORT).show();
            }

        });


        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onScheduleListFragmentInteraction(holder.mEvent);
            }
        });


    }


    @Override
    public int getItemCount() { return mValues.size(); }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mTitleView;
        public final TextView mDeadlineView;
        public final TextView mTimeZoneView;
        public final TextView mDateView;
        public final TextView mPlaceView;
        public final TextView mStarCountView;
        public final ImageButton mFavouriteBtnView;
        public Schedule mEvent;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.titleTV);
            mDeadlineView = view.findViewById(R.id.deadlineTV);
            mTimeZoneView = view.findViewById(R.id.timezoneTV);
            mDateView = view.findViewById(R.id.dateTV);
            mPlaceView = view.findViewById(R.id.placeTV);
            mStarCountView = view.findViewById(R.id.starCountTV);
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
