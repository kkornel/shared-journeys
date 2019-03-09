package com.put.miasi.main.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchActivity extends AppCompatActivity
{
    private ArrayList<Offer> data = new ArrayList<Offer>();
    final List<RideOffer> rideOffers = new ArrayList<>();
    final List<User> users = new ArrayList<>();
    private String TAG = "SearchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tests();
    }
    public void generateListView()
    {
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(new MyListAdapter(this, R.layout.list_item, data));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchActivity.this, "List items was clicked "+ position, Toast.LENGTH_SHORT).show();
                Intent rideDetailsIntent = new Intent(SearchActivity.this, RideDetailsActivity.class);
                rideDetailsIntent.putExtra("Uid", data.get(position).uid);
                startActivity(rideDetailsIntent);
            }
        });
    }


    private void generateListContent() {
        for (RideOffer x : rideOffers)
        {
            Offer offer = new Offer();
            for (User y : users)
            {
                if (x.getDriverUid().equals(y.getUid()))
                {
                    offer.nick = y.getFirstName() + " " + y.getSurname();
                    offer.avatar = y.getAvatarUrl();
                }
            }
            offer.uid = x.getDriverUid();
            offer.from = "From: " + GeoUtils.getCityFromLatLng(this, x.getStartPoint().toLatLng());
            offer.to = "To: " + GeoUtils.getCityFromLatLng(this, x.getDestinationPoint().toLatLng());
            offer.price = String.valueOf(x.getPrice()) + " zł";

            Calendar cal = DateUtils.getCalendarFromMilliSecs(x.getDate());
            String startHour = DateUtils.getHourFromCalendar(cal);
            String startMin = DateUtils.getMinFromCalendar(cal);
            offer.hour_begin = startHour + ":" + startMin;

            int durationHours = DateUtils.getDurationHoursFromLongSeconds(x.getDuration());
            int durationMins = DateUtils.getDurationMinsFromLongSeconds(x.getDuration());
            cal.add(Calendar.HOUR_OF_DAY, durationHours);
            cal.add(Calendar.MINUTE, durationMins);
            String arrivalHour = DateUtils.getHourFromCalendar(cal);
            String arrivalMin = DateUtils.getMinFromCalendar(cal);
            offer.hour_end =arrivalHour + ":" + arrivalMin;

            data.add(offer);
        }
    }



    private class MyListAdapter extends ArrayAdapter<Offer>
    {
        private int layout;
        private MyListAdapter(Context context, int resource, List<Offer> objects)
        {
            super(context,resource,objects);
            layout = resource;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder mainViewholder = null;
            if (convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout,parent,false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.avatar = (ImageView) convertView.findViewById(R.id.list_item_avatar);
                viewHolder.nick = (TextView) convertView.findViewById(R.id.list_item_nick);
                viewHolder.from = (TextView) convertView.findViewById(R.id.list_item_from);
                viewHolder.to = (TextView) convertView.findViewById(R.id.list_item_to);
                viewHolder.price = (TextView) convertView.findViewById(R.id.list_item_price);
                viewHolder.hour_begin = (TextView) convertView.findViewById(R.id.list_item_hour_begin);
                viewHolder.hour_end = (TextView) convertView.findViewById(R.id.list_item_hour_end);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            Picasso.get().load(getItem(position).avatar).into(mainViewholder.avatar);
            mainViewholder.nick.setText(getItem(position).nick);
            mainViewholder.from.setText(getItem(position).from);
            mainViewholder.to.setText(getItem(position).to);
            mainViewholder.price.setText(getItem(position).price);
            mainViewholder.hour_begin.setText(getItem(position).hour_begin);
            mainViewholder.hour_end.setText(getItem(position).hour_end);


            return convertView;
        }
    }
    public class ViewHolder
    {
        ImageView avatar;
        TextView nick;
        TextView from;
        TextView to;
        TextView price;
        TextView hour_begin;
        TextView hour_end;
    }
    public class Offer
    {
        String uid;
        String avatar;
        String nick;
        String from;
        String to;
        String price;
        String hour_begin;
        String hour_end;
    }

    /////////////////////////// FIREBASE //////////////////////////////////
    private void tests() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();


        final DatabaseReference offeredRidesRef = database.child(Database.RIDES);

        ValueEventListener ridesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferLog.d(dataSnapshot.toString());
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    RideOffer rideOffer = ds.getValue(RideOffer.class);
                    rideOffer.setKey(ds.getKey());
                    OfferLog.d(rideOffer.toString());
                    rideOffers.add(rideOffer);
                    OfferLog.d(String.valueOf(rideOffers.size()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        offeredRidesRef.addListenerForSingleValueEvent(ridesListener);


        final DatabaseReference usersRef = database.child(Database.USERS);

        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferLog.d(dataSnapshot.toString());
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    user.setUid(ds.getKey());
                    OfferLog.d(user.toString());
                    users.add(user);
                    OfferLog.d(String.valueOf(users.size()));
                }
                Log.i(TAG,"users: "+ users.size());
                Log.i(TAG, "rides: " + rideOffers.size());
                generateListContent();
                generateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        usersRef.addListenerForSingleValueEvent(usersListener);


    }
}
