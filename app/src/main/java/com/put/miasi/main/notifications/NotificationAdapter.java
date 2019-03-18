package com.put.miasi.main.notifications;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.NotificationListItemClickListener;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private static final String TAG = "NotificationAdapter";

    private Context mContext;

    private final NotificationListItemClickListener mOnClickListener;

    // private HashMap<String, Boolean> mNotificationsFromProfile;
    private List<Notification> mNotifications;
    private HashMap<String, User> mUsers;
    private HashMap<String,RideOffer> mRides;

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
            User sender = mUsers.get(notification.getSenderUid());
            RideOffer ride = mRides.get(notification.getRideUid());

            String destinationFromCanceled = notification.getDestinationFromCanceled();

            Picasso.get()
                    .load(sender.getAvatarUrl())
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .error(R.drawable.ic_error_red_24dp)
                    .into(viewHolder.mImageView);

            String title = "";
            Notification.NotificationType notificationType = notification.getNotificationType();

            switch (notificationType) {
                case NEW_PASSENGER:
                    title = "New passenger for your ride to " + GeoUtils.getCityFromLatLng(mContext, ride.destinationPoint.toLatLng());
                    break;
                case RIDE_CANCELED:
                    title = sender.getFullname() + " canceled ride to " + destinationFromCanceled;
                    break;
                case RIDE_DECLINED:
                    title = sender.getFullname() + " declined your ride to " + destinationFromCanceled;
                    break;
                case PASSENGER_RESIGNED:
                    title = sender.getFullname() + " resigned from your ride to " + GeoUtils.getCityFromLatLng(mContext, ride.destinationPoint.toLatLng());
                    break;
                case RATED_AS_DRIVER:
                    title = "New rate as driver: " + notification.getRate();
                    break;
                case RATED_AS_PASSENGER:
                    title = "New rate as passenger: " + notification.getRate();
                    break;
            }
            viewHolder.mTitleNotification.setText(title);

            viewHolder.mDateTextView.setText(DateUtils.getDate(notification.getTimeStamp(), DateUtils.STANDARD_DATE_FORMAT));

            viewHolder.mNewNotificationTextView.setVisibility(View.VISIBLE);

            // boolean wasRead = mNotificationsFromProfile.get(notification.getNotificationUid());
            // if (!wasRead) {
            //     viewHolder.mNewNotificationTextView.setVisibility(View.VISIBLE);
            // } else {
            //     viewHolder.mNewNotificationTextView.setVisibility(View.INVISIBLE);
            // }
        }
    }

    @Override
    public int getItemCount() {
        return ((mNotifications != null) && (mNotifications.size() != 0) ? mNotifications.size() : 0);
    }

    public void loadNewData(List<Notification> newNotifications, HashMap<String, User> user, HashMap<String, RideOffer> rides) {
        mNotifications = newNotifications;
        mUsers = user;
        mRides = rides;
        notifyDataSetChanged();
    }

    // public void loadNewData(HashMap<String, Boolean> newNotificationsFromProfile, List<Notification> newNotifications, HashMap<String, User> user, HashMap<String, RideOffer> rides) {
    //     mNotificationsFromProfile = newNotificationsFromProfile;
    //     mNotifications = newNotifications;
    //     mUsers = user;
    //     mRides = rides;
    //     notifyDataSetChanged();
    // }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private TextView mTitleNotification;
        private TextView mDateTextView;
        private TextView mNewNotificationTextView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            this.mImageView = itemView.findViewById(R.id.imageView);
            this.mTitleNotification = itemView.findViewById(R.id.titleNotificationTextView);
            this.mDateTextView = itemView.findViewById(R.id.dateTextView);
            this.mNewNotificationTextView = itemView.findViewById(R.id.newNotificationTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Notification notification = mNotifications.get(clickedPosition);
            mOnClickListener.onListItemClick(notification, mTitleNotification.getText().toString());
        }
    }
}