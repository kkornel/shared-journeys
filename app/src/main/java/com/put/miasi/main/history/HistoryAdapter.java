package com.put.miasi.main.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.put.miasi.R;
import com.put.miasi.main.offer.OfferSummaryActivity;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.RideOffer;

import java.util.Calendar;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private static final String TAG = "HistoryAdapter";

    private Context mContext;

    private final ListItemClickListener mOnClickListener;

    private List<RideOffer> mRides;

    public HistoryAdapter(Context context, ListItemClickListener onClickListener, List<RideOffer> rides) {
        mContext = context;
        mOnClickListener = onClickListener;
        mRides = rides;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.history_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        HistoryViewHolder workoutViewHolder = new HistoryViewHolder(view);

        return workoutViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder viewHolder, int position) {
        if ((mRides == null) || (mRides.size() == 0)) {
            viewHolder.mDateTextView.setText("halo?");
        } else {
            RideOffer ride = mRides.get(position);

            Calendar cal = DateUtils.getCalendarFromMilliSecs(ride.getDate());
            viewHolder.mDateTextView.setText(DateUtils.getDayFromCalendar(cal) + "/" + DateUtils.getMonthFromCalendar(cal));

            LatLng startLatLng = ride.startPoint.toLatLng();
            LatLng destLatLng = ride.destinationPoint.toLatLng();
            String startCity = GeoUtils.getCityFromLatLng(mContext, startLatLng);
            String destCity = GeoUtils.getCityFromLatLng(mContext, destLatLng);
            viewHolder.mStartCityTextView.setText(startCity);
            viewHolder.mDestinationCityTextView.setText(destCity);

            String startHour = DateUtils.getHourFromCalendar(cal);
            String startMin = DateUtils.getMinFromCalendar(cal);
            viewHolder.mStartTimeTextView.setText(startHour + ":" + startMin);

            int durationHours = DateUtils.getDurationHoursFromLongSeconds(ride.getDuration());
            int durationMins = DateUtils.getDurationMinsFromLongSeconds(ride.getDuration());
            cal.add(Calendar.HOUR_OF_DAY, durationHours);
            cal.add(Calendar.MINUTE, durationMins);

            String arrivalHour = DateUtils.getHourFromCalendar(cal);
            String arrivalMin = DateUtils.getMinFromCalendar(cal);
            viewHolder.mArrivalTextView.setText(arrivalHour + ":" + arrivalMin);
        }
    }

    @Override
    public int getItemCount() {
        return ((mRides != null) && (mRides.size() != 0) ? mRides.size() : 0);
    }

    void loadNewData(List<RideOffer> newRides) {
        mRides = newRides;
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDateTextView;
        private TextView mStartCityTextView;
        private TextView mDestinationCityTextView;
        private TextView mStartTimeTextView;
        private TextView mArrivalTextView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            this.mDateTextView = itemView.findViewById(R.id.dateTextView);
            this.mStartCityTextView = itemView.findViewById(R.id.startCityTextView);
            this.mDestinationCityTextView = itemView.findViewById(R.id.destinationCityTextView);
            this.mStartTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            this.mArrivalTextView = itemView.findViewById(R.id.arrivalTimeTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
