package com.example.android.uamp.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.android.uamp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CalendarListAdapter extends ArrayAdapter<String> {
    private final Context context;
    int layoutResourceId;
    List<String> title;
    List<String> location;
    List<String> date;
    List<String> picture;


    public CalendarListAdapter(Context context,int layoutResourceId, List<String> titles, List<String> locations, List<String> dates, List<String> pictures) {
        super(context, -1, titles);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.title = titles;
        this.location = locations;
        this.date = dates;
        this.picture = pictures;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        //rowView.setBackgroundColor(Color.GREEN);
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, parent, false);
        }


        TextView titleView = (TextView) rowView.findViewById(R.id.calendar_name);
        TextView authorView = (TextView) rowView.findViewById(R.id.calendar_location);
        TextView formatView = (TextView) rowView.findViewById(R.id.calendar_date);
        ImageView pictureView = (ImageView) rowView.findViewById(R.id.calImage);

        titleView.setText(title.get(position));
        authorView.setText(location.get(position));
        formatView.setText(date.get(position));

        Picasso.with(this.context)
                .load(picture.get(position))
                .error(R.drawable.ic_menu_my_calendar)
                .resize(170, 170)                        // optional
                .into(pictureView);


        //Linkify.addLinks(phoneView, Linkify.PHONE_NUMBERS);

        return rowView;
    }
}
