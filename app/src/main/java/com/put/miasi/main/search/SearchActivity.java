package com.put.miasi.main.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.put.miasi.MainActivityOldBasic;
import com.put.miasi.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
{
    private ArrayList<Offers> data = new ArrayList<Offers>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ListView lv = (ListView) findViewById(R.id.listview);
        generateListContent();
        lv.setAdapter(new MyListAdapter(this, R.layout.list_item, data));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchActivity.this, "List items was clicked "+ position, Toast.LENGTH_SHORT).show();
                Intent rideDetailsIntent = new Intent(SearchActivity.this, RideDetailsActivity.class);
                startActivity(rideDetailsIntent);
            }
        });
    }



    private void generateListContent() {
        for(int i = 0; i < 55; i++) {
            Offers offer = new Offers();
            offer.nick = "tata";
            offer.from = "lol";
            offer.to = "1";
            offer.price = "2";
            offer.hour_begin = "3";
            offer.hour_end = "4";
            data.add(offer);
        }
    }



    private class MyListAdapter extends ArrayAdapter<Offers>
    {
        private int layout;
        private MyListAdapter(Context context, int resource, List<Offers> objects)
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
    public class Offers
    {
        String nick;
        String from;
        String to;
        String price;
        String hour_begin;
        String hour_end;
    }

    /////////////////////////// FIREBASE //////////////////////////////////
}
