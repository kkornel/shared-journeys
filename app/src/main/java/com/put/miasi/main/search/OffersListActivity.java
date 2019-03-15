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
import com.put.miasi.utils.CircleTransform;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class OffersListActivity extends AppCompatActivity {

    private ArrayList<Offer> data = new ArrayList<Offer>();
    final List<RideOffer> rideOffers = new ArrayList<>();
    final List<User> users = new ArrayList<>();
    private String TAG = "OffersListActivity";
    private ListView listView;

    private String startCity;
    private String destinationCity;
    private int mHour;
    private int mMin;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_list);
        getFromIntent();
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(null);
        firebaseInit();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Available rides list");


    }
    private void getFromIntent()
    {
        Intent intent = getIntent();
        startCity = intent.getStringExtra("startCity");
        destinationCity = intent.getStringExtra("destinationCity");
        mHour = intent.getIntExtra("hour",0);
        mMin = intent.getIntExtra("min",0);
        startCity = intent.getStringExtra("startCity");
        date = intent.getStringExtra("date");
    }
    public void generateListView()
    {
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(null);
        lv.setAdapter(new MyListAdapter(this, R.layout.list_item_offer, data));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(OffersListActivity.this, "List items was clicked "+ position, Toast.LENGTH_SHORT).show();
                Intent rideDetailsIntent = new Intent(OffersListActivity.this, RideDetailsActivity.class);
                rideDetailsIntent.putExtra("Uid", data.get(position).uid);
                startActivity(rideDetailsIntent);
            }
        });
    }

    private void generateListContent() {
        data.clear();
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
            offer.uid = x.getKey();
            offer.from = "From: " + GeoUtils.getCityFromLatLng(this, x.getStartPoint().toLatLng());
            offer.to = "To: " + GeoUtils.getCityFromLatLng(this, x.getDestinationPoint().toLatLng());
            offer.price = String.valueOf(x.getPrice()) + " zł";
            offer.hour_begin_ms = x.getDate();

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
            offer.distance = "5km" + " from you";
            offer.seats = "Available seats: " + x.getSeats();
            data.add(offer);
        }
        sortListByStartHour();
    }
    private void sortListByStartHour()
    {

        for (int i = 0; i < data.size() - 1; i++)
        {
            for (int j = 0; j < data.size() - i - 1; j++)
            {
                if (data.get(j).hour_begin_ms > data.get(j+1).hour_begin_ms)
                {
                    Collections.swap(data,j, j+1);
                }
            }
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
                viewHolder.seats = (TextView) convertView.findViewById(R.id.list_item_available_spaces);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.list_item_distance);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();

            Picasso.get().load(getItem(position).avatar).transform(new CircleTransform()).into(mainViewholder.avatar);
            mainViewholder.nick.setText(getItem(position).nick);
            mainViewholder.from.setText(getItem(position).from);
            mainViewholder.to.setText(getItem(position).to);
            mainViewholder.price.setText(getItem(position).price);
            mainViewholder.hour_begin.setText(getItem(position).hour_begin);
            mainViewholder.hour_end.setText(getItem(position).hour_end);
            mainViewholder.seats.setText(getItem(position).seats);
            mainViewholder.distance.setText(getItem(position).distance);

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
        TextView seats;
        TextView distance;
    }
    public class Offer
    {
        String uid;
        String avatar;
        String nick;
        String from;
        String to;
        String price;
        long hour_begin_ms;
        String hour_begin;
        String hour_end;
        String seats;
        String distance;
    }


    private void searchForFittingOffers(RideOffer rideOffer)
    {
        Calendar currentCalendar = Calendar.getInstance();
        long currentTime = currentCalendar.getTimeInMillis();
        long rideOfferTime = rideOffer.getDate();
        int rideOfferSeats = rideOffer.getSeats();
        String rideOfferDate = DateUtils.getDate(rideOffer.getDate(), DateUtils.STANDARD_DATE_FORMAT);
        String rideOfferStartPoint = GeoUtils.getCityFromLatLng(OffersListActivity.this, rideOffer.getStartPoint().toLatLng());
        String rideOfferDestinationPoint = GeoUtils.getCityFromLatLng(OffersListActivity.this, rideOffer.getDestinationPoint().toLatLng());

        Calendar cal = DateUtils.getCalendarFromMilliSecs(rideOffer.getDate());
        String startHour = DateUtils.getHourFromCalendar(cal);
        String startMin = DateUtils.getMinFromCalendar(cal);

        int startHourInMinutes = Integer.valueOf(startHour) * 60 + Integer.valueOf(startMin);
        int timePickedInMinutes = mHour *60 + mMin;

        if (rideOfferDate.equals(date) && rideOfferStartPoint.equals(startCity) && rideOfferDestinationPoint.equals(destinationCity)
                && rideOfferTime > currentTime && rideOfferSeats > 0  && startHourInMinutes > timePickedInMinutes)
        {
            rideOffers.add(rideOffer);
        }
    }

    /////////////////////////// FIREBASE //////////////////////////////////
    private void firebaseInit() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference offeredRidesRef = database.child(Database.RIDES);
        rideOffers.clear();
        ValueEventListener ridesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    RideOffer rideOffer = ds.getValue(RideOffer.class);
                    rideOffer.setKey(ds.getKey());
                    searchForFittingOffers(rideOffer);
                }
                if (rideOffers.size() == 0)
                {
                    Toast.makeText(OffersListActivity.this, "No ride offers available!", Toast.LENGTH_LONG).show();
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
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    user.setUid(ds.getKey());
                    users.add(user);
                }
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
