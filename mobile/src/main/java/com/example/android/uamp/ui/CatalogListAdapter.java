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

class CatalogListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private int layoutResourceId;
    private List<String> title;
    private List<String> author;
    private List<String> format;
    private List<String> picture;


    public CatalogListAdapter(Context context, List<String> titles, List<String> authors, List<String> formats, List<String> pictures) {
        super(context, -1, titles);
        this.layoutResourceId = R.layout.cataloglist_item;
        this.context = context;
        this.title = titles;
        this.author = authors;
        this.format = formats;
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


        TextView titleView = (TextView) rowView.findViewById(R.id.catalog_name);
        TextView authorView = (TextView) rowView.findViewById(R.id.catalog_author);
        TextView formatView = (TextView) rowView.findViewById(R.id.catalog_format);
        ImageView pictureView = (ImageView) rowView.findViewById(R.id.catImage);

        titleView.setText(title.get(position));
        authorView.setText(author.get(position));
        formatView.setText(format.get(position));

        Picasso.with(this.context)
                .load(picture.get(position))
                .error(R.drawable.ic_by_genre)
                .resize(170, 170)                        // optional
                .into(pictureView);


        //Linkify.addLinks(phoneView, Linkify.PHONE_NUMBERS);

        return rowView;
    }
}
