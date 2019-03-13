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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SearchActivity extends AppCompatActivity
{
    private ArrayList<Offer> data = new ArrayList<Offer>();
    final List<RideOffer> rideOffers = new ArrayList<>();
    final List<User> users = new ArrayList<>();
    private String TAG = "SearchActivity";
    private String date;
    private Button btn_search;
    private ListView listView;
    private String startCity;
    private String destinationCity;
    boolean startTextFilled = false;
    boolean destinationTextFilled = false;

    private static String SEARCH_COUNTRY = "PL";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.listview);

        Intent intent = getIntent();
        long dateInMs = intent.getLongExtra("date",0);
        date = DateUtils.getDate(dateInMs, DateUtils.STANDARD_DATE_FORMAT);
        initializeSearchButton();
        f();
    }

    public void initializeSearchButton()
    {
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setVisibility(View.INVISIBLE);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setAdapter(null);
                firebaseInit();


            }
        });
    }

    public void generateListView()
    {
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(null);
        lv.setAdapter(new MyListAdapter(this, R.layout.list_item_offer, data));
        setListViewHeightBasedOnChildren(lv);
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
        String hour_begin;
        String hour_end;
        String seats;
        String distance;
    }

    private void f() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_destination));

        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        // Create a new Places client instance.
        final PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setCountry(SEARCH_COUNTRY);
        autocompleteFragment.setHint("e.g. Poznań");
        autocompleteFragment.getView().setBackgroundColor(getResources().getColor(R.color.colorSearchBackground));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place)
            {
                startTextFilled = true;
                startCity = place.getName();
                if (destinationTextFilled && startTextFilled)
                {
                    btn_search.setVisibility(View.VISIBLE);
                }
                // Toast.makeText(SearchActivity.this, "List items was clicked "+ place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG,"An error occurred: " + status);
            }
        });

        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);

        // Specify the types of place data to return.
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment2.setCountry(SEARCH_COUNTRY);
        autocompleteFragment2.setHint("e.g. Bydgoszcz");
        autocompleteFragment2.getView().setBackgroundColor(getResources().getColor(R.color.colorSearchBackground));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destinationTextFilled = true;
                destinationCity = place.getName();
                if (destinationTextFilled  && startTextFilled)
                {
                    btn_search.setVisibility(View.VISIBLE);
                }
                // Toast.makeText(SearchActivity.this, "List items was clicked "+ place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        });


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
                    String rideOfferDate = DateUtils.getDate(rideOffer.getDate(), DateUtils.STANDARD_DATE_FORMAT);
                    String rideOfferStartPoint = GeoUtils.getCityFromLatLng(SearchActivity.this, rideOffer.getStartPoint().toLatLng());
                    String rideOfferDestinationPoint = GeoUtils.getCityFromLatLng(SearchActivity.this, rideOffer.getDestinationPoint().toLatLng());
                    //if (rideOfferDate.equals(date) && rideOfferStartPoint.equals(startCity) && rideOfferDestinationPoint.equals(destinationCity))
                    //{
                        rideOffers.add(rideOffer);
                    //}
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

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
