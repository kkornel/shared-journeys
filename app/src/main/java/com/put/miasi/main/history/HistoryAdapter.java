package com.put.miasi.main.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.put.miasi.R;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.RideOffer;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
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
    public void onBindViewHolder(@NonNull HistoryViewHolder workoutViewHolder, int position) {
        if ((mRides == null) || (mRides.size() == 0)) {
            // workoutViewHolder.mDateTextView.setText("");
        } else {

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
        private ImageView mWorkoutImageView;
        private TextView mDateTextView;
        private TextView mDistanceTextView;
        private TextView mDurationTextView;

        private ImageView mDistanceImageView;
        private ImageView mDurationImageView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            // this.mWorkoutImageView = itemView.findViewById(R.id.activityImageView);
            this.mDateTextView = itemView.findViewById(R.id.dateTextView);
            this.mDistanceTextView = itemView.findViewById(R.id.timeTextView);
            this.mDurationTextView = itemView.findViewById(R.id.durationTextView);
            // this.mDistanceImageView = itemView.findViewById(R.id.distanceImageView);
            // this.mDurationImageView = itemView.findViewById(R.id.durationImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
