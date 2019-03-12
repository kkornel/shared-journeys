package com.put.miasi.main.notifications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.put.miasi.R;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.NotificationListItemClickListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private static final String TAG = "NotificationAdapter";

    private Context mContext;

    private final NotificationListItemClickListener mOnClickListener;

    private List<Notification> mNotifications;

    public NotificationAdapter(Context context, NotificationListItemClickListener onClickListener, List<Notification> notifications) {
        mContext = context;
        mOnClickListener = onClickListener;
        mNotifications = notifications;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.list_item_notification;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        NotificationViewHolder viewHolder = new NotificationViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder viewHolder, int position) {
        if ((mNotifications == null) || (mNotifications.size() == 0)) {
            viewHolder.mDateTextView.setText(mContext.getString(R.string.no_data));
        } else {
            Notification notification = mNotifications.get(position);
        }
    }

    @Override
    public int getItemCount() {
        return ((mNotifications != null) && (mNotifications.size() != 0) ? mNotifications.size() : 0);
    }

    public void loadNewData(List<Notification> newNotifications) {
        mNotifications = newNotifications;
        notifyDataSetChanged();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView mCardView;
        private TextView mDateTextView;
        private TextView mStartCityTextView;
        private TextView mDestinationCityTextView;
        private TextView mStartTimeTextView;
        private TextView mArrivalTextView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            this.mCardView = itemView.findViewById(R.id.myCircle);
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
            Notification notification = mNotifications.get(clickedPosition);
            mOnClickListener.onListItemClick(notification);
        }
    }
}