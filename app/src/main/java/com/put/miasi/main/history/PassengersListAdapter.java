package com.put.miasi.main.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.put.miasi.R;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.Passenger;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PassengersListAdapter extends RecyclerView.Adapter<PassengersListAdapter.PassengersListViewHolder> {
    private static final String TAG = "PassengersListAdapter";

    private Context mContext;
    private final ListItemClickListener mOnClickListener;
    private List<Passenger> mPassengersList;

    public PassengersListAdapter(ListItemClickListener onClickListener, List<Passenger> passengers, Context context) {
        mOnClickListener = onClickListener;
        mPassengersList = passengers;
        mContext = context;
    }

    @Override
    public PassengersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.list_item_passengers;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PassengersListViewHolder viewHolder = new PassengersListViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PassengersListViewHolder viewHolder, int position) {
        if ((mPassengersList == null) || (mPassengersList.size() == 0)) {
            viewHolder.mNameTextView.setText("ERROR");
        } else {
            Passenger passenger = mPassengersList.get(position);
            User user = passenger.getUser();

            Picasso.get()
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .error(R.drawable.ic_error_red_24dp)
                    .into(viewHolder.mAvatarImageView);

            viewHolder.mNameTextView.setText(user.getFirstName() + " " + user.getSurname());
            viewHolder.mSeatsTextView.setText(passenger.getNumOfSeatsReserved() + "");
            viewHolder.mAvgPassRateTextView.setText(user.getPassengerRaingAvg() + "");
            viewHolder.mNumPassRateTextView.setText(user.getNumberOfPassengerRatings() + "");

            viewHolder.mRefuseRideButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Refuse", Toast.LENGTH_SHORT).show();
                }
            });
            viewHolder.mCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Call", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ((mPassengersList != null) && (mPassengersList.size() != 0) ? mPassengersList.size() : 0);
    }

    void loadNewData(List<Passenger> passengers) {
        mPassengersList = passengers;
        notifyDataSetChanged();
    }

    class PassengersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mAvatarImageView;
        private TextView mNameTextView;
        private TextView mSeatsTextView;
        private TextView mAvgPassRateTextView;
        private TextView mNumPassRateTextView;
        private Button mRefuseRideButton;
        private Button mCallButton;

        public PassengersListViewHolder(View itemView) {
            super(itemView);
            mAvatarImageView = itemView.findViewById(R.id.avatarImageView);
            mNameTextView = itemView.findViewById(R.id.nameTextView);
            mSeatsTextView = itemView.findViewById(R.id.seatsTextView);
            mAvgPassRateTextView = itemView.findViewById(R.id.avgPassRateTextView);
            mNumPassRateTextView = itemView.findViewById(R.id.numPassRateTextView);
            mRefuseRideButton = itemView.findViewById(R.id.refuseRideButton);
            mCallButton = itemView.findViewById(R.id.callButton);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}